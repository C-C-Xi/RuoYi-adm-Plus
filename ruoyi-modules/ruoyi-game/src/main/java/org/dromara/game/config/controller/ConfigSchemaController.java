package org.dromara.game.config.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.game.config.domain.bo.SchemaBo;
import org.dromara.game.config.domain.bo.SchemaColumnBo;
import org.dromara.game.config.domain.req.ConfigExportParam;
import org.dromara.game.config.service.ConfigSchemaService;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schema")
@RequiredArgsConstructor
@Slf4j
public class ConfigSchemaController {
    private final ConfigSchemaService schemaService;


    @GetMapping("/urls")
    public R<List<Map<String, Object>>> getUrls() {
        return R.ok(schemaService.getUrls());
    }



    @GetMapping("/{tableName}/columns")
    public R<List<SchemaColumnBo>> getSchemaColumns(@PathVariable String tableName) {
        return R.ok(schemaService.getSchemaColumns(tableName));
    }
    @GetMapping("/{tableName}/list")
    public TableDataInfo getSchema(PageQuery pageQuery, @RequestParam(required = false) Integer Id, @RequestParam(required = false) Integer UrlId, @PathVariable String tableName) {
        return schemaService.getSchemaItems(tableName,pageQuery, Id,UrlId );
    }
    @GetMapping("/{tableName}/{urlId}/{id}")
    public R<Map> getSchema(@PathVariable int urlId,@PathVariable int id,@PathVariable String tableName) {
        return R.ok(schemaService.getSchemaItemInfo(tableName,urlId,id));
    }
    @PostMapping("/{tableName}")
    public R addSchemaItem(@PathVariable String tableName, @RequestBody Map<String, Object> data) {
        log.info("添加数据：" + JsonUtils.toJsonString(data));
        log.info("添加数据：" + JsonUtils.toJsonString(data));
        schemaService.addSchemazItem(tableName, data);
        return R.ok();
    }
    @PutMapping("/{tableName}")
    public R updateSchemaItem(@PathVariable String tableName, @RequestBody Map<String, Object> data) {
        schemaService.updateSchemaItem(tableName, data);
        return R.ok();
    }
    @DeleteMapping("/{tableName}/{ids}")
    public R deleteItem(@PathVariable String tableName,@PathVariable String[] ids) {
        schemaService.deleteItem(tableName, ids);
        return R.ok();
    }


    /**
     * 导出参数配置列表
     */
    @Log(title = "导出配置列表", businessType = BusinessType.EXPORT)
    @PostMapping("export")
    public void export(@RequestBody ConfigExportParam param, HttpServletResponse response) {

        List<Map> list = schemaService.selectConfigList(param.getTableName(), param.getUrlId());
        ExcelUtil.exportExcel(list, "配置列表", Map.class, response);
    }

}