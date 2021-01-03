package com.sivalabs.awsdemo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sivalabs.awsdemo.config.ApplicationProperties;
import com.sivalabs.awsdemo.entity.Message;
import com.sivalabs.awsdemo.external.aws.SQSService;
import com.sivalabs.awsdemo.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final MessageRepository messageRepository;
    private final SQSService sqsService;
    private final ObjectMapper objectMapper;
    private final ApplicationProperties properties;

    public void sendMessage(String text) {
        Message message = new Message();
        message.setContent(text);
        message.setCreatedAt(LocalDateTime.now());
        try {
            sqsService.sendMessage(properties.getQueueName(), objectMapper.writeValueAsString(message));
        } catch (Exception e) {
            log.error("Error while sending message to queue.", e);
        }
    }

    @SqsListener(value = "${awsdemo.queue-name}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void queueListener(String payload) throws JsonProcessingException {
        log.info("Received SQS message: {}", payload);
        Message message = objectMapper.readValue(payload, Message.class);
        messageRepository.save(message);
    }

    public List<Message> getMessages() {
        return messageRepository.findAll();
    }

    public boolean deleteMessage(Long id) {
        Optional<Message> msgOptional = messageRepository.findById(id);
        if (msgOptional.isPresent()) {
            messageRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
