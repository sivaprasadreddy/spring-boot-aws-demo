package com.sivalabs.awsdemo.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("aws")
public class AwsProperties {
    private String endpointUri;
    private String region;
}
