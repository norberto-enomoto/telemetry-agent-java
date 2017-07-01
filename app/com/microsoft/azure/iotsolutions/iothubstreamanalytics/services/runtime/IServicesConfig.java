// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubstreamanalytics.services.runtime;

public interface IServicesConfig {

    /**
     * Get Azure IoT Hub connection string.
     *
     * @return Connection string
     */
    String getHubConnString();
}
