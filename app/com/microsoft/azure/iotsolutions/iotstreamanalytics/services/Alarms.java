// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iotstreamanalytics.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.microsoft.azure.documentdb.*;
import com.microsoft.azure.iot.iothubreact.MessageFromDevice;
import com.microsoft.azure.iotsolutions.iotstreamanalytics.services.models.AlarmServiceModel;
import com.microsoft.azure.iotsolutions.iotstreamanalytics.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.iotstreamanalytics.services.runtime.StorageConfig;
import org.joda.time.DateTime;
import play.Logger;
import play.libs.Json;

import java.util.*;

// TODO: decouple the class from DocumentDb:
//  * use a generic storage interface, e.g. allow using Cassandra
//  * wrap exception in generic storage exceptions
// https://github.com/Azure/iot-stream-analytics-java/issues/20
// TODO: resilience to errors and throttling
public class Alarms implements IAlarms {

    private static final Logger.ALogger log = Logger.of(Alarms.class);

    private final DocumentClient docDbConnection;
    private final String docDbDatabase;
    private final String docDbCollection;
    private final int docDbRUs;
    private final RequestOptions docDbOptions;
    private static Random rand = new Random();
    private long lastAlarmCreated = 0;

    private final String docSchemaKey = "doc.schema";
    private final String docSchemaValue = "alarm";

    private final String docSchemaVersionKey = "doc.schemaVersion";
    private final int docSchemaVersionValue = 1;

    private final String createdKey = "created";
    private final String modifiedKey = "modified";
    private final String descriptionKey = "description";
    private final String statusKey = "status";
    private final String deviceIdKey = "device.id";

    private final String ruleIdKey = "rule.id";
    private final String ruleSeverityKey = "rule.severity";
    private final String ruleDescriptionKey = "rule.description";

    @Inject
    public Alarms(IServicesConfig config) throws DocumentClientException {

        final StorageConfig storageConfig = config.getAlarmsStorageConfig();

        this.docDbConnection = new DocumentClient(
            storageConfig.getDocumentDbUri(),
            storageConfig.getDocumentDbKey(),
            ConnectionPolicy.GetDefault(),
            ConsistencyLevel.Eventual);

        this.docDbDatabase = storageConfig.getDocumentDbDatabase();
        this.docDbCollection = storageConfig.getDocumentDbCollection();
        this.docDbRUs = storageConfig.getDocumentDbRUs();
        this.docDbOptions = this.getDocDbOptions();

        this.createDatabaseIfNotExists();
        this.createCollectionIfNotExists();
    }

    @Override
    public void process(MessageFromDevice m) {
        AlarmServiceModel alarm = this.generateFakeAlarms(m);
        if (alarm != null) {
            log.info("Alarm! " + alarm.getDescription());
            this.saveAlarm(alarm);
        }
    }

    private void saveAlarm(AlarmServiceModel alarm) {
        Document doc = this.alarmToDocument(alarm);

        String collectionLink = String.format("/dbs/%s/colls/%s",
            this.docDbDatabase, this.docDbCollection);
        try {
            this.docDbConnection.createDocument(collectionLink, doc, this.docDbOptions, true);
        } catch (DocumentClientException e) {
            if (e.getStatusCode() != 409) {
                log.error("Error while writing alarm", e);
            }
        }
    }

    private Document alarmToDocument(AlarmServiceModel alarm) {

        Document document = new Document();

        // TODO: make inserts idempotent
        document.setId(UUID.randomUUID().toString());
        document.set(docSchemaKey, docSchemaValue);
        document.set(docSchemaVersionKey, docSchemaVersionValue);
        document.set(createdKey, alarm.getDateCreated().getMillis());
        document.set(modifiedKey, alarm.getDateModified().getMillis());
        document.set(statusKey, alarm.getStatus());
        document.set(descriptionKey, alarm.getDescription());
        document.set(deviceIdKey, alarm.getDeviceId());
        document.set(ruleIdKey, alarm.getRuleId());
        document.set(ruleSeverityKey, alarm.getRuleSeverity());
        document.set(ruleDescriptionKey, alarm.getRuleDescription());

        // The logic used to generate the alarm (future proofing for ML)
        document.set("logic", "1Device-1Rule-1Message");

        return document;
    }

    private void createDatabaseIfNotExists()
        throws DocumentClientException {
        String databaseLink = String.format("/dbs/%s", this.docDbDatabase);
        try {
            this.docDbConnection.readDatabase(databaseLink, this.docDbOptions);
        } catch (DocumentClientException e) {
            if (e.getStatusCode() != 404) {
                log.error("Error while getting DocumentDb database", e);
                throw e;
            }

            this.createDatabase();
        } catch (Exception e) {
            log.error("Error while getting DocumentDb database", e);
            throw e;
        }
    }

    private void createCollectionIfNotExists()
        throws DocumentClientException {

        String collectionLink = String.format("/dbs/%s/colls/%s",
            this.docDbDatabase, this.docDbCollection);

        try {
            this.docDbConnection.readCollection(collectionLink, this.docDbOptions);
            return;
        } catch (DocumentClientException e) {
            if (e.getStatusCode() != 404) {
                log.error("Error while getting DocumentDb collection", e);
                throw e;
            }

            this.createCollection();
        } catch (Exception e) {
            log.error("Error while getting DocumentDb collection", e);
            throw e;
        }
    }

    private void createDatabase() throws DocumentClientException {
        try {
            log.info("Creating DocumentDb database: {}", this.docDbDatabase);
            Database database = new Database();
            database.setId(this.docDbDatabase);
            this.docDbConnection.createDatabase(database, this.docDbOptions);
        } catch (Exception e) {
            log.error("Error while creating DocumentDb database", e);
            throw e;
        }
    }

    private void createCollection() throws DocumentClientException {
        try {
            log.info("Creating DocumentDb collection: {}", this.docDbCollection);

            DocumentCollection info = new DocumentCollection();
            info.setId(this.docDbCollection);

            RangeIndex index = Index.Range(DataType.String, -1);
            IndexingPolicy indexing = new IndexingPolicy(new Index[]{index});
            indexing.setIndexingMode(IndexingMode.Consistent);
            info.setIndexingPolicy(indexing);

            PartitionKeyDefinition partitionKeyDefinition = new PartitionKeyDefinition();
            partitionKeyDefinition.setPaths(new ArrayList<String>() {{
                add("/id");
            }});
            info.setPartitionKey(partitionKeyDefinition);

            String databaseLink = String.format("/dbs/%s", this.docDbDatabase);

            this.docDbConnection.createCollection(databaseLink, info, this.docDbOptions);
        } catch (DocumentClientException e) {
            if (e.getStatusCode() != 409) {
                log.error("Error while getting DocumentDb collection", e);
                throw e;
            }

            log.warn("Another process already created the collection", e);
        }
        catch (Exception e) {
            log.error("Error while creating DocumentDb collection", e);
            throw e;
        }
    }

    private RequestOptions getDocDbOptions() {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.setOfferThroughput(this.docDbRUs);
        requestOptions.setConsistencyLevel(ConsistencyLevel.Eventual);
        return requestOptions;
    }

    // TODO: temporary code, remove it
    private AlarmServiceModel generateFakeAlarms(MessageFromDevice m) {

        final String open = "open";
        final String acknowledged = "acknowledged";
        final String closed = "closed";
        final String critical = "critical";
        final String warning = "warning";
        final String info = "info";

        // Don't create more than 1 alarm every 30 seconds
        final int frequency = 30;

        long now = DateTime.now().getMillis() / 1000;
        if (this.lastAlarmCreated == 0) {
            this.lastAlarmCreated = now;
            return null;
        }

        if (now - this.lastAlarmCreated < frequency) return null;

        // Try to deserialize the message
        JsonNode data;
        try {
            data = Json.parse(m.contentAsString());
            if (!data.isObject()) return null;
        } catch (Throwable t) {
            return null;
        }

        boolean headOrTails1 = rand.nextInt(2) == 1;
        boolean headOrTails2 = rand.nextInt(2) == 1;
        String severity = headOrTails1 ? critical : headOrTails2 ? warning : info;

        if (data.hasNonNull("temperature") && headOrTails1) {
            this.lastAlarmCreated = now;
            return new AlarmServiceModel(
                UUID.randomUUID().toString(),
                DateTime.now(),
                DateTime.now(),
                "Temperature too high: " + data.get("temperature"),
                m.deviceId(),
                open,
                "fakeRuleId-1234",
                severity,
                "A fake rule for temperature"
            );
        }

        if (data.hasNonNull("humidity") && headOrTails1) {
            this.lastAlarmCreated = now;
            return new AlarmServiceModel(
                UUID.randomUUID().toString(),
                DateTime.now(),
                DateTime.now(),
                "Humidity too high: " + data.get("humidity"),
                m.deviceId(),
                open,
                "fakeRuleId-1999",
                severity,
                "A fake rule about humidity"
            );
        }

        if (data.hasNonNull("floor") && headOrTails1) {
            this.lastAlarmCreated = now;
            return new AlarmServiceModel(
                UUID.randomUUID().toString(),
                DateTime.now(),
                DateTime.now(),
                "Blocked at floor: " + data.get("floor"),
                m.deviceId(),
                open,
                "fakeRuleId-007",
                severity,
                "A fake rule about building floors"
            );
        }

        if (data.hasNonNull("speed") && headOrTails1) {
            this.lastAlarmCreated = now;
            return new AlarmServiceModel(
                UUID.randomUUID().toString(),
                DateTime.now(),
                DateTime.now(),
                "Excessive speed: " + data.get("speed"),
                m.deviceId(),
                open,
                "fakeRuleId-66",
                severity,
                "A fake rule about speed"
            );
        }

        return null;
    }
}
