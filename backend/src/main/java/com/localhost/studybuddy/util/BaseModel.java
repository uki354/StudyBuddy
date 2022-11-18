package com.localhost.studybuddy.util;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.Instant;

@MappedSuperclass
@Data
public class BaseModel {

    @Column(name = "is_deleted", columnDefinition = "TINYINT(1)")
    private boolean isDeleted;
    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    private Instant createdAt;
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private Instant updatedAt;
    @Column(name = "deleted_at", columnDefinition = "TIMESTAMP")
    private Instant deletedAt;

}
