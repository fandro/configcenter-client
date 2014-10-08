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
package com.baidu.cc.annotation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baidu.cc.interfaces.ChangedConfigItem;

/**
 * @author xiemalin
 *
 */
@Service("configChangedMock")
public class ConfigChangedMock {

    @ConfigChangeNotifer
    public void doChangedAll(List<ChangedConfigItem> changedConfigItems) {
        System.out.println("doChangedAll call back");
        System.out.println(changedConfigItems);
        
    }
   
    @ConfigChangeNotifer(keys = "key3,key2,key1")
    public void doChangedByFilter(List<ChangedConfigItem> changedConfigItems) {
        System.out.println("doChangedByFilter call back");
        System.out.println(changedConfigItems);
        
    }
    
}
