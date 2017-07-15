// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iotstreamanalytics.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.iotstreamanalytics.services.models.Device;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

// TODO: documentation

@ImplementedBy(Devices.class)
public interface IDevices {

    CompletableFuture<ArrayList<Device>> getListAsync();

    CompletableFuture<Device> getAsync(String id);

    CompletableFuture<Device> createAsync(Device device);
}
