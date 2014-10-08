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
package com.baidu.cc.spring;

import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * Extractor property from local {@link PropertyPlaceholderConfigurer} to 
 * configuration center server
 * 
 * @author xiemalin
 * @since 1.0.1.0
 */
public class ConfigCenterPropertyExtractor implements InitializingBean {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger
            .getLogger(ConfigCenterPropertyExtractor.class);
    
    /**
     * Configuration items importer.
     */
    private ConfigCenterPropertyPlaceholderConfigurer importer;
    
    /**
     * set {@link ConfigCenterPropertyPlaceholderConfigurer} instance
     * @param importer the importer to set
     */
    public void setImporter(ConfigCenterPropertyPlaceholderConfigurer importer) {
        this.importer = importer;
    }
    
    /**
     * extractor is to get configuration items to import.
     */
    private PropertyPlaceholderConfigurer extractor;

    /**
     * set PropertyPlaceholderConfigurer instance
     * @param extractor the extractor to set
     */
    public void setExtractor(PropertyPlaceholderConfigurer extractor) {
        this.extractor = extractor;
    }

    /**
     * Request configuration version
     */
    private long ccVersion;
    
    /**
     * set version
     * @param ccVersion the ccVersion to set
     */
    public void setCcVersion(long ccVersion) {
        this.ccVersion = ccVersion;
    }

    /**
     * To initialize properties import action from extractor to target configuration
     * center server. importer and extractor should not be null.
     * @throws Exception to throw out all exception to spring container.
     */
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(importer, "'importer property is null.'");
        Assert.notNull(extractor, "'extractor property is null.'");
        
        //get configuration items
        Method m = ReflectionUtils.findMethod(PropertyPlaceholderConfigurer.class,
                "mergeProperties");
        if (m != null) {
            m.setAccessible(true);
            Properties properties = (Properties) ReflectionUtils.invokeMethod(m, extractor);
            if (properties == null) {
                LOGGER.warn("Target extractor get a null property, import will ignore.");
                return;
            }
            
            Map copied = new HashMap(properties);
            importer.getConfigLoader().importConfigItems(ccVersion, copied);
        }
    }
}
