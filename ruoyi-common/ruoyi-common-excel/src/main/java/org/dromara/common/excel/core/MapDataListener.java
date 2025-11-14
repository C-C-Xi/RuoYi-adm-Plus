package org.dromara.common.excel.core;

import cn.hutool.core.util.StrUtil;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import cn.idev.excel.exception.ExcelAnalysisException;
import cn.idev.excel.exception.ExcelDataConvertException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.StreamUtils;
import org.dromara.common.core.utils.ValidatorUtils;
import org.dromara.common.json.utils.JsonUtils;

import java.util.*;
import java.util.function.Consumer;

/**
 * Excel 导入监听
 *
 * @author Yjoioooo
 * @author Lion Li
 */
@Slf4j
public class MapDataListener extends AnalysisEventListener<Map<String, String>> implements ExcelListener<Map<String, String>> {
    private List<Map<String, String>> dataList;
    private List<String> headerNames;


    /**
     * 导入回执
     */
    private ExcelResult<Map<String, String>> excelResult;
    public MapDataListener() {
        this.excelResult = new DefaultExcelResult<>();
        this.dataList = new ArrayList<Map<String, String>>();
    }

    @Override
    public void invoke(Map<String, String> data, AnalysisContext context) {
        if (headerNames != null) {
            Map<String, String> row = new HashMap<>();
            for (int i = 0; i < headerNames.size(); i++) {
                row.put(headerNames.get(i), data.get(i));
            }
//            dataList.add(row);
            excelResult.getList().add(row);

        }
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        headerNames = new ArrayList<>(headMap.values());
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
    }


    @Override
    public ExcelResult<Map<String, String>> getExcelResult() {
        return excelResult;
    }
}

