package org.chieftain.dynamicds.annotation;

import org.chieftain.dynamicds.druidconfiguration.DruidDataSourceConfigurationSet;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {

    //该值即key值，默认使用默认数据库
    String value() default DruidDataSourceConfigurationSet.DEFAULT_KEY;
}