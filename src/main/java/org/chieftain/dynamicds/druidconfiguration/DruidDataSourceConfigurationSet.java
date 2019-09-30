package org.chieftain.dynamicds.druidconfiguration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chieftain
 * @date 2019-09-28 16:16
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.druidconfig")
public class DruidDataSourceConfigurationSet {

    public static final String DEFAULT_KEY = "master";

    private String enable;

    private String masterKey;

    private List<DruidDataSourceConfiguration> dynamicDataSource = new ArrayList<>();
}
