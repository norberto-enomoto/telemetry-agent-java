// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iotstreamanalytics.streamingAgent;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;

/**
 * Initialize reactive streaming
 */
public class AkkaApp {
    private static ActorSystem system = ActorSystem.create("streamingAgent");

    protected final static Materializer streamMaterializer = ActorMaterializer.create(system);
}
