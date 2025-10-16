package org.dromara.game.config.domain;

import lombok.Data;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Query;

@Data
public class ConfigUpsertBO {
    private Query query;
    private Document document;

    public ConfigUpsertBO() {
    }
    public ConfigUpsertBO(Query query, Document document) {
        this.query = query;
        this.document = document;
    }
}
