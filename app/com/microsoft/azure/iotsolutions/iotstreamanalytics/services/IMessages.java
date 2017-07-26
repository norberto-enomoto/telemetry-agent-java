// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iotstreamanalytics.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iot.iothubreact.MessageFromDevice;

@ImplementedBy(Messages.class)
public interface IMessages {
    void process(MessageFromDevice m);
}
