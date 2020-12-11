package com.tutorial.springbootmultitenancymongo.configuration;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.tutorial.springbootmultitenancymongo.domain.TenantDatasource;
import com.tutorial.springbootmultitenancymongo.exception.TenantAliasNotFoundException;
import com.tutorial.springbootmultitenancymongo.filter.TenantContext;
import com.tutorial.springbootmultitenancymongo.service.RedisDatasourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class MongoDataSources {


    /**
     * Key: String tenant alias
     * Value: TenantDatasource
     */
    private Map<String, TenantDatasource> tenantClients;

    private final ApplicationProperties applicationProperties;
    private final RedisDatasourceService redisDatasourceService;

    public MongoDataSources(ApplicationProperties applicationProperties, RedisDatasourceService redisDatasourceService) {
        this.applicationProperties = applicationProperties;
        this.redisDatasourceService = redisDatasourceService;
    }


    /**
     * Initialize all mongo datasource
     */
    @PostConstruct
    @Lazy
    public void initTenant() {
        tenantClients = new HashMap<>();
        tenantClients = redisDatasourceService.loadServiceDatasources();
    }

    /**
     * Default Database name for spring initialization. It is used to be injected into the constructor of MultiTenantMongoDBFactory.
     *
     * @return String of default database.
     */
    @Bean
    public String databaseName() {
        return applicationProperties.getDatasourceDefault().getDatabase();
    }

    /**
     * Default Mongo Connection for spring initialization.
     * It is used to be injected into the constructor of MultiTenantMongoDBFactory.
     */
    @Bean
    public MongoClient getMongoClient() {
        MongoCredential credential = MongoCredential.createCredential(applicationProperties.getDatasourceDefault().getUsername(), applicationProperties.getDatasourceDefault().getDatabase(), applicationProperties.getDatasourceDefault().getPassword().toCharArray());
        return MongoClients.create(MongoClientSettings.builder()
                .applyToClusterSettings(builder ->
                        builder.hosts(Collections.singletonList(new ServerAddress(applicationProperties.getDatasourceDefault().getHost(), Integer.parseInt(applicationProperties.getDatasourceDefault().getPort())))))
                .credential(credential)
                .build());
    }

    /**
     * This will get called for each DB operations
     * Resiliency handlers can be added here.
     *
     * @return MongoDatabase
     */
    public MongoDatabase getMongoDatabaseForCurrentContext() {
        try {
            final String tenantId = TenantContext.getTenantId();

            // Compose tenant alias. (tenantAlias = key + tenantId)
            String tenantAlias = String.format("%s_%s", applicationProperties.getTenantKey(), (tenantId != null && !tenantId.isEmpty()) ? tenantId : applicationProperties.getDatasourceDefault().getAlias());

            return tenantClients.get(tenantAlias).getClient().
                    getDatabase(tenantClients.get(tenantAlias).getDatabase());
        } catch (NullPointerException exception) {
            throw new TenantAliasNotFoundException("Tenant Datasource alias not found.");
        }
    }
}