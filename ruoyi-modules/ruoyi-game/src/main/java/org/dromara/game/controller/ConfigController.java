package org.dromara.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    @Autowired
    @Qualifier("toGameConfigMongoTemplate")
    private MongoTemplate toGameConfigMongoTemplate;

    // 获取所有配置表列表
    @GetMapping("/collections")
    public List<Map> listCollections() {
        return toGameConfigMongoTemplate.findAll(Map.class, "config_schema");
    }

    // 创建一个新的配置表（仅注册 schema）
    @PostMapping("/schema/create")
    public void createSchema(@RequestBody Map<String, Object> schema) {
        toGameConfigMongoTemplate.insert(schema, "config_schema");
    }

    // 删除配置表（仅删 schema，不删数据）
    @PostMapping("/schema/delete")
    public void deleteSchema(@RequestBody String collection) {
        Query query = Query.query(Criteria.where("collection").is(collection));
        toGameConfigMongoTemplate.remove(query, "config_schema");
    }

    // 更新字段定义（更新整个 columns）
    @PostMapping("/schema/updateFields/{collection}")
    public void updateSchemaFields(@PathVariable String collection, @RequestBody List<Map<String, Object>> columns) {
        Query query = Query.query(Criteria.where("collection").is(collection));
        Update update = new Update().set("columns", columns);
        toGameConfigMongoTemplate.updateFirst(query, update, "config_schema");
    }

    // 获取某个配置表的 schema
    @GetMapping("/schema/{collection}")
    public Map<String, Object> getSchema(@PathVariable String collection) {
        Query query = Query.query(Criteria.where("collection").is(collection));
        return toGameConfigMongoTemplate.findOne(query, Map.class, "config_schema");
    }

    // 获取该表数据列表
    @GetMapping("/list/{collection}")
    public List<Map> list(@PathVariable String collection) {
        return toGameConfigMongoTemplate.findAll(Map.class, collection);
    }

    // 保存或更新
    @PostMapping("/save/{collection}")
    public void save(@PathVariable String collection, @RequestBody Map<String, Object> data) {
        toGameConfigMongoTemplate.save(data, collection);
    }

    // 删除
    @PostMapping("/delete/{collection}")
    public void delete(@PathVariable String collection, @RequestBody List<String> ids) {
        ids.forEach(id -> toGameConfigMongoTemplate.remove(Query.query(Criteria.where("_id").is(id)), collection));
    }
}
