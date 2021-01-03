package com.sivalabs.awsdemo.web.controller;

import com.sivalabs.awsdemo.entity.Message;
import com.sivalabs.awsdemo.service.MessageService;
import com.sivalabs.awsdemo.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageRestController {

    private final MessageService messageService;
    private final NotificationService notificationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void sendMessage(@RequestBody Message message) {
        messageService.sendMessage(message.getContent());
        notificationService.sendMessage(message.getContent());
    }

    @GetMapping
    public List<Message> getMessages() {
        return messageService.getMessages();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        boolean success = messageService.deleteMessage(id);
        if (success) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
