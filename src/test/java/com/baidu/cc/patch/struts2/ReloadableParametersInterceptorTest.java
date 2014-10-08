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
package com.baidu.cc.patch.struts2;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.baidu.cc.interfaces.ChangedConfigItem;

/**
 * test class for ReloadableParametersInterceptor
 * 
 * @author xiemalin
 * @since 1.0.0.6
 */
public class ReloadableParametersInterceptorTest {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger
            .getLogger(ReloadableParametersInterceptorTest.class);

    
    /**
     * test init method
     */
    @Test
    public void testinit() {
        ReloadableParametersInterceptor rpi = new ReloadableParametersInterceptor();
        rpi.setCcServerUrl("http://configcenter.jpaas-idea.baidu.com/rpc/ExtConfigServerService");
        rpi.setCcUser("test1");
        rpi.setCcPassword("123");
        rpi.setCcVersionName("struts2-parameters-1.0.0");
        rpi.setConnectionTimeout(2000);
        rpi.setReadTimeout(1000);
        rpi.setEnableUpdateCallback(true);
        
        Assert.assertEquals("http://configcenter.jpaas-idea.baidu.com/rpc/ExtConfigServerService", rpi.getCcServerUrl());
        Assert.assertEquals("test1", rpi.getCcUser());
        Assert.assertEquals("123", rpi.getCcPassword());
        Assert.assertEquals("struts2-parameters-1.0.0", rpi.getCcVersionName());
        Assert.assertEquals(2000, rpi.getConnectionTimeout());
        Assert.assertEquals(1000, rpi.getReadTimeout());
        
        try {
            rpi.init();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
    
    
    /**
     * test propertiesLoad method
     */
    @Test
    public void testpropertiesLoad() {
        ReloadableParametersInterceptor rpi = new ReloadableParametersInterceptor();
        
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(ReloadableParametersInterceptor.EXCLUDEPARAMS_PROP, "abc");
        properties.put(ReloadableParametersInterceptor.ACCEPTPARAMNAMES_PROP, "cde");
        rpi.propertiesLoad(properties);
        
        Assert.assertTrue(rpi.isExcluded("abc"));
        
        Assert.assertTrue(rpi.isAccepted("cde"));
        
    }
    
    /**
     * test changed method
     */
    @Test
    public void testchanged() {
        ReloadableParametersInterceptor rpi = new ReloadableParametersInterceptor();
        
        List<ChangedConfigItem> changedConfigItemList = new ArrayList<ChangedConfigItem>(2);
        
        ChangedConfigItem cci = new ChangedConfigItem();
        cci.setKey(ReloadableParametersInterceptor.EXCLUDEPARAMS_PROP);
        cci.setNewValue("abc");
        changedConfigItemList.add(cci);
        
        cci = new ChangedConfigItem();
        cci.setKey(ReloadableParametersInterceptor.ACCEPTPARAMNAMES_PROP);
        cci.setNewValue("cde");
        changedConfigItemList.add(cci);
        
        rpi.changed(changedConfigItemList);
        
        Assert.assertTrue(rpi.isExcluded("abc"));
        
        Assert.assertTrue(rpi.isAccepted("cde"));
    }
}
