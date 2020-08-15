package com.sivalabs.todolist.common;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.localstack.LocalStackContainer;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

@TestConfiguration
public class LocalStackConfig {

    public static final String TEST_ACCESS_KEY = "test";
    public static final String TEST_SECRET_KEY = "test";

    public static final AWSCredentials TEST_CREDENTIALS = new BasicAWSCredentials(TEST_ACCESS_KEY, TEST_SECRET_KEY);

    static LocalStackContainer localStackContainer;

    static {
        System.setProperty("com.amazonaws.sdk.disableCbor", "true");
        localStackContainer = new LocalStackContainer("0.11.2")
            .withServices(SQS)
            .withExposedPorts(4566);
        localStackContainer.start();
    }

    @Bean
    @Primary
    public AmazonSQSAsync localstackAmazonSQSAsync() {
        AmazonSQSAsync amazonSQSAsync = AmazonSQSAsyncClientBuilder.standard()
            .withEndpointConfiguration(localStackContainer.getEndpointConfiguration(SQS))
            .withCredentials(getCredentialsProvider())
            .build();

        return amazonSQSAsync;
    }

    private AWSCredentialsProvider getCredentialsProvider() {
        return new AWSStaticCredentialsProvider(TEST_CREDENTIALS);
    }
}
