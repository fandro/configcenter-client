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

import com.baidu.cc.spring.ConfigQueryManager;

/**
 * Interface to be implemented by any object that wishes to be notified
 * of the {@link ConfigQueryManager} that it runs in.
 * 
 * @author xiemalin
 * @since 1.0.0.0
 */
public interface ConfigQueryManagerAware {

    /**
     * Set the ConfigQueryManager that this object runs in. <br>
     * Normally this call will be used to initialize the object.<br>
     * Note it maybe call times as defined more than one {@link ConfigCenterPropertyPlaceholderConfigurer} 
     * defined in the container.
     * 
     * @param configQueryManager target {@link ConfigQueryManager} in container.
     */
    void setConfigQueryManager(ConfigQueryManager configQueryManager);
}
