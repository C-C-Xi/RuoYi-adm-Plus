package org.dromara.common.mongo.model.toGameConfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Document(collection = "config_schemas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigSchema {
    @Id
    private String id; // UUID or ObjectId
    private String tableName; // 唯一 key，例如 "app_config", 可作为前端路由参数
    private String displayName;
    private long version; // schema 版本号，自增
    private List<FieldDef> fields; // 字段定义列表
    private Date createdAt;
    private Date updatedAt;
    private String createdBy;
    private String updatedBy;

    @Data
    public static class FieldDef {
        private String field; // 字段名
        private String label; // 显示名
        private String formType; // input/select/number/date/textarea
        private Boolean required;
        private Map<String,Object> extra; // 如 select options 等
    }
}