package org.dromara.game.config.domain.req;

import lombok.Data;

@Data
public class ConfigExportParam {
    private String tableName;
    private Integer urlId;
}
