# ComKit
Android组件化框架。方案思想源于 https://github.com/mqzhangw/JIMU

| comkit-annotation | comkit-core | comkit-processor | comkit-plugin |
| :---: | :---: | :---: | :---: |
| [ ![Download](https://api.bintray.com/packages/fangdawei/maven/comkit-annotation/images/download.svg?version=1.0.0) ](https://bintray.com/fangdawei/maven/comkit-annotation/1.0.0/link) | [ ![Download](https://api.bintray.com/packages/fangdawei/maven/comkit-core/images/download.svg?version=1.0.0) ](https://bintray.com/fangdawei/maven/comkit-core/1.0.0/link) | [ ![Download](https://api.bintray.com/packages/fangdawei/maven/comkit-processor/images/download.svg?version=1.0.0) ](https://bintray.com/fangdawei/maven/comkit-processor/1.0.0/link) | [ ![Download](https://api.bintray.com/packages/fangdawei/maven/comkit-plugin/images/download.svg?version=1.0.0) ](https://bintray.com/fangdawei/maven/comkit-plugin/1.0.0/link) |

#### 介绍

该组件化框架主要解决的问题是支持每个module独立运行。

通常的Android工程，包含一个application module和若干个library（Java Library或Android Library） module。application module最终会编译出apk，而librabry module只是作为application module的依赖库，本质上跟引入第三方的aar或jar没有什么区别。

在组件化中，application module最终会变很简单，主要的职责就是提供一个聚合各个功能模块的容器。而各个功能模块的具体实现被拆分到了每个android library module中。各个module之间相互独立，不存在依赖关系。

#### 使用方式

在这里，我们称application module为主module。

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

在需要支持独立运行的module下创建gradle.properties文件，根据需要配置如下参数

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

在module下的build.gradle中，使用

```
apply plugin: 'comkit-plugin'
```

替换掉

```
apply plugin: 'com.android.application'
apply plugin: 'com.android.library'
```

完成上述工作后，还需要为每个module（除主module外）提供独立运行所需要的一些文件。

在src/main目录下新建runalone目录，最终结构如下

```
src
└── main
    ├── AndroidManifest.xml
    ├── java
    ├── res
    └── runalone
        ├── AndroidManifest.xml
        ├── java
        └── res
```

在runalone下的对应文件夹中添加独立运行所需要的文件。该目录下的文件在module独立运行时会被加入到sourceSets中，作为library时则会被忽略。

在library module中，如果需要Application，是不能直接去获取的。因为在作为library和独立运行这两种场景下，Application通常是不同的，所以你无法预知当前运行的Application是哪一个。为了解决这个问题，框架提供了AppDelegate。AppDelegate可以认为是一个虚拟的Application。

```
@AppDelegate
class SampleAppDelegate : IAppDelegate {
    override fun onCreate(app: Application) {
        super.onCreate(app)
    }

    override fun onTerminate() {
        super.onTerminate()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
    }
}
```
在AppDelegate中，你可以做原本需要在Application中做的事情。



