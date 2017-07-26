// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iotstreamanalytics.streamingAgent.runtime;

import com.microsoft.azure.iotsolutions.iotstreamanalytics.services.runtime.*;
import com.typesafe.config.ConfigFactory;

public class Config implements IConfig {

    // Namespace applied to all the custom configuration settings
    private final String Namespace = "com.microsoft.azure.iotsolutions.";

    // Settings about this application
    private final String ApplicationKey = Namespace + "iot-stream-analytics.";

    private final String messagesStorageTypeKey = ApplicationKey + "messages.storageType";
    private final String messagesDocDbConnStringKey = ApplicationKey + "messages.documentDb.connString";
    private final String messagesDocDbDatabaseKey = ApplicationKey + "messages.documentDb.database";
    private final String messagesDocDbCollectionKey = ApplicationKey + "messages.documentDb.collection";
    private final String messagesDocDbRUsKey = ApplicationKey + "messages.documentDb.RUs";

    private final String alarmsStorageTypeKey = ApplicationKey + "alarms.storageType";
    private final String alarmsDocDbConnStringKey = ApplicationKey + "alarms.documentDb.connString";
    private final String alarmsDocDbDatabaseKey = ApplicationKey + "alarms.documentDb.database";
    private final String alarmsDocDbCollectionKey = ApplicationKey + "alarms.documentDb.collection";
    private final String alarmsDocDbRUsKey = ApplicationKey + "alarms.documentDb.RUs";

    private IServicesConfig servicesConfig;

    public Config() {
    }

    /**
     * Service layer configuration
     */
    @Override
    public IServicesConfig getServicesConfig() {

        if (this.servicesConfig != null) return this.servicesConfig;

        com.typesafe.config.Config data = ConfigFactory.load();

        StorageConfig messagesConfig = new StorageConfig(
            data.getString(messagesStorageTypeKey).toLowerCase(),
            data.getString(messagesDocDbConnStringKey),
            data.getString(messagesDocDbDatabaseKey),
            data.getString(messagesDocDbCollectionKey),
            data.getInt(messagesDocDbRUsKey));

        StorageConfig alarmsConfig = new StorageConfig(
            data.getString(alarmsStorageTypeKey).toLowerCase(),
            data.getString(alarmsDocDbConnStringKey),
            data.getString(alarmsDocDbDatabaseKey),
            data.getString(alarmsDocDbCollectionKey),
            data.getInt(alarmsDocDbRUsKey));

        this.servicesConfig = new ServicesConfig(messagesConfig, alarmsConfig);

        return this.servicesConfig;
    }
}
