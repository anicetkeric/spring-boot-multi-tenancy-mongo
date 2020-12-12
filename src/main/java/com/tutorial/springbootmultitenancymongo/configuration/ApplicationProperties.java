package com.tutorial.springbootmultitenancymongo.configuration;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Properties specific.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 */

@Data
@Component
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private String serviceKey;
    private String tenantKey;
    private final DatasourceDefault datasourceDefault = new DatasourceDefault();
    private final Encryption encryption = new Encryption();

    @Getter
    @Setter
    public static class DatasourceDefault {
        private String alias;
        private String host;
        private String port;
        private String database;
        private String username;
        private String password;
    }

    @Getter
    @Setter
    public static class Encryption {
        private String secret;
        private String salt;
    }

}
