package com.turong.multitenant.mybatisplus.config;

import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class DynamicTableInterceptor extends DynamicTableNameInnerInterceptor {

    @Override
    public void setTableNameHandlerMap(Map<String, TableNameHandler> tableNameHandlerMap) {

        TableNameHandler tableNameHandler = new TableNameHandler() {
            @Override
            public String dynamicTableName(String sql, String tableName) {
                String tenant = AppContextHolder.getTenant();
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                return StringUtils.join(Arrays.asList(tableName, tenant, currentYear), "_");
            }
        };
        final Map<String, TableNameHandler> handlers = tableNameHandlerMap.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.setValue(tableNameHandler)));
        super.setTableNameHandlerMap(handlers);
    }


}
