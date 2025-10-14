package org.dromara.game.config.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.game.config.domain.bo.SchemaBo;
import org.dromara.game.config.domain.bo.SchemaColumnBo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ConfigSchemaServiceImpl implements ConfigSchemaService {
    private static final String COLLECTION_NAME = "config_schema";
    private static final String URL_ID_COLLECTION_NAME = "Config_PureH5_AccountUrlConfig";
    @Autowired
    @Qualifier("toGameConfigMongoTemplate")
    private MongoTemplate toGameConfigMongoTemplate;

    @Override
    public List<Map> getSchemaItems(String tableName, PageQuery pageQuery) {
        Query query = new Query();
//        if(bo.getCollection() != null){
//            query.addCriteria(Criteria.where("collection").is(bo.getCollection()));
//        }
        query.skip((pageQuery.getPageNum() - 1) * pageQuery.getPageSize());
        query.limit(pageQuery.getPageSize());
        List<Map> tablelogs = toGameConfigMongoTemplate.find(query, Map.class, tableName);
        return tablelogs;
    }

    @Override
    public Map getSchemaItemInfo(String tableName,int urlId,int id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("Id").is(id));
        Query tableQuery = Query.query(Criteria.where("collection").is(tableName));
        tableQuery.addCriteria(Criteria.where("columns.UrlId").exists(true));
        if(toGameConfigMongoTemplate.count(tableQuery, Map.class, COLLECTION_NAME)>0){
            query.addCriteria(Criteria.where("UrlId").is(urlId));
        }
        return toGameConfigMongoTemplate.findOne(query, Map.class, tableName);
    }

    @Override
    public void addSchemazItem(String tableName, Map<String, Object> data) {
        SchemaBo schemaBo = toGameConfigMongoTemplate.findOne(Query.query(Criteria.where("collection").is(tableName)),
                SchemaBo.class, COLLECTION_NAME);
        Map<String, Object> result = this.getFormatSchemaItem(tableName, data, schemaBo);
        Query query = new Query();
        String[] conditions = schemaBo.getPrimaryKey().split(",");
        for (String condition : conditions) {
            if (data.containsKey(condition)) {
                query.addCriteria(Criteria.where(condition).is(data.get(condition)));
            }
        }
        if (toGameConfigMongoTemplate.exists(query, tableName)) {
            throw new RuntimeException("数据已存在");
        }
        log.info("插入数据：" + JsonUtils.toJsonString(result));
        log.info(tableName);
       Object object= toGameConfigMongoTemplate.insert(result, tableName);
        log.info("插入数据：" + JsonUtils.toJsonString(object));

    }

    @Override
    public List<Map<String, Object>> getUrls() {
        // 构建聚合管道
        List<AggregationOperation> pipeline = new ArrayList<>();

        // $group阶段 - 按UrlId分组，取每组的第一个文档
        pipeline.add(Aggregation.group("UrlId").first("$$ROOT").as("doc"));

        // $replaceRoot阶段 - 将doc字段替换为根文档
        pipeline.add(Aggregation.replaceRoot().withValueOf("$doc"));

        // $sort阶段 - 按UrlId升序排序
        pipeline.add(Aggregation.sort(org.springframework.data.domain.Sort.Direction.ASC, "UrlId"));

        // 创建聚合
        Aggregation aggregation = Aggregation.newAggregation(pipeline);

        // 执行聚合查询，需要替换"collectionName"为实际的集合名称
        AggregationResults<Map> results = toGameConfigMongoTemplate.aggregate(
                aggregation,
                URL_ID_COLLECTION_NAME, // 替换为实际的集合名称
                Map.class
        );

        List<Map<String, Object>> data = new ArrayList<>();
        for (Map doc : results) {
            data.add(doc);
        }
//        log.info(JsonUtils.toJsonString(data));
        // 处理查询结果
        for (Map<String, Object> ele : data) {
            try {
                Object directUrlObj = ele.get("DirectUrl");
                if (directUrlObj instanceof String) {
                    String directUrl = (String) directUrlObj;
                    URL url = new URL(directUrl);
                    String hostname = url.getHost();
                    String[] parts = hostname.split("\\.");
                    if (parts.length >= 2) {
                        String subdomain = parts[1];
                        ele.put("label", subdomain);
                    }
                    ele.put("value", ele.get("UrlId"));
                }
            } catch (Exception error) {
                // URL解析异常处理
                // 可以添加日志记录或其他错误处理逻辑
            }
        }

        return data;
    }

    @Override
    public List<SchemaColumnBo> getSchemaColumns(String tableName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("collection").is(tableName));
        SchemaBo result = toGameConfigMongoTemplate.findOne(query, SchemaBo.class, COLLECTION_NAME);
        return result.getColumns();
    }

    @Override
    public void updateSchemaItem(String tableName, Map<String, Object> data) {

    }


    private Map<String, Object> getFormatSchemaItem(String tableName, Map<String, Object> data, SchemaBo schemaBo) {

        if (schemaBo != null) {
            List<SchemaColumnBo> schemaColumnBos = schemaBo.getColumns();
            for (SchemaColumnBo schemaColumnBo : schemaColumnBos) {
                if (schemaColumnBo.getFieldName().equals(data.get(schemaColumnBo.getFieldName()))) {
                    Object value = data.get(schemaColumnBo.getFieldName());
                    switch (schemaColumnBo.getDataType()) {
                        case "int":
                            data.put(schemaColumnBo.getFieldName(), Integer.valueOf(value.toString()));
                            break;
                        case "string":
                            data.put(schemaColumnBo.getFieldName(), value.toString());
                            break;
                        case "boolean":
                            data.put(schemaColumnBo.getFieldName(), Boolean.valueOf(value.toString()));
                            break;
                        case "long":
                            data.put(schemaColumnBo.getFieldName(), Long.valueOf(value.toString()));
                            break;
                    }

                }
            }
        }
        ;
        return data;
    }
}
