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
package com.baidu.cc.patch.struts2;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.util.Assert;

import com.baidu.bjf.web.struts2.interceptor.JsonAwareParametersInterceptor;
import com.baidu.cc.ConfigLoader;
import com.baidu.cc.ConfigLoaderPropertySupport;
import com.baidu.cc.Constants;
import com.baidu.cc.interfaces.ChangedConfigItem;

/**
 * support configuration load from configuration center.
 * 
 * @author xiemalin
 * @since 1.0.0.6
 */
public class ReloadableJsonAwareParametersInterceptor extends JsonAwareParametersInterceptor
implements ConfigLoaderPropertySupport {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger
            .getLogger(ReloadableJsonAwareParametersInterceptor.class);
    
    /**
     * property name for excludeParams
     */
    private static final String EXCLUDEPARAMS_PROP = "excludeParams";

    /**
     * property name for acceptParamNames
     */
    private static final String ACCEPTPARAMNAMES_PROP = "acceptParamNames";
    
    
    /**
     * serial Version UID
     */
    private static final long serialVersionUID = 2786118695147424045L;
    
    /**
     * Configuration center server URI.
     */
    private String ccServerUrl;
    /**
     * Configuration center server user name.
     */
    private String ccUser;
    /**
     * Configuration center server password.
     */
    private String ccPassword;
    /**
     * 版本名称，如果不为空，则忽略ccVersion
     */
    private String ccVersionName;
    
    /**
     * set if enable update call back
     */
    private boolean enableUpdateCallback = true;
    
    /**
     * default connection time out value.
     */
    protected static final int DEFAULT_CONNECTION_TIMEOUT = 2000;
    
    /**
     * connection time out value to connect configuration center server.
     */
    private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
    
    /**
     * default read time out value.
     */
    protected static final int DEFAULT_READ_TIMEOUT = 2000;
    
    /**
     * set read time out value on reading value from configuration center server.
     */
    private int readTimeout = DEFAULT_READ_TIMEOUT;
    
    /**
     * default call back interval value.
     */
    protected static final long DEFAULT_CALLBACK_INTERVAL = 2000L;
    
    /**
     * set call back listen call back interval
     */
    private long callbackInteval = DEFAULT_CALLBACK_INTERVAL;
    
    /**
     * configLoader
     */
    private ConfigLoader configLoader;
    
    /**
     * do property load here.
     */
    @Override
    public void init() {
        super.init();
        
        ccServerUrl = Constants.getServerUrl(ccServerUrl);
        ccUser = Constants.getUser(ccUser);
        ccPassword = Constants.getPassword(ccPassword);
        
        
        Assert.hasLength(ccServerUrl, "property 'ccServerUrl' can not blank");
        Assert.hasLength(ccUser, "property 'ccUser' can not blank");
        Assert.hasText(ccVersionName, "property 'ccVersionName' can not blank");
        
        configLoader = ConfigLoader.createConfigLoader(this);
    }
    
    /**
     * close {@link ConfigLoader} .
     */
    @Override
    public void destroy() {
        super.destroy();
        
        if (configLoader != null) {
            configLoader.stop();
        }
    }

    /**
     * 配置中心值变更回调接口
     * ChangedConfigItem有三个属性 key/oldValue/newValue
     * @param changedConfigItemList 发生变更的key列表
     */
    public void changed(List<ChangedConfigItem> changedConfigItemList) {
        for (ChangedConfigItem changedConfigItem : changedConfigItemList) {
            String key = changedConfigItem.getKey();
            String value = changedConfigItem.getNewValue();
            if (EXCLUDEPARAMS_PROP.equals(key)) {
                setExcludeParams(value);
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Read 'excludeParams' value=" + value);
                }
            } else if (ACCEPTPARAMNAMES_PROP.equals(key)) {
                setAcceptParamNames(value);
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Read 'acceptParamNames' value=" + value);
                }
                
            }
            
        }
        
    }

    /**
     * set ccServerUrl
     * @param ccServerUrl the ccServerUrl to set
     */
    public void setCcServerUrl(String ccServerUrl) {
        this.ccServerUrl = ccServerUrl;
    }

    /**
     * set ccUser
     * @param ccUser the ccUser to set
     */
    public void setCcUser(String ccUser) {
        this.ccUser = ccUser;
    }

    /**
     * set ccPassword
     * @param ccPassword the ccPassword to set
     */
    public void setCcPassword(String ccPassword) {
        this.ccPassword = ccPassword;
    }

    /**
     * set ccVersionName
     * @param ccVersionName the ccVersionName to set
     */
    public void setCcVersionName(String ccVersionName) {
        this.ccVersionName = ccVersionName;
    }

    /**
     * set enableUpdateCallback
     * @param enableUpdateCallback the enableUpdateCallback to set
     */
    public void setEnableUpdateCallback(boolean enableUpdateCallback) {
        this.enableUpdateCallback = enableUpdateCallback;
    }

    /**
     * set connectionTimeout
     * @param connectionTimeout the connectionTimeout to set
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * set readTimeout
     * @param readTimeout the readTimeout to set
     */
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    /**
     * set callbackInteval
     * @param callbackInteval the callbackInteval to set
     */
    public void setCallbackInteval(long callbackInteval) {
        this.callbackInteval = callbackInteval;
    }

    /**
     * get ccServerUrl
     * @return the ccServerUrl
     */
    public String getCcServerUrl() {
        return ccServerUrl;
    }

    /**
     * get ccUser
     * @return the ccUser
     */
    public String getCcUser() {
        return ccUser;
    }

    /**
     * get ccPassword
     * @return the ccPassword
     */
    public String getCcPassword() {
        return ccPassword;
    }

    /**
     * get ccVersionName
     * @return the ccVersionName
     */
    public String getCcVersionName() {
        return ccVersionName;
    }

    /**
     * get enableUpdateCallback
     * @return the enableUpdateCallback
     */
    public boolean isEnableUpdateCallback() {
        return enableUpdateCallback;
    }

    /**
     * get connectionTimeout
     * @return the connectionTimeout
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * get readTimeout
     * @return the readTimeout
     */
    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     * get callbackInteval
     * @return the callbackInteval
     */
    public long getCallbackInteval() {
        return callbackInteval;
    }
    
    /**
     * Call back after properties load
     * 
     * @param properties loaded properties.
     */
    public void propertiesLoad(Map<String, String> properties) {
        if (MapUtils.isEmpty(properties)) {
            return;
        }
        String exclueParams = properties.get(EXCLUDEPARAMS_PROP);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Read 'excludeParams' value=" + exclueParams);
        }
        setExcludeParams(exclueParams);
        
        String acceptParamNames = properties.get(ACCEPTPARAMNAMES_PROP);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Read 'acceptParamNames' value=" + acceptParamNames);
        }
        
        setAcceptParamNames(acceptParamNames);
    }
    
    /**
     * check param is accepted
     * @param paramName parameter name
     * @return true if accepted
     */
    @Override
    protected boolean isAccepted(String paramName) {
        return super.isAccepted(paramName);
    }
    
    /**
     * check param is exclude
     * @param paramName parameter name
     * @return true if exlucded
     */
    @Override
    protected boolean isExcluded(String paramName) {
        return super.isExcluded(paramName);
    }
}

