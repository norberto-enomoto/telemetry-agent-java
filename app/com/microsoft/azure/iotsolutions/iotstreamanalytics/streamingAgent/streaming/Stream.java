// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iotstreamanalytics.streamingAgent.streaming;

import akka.NotUsed;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Source;
import com.google.inject.Inject;
import com.microsoft.azure.iot.iothubreact.MessageFromDevice;
import com.microsoft.azure.iot.iothubreact.SourceOptions;
import com.microsoft.azure.iot.iothubreact.javadsl.IoTHub;
import com.microsoft.azure.iotsolutions.iotstreamanalytics.services.IMessages;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class Stream implements IStream {
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

    @Inject
    public Stream(
        Materializer streamMaterializer,
        IMessages messagesProcessor) {
        this.streamMaterializer = streamMaterializer;
        this.messagesProcessor = messagesProcessor;
        this.hub = new IoTHub();
    }

    @Override
    public void Run() {
        this.source = this.hub.source(this.getStreamOptions());
        this.source
            .via(processingFlow())
            .to(this.hub.checkpointSink())
            .run(this.streamMaterializer);
    }

    private SourceOptions getStreamOptions() {
        Instant startTimeIfNoCheckpoint = Instant.now().minus(StartFrom, ChronoUnit.HOURS);
        return new SourceOptions().fromCheckpoint(startTimeIfNoCheckpoint);
    }

    private Flow<MessageFromDevice, MessageFromDevice, NotUsed> processingFlow() {
        return Flow.of(MessageFromDevice.class).map(m -> {
            this.messagesProcessor.process(m);
            return m;
        });
    }
}
