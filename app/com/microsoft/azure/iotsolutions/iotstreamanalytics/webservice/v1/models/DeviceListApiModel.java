// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iotstreamanalytics.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.iotstreamanalytics.services.models.Device;
import com.microsoft.azure.iotsolutions.iotstreamanalytics.webservice.v1.Version;

import java.util.*;

public final class DeviceListApiModel {

    private final ArrayList<DeviceApiModel> items;

    public DeviceListApiModel(final ArrayList<Device> devices) {

        this.items = new ArrayList<>();
        for (Device device : devices) {
            this.items.add(new DeviceApiModel(device));
        }
    }

    @JsonProperty("Items")
    public ArrayList<DeviceApiModel> getItems() {
        return items;
    }

    @JsonProperty("$metadata")
    public Dictionary<String, String> getMetadata() {
        return new Hashtable<String, String>() {{
            put("$type", "DeviceList;" + Version.NAME);
            put("$uri", "/" + Version.NAME + "/devices");
        }};
    }
}
