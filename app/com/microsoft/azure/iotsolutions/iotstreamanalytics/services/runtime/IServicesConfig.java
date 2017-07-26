// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iotstreamanalytics.services.runtime;

import com.google.inject.ImplementedBy;

@ImplementedBy(ServicesConfig.class)
public interface IServicesConfig {

    StorageConfig getAlarmsStorageConfig();

    StorageConfig getMessagesStorageConfig();
}
