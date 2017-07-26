// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iotstreamanalytics.streamingAgent.runtime;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.iotstreamanalytics.services.runtime.IServicesConfig;

@ImplementedBy(Config.class)
public interface IConfig {

    /**
     * Service layer configuration
     */
    IServicesConfig getServicesConfig();
}
