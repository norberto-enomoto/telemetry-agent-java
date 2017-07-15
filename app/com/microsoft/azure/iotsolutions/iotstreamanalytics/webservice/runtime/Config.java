// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iotstreamanalytics.webservice.runtime;

import com.microsoft.azure.iotsolutions.iotstreamanalytics.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.iotstreamanalytics.services.runtime.ServicesConfig;
import com.typesafe.config.ConfigFactory;

// TODO: documentation
// TODO: handle exceptions

public class Config implements IConfig {

    private final String Namespace = "com.microsoft.azure.iotsolutions.";
    private final String ApplicationKey = Namespace + "iot-stream-analytics.";

    private final String PortKey = ApplicationKey + "webservice-port";
    private final String IoTHubConnStringKey = ApplicationKey + "iothub.connstring";

    private com.typesafe.config.Config data;
    private IServicesConfig servicesConfig;

    public Config() {
        // Load `application.conf` and replace placeholders with
        // environment variables
        data = ConfigFactory.load();

        String cs = data.getString(IoTHubConnStringKey);
        this.servicesConfig = new ServicesConfig(cs);
    }

    /**
     * Get the TCP port number where the service listen for requests.
     *
     * @return TCP port number
     */
    public int getPort() {
        return data.getInt(PortKey);
    }

    /**
     * Service layer configuration
     */
    public IServicesConfig getServicesConfig() {
        return this.servicesConfig;
    }
}
