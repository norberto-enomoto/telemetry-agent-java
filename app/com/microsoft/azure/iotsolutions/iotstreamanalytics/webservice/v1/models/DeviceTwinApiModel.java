// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iotstreamanalytics.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.iotstreamanalytics.services.models.DeviceTwin;
import com.microsoft.azure.iotsolutions.iotstreamanalytics.webservice.v1.Version;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Dictionary;
import java.util.Hashtable;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class DeviceTwinApiModel {

    private String eTag;
    private String deviceId;
    private Hashtable<String, Object> tags;

    private DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");

    public DeviceTwinApiModel(final String id, final DeviceTwin twin) {

        if (twin != null) {
            this.eTag = twin.getEtag();
        } else {
            this.eTag = "";
        }

        this.deviceId = id;
        this.tags = new Hashtable<String, Object>();
    }

    @JsonProperty("DeviceId")
    public String getDeviceId() {
        return this.deviceId;
    }

    @JsonProperty("DeviceId")
    public void setDeviceId(String value) {
        this.deviceId = value;
    }

    @JsonProperty("Etag")
    public String getETag() {
        return this.eTag;
    }

    @JsonProperty("Etag")
    public void setETag(String value) {
        this.eTag = value;
    }

    @JsonProperty("Tags")
    public Hashtable<String, Object> getTags() {
        return this.tags;
    }

    @JsonProperty("Tags")
    public void setTags(Hashtable<String, Object> value) {
        this.tags = value;
    }

    @JsonProperty("$metadata")
    public Dictionary<String, String> getMetadata() {
        String id = this.getDeviceId();
        return new Hashtable<String, String>() {{
            put("$type", "DeviceTwin;" + Version.NAME);
            put("$uri", "/" + Version.NAME + "/devices/" + id);

            // Entity version number, maintained by the service, e.g. with
            // a dedicated property in the storage entity schema.
            // TODO: use the correct value, this is just a sample
            put("$version", "123");

            // When the entity was created (if supported by the storage)
            // TODO: use the correct value, this is just a sample
            put("$created", dateFormat.print(DateTime.now(DateTimeZone.UTC)));

            // Last time the entity was modified (if supported by the storage)
            // TODO: use the correct value, this is just a sample
            put("$modified", dateFormat.print(DateTime.now(DateTimeZone.UTC)));
        }};
    }

    public DeviceTwin toServiceModel() {
        // TODO
        return null;
    }
}
