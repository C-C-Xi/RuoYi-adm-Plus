package org.dromara.common.mongo.model.backend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;

@Document(collection = "record_histories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordHistory {
    @Id
    private String id;
    private String recordId;
    private String tableName;
    private long version;
    private Map<String, Object> snapshot;
    private String operator;
    private Date opAt;
    private String opType; // CREATE/UPDATE/DELETE/ROLLBACK
    private String comment;
}