package club.fdawei.comkit.plugin.common

/**
 * Created by david on 2019/07/22.
 */
class ClassInfo {

    final static class AppDelegateContainer {
        static final String NAME = 'comkit.generated.AppDelegateContainer'
    }

    final static class ModuleProvider {
        static boolean isModuleProvider(String className) {
            return className ==~ /^comkit\.generated\.providers\.ComKit_\S+_Provider$/
        }
    }

    final static class AbsAppDelegateContainer {
        static final String NAME = 'club.fdawei.comkit.api.app.AbsAppDelegateContainer'
        static final String METHOD_INIT_PROVIDERS = 'initProviders'
        static final String METHOD_ADD_PROVIDER = 'addProvider'
    }

    final static class AppDelegateDispatcher {
        static final String NAME = 'club.fdawei.comkit.api.app.AppDelegateDispatcher'
        static final String METHOD_INIT = 'init'
    }

    final static class Configuration {
        static final String NAME = 'android.content.res.Configuration'
    }

    final static class IAppDelegate {
        static final String METHOD_ON_CREATE = 'onCreate'
        static final String METHOD_ON_TERMINATE = 'onTerminate'
        static final String METHOD_ON_CONFIG_CHANGED = 'onConfigurationChanged'
        static final String METHOD_ON_LOW_MEMORY = 'onLowMemory'
        static final String METHOD_ON_TRIM_MEMORY = 'onTrimMemory'
    }
}
