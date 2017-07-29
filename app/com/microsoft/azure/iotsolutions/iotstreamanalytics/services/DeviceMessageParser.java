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

public class DeviceMessageParser implements IDeviceMessageParser {
    private static final Logger.ALogger log = Logger.of(DeviceMessageParser.class);

    private final String docSchemaKey = "doc.schema";
    private final String docSchemaValue = "d2cmessage";

    private final String docSchemaVersionKey = "doc.schemaVersion";
    private final int docSchemaVersionValue = 1;

    private final String deviceIdKey = "device.id";
    private final String deviceMessageSchemaKey = "device.msg.schema";
    private final String deviceMessageCreatedKey = "device.msg.created";
    private final String deviceMessageReceivedKey = "device.msg.received";

    private final String userDataPrefix = "data.";

    @Override
    public Document messageToDocument(MessageFromDevice m) {
        String schema = m.messageSchema();
        String contentType = m.contentType();
        String content = m.contentAsString();

        Document document = new Document();
        document.setId(m.deviceId() + ";" + m.received().toEpochMilli());
        document.set(docSchemaKey, docSchemaValue);
        document.set(docSchemaVersionKey, docSchemaVersionValue);
        document.set(deviceIdKey, m.deviceId());
        document.set(deviceMessageSchemaKey, schema);
        document.set(deviceMessageCreatedKey, m.created().toEpochMilli());
        document.set(deviceMessageReceivedKey, m.received().toEpochMilli());

        if (contentType.toLowerCase().contains("json")
            || schema.toLowerCase().contains("json")
            || (contentType.isEmpty() && content.contains("{"))) {
            JsonNode data = this.tryExtractFromJson(content);

            if (data == null) {
                // Log as an error, including stream information
                log.error("Invalid JSON, partition:{}, offset:{}, msgId:{}, device:{}, msgTime:{}, msg:{}",
                    getPartitionNumber(m), m.offset(), m.messageId(),
                    m.deviceId(), m.received(), content);
            } else if (data.isObject()) {
                Iterator<Map.Entry<String, JsonNode>> fields = data.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> field = fields.next();
                    JsonNode value = field.getValue();
                    if (value.isNumber()) {
                        document.set(userDataPrefix + field.getKey(), field.getValue().asDouble());
                    } else if (value.isBoolean()) {
                        document.set(userDataPrefix + field.getKey(), field.getValue().asBoolean());
                    } else {
                        document.set(userDataPrefix + field.getKey(), field.getValue().asText(""));
                    }
                }
            } else if (data.isNumber()) {
                document.set(userDataPrefix + "value", data.asDouble());
            } else if (data.isBoolean()) {
                document.set(userDataPrefix + "value", data.asBoolean());
            } else if (data.isTextual()) {
                document.set(userDataPrefix + "value", data.asText());
            }
        }

        return document;
    }

    private JsonNode tryExtractFromJson(String content) {
        try {
            return Json.parse(content);
        } catch (Throwable t) {
            return null;
        }
    }

    private int getPartitionNumber(MessageFromDevice m) {
        Option<Object> partitionNumber = m.runtimeInfo().partitionInfo().partitionNumber();
        return partitionNumber.isEmpty() ? -1 : (int) partitionNumber.get();
    }
}
