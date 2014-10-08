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

import java.util.HashMap;
import java.util.Map;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.Assert;

import com.baidu.cc.interfaces.ExtConfigServerService;

/**
 * Mock class for {@link ExtConfigServerService} implementation.
 * 
 * @author xiemalin
 * @since 1.0.0.0
 */
public class ConfigServerServiceMock implements ExtConfigServerService {
    private static final String TEST_TAG_VALUE = "__test_tag_value__";
    public static final String TEST_TAG_NEW_VALUE = "__test_tag_new_value__";
    
    
    private  Map<String, String> props;
    private String tagValue;

    public String getTagValue() {
        return tagValue;
    }

    /**
     * @param tag the tag to set
     */
    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }

    public ConfigServerServiceMock() {
        Map<String, String> ret = new HashMap<String, String>();
        ret.put(ExtConfigServerService.TAG_KEY, TEST_TAG_VALUE);
        
        ret.put("key1", "value1");
        ret.put("key2", "value2");
        ret.put("xml", "1");
        props = ret;
        
        tagValue = TEST_TAG_VALUE;
    }
    
    /**
     * @param props the props to set
     */
    public void setProps(Map<String, String> props) {
        this.props = props;
    }

    /* (non-Javadoc)
     * @see com.baidu.cc.interfaces.ConfigServerService#getConfigItems(java.lang.String, java.lang.String, java.lang.Long)
     */
    public Map<String, String> getConfigItems(String user, String password,
            Long version) {
        authenticate(user, password);
        return new HashMap<String, String>(props);
    }

    /* (non-Javadoc)
     * @see com.baidu.cc.interfaces.ConfigServerService#getLastestConfigItems(java.lang.String, java.lang.String, java.lang.Long)
     */
    public Map<String, String> getLastestConfigItems(String user,
            String password, Long envId) {
        authenticate(user, password);
        return null;
    }

    /* (non-Javadoc)
     * @see com.baidu.cc.interfaces.ConfigServerService#getLastestConfigVersion(java.lang.String, java.lang.String, java.lang.Long)
     */
    public Long getLastestConfigVersion(String user, String password, Long envId) {
        authenticate(user, password);
        return null;
    }

    /* (non-Javadoc)
     * @see com.baidu.cc.interfaces.ConfigServerService#getConfigItemValue(java.lang.String, java.lang.String, java.lang.Long, java.lang.String)
     */
    public String getConfigItemValue(String user, String password,
            Long version, String key) {
        authenticate(user, password);
        return null;
    }

    /* (non-Javadoc)
     * @see com.baidu.cc.interfaces.ConfigServerService#checkVersionTag(java.lang.String, java.lang.String, java.lang.Long, java.lang.String)
     */
    public boolean checkVersionTag(String user, String password, Long version,
            String tag) {
        authenticate(user, password);
        System.out.println("check tag: current:" + tagValue + " to check:" + tag);
        return this.tagValue.equals(tag);
        
    }

    private void authenticate(String user, String password) {
        boolean b = "user".equals(user);
        
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(ExtConfigServerService.class.getSimpleName());
        
        b = b && "password".equals(encryptor.decrypt(password));
        
        if (!b) {
            Assert.fail("authenticate failed. user name should be 'user' " +
                    "and password should be password");
        }
    }

    public Long getLastestConfigVersion(String user, String password,
            String projectName, String envName) {
        return 1L;
    }

    public void importConfigItems(String user, String password, Long version,
            Map<String, String> configItems) {
        
        
    }

    public Map<String, String> getConfigItems(String user, String password,
            String versionName) {
        
        return null;
    }

    public Long getVersionId(String user, String password, String versionName) {
        
        return null;
    }
}
