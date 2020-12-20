package com.sivalabs.awsdemo.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("awsdemo")
public class ApplicationProperties {
    private String endpointUri;
    private String region;
    private String queueName;
}
