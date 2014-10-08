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

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.ClassPathResource;

import com.baidu.cc.interfaces.ExtConfigServerService;

/**
 * Test class for {@link ConfigCenterPropertyExtractor}
 * 
 * 
 * @author xiemalin
 * @since 1.0.1.0
 */
public class ConfigCenterPropertyExtractorTest {
    
    /**
     * test property extractor
     * this test method is ignored due to local environment required.
     * 
     * @throws Exception throw out all exception to break unit test and mark failed.
     */
    @Ignore
    @Test
    public void testPropertyExtractorNoMock() throws Exception {
        PropertyPlaceholderConfigurer extractor = new PropertyPlaceholderConfigurer();
        ClassPathResource cpr = new ClassPathResource("/com/baidu/cc/spring/test.properties");
        extractor.setLocation(cpr);
        
        ConfigCenterPropertyPlaceholderConfigurer importer;
        importer = new ConfigCenterPropertyPlaceholderConfigurer();
        
        importer.setCcUser("test1");
        importer.setCcPassword("123");
        importer.setCcVersion(586);
        importer.setCcServerUrl("http://localhost:8080/rpc/ExtConfigServerService");
        importer.setReadTimeout(5000);
        
        //initial importer
        ConfigCenterPropertyPlaceholderConfigurerTest test = new ConfigCenterPropertyPlaceholderConfigurerTest();
        importer.setApplicationContext(test.appContext);
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        importer.postProcessBeanFactory(beanFactory);
        
        
        ConfigCenterPropertyExtractor ccpe = new ConfigCenterPropertyExtractor();
        ccpe.setCcVersion(586);
        ccpe.setExtractor(extractor);
        ccpe.setImporter(importer);
        
        
        ccpe.afterPropertiesSet();
    }
    
    
    /**
     * test property extractor
     * @throws Exception 
     */
    @Test
    public void testPropertyExtractor() throws Exception {
        
        ExtConfigServerService service = new ExtConfigServerService() {
            
            /* (non-Javadoc)
             * @see com.baidu.cc.interfaces.ConfigServerService#getLastestConfigVersion(java.lang.String, java.lang.String, java.lang.Long)
             */
            public Long getLastestConfigVersion(String user, String password, Long envId) {
                return null;
            }
            
            /* (non-Javadoc)
             * @see com.baidu.cc.interfaces.ConfigServerService#getLastestConfigItems(java.lang.String, java.lang.String, java.lang.Long)
             */
            public Map<String, String> getLastestConfigItems(String user,
                    String password, Long envId) {
                return null;
            }
            
            /* (non-Javadoc)
             * @see com.baidu.cc.interfaces.ConfigServerService#getConfigItems(java.lang.String, java.lang.String, java.lang.Long)
             */
            public Map<String, String> getConfigItems(String user, String password,
                    Long version) {
                Map<String, String> ret = new HashMap<String, String>();
                ret.put("new", "value");
                return ret;
            }
            
            /* (non-Javadoc)
             * @see com.baidu.cc.interfaces.ConfigServerService#getConfigItemValue(java.lang.String, java.lang.String, java.lang.Long, java.lang.String)
             */
            public String getConfigItemValue(String user, String password,
                    Long version, String key) {
                return null;
            }
            
            /* (non-Javadoc)
             * @see com.baidu.cc.interfaces.ConfigServerService#checkVersionTag(java.lang.String, java.lang.String, java.lang.Long, java.lang.String)
             */
            public boolean checkVersionTag(String user, String password, Long version,
                    String tag) {
                return false;
            }
            
            /* (non-Javadoc)
             * @see com.baidu.cc.interfaces.ExtConfigServerService#importConfigItems(java.lang.String, java.lang.String, java.lang.Long, java.util.Map)
             */
            public void importConfigItems(String user, String password, Long version,
                    Map<String, String> configItems) {
                Assert.assertEquals("xie", user);
                
                StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
                encryptor.setPassword(ExtConfigServerService.class.getSimpleName());
                Assert.assertEquals("matthew", encryptor.decrypt(password));
                
                
                Assert.assertEquals(2, version.longValue());
                Assert.assertTrue(configItems.containsKey("name"));
                Assert.assertTrue(configItems.containsKey("age"));
                Assert.assertTrue(configItems.containsValue("100"));
                
                Assert.assertEquals(3, configItems.size());
            }

            /* (non-Javadoc)
             * @see com.baidu.cc.interfaces.ConfigServerService#getLastestConfigVersion(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
             */
            public Long getLastestConfigVersion(String user, String password,
                    String projectName, String envName) {
                
                return null;
            }

            /* (non-Javadoc)
             * @see com.baidu.cc.interfaces.ConfigServerService#getVersionId(java.lang.String, java.lang.String, java.lang.String)
             */
            public Long getVersionId(String user, String password,
                    String versionName) {
                return null;
            }
        };
        
        
        ConfigCenterPropertyPlaceholderConfigurer extractor = new ConfigCenterPropertyPlaceholderConfigurer();
        ClassPathResource cpr = new ClassPathResource("/com/baidu/cc/spring/test.properties");
        extractor.setLocation(cpr);
        
        extractor.setConfigServerService(service);
        
        
        ConfigCenterPropertyPlaceholderConfigurer importer;
        importer = new ConfigCenterPropertyPlaceholderConfigurer();
        
        importer.setCcUser("xie");
        importer.setCcPassword("matthew");
        importer.setCcVersion(1);
        
        extractor.setCcUser("xie");
        extractor.setCcPassword("matthew");
        extractor.setCcVersion(1);
        
        importer.setConfigServerService(service);
        
        //initial importer
        ConfigCenterPropertyPlaceholderConfigurerTest test = new ConfigCenterPropertyPlaceholderConfigurerTest();
        importer.setApplicationContext(test.appContext);
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        importer.postProcessBeanFactory(beanFactory);
        
        extractor.setApplicationContext(test.appContext);
        extractor.postProcessBeanFactory(beanFactory);
        ConfigCenterPropertyExtractor ccpe = new ConfigCenterPropertyExtractor();
        ccpe.setCcVersion(2);
        ccpe.setExtractor(extractor);
        ccpe.setImporter(importer);
        
        
        ccpe.afterPropertiesSet();
        
    }
}
