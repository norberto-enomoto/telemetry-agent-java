// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iotstreamanalytics.streamingAgent;

import akka.Done;
import akka.NotUsed;
import akka.stream.javadsl.*;
import com.microsoft.azure.iot.iothubreact.MessageFromDevice;
import com.microsoft.azure.iot.iothubreact.SourceOptions;
import com.microsoft.azure.iot.iothubreact.javadsl.IoTHub;
import com.microsoft.azure.iotsolutions.iotstreamanalytics.streamingAgent.exceptions.ConfigurationException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.time.Instant;
import java.util.concurrent.CompletionStage;

import static java.lang.System.out;

public class Main extends AkkaApp {

    // In case the position is missing in the storage, start streaming data
    // from no more than 10 days in the past
    static final int StartFrom = 10 * 86400;

    public static void main(String[] args) throws ConfigurationException {

        checkConfiguration();

        IoTHub hub = new IoTHub();
        SourceOptions options = new SourceOptions()
            .fromCheckpoint(Instant.now().minusSeconds(StartFrom));

        // TODO: change CP type
        String cpType="outofband";

        if (cpType == "outofband") {
            options.checkpointOnPull();
            Source<MessageFromDevice, NotUsed> messages = hub.source(options);
            ProcessAndCheckpointOutOfBand(messages);
        } else {
            Sink<MessageFromDevice, CompletionStage<Done>> sink = hub.checkpointSink();
            Source<MessageFromDevice, NotUsed> messages = hub.source(options);
            ProcessAndCheckpointAfterProcessing(messages, sink);
        }
    }

    private static void ProcessAndCheckpointOutOfBand(
        Source<MessageFromDevice, NotUsed> messages) {
        messages
            .to(consoleSink())
            .run(streamMaterializer);
    }

    // TODO: fix, the stream is not working
    private static void ProcessAndCheckpointAfterProcessing(
        Source<MessageFromDevice, NotUsed> messages,
        Sink<MessageFromDevice, CompletionStage<Done>> sink) {
        messages
            .via(consoleFlow())
            .to(sink)
            .run(streamMaterializer);
    }

    private static Flow<MessageFromDevice, MessageFromDevice, NotUsed> consoleFlow() {
        return Flow.of(MessageFromDevice.class).map(m -> process(m));
    }

    private static Sink<MessageFromDevice, CompletionStage<Done>> consoleSink() {
        return Sink.foreach(m -> process(m));
    }

    // Temporary code, print events to console
    private static MessageFromDevice process(MessageFromDevice m) {
        out.println("Device: " + m.deviceId() + ": Schema: " + m.messageSchema() + ": Content: " + m.contentAsString());
        return m;
    }

    private static void checkConfiguration() throws ConfigurationException {
        Config conf = ConfigFactory.load();

        String hubName = conf.getString("iothub-react.connection.hubName");
        String hubEndpoint = conf.getString("iothub-react.connection.hubEndpoint");
        String hubPartitions = conf.getString("iothub-react.connection.hubPartitions");
        String cpBackendType = conf.getString("iothub-react.checkpointing.storage.backendType");
        String cpNamespace = conf.getString("iothub-react.checkpointing.storage.namespace");
        String cpFrequency = conf.getString("iothub-react.checkpointing.frequency");

        if (hubName.isEmpty()) {
            throw new ConfigurationException("Azure IoT Hub name not found in the configuration.");
        }

        if (hubEndpoint.isEmpty()) {
            throw new ConfigurationException("Azure IoT Hub endpoint not found in the configuration.");
        }

        if (hubPartitions.isEmpty()) {
            throw new ConfigurationException("Azure IoT Hub partition number not found in the configuration.");
        }

        if (cpNamespace.isEmpty()) {
            throw new ConfigurationException("Checkpointing namespace not found in the configuration.");
        }

        if (cpBackendType.toLowerCase() == "cosmosdbsql") {
            String cs = conf.getString("iothub-react.checkpointing.storage.cosmosdbsql.connString");
            if (cs.isEmpty()) {
                throw new ConfigurationException("CosmosDb connection string not found in the configuration.");
            }
        }

        out.println("IoT Hub name: " + hubName);
        out.println("IoT Hub endpoint: " + hubEndpoint);
        out.println("IoT Hub partitions: " + hubPartitions);
        out.println("Checkpointing storage: " + cpBackendType);
        out.println("Checkpointing namespace: " + cpNamespace);
        out.println("Checkpointing min frequency: " + cpFrequency);
    }
}
