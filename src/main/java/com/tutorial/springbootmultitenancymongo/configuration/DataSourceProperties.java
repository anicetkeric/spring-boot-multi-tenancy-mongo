package com.tutorial.springbootmultitenancymongo.configuration;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

@Data
@ConfigurationProperties(prefix = "tenants")
@PropertySource(value = "classpath:tenants.yml", factory = YamlPropertySourceFactory.class)
@Component
public class DataSourceProperties {

    private List<Tenant> datasources = new ArrayList<>();

    @Getter
    @Setter
    public static class Tenant {
        private String alias;
        private String host;
        private int port;
        private String database;
        private String username;
        private String password;
    }

}

class YamlPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertiesPropertySource createPropertySource(@Nullable String name, EncodedResource resource) throws IOException {
        Properties propertiesFromYaml = loadYamlIntoProperties(resource);
        String sourceName = name != null ? name : resource.getResource().getFilename();
        return new PropertiesPropertySource(Objects.requireNonNull(sourceName), propertiesFromYaml);
    }

    private Properties loadYamlIntoProperties(EncodedResource resource) throws FileNotFoundException {
        try {
            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            factory.setResources(resource.getResource());
            factory.afterPropertiesSet();
            return factory.getObject();
        } catch (IllegalStateException e) {
            // for ignoreResourceNotFound
            Throwable cause = e.getCause();
            if (cause instanceof FileNotFoundException)
                throw (FileNotFoundException) e.getCause();
            throw e;
        }
    }
}