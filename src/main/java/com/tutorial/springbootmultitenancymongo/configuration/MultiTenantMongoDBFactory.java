package com.tutorial.springbootmultitenancymongo.configuration;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
@Slf4j
public class MultiTenantMongoDBFactory extends SimpleMongoClientDatabaseFactory {

    @Autowired
    MongoDataSources mongoDataSources;

    public MultiTenantMongoDBFactory(MongoClient mongoClient, String databaseName) {
        super(mongoClient, databaseName);
    }

    @Override
    protected MongoDatabase doGetMongoDatabase(String dbName) {
        return mongoDataSources.getMongoDatabaseForCurrentContext();
    }

}