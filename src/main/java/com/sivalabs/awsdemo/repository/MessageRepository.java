package com.sivalabs.awsdemo.repository;

import com.sivalabs.awsdemo.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
