// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iotstreamanalytics.streamingAgent;

import akka.actor.ActorSystem;
import akka.japi.function.Function;
import akka.stream.ActorMaterializer;
import akka.stream.ActorMaterializerSettings;
import akka.stream.Materializer;
import akka.stream.Supervision;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.microsoft.azure.iotsolutions.iotstreamanalytics.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.iotstreamanalytics.streamingAgent.runtime.IConfig;
import play.Logger;

public class Module extends AbstractModule {

    private static final Logger.ALogger log = Logger.of(Module.class);

    ActorSystem system = ActorSystem.create("streamingAgent");

    @Override
    public void configure() {
        // Note: this method should be empty
        // Try to use use JIT binding and @ImplementedBy instead
    }

    @Provides
    ActorSystem provideActorSystem() {
        return system;
    }

    @Provides
    Materializer provideMaterializer() {

        final Function<Throwable, Supervision.Directive> decider = e -> {
            log.error(e.getMessage(), e);
            e.printStackTrace();

            return Supervision.stop();
        };

        Materializer streamMaterializer = ActorMaterializer.create(
            ActorMaterializerSettings
                .create(system)
                .withSupervisionStrategy(decider),
            system);

        return streamMaterializer;
    }

    @Provides
    IServicesConfig provideIServicesConfig(IConfig config) {
        log.info("Messages db: {}", config.getServicesConfig().getMessagesStorageConfig().getDocumentDbDatabase());
        log.info("Messages collection: {}", config.getServicesConfig().getMessagesStorageConfig().getDocumentDbCollection());
        return config.getServicesConfig();
    }
}
