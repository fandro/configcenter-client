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

import org.apache.log4j.Logger;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baidu.cc.interfaces.ChangedConfigItem;
import com.baidu.cc.interfaces.ConfigItemChangedCallable;

/**
 * mock class for test {@link ConfigItemChangedCallable} interface.
 * 
 * @author xiemalin
 * @since 1.0.0.0
 */
@Service("configChangedCallbackMock")
public class ConfigChangedCallbackMock implements ConfigItemChangedCallable {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger
            .getLogger(ConfigChangedCallbackMock.class);

    /* (non-Javadoc)
     * @see com.baidu.cc.interfaces.ConfigItemChangedCallable#changed(java.util.List)
     */
    public void changed(List<ChangedConfigItem> changedConfigItemList) {
       
        for (ChangedConfigItem changedConfigItem : changedConfigItemList) {
            //get change notify content
            LOGGER.info("Changed configuration key: " + changedConfigItem.getKey());
            LOGGER.info("original value: " + changedConfigItem.getOldValue());
            LOGGER.info("new value: " + changedConfigItem.getNewValue());
        }

    }

}
