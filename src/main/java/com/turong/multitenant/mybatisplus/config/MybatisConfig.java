package com.turong.multitenant.mybatisplus.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import lombok.extern.log4j.Log4j2;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Configuration
@MapperScan("com.turong.multitenant.mybatisplus.mapper")
@Log4j2
public class MybatisConfig {

    private static final String TENANT_COLUMN = "tenant_id";

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                String currentTenant = AppContextHolder.getTenant();
                log.info("Current tenant ={}", currentTenant);
                if (isBlank(currentTenant)) {
                    return new StringValue("");
                }
                return new StringValue(currentTenant);
            }

            @Override
            public boolean ignoreTable(String tableName) {
                return false;
            }

            @Override
            public String getTenantIdColumn() {
                return TENANT_COLUMN;
            }

        }));
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        final Map<String, TableNameHandler> tableNameHandlers = new HashMap<>();
        tableNameHandlers.put("users", new TableNameHandler() {
            @Override
            public String dynamicTableName(String sql, String tableName) {
                String tenant = AppContextHolder.getTenant();
                if (isBlank(tenant)) {
                    return tableName;
                }
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                return StringUtils.join(Arrays.asList(tableName, tenant, currentYear), "_");
            }
        });
        DynamicTableNameInnerInterceptor tableNameInterceptor = new DynamicTableNameInnerInterceptor();
        tableNameInterceptor.setTableNameHandlerMap(tableNameHandlers);
        interceptor.addInnerInterceptor(tableNameInterceptor);
        return interceptor;
    }

    @Bean
    public MybatisConfiguration mybatisConfiguration() {
        MybatisConfiguration configuration = new MybatisConfiguration();

        Properties properties = configuration.getVariables();
        if (Objects.isNull(properties)) {
            properties = new Properties();
        }
        log.info("Current Ibatis configuration={}", properties);
        properties.put("useDeprecatedExecutor", Boolean.FALSE);
        configuration.setVariables(properties);

        return configuration;
    }

}
