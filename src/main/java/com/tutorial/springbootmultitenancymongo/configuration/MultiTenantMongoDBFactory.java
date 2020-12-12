package com.tutorial.springbootmultitenancymongo.configuration;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
public class MultiTenantMongoDBFactory extends SimpleMongoClientDatabaseFactory {

    @Autowired
    MongoDataSources mongoDataSources;

    public MultiTenantMongoDBFactory(@Qualifier("getMongoClient") MongoClient mongoClient, String databaseName) {
        super(mongoClient, databaseName);
    }

    @Override
    protected MongoDatabase doGetMongoDatabase(String dbName) {
        return mongoDataSources.mongoDatabaseCurrentTenantResolver();
    }
}