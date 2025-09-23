package org.dromara.game.config.controller;

import org.bson.Document;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.game.config.domain.CollectionBo;
import org.dromara.game.config.domain.bo.SchemaColumnBo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.dromara.common.core.domain.R.ok;

@RestController
@RequestMapping("/api/config")
public class ConfigController {
    private static final String COLLECTION_NAME = "config_schema";
    @Autowired
    @Qualifier("toGameConfigMongoTemplate")
    private MongoTemplate toGameConfigMongoTemplate;

    // 获取所有配置表列表
    @GetMapping("/collections")
    public R<List<Map>> listCollections() {
        Query query=new Query();
        List<Map> collections = toGameConfigMongoTemplate.find(query,Map.class, COLLECTION_NAME);
        return R.ok(collections);
    }

    /**
     * 获取已有配置的表
     * @return
     */
    @GetMapping("/schema/collections")
    public R<List<Map>> getCollectionsConfig(PageQuery pageQuery, CollectionBo bo) {
        Query query=new Query();
        if(bo.getCollection() != null){
            query.addCriteria(Criteria.where("collection").is(bo.getCollection()));
        }
        if (bo.getDisplayName() != null){
            query.addCriteria(Criteria.where("displayName").regex(bo.getDisplayName()));
        }
        query.skip((pageQuery.getPageNum() - 1)*pageQuery.getPageSize());
        query.limit(pageQuery.getPageSize());
        List<Map> collections = toGameConfigMongoTemplate.find(query,Map.class, COLLECTION_NAME);
        return R.ok(collections);
    }

    // 创建一个新的配置表（仅注册 schema）
    @PostMapping("/schema/create")
    public R createSchema(@RequestBody SchemaColumnBo  schema) {
        toGameConfigMongoTemplate.insert(schema, COLLECTION_NAME);
        return R.ok();
    }

    // 删除配置表（仅删 schema，不删数据）
    @PostMapping("/schema/delete")
    public R deleteSchema(@RequestBody String collection) {
        Query query = Query.query(Criteria.where("collection").is(collection));
        long count = toGameConfigMongoTemplate.count(new Query(), Map.class, collection);
        if (count > 0) {
            return R.fail("该表有数据，请先清空数据");
        }
        toGameConfigMongoTemplate.remove(query, COLLECTION_NAME);
        return R.ok();
    }

    // 更新字段定义（更新整个 columns）
    @PostMapping("/schema/updateFields/{collection}")
    public R updateSchemaFields(@PathVariable String collection, @RequestBody List<Map<String, Object>> columns) {
        Query query = Query.query(Criteria.where("collection").is(collection));
        Update update = new Update().set("columns", columns);
        toGameConfigMongoTemplate.updateFirst(query, update, COLLECTION_NAME);
        return R.ok();
    }
    @GetMapping("/schema/column/{collection}/{fieldName}")
    public R getSchemaColumn(@PathVariable String collection,@PathVariable String fieldName) {
// ... existing code ...
        Query query = Query.query(Criteria.where("collection").is(collection));
        query.addCriteria(Criteria.where("columns.fieldName").is(fieldName));
        return R.ok(toGameConfigMongoTemplate.findOne(query, Map.class, COLLECTION_NAME));
    }
    @PostMapping("/schema/column/{collection}")
    public R addSchemaColumn(@PathVariable String collection, @RequestBody SchemaColumnBo schemaColumn ) {
        Query query = Query.query(Criteria.where("collection").is(collection));
        Update update = new Update().push("columns", schemaColumn);
        toGameConfigMongoTemplate.updateFirst(query, update, COLLECTION_NAME);
        return R.ok();
    }
    @PutMapping("/schema/column/{collection}/{index}")
    public R updateSchemaColumn(@PathVariable String collection,@PathVariable String index, @RequestBody SchemaColumnBo schemaColumn) {
        Query query = Query.query(Criteria.where("collection").is(collection));
        Update update = new Update().set( "columns." + index, schemaColumn);
        toGameConfigMongoTemplate.updateFirst(query, update, COLLECTION_NAME);
        return R.ok();
    }
    @DeleteMapping("/schema/column/{collection}/{fieldNames}")
    public R deleteSchemaColumn(@PathVariable String collection,@PathVariable String[] fieldNames) {
        Query query = Query.query(Criteria.where("collection").is(collection));
        // 使用预定义常量替换硬编码字符串，提高可读性和可维护性
        Document fieldCriteria = new Document("fieldName", new Document("$in", Arrays.asList(fieldNames)));
        Document pullOperation = new Document("$pull", new Document("columns", fieldCriteria));

        Update update = Update.fromDocument(pullOperation);
        toGameConfigMongoTemplate.updateFirst(query,update, COLLECTION_NAME);
// ... existing code ...

        return R.ok();
    }

    // 获取某个配置表的 schema
    @GetMapping("/schema/{collection}")
    public R<Map<String, Object>> getSchema(@PathVariable String collection) {
        Query query = Query.query(Criteria.where("collection").is(collection));
        return R.ok(toGameConfigMongoTemplate.findOne(query, Map.class, COLLECTION_NAME));
    }

    // 获取该表数据列表
    @GetMapping("/list/{collection}")
    public R<List<Map>> list(@PathVariable String collection) {
        return R.ok(toGameConfigMongoTemplate.findAll(Map.class, collection));
    }

    // 保存或更新
    @PostMapping("/save/{collection}")
    public R save(@PathVariable String collection, @RequestBody Map<String, Object> data) {
        toGameConfigMongoTemplate.save(data, collection);
        return R.ok();
    }

    // 删除
    @PostMapping("/delete/{collection}")
    public R delete(@PathVariable String collection, @RequestBody List<String> ids) {
        ids.forEach(id -> toGameConfigMongoTemplate.remove(Query.query(Criteria.where("_id").is(id)), collection));
        return R.ok();
    }
}
