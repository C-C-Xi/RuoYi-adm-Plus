package org.dromara.game.config.service;

import lombok.RequiredArgsConstructor;
import org.dromara.common.mongo.model.backend.SchemaHistory;
import org.dromara.common.mongo.model.toGameConfig.ConfigSchema;
import org.dromara.common.mongo.repository.ConfigSchemaRepository;
import org.dromara.common.mongo.repository.SchemaHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfigSchemaService {
    private final ConfigSchemaRepository schemaRepo;
    private final SchemaHistoryRepository historyRepo;

    public ConfigSchema createSchema(ConfigSchema dto, String operator) {
        dto.setVersion(1);
        dto.setCreatedAt(new Date());
        dto.setUpdatedAt(new Date());
        dto.setCreatedBy(operator);
        dto.setUpdatedBy(operator);
        ConfigSchema saved = schemaRepo.save(dto);

        // Save Schema History
        SchemaHistory history = new SchemaHistory();
        history.setSchemaId(saved.getId());
        history.setTableName(saved.getTableName());
        history.setVersion(saved.getVersion());
        history.setSnapshot(saved);
        history.setOperator(operator);
        history.setOpAt(new Date());
        history.setOpType("CREATE");
        historyRepo.save(history);

        return saved;
    }

    public List<SchemaHistory> getSchemaHistory(String tableName) {
        return historyRepo.findBySchemaIdOrderByVersionDesc(tableName);
    }
}