package com.sivalabs.awsdemo.service;

import com.sivalabs.awsdemo.config.ApplicationProperties;
import com.sivalabs.awsdemo.external.aws.SNSService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final SNSService snsService;
    private final ApplicationProperties properties;

    public void sendMessage(String text) {
        try {
            final String subject = "SNS Demo Message";
            snsService.sendMessage(properties.getTopicName(), subject, text);

        } catch (Exception e) {
            log.error("Error while sending message to SNS topic.", e);
        }
    }
}
