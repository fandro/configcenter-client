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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.baidu.cc.interfaces.ChangedConfigItem;
import com.baidu.cc.interfaces.ConfigItemChangedCallable;

/**
 * Batch operation support for {@link ConfigItemChangedCallable}
 * 
 * @author xiemalin
 * @since 1.0.0.0
 */
public class BatchConfigItemChangedCallable implements
        ConfigItemChangedCallable {

    /**
     * batch {@link ConfigItemChangedCallable} map
     */
    private List<ConfigItemChangedCallable> list;
    /**
     * all loaded batch callback bean names
     */
    private Set<String> beanNames;
    
    /**
     * default constructor
     */
    public BatchConfigItemChangedCallable() {
        list = new ArrayList<ConfigItemChangedCallable>();
        beanNames = new HashSet<String>();
    }
    
    /**
     * add target {@link ConfigItemChangedCallable} in batch call back list.
     * 
     * @param name bean name
     * @param callable target {@link ConfigItemChangedCallable} to batch call back
     */
    public void addConfigItemChangedCallable(String name, ConfigItemChangedCallable callable) {
        if (callable != null) {
            list.add(callable);
            beanNames.add(name);
        }
    }
    
    
    /**
     * Returns <tt>true</tt> if this batch list contains no elements.
     *
     * @return <tt>true</tt> if this batch list contains no elements.
     */
    public boolean isEmpty() {
        return list.isEmpty();
    }
    
    /**
     * Clear this batch list emements.
     * 
     * @return
     */
    public void clear() {
        list.clear();
    }
    
    /**
     * get batch callable bean names
     * @return batch callable bean names
     */
    public Set<String> getCallableBeanNames() {
        return beanNames;
    }
     
    /**
     * 批量回调用配置变更接口
     * ChangedConfigItem 变更的回调接口类
     * @param changedConfigItemList 批量回调的配置变更回调接口
     */
    public void changed(List<ChangedConfigItem> changedConfigItemList) {
        if (isEmpty()) {
            return;
        }
        
        List<ConfigItemChangedCallable> copied = new ArrayList<ConfigItemChangedCallable>(list);
        for (ConfigItemChangedCallable configItemChangedCallable : copied) {
            configItemChangedCallable.changed(changedConfigItemList);
        }

    }

}
