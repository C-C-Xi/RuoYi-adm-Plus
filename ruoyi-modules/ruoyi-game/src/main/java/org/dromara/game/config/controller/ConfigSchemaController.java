package org.dromara.game.config.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.game.config.domain.bo.SchemaColumnBo;
import org.dromara.game.config.service.ConfigSchemaService;
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
    public R<List<Map>> getSchema(PageQuery pageQuery, @PathVariable String tableName) {
        return R.ok(schemaService.getSchemaItems(tableName,pageQuery));
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
}