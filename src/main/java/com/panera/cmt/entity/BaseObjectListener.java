package com.panera.cmt.entity;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

import static com.panera.cmt.config.Constants.PLATFORM_CODE;
import static com.panera.cmt.filter.HttpLoggingFilter.getUsername;

public class BaseObjectListener {

    @PrePersist
    public void beforeSave(AuditableEntity entity) {
        entity.setCreatedBy(getUsername());
        entity.setLastUpdatedBy(getUsername());
        entity.setLastUpdatedByPlatform(PLATFORM_CODE);
        entity.setCreatedAt(new Date());
        entity.setLastUpdatedAt(new Date());
        entity.setActive(true);
    }

    @PreUpdate
    public void beforeUpdate(AuditableEntity entity) {
        entity.setLastUpdatedBy(getUsername());
        entity.setLastUpdatedByPlatform(PLATFORM_CODE);
        entity.setLastUpdatedAt(new Date());
    }
}
