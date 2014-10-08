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

import org.springframework.util.Assert;

import com.baidu.bjf.remoting.mcpack.OperationTimeoutMcpackRpcProxyFactoryBean;
import com.baidu.cc.interfaces.ExtConfigServerService;

/**
 * 
 * Client API utility class to access configuration center server.
 *
 * @author xiemalin
 * @since 1.0.1.0
 */
public class ConfigCenterClient {

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
     * Request configuration version
     */
    private long ccVersion;
    
    /**
     * 版本名称，如果不为空，则忽略ccVersion
     */
    private String ccVersionName;
    
    /**
     * default connection time out value.
     */
    public static final int DEFAULT_CONNECTION_TIMEOUT = 2000;
    
    /**
     * connection time out value to connect configuration center server.
     */
    private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
    
    /**
     * default read time out value.
     */
    public static final int DEFAULT_READ_TIMEOUT = 2000;
    
    /**
     * set read time out value on reading value from configuration center server.
     */
    private int readTimeout = DEFAULT_READ_TIMEOUT;
    
    /**
     * default call back interval value.
     */
    public static final long DEFAULT_CALLBACK_INTERVAL = 2000L;
    
    /**
     * set call back listen call back interval
     */
    private long callbackInteval = DEFAULT_CALLBACK_INTERVAL;
    
    /**
     * project name
     */
    private String projectName;
    
    /**
     * set project name
     * @param projectName the projectName to set
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * environment name
     */
    private String envName;
    
    /**
     * set environment name
     * @param envName the envName to set
     */
    public void setEnvName(String envName) {
        this.envName = envName;
    }
    
    /**
     * request configuration environment id under specified version.
     */
    private long ccEnvId;
    
    /**
     * crate {@link ConfigLoader}
     * @return {@link ConfigLoader} instance
     */
    public ConfigLoader createConfigLoader() {
        ccServerUrl = Constants.getServerUrl(ccServerUrl);
        Assert.hasLength(ccServerUrl, "property 'ccServerUrl' is blank.");
        
        ExtConfigServerService configServerService = createConfigService(ccServerUrl, 
                connectionTimeout, readTimeout);
        
        ConfigLoader configLoader = new ConfigLoader();
        configLoader.setCcEnvId(Constants.getEnvId(String.valueOf(ccEnvId)));
        configLoader.setCcVersion(Constants.getVersion(String.valueOf(ccVersion)));
        configLoader.setProjectName(Constants.getProjectName(projectName));
        configLoader.setEnvName(Constants.getEnvName(envName));
        configLoader.setCcUser(Constants.getUser(ccUser));
        configLoader.setCcPassword(Constants.getPassword(ccPassword));
        configLoader.setCallbackInteval(callbackInteval);
        configLoader.setCcVersionName(Constants.getVersionName(ccVersionName));
        
        configLoader.setConfigServerService(configServerService);
        
        configLoader.init();
        
        return configLoader;
    }

    /**
     * To create {@link ExtConfigServerService} proxy instance.
     * 
     * @param ccServerUrl configuration server url
     * @param connectionTimeout connection time out value in ms.
     * @param readTimeout read time out value in ms.
     * @return proxy instance.
     */
    private ExtConfigServerService createConfigService(String ccServerUrl, int connectionTimeout,
            int readTimeout) {
        OperationTimeoutMcpackRpcProxyFactoryBean proxy = new OperationTimeoutMcpackRpcProxyFactoryBean();
        proxy.setServiceUrl(Constants.getServerUrl(ccServerUrl));
        proxy.setServiceInterface(ExtConfigServerService.class);
        proxy.setConnectionTimeout(connectionTimeout);
        proxy.setSoTimeout(readTimeout);
        
        // do initial
        proxy.afterPropertiesSet();
        
        return (ExtConfigServerService) proxy.getObject();
    }

    /**
     * set ccServerUrl value to ccServerUrl
     * @param ccServerUrl the ccServerUrl to set
     */
    public void setCcServerUrl(String ccServerUrl) {
        this.ccServerUrl = ccServerUrl;
    }

    /**
     * set ccUser value to ccUser
     * @param ccUser the ccUser to set
     */
    public void setCcUser(String ccUser) {
        this.ccUser = ccUser;
    }

    /**
     * set ccPassword value to ccPassword
     * @param ccPassword the ccPassword to set
     */
    public void setCcPassword(String ccPassword) {
        this.ccPassword = ccPassword;
    }

    /**
     * set ccVersion value to ccVersion
     * @param ccVersion the ccVersion to set
     */
    public void setCcVersion(long ccVersion) {
        this.ccVersion = ccVersion;
    }

    /**
     * set ccVersionName value to ccVersionName
     * @param ccVersionName the ccVersionName to set
     */
    public void setCcVersionName(String ccVersionName) {
        this.ccVersionName = ccVersionName;
    }

    /**
     * set connectionTimeout value to connectionTimeout
     * @param connectionTimeout the connectionTimeout to set
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * set readTimeout value to readTimeout
     * @param readTimeout the readTimeout to set
     */
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    /**
     * set callbackInteval value to callbackInteval
     * @param callbackInteval the callbackInteval to set
     */
    public void setCallbackInteval(long callbackInteval) {
        this.callbackInteval = callbackInteval;
    }

    /**
     * set ccEnvId value to ccEnvId
     * @param ccEnvId the ccEnvId to set
     */
    public void setCcEnvId(long ccEnvId) {
        this.ccEnvId = ccEnvId;
    }
    
    
}
