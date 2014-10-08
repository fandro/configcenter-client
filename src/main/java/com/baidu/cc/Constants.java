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

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * 常量工具类
 * 
 * @author xiemalin
 * @since 1.0.0.0
 */
public final class Constants {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(Constants.class);

    /**
     * 设置配置中心服务url -DccServerUrl=
     */
    public static final String ENV_SERVER_URL = "ccServerUrl";
    /**
     * 设置配置中心用户名  -DccUser=
     */   
    public static final String ENV_USER = "ccUser";
    
    /**
     * 设置配置中心密码 -DccPassword=
     */   
    public static final String ENV_PASSWORD = "ccPassword";

    /**
     * 设置配置中心版本号 -DccVersion=
     */   
    public static final String ENV_VERSION = "ccVersion";

    /**
     * 设置配置中心版本名称 -DccVersionName=
     */   
    public static final String ENV_VERSION_NAME = "ccVersionName";
    
    /**
     * 设置配置中心运行环境id -DccEnvId=
     */   
    public static final String ENV_ENVID = "ccEnvId";
    
    /**
     * 设置配置中心工程名称-DccProject=
     */   
    public static final String ENV_PROJECT = "ccProject";
    
    /**
     * 设置配置中心运行环境名称-DccEnvName=
     */   
    public static final String ENV_ENVNAME = "ccEnvName";
    
    
    /**
     * get server url value.
     * 
     * @param serverUrl return this value if not exist in the system property
     * @return server url value.
     */
    public static String getServerUrl(String serverUrl) {
        return getSystemProp(ENV_SERVER_URL, serverUrl);
    }

    /**
     * get user name value.
     * 
     * @param user return this value if not exist in the system property
     * @return user name.
     */
    public static String getUser(String user) {
        return getSystemProp(ENV_USER, user);
    }
 
    /**
     * get user password value.
     * 
     * @param password return this value if not exist in the system property
     * @return user password.
     */
    public static String getPassword(String password) {
        return getSystemProp(ENV_PASSWORD, password);
    }

    /**
     * get configuration version id value.
     * 
     * @param version return this value if not exist in the system property
     * @return configuration version value.
     */
    public static long getVersion(String version) {
        return Long.valueOf(getSystemProp(ENV_VERSION, version));
    }

    /**
     * get configuration version id value.
     * 
     * @param version return this value if not exist in the system property
     * @return configuration version value.
     */
    public static String getVersionName(String versionName) {
        return getSystemProp(ENV_VERSION_NAME, versionName);
    }
    
    /**
     * get configuration environment id value.
     * 
     * @param envId return this value if not exist in the system property
     * @return configuration environment value.
     * @throws RuntimeException in case of envId converts failed exception
     */
    public static long getEnvId(String envId) {
        //environment id should valid or throws exception
        try {
            return Long.valueOf(getSystemProp(ENV_ENVID, envId));
        } catch (NumberFormatException e) {
            throw new RuntimeException("environment id is not a valid number. value=" + 
                    envId, e);
        }
    }
    
    /**
     * get configuration environment name value.
     * 
     * @param envId return this value if not exist in the system property
     * @return configuration environment value.
     */
    public static String getEnvName(String envName) {
        return getSystemProp(ENV_ENVNAME, envName);
    }
    
    /**
     * get configuration project value.
     * 
     * @param envId return this value if not exist in the system property
     * @return configuration environment value.
     */
    public static String getProjectName(String projectName) {
        return getSystemProp(ENV_PROJECT, projectName);
    }
    
    /**
     * get system property by specified key name. or return defaultValue if 
     * key not exist.
     * 
     * @param key property key to read
     * @param defaultValue default value to return if key not exist.
     * @return property value by the key
     */
    private static String getSystemProp(String key, String defaultValue) {
        
        String value = AccessController.doPrivileged(new SystemGetPropertyAction(key));
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Read value from JDK property key: " + key);
        }
        return value;
    }
    
    
    /**
     *  Get property value by key from {@link System} with privileges enabled.
     * 
     * @author xiemalin
     * @since 1.0.0.0
     */
    private static class SystemGetPropertyAction implements PrivilegedAction<String> {
        private final String name;

        /**
         * Default constructor with name
         * @param name property name
         */
        public SystemGetPropertyAction(String name) {
            this.name = name;
        }

        /**
         * Performs to get value from {@link System}.
         */
        public String run() {
            return System.getProperty(name);
        }
    }

}
