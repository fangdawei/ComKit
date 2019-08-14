# ComKit
Android组件化框架。方案思想源于 https://github.com/mqzhangw/JIMU


| comkit-annotation | comkit-core | comkit-processor | comkit-plugin |
| :---: | :---: | :---: | :---: |
| [ ![Download](https://api.bintray.com/packages/fangdawei/maven/comkit-annotation/images/download.svg?version=1.0.0) ](https://bintray.com/fangdawei/maven/comkit-annotation/1.0.0/link) | [ ![Download](https://api.bintray.com/packages/fangdawei/maven/comkit-core/images/download.svg?version=1.0.0) ](https://bintray.com/fangdawei/maven/comkit-core/1.0.0/link) | [ ![Download](https://api.bintray.com/packages/fangdawei/maven/comkit-processor/images/download.svg?version=1.0.0) ](https://bintray.com/fangdawei/maven/comkit-processor/1.0.0/link) | [ ![Download](https://api.bintray.com/packages/fangdawei/maven/comkit-plugin/images/download.svg?version=1.0.0) ](https://bintray.com/fangdawei/maven/comkit-plugin/1.0.0/link) |

#### 介绍

该组件化框架主要解决的问题是支持每个module独立运行。

通常的Android工程，包含一个application module和若干个library（Java Library或Android Library） module。application module最终会编译出apk，而librabry module只是作为application的依赖库，本质上跟引入第三方的aar或jar没有什么区别。

这里我们称application module为主module

#### 使用方式

根目录下的build.gradle添加classpath

```
buildscript {
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath "club.fdawei.comkit:comkit-plugin:?"
    }
}
```

需要独立运行的module下创建gradle.properties文件，配置如下

```
# 配置是否需要支持独立运行（true/false）
# 主module不用配置，默认是独立运行的
comkit.can.runalone=true

# 配置主module的path
# 如果不配置，默认:app
comkit.main.module=:app

# 独立运行时的applicationId
# 主module不用配置
comkit.application.id=xxx.xxx.xxx

# 运行时需要的依赖，支持两种依赖方式
# 1、依赖其他module（:xxx）
# 2、Maven依赖（xxx:xxx:version）
# 当改module独立运行时，这些依赖才会生效，而作为library时不生效
comkit.run.dependencies=:xxx,xxx:xxx:version
```


