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
package com.baidu.cc;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test class for {@link ConfigLoader}
 * 
 * @author xiemalin
 * @since 1.0.0.0
 */
public class ConfigLoaderTest {

    /**
     * test local resource write and read methods
     * 
     * @throws IOException io exception
     */
    @Test
    public void testReadWriteLoaclResource() throws IOException {
        URL resource = getClass().getResource("/com/baidu/cc/ConfigLoaderTestPropFile");
        
        //write property
        Map<String, String> ret = new HashMap<String, String>();
        ret.put("key1", "value1");
        ret.put("key2", "value2");
        
        ConfigLoader configLoader = new ConfigLoader();
        configLoader.writeLocal(ret, new File(resource.getPath()));
        
        Properties props = new Properties();
        
        configLoader.readLocal(props, new File(resource.getPath()));
        
        Assert.assertEquals(2, props.values().size());
        Assert.assertTrue(props.containsKey("key1"));
        Assert.assertTrue(props.containsValue("value2"));
    }
}
