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
        basePackages = "org.dromara.common.mongo.repository.toAD",
        mongoTemplateRef = "toGameMongoTemplate"
)
public class TOADMongoConfig {
    @Value("${spring.data.mongodb.TOAD.uri}")
    private String toAdUri;
    @Bean
    public MongoDatabaseFactory toAdDbFactory() {
        return new SimpleMongoClientDatabaseFactory(toAdMongoClient(), "TOAD");
    }

    @Bean("toAdMongoTemplate")
    public MongoTemplate toAdMongoTemplate() {
        return new MongoTemplate(toAdDbFactory());
    }

    @Bean
    public MongoClient toAdMongoClient() {
        System.out.println("toAdUri:" + toAdUri);
        return MongoClients.create(toAdUri);
    }
}
