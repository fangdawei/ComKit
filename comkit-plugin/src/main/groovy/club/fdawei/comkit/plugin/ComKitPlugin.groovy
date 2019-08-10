package club.fdawei.comkit.plugin

import club.fdawei.comkit.plugin.base.GradleCmdInfo
import club.fdawei.comkit.plugin.common.ConfigKeys
import club.fdawei.comkit.plugin.common.KaptKeys
import club.fdawei.comkit.plugin.ext.ComKitExtension
import club.fdawei.comkit.plugin.ext.ModuleNameObserver
import club.fdawei.comkit.plugin.log.Logger
import club.fdawei.comkit.plugin.util.DepUtils
import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariantOutput
import com.android.build.gradle.tasks.ProcessApplicationManifest
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by david on 2019/07/15.
 */
class ComKitPlugin implements Plugin<Project> {

    private Project currProject
    private String mainModulePath
    private ComKitExtension comKitExt

    @Override
    void apply(Project target) {
        this.currProject = target
        if (target.hasProperty(ConfigKeys.KEY_MAIN_MODULE)) {
            mainModulePath = target.properties.get(ConfigKeys.KEY_MAIN_MODULE)
        } else {
            mainModulePath = "${Project.PATH_SEPARATOR}app"
        }
        comKitExt = target.extensions.create('comkit', ComKitExtension)
        comKitExt.name = target.name

        Logger.i("apply plugin, " +
                "currProject(name=${target.name}, " +
                "path=${target.path}, " +
                "mainModulePath=${mainModulePath})")

        def gradleCmdInfo = parseGradleCmdInfo()
        Logger.i("gradle cmd info ${gradleCmdInfo}")

        def isRunAlone = judgeIfRunAlone(gradleCmdInfo)
        Logger.i("isRunAlone=$isRunAlone")
        if (isRunAlone) {
            target.apply plugin: 'com.android.application'
            if (!isCurrMainModule()) {
                // 非主module，runalone模式下自动添加相关配置
                configModuleNotMainToRunAlone()
            }
            // 添加runalone的依赖
            if (gradleCmdInfo.isAssemble) {
                addRunDependencies()
            }
            // 注册ComKitTransform
            def android = currProject.extensions.getByType(AppExtension)
            android.registerTransform(new ComKitTransform(currProject))
        } else {
            target.apply plugin: 'com.android.library'
        }

        target.afterEvaluate {
            // 配置kapt，注入一些参数
            configKapt()

            if (isRunAlone) {
                hookManifestMerger()
            }
        }
    }

    private void configKapt() {
        def kaptExt = currProject.extensions.findByName('kapt')
        if (kaptExt == null) {
            return
        }
        comKitExt.observeModuleName(new ModuleNameObserver() {
            @Override
            void onUpdate(String name) {
                kaptExt.arguments {
                    arg(KaptKeys.KEY_MODULE_NAME, name)
                }
            }
        })
    }

    private boolean isCurrMainModule() {
        return currProject.path == mainModulePath
    }

    private boolean judgeIfRunAlone(GradleCmdInfo cmdInfo) {
        if (isCurrMainModule()) {
            return true
        }
        def canRunAlone = Boolean.parseBoolean(currProject.properties.get(ConfigKeys.KEY_CAN_RUNALONE))
        if (canRunAlone && cmdInfo.isAssemble) {
            if (cmdInfo.isMultiAssemble()) {
                return isCurrMainModule()
            } else {
                return currProject.path == cmdInfo.getFirstAssemblePath()
            }
        } else {
            return canRunAlone
        }
    }

    private void configModuleNotMainToRunAlone() {
        // 配置applicationId、sourceSets
        def appId = currProject.properties.get(ConfigKeys.KEY_APPLICATION_ID)
        if (appId == null) {
            throw new RuntimeException("${ConfigKeys.KEY_APPLICATION_ID} need, but not found!")
        }
        def android = currProject.extensions.getByType(AppExtension)
        android.defaultConfig {
            applicationId appId
        }
        android.sourceSets {
            main {
                manifest.srcFile 'src/main/runalone/AndroidManifest.xml'
                java.srcDirs = ['src/main/java', 'src/main/runalone/java']
                res.srcDirs = ['src/main/res', 'src/main/runalone/res']
                assets.srcDirs = ['src/main/assets', 'src/main/runalone/assets']
                jni.srcDirs = ['src/main/jni', 'src/main/runalone/jni']
                jniLibs.srcDirs = ['src/main/jniLibs', 'src/main/runalone/jniLibs']
                aidl.srcDirs = ['src/main/aidl', 'src/main/runalone/aidl']
            }
        }
        comKitExt.manifests.add('src/main/AndroidManifest.xml')
    }

    private void addRunDependencies() {
        def deps = new LinkedList<String>()
        if (currProject.hasProperty(ConfigKeys.KEY_RUN_DEPS)) {
            def depConfig = currProject.properties.get(ConfigKeys.KEY_RUN_DEPS) as String
            def blocks = depConfig.split(',')
            blocks.each { block ->
                if (block != null && !block.isBlank()) {
                    deps.add(block.trim())
                }
            }
        }
        Logger.i("add dependencies ${deps}")
        deps.each {
            if (DepUtils.isDepMaven(it)) {
                currProject.dependencies.add('implementation', it)
            } else if (DepUtils.isDepProject(it)) {
                currProject.dependencies.add('implementation', currProject.project(it))
            }
        }
    }

    private GradleCmdInfo parseGradleCmdInfo() {
        def cmdInfo = new GradleCmdInfo()
        def taskNames = currProject.gradle.startParameter.taskNames
        for (def taskName : taskNames) {
            if (taskName.toUpperCase().contains('ASSEMBLE')
                    || taskName.toUpperCase().contains('BUILD')
                    || taskName.toUpperCase().contains('INSTALL')) {
                cmdInfo.isAssemble = true
                def blocks = taskName.split(Project.PATH_SEPARATOR)
                def pathBuilder = new StringBuilder()
                for (int i = 0; i < blocks.length - 1; i++) {
                    def str = blocks[i]
                    if (str != null && !str.isBlank()) {
                        pathBuilder.append(Project.PATH_SEPARATOR).append(str)
                    }
                }
                if (!pathBuilder.isBlank()) {
                    cmdInfo.addAssemblePath(pathBuilder.toString())
                }
            }
        }
        if (cmdInfo.isAssemble) {
            if (!cmdInfo.hasAssemblePath()) {
                cmdInfo.addAssemblePath(mainModulePath)
            }
        }
        return cmdInfo
    }

    private void hookManifestMerger() {
        def android = currProject.extensions.findByType(AppExtension)
        if (android == null) {
            return
        }
        final def sourceManifestFiles = new LinkedList<File>()
        comKitExt.manifests.each {
            if (it != null && !it.isBlank()) {
                File manifestFile
                if (it.startsWith(File.separator)) {
                    manifestFile = new File(it)
                } else {
                    manifestFile = new File(currProject.projectDir, it)
                }
                if (manifestFile.exists()) {
                    sourceManifestFiles.add(manifestFile)
                }
            }
        }
        if (sourceManifestFiles.isEmpty()) {
            return
        }
        android.applicationVariants.all { ApplicationVariant variant ->
            variant.outputs.all { BaseVariantOutput output ->
                def task = output.processManifestProvider.get()
                task.doFirst {
                    if (task instanceof ProcessApplicationManifest) {
                        def configProxy = VariantConfigurationProxy.create(task.variantConfiguration,
                                sourceManifestFiles)
                        task.variantConfiguration = configProxy
                    }
                }
            }
        }
    }
}
