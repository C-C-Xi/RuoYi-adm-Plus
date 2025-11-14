package org.dromara.game.config.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.excel.core.ExcelResult;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schema")
@RequiredArgsConstructor
@Slf4j
public class ConfigSchemaController {
    private final ConfigSchemaService schemaService;

    /**
     * 导出参数配置列表
     * @param response
     * @param formData
     */
    @Log(title = "导出配置列表", businessType = BusinessType.EXPORT)
    @PostMapping("export/excel")
    public void export(@RequestParam Map<String, String> formData, HttpServletResponse response) {

        schemaService.selectConfigList(formData.get("tableName"), Integer.valueOf(formData.get("UrlId")),response);

    }
    /**
     * 导入参数数据
     * @param file
     */
    @PostMapping(value = "importData/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<Void> importData(@RequestParam("file") MultipartFile file,
                              @RequestParam Map<String, String> formDat) {
        log.info("导入数据：" + JsonUtils.toJsonString(formDat));
        schemaService.importData(formDat.get("tableName"), Integer.valueOf(formDat.get("UrlId")),Boolean.valueOf(formDat.get("updateSupport")),file);
        return R.ok();
    }

    /**
     * 获取所有配置的地址
     */
    public R<List<Map<String, Object>>> getUrls() {
        return R.ok(schemaService.getUrls());
    }

    /**
     * 获取指定表名的字段
     * @param tableName
     */
    @GetMapping("/{tableName}/columns")
    public R<List<SchemaColumnBo>> getSchemaColumns(@PathVariable String tableName) {
        return R.ok(schemaService.getSchemaColumns(tableName));
    }

    /**
     * 获取指定表名的字段
     * @param tableName
     */
    @GetMapping("/{tableName}/list")
    public TableDataInfo getSchema(PageQuery pageQuery, @RequestParam(required = false) Integer Id, @RequestParam(required = false) Integer UrlId, @PathVariable String tableName) {
        return schemaService.getSchemaItems(tableName,pageQuery, Id,UrlId );
    }
    /**
     * 获取指定表名和id的数据
     * @param tableName
     * @param id
     * @param urlId
     */
    @GetMapping("/{tableName}/{urlId}/{id}")
    public R<Map> getSchema(@PathVariable int urlId,@PathVariable int id,@PathVariable String tableName) {
        return R.ok(schemaService.getSchemaItemInfo(tableName,urlId,id));
    }

    /**
     * 添加数据
     * @param tableName
     * @param data
     * @return
     */
    @PostMapping("/{tableName}")
    public R addSchemaItem(@PathVariable String tableName, @RequestBody Map<String, Object> data) {
        log.info("添加数据：" + JsonUtils.toJsonString(data));
        log.info("添加数据：" + JsonUtils.toJsonString(data));
        schemaService.addSchemazItem(tableName, data);
        return R.ok();
    }

    /**
     * 更新 数据
     * @param tableName
     * @param data
     * @return
     */
    @PutMapping("/{tableName}")
    public R updateSchemaItem(@PathVariable String tableName, @RequestBody Map<String, Object> data) {
        schemaService.updateSchemaItem(tableName, data);
        return R.ok();
    }
    /**
     * 删除数据
     * @param tableName
     * @param ids
     * @return
     */
    @DeleteMapping("/{tableName}/{ids}")
    public R deleteItem(@PathVariable String tableName,@PathVariable String[] ids) {
        schemaService.deleteItem(tableName, ids);
        return R.ok();
    }



}