// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iotstreamanalytics.streamingAgent.streaming;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.Materializer;
import akka.stream.javadsl.*;
import com.google.inject.Inject;
import com.microsoft.azure.iot.iothubreact.MessageFromDevice;
import com.microsoft.azure.iot.iothubreact.SourceOptions;
import com.microsoft.azure.iot.iothubreact.javadsl.IoTHub;
import com.microsoft.azure.iotsolutions.iotstreamanalytics.services.IMessages;
import org.joda.time.DateTime;
import play.Logger;
import scala.concurrent.duration.FiniteDuration;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class Stream implements IStream {

    private static final Logger.ALogger log = Logger.of(Stream.class);

    /**
     * Streaming options: use the checkpoint if available,
     * otherwise start streaming from 24 hours in the past
     */
    private final int StartFrom = 24;

    /**
     * Akka streams materializer
     */
    private final Materializer streamMaterializer;

    /**
     * Akka actor system used here for scheduling
     */
    private final ActorSystem system;

    /**
     * The logic used to process each message
     */
    private IMessages messagesProcessor;

    /**
     * Azure IoT Hub (reactive) instance
     */
    private final IoTHub hub;

    /**
     * Akka stream source
     */
    private Source<MessageFromDevice, NotUsed> source;

    public long throughputPreviousTime = 0;
    public long throughputPreviousTotal = 0;
    public long throughputTotal = 0;

    @Inject
    public Stream(
        ActorSystem system,
        Materializer streamMaterializer,
        IMessages messagesProcessor) {
        this.system = system;
        this.streamMaterializer = streamMaterializer;
        this.messagesProcessor = messagesProcessor;
        this.hub = new IoTHub();
    }

    @Override
    public void Run() {
        log.info("Starting stream");
        this.source = this.hub.source(this.getStreamOptions());
        this.source
            .via(processingFlow())
            .to(this.hub.checkpointSink())
            .run(this.streamMaterializer);

        if (log.isInfoEnabled()) {
            // Every 30 seconds log the throughput
            this.system.scheduler().schedule(
                new FiniteDuration(1, TimeUnit.SECONDS),
                new FiniteDuration(30, TimeUnit.SECONDS),
                logThroughput(this),
                this.system.dispatcher());
        }
    }

    private SourceOptions getStreamOptions() {
        Instant startTimeIfNoCheckpoint = Instant.now().minus(this.StartFrom, ChronoUnit.HOURS);
        return new SourceOptions().fromCheckpoint(startTimeIfNoCheckpoint);
    }

    private Flow<MessageFromDevice, MessageFromDevice, NotUsed> processingFlow() {
        return Flow.of(MessageFromDevice.class).map(m -> {
            this.messagesProcessor.process(m);
            this.throughputTotal++;
            return m;
        });
    }

    private Runnable logThroughput(Stream self) {
        return () -> {
            long currTime = DateTime.now().getMillis();
            if (self.throughputPreviousTime == 0) {
                self.throughputPreviousTime = currTime;
                self.throughputPreviousTotal = self.throughputTotal;
                return;
            }

            double countDelta = self.throughputTotal - self.throughputPreviousTotal;
            double timeDelta = currTime - self.throughputPreviousTime;
            double throughput = (countDelta / timeDelta) * 1000;

            log.info("Throughput: {} msgs/sec - {} messages in the last {} seconds ",
                (int) throughput, (int) countDelta, (int) (timeDelta / 1000));

            self.throughputPreviousTotal = self.throughputTotal;
            self.throughputPreviousTime = currTime;
        };
    }
}
