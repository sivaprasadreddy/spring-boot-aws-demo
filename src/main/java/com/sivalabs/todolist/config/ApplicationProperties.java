package com.sivalabs.todolist.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("myapp")
public class ApplicationProperties {

    private String queueName;
    private LocalstackProperties localstack = new LocalstackProperties();

    @Data
    public static class LocalstackProperties {
        private String endpointURI;
        private String region;
    }
}
