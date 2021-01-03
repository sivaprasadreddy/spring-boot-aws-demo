package com.sivalabs.awsdemo.external.aws;

import com.amazonaws.services.sns.AmazonSNS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SNSService {
    private final NotificationMessagingTemplate notificationMessagingTemplate;

    public SNSService(AmazonSNS amazonSNS) {
        this.notificationMessagingTemplate = new NotificationMessagingTemplate(amazonSNS);
    }

    public void sendMessage(String topic, String subject, String msg) {
        this.notificationMessagingTemplate.sendNotification(topic, msg, subject);
    }
}
