package org.dromara.common.mongo.repository;

import org.dromara.common.mongo.model.backend.SchemaHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SchemaHistoryRepository extends MongoRepository<SchemaHistory, String> {
    List<SchemaHistory> findBySchemaIdOrderByVersionDesc(String schemaId);
}
