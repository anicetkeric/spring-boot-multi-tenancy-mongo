package com.tutorial.springbootmultitenancymongo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tutorial.springbootmultitenancymongo.configuration.ApplicationProperties;
import com.tutorial.springbootmultitenancymongo.configuration.DataSourceProperties;
import com.tutorial.springbootmultitenancymongo.domain.TenantDatasource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <h2>RedisDatasourceService</h2>
 * This class is used to store all information from tenant databases.
 */
@Service
public class RedisDatasourceService {


    private final RedisTemplate redisTemplate;

    private final ApplicationProperties applicationProperties;
    private final DataSourceProperties dataSourceProperties;
    private final EncryptionService encryptionService;

    public RedisDatasourceService(RedisTemplate redisTemplate, ApplicationProperties applicationProperties, DataSourceProperties dataSourceProperties, EncryptionService encryptionService) {
        this.redisTemplate = redisTemplate;
        this.applicationProperties = applicationProperties;
        this.dataSourceProperties = dataSourceProperties;
        this.encryptionService = encryptionService;
    }

    /**
     * Save tenant datasource infos
     *
     * @param tenantDatasource data of datasource
     * @return status if true save successfully , false error
     */
    public boolean save(TenantDatasource tenantDatasource) {
        try {
            Map ruleHash = new ObjectMapper().convertValue(tenantDatasource, Map.class);
            redisTemplate.opsForHash().put(applicationProperties.getServiceKey(), String.format("%s_%s", applicationProperties.getTenantKey(), tenantDatasource.getAlias()), ruleHash);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get all of keys
     *
     * @return list of datasource
     */
    public List findAll() {
        return redisTemplate.opsForHash().values(applicationProperties.getServiceKey());
    }

    /**
     * Get datasource
     *
     * @return map key and datasource infos
     */
    public Map<String, TenantDatasource> loadServiceDatasources() {

        List<Map<String, Object>> datasourceConfigList = findAll();

        // Save datasource credentials first time
        // In production mode, this part can be skip
        if (datasourceConfigList.isEmpty()) {

            List<DataSourceProperties.Tenant> tenants = dataSourceProperties.getDatasources();
            tenants.forEach(d -> {
                String encryptedPassword = encryptionService.encrypt(d.getPassword(), applicationProperties.getEncryption().getSecret(), applicationProperties.getEncryption().getSalt());

                TenantDatasource tenant = TenantDatasource.builder()
                        .alias(d.getAlias())
                        .database(d.getDatabase())
                        .host(d.getHost())
                        .port(d.getPort())
                        .username(d.getUsername())
                        .password(encryptedPassword)
                        .build();

                save(tenant);
            });

        }

        return getDataSourceHashMap();
    }

    /**
     * Get all tenant alias
     *
     * @return list of alias
     */
    public List<String> getTenantsAlias() {
        // get list all datasource for this microservice
        List<Map<String, Object>> datasourceConfigList = findAll();

        return datasourceConfigList.stream().map(data -> (String) data.get("alias")).collect(Collectors.toList());
    }

    /**
     * Fill the data sources list.
     *
     * @return Map<String, TenantDatasource>
     */
    private Map<String, TenantDatasource> getDataSourceHashMap() {

        Map<String, TenantDatasource> datasourceMap = new HashMap<>();

        // get list all datasource for this microservice
        List<Map<String, Object>> datasourceConfigList = findAll();

        datasourceConfigList.forEach(data -> {
            String decryptedPassword = encryptionService.decrypt((String) data.get("password"), applicationProperties.getEncryption().getSecret(), applicationProperties.getEncryption().getSalt());
            datasourceMap.put(String.format("%s_%s", applicationProperties.getTenantKey(), (String) data.get("alias")), new TenantDatasource((String) data.get("alias"), (String) data.get("host"), (int) data.get("port"), (String) data.get("database"), (String) data.get("username"), decryptedPassword));
        });

        return datasourceMap;
    }
}
