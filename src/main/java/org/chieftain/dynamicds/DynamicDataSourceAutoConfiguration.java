package org.chieftain.dynamicds;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.baomidou.mybatisplus.spring.boot.starter.MybatisPlusAutoConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.chieftain.dynamicds.aop.DynamicDataSourceAspect;
import org.chieftain.dynamicds.druidconfiguration.DruidDataSourceConfiguration;
import org.chieftain.dynamicds.druidconfiguration.DruidDataSourceConfigurationSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ConditionalOnClass({DataSource.class, SpringBootApplication.class})
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@EnableConfigurationProperties({DruidDataSourceConfigurationSet.class})
@ConditionalOnProperty(prefix = "dbconfig.druidconfig", name = "enable", havingValue = "true")
public class DynamicDataSourceAutoConfiguration {

    @Autowired
    private MybatisPlusAutoConfiguration mybatisPlusAutoConfiguration;

    @Autowired
    private DruidDataSourceConfigurationSet druidDataSourceConfigurationSet;

    @Bean(name = "druidDataSourceMap")
    @ConditionalOnMissingBean(name = {"druidDataSourceMap"})
    public Map<String, DataSource> druidDatasourceMap () throws Exception {
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        for (DruidDataSourceConfiguration druidDataSourceConfiguration : druidDataSourceConfigurationSet.getDynamicDataSource()) {
            dataSourceMap.put(druidDataSourceConfiguration.getKey(), DruidDataSourceFactory.createDataSource(druidDataSourceConfiguration.convertProperties()));
        }
        return dataSourceMap;
    }

    @Bean(name = "dataSourceKeys")
    @ConditionalOnMissingBean(name = {"dataSourceKeys"})
    @DependsOn("druidDataSourceMap")
    public List<String> dataSourceKeys (@Qualifier("druidDataSourceMap") Map<String, DataSource> dataSourceMap) {
        return new ArrayList<>(dataSourceMap.keySet());
    }

    @Bean(name = "dynamicDataSource")
    @ConditionalOnMissingBean(name = {"dynamicDataSource"})
    @DependsOn("druidDataSourceMap")
    public DynamicDataSourceSelector dynamicDataSource(@Qualifier("druidDataSourceMap") Map<String, DataSource> dataSourceMap) {

        //这个地方是比较核心的targetDataSource 集合是我们数据库和名字之间的映射
        Map<Object, Object> targetDataSource = new HashMap<>(dataSourceMap);
        DynamicDataSourceSelector dataSource = new DynamicDataSourceSelector();
        dataSource.setTargetDataSources(targetDataSource);
        dataSource.setDefaultTargetDataSource(StringUtils.isBlank(druidDataSourceConfigurationSet.getMasterKey()) ? dataSourceMap.get(DruidDataSourceConfigurationSet.DEFAULT_KEY) : dataSourceMap.get(druidDataSourceConfigurationSet.getMasterKey()));
        return dataSource;
    }

    /**
     * 为mybatisplus注入数据源
     * @param dynamicDataSource
     * @return
     * @throws Exception
     */
    @Bean(name = "sqlSessionFactory")
    @ConditionalOnMissingBean(name = {"sqlSessionFactory"})
    @DependsOn("dynamicDataSource")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dynamicDataSource") DataSource dynamicDataSource) throws Exception {
        return mybatisPlusAutoConfiguration.sqlSessionFactory(dynamicDataSource);
    }

    @Bean(name = "dynamicDataSourceAspect")
    @ConditionalOnMissingBean(name = {"dynamicDataSourceAspect"})
    @DependsOn("dataSourceKeys")
    public DynamicDataSourceAspect registerDynamicDataSourceAspect (@Qualifier("dataSourceKeys") List<String> dataSourceKeys) {
        return new DynamicDataSourceAspect(dataSourceKeys);
    }

    @Bean(name = "txManager")
    @ConditionalOnMissingBean(name = {"txManager"})
    @DependsOn("dynamicDataSource")
    public DataSourceTransactionManager txManager (@Qualifier("dynamicDataSource") DataSource dynamicDataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dynamicDataSource);
        return transactionManager;
    }
}