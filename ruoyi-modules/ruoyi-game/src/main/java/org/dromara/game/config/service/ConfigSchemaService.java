package org.dromara.game.config.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.game.config.domain.bo.SchemaColumnBo;

import java.util.List;
import java.util.Map;

public interface ConfigSchemaService {

    public List<Map> getSchemaItems(String tableName, PageQuery pageQuery);

    Map getSchemaItemInfo(String tableName,int urlId,int id);

    void addSchemazItem(String tableName, Map<String, Object> data);

    List<Map<String, Object>> getUrls();

    List<SchemaColumnBo> getSchemaColumns(String tableName);

    void updateSchemaItem(String tableName, Map<String, Object> data);
}