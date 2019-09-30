package org.chieftain.dynamicds.druidconfiguration;

import lombok.Data;
import org.chieftain.dynamicds.utils.ReflectUtils;

import java.util.Properties;

/**
 * @author chieftain
 * @date 2019-09-28 16:06
 */
@Data
public class DruidDataSourceConfiguration {
    private String key;

    private String defaultAutoCommit;
    private String defaultReadOnly;
    private String defaultTransactionIsolation;
    private String defaultCatalog;
    private String driverClassName;
    private String maxActive;
    private String maxIdle;
    private String minIdle;
    private String initialSize;
    private String maxWait;
    private String testOnBorrow;
    private String testOnReturn;
    private String timeBetweenEvictionRunsMillis;
    private String numTestsPerEvictionRun;
    private String minEvictableIdleTimeMillis;
    private String phyTimeoutMillis;
    private String testWhileIdle;
    private String password;
    private String url;
    private String username;
    private String validationQuery;
    private String validationQueryTimeout;
    private String accessToUnderlyingConnectionAllowed;
    private String removeAbandoned;
    private String removeAbandonedTimeout;
    private String logAbandoned;
    private String poolPreparedStatements;
    private String filters;
    private String exceptionSorter;
    //exception-sorter-class-name
    private String exceptionSorterClassName;
    private String initConnectionSqls;
    private String connectionProperties;
    private String init;

    public Properties convertProperties () {
        Properties properties = ReflectUtils.convertProperties(this,"key");
        assert properties != null;
        if (properties.containsKey("exceptionSorterClassName")) {
            properties.setProperty("exception-sorter-class-name", properties.getProperty("url"));
        }
        return properties;
    }
}
