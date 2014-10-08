配置中心客户端
=========

配置中心能统一管理多个应用的资源配置信息，如memcached、DB、redis等的配置，这些配置多是K-V结构。

配置中心使得配置信息不散落在各个项目，简化了配置文件的管理和维护；支持多环境、多版本的配置管理，隔离了测试和生产环境配置；支持在不改变应用源码的情况下无缝的切换配置。

配置中心分为server和client两个部分。server负责配置的添加、修改、变更通知等，所有的配置信息均记录到Mysql数据库中；client负责与server通信，获取配置、替换本地配置等。

本项目为配置中心的client部分，server部分请参看[configcenter项目](https://github.com/Baidu-ecom/configcenter)。 

## 环境要求 ##

Ant，JDK 6或以上版本，Tomcat 6或以上版本

## 编译发布说明 ##

### 编译前操作 ###

配置中心的编译依赖Ant和JDK 1.6+，请确保已经安装Ant和JDK 1.6+，并且添加到path。

### 编译 ###

Windows下：
在命令行运行build_ant.bat脚本。

Linux下：
在命令行运行build_ant.sh脚本。

脚本运行成功后会在源码目录的dist/jar目录下生成名为configcenter-bjf的jar包。

编译成功后，将生成的jar包放到项目classpath下就可以使用配置中心了。如果不想编译，也可以直接将releases目录下的jar包拷贝到项目classpath下。

## 配置中心client使用说明 ##

使用前请确保已经成功部署配置中心服务端，并且在服务端添加了相关配置文件。

### 使用API ###

可以直接在代码中使用client提供的API来获取配置及监听配置变更等。

使用示例如下：

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

代码中的一些属性设置可以参看《与Spring集成》部分的参数说明。

### 与Spring集成 ###

在spring配置文件中增加如下配置：
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

#### 参数说明 ####

参数名称             | 说明                               | 默认值
---------------------|------------------------------------|-----------
callbackInteval      | 配置变更回调监听时间间隔，单位ms | 2000
ccServerUrl          | 配置中心服务url，如果JVM参数中指定了该-DccServerUrl变量，将忽略本设置 | 
ccUser               | 配置中心登录用户名，如果JVM参数中指定了该-DccUser变量，将忽略本设置 | 
ccPassword           | 配置中心登录密码，如果JVM参数中指定了该-DccPassword变量，将忽略本设置 | 
ccVersion            | 配置中心访问的版本号，如果JVM参数中指定了该-DccVersion变量，将忽略本设置 | 
ccEnvId              | 配置中心访问的运行环境id号，如果JVM参数中指定了该-DccEvnId变量，将忽略本设置 | 
enableUpdateCallback | 是否开启变更回调功能 | 默认为false，关闭变更回调功能
localResource        | 设置本地配置缓存文件 | 默认为null，关闭本地缓存功能
connectionTimeout    | 连接配置中心服务器超时设置，单位ms | 2000
readTimeout          | 配置中心访问超时设置，单位ms | 2000
projectName          | 工程名称 | 
envName              | 运行环境名称 | 
ccVersionName        | 版本名称 | 
logProperties        | 加载配置成功后打印加载的配置key与部分value | 默认为false，不打印

#### 属性的加载优先级说明 ####

配置的加载的方式最终都需要通过version id从配置中心服务器获取配置内容。

第一优先加载ccVersion。如果指定版本，则直接使用该版本下的配置。第二优先加载ccVersionName。如果指定版本名称，则直接使用该版本名称来获取版本号，进而获取该版本下的配置。第三顺位加载projectName和envName这两个属性。如果这两个都不为空，则会根据这两个值从配置中心获取最新的一份配置。第四顺位加载evnId。如果这个值不为空，则会根据这个值从配置中心获取最新的一份配置。

### 配置导入功能 ###

配置导入功能可以支持把本地加载的配置内容导入到配置中心服务器(只支持开发环境)。该功能可以很方便让原来不支持配置中心的项目快速使用配置中心应用。

在spring配置文件中增加如下配置：
```property
<bean class="com.baidu.cc.spring.ConfigCenterPropertyExtractor">
	<property name="ccVersion" value="1"></property> 
	<property name="importer" 
		ref=" ConfigCenterPropertyPlaceholderConfigurer"></property>
	<property name="extractor" ref="PropertyPlaceholderConfigurer"></property>
</bean> 
```

#### 参数说明 ####

参数名称 | 说明
------------|--------------
ccVersion | 表示需要导入到的目标版本号id。为了安全，只有针对空的版本id，才可以导入
importer | 指定需要导入的配置中心服务器
extractor |  需要导入的配置内容

### 配置变更监听功能 ###

client端提供接口和注解两种方式支持开发人员实现配置变更事件监听。

#### 基于ConfigItemChangedCallable接口的实现 ####

实现ConfigItemChangedCallable接口，把实现的Bean配置到Spring容器中即可。

实现示例如下：
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

#### 基于注解的实现 ####

要支持注解功能，需要将lib目录下的bjf-1.0.1.0.jar放到项目的classpath下，并增加如下spring配置：
```property
<bean class="com.baidu.bjf.beans.context.annotation.CommonAnnotationBeanPostProcessor">
	<property name="callback">
		<bean class="com.baidu.cc.annotation.ConfigChangeNotiferAnnotationParser">
		</bean>
	</property>
</bean>
```

使用示例如下:
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

@ConfigChangeNotifer只有一个参数，默认为空，表示接收所有配置的变更内容。如果指定参数，则用逗号（,）分隔多个key名称，表示只监听指定key的变更内容。

## 联系我们 ##

email: [rigel-opensource@baidu.com](mailto://email:rigel-opensource@baidu.com "发邮件给配置中心开发组")


