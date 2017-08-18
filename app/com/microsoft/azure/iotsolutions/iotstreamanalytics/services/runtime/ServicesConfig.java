// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iotstreamanalytics.services.runtime;

public class ServicesConfig implements IServicesConfig {

    private final String monitoringRulesUrl;
    private final StorageConfig messagesConfig;
    private final StorageConfig alarmsConfig;

    public ServicesConfig(
        final String monitoringRulesUrl,
        StorageConfig messagesConfig,
        StorageConfig alarmsConfig) {
        this.monitoringRulesUrl = monitoringRulesUrl;
        this.messagesConfig = messagesConfig;
        this.alarmsConfig = alarmsConfig;
    }

    public String getMonitoringRulesUrl() {
        return this.monitoringRulesUrl;
    }

    public StorageConfig getMessagesStorageConfig() {
        return this.messagesConfig;
    }

    public StorageConfig getAlarmsStorageConfig() {
        return this.alarmsConfig;
    }
}
