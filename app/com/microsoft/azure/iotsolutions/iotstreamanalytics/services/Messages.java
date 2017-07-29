// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iotstreamanalytics.services;

import com.google.inject.Inject;
import com.microsoft.azure.documentdb.*;
import com.microsoft.azure.iot.iothubreact.MessageFromDevice;
import com.microsoft.azure.iotsolutions.iotstreamanalytics.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.iotstreamanalytics.services.runtime.StorageConfig;
import play.Logger;

import java.util.ArrayList;

// TODO: decouple the class from DocumentDb:
//  * use a generic storage interface, e.g. allow using Cassandra
//  * wrap exception in generic storage exceptions
// https://github.com/Azure/iot-stream-analytics-java/issues/20
// TODO: resilience to errors and throttling
public class Messages implements IMessages {

    private static final Logger.ALogger log = Logger.of(Messages.class);

    private final IDeviceMessageParser messageParser;
    private final IAlarms alarms;
    private final DocumentClient docDbConnection;
    private final String docDbDatabase;
    private final String docDbCollection;
    private final int docDbRUs;
    private final RequestOptions docDbOptions;

    @Inject
    public Messages(
        IServicesConfig config,
        IDeviceMessageParser messageParser,
        IAlarms alarms) throws DocumentClientException {

        final StorageConfig storageConfig = config.getMessagesStorageConfig();

        this.messageParser = messageParser;
        this.alarms = alarms;
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
        this.alarms.process(m);
        this.saveMessage(m);
    }

    private void saveMessage(MessageFromDevice m) {

        Document doc = this.messageParser.messageToDocument(m);

        String collectionLink = String.format("/dbs/%s/colls/%s",
            this.docDbDatabase, this.docDbCollection);
        try {
            this.docDbConnection.createDocument(collectionLink, doc, this.docDbOptions, true);
        } catch (DocumentClientException e) {
            if (e.getStatusCode() != 409) {
                log.error("Error while writing message", e);
                // TODO: fix, message gets lost otherwise
            }
        }
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

            IndexingPolicy indexing = new IndexingPolicy();
            indexing.setIndexingMode(IndexingMode.Lazy);
            //indexing.setIncludedPaths(new ArrayList<IncludedPath>() {{
            //    add(new IncludedPath("/device/id"));
            //    add(new IncludedPath("/doc/schema"));
            //    add(new IncludedPath("/doc/schemaVersion"));
            //    add(new IncludedPath("/device/msg/schema"));
            //    add(new IncludedPath("/device/msg/created"));
            //    add(new IncludedPath("/device/msg/received"));
            //}});
            info.setIndexingPolicy(indexing);

            PartitionKeyDefinition partitionKeyDefinition = new PartitionKeyDefinition();
            partitionKeyDefinition.setPaths(new ArrayList<String>() {{
                add("/id");
            }});
            info.setPartitionKey(partitionKeyDefinition);

            String databaseLink = String.format("/dbs/%s", this.docDbDatabase);

            this.docDbConnection.createCollection(databaseLink, info, this.docDbOptions);
        } catch (Exception e) {
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
}
