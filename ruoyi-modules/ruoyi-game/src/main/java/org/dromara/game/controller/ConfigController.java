package org.dromara.game.controller;

import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.game.domain.CollectionBo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.dromara.common.core.domain.R.ok;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    @Autowired
    @Qualifier("toGameConfigMongoTemplate")
    private MongoTemplate toGameConfigMongoTemplate;

    // 获取所有配置表列表
    @GetMapping("/collections")
    public R<Set<String>> listCollections() {
        return R.ok(toGameConfigMongoTemplate.getCollectionNames());
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
        List<Map> collections = toGameConfigMongoTemplate.find(query,Map.class, "config_schema");
        return R.ok(collections);
    }

    // 创建一个新的配置表（仅注册 schema）
    @PostMapping("/schema/create")
    public R createSchema(@RequestBody Map<String, Object> schema) {
        toGameConfigMongoTemplate.insert(schema, "config_schema");
        return R.ok();
    }

    // 删除配置表（仅删 schema，不删数据）
    @PostMapping("/schema/delete")
    public R deleteSchema(@RequestBody String collection) {
        Query query = Query.query(Criteria.where("collection").is(collection));
        toGameConfigMongoTemplate.remove(query, "config_schema");
        return R.ok();
    }

    // 更新字段定义（更新整个 columns）
    @PostMapping("/schema/updateFields/{collection}")
    public R updateSchemaFields(@PathVariable String collection, @RequestBody List<Map<String, Object>> columns) {
        Query query = Query.query(Criteria.where("collection").is(collection));
        Update update = new Update().set("columns", columns);
        toGameConfigMongoTemplate.updateFirst(query, update, "config_schema");
        return R.ok();
    }

    // 获取某个配置表的 schema
    @GetMapping("/schema/{collection}")
    public R<Map<String, Object>> getSchema(@PathVariable String collection) {
        Query query = Query.query(Criteria.where("collection").is(collection));
        return R.ok(toGameConfigMongoTemplate.findOne(query, Map.class, "config_schema"));
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
