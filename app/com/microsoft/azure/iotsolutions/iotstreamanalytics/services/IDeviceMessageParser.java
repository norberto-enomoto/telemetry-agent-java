// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iotstreamanalytics.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.documentdb.Document;
import com.microsoft.azure.iot.iothubreact.MessageFromDevice;

@ImplementedBy(DeviceMessageParser.class)
public interface IDeviceMessageParser
{
    Document messageToDocument(MessageFromDevice m);
}
