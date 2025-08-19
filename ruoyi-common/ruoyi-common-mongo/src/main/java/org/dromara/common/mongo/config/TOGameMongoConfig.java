package org.dromara.common.mongo.config;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = "org.dromara.common.mongo.repository.toGame",
        mongoTemplateRef = "toGameMongoTemplate"
)
public class TOGameMongoConfig {
    @Value("${spring.data.mongodb.TOGame.uri}")
    private String toGameUri;
    @Bean
    @Primary
    public MongoDatabaseFactory toGameDbFactory() {
        return new SimpleMongoClientDatabaseFactory(toGameMongoClient(), "TOGame");
    }

    @Bean("toGameMongoTemplate")

    @Primary
    public MongoTemplate toGameMongoTemplate() {
        return new MongoTemplate(toGameDbFactory());
    }

    @Bean
    public MongoClient toGameMongoClient() {
        return MongoClients.create(toGameUri);
    }
}
