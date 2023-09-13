package com.panera.cmt.mongo.repository;

import com.panera.cmt.mongo.entity.Audit;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IAuditRepository extends MongoRepository<Audit, String> {
}
