package com.panera.cmt.mongo.entity;

import com.panera.cmt.enums.ActionType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.util.Date;

@Data
@Document(collection = "cmtAudit")
public class Audit {

    @Id
    private String id;

    @Indexed
    private ActionType actionType;

    @Indexed
    private String subject;

    @Indexed
    private Long personId;

    private Long objectId;

    private Object payload;

    @Indexed
    private Integer statusCode;

    @Indexed
    protected String createdBy;

    protected Date createdAt;

    @Indexed(expireAfterSeconds = 0)
    private Date expirationDate;
}
