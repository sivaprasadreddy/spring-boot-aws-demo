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
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "todos")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Todo extends BaseEntity {
    @Id
    @SequenceGenerator(name = "todo_id_generator", sequenceName = "todo_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "todo_id_generator")
    private Long id;

    @Column(nullable = false)
    @NotEmpty
    private String content;

}
