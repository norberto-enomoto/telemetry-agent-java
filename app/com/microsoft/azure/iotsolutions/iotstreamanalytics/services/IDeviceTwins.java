// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iotstreamanalytics.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.iotstreamanalytics.services.models.DeviceTwin;

import java.util.ArrayList;

// TODO: documentation

@ImplementedBy(DeviceTwins.class)
public interface IDeviceTwins {
     DeviceTwin get(final String id);

     ArrayList<DeviceTwin> getList();
}
