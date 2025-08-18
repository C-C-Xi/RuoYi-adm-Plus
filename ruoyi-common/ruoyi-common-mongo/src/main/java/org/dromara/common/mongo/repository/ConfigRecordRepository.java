package org.dromara.common.mongo.repository;

import org.dromara.common.mongo.model.backend.ConfigRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ConfigRecordRepository extends MongoRepository<ConfigRecord, String> {
    List<ConfigRecord> findByTableName(String tableName, Pageable p);
    long countByTableName(String tableName);
}