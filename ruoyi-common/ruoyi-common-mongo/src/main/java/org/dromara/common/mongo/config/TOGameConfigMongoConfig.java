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
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = "org.dromara.common.mongo.repository.toGameConfig",
        mongoTemplateRef = "toGameConfigMongoTemplate"
)
public class TOGameConfigMongoConfig {
    @Value("${spring.data.mongodb.TOGameConfig.uri}")
    private String toGameConfigUri;
    @Bean
    public MongoDatabaseFactory toGameConfigDbFactory() {
        return new SimpleMongoClientDatabaseFactory(toGameConfigMongoClient(), "TOGameConfig");
    }

    @Bean("toGameConfigMongoTemplate")
    public MongoTemplate toGameConfigMongoTemplate( ) {
        return new MongoTemplate(toGameConfigDbFactory());
    }

    @Bean
    public MongoClient toGameConfigMongoClient() {
        return MongoClients.create(toGameConfigUri);
    }
}
