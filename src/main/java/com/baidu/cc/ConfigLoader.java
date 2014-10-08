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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.util.Assert;

import com.baidu.bjf.remoting.mcpack.OperationTimeoutMcpackRpcProxyFactoryBean;
import com.baidu.bjf.util.StopWatch;
import com.baidu.cc.interfaces.ChangedConfigItem;
import com.baidu.cc.interfaces.ConfigItemChangedCallable;
import com.baidu.cc.interfaces.ConfigServerService;
import com.baidu.cc.interfaces.ExtConfigServerService;
import com.baidu.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Configuration loader utility class.
 * 
 * @author xiemalin
 * @since 1.0.0.0
 */
public class ConfigLoader {
    
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(ConfigLoader.class);
    
    /**
     * JSON utility class to serialize configuration to file and reverse action. 
     */
    private Gson gson = new Gson();
    
    /**
     * file encode string
     */
    private static final String FILE_ENCODE = "utf-8";
    
    /**
     * configuration server client instance.
     */
    private ExtConfigServerService configServerService;

    /**
     * set configServerService instance.
     * @param configServerService the configServerService to set
     */
    public void setConfigServerService(ExtConfigServerService configServerService) {
        this.configServerService = configServerService;
    }


    /**
     * Configuration item changed callable instances.
     */
    private Map<String, ConfigItemChangedCallable> callables = new HashMap<String, ConfigItemChangedCallable>();
    
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
    private Long ccVersion;
    
    /**
     * 版本名称，如果不为空，则忽略ccVersion
     */
    private String ccVersionName;
    /**
     * request configuration environment id under specified version.
     */
    private Long ccEnvId;
    
    /**
     * project name
     */
    private String projectName;
    
    /**
     * environment name
     */
    private String envName;
    
    /**
     * set call back listen call back interval
     */
    private long callbackInteval;
    
    /**
     * current version tag value
     */
    private String versionTag;
    
    /**
     * local property file
     */
    private File localPropertyFile;
    
    
    /**
     * set project name
     * @param projectName the projectName to set
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    /**
     * set environment name
     * @param envName the envName to set
     */
    public void setEnvName(String envName) {
        this.envName = envName;
    }

    
    /**
     * set local property fiel
     * @param localPropertyFile the localPropertyFile to set
     */
    public void setLocalPropertyFile(File localPropertyFile) {
        this.localPropertyFile = localPropertyFile;
    }


    /**
     * get current version tag.
     * 
     * @return the versionTag
     */
    public String getVersionTag() {
        return versionTag;
    }


    /**
     * set current version tag.
     * @param versionTag the versionTag to set
     */
    public void setVersionTag(String versionTag) {
        this.versionTag = versionTag;
    }
    
    /**
     * set current version tag. and tag key will remove from map.
     * 
     * @param configItems configuration items
     */
    public void setVersionTag(Map<String, String> configItems) {
        if (MapUtils.isEmpty(configItems)) {
            this.versionTag = null;
            return;
        }
        
        setVersionTag(configItems.get(ConfigServerService.TAG_KEY));
        configItems.remove(ConfigServerService.TAG_KEY);
    }


    /**
     * get call back listen call back interval
     * @return the callbackInteval
     */
    protected long getCallbackInteval() {
        return callbackInteval;
    }


    /**
     * configuration changed listener
     */
    private ConfigChangedListener configChangedListener;
    
    /**
     * print current version id
     */
    private void printUseVersionLog() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Current use version id:" + ccVersion);
        }
    }
    
    /**
     * do initialize operation.
     */
    public void init() {
        //here need to check parameters
        Assert.hasLength(ccUser, "property 'ccUser' is blank.");
        Assert.notNull(configServerService, "property 'configServerService' is null");
        
        if (ccVersion != null && ccVersion > 0) {
            printUseVersionLog();
            return;
        }
        
        //if set ccVersionName
        Long vId = configServerService.getVersionId(ccUser, ccPassword, ccVersionName);
        
        if (vId != null) {
            ccVersion = vId;
            printUseVersionLog();
            return;
        }
        
        if (StringUtils.isNotBlank(projectName) && 
                StringUtils.isNotBlank(envName)) {
            long time = System.currentTimeMillis();
            ccVersion = configServerService.getLastestConfigVersion(ccUser,
                    ccPassword, projectName, envName);
            long timetook = System.currentTimeMillis() - time;
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Get configuration version id by projectName[" + projectName
                        + "] and envName[" + envName + "] and get version is:" + ccVersion + " time took(ms):" + timetook);
                
            }
            printUseVersionLog();
            return;
        }
        
        //check ccVersionName
        //if ccEnvId is not null should check ccVersion 
        if (ccEnvId != null && ccEnvId > 0) {
            //if ccVersion is not null then ccEnvId will be ignore
            if (ccVersion != null && ccVersion > 0) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Found configuration version id[" + ccVersion
                            + "] ccEnvId[" + ccEnvId + "] will be ignored.");
                    
                }
                
            } else {
                long time = System.currentTimeMillis();
                //should get ccVersion id by ccEnvId
                ccVersion = configServerService.getLastestConfigVersion(ccUser,
                        ccPassword, ccEnvId);
                if (LOGGER.isInfoEnabled()) {
                    long timetook = System.currentTimeMillis() - time;
                    LOGGER.info("Get configuration version id by envId[" + ccEnvId
                            + "] and get version is:" + ccVersion + " time took(ms):" + timetook);
                    
                }
            }
            
        }
       
        Assert.notNull(ccVersion, "property 'ccVersion' is null");
        
    }
    

    /**
     * set user name
     * @param ccUser the ccUser to set
     */
    public void setCcUser(String ccUser) {
        this.ccUser = ccUser;
    }


    /**
     * set password
     * @param ccPassword the ccPassword to set
     */
    public void setCcPassword(String ccPassword) {
        
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(ExtConfigServerService.class.getSimpleName());
        
        //all password to transport should be encrypt
        this.ccPassword = encryptor.encrypt(ccPassword);
    }


    /**
     * set version
     * @param ccVersion the ccVersion to set
     */
    public void setCcVersion(Long ccVersion) {
        this.ccVersion = ccVersion;
    }


    /**
     * @param ccVersionName the ccVersionName to set
     */
    public void setCcVersionName(String ccVersionName) {
        this.ccVersionName = ccVersionName;
    }

    /**
     * set environment id
     * @param ccEnvId the ccEnvId to set
     */
    public void setCcEnvId(Long ccEnvId) {
        this.ccEnvId = ccEnvId;
    }

    
    /**
     * set call back listen call back interval
     * @param callbackInteval the callbackInteval to set
     */
    public void setCallbackInteval(long callbackInteval) {
        this.callbackInteval = callbackInteval;
    }
    
    /**
     * do call back action.
     * 
     * @param changedConfigItems changed config item list
     */
    protected void doCallback(List<ChangedConfigItem> changedConfigItems) {
        if (CollectionUtils.isEmpty(changedConfigItems)) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Empty changed config items call back.");
            }
            return;
        }
        if (MapUtils.isNotEmpty(callables)) {
            Collection<ConfigItemChangedCallable> tmp;
            tmp = new ArrayList<ConfigItemChangedCallable>(callables.values());  
            //for safety consider
            List<ChangedConfigItem> changedItems = ListUtils.unmodifiableList(changedConfigItems);
            
            //do call back to each call
            for (ConfigItemChangedCallable configItemChangedCallable : tmp) {
                try {
                    configItemChangedCallable.changed(changedItems);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        
    }
    
    
    /**
     * Delegate to get config items from {@link ConfigServerService}
     * @return config item map
     */
    public Map<String, String> getConfigItems() {
        StopWatch sw = new StopWatch();
        sw.start();
        Map<String, String> ret = configServerService.getConfigItems(ccUser, ccPassword, ccVersion);
        long timetook = sw.stop();
        logRpcTimeTook(timetook, "getConfigItems");
        return ret;
    }
    
    /**
     * To log RPC method invoke message.
     * 
     * @param timetook RPC invoke time took
     * @param methodName RPC method name
     */
    private void logRpcTimeTook(long timetook, String methodName) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("RPC method '" + methodName + "' time took(ms) :" + timetook);
        }
    }
    
    /**
     * Import configuration items into the server.
     * 
     * @param ccVersion version id
     * @param configItems configuration item list
     */
    public void importConfigItems(long ccVersion, Map<String, String> configItems) {
        StopWatch sw = new StopWatch();
        sw.start();
        configServerService.importConfigItems(ccUser, ccPassword, ccVersion, configItems);
        long timetook = sw.stop();
        logRpcTimeTook(timetook, "importConfigItems");
    }
    
    /**
     * To check version tag is update or not.
     * @return true if version tag is same.
     */
    protected boolean checkVersionTag() {
        StopWatch sw = new StopWatch();
        sw.start();
        boolean ret = configServerService.checkVersionTag(ccUser, ccPassword, 
                ccVersion, versionTag);
        long timetook = sw.stop();
        logRpcTimeTook(timetook, "checkVersionTag");       
        return ret;
    }
    
    /**
     * get configuration item changed callable instances.
     * @return the callables
     */
    public Map<String, ConfigItemChangedCallable> getCallables() {
        return callables;
    }

    /**
     * set configuration item changed callable instances.
     * @param callables the callables to set
     */
    public void setCallables(Map<String, ConfigItemChangedCallable> callables) {
        if (callables != null) {
            this.callables.putAll(callables);
        }
    }
    
    /**
     * stop call back listening
     */
    public synchronized void stop() {
        if (configChangedListener != null) {
            configChangedListener.close();
        }
    }


    /**
     * start call back listening.
     * 
     * @param props current configuration item set.
     */
    public synchronized void startListening(Properties props) {
        if (configChangedListener == null) {
            configChangedListener = new ConfigChangedListener(props, this);
        }
        
        if (configChangedListener.isStop()) {
            configChangedListener.start();
        }
    }

    
    /**
     * Read property from local resource file
     * 
     * @param props to merge from local
     * @param localFile local resource file
     * @throws IOException throw all file operation exception
     */
    public void readLocal(Properties props, File localFile) throws IOException {
        Assert.notNull(localFile, "Property 'localFile' is null.");
        if (!localFile.exists()) {
            throw new IOException("File not exist. " + localFile.getPath());
        }
        
        byte[] byteArray = FileUtils.readFileToByteArray(localFile);
        Hex encoder = new Hex();
        try {
            byteArray = encoder.decode(byteArray);
        } catch (DecoderException e) {
            throw new IOException(e.getMessage());
        }
        
        String json = new String(byteArray, FILE_ENCODE);
        
        Map<String, String> map = gson.fromJson(json, 
                new TypeToken<Map<String, String>>() {}.getType()); 
        
        setVersionTag(map);
        props.putAll(map);
    }
    
    /**
     * Write property content to local resource file.
     * 
     * @param configItems latest configuration items
     * @param localFile loca resource file
     * @throws IOException throw all file operation exception
     */
    public void writeLocal(Map<String, String> configItems, File localFile) throws IOException {
        Assert.notNull(localFile, "Property 'localFile' is null.");
        if (!localFile.exists()) {
            throw new IOException("File not exist. " + localFile.getPath());
        }
        
        String json = gson.toJson(configItems);
        //to byte array
        byte[] byteArray = json.getBytes(FILE_ENCODE);
        
        Hex encoder = new Hex();
        byteArray = encoder.encode(byteArray);
        
        FileUtils.writeByteArrayToFile(localFile, byteArray);
    }


    /**
     * If version tag changed will update latest configuration items.
     * 
     * @param configItems latest configuration items
     */
    public void onLatestUpdate(Map<String, String> configItems) {
        if (localPropertyFile == null) {
            return;
        }
        
        try {
            writeLocal(configItems, localPropertyFile);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
    
    /**
     * create {@link ConfigLoader} instance.
     * 
     * @param propertySupport {@link ConfigLoaderPropertySupport}
     * @return {@link ConfigLoader} instance.
     */
    public static ConfigLoader createConfigLoader(ConfigLoaderPropertySupport propertySupport) {
      //create configservice 
        ExtConfigServerService service;
        service = createConfigService(propertySupport.getCcServerUrl(), 
                propertySupport.getConnectionTimeout(), propertySupport.getReadTimeout());
        
        ConfigLoader configLoader = new ConfigLoader();
        configLoader.setCcVersionName(propertySupport.getCcVersionName());
        configLoader.setCcUser(propertySupport.getCcUser());
        configLoader.setCcPassword(propertySupport.getCcPassword());
        configLoader.setCallbackInteval(propertySupport.getCallbackInteval());
        
        configLoader.setConfigServerService(service);
        configLoader.init();
        
        //get configruation items from server
        Map<String, String> configItems = configLoader.getConfigItems();
        
        //add call back listener here
        configLoader.getCallables().put("cust_listener", propertySupport);
        
        //以当前的数据为基准进行更新支持
        Properties properties = new Properties();
        properties.putAll(configItems);
        configLoader.setVersionTag(configItems);
        
        propertySupport.propertiesLoad(configItems);
        
        if (propertySupport.isEnableUpdateCallback()) {
            configLoader.startListening(properties);
        }
        
        return configLoader;
    }
    
    /**
     * Create {@link ExtConfigServerService} proxy instance.
     * 
     * @param ccServerUrl configuration center server URL.
     * @param connectionTimeout connection time out
     * @param readTimeout read time out
     * @return {@link ExtConfigServerService} proxy instance.
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
}
