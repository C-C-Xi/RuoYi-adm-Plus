package org.dromara.game.config.controller;

import cn.idev.excel.FastExcel;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import cn.idev.excel.read.listener.ReadListener;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.dromara.common.core.domain.R;
import org.dromara.common.excel.core.ExcelResult;
import org.dromara.common.excel.core.MapDataListener;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.mongo.model.toGameConfig.ConfigSchema;
import org.dromara.common.mongo.model.toGameConfig.SchemaColumn;
import org.dromara.common.mongo.repository.toGameConfig.ConfigSchemaRepository;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.game.config.domain.CollectionBo;
import org.dromara.game.config.domain.bo.SchemaBo;
import org.dromara.game.config.domain.bo.SchemaColumnBo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.dromara.common.core.domain.R.ok;

@RestController
@RequestMapping("/api/config")
public class ConfigController {
    private static final String COLLECTION_NAME = "config_schema";
    @Autowired
    @Qualifier("toGameConfigMongoTemplate")
    private MongoTemplate toGameConfigMongoTemplate;
    @Autowired
    private ConfigSchemaRepository configSchemaRepository;

    /**
     * 获取所有配置表
     * @return
     */
    @GetMapping("/collections")
    public R listCollections() {
//        Query query=new Query();
//        List<Map> collections = toGameConfigMongoTemplate.find(query,Map.class, COLLECTION_NAME);
        return R.ok(configSchemaRepository.findAll());
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

    /**
     * 创建配置表
     * @param schema
     * @return
     */
    @PostMapping("/schema/create")
    public R createSchema(@RequestBody SchemaColumnBo  schema) {
        toGameConfigMongoTemplate.insert(schema, COLLECTION_NAME);
        return R.ok();
    }
    /**
     * 通过excel创建配置表
     * @param formDat
     * @return
     */
    @PostMapping(path = "/schema/excel/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R createSchemaByExcel(@RequestParam("file") MultipartFile file,
                                 @RequestParam Map<String, String> formDat) throws IOException {
        String collectionName = formDat.get("collectionName");
        if (collectionName == null || collectionName.isEmpty()) {
            return R.fail("请填写表名");
        }
        String displayName = formDat.get("displayName");
        if (displayName == null || displayName.isEmpty()) {
            return R.fail("请填写展示名");
        }
        String primaryKey = formDat.get("primaryKey");
        if (primaryKey == null || primaryKey.isEmpty()) {
            return R.fail("请填写表主键");
        }
        System.out.println( collectionName);
        System.out.println( displayName);
        List<List<String>> rows = new ArrayList<>();
        MapDataListener listener = new MapDataListener();
        FastExcel.read(file.getInputStream(), listener).headRowNumber(1).sheet().doRead();
        ;
        List<Map<String, String>> list =listener.getExcelResult().getList();
        int columnCount = list.get(0).size();
        rows.add(list.get(0).keySet().stream().toList());
        rows.add(list.get(0).values().stream().toList());
        rows.add(list.get(1).values().stream().toList());
        List<SchemaColumn> columns = new ArrayList<>();
        for (int col = 0; col < columnCount; col++) {
            SchemaColumn colBo = new SchemaColumn();
            colBo.setOrder(col + 1);
            colBo.setComponent("input");
            if (rows.size() > 0 && col < rows.get(0).size()){
                colBo.setLabel(rows.get(0).get(col));
            }
            if (rows.size() > 1 && col < rows.get(1).size()){
                colBo.setFieldName(rows.get(1).get(col));
            }
            colBo.setDataType("string");
            if (rows.size() > 2 && col < rows.get(2).size()) {
                String dataType = rows.get(2).get(col);
                Set<String> validTypes = new HashSet<>(Arrays.asList("int", "long", "string"));
                colBo.setDataType(validTypes.contains(dataType) ? dataType : "string");
            }
            columns.add(colBo);
        }
        ConfigSchema schema = new ConfigSchema();
        schema.setCollection(collectionName);
        schema.setDisplayName(displayName);
        schema.setColumns(columns);
        schema.setPrimaryKey(primaryKey);
        if(configSchemaRepository.findByCollection(collectionName).isPresent()){
            return R.fail("该表已存在");
        }
        configSchemaRepository.insert(schema);
        return R.ok();
    }

    /**
     * 删除配置表
     * @param collection
     * @return
     */
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

    /**
     * 更新字段定义
     * @param collection
     * @param columns
     * @return
     */
    @PostMapping("/schema/updateFields/{collection}")
    public R updateSchemaFields(@PathVariable String collection, @RequestBody List<Map<String, Object>> columns) {
        Query query = Query.query(Criteria.where("collection").is(collection));
        Update update = new Update().set("columns", columns);
        toGameConfigMongoTemplate.updateFirst(query, update, COLLECTION_NAME);
        return R.ok();
    }
    /**
     * 获取字段定义
     * @param collection
     * @param fieldName
     * @return
     */
    public R getSchemaColumn(@PathVariable String collection,@PathVariable String fieldName) {
        System.out.println("fieldName"+fieldName);
// ... existing code ...
        Query query = Query.query(Criteria.where("collection").is(collection));
        query.addCriteria(Criteria.where("columns.fieldName").is(fieldName));
        SchemaBo schemaBo=toGameConfigMongoTemplate.findOne(query, SchemaBo.class, COLLECTION_NAME);
        System.out.println("schemaBo"+schemaBo);
        SchemaColumnBo schemaColumnBo=schemaBo.getColumns().stream().filter(column -> column.getFieldName().equals(fieldName)).findFirst().get();
        return R.ok(schemaColumnBo);
    }
    /**
     * 添加字段定义
     * @param collection
     * @param schemaColumn
     * @return
     */
    public R addSchemaColumn(@PathVariable String collection, @RequestBody SchemaColumnBo schemaColumn ) {
        Query query = Query.query(Criteria.where("collection").is(collection));
        Update update = new Update().push("columns", schemaColumn);
        toGameConfigMongoTemplate.updateFirst(query, update, COLLECTION_NAME);
        return R.ok();
    }
    /**
     * 更新字段定义
     * @param collection
     * @param fieldName
     * @param schemaColumn
     * @return
     */
    public R updateSchemaColumn(@PathVariable String collection,@PathVariable String fieldName, @RequestBody SchemaColumnBo schemaColumn) {
        Query query = new Query();
        query.addCriteria(
                Criteria.where("collection").is(collection)
                        .and("columns.fieldName").is(fieldName)
        );

        Update update = new Update();
        update.set("columns.$.label", schemaColumn.getLabel());
//        update.set("columns.$.fieldName", schemaColumn.getFieldName());
        update.set("columns.$.component", schemaColumn.getComponent());
        update.set("columns.$.required", schemaColumn.isRequired());
        update.set("columns.$.dataType", schemaColumn.getDataType());
        update.set("columns.$.order", schemaColumn.getOrder());

        toGameConfigMongoTemplate.updateFirst(query, update, COLLECTION_NAME);
        return R.ok();
    }
    /**
     * 删除字段定义
     * @param collection
     * @param fieldNames
     * @return
     */
    public R deleteSchemaColumn(@PathVariable String collection,@PathVariable String[] fieldNames) {
        Query query = Query.query(Criteria.where("collection").is(collection));
        // 使用预定义常量替换硬编码字符串，提高可读性和可维护性
        Document fieldCriteria = new Document("fieldName", new Document("$in", Arrays.asList(fieldNames)));
        Document pullOperation = new Document("$pull", new Document("columns", fieldCriteria));

        Update update = Update.fromDocument(pullOperation);
        toGameConfigMongoTemplate.updateFirst(query,update, COLLECTION_NAME);

        return R.ok();
    }

    /**
     * 获取某个配置表的 schema
     * @param collection
     * @return
     */
    public R<Map<String, Object>> getSchema(@PathVariable String collection) {
        Query query = Query.query(Criteria.where("collection").is(collection));
        return R.ok(toGameConfigMongoTemplate.findOne(query, Map.class, COLLECTION_NAME));
    }

    /**
     * 获取该表数据列表
     * @param collection
     * @return
     */
    @GetMapping("/list/{collection}")
    public R<List<Map>> list(@PathVariable String collection) {
        return R.ok(toGameConfigMongoTemplate.findAll(Map.class, collection));
    }

    /**
     * 保存
     * @param collection
     * @param data
     * @return
     */
    public R save(@PathVariable String collection, @RequestBody Map<String, Object> data) {
        toGameConfigMongoTemplate.save(data, collection);
        return R.ok();
    }

    /**
     * 删除
     * @param collection
     * @param ids
     * @return
     */
    public R delete(@PathVariable String collection, @RequestBody List<String> ids) {
        ids.forEach(id -> toGameConfigMongoTemplate.remove(Query.query(Criteria.where("_id").is(id)), collection));
        return R.ok();
    }
}
