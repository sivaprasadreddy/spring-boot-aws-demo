package com.sivalabs.todolist.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("aws")
public class AwsProperties {
    private String endpointURI;
    private String region;
}
