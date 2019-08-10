package club.fdawei.comkit.sample.me

import android.app.Application
import android.util.Log
import club.fdawei.comkit.annotation.AppDelegate
import club.fdawei.comkit.api.app.IAppDelegate

/**
 * Create by david on 2019/07/21.
 */
@AppDelegate
class MeAppDelegate : IAppDelegate {
    override fun onCreate(app: Application) {
        Log.i("me", "MeAppDelegate onCreate")
    }
}