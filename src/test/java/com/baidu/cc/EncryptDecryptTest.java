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

import org.apache.log4j.Logger;

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * test encrypt and decrypt by using jasypt
 * 
 * @author xiemalin
 * @since 1.0.0.0
 */
public class EncryptDecryptTest {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger
            .getLogger(EncryptDecryptTest.class);

    
    /**
     * test encrypt and decrypt by StrongPasswordEncryptor
     */
    @Test
    public void testEncryptDecryptByStrongPasswordEncryptor() {
        
        String password = "hello";
        
        StrongPasswordEncryptor encryptor = new StrongPasswordEncryptor();
        
        String encoded = encryptor.encryptPassword(password);
        LOGGER.info(encoded);
        boolean ok = encryptor.checkPassword(password, encoded);
        
        Assert.assertTrue(ok);
        
    }
    
    
}
