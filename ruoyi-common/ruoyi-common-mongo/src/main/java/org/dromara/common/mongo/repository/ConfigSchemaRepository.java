package org.dromara.common.mongo.repository;

import org.dromara.common.mongo.model.toGameConfig.ConfigSchema;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ConfigSchemaRepository extends MongoRepository<ConfigSchema, String> {
    Optional<ConfigSchema> findByTableName(String tableName);
}