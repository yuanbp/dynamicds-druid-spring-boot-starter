package org.chieftain.dynamicds;

import org.apache.commons.lang3.StringUtils;
import org.chieftain.dynamicds.druidconfiguration.DruidDataSourceConfigurationSet;

/**
 * @author chieftain
 * @date 2019-09-27 21:54
 */
public class DynamicDataSourceKey {

    // 使用ThreadLocal保证线程安全
    private static final ThreadLocal<String> DATASOURCEKEY = new ThreadLocal<>();

    // 往当前线程里设置数据源类型
    public static void setDataSourceKey (String dataSourceKey) {
        if (StringUtils.isBlank(dataSourceKey)) {
            throw new NullPointerException();
        }
        DATASOURCEKEY.set(dataSourceKey);
    }

    // 获取数据源类型
    public static String getDataSourceKey () {
        return DATASOURCEKEY.get() == null ? DruidDataSourceConfigurationSet.DEFAULT_KEY : DATASOURCEKEY.get();
    }

    // 清空数据类型
    public static void clearDataSourceKey () {
        DATASOURCEKEY.remove();
    }
}
