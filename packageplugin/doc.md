###gradle自定义插件


**java assistant documt<br/>**
https://jboss-javassist.github.io/javassist/tutorial/tutorial.html

https://git.chunyu.me/android/gradle_base/raw/master/gradle/mvn_release.gradle

上传插件

````
apply plugin: 'groovy'
apply plugin: 'java'
apply from: 'bintray.gradle'

group 'com.antfortune.freeline'
version 0.8.17

uploadArchives {
    repositories {
        mavenDeployer {
            repository(url: uri('../repo'))
            
            <!--
            repository(url: "http://maven.chunyu.mobi/content/repositories/releases/") {
                authentication(userName: maven_user, password: maven_password)
            }
            pom.groupId = 'me.chunyu.android'
            pom.artifactId = 'weixinhelper'
            pom.version = '0.1.4.4'
            -->
        }
    }
}


task androidSourcesJar(type: Jar) {
    classifier = 'sources'
    from android.sourceSets.main.java.srcDirs
}

artifacts {
    archives file: file('build/intermediates/bundles/release/classes.jar'), type: 'jar'
}

artifacts {
    archives androidSourcesJar
}

````


引用自定义插件

````
buildscript {
    repositories {
        maven {
            url uri('./repo')
        }
        mavenLocal()
        jcenter()
        maven { url 'http://dl.bintray.com/jetbrains/intellij-plugin-service' }
    }
    dependencies {
        classpath 'com.antfortune.freeline:gradle:0.8.17'
    }
}

allprojects {
    repositories {
        maven {
            url uri('./repo')
        }
        jcenter()
    }
}

````

测速插件策略：

```

Speed注解的优先级最高，如果类上添加注解，则所有方法均打点，如果仅方法添加注解，则只有方法打点，  如果当前类无注解，则使用配置文件的规则。

package me.chunyu.gradle.speed.annotation;
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface Speed {
}


<?xml version="1.0" encoding="utf-8"?>
<resources>
<switch>
        <!--true代表打开Speed，请注意即使这个值为true，Speed也默认只在Release模式下开启-->
        <!--false代表关闭Speed，无论是Debug还是Release模式都不会运行Speed-->
        <turnOnSpeed>false</turnOnSpeed>
        <!--<turnOnSpeed>false</turnOnSpeed>-->

        <!--是否开启手动模式，手动模式会去寻找配置项patchPackname包名下的所有类，自动的处理混淆，然后把patchPackname包名下的所有类制作成补丁-->
        <!--这个开关只是把配置项patchPackname包名下的所有类制作成补丁，适用于特殊情况，一般不会遇到-->
        <!--<manual>true</manual>-->
        <manual>false</manual>

        <!--是否在补丁加上log，建议上线的时候这个开关的值为false，测试的时候为true-->
        <!--<patchLog>true</patchLog>-->
        <patchLog>false</patchLog>

        <!--项目是否支持progaurd-->
        <proguard>true</proguard>
        <!--<proguard>false</proguard>-->

        <!--项目是否支持ASM进行插桩，默认使用ASM，推荐使用ASM，Javaassist在容易和其他字节码工具相互干扰-->
        <useAsm>false</useAsm>
        <!--<useAsm>false</useAsm>-->
    </switch>

    <!--需要热补的包名或者类名，这些包名下的所有类都被会插入代码-->
    <!--这个配置项是各个APP需要自行配置，就是你们App里面你们自己代码的包名，
    这些包名下的类会被Speed插入代码，没有被Speed插入代码的类Speed是无法修复的-->
    <packname name="includePackage">
        <name>me.chunyu</name>
    </packname>

    <!--不需要Speed插入代码的包名，Speed库不需要插入代码，如下的配置项请保留，还可以根据各个APP的情况执行添加-->
    <exceptPackname name="exceptPackage">
        <name>me.chunyu.gradle.speed</name>
    </exceptPackname>
</resources>

```


```
// gradle clean build generateRelease

apply plugin: 'maven'

ext.getGroupId = { ->
    if (project.hasProperty('PUBLISH_GROUP_ID')) {
        return project.PUBLISH_GROUP_ID;
    } else {
        return 'me.chunyu.android'
    }
}

ext.getPomVersion = { ->
    if (project.hasProperty('PUBLISH_VERSION')) {
        return project.PUBLISH_VERSION;
    } else {
        return null
    }
}

ext.getArtifactId = { ->
    if (project.hasProperty('PUBLISH_ARTIFACT_ID')) {
        return project.PUBLISH_ARTIFACT_ID;
    } else {
        return null
    }
}

def groupId = getGroupId()
def artifactId = getArtifactId()
def version = getPomVersion()
def isJar = project.hasProperty('PUBLISH_IS_JAR') && project.PUBLISH_IS_JAR

def repo = "http://maven.chunyu.mobi/content/repositories/releases/"

//def localReleaseDest = "${buildDir}/release/${version}"

//task androidJavadocs(type: Javadoc) {
//    source = android.sourceSets.main.java.srcDirs
//    ext.androidJar = "${android.sdkDirectory}/platforms/${android.compileSdkVersion}/android.jar"
//    classpath += files(ext.androidJar)
//}
//
//task androidJavadocsJar(type: Jar, dependsOn: androidJavadocs) {
//    classifier = 'javadoc'
//    from androidJavadocs.destinationDir
//}

task androidSourcesJar(type: Jar) {
    classifier = 'sources'
    from android.sourceSets.main.java.srcDirs
}

uploadArchives {
    repositories.mavenDeployer {
        pom.groupId = groupId
        pom.artifactId = artifactId
        pom.version = version
        // Add other pom properties here if you want (developer details / licenses)
        repository(url: repo) {
            authentication(userName: maven_user, password: maven_password)
        }
    }
}

if (isJar) {
    artifacts {
        archives file: file('build/intermediates/bundles/release/classes.jar'), type: 'jar'
    }
}

artifacts {
    archives androidSourcesJar
}
```











