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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.baidu.cc.interfaces.ChangedConfigItem;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * Configuartion changed listener.
 * 
 * @author xiemalin
 * @since 1.0.0.0
 */
public class ConfigChangedListener implements Runnable {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger
            .getLogger(ConfigChangedListener.class);

    /**
     * if true means listener thread is stopped.
     */
    private boolean stop = true;
    /**
     * listener thread executor instance.
     */
    private ExecutorService es;

    /**
     * get stop status.
     * @return the stop
     */
    public boolean isStop() {
        return stop;
    }
    
    /**
     * start listener thread
     */
    public synchronized void start() {
        if (es == null) {
            ThreadFactoryBuilder tfbuilder = new ThreadFactoryBuilder();
            tfbuilder.setNameFormat("ConfigChangedListener-Thread");
            es = Executors.newSingleThreadExecutor(tfbuilder.build());
        }
        stop = false;
        es.execute(this);
    }
    
    /**
     * do close and shut down listener thread
     */
    public synchronized void close() {
        if (es != null) {
            stop = true;
            es.shutdown();
            es = null;
        }
    }
    
    /**
     * old configuration items to check.
     */
    private Properties properties;
    /**
     * {@link ConfigLoader} instance.
     */
    private ConfigLoader configLoader;

    /**
     * default constructor.
     * 
     * @param properties all loaded properties
     * @param configLoader {@link ConfigLoader} instance.
     */
    public ConfigChangedListener(Properties properties,
            ConfigLoader configLoader) {
        super();
        this.properties = properties;
        this.configLoader = configLoader;
    }
    
    /**
     * create a new {@link ChangedConfigItem} instance.
     * 
     * @param key item key
     * @param oldValue old value
     * @param newValue new value
     * @return  a new {@link ChangedConfigItem} instance.
     */
    private ChangedConfigItem newChangedConfigItem(String key, String oldValue,
            String newValue) {
        ChangedConfigItem changedConfigItem = new ChangedConfigItem();
        changedConfigItem.setKey(key);
        changedConfigItem.setOldValue(oldValue);
        changedConfigItem.setNewValue(newValue);
        return changedConfigItem;
    }
    
    /**
     * get changed item list compare with current.
     * 
     * @return changed item list
     */
    private List<ChangedConfigItem> checkChangedItems() {
        
        Map<String, String> configItems = configLoader.getConfigItems();
        if (configItems == null) {
            return Collections.emptyList();
        }
        
        //update local resource if need
        configLoader.onLatestUpdate(configItems);
        
        //update version tag
        configLoader.setVersionTag(configItems);
        
        Map copied = new HashMap(properties);
        
        //find different from old
        Iterator<Entry<Object, Object>> iterator = copied.entrySet().iterator();
        List<ChangedConfigItem> changedConfigItems = new LinkedList<ChangedConfigItem>();
        while (iterator.hasNext()) {
            Entry<Object, Object> entry = iterator.next();
            String key = entry.getKey().toString();
            String oldValue = entry.getValue().toString();
            String newValue = configItems.get(key);
            if (newValue == null) {
                changedConfigItems.add(newChangedConfigItem(key, oldValue, null));
                //remove deleted
                properties.remove(key);
            } else {
                if (!oldValue.equals(newValue)) {
                    changedConfigItems.add(newChangedConfigItem(key, oldValue, newValue)); 
                    
                    //update value
                    properties.put(key, newValue);
                }
            }

            configItems.remove(key);
        }
        
        //add new added
        Iterator<Entry<String, String>> newAdded = configItems.entrySet().iterator();
        while (newAdded.hasNext()) {
            Entry<String, String> entry = newAdded.next();
            changedConfigItems.add(newChangedConfigItem(entry.getKey(), null, entry.getValue()));
            
            //add new value
            properties.put(entry.getKey(), entry.getValue());
        }
        
        
        return changedConfigItems;
    }

    /**
     * do start listener thread.
     */
    public void run() {
        while (!stop) {
            try {
                Thread.sleep(configLoader.getCallbackInteval());
                
                
                //check version tag. tag should not be null
                String versionTag = configLoader.getVersionTag();
                if (StringUtils.isBlank(versionTag)) {
                    LOGGER.warn("Found local version tag is null, it could be a error " +
                    		"from configuration center server. ");
                    continue;
                } else {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Checking version tag to server... current tag:" + versionTag);
                    }
                }
                
                //check tag
                boolean isLastTag = configLoader.checkVersionTag();
                
                if (!isLastTag) {
                    List<ChangedConfigItem> changedItems = checkChangedItems();
                    configLoader.doCallback(changedItems);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        
    }

    
}
