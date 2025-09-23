package org.dromara.game.config.domain.bo;

import lombok.Data;

import java.util.List;

@Data
public class SchemaBo {
    private String collection;
    private String displayName;
    private String primaryKey;
    private List<SchemaColumnBo> columns;
}
