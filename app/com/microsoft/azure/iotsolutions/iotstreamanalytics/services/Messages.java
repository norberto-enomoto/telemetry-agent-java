// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iotstreamanalytics.services;

import com.google.inject.Inject;
import com.microsoft.azure.documentdb.*;
import com.microsoft.azure.iot.iothubreact.MessageFromDevice;
import com.microsoft.azure.iotsolutions.iotstreamanalytics.services.runtime.IServicesConfig;
import play.Logger;

// TODO: decouple the class from DocumentDb:
//  * use a generic storage interface, e.g. allow using Cassandra
//  * wrap exception in generic storage exceptions
// https://github.com/Azure/iot-stream-analytics-java/issues/20
// TODO: resilience to errors and throttling
public class Messages implements IMessages
{
    private static final Logger.ALogger log = Logger.of(Messages.class);

    private final DocumentClient docDbConnection;
    private final String docDbDatabase;
    private final String docDbCollection;
    private final int docDbRUs;
    private final IDeviceMessageParser messageParser;

    @Inject
    public Messages(
            IServicesConfig servicesConfig,
            IDeviceMessageParser messageParser)
            throws DocumentClientException
    {
        this.messageParser = messageParser;
        this.docDbConnection = new DocumentClient(
                servicesConfig.getMessagesStorageConfig().getDocumentDbUri(),
                servicesConfig.getMessagesStorageConfig().getDocumentDbKey(),
                ConnectionPolicy.GetDefault(),
                ConsistencyLevel.Eventual);

        this.docDbDatabase = servicesConfig.getMessagesStorageConfig().getDocumentDbDatabase();
        this.docDbCollection = servicesConfig.getMessagesStorageConfig().getDocumentDbCollection();
        this.docDbRUs = servicesConfig.getMessagesStorageConfig().getDocumentDbRUs();

        this.createDatabaseIfNotExists();
        this.createCollectionIfNotExists();
    }

    @Override
    public void process(MessageFromDevice m)
    {
        this.saveMessage(m);
    }

    private void saveMessage(MessageFromDevice m)
    {
        // Skip internal messages (we can probably remove this, TBC)
        if (m.isKeepAlive()) return;

        Document doc = this.messageParser.messageToDocument(m);

        String collectionLink = String.format("/dbs/%s/colls/%s",
                this.docDbDatabase, this.docDbCollection);
        try
        {
            this.docDbConnection.upsertDocument(collectionLink, doc, null, true);
        } catch (DocumentClientException e)
        {
            log.error("Error while writing message", e);
        }
    }

    private void createDatabaseIfNotExists()
            throws DocumentClientException
    {
        String databaseLink = String.format("/dbs/%s", this.docDbDatabase);
        try
        {
            this.docDbConnection.readDatabase(databaseLink, null);
        } catch (DocumentClientException e)
        {
            if (e.getStatusCode() != 404)
            {
                log.error("Error while getting DocumentDb database", e);
                throw e;
            }

            log.info("Creating DocumentDb database: {}", this.docDbDatabase);
            Database database = new Database();
            database.setId(this.docDbDatabase);
            this.docDbConnection.createDatabase(database, null);
        }
    }

    private void createCollectionIfNotExists()
            throws DocumentClientException
    {
        String databaseLink = String.format("/dbs/%s", this.docDbDatabase);
        String collectionLink = String.format("/dbs/%s/colls/%s",
                this.docDbDatabase, this.docDbCollection);

        try
        {
            this.docDbConnection.readCollection(collectionLink, null);
        } catch (DocumentClientException e)
        {
            if (e.getStatusCode() != 404)
            {
                log.error("Error while getting DocumentDb collection", e);
                throw e;
            }

            log.info("Creating DocumentDb collection: {}", this.docDbCollection);

            DocumentCollection collectionInfo = new DocumentCollection();
            collectionInfo.setId(this.docDbCollection);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.setOfferThroughput(docDbRUs);
            this.docDbConnection.createCollection(
                    databaseLink, collectionInfo, requestOptions);
        }
    }
}
