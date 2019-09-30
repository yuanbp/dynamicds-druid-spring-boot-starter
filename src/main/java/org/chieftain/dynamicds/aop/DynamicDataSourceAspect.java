package org.chieftain.dynamicds.aop;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.chieftain.dynamicds.DynamicDataSourceKey;
import org.chieftain.dynamicds.annotation.DataSource;
import org.chieftain.dynamicds.druidconfiguration.DruidDataSourceConfigurationSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
@Order(0)
@NoArgsConstructor
@AllArgsConstructor
public class DynamicDataSourceAspect {

    @Autowired
    List<String> dataSourceKeys;

    /**
     * 拦截自定义注解
     * @param point
     * @param dataSource
     * @throws Throwable
     */
    @Before("@annotation(dataSource)")
    public void changeDataSource(JoinPoint point, DataSource dataSource) throws Throwable {
        String value = dataSource.value();
        if (dataSourceKeys.isEmpty()) {
            DynamicDataSourceKey.setDataSourceKey(DruidDataSourceConfigurationSet.DEFAULT_KEY);
        }
        if (dataSourceKeys.contains(value)) {
            DynamicDataSourceKey.setDataSourceKey(value);
            return;
        }
        DynamicDataSourceKey.setDataSourceKey(DruidDataSourceConfigurationSet.DEFAULT_KEY);
    }

    /**
     *  清除数据源的配置
     * @param point
     * @param dataSource
     */
    @After("@annotation(dataSource)")
    public void restoreDataSource(JoinPoint point, DataSource dataSource) {
        DynamicDataSourceKey.clearDataSourceKey();
    }
}