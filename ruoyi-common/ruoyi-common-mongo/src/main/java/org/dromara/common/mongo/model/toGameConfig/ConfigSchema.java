package org.dromara.common.mongo.model.toGameConfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Document(collection = "config_schema")
@Data
@AllArgsConstructor
public class ConfigSchema {
    @Id
    private String id; // 对应 ObjectId，Spring Data 会自动转换

    private String collection;

    private String displayName;

    private String primaryKey;

    private Long createTime;

    private Long updateTime;

    private List<SchemaColumn> columns;


    public ConfigSchema() {
        this.updateTime = new Date().getTime();
        this.primaryKey = "Id";
    }
}