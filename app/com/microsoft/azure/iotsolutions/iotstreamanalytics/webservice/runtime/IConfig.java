// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iotstreamanalytics.webservice.runtime;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.iotstreamanalytics.services.runtime.IServicesConfig;

@ImplementedBy(Config.class)
public interface IConfig {
    /**
     * Get the TCP port number where the service listen for requests.
     *
     * @return TCP port number
     */
    int getPort();

    /**
     * Service layer configuration
     */
    IServicesConfig getServicesConfig();
}
