// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iotstreamanalytics.services.runtime;

public class ServicesConfig implements IServicesConfig {

    private final StorageConfig messagesConfig;

    private final StorageConfig alarmsConfig;

    public ServicesConfig(
        StorageConfig messagesConfig,
        StorageConfig alarmsConfig) {
        this.messagesConfig = messagesConfig;
        this.alarmsConfig = alarmsConfig;
    }

    public StorageConfig getMessagesStorageConfig() {
        return this.messagesConfig;
    }

    public StorageConfig getAlarmsStorageConfig() {
        return this.alarmsConfig;
    }
}
