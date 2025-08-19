package org.dromara.common.mongo.repository.backend;

import org.dromara.common.mongo.model.backend.RecordHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RecordHistoryRepository extends MongoRepository<RecordHistory, String> {
    List<RecordHistory> findByRecordIdOrderByVersionDesc(String recordId);
}