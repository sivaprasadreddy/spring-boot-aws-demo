package com.sivalabs.todolist.service;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TodoCleanupJob {
    private final SQSService sqsService;

    @Scheduled(fixedDelay = 5000)
    public void cleanup() {
      log.info("Running clean up at {}", LocalDateTime.now());
        sqsService.sendMessage("test_queue", "TestMessage-"+LocalDateTime.now().toString());
    }

    @SqsListener(value = "test_queue", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void queueListener(String payload) {
        log.info("Received SQS message: {}", payload);
    }
}
