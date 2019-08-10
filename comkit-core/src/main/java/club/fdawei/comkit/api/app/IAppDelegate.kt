package club.fdawei.comkit.api.app

import android.app.Application
import android.content.res.Configuration

/**
 * Created by david on 2019/07/19.
 */
interface IAppDelegate {
    fun onCreate(app: Application) {}

    fun onTerminate() {}

    fun onConfigurationChanged(newConfig: Configuration) {}

    fun onLowMemory() {}

    fun onTrimMemory(level: Int) {}
}