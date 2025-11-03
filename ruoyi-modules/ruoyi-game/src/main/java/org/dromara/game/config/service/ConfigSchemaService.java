package org.dromara.game.config.service;

import jakarta.servlet.http.HttpServletResponse;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.game.config.domain.bo.SchemaColumnBo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ConfigSchemaService {

    public TableDataInfo<Map> getSchemaItems(String tableName, PageQuery pageQuery, Integer Id, Integer UrlId);

    Map getSchemaItemInfo(String tableName,int urlId,int id);

    void addSchemazItem(String tableName, Map<String, Object> data);

    List<Map<String, Object>> getUrls();

    List<SchemaColumnBo> getSchemaColumns(String tableName);

    void updateSchemaItem(String tableName, Map<String, Object> data);

    void deleteItem(String tableName, String[] ids);

    void selectConfigList(String tableName,Integer UrlId, HttpServletResponse response);

    void importData(String tableName, Integer urlId, MultipartFile file);
}