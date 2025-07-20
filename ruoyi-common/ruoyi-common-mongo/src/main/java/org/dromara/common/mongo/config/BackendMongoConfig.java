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
public class BackendMongoConfig {
    @Value("${spring.data.mongodb.Backend.uri}")
    private String backendUri;
    @Bean
    public MongoDatabaseFactory backendDbFactory() {
        return new SimpleMongoClientDatabaseFactory(backendMongoClient(), "Backend");
    }

    @Bean("backendMongoTemplate")
    public MongoTemplate backendMongoTemplate() {
        return new MongoTemplate(backendDbFactory());
    }

    @Bean
    public MongoClient backendMongoClient() {
        return MongoClients.create(backendUri);
    }
}
