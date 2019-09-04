package club.fdawei.comkit.plugin

import club.fdawei.comkit.plugin.log.Logger
import club.fdawei.comkit.plugin.util.ClassUtils
import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.ide.common.internal.WaitableExecutor
import com.android.utils.FileUtils
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonWriter
import javassist.ClassPool
import javassist.CtNewMethod
import javassist.NotFoundException
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Project

import java.util.jar.JarFile

import static club.fdawei.comkit.plugin.common.ClassInfo.*

/**
 * Created by david on 2019/07/22.
 */
class AppDelegateCollector {

    private Project project
    private TransformInvocation invocation
    private ClassPool classPool = new ClassPool(true)
    private final ProviderContainer providerContainer = new ProviderContainer()
    private String appClassName = null
    private File appClassInDir = null
    private String appClassFromKey = null

    AppDelegateCollector(Project project, TransformInvocation invocation) {
        this.project = project
        this.invocation = invocation
        this.providerContainer.cacheDir = new File(project.buildDir,
                "intermediates/incremental/collectComKitProviders/${invocation.context.variantName}")
        appendSdkClassPath()
    }

    private void appendSdkClassPath() {
        def android = project.extensions.getByType(AppExtension)
        def sdkVer = android.compileSdkVersion
        def sdkPath = "platforms${File.separator}${sdkVer}${File.separator}android.jar"
        def androidJarFile = new File(android.sdkDirectory, sdkPath)
        classPool.appendClassPath(androidJarFile.absolutePath)
    }

    void transform() {
        Logger.i("collect AppDelegate start, incremental=${invocation.isIncremental()}")
        long startTime = System.currentTimeMillis()
        appClassName = getApplicationName()
        providerContainer.clear()
        if (invocation.incremental && providerContainer.loadCache()) {
            incrementalProcess()
        } else {
            fullProcess()
        }
        if (genAppDelegateContainer(appClassInDir)) {
            hookApplication(appClassName, appClassInDir)
        }
        providerContainer.saveCache()
        long costTime = System.currentTimeMillis() - startTime
        Logger.i("collect AppDelegate finish, cost time ${String.format('%.3f', costTime / 1000f)}s")
    }

    private void fullProcess() {
        invocation.outputProvider.deleteAll()
        def collectExecutor = WaitableExecutor.useGlobalSharedThreadPool()
        invocation.inputs.each {
            it.directoryInputs.each { dirInput ->
                classPool.appendClassPath(dirInput.file.absolutePath)
                def location = invocation.outputProvider.getContentLocation(
                        dirInput.name, dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY)
                collectExecutor.execute {
                    collectInDir(dirInput)
                    FileUtils.copyDirectory(dirInput.file, location)
                }
            }
            it.jarInputs.each { jarInput ->
                classPool.appendClassPath(jarInput.file.absolutePath)
                def location = invocation.outputProvider.getContentLocation(
                        jarInput.name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                collectExecutor.execute {
                    collectInJar(jarInput)
                    FileUtils.copyFile(jarInput.file, location)
                }
            }
        }
        collectExecutor.waitForTasksWithQuickFail(true)
    }

    private void incrementalProcess() {
        def collectExecutor = WaitableExecutor.useGlobalSharedThreadPool()
        invocation.inputs.each {
            it.directoryInputs.each { dirInput ->
                def inputKey = parseInputKey(dirInput, Format.DIRECTORY)
                def location = invocation.outputProvider.getContentLocation(dirInput.name,
                        dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY)
                if (dirInput.file.exists()) {
                    classPool.appendClassPath(dirInput.file.absolutePath)
                    providerContainer.remove(inputKey)
                    collectExecutor.execute {
                        collectInDir(dirInput)
                        FileUtils.deleteRecursivelyIfExists(location)
                        FileUtils.copyDirectory(dirInput.file, location)
                    }
                } else {
                    providerContainer.remove(inputKey)
                    FileUtils.deleteRecursivelyIfExists(location)
                }
            }
            it.jarInputs.each { jarInput ->
                classPool.appendClassPath(jarInput.file.absolutePath)
                def location = invocation.outputProvider.getContentLocation(jarInput.name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                def inputKey = parseInputKey(jarInput, Format.JAR)
                switch (jarInput.status) {
                    case Status.NOTCHANGED:
                        break
                    case Status.ADDED:
                    case Status.CHANGED:
                        providerContainer.remove(inputKey)
                        collectExecutor.execute {
                            collectInJar(jarInput)
                            FileUtils.deleteIfExists(location)
                            FileUtils.copyFile(jarInput.file, location)
                        }
                        break
                    case Status.REMOVED:
                        providerContainer.remove(inputKey)
                        FileUtils.deleteIfExists(location)
                        break
                }
            }
        }
        collectExecutor.waitForTasksWithQuickFail(true)
    }

    private collectInDir(DirectoryInput input) {
        def inputKey = parseInputKey(input, Format.DIRECTORY)
        input.file.eachFileRecurse {
            if (it.file && it.exists()) {
                def className = ClassUtils.getClassName(input.file, it)
                if (ModuleProvider.isModuleProvider(className)) {
                    onFindProvider(inputKey, ProviderInfo.fromDir(input.file.absolutePath, className))
                } else if (className == appClassName) {
                    def location = invocation.getOutputProvider().getContentLocation(input.name,
                            input.contentTypes, input.scopes, Format.DIRECTORY)
                    onFindApplicationClass(inputKey, location)
                }
            }
        }
    }

    private collectInJar(JarInput input) {
        def inputKey = parseInputKey(input, Format.JAR)
        def jarFile = new JarFile(input.file)
        def entries = jarFile.entries()
        while (entries.hasMoreElements()) {
            def entry = entries.nextElement()
            if (entry.directory) {
                continue
            }
            def className = ClassUtils.getClassName(entry.name)
            if (ModuleProvider.isModuleProvider(className)) {
                onFindProvider(inputKey, ProviderInfo.fromJar(input.file.absolutePath, className))
            }
        }
    }

    private void onFindProvider(String inputKey, ProviderInfo provider) {
        synchronized (providerContainer) {
            providerContainer.addProvider(inputKey, provider)
        }
    }

    private synchronized void onFindApplicationClass(String inputKey, File inDir) {
        appClassInDir = inDir
        appClassFromKey = inputKey
    }

    private File getManifestFile() {
        def variantName = invocation.context.variantName
        def android = project.extensions.getByType(AppExtension)
        for (def variant in android.applicationVariants) {
            if (variant.name == variantName) {
                for (def sourceSet in variant.sourceSets) {
                    if (sourceSet.manifestFile != null) {
                        return sourceSet.manifestFile
                    }
                }
            }
        }
        return null
    }

    private String getApplicationName() {
        def manifestFile = getManifestFile()
        if (manifestFile != null && manifestFile.exists()) {
            def xmlSlurper = new XmlSlurper(false, false)
            def manifest = xmlSlurper.parse(manifestFile)
            String packageName = manifest.'@package'.text()
            String applicationName = manifest.application[0].'@android:name'.text()
            if (applicationName.startsWith('.')) {
                applicationName = packageName + applicationName
            } else {
                applicationName = "$packageName.$applicationName"
            }
            return applicationName
        }
        return null
    }

    private boolean genAppDelegateContainer(File writeDir) {
        if (writeDir == null || !writeDir.exists()) {
            Logger.e("directory to write class ${AbsAppDelegateContainer.NAME} not found")
            return false
        }
        def superAbsClass = classPool.getCtClass(AbsAppDelegateContainer.NAME)
        def ctClass = classPool.makeClass(AppDelegateContainer.NAME, superAbsClass)
        def methodSrcBuilder = new StringBuilder()
        methodSrcBuilder.append("protected void ${AbsAppDelegateContainer.METHOD_INIT_PROVIDERS}() {")
        providerContainer.providers.each {
            Logger.i("find ${it.className} in ${it.fromTypeName}(${it.fromFile})")
            methodSrcBuilder.append(
                    "this.${AbsAppDelegateContainer.METHOD_ADD_PROVIDER}(new ${it.className}());")
        }
        methodSrcBuilder.append('}')
        ctClass.addMethod(CtNewMethod.make(methodSrcBuilder.toString(), ctClass))
        ctClass.writeFile(appClassInDir.absolutePath)
        Logger.i("generate class ${AppDelegateContainer.NAME}")
        return true
    }

    private void hookApplication(String className, File writeDir) {
        if (className == null || writeDir == null) {
            Logger.e("application class not found")
            return
        }
        def appCtClass = classPool.getCtClass(className)
        if (appCtClass.frozen) {
            appCtClass.defrost()
        }

        // hook onCreate
        def dispatcherInitSrc = "${AppLifecycleDispatcher.NAME}.INSTANCE." +
                "${AppLifecycleDispatcher.METHOD_INIT}(new ${AppDelegateContainer.NAME}());"
        def delegateCreateSrc = "${AppLifecycleDispatcher.NAME}.INSTANCE.${IAppDelegate.METHOD_ON_CREATE}(this);"
        try {
            def onCreateCtMethod = appCtClass.getDeclaredMethod(IAppDelegate.METHOD_ON_CREATE)
            onCreateCtMethod.insertBefore(dispatcherInitSrc)
            onCreateCtMethod.insertAfter(delegateCreateSrc)
        } catch (NotFoundException ignored) {
            def onCreateSrcBuilder = new StringBuilder()
            onCreateSrcBuilder.append("public void ${IAppDelegate.METHOD_ON_CREATE}() {")
            onCreateSrcBuilder.append(dispatcherInitSrc)
            onCreateSrcBuilder.append("super.${IAppDelegate.METHOD_ON_CREATE}();")
            onCreateSrcBuilder.append(delegateCreateSrc)
            onCreateSrcBuilder.append('}')
            appCtClass.addMethod(CtNewMethod.make(onCreateSrcBuilder.toString(), appCtClass))
        }

        // hook onTerminate
        def delegateTerminateSrc = "${AppLifecycleDispatcher.NAME}.INSTANCE.${IAppDelegate.METHOD_ON_TERMINATE}();"
        try {
            def onTerminateCtMethod = appCtClass.getDeclaredMethod(IAppDelegate.METHOD_ON_TERMINATE)
            onTerminateCtMethod.insertAfter(delegateTerminateSrc)
        } catch (NotFoundException ignored) {
            def onTerminateSrcBuilder = new StringBuilder()
            onTerminateSrcBuilder.append("public void ${IAppDelegate.METHOD_ON_TERMINATE}() {")
            onTerminateSrcBuilder.append("super.${IAppDelegate.METHOD_ON_TERMINATE}();")
            onTerminateSrcBuilder.append(delegateTerminateSrc)
            onTerminateSrcBuilder.append('}')
            appCtClass.addMethod(CtNewMethod.make(onTerminateSrcBuilder.toString(), appCtClass))
        }

        // hook onConfigurationChanged
        try {
            def onConfigChangedCtMethod = appCtClass.getDeclaredMethod(IAppDelegate.METHOD_ON_CONFIG_CHANGED)
            onConfigChangedCtMethod.insertAfter("${AppLifecycleDispatcher.NAME}.INSTANCE." +
                    "${IAppDelegate.METHOD_ON_CONFIG_CHANGED}(\$\$);")
        } catch (NotFoundException ignored) {
            def onConfigChangedSrcBuilder = new StringBuilder()
            onConfigChangedSrcBuilder.append("public void ${IAppDelegate.METHOD_ON_CONFIG_CHANGED}(" +
                    "${Configuration.NAME} newConfig) {")
            onConfigChangedSrcBuilder.append("super.${IAppDelegate.METHOD_ON_CONFIG_CHANGED}(newConfig);")
            onConfigChangedSrcBuilder.append("${AppLifecycleDispatcher.NAME}.INSTANCE." +
                    "${IAppDelegate.METHOD_ON_CONFIG_CHANGED}(newConfig);")
            onConfigChangedSrcBuilder.append('}')
            appCtClass.addMethod(CtNewMethod.make(onConfigChangedSrcBuilder.toString(), appCtClass))
        }

        // hook onLowMemory
        def delegateLowMemorySrc = "${AppLifecycleDispatcher.NAME}.INSTANCE.${IAppDelegate.METHOD_ON_LOW_MEMORY}();"
        try {
            def onLowMemoryCtMethod = appCtClass.getDeclaredMethod(IAppDelegate.METHOD_ON_LOW_MEMORY)
            onLowMemoryCtMethod.insertAfter(delegateLowMemorySrc)
        } catch (NotFoundException ignored) {
            def onLowMemorySrcBuilder = new StringBuilder()
            onLowMemorySrcBuilder.append("public void ${IAppDelegate.METHOD_ON_LOW_MEMORY}() {")
            onLowMemorySrcBuilder.append("super.${IAppDelegate.METHOD_ON_LOW_MEMORY}();")
            onLowMemorySrcBuilder.append(delegateLowMemorySrc)
            onLowMemorySrcBuilder.append('}')
            appCtClass.addMethod(CtNewMethod.make(onLowMemorySrcBuilder.toString(), appCtClass))
        }

        // hook onTrimMemory
        try {
            def onTrimMemoryCtMethod = appCtClass.getDeclaredMethod(IAppDelegate.METHOD_ON_TRIM_MEMORY)
            onTrimMemoryCtMethod.insertAfter("${AppLifecycleDispatcher.NAME}.INSTANCE." +
                    "${IAppDelegate.METHOD_ON_TRIM_MEMORY}(\$\$);")
        } catch (NotFoundException ignored) {
            def onTrimMemorySrcBuilder = new StringBuilder()
            onTrimMemorySrcBuilder.append("public void ${IAppDelegate.METHOD_ON_TRIM_MEMORY}(int level) {")
            onTrimMemorySrcBuilder.append("super.${IAppDelegate.METHOD_ON_TRIM_MEMORY}(level);")
            onTrimMemorySrcBuilder.append("${AppLifecycleDispatcher.NAME}.INSTANCE." +
                    "${IAppDelegate.METHOD_ON_TRIM_MEMORY}(level);")
            onTrimMemorySrcBuilder.append('}')
            appCtClass.addMethod(CtNewMethod.make(onTrimMemorySrcBuilder.toString(), appCtClass))
        }
        appCtClass.writeFile(writeDir.absolutePath)
    }

    static String parseInputKey(QualifiedContent content, Format format) {
        return DigestUtils.md5Hex("${content.name}#${content.contentTypes}#${content.scopes}#${format}")
    }

    static class ProviderInfo {

        @SerializedName('fromType')
        int fromType
        @SerializedName('fromFile')
        String fromFile
        @SerializedName('className')
        String className

        private ProviderInfo() {

        }

        String getFromTypeName() {
            return FromType.typeName(fromType)
        }

        static ProviderInfo fromDir(String fromFile, String className) {
            ProviderInfo info = new ProviderInfo()
            info.fromType = FromType.FROM_DIR
            info.fromFile = fromFile
            info.className = className
            return info
        }

        static ProviderInfo fromJar(String fromFile, String className) {
            ProviderInfo info = new ProviderInfo()
            info.fromType = FromType.FROM_JAR
            info.fromFile = fromFile
            info.className = className
            return info
        }

        static class FromType {
            static final int FROM_DIR = 0
            static final int FROM_JAR = 1

            static String typeName(int type) {
                if (type == FROM_JAR) {
                    return 'jar'
                } else {
                    return 'dir'
                }
            }
        }
    }

    static class ProviderBundle {
        @SerializedName('version')
        long version
        @SerializedName('data')
        Map<String, Map<String, ProviderInfo>> data = new LinkedHashMap<>()
    }

    static class ProviderContainer {

        private static final long VERSION = 1L
        private static final String CACHE_FILE = 'providers.json'

        private Gson gson = new Gson()
        private Map<String, Map<String, ProviderInfo>> data = new LinkedHashMap<>()
        private File cacheDir

        private Map<String, ProviderInfo> getProviderMapByKey(String key) {
            def map = data.get(key)
            if (map == null) {
                map = new LinkedHashMap<String, ProviderInfo>()
                data.put(key, map)
            }
            return map
        }

        void setCacheDir(File cacheDir) {
            this.cacheDir = cacheDir
        }

        void addProvider(String key, ProviderInfo provider) {
            getProviderMapByKey(key).put(provider.className, provider)
        }

        void removeProvider(String key, String className) {
            getProviderMapByKey(key).remove(className)
        }

        void remove(String key) {
            data.remove(key)
        }

        void clear() {
            data.clear()
        }

        boolean loadCache() {
            if (cacheDir == null) {
                return false
            }
            File cacheFile = new File(cacheDir, CACHE_FILE)
            if (!cacheFile.exists()) {
                return false
            }
            boolean result = true
            ProviderBundle bundle
            Reader reader
            try {
                reader = new FileReader(cacheFile)
                bundle = gson.fromJson(reader, ProviderBundle.class)
            } catch (Exception e) {
                Logger.e("loadCache error, ${e.message}")
                result = false
            } finally {
                if (reader != null) {
                    reader.close()
                }
            }
            if (result && bundle != null) {
                if (bundle.version != VERSION) {
                    result = false
                } else {
                    if (bundle.data != null) {
                        data.putAll(bundle.data)
                    }
                }
            }
            return result
        }

        void saveCache() {
            if (cacheDir == null) {
                return
            }
            File cacheFile = new File(cacheDir, CACHE_FILE)
            FileUtils.deleteIfExists(cacheFile)
            cacheDir.mkdirs()
            def bundle = new ProviderBundle()
            bundle.version = VERSION
            bundle.data = data
            JsonWriter jsonWriter
            try {
                jsonWriter = gson.newJsonWriter(new FileWriter(cacheFile))
                gson.toJson(bundle, ProviderBundle.class, jsonWriter)
                jsonWriter.flush()
            } catch (Exception e) {
                Logger.e("saveCache error, ${e.message}")
            } finally {
                if (jsonWriter != null) {
                    jsonWriter.close()
                }
            }
        }

        List<ProviderInfo> getProviders() {
            def list = new LinkedList()
            data.values().each { map ->
                map.values().each { provider ->
                    list.add(provider)
                }
            }
            return list
        }
    }
}
