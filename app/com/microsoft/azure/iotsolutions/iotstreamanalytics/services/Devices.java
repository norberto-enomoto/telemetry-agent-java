// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iotstreamanalytics.services;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.iotstreamanalytics.services.models.Device;
import com.microsoft.azure.sdk.iot.service.RegistryManager;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

// TODO: handle exceptions
// TODO: logging
// TODO: documentation

public final class Devices implements IDevices {

    private static final int MAX_GET_LIST = 1000;

    private final RegistryManager registry;
    private final IDeviceTwins deviceTwins;

    @Inject
    public Devices(
        final IIoTHubWrapper ioTHubService,
        final IDeviceTwins deviceTwins)
        throws Exception {

        this.registry = ioTHubService.getRegistryManagerClient();
        this.deviceTwins = deviceTwins;
    }

    public CompletableFuture<ArrayList<Device>> getListAsync() {
        try {
            return this.registry.getDevicesAsync(MAX_GET_LIST)
                .thenApply(devices -> {
                    ArrayList<Device> result = new ArrayList<>();
                    for (com.microsoft.azure.sdk.iot.service.Device device : devices) {
                        result.add(new Device(device, null));
                    }
                    return result;
                });
        } catch (IOException e) {
            // TODO
            return null;
        } catch (IotHubException e) {
            // TODO
            return null;
        }
    }

    public CompletableFuture<Device> getAsync(final String id) {
        try {
            return this.registry.getDeviceAsync(id)
                .thenApply(device -> new Device(device, this.deviceTwins.get(id)));
        } catch (IOException e) {
            // TODO
            return null;
        } catch (IotHubException e) {
            // TODO
            return null;
        }
    }

    public CompletableFuture<Device> createAsync(final Device device) {
        // TODO
        return null;
    }
}
