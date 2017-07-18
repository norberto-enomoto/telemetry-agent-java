// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iotstreamanalytics.streamingAgent;

import akka.Done;
import akka.NotUsed;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.microsoft.azure.iot.iothubreact.MessageFromDevice;
import com.microsoft.azure.iot.iothubreact.SourceOptions;
import com.microsoft.azure.iot.iothubreact.javadsl.IoTHub;

import java.util.concurrent.CompletionStage;

import static java.lang.System.out;

public class Main extends AkkaApp {

    public static void main(String[] args) {

        SourceOptions options = new SourceOptions().allPartitions().fromStart();
        Source<MessageFromDevice, NotUsed> messages = new IoTHub().source(options);

        // Temporary code, print events to console
        messages
            .to(console())
            .run(streamMaterializer);
    }

    public static Sink<MessageFromDevice, CompletionStage<Done>> console() {
        return Sink.foreach(m ->
        {
            out.println("Device: " + m.deviceId() + ": Schema: " + m.messageSchema() + ": Content: " + m.contentAsString());
        });
    }
}
