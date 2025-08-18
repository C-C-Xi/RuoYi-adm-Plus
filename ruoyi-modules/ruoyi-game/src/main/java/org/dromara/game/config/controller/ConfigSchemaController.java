package org.dromara.game.config.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.dromara.common.mongo.model.backend.SchemaHistory;
import org.dromara.common.mongo.model.toGameConfig.ConfigSchema;
import org.dromara.game.config.service.ConfigSchemaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schemas")
@RequiredArgsConstructor
public class ConfigSchemaController {
    private final ConfigSchemaService schemaService;

    @PostMapping
    public ResponseEntity<ConfigSchema> createSchema(@RequestBody ConfigSchema schema, @RequestHeader("X-User") String user) {
        return ResponseEntity.ok(schemaService.createSchema(schema, user));
    }

    @GetMapping("/{tableName}/history")
    public ResponseEntity<List<SchemaHistory>> getHistory(@PathVariable String tableName) {
        return ResponseEntity.ok(schemaService.getSchemaHistory(tableName));
    }
}