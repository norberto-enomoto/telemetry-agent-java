// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iotstreamanalytics.services.models;

import com.microsoft.azure.documentdb.Document;

import java.util.*;

public class RawMessage {

    public static final String STRING = "STRING";
    public static final String NUMBER = "NUMBER";
    public static final String BOOLEAN = "BOOLEAN";

    private final String MESSAGE_ID = "id";
    private final String DEVICE_ID = "device.id";
    private final String MESSAGE_SCHEMA = "device.msg.schema";
    private final String MESSAGE_CREATED = "device.msg.created";
    private final String MESSAGE_RECEIVED = "device.msg.received";

    private final HashMap<String, String> keys;
    private final HashMap<String, Double> numbers;
    private final HashMap<String, String> strings;
    private final HashMap<String, Boolean> booleans;

    public RawMessage() {
        this.keys = new HashMap<>();
        this.numbers = new HashMap<>();
        this.strings = new HashMap<>();
        this.booleans = new HashMap<>();
    }

    public RawMessage setId(String v){
        this.setString(MESSAGE_ID, v);
        return this;
    }

    public RawMessage setDeviceId(String v){
        this.setString(DEVICE_ID, v);
        return this;
    }

    public String getDeviceId(){
        return this.getString(DEVICE_ID);
    }

    public RawMessage setSchema(String v){
        this.setString(MESSAGE_SCHEMA, v);
        return this;
    }

    public RawMessage setCreateTime(long v){
        this.setNumber(MESSAGE_CREATED, v);
        return this;
    }

    public RawMessage setReceivedTime(long v){
        this.setNumber(MESSAGE_RECEIVED, v);
        return this;
    }

    public long getReceivedTime(){
        return this.getNumber(MESSAGE_RECEIVED).longValue();
    }

    public RawMessage setNumber(String k, double v) {
        this.keys.put(k, NUMBER);
        this.numbers.put(k, v);
        return this;
    }

    public RawMessage setNumber(String k, int v) {
        this.keys.put(k, NUMBER);
        this.numbers.put(k, (double) v);
        return this;
    }

    public RawMessage setNumber(String k, long v) {
        this.keys.put(k, NUMBER);
        this.numbers.put(k, (double) v);
        return this;
    }

    public Double getNumber(String k) {
        return this.numbers.get(k);
    }

    public RawMessage setString(String k, String v) {
        this.keys.put(k, STRING);
        this.strings.put(k, v);
        return this;
    }

    public String getString(String k) {
        return this.strings.get(k);
    }

    public RawMessage setBoolean(String k, Boolean v) {
        this.keys.put(k, BOOLEAN);
        this.booleans.put(k, v);
        return this;
    }

    public Boolean getBoolean(String k) {
        return this.booleans.get(k);
    }

    public boolean has(String k) {
        return this.keys.containsKey(k);
    }

    public String typeOf(String k) {
        if (!this.has(k)) return null;
        return this.keys.get(k);
    }

    public Document toDocument() {
        Document document = new Document();

        document.set("doc.schema", "d2cmessage");
        document.set("doc.schemaVersion", 1);

        document.setId(this.getString(MESSAGE_ID));
        for (Map.Entry<String, String> k : this.keys.entrySet()) {
            if (k.getKey() != MESSAGE_ID) {
                switch (k.getValue()) {
                    case STRING:
                        document.set(k.getKey(), this.getString(k.getKey()));
                        break;
                    case NUMBER:
                        document.set(k.getKey(), this.getNumber(k.getKey()));
                        break;
                    case BOOLEAN:
                        document.set(k.getKey(), this.getBoolean(k.getKey()));
                        break;
                }
            }
        }

        return document;
    }
}
