package org.dromara.common.mongo.model.toGameConfig;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class SchemaColumn {
    private String fieldName;

    private String label;

    private String component;

    private Boolean required;

    private String dataType;

    private Integer order;

    // 注意：_class 字段通常由 Spring Data 自动管理（用于多态）
    // 如果你不需要多态，可以忽略或显式排除
    // 如果保留，建议用 @Field("_class") 显式映射
    @Field("_class")
    private String clazz; // 避免与 Java 关键字冲突，用 clazz 或 _class（需配置）
}
