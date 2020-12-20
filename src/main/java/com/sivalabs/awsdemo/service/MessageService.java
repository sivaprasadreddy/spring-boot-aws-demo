package com.sivalabs.awsdemo.service;

import java.time.LocalDateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sivalabs.awsdemo.config.ApplicationProperties;
import com.sivalabs.awsdemo.entity.Message;
import com.sivalabs.awsdemo.external.aws.SQSService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final SQSService sqsService;
    private final ObjectMapper objectMapper;
    private final ApplicationProperties properties;

    public void sendMessage(String text) {
        Message message = new Message();
        message.setContent(text);
        message.setCreatedAt(LocalDateTime.now());

        try {
            sqsService.sendMessage(properties.getQueueName(), objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            log.error("Error while sending message to queue.", e);
        }
    }

    @SqsListener(value = "${awsdemo.queue-name}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void queueListener(String payload) {
        log.info("Received SQS message: {}", payload);
    }
}
