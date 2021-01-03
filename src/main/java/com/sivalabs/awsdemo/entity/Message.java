package com.sivalabs.awsdemo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "messages")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Message extends BaseEntity {
    @Id
    @SequenceGenerator(name = "msg_id_generator", sequenceName = "msg_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "msg_id_generator")
    private Long id;

    @Column(nullable = false)
    private String content;
}
