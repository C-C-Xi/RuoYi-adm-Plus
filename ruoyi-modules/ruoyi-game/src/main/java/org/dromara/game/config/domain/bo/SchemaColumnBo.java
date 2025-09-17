package org.dromara.game.config.domain.bo;

import lombok.Data;

import java.util.List;

@Data
public class SchemaColumnBo {
    private String label;
    private String fieldName;
    private String component;
    private boolean  required;
    private List<Options> options;
    @Data
    class Options {
        private String label;
        private String value;
    }
}
