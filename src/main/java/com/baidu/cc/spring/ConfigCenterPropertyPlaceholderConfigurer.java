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
package com.baidu.cc.spring;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.util.Assert;

import com.baidu.bjf.remoting.mcpack.OperationTimeoutMcpackRpcProxyFactoryBean;
import com.baidu.cc.BatchConfigItemChangedCallable;
import com.baidu.cc.ConfigCenterClient;
import com.baidu.cc.ConfigLoader;
import com.baidu.cc.ConfigQueryManagerAware;
import com.baidu.cc.Constants;
import com.baidu.cc.interfaces.ConfigChangeManager;
import com.baidu.cc.interfaces.ConfigItemChangedCallable;
import com.baidu.cc.interfaces.ExtConfigServerService;
import com.baidu.rpc.exception.InternalErrorException;

/**
 * A extend utility class for spring property configuration resolve from configuration
 * center server.<br>
 * 
 * <pre>
 * {@code
 * example usage in spring:
 * <bean class="com.baidu.cc.spring.ConfigCenterPropertyPlaceholderConfigurer">
 *   <property name="callbackInteval" value="500"></property>
 *   <property name="ccServerUrl" 
 *       value="http://localhost:8080/rpc/ConfigService"></property>
 *   <property name="ccUser" value="user"></property>
 *   <property name="ccPassword" value="password"></property>
 *   <property name="ccVersion" value="1"></property>
 *   <property name="enableUpdateCallback" value="false"></property>
 *   <property name="localResource" value="/local/localPropFile"></property>
 * </bean>
 * }
 * </pre>
 * 
 * @author xiemalin
 * @since 1.0.0.0
 */
public class ConfigCenterPropertyPlaceholderConfigurer extends
    PropertyPlaceholderConfigurer implements ApplicationContextAware, 
    DisposableBean, ApplicationListener, ConfigChangeManager, 
    ConfigQueryManager {

    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger
            .getLogger(ConfigCenterPropertyPlaceholderConfigurer.class);
    
    /**
     * local cached props
     */
    protected Properties cachedProps;
    
    /**
     * local cached props load from configuration center
     */
    protected Properties ccLoadedProps;
    
    /**
     * set if enable update call back
     */
    private boolean enableUpdateCallback = false;
    
    /**
     * connection time out value to connect configuration center server.
     */
    private int connectionTimeout = ConfigCenterClient.DEFAULT_CONNECTION_TIMEOUT;
    
    /**
     * set read time out value on reading value from configuration center server.
     */
    private int readTimeout = ConfigCenterClient.DEFAULT_READ_TIMEOUT;
    
    /**
     * set call back listen call back interval
     */
    private long callbackInteval = ConfigCenterClient.DEFAULT_CALLBACK_INTERVAL;
    
    /**
     * set call back listen call back interval
     * @param callbackInteval the callbackInteval to set
     */
    public void setCallbackInteval(long callbackInteval) {
        this.callbackInteval = callbackInteval;
    }

    /**
     * configServerService client proxy bean.
     */
    private OperationTimeoutMcpackRpcProxyFactoryBean proxy;

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
     * request configuration environment id under specified version.
     */
    private long ccEnvId;
    
    /**
     * local property file resource. if not null will loaded on configuration center connect failed.
     */
    private String localResource;
    
    /**
     * if log out loaded properties
     */
    private boolean logProperties = false;
    
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
     * set logProperties
     * @param logProperties the logProperties to set
     */
    public void setLogProperties(boolean logProperties) {
        this.logProperties = logProperties;
    }


    /**
     * set  local resource path
     * @param localResource the localResource to set
     */
    public void setLocalResource(String localResource) {
        this.localResource = localResource;
    }

    /**
     * local property file.
     */
    private File localResourceFile;
    
    /**
     * configuration server client instance.
     */
    private ExtConfigServerService configServerService;
    
    /**
     *  get {@link ExtConfigServerService} instance
     * @return the extConfigServerService
     */
    public ExtConfigServerService getConfigServerService() {
        return configServerService;
    }


    /**
     * set {@link ExtConfigServerService} instance
     * @param extConfigServerService the extConfigServerService to set
     */
    public void setConfigServerService(
            ExtConfigServerService extConfigServerService) {
        this.configServerService = extConfigServerService;
    }


    /**
     * configuration loader instance
     */
    private ConfigLoader configLoader;
    
    /**
     * spring bean factory
     */
    private ApplicationContext applicationContext;
    

    /**
     * get configuration loader instance
     * 
     * @return the configLoader
     */
    public ConfigLoader getConfigLoader() {
        return configLoader;
    }


    /**
     * set server url
     * @param ccServerUrl the ccServerUrl to set
     */
    public void setCcServerUrl(String ccServerUrl) {
        this.ccServerUrl = ccServerUrl;
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
        this.ccPassword = ccPassword;
    }


    /**
     * set version
     * @param ccVersion the ccVersion to set
     */
    public void setCcVersion(long ccVersion) {
        this.ccVersion = ccVersion;
    }


    /**
     * set version name
     * @param ccVersionName the ccVersionName to set
     */
    public void setCcVersionName(String ccVersionName) {
        this.ccVersionName = ccVersionName;
    }


    /**
     * set environment id
     * @param ccEnvId the ccEnvId to set
     */
    public void setCcEnvId(long ccEnvId) {
        this.ccEnvId = ccEnvId;
    }


    /**
     * set read time out value
     * @param readTimeout the readTimeout to set
     */
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }


    /**
     * set connection time out value
     * @param connectionTimeout the connectionTimeout to set
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }


    /**
     * set enableUpdateCallback value
     * 
     * @param enableUpdateCallback value
     */
    public void setEnableUpdateCallback(boolean enableUpdateCallback) {
        this.enableUpdateCallback = enableUpdateCallback;
    }
    
    /**
     * create a new {@link ConfigServerService} instance by proxy.
     */
    protected void createConfigServerService() {
        if (configServerService == null) {
            //create a new one
            ccServerUrl = Constants.getServerUrl(ccServerUrl);
            Assert.hasLength(ccServerUrl, "property 'ccServerUrl' is blank.");
            
            proxy = new OperationTimeoutMcpackRpcProxyFactoryBean();
            proxy.setServiceUrl(ccServerUrl);
            proxy.setServiceInterface(ExtConfigServerService.class);
            proxy.setConnectionTimeout(connectionTimeout);
            proxy.setSoTimeout(readTimeout);
            
            //do initial
            proxy.afterPropertiesSet();
            
            configServerService = (ExtConfigServerService) proxy.getObject();
            
        }
    }
    
    /**
     * check configuraiton item is valid
     */
    protected void checkValid() {
        
        createConfigServerService();
        
        if (configLoader == null) {
            configLoader = new ConfigLoader();
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
        }
        
        if (StringUtils.isNotBlank(localResource)) {
            localResourceFile = new File(localResource);
            configLoader.setLocalPropertyFile(localResourceFile);
        }
        
    }
    
    /**
     * if properties already loaded will ignore repeated load.
     * 
     * @param props loaded properties.
     * @throws IOException IO exception
     */
    @Override
    protected void loadProperties(Properties props) throws IOException {
        if (cachedProps != null) {
            props.putAll(cachedProps);
            return;
        }
        
        super.loadProperties(props);
    }

    /**
     * begin to load properties from configuration center server
     * @param props target propterties to merge from configuration center server
     * @throws RuntimeException in case of any properties load failed exception.
     */
    @Override
    protected void convertProperties(Properties props) {
        // check property
        checkValid();

        // load configuration item info from configuration center
        try {
            loadProptiesFromCC(props);
        } catch (InternalErrorException e) {
            // here is a error on connect to server
            if (localResourceFile == null) {
                LOGGER.error(e.getMessage(), e);
                throw new RuntimeException(e.getCause().getMessage(), e);
            }
            // try to load from local resource
            try {
                LOGGER.warn("Connect to server failed try to read from local resource file:"
                        + localResource);
                configLoader.readLocal(props, localResourceFile);
            } catch (Exception e2) {
                throw new RuntimeException(e2.getCause().getMessage(), e2);
            }
        }

        cachedProps = props;
        super.convertProperties(props);
    }
    
    /**
     * initialize configuration center bean load actions.
     * 
     * @param beanFactory Spring bean factory instance.
     * @throws BeansException throw out bean initialize excpetion to spring container.
     */
    @Override
    public void postProcessBeanFactory(
            ConfigurableListableBeanFactory beanFactory) throws BeansException {
        super.postProcessBeanFactory(beanFactory);
        
        if (logProperties) {
            doLogLoadedProperties(cachedProps);
        }
        
        ConfigQueryManagerHelper.setConfigQueryManager(this);
        
        initConfigQueryManagerAware();
        
        //start call back listening thread
        if (enableUpdateCallback) {
            Map callbackBeans = applicationContext.getBeansOfType(ConfigItemChangedCallable.class);

            if (callbackBeans != null) {
                configLoader.setCallables(callbackBeans);
            }
            configLoader.startListening(ccLoadedProps);
        }
        
    }
    
    /**
     * initialize {@link ConfigQueryManagerAware} interface call back
     */
    protected void initConfigQueryManagerAware() {
        Map beansMap = applicationContext.getBeansOfType(ConfigQueryManagerAware.class);
        if (beansMap == null) {
            return;
        }
        
        Iterator<Map.Entry<String, Object>> iterator = beansMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, Object> next = iterator.next();
            Object bean = next.getValue();
            
            ((ConfigQueryManagerAware) bean).setConfigQueryManager(this);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Inject ConfigQueryManagerAware interface for bean name '" + next.getKey() + "'");
            }
        }
    }

    /**
     * log out loaded properties.<br>
     * For safety consider all value will print out only include partial string.
     * example a result "key=jdbcpassword" will finally print out as "key=jdbcpa..."
     * 
     * @param props properties to log out
     */
    protected void doLogLoadedProperties(Properties props) {
        
        Iterator<Entry<Object, Object>> iterator = props.entrySet().iterator();
        StringBuilder logContent = new StringBuilder();
        logContent.append("Loaded property list:\n");
        while (iterator.hasNext()) {
            Entry<Object, Object> next = iterator.next();
            logContent.append(next.getKey()).append("=");
            
            String value = String.valueOf(next.getValue());
            if (value == null) {
                logContent.append("\n");
                continue;
            }
            
            int length = value.length();
            if (length < 2) {
                logContent.append("...");
            } else {
                int sub = length / 2;
                logContent.append(StringUtils.substring(value, 0, sub));
                logContent.append("...");
            }
            
            logContent.append("\n");
            
        }
        
        LOGGER.info(logContent);
    }


    /**
     * print callable beans info
     */
    private void printCallbackBeansLog() {
        if (enableUpdateCallback && configLoader.getCallables() != null) {
            if (LOGGER.isInfoEnabled()) {
                
                Map<String, ConfigItemChangedCallable> callables = configLoader.getCallables();
                List<String> beanNames = new ArrayList<String>();
                Iterator<Entry<String, ConfigItemChangedCallable>> iter = callables.entrySet().iterator();
                while (iter.hasNext()) {
                    Entry<String, ConfigItemChangedCallable> next = iter.next();
                    
                    ConfigItemChangedCallable c = next.getValue();
                    if (c instanceof BatchConfigItemChangedCallable) {
                        BatchConfigItemChangedCallable bc = (BatchConfigItemChangedCallable) c;
                        beanNames.addAll(bc.getCallableBeanNames());
                    } else {
                        beanNames.add(next.getKey());
                    } 
                }
                
                LOGGER.info("Follow spring beans will listening configuration item changed call back:" +
                        beanNames);
            }
        }
    }


    /**
     * Load configuration properties from configuration center server
     * 
     * @param props properties to merge.
     */
    private void loadProptiesFromCC(Properties props) {
       
        Map<String, String> configItems = configLoader.getConfigItems();
        //do merge
        if (configItems != null) {
            //set tag value
            configLoader.setVersionTag(configItems);
            
            //set to cached config
            ccLoadedProps = new Properties();
            ccLoadedProps.putAll(configItems);
            
            props.putAll(configItems);
            
        }
    }
    
    


    /**
     * set {@link ApplicationContext} instance after it created in spring container.<br>
     * this method is call back by {@link ApplicationContextAware} interface.<br>
     * @param applicationContext spring application context instance
     * @throws BeansException bean create exception
     */
    public void setApplicationContext(ApplicationContext applicationContext)
        throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * Stop {@link ConfigLoader} listener and destroy proxy bean on spring container closing.
     * 
     * @throws Exception throws any exception.
     */
    public void destroy() throws Exception {
        if (configLoader != null) {
            configLoader.stop();
        }
        
        if (proxy != null) {
            proxy.destroy();
        }
    }

    /**
     * listen all spring application event
     * @param event spring application event
     */
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextStartedEvent) {
            printCallbackBeansLog();
        }
        
    }

    /**
     * add a {@link ConfigItemChangedCallable} instance to manager by name.
     * 
     * @param name unique name
     * @param callable {@link ConfigItemChangedCallable} instance 
     */
    public void addConfigItemChangeCallable(String name,
            ConfigItemChangedCallable callable) {
        if (getConfigLoader() != null) {
            getConfigLoader().getCallables().put(name, callable);
        }
    }

    /**
     * Get all loaded properties.
     * @return loaded properties
     */
    public Map<String, String> getProperties() {
        if (cachedProps == null) {
            return Collections.emptyMap();
        }
        Map<String, String> ret = new HashMap(cachedProps);
        return ret;
    }


}
