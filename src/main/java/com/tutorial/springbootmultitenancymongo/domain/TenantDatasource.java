package com.tutorial.springbootmultitenancymongo.domain;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.*;

import java.util.Collections;

/**
 * <h2>TenantDatasource</h2>
 *
 * <p>
 * Description: this class is model for tenant datasource.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TenantDatasource {

    private String alias;
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
    private MongoClient client;


    public TenantDatasource(String alias, String host, int port, String databaseName, String username, String password) {
        this.alias = alias;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = databaseName;
        createClient();
    }

    /**
     * Init mongo client
     */
    private void createClient() {
        MongoCredential credential = MongoCredential.createCredential(username, database, password.toCharArray());
        client = MongoClients.create(MongoClientSettings.builder()
                .applyToClusterSettings(builder ->
                        builder.hosts(Collections.singletonList(new ServerAddress(host, port))))
                .credential(credential)
                .build());
    }
}
