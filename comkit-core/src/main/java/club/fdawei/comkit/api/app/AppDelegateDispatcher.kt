package club.fdawei.comkit.api.app

import android.app.Application
import android.content.res.Configuration

/**
 * Created by david on 2019/07/19.
 */
object AppDelegateDispatcher : IAppDelegate {

    private var delegateContainer: AbsAppDelegateContainer? = null

    fun init(container: AbsAppDelegateContainer) {
        delegateContainer = container
    }

    override fun onCreate(app: Application) {
        delegateContainer?.delegates?.forEach {
            it.onCreate(app)
        }
    }

    override fun onTerminate() {
        delegateContainer?.delegates?.forEach {
            it.onTerminate()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        delegateContainer?.delegates?.forEach {
            it.onConfigurationChanged(newConfig)
        }
    }

    override fun onLowMemory() {
        delegateContainer?.delegates?.forEach {
            it.onLowMemory()
        }
    }

    override fun onTrimMemory(level: Int) {
        delegateContainer?.delegates?.forEach {
            it.onTrimMemory(level)
        }
    }
}