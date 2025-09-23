package org.dromara.game.config.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.mongo.model.backend.SchemaHistory;
import org.dromara.common.mongo.model.toGameConfig.ConfigSchema;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.game.config.domain.bo.SchemaColumnBo;
import org.dromara.game.config.service.ConfigSchemaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schema")
@RequiredArgsConstructor
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
    @GetMapping("/{tableName}/info")
    public R<Map> getSchema(@PathVariable String tableName) {
        return R.ok(schemaService.getSchemaItemInfo(tableName));
    }
    @PostMapping("/{tableName}")
    public R addSchemaItem(@PathVariable String tableName, @RequestBody Map<String, Object> data) {
        schemaService.addSchemaItem(tableName, data);
        return R.ok();
    }
    @PutMapping("/{tableName}/")
    public R updateSchemaItem(@PathVariable String tableName, @RequestBody Map<String, Object> data) {
        schemaService.updateSchemaItem(tableName, data);
        return R.ok();
    }
}