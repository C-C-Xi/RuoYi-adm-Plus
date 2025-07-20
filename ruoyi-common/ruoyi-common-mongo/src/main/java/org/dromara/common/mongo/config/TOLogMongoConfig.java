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

@Configuration
public class TOLogMongoConfig {
    @Value("${spring.data.mongodb.TOLog.uri}")
    private String toLogUri;
    @Bean
    public MongoDatabaseFactory toLogDbFactory() {
        return new SimpleMongoClientDatabaseFactory(toLogMongoClient(), "TOLog");
    }

    @Bean(name="toLogMongoTemplate")
    public MongoTemplate toLogMongoTemplate() {
        return new MongoTemplate(toLogDbFactory());
    }

    @Bean
    public MongoClient toLogMongoClient() {
        return MongoClients.create(toLogUri);
    }
}
