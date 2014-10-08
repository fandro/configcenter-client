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

import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.baidu.bjf.remoting.mcpack.OperationTimeoutMcpackRpcProxyFactoryBean;
import com.baidu.cc.ConfigLoader;
import com.baidu.cc.Constants;
import com.baidu.cc.interfaces.ChangedConfigItem;
import com.baidu.cc.interfaces.ConfigItemChangedCallable;
import com.baidu.cc.interfaces.ExtConfigServerService;

/**
 * Configuration center kernal api usage example.
 * this example will should the detail how to use API to connection to 
 * configuration center directly.
 * 
 * @author xiemalin
 * @since 1.0.0.0
 */
public class ConfigCenterAPIDemo implements ConfigItemChangedCallable {
	/**
	 * Logger for this class
	 */
    private static final Logger LOGGER = Logger
			.getLogger(ConfigCenterAPIDemo.class);

    /**
     * To create {@link ExtConfigServerService} proxy instance.
     * 
     * @param ccServerUrl configuration server url
     * @param connectionTimeout connection time out value in ms.
     * @param readTimeout read time out value in ms.
     * @return proxy instance.
     */
    private static ExtConfigServerService createConfigService(String ccServerUrl, int connectionTimeout,
            int readTimeout) {
        OperationTimeoutMcpackRpcProxyFactoryBean proxy = new OperationTimeoutMcpackRpcProxyFactoryBean();
        proxy.setServiceUrl(Constants.getServerUrl(ccServerUrl));
        proxy.setServiceInterface(ExtConfigServerService.class);
        proxy.setConnectionTimeout(connectionTimeout);
        proxy.setSoTimeout(readTimeout);
        
        //do initial
        proxy.afterPropertiesSet();
        
        return (ExtConfigServerService) proxy.getObject();
    }
    
    
    /**
     * main test method.
     * 
     * @param args main execute arguments.
     * @throws Exception to throw out all exception to console
     */
    public static void main(String[] args) throws Exception {
    	String ccServerUrl = "http://localhost:8080/rpc/ExtConfigServerService";
        int connectionTimeout = 13000;
        int readTimeout = 13000;
        
        //create configservice 
        ExtConfigServerService service;
        service = createConfigService(ccServerUrl, connectionTimeout, readTimeout);
        
        ConfigLoader configLoader = new ConfigLoader();
        configLoader.setCcVersion(370L);
        configLoader.setCcUser("test1");
        configLoader.setCcPassword("123");
        configLoader.setCallbackInteval(1000L);
        
        configLoader.setConfigServerService(service);
        configLoader.init();
        
        //get configruation items from server
        Map<String, String> configItems = configLoader.getConfigItems();
        
        configLoader.getCallables().put("cust_listener", new ConfigCenterAPIDemo());
        
        Properties properties = new Properties();
        properties.putAll(configItems);
        configLoader.setVersionTag(configItems);
        configLoader.startListening(properties);

    }

    /**
     * Due to this a demo method please implement it in product environment.
     * @param changedConfigItemList changed configuration item list.
     */
    public void changed(List<ChangedConfigItem> changedConfigItemList) {
        
        for (ChangedConfigItem changedConfigItem : changedConfigItemList) {
        	LOGGER.info("configuration item changed: key=" + changedConfigItem.getKey()
        			+ " old-value=" + changedConfigItem.getOldValue() + 
        			 " new-value=" + changedConfigItem.getNewValue());
		}
    }

}
