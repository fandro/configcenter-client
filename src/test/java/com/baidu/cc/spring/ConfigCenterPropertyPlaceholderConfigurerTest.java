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

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.io.Resource;

import com.baidu.cc.ConfigLoader;
import com.baidu.cc.interfaces.ChangedConfigItem;
import com.baidu.cc.interfaces.ConfigItemChangedCallable;
import com.baidu.cc.interfaces.ConfigServerService;
import com.baidu.cc.interfaces.ExtConfigServerService;

/**
 * Test for ConfigCenterPropertyPlaceholderConfigurer
 * 
 * @author xiemalin
 * @since 1.0.0.0
 */
public class ConfigCenterPropertyPlaceholderConfigurerTest {
    /**
     * value3 of key3
     */
    private static final String VALUE2SFDSFDSFDS = "value2sfdsfdsfds";

    /**
     * value3 of key2
     */
    private static final String VALUE2 = "value2";

    /**
     * value1 of key1
     */
    private static final String VALUE1 = "value1";

    /**
     * key3 name
     */
    private static final String KEY3 = "key3";

    /**
     * key2 name
     */
    private static final String KEY2 = "key2";

    /**
     * key1 name
     */
    private static final String KEY1 = "key1";

    /**
     * password value
     */
    private static final String PASSWORD2 = "password";

    /**
     * user name
     */
    private static final String USER2 = "user";

    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger
            .getLogger(ConfigCenterPropertyPlaceholderConfigurerTest.class);
    
    /**
     * tag value for test
     */
    private static final String TEST_TAG_VALUE = "__test_tag_value__";
    /**
     * new tag value for test
     */
    private static final String TEST_TAG_NEW_VALUE = "__test_tag_new_value__";
    
    /**
     * current tag value
     */
    private String tag = TEST_TAG_VALUE;
    
    /**
     * all loaded properties
     */
    protected Map<String, String> props = new HashMap<String, String>();

    /**
     * Do authenticate by user name and password. if authenticate failed will mark failed 
     * at current unit test
     * @param user use name
     * @param password password
     */
    private void authenticate(String user, String password) {
        boolean b = USER2.equals(user);
        
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(ExtConfigServerService.class.getSimpleName());
        
        b = b && PASSWORD2.equals(encryptor.decrypt(password));
        
        if (!b) {
            Assert.fail("authenticate failed. user name should be 'user' " +
                "and password should be password");
        }
    }

    /**
     * inner class of {@link ExtConfigServerService} implement.
     */
    ExtConfigServerService configServerServiceMock = new ExtConfigServerService() {
        
        /* (non-Javadoc)
         * @see com.baidu.cc.interfaces.ConfigServerService#getLastestConfigItems(java.lang.String, java.lang.String, java.lang.Long)
         */
        public Map<String, String> getLastestConfigItems(String arg0, String arg1,
                Long arg2) {
            authenticate(arg0, arg1);
            return null;
        }
        
        /* (non-Javadoc)
         * @see com.baidu.cc.interfaces.ConfigServerService#getConfigItems(java.lang.String, java.lang.String, java.lang.Long)
         */
        public Map<String, String> getConfigItems(String arg0, String arg1,
                Long arg2) {
            authenticate(arg0, arg1);
            Assert.assertEquals(arg2, Long.valueOf(1));
            
            return props;
        }
        
        /* (non-Javadoc)
         * @see com.baidu.cc.interfaces.ConfigServerService#getConfigItemValue(java.lang.String, java.lang.String, java.lang.Long, java.lang.String)
         */
        public String getConfigItemValue(String arg0, String arg1, Long arg2,
                String arg3) {
            authenticate(arg0, arg1);
            
            return null;
        }
        
        /* (non-Javadoc)
         * @see com.baidu.cc.interfaces.ConfigServerService#checkVersionTag(java.lang.String, java.lang.String, java.lang.Long, java.lang.String)
         */
        public boolean checkVersionTag(String arg0, String arg1, Long arg2,
                String arg3) {
            authenticate(arg0, arg1);
            Assert.assertEquals(arg2, Long.valueOf(1));
            return tag.equals(arg3);
        }

        /* (non-Javadoc)
         * @see com.baidu.cc.interfaces.ConfigServerService#getLastestConfigVersion(java.lang.String, java.lang.String, java.lang.Long)
         */
        public Long getLastestConfigVersion(String user, String password,
                Long envId) {
            Assert.assertEquals(Long.valueOf(1L), envId);
            
            return 1L;
        }
        
        /* (non-Javadoc)
         * @see com.baidu.cc.interfaces.ExtConfigServerService#importConfigItems(java.lang.String, java.lang.String, java.lang.Long, java.util.Map)
         */
        public void importConfigItems(String user, String password, Long version,
                Map<String, String> configItems) {
            
        }

        /* (non-Javadoc)
         * @see com.baidu.cc.interfaces.ConfigServerService#getLastestConfigVersion(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
         */
        public Long getLastestConfigVersion(String user, String password,
                String projectName, String envName) {
            Assert.assertEquals("pName", projectName);
            Assert.assertEquals("eName", envName);
            return 1L;
        }

        /* (non-Javadoc)
         * @see com.baidu.cc.interfaces.ConfigServerService#getVersionId(java.lang.String, java.lang.String, java.lang.String)
         */
        public Long getVersionId(String user, String password,
                String versionName) {
            return null;
        }
    };
    
    /**
     * test local resource function.
     * 
     * @throws Exception all exception will throw out and mark failed at unit test.
     */
    @Test 
    public void testLocalResource() throws Exception {
        //local resource
        URL resource = getClass().getResource("/com/baidu/cc/spring/LocalProperty");
        
        //write property
        Map<String, String> ret = new HashMap<String, String>();
        ret.put(ConfigServerService.TAG_KEY, TEST_TAG_VALUE);
        ret.put(KEY1, VALUE1);
        ret.put(KEY2, VALUE2);
        ret.put(KEY3, VALUE2SFDSFDSFDS);
        
        ConfigLoader configLoader = new ConfigLoader();
        configLoader.writeLocal(ret, new File(resource.getPath()));
        
      
        ConfigCenterPropertyPlaceholderConfigurer ccpc;
        ccpc = new ConfigCenterPropertyPlaceholderConfigurer();
        ccpc.setLogProperties(true);
        
        ccpc.setCcVersion(1L);
        
        ccpc.setCcServerUrl("http://localhost:8180/abcrpc/ConfigServerService");
        ccpc.setCcUser(USER2);
        ccpc.setCcPassword(PASSWORD2);
        ccpc.setEnableUpdateCallback(true);
        ccpc.setCallbackInteval(100L);
        
        ccpc.setLocalResource(resource.getPath());
        
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        ccpc.setApplicationContext(appContext);
        ccpc.postProcessBeanFactory(beanFactory);
        
        Assert.assertEquals(3, ccpc.cachedProps.size());
        Assert.assertTrue(ccpc.cachedProps.containsKey(KEY1));
        Assert.assertTrue(ccpc.cachedProps.containsKey(KEY2));
        
        try {
            Thread.sleep(1000L);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
    
    /**
     * test get version id by environment id.
     * 
     * @throws Exception all exception will throw out and mark failed at unit test.
     */
    @Test
    public void testGetVersionIDByEnvId() throws Exception {
      //prepare data
        Map<String, String> ret = new HashMap<String, String>();
        ret.put(ConfigServerService.TAG_KEY, TEST_TAG_VALUE);
        
        ret.put(KEY1, VALUE1);
        ret.put(KEY2, VALUE2);
        props = ret;
        
        ConfigCenterPropertyPlaceholderConfigurer ccpc;
        ccpc = new ConfigCenterPropertyPlaceholderConfigurer();
        
        ccpc.setCcServerUrl("http://localhost:8080/rpc/ConfigServerService");
        ccpc.setCcUser(USER2);
        ccpc.setCcPassword(PASSWORD2);
        ccpc.setEnableUpdateCallback(true);
        ccpc.setCallbackInteval(100L);
        
        ccpc.setCcEnvId(1L);
        
        ccpc.setConfigServerService(configServerServiceMock);
        
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        ccpc.setApplicationContext(appContext);
        ccpc.postProcessBeanFactory(beanFactory);
        
    }   
    
    /**
     * test get version by project name and environment name
     * @throws Exception all exception will throw out and mark failed at unit test.
     */
    @Test
    public void testGetVersionByProjectNameAndEvnName() throws Exception {
        
        //prepare data
        Map<String, String> ret = new HashMap<String, String>();
        ret.put(ConfigServerService.TAG_KEY, TEST_TAG_VALUE);
        
        ret.put(KEY1, VALUE1);
        ret.put(KEY2, VALUE2);
        props = ret;
        
        ConfigCenterPropertyPlaceholderConfigurer ccpc;
        ccpc = new ConfigCenterPropertyPlaceholderConfigurer();
        
        ccpc.setCcVersion(1L);
        
        ccpc.setCcServerUrl("http://localhost:8080/rpc/ConfigServerService");
        ccpc.setCcUser(USER2);
        ccpc.setCcPassword(PASSWORD2);
        ccpc.setEnableUpdateCallback(true);
        ccpc.setCallbackInteval(100L);
        
        ccpc.setProjectName("pName");
        ccpc.setEnvName("eName");
        
        ccpc.setConfigServerService(configServerServiceMock);
        
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        ccpc.setApplicationContext(appContext);
        ccpc.postProcessBeanFactory(beanFactory);
    }
    
 
    
    /**
     * test get version
     * 
     * @throws Exception all exception will throw out and mark failed at unit test.
     */
    @Test
    public void testGetFromVersion() throws Exception {
        
        //prepare data
        Map<String, String> ret = new HashMap<String, String>();
        ret.put(ConfigServerService.TAG_KEY, TEST_TAG_VALUE);
        
        ret.put(KEY1, VALUE1);
        ret.put(KEY2, VALUE2);
        props = ret;
        
        ConfigCenterPropertyPlaceholderConfigurer ccpc;
        ccpc = new ConfigCenterPropertyPlaceholderConfigurer();
        
        ccpc.setCcVersion(1L);
        
        ccpc.setCcServerUrl("http://localhost:8080/rpc/ConfigServerService");
        ccpc.setCcUser(USER2);
        ccpc.setCcPassword(PASSWORD2);
        ccpc.setEnableUpdateCallback(true);
        ccpc.setCallbackInteval(100L);
        
        ccpc.setConfigServerService(configServerServiceMock);
        
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        ccpc.setApplicationContext(appContext);
        ccpc.postProcessBeanFactory(beanFactory);
        
        Assert.assertEquals(2, ccpc.cachedProps.size());
        Assert.assertTrue(ccpc.cachedProps.containsKey(KEY1));
        Assert.assertTrue(ccpc.cachedProps.containsKey(KEY2));
        
        Assert.assertEquals(VALUE1, ccpc.cachedProps.getProperty(KEY1));
        Assert.assertEquals(VALUE2, ccpc.cachedProps.getProperty(KEY2));
        //assert property check
        
        try {
            Thread.sleep(500L);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        
        ret = new HashMap<String, String>();
        ret.put(ConfigServerService.TAG_KEY, TEST_TAG_NEW_VALUE);
        
        ret.put(KEY1, VALUE1);
        ret.put(KEY2, "value22");
        ret.put(KEY3, "value3");
        tag = TEST_TAG_NEW_VALUE;
        props = ret;
        
        try {
            Thread.sleep(1000L);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        Assert.assertEquals(2, ccpc.cachedProps.size());
        Assert.assertTrue(ccpc.cachedProps.containsKey(KEY1));
        Assert.assertTrue(ccpc.cachedProps.containsKey(KEY2));
        
        Assert.assertEquals(VALUE1, ccpc.cachedProps.getProperty(KEY1));
        Assert.assertEquals(VALUE2, ccpc.cachedProps.getProperty(KEY2));
        ccpc.destroy();
        
    }
    
    /**
     * inner class to implements of {@link ConfigItemChangedCallable}
     */
    final ConfigItemChangedCallable callable = new ConfigItemChangedCallable() {
        
        public void changed(List<ChangedConfigItem> changedConfigItemList) {
            
            LOGGER.info("change config item:##################");
            for (ChangedConfigItem item : changedConfigItemList) {
                LOGGER.info("key=" + item.getKey() + ",old=" + item.getOldValue() + 
                        ",new=" + item.getNewValue());
            }
            
        }
    };
    
    
    /**
     *  inner class to implements of {@link ApplicationContext}
     */
    public ApplicationContext appContext = new ApplicationContext() {
        
        /* (non-Javadoc)
         * @see org.springframework.core.io.ResourceLoader#getResource(java.lang.String)
         */
        public Resource getResource(String location) {
            return null;
        }
        
        /* (non-Javadoc)
         * @see org.springframework.core.io.ResourceLoader#getClassLoader()
         */
        public ClassLoader getClassLoader() {
            return null;
        }
        
        /* (non-Javadoc)
         * @see org.springframework.core.io.support.ResourcePatternResolver#getResources(java.lang.String)
         */
        public Resource[] getResources(String locationPattern) throws IOException {
            return null;
        }
        
        /* (non-Javadoc)
         * @see org.springframework.context.ApplicationEventPublisher#publishEvent(org.springframework.context.ApplicationEvent)
         */
        public void publishEvent(ApplicationEvent event) {
            
        }
        
        /* (non-Javadoc)
         * @see org.springframework.context.MessageSource#getMessage(java.lang.String, java.lang.Object[], java.lang.String, java.util.Locale)
         */
        public String getMessage(String code, Object[] args, String defaultMessage,
                Locale locale) {
            return null;
        }
        
        /* (non-Javadoc)
         * @see org.springframework.context.MessageSource#getMessage(java.lang.String, java.lang.Object[], java.util.Locale)
         */
        public String getMessage(String code, Object[] args, Locale locale)
            throws NoSuchMessageException {
            return null;
        }
        
        /* (non-Javadoc)
         * @see org.springframework.context.MessageSource#getMessage(org.springframework.context.MessageSourceResolvable, java.util.Locale)
         */
        public String getMessage(MessageSourceResolvable resolvable, Locale locale)
            throws NoSuchMessageException {
            return null;
        }
        
        /* (non-Javadoc)
         * @see org.springframework.beans.factory.HierarchicalBeanFactory#getParentBeanFactory()
         */
        public BeanFactory getParentBeanFactory() {
            return null;
        }
        
        /* (non-Javadoc)
         * @see org.springframework.beans.factory.HierarchicalBeanFactory#containsLocalBean(java.lang.String)
         */
        public boolean containsLocalBean(String name) {
            return false;
        }
        
        /* (non-Javadoc)
         * @see org.springframework.beans.factory.BeanFactory#isTypeMatch(java.lang.String, java.lang.Class)
         */
        public boolean isTypeMatch(String name, Class targetType)
            throws NoSuchBeanDefinitionException {
            return false;
        }
        
        /* (non-Javadoc)
         * @see org.springframework.beans.factory.BeanFactory#isSingleton(java.lang.String)
         */
        public boolean isSingleton(String name)
            throws NoSuchBeanDefinitionException {
            return false;
        }
        
        /* (non-Javadoc)
         * @see org.springframework.beans.factory.BeanFactory#isPrototype(java.lang.String)
         */
        public boolean isPrototype(String name)
            throws NoSuchBeanDefinitionException {
            return false;
        }
        
        /* (non-Javadoc)
         * @see org.springframework.beans.factory.BeanFactory#getType(java.lang.String)
         */
        public Class getType(String name) throws NoSuchBeanDefinitionException {
            return null;
        }
        
        /* (non-Javadoc)
         * @see org.springframework.beans.factory.BeanFactory#getBean(java.lang.String, java.lang.Object[])
         */
        public Object getBean(String name, Object[] args) throws BeansException {
            return null;
        }
        
        /* (non-Javadoc)
         * @see org.springframework.beans.factory.BeanFactory#getBean(java.lang.String, java.lang.Class)
         */
        public Object getBean(String name, Class requiredType)
            throws BeansException {
            return null;
        }
        
        /* (non-Javadoc)
         * @see org.springframework.beans.factory.BeanFactory#getBean(java.lang.String)
         */
        public Object getBean(String name) throws BeansException {
            return null;
        }
        
        /* (non-Javadoc)
         * @see org.springframework.beans.factory.BeanFactory#getAliases(java.lang.String)
         */
        public String[] getAliases(String name) {
            return null;
        }
        
        /* (non-Javadoc)
         * @see org.springframework.beans.factory.BeanFactory#containsBean(java.lang.String)
         */
        public boolean containsBean(String name) {
            return false;
        }
        
        /* (non-Javadoc)
         * @see org.springframework.beans.factory.ListableBeanFactory#getBeansOfType(java.lang.Class, boolean, boolean)
         */
        public Map getBeansOfType(Class type, boolean includeNonSingletons,
                boolean allowEagerInit) throws BeansException {
            return null;
        }
        
        /* (non-Javadoc)
         * @see org.springframework.beans.factory.ListableBeanFactory#getBeansOfType(java.lang.Class)
         */
        public Map getBeansOfType(Class type) throws BeansException {
            if (ConfigItemChangedCallable.class.equals(type)) {
                Map map = new HashMap();
                map.put("callable", callable);
                
                return map;
            }
            
            
            return null;
        }
        
        /* (non-Javadoc)
         * @see org.springframework.beans.factory.ListableBeanFactory#getBeanNamesForType(java.lang.Class, boolean, boolean)
         */
        public String[] getBeanNamesForType(Class type,
                boolean includeNonSingletons, boolean allowEagerInit) {
            return null;
        }
        
        /* (non-Javadoc)
         * @see org.springframework.beans.factory.ListableBeanFactory#getBeanNamesForType(java.lang.Class)
         */
        public String[] getBeanNamesForType(Class type) {
            return null;
        }
        
        /* (non-Javadoc)
         * @see org.springframework.beans.factory.ListableBeanFactory#getBeanDefinitionNames()
         */
        public String[] getBeanDefinitionNames() {
            return null;
        }
        
        /* (non-Javadoc)
         * @see org.springframework.beans.factory.ListableBeanFactory#getBeanDefinitionCount()
         */
        public int getBeanDefinitionCount() {
            return 0;
        }
        
        /* (non-Javadoc)
         * @see org.springframework.beans.factory.ListableBeanFactory#containsBeanDefinition(java.lang.String)
         */
        public boolean containsBeanDefinition(String beanName) {
            return false;
        }
        
        /* (non-Javadoc)
         * @see org.springframework.context.ApplicationContext#getStartupDate()
         */
        public long getStartupDate() {
            return 0;
        }
        
        /* (non-Javadoc)
         * @see org.springframework.context.ApplicationContext#getParent()
         */
        public ApplicationContext getParent() {
            return null;
        }
        
        /* (non-Javadoc)
         * @see org.springframework.context.ApplicationContext#getId()
         */
        public String getId() {
            return null;
        }
        /* (non-Javadoc)
         * @see org.springframework.context.ApplicationContext#getDisplayName()
         */
        public String getDisplayName() {
            return null;
        }
        
        /* (non-Javadoc)
         * @see org.springframework.context.ApplicationContext#getAutowireCapableBeanFactory()
         */
        public AutowireCapableBeanFactory getAutowireCapableBeanFactory()
            throws IllegalStateException {
            return null;
        }
    };
}
