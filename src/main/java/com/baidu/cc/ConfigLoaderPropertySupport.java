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

import java.util.Map;

import com.baidu.cc.interfaces.ConfigItemChangedCallable;

/**
 * {@link ConfigLoader} property support
 * 
 * @author xiemalin
 * @since 1.0.0.5
 */
public interface ConfigLoaderPropertySupport extends ConfigItemChangedCallable {

    /**
     * set ccServerUrl
     * @param ccServerUrl the ccServerUrl to set
     */
    void setCcServerUrl(String ccServerUrl);

    /**
     * set ccUser
     * @param ccUser the ccUser to set
     */
    void setCcUser(String ccUser);

    /**
     * set ccPassword
     * @param ccPassword the ccPassword to set
     */
    void setCcPassword(String ccPassword);

    /**
     * set ccVersionName
     * @param ccVersionName the ccVersionName to set
     */
    void setCcVersionName(String ccVersionName);
    /**
     * set enableUpdateCallback
     * @param enableUpdateCallback the enableUpdateCallback to set
     */
    void setEnableUpdateCallback(boolean enableUpdateCallback);

    /**
     * set connectionTimeout
     * @param connectionTimeout the connectionTimeout to set
     */
    void setConnectionTimeout(int connectionTimeout);

    /**
     * set readTimeout
     * @param readTimeout the readTimeout to set
     */
    void setReadTimeout(int readTimeout);

    /**
     * set callbackInteval
     * @param callbackInteval the callbackInteval to set
     */
    void setCallbackInteval(long callbackInteval);

    /**
     * get ccServerUrl
     * @return the ccServerUrl
     */
    String getCcServerUrl();

    /**
     * get ccUser
     * @return the ccUser
     */
    String getCcUser();
    /**
     * get ccPassword
     * @return the ccPassword
     */
    String getCcPassword();

    /**
     * get ccVersionName
     * @return the ccVersionName
     */
    String getCcVersionName();

    /**
     * get enableUpdateCallback
     * @return the enableUpdateCallback
     */
    boolean isEnableUpdateCallback();

    /**
     * get connectionTimeout
     * @return the connectionTimeout
     */
    int getConnectionTimeout();

    /**
     * get readTimeout
     * @return the readTimeout
     */
    int getReadTimeout();

    /**
     * get callbackInteval
     * @return the callbackInteval
     */
    long getCallbackInteval();
    
    /**
     * Call back after properties load
     * 
     * @param properties loaded properties.
     */
    void propertiesLoad(Map<String, String> properties);
    
}
