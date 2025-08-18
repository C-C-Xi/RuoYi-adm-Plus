package org.dromara.common.mongo.model.backend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.common.mongo.model.toGameConfig.ConfigSchema;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "config_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigRecord {
    @Id
    private String id;
    private String schemaId;
    private String tableName;
    private long version;
    private ConfigSchema snapshot; // 完整 snapshot（旧版本）
    private String operator;
    private Date opAt;
    private String opType; // CREATE / UPDATE / ROLLBACK / DELETE
    private String comment; // 可选回滚原因
}

