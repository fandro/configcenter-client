/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baidu.cc.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import com.baidu.bjf.beans.context.annotation.AbstractAnnotationParserCallback;
import com.baidu.cc.BatchConfigItemChangedCallable;
import com.baidu.cc.FilterableConfigItemChangedCallable;
import com.baidu.cc.interfaces.ConfigChangeManager;
import com.baidu.cc.interfaces.ConfigItemChangedCallable;
import com.baidu.cc.spring.ConfigCenterPropertyPlaceholderConfigurer;

/**
 * Annotation {@link ConfigChangeNotifer} parser.
 *
 * @author xiemalin
 * @since 1.0.0.0
 */
public class ConfigChangeNotiferAnnotationParser extends
        AbstractAnnotationParserCallback implements BeanNameAware {
    
    /**
     * Main {@link ConfigItemChangedCallable} entry for all @ConfigChangeNotifer marked classes.
     */
    private BatchConfigItemChangedCallable batchCallbable = new BatchConfigItemChangedCallable();
    /**
     * mark if add BatchConfigItemChangedCallable to listener
     */
    private boolean added;
    /**
     * cuurent bean name
     */
    private String beanName;
    

    /**
     * process all annotation on class type.
     * 
     * @param t annotation instance.
     * @param bean target bean
     * @param beanName target bean name
     * @param beanFactory spring bean factory
     * @return wrapped bean
     * @throws BeansException exceptions on spring beans create error.
     */
    public Object annotationAtType(Annotation t, Object bean, String beanName,
            ConfigurableListableBeanFactory beanFactory) throws BeansException {
        return null;
    }

    /**
     * process all annotation on class type after spring containter started
     * 
     * @param t annotation instance.
     * @param bean target bean
     * @param beanName target bean name
     * @param beanFactory spring bean factory
     * @throws BeansException exceptions on spring beans create error.
     */
    public void annotationAtTypeAfterStarted(Annotation t, Object bean,
            String beanName, ConfigurableListableBeanFactory beanFactory)
        throws BeansException {
    }

    /**
     * process all annotation on class field.
     * 
     * @param t annotation instance.
     * @param bean target bean
     * @param beanName target bean name
     * @param pvs bean property values
     * @param beanFactory spring bean factory
     * @param field field instance
     * @return field value
     * @throws BeansException exceptions on spring beans create error.
     */
    public Object annotationAtField(Annotation t, Object bean, String beanName,
            PropertyValues pvs, ConfigurableListableBeanFactory beanFactory,
            Field field) throws BeansException {
        return null;
    }

    /**
     * process all annotation on class method.
     * 
     * @param t annotation instance.
     * @param bean target bean
     * @param beanName target bean name
     * @param pvs bean property values
     * @param beanFactory spring bean factory
     * @param method method instance
     * @return method invoke parameter
     * @throws BeansException exceptions on spring beans create error.
     */
    public Object annotationAtMethod(Annotation t, Object bean,
            String beanName, PropertyValues pvs,
            ConfigurableListableBeanFactory beanFactory, Method method)
        throws BeansException {
        if (t instanceof ConfigChangeNotifer) {
            ConfigChangeNotifer configChangeNotifer = 
                (ConfigChangeNotifer) t;
            
            FilterableConfigItemChangedCallable callable;
            callable = new FilterableConfigItemChangedCallable(method, 
                    bean, configChangeNotifer.keys());
            
            batchCallbable.addConfigItemChangedCallable(beanName, callable);
            
            addToPropertyPlaceholderConfigurer(beanFactory);
        }
        
        return null;
    }
    
    /**
     * add {@link ConfigItemChangedCallable} call back entry to {@link ConfigCenterPropertyPlaceholderConfigurer}
     * 
     * @param beanFactory spring bean factory
     */
    private void addToPropertyPlaceholderConfigurer(ConfigurableListableBeanFactory beanFactory) {
        //if already added then ignore
        if (added) {
            return;
        }
        
        //add to ConfigCenterPropertyPlaceholderConfigurer
        Map beansMap = beanFactory.getBeansOfType(ConfigChangeManager.class);
        if (beansMap != null) {
            Collection<?> beans = beansMap.values();
            ConfigChangeManager parser;
            for (Object object : beans) {
                parser = (ConfigChangeManager) object; 
                //configloader property will not be null
                parser.addConfigItemChangeCallable(beanName, batchCallbable);
            }
        }
        added = true;
    }

    /**
     * get annotation type on class type
     * 
     * @return annotation type on class type
     */
    public Class<? extends Annotation> getTypeAnnotation() {
        return null;
    }

    /**
     * get annotation type on class field or method
     * 
     * @return annotation type on class field or method
     */
    public Class<? extends Annotation> getMethodFieldAnnotation() {
        return ConfigChangeNotifer.class;
    }

    /**
     * do destroy action on spring container close.
     * @throws Exception throw any excpetion
     */
    public void destroy() throws Exception {
        batchCallbable.clear();
    }
    /**
     * set bean name back
     * @param beanName bean name
     */
    public void setBeanName(String beanName) {
        this.beanName = beanName;
        
    }

}
