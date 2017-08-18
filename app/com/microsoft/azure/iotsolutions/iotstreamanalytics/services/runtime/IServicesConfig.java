// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iotstreamanalytics.services.runtime;

import com.google.inject.ImplementedBy;

@ImplementedBy(ServicesConfig.class)
public interface IServicesConfig {

    String getMonitoringRulesUrl();

    StorageConfig getAlarmsStorageConfig();

    StorageConfig getMessagesStorageConfig();
}
