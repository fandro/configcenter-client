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

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.baidu.cc.ConfigServerServiceMock;
import com.baidu.cc.interfaces.ConfigServerService;

/**
 * test class for configuration item change notify by annotation
 * 
 * @author xiemalin
 * @since 1.0.0.0
 */
public class ConfigChangeNotiferAnnotationParserTest {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger
            .getLogger(ConfigChangeNotiferAnnotationParserTest.class);
    
    /**
     * test annotation parser.
     */
    @Ignore
    @Test
    public void testAnnotationParserInRealRuntime() {
        
        ClassPathXmlApplicationContext appContext;
        appContext = new ClassPathXmlApplicationContext(
            "classpath:/com/baidu/cc/annotation/applicationContext-no-mock.xml");
        
        
        appContext.start();
        
        try {
            Thread.sleep(3000L);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
    

    /**
     * test annotation parser. by mock class.
     */
    @Test
    public void testAnnotationParserByMock() {
        
        ClassPathXmlApplicationContext appContext;
        appContext = new ClassPathXmlApplicationContext("classpath:/com/baidu/cc/annotation/applicationContext.xml");
        
        
        appContext.start();
        
        //get service bean
        ConfigServerServiceMock configServerService;
        configServerService = (ConfigServerServiceMock) appContext.getBean("configServerServiceMocker");
        
        
        Assert.assertNotNull(configServerService);
        try {
            Thread.sleep(500L);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        
        Map<String, String> ret = new HashMap<String, String>();
        ret.put(ConfigServerService.TAG_KEY, ConfigServerServiceMock.TEST_TAG_NEW_VALUE);
        
        ret.put("key1", "value1");
        ret.put("key2", "value22");
        ret.put("key3", "value3");
        ret.put("xml", "2");
        
        configServerService.setProps(ret);
        configServerService.setTagValue(ConfigServerServiceMock.TEST_TAG_NEW_VALUE);
        
        Assert.assertEquals(ConfigServerServiceMock.TEST_TAG_NEW_VALUE, configServerService.getTagValue());
        
        try {
            Thread.sleep(3000L);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        
       
       
       // appContext.stop();
        
    }
}
