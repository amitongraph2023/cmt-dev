package com.panera.cmt.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@EntityListeners(BaseObjectListener.class)
@Getter
@MappedSuperclass
@Setter
public abstract class AuditableEntity {

    @Column(name = "CRTE_BY_NM")
    protected String createdBy;

    @Column(name = "CRTE_DTTM")
    protected Date createdAt;

    @Column(name = "UPDT_BY_NM")
    protected String lastUpdatedBy;

    @Column(name = "UPDT_BY_PLATFORM_CD")
    protected String lastUpdatedByPlatform;

    @Column(name = "UPDT_DTTM")
    protected Date lastUpdatedAt;

    @Type(type="yes_no")
    @Column(name = "ACTIVE_IND")
    protected boolean isActive;
}