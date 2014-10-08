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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import com.baidu.cc.interfaces.ChangedConfigItem;
import com.baidu.cc.interfaces.ConfigItemChangedCallable;
import com.baidu.rigel.platform.util.PatternUtils;

/**
 * Composite {@link ConfigItemChangedCallable} supports filter keys 
 * 
 * @author xiemalin
 * @since 1.0.0.0
 */
public class FilterableConfigItemChangedCallable implements
        ConfigItemChangedCallable {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger
            .getLogger(FilterableConfigItemChangedCallable.class);

    /**
     * target method
     */
    private Method method;
    /**
     * target bean
     */
    private Object target;
    
    /**
     * filter keys set from annotation.
     */
    private Set<String> filerKeys;
    
    
    /**
     * default constructor
     * 
     * @param method target method
     * @param target target bean
     * @param filerKeyStr filter keys set from annotation
     */
    public FilterableConfigItemChangedCallable(Method method, Object target,
            String filerKeyStr) {
        super();
        Assert.notNull(method, "property 'method' should not be null");
        this.method = method;
        //check method
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes == null || parameterTypes.length != 1 ||
                !List.class.equals(parameterTypes[0])) {
            throw new RuntimeException("Invalid method. @ConfigChangeNotifer method only " +
                "has one parameter and type is List<'" + ChangedConfigItem.class.getName() + ">'");
        }
        
        this.target = target;
        filerKeys = new HashSet<String>();
        if (StringUtils.isNotBlank(filerKeyStr)) {
            String[] split = StringUtils.split(filerKeyStr, ",");
            for (String string : split) {
                filerKeys.add(StringUtils.trim(string));
            }
        } 
        
    }

    /**
     * process call back here. <br>
     * it will process each config item call back by its filter keys set.
     * @param changedConfigItemList changed config item to filter by filter keys.
     * 
     */
    public void changed(List<ChangedConfigItem> changedConfigItemList) {
        if (CollectionUtils.isEmpty(changedConfigItemList)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("got a blank changedConfigItemList.");
            }
            return;
        }
        
        if (CollectionUtils.isEmpty(filerKeys)) {
            doChanged(changedConfigItemList);
        }
        
        List<ChangedConfigItem> filteredItems = new ArrayList<ChangedConfigItem>();
        for (ChangedConfigItem changedConfigItem : changedConfigItemList) {
            String key = changedConfigItem.getKey();
            if (isFiltered(key)) {
                filteredItems.add(changedConfigItem);
            }
        }
        
        if (!filteredItems.isEmpty()) {
            doChanged(filteredItems);
        }
        

    }
    
    /**
     * check if key is in filter set.
     * 
     * @param key to filter check
     * @return true if in filter list
     */
    private boolean isFiltered(String key) {
        for (String filter : filerKeys) {
            if (PatternUtils.simpleMatch(filter, key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * do configuration item change call back
     * @param changedConfigItemList changed configuration item list
     */
    private void doChanged(List<ChangedConfigItem> changedConfigItemList) {
        try {
            method.invoke(target, changedConfigItemList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
    
}
