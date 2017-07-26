// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iotstreamanalytics.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.azure.documentdb.Document;
import com.microsoft.azure.iot.iothubreact.MessageFromDevice;
import play.Logger;
import play.libs.Json;
import scala.Option;

import java.util.Iterator;
import java.util.Map;

public class DeviceMessageParser implements IDeviceMessageParser
{
    private static final Logger.ALogger log = Logger.of(DeviceMessageParser.class);

    @Override
    public Document messageToDocument(MessageFromDevice m)
    {
        String schema = m.messageSchema();
        String contentType = m.contentType();
        String content = m.contentAsString();

        Document document = new Document();
        document.setId(m.deviceId() + ";" + m.received().toEpochMilli());
        document.set("@pcsDocumentType", "d2cMessage");
        document.set("@iotDeviceId", m.deviceId());
        document.set("@iotSchema", schema);
        document.set("@iotCreated", m.created().toEpochMilli());
        document.set("@iotReceived", m.received().toEpochMilli());

        if (contentType.toLowerCase().contains("json")
                || schema.toLowerCase().contains("json")
                || (contentType.isEmpty() && content.contains("{")))
        {
            JsonNode data = this.tryExtractFromJson(content);

            if (data == null)
            {
                // Log as an error, including stream information
                log.error("Invalid JSON, partition:{}, offset:{}, msgId:{}, device:{}, msgTime:{}, msg:{}",
                        getPartitionNumber(m), m.offset(), m.messageId(),
                        m.deviceId(), m.received(), content);
            } else if (data.isObject())
            {
                Iterator<Map.Entry<String, JsonNode>> fields = data.fields();
                while (fields.hasNext())
                {
                    Map.Entry<String, JsonNode> field = fields.next();
                    document.set(field.getKey(), field.getValue().asText());
                }
            } else if (data.isNumber())
            {
                document.set("valueAsDouble", data.asDouble());
            } else if (data.isTextual())
            {
                document.set("valueAsText", data.asText());
            }
        }

        return document;
    }

    private JsonNode tryExtractFromJson(String content)
    {
        try
        {
            return Json.parse(content);
        } catch (Throwable t)
        {
            return null;
        }
    }

    private int getPartitionNumber(MessageFromDevice m)
    {
        Option<Object> partitionNumber = m.runtimeInfo().partitionInfo().partitionNumber();
        return partitionNumber.isEmpty() ? -1 : (int) partitionNumber.get();
    }
}
