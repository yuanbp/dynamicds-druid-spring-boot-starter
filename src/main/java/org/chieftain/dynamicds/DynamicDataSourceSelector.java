package org.chieftain.dynamicds;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 扩展Spring的AbstractRoutingDataSource抽象类，实现动态数据源。
 * AbstractRoutingDataSource中的抽象方法determineCurrentLookupKey是实现数据源的route的核心，
 * 这里对该方法进行Override。 【上下文DbContextHolder为一线程安全的ThreadLocal】
 *
 * @author chieftain
 * @date 2019-09-27 21:47
 *
 */
public class DynamicDataSourceSelector extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceKey.getDataSourceKey();
    }
}
