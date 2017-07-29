// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iotstreamanalytics.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iot.iothubreact.MessageFromDevice;

@ImplementedBy(Alarms.class)
public interface IAlarms {
    void process(MessageFromDevice m);
}
