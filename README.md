�������Ŀͻ���
=========

����������ͳһ������Ӧ�õ���Դ������Ϣ����memcached��DB��redis�ȵ����ã���Щ���ö���K-V�ṹ��

��������ʹ��������Ϣ��ɢ���ڸ�����Ŀ�����������ļ��Ĺ����ά����֧�ֶ໷������汾�����ù��������˲��Ժ������������ã�֧���ڲ��ı�Ӧ��Դ���������޷���л����á�

�������ķ�Ϊserver��client�������֡�server�������õ���ӡ��޸ġ����֪ͨ�ȣ����е�������Ϣ����¼��Mysql���ݿ��У�client������serverͨ�ţ���ȡ���á��滻�������õȡ�

����ĿΪ�������ĵ�client���֣�server������ο�[configcenter��Ŀ](https://github.com/Baidu-ecom/configcenter)�� 

## ����Ҫ�� ##

Ant��JDK 6�����ϰ汾��Tomcat 6�����ϰ汾

## ���뷢��˵�� ##

### ����ǰ���� ###

�������ĵı�������Ant��JDK 1.6+����ȷ���Ѿ���װAnt��JDK 1.6+��������ӵ�path��

### ���� ###

Windows�£�
������������build_ant.bat�ű���

Linux�£�
������������build_ant.sh�ű���

�ű����гɹ������Դ��Ŀ¼��dist/jarĿ¼��������Ϊconfigcenter-bjf��jar����

����ɹ��󣬽����ɵ�jar���ŵ���Ŀclasspath�¾Ϳ���ʹ�����������ˡ����������룬Ҳ����ֱ�ӽ�releasesĿ¼�µ�jar����������Ŀclasspath�¡�

## ��������clientʹ��˵�� ##

ʹ��ǰ��ȷ���Ѿ��ɹ������������ķ���ˣ������ڷ�����������������ļ���

### ʹ��API ###

����ֱ���ڴ�����ʹ��client�ṩ��API����ȡ���ü��������ñ���ȡ�

ʹ��ʾ�����£�

```java
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.baidu.cc.interfaces.ChangedConfigItem;
import com.baidu.cc.interfaces.ConfigItemChangedCallable;

public class ConfigCenterClientTest {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(ConfigCenterClientTest.class);

    /**
     * demo for API usage.
     */
    public void demoforAPIUsage() {
        ConfigCenterClient client = new ConfigCenterClient();
        
        String ccServerUrl = "http://localhost:8080/rpc/ExtConfigServerService";
        String ccUser = "test1";
        String ccPassword = "123";
        String ccVersionName = "bjf-1.0.1.0";
        
        client.setCcServerUrl(ccServerUrl);
        client.setCcUser(ccUser);
        client.setCcPassword(ccPassword);
        client.setCcVersionName(ccVersionName);
        
        // create config loader
        ConfigLoader configLoader = client.createConfigLoader();
        
        // read all config item
        Map<String, String> configItems = configLoader.getConfigItems();
        LOGGER.info(configItems);
        
        // add call back listener here
        configLoader.getCallables().put("cust_listener", new ConfigItemChangedCallable() {
            
            public void changed(List<ChangedConfigItem> changedConfigItemList) {
                
            }
        });
        Properties properties = new Properties();
        properties.putAll(configItems);
        configLoader.setVersionTag(configItems);
        configLoader.startListening(properties);
        
    }   
}
``` 

�����е�һЩ�������ÿ��Բο�����Spring���ɡ����ֵĲ���˵����

### ��Spring���� ###

��spring�����ļ��������������ã�
```property
<bean class="com.baidu.cc.spring.ConfigCenterPropertyPlaceholderConfigurer">
	<property name="callbackInteval" value="500"></property>
	<property name="ccServerUrl" 
		value="http://localhost:8080/rpc/ExtConfigServerService"></property>
	<property name="ccUser" value="user"></property>
	<property name="ccPassword" value="password"></property>
	<property name="ccVersion" value="1"></property>
	<property name="enableUpdateCallback" value="false"></property>
	<property name="localResource" value="/local/localPropFile"></property>
</bean>  
```

#### ����˵�� ####

��������             | ˵��                               | Ĭ��ֵ
---------------------|------------------------------------|-----------
callbackInteval      | ���ñ���ص�����ʱ��������λms | 2000
ccServerUrl          | �������ķ���url�����JVM������ָ���˸�-DccServerUrl�����������Ա����� | 
ccUser               | �������ĵ�¼�û��������JVM������ָ���˸�-DccUser�����������Ա����� | 
ccPassword           | �������ĵ�¼���룬���JVM������ָ���˸�-DccPassword�����������Ա����� | 
ccVersion            | �������ķ��ʵİ汾�ţ����JVM������ָ���˸�-DccVersion�����������Ա����� | 
ccEnvId              | �������ķ��ʵ����л���id�ţ����JVM������ָ���˸�-DccEvnId�����������Ա����� | 
enableUpdateCallback | �Ƿ�������ص����� | Ĭ��Ϊfalse���رձ���ص�����
localResource        | ���ñ������û����ļ� | Ĭ��Ϊnull���رձ��ػ��湦��
connectionTimeout    | �����������ķ�������ʱ���ã���λms | 2000
readTimeout          | �������ķ��ʳ�ʱ���ã���λms | 2000
projectName          | �������� | 
envName              | ���л������� | 
ccVersionName        | �汾���� | 
logProperties        | �������óɹ����ӡ���ص�����key�벿��value | Ĭ��Ϊfalse������ӡ

#### ���Եļ������ȼ�˵�� ####

���õļ��صķ�ʽ���ն���Ҫͨ��version id���������ķ�������ȡ�������ݡ�

��һ���ȼ���ccVersion�����ָ���汾����ֱ��ʹ�øð汾�µ����á��ڶ����ȼ���ccVersionName�����ָ���汾���ƣ���ֱ��ʹ�øð汾��������ȡ�汾�ţ�������ȡ�ð汾�µ����á�����˳λ����projectName��envName���������ԡ��������������Ϊ�գ�������������ֵ���������Ļ�ȡ���µ�һ�����á�����˳λ����evnId��������ֵ��Ϊ�գ����������ֵ���������Ļ�ȡ���µ�һ�����á�

### ���õ��빦�� ###

���õ��빦�ܿ���֧�ְѱ��ؼ��ص��������ݵ��뵽�������ķ�����(ֻ֧�ֿ�������)���ù��ܿ��Ժܷ�����ԭ����֧���������ĵ���Ŀ����ʹ����������Ӧ�á�

��spring�����ļ��������������ã�
```property
<bean class="com.baidu.cc.spring.ConfigCenterPropertyExtractor">
	<property name="ccVersion" value="1"></property> 
	<property name="importer" 
		ref=" ConfigCenterPropertyPlaceholderConfigurer"></property>
	<property name="extractor" ref="PropertyPlaceholderConfigurer"></property>
</bean> 
```

#### ����˵�� ####

�������� | ˵��
------------|--------------
ccVersion | ��ʾ��Ҫ���뵽��Ŀ��汾��id��Ϊ�˰�ȫ��ֻ����Կյİ汾id���ſ��Ե���
importer | ָ����Ҫ������������ķ�����
extractor |  ��Ҫ�������������

### ���ñ���������� ###

client���ṩ�ӿں�ע�����ַ�ʽ֧�ֿ�����Աʵ�����ñ���¼�������

#### ����ConfigItemChangedCallable�ӿڵ�ʵ�� ####

ʵ��ConfigItemChangedCallable�ӿڣ���ʵ�ֵ�Bean���õ�Spring�����м��ɡ�

ʵ��ʾ�����£�
```java
@Service("configChangedCallbackMock")
public class ConfigChangedCallbackMock implements ConfigItemChangedCallable {
        
    /* (non-Javadoc)
    * @see com.baidu.cc.interfaces.ConfigItemChangedCallable#changed(java.util.List)
    */
    public void changed(List<ChangedConfigItem> changedConfigItemList) {
                
        for (ChangedConfigItem changedConfigItem : changedConfigItemList) {
            //get change notify content
            System.out.println("Changed configuration key: " + changedConfigItem.getKey());
            System.out.println("original value: " + changedConfigItem.getOldValue());
            System.out.println("new value: " + changedConfigItem.getNewValue());
        }
    }
}
``` 

#### ����ע���ʵ�� ####

Ҫ֧��ע�⹦�ܣ���Ҫ��������spring���ã�
```property
<bean class="com.baidu.bjf.beans.context.annotation.CommonAnnotationBeanPostProcessor">
	<property name="callback">
		<bean class="com.baidu.cc.annotation.ConfigChangeNotiferAnnotationParser">
		</bean>
	</property>
</bean>
```

ʹ��ʾ������:
```java
@Service("configChangedMock")
public class ConfigChangedMock {
        
    @ConfigChangeNotifer
    public void doChangedAll(List<ChangedConfigItem> changedConfigItems) {
        System.out.println("doChangedAll call back");
        System.out.println(changedConfigItems);
    }
        
    @ConfigChangeNotifer(keys = "key3,key2,key1")
    public void doChangedByFilter(List<ChangedConfigItem> changedConfigItems) {
        System.out.println("doChangedByFilter call back");
        System.out.println(changedConfigItems);                
    }
}
``` 

@ConfigChangeNotiferֻ��һ��������Ĭ��Ϊ�գ���ʾ�����������õı�����ݡ����ָ�����������ö��ţ�,���ָ����key���ƣ���ʾֻ����ָ��key�ı�����ݡ�

## ��ϵ���� ##

email: [rigel-opensource@baidu.com](mailto://email:rigel-opensource@baidu.com "���ʼ����������Ŀ�����")


