package com.sivalabs.awsdemo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@MappedSuperclass
@Setter
@Getter
public abstract class BaseEntity {
    @JsonProperty("created_at")
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @JsonProperty("updated_at")
    @Column(insertable = false)
    private LocalDateTime updatedAt;

    abstract Long getId();

    abstract void setId(Long id);

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
