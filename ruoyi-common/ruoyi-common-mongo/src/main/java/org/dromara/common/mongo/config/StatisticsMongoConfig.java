package org.dromara.common.mongo.config;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.List;

@Configuration
@EnableMongoRepositories(
        basePackages = "org.dromara.common.mongo.repository.statitics",
        mongoTemplateRef = "toGameMongoTemplate"
)
public class StatisticsMongoConfig {
    @Value("${spring.data.mongodb.Statistics.uri}")
    private String statisticsUri;
    @Bean
    public MongoDatabaseFactory statisticsDbFactory() {
        return new SimpleMongoClientDatabaseFactory(statisticsMongoClient(), "Statistics");
    }

    @Bean("statisticsMongoTemplate")
    public MongoTemplate statisticsMongoTemplate() {
        MongoTemplate mongoTemplate =new MongoTemplate(statisticsDbFactory());
        return mongoTemplate;
    }

    @Bean
    public MongoClient statisticsMongoClient() {
        return MongoClients.create(statisticsUri);
    }
}
