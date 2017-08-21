###gradle自定义插件

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
        }
    }
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