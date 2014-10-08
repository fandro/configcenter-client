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

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.baidu.cc.interfaces.ChangedConfigItem;
import com.baidu.cc.interfaces.ConfigItemChangedCallable;

/**
 * Test class for {@link ConfigCenterClient}
 * @author xiemalin
 * @since 1.0.1.0
 */
public class ConfigCenterClientTest {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(ConfigCenterClientTest.class);

    /**
     * demo for API usage.
     */
    public void demoforAPIUsage() {
        ConfigCenterClient client = new ConfigCenterClient();
        
        String ccServerUrl = "http://localhost:8080/rpc/ExtConfigServerService";
        String ccUser = "test1";
        String ccPassword = "123";
        String ccVersionName = "bjf-1.0.1.0";
        
        client.setCcServerUrl(ccServerUrl);
        client.setCcUser(ccUser);
        client.setCcPassword(ccPassword);
        client.setCcVersionName(ccVersionName);
        
        // create config loader
        ConfigLoader configLoader = client.createConfigLoader();
        
        // read all config item
        Map<String, String> configItems = configLoader.getConfigItems();
        LOGGER.info(configItems);
        
        // add call back listener here
        configLoader.getCallables().put("cust_listener", new ConfigItemChangedCallable() {
            
            public void changed(List<ChangedConfigItem> changedConfigItemList) {
                
            }
        });
        Properties properties = new Properties();
        properties.putAll(configItems);
        configLoader.setVersionTag(configItems);
        configLoader.startListening(properties);
        
    }
    
}
