package club.fdawei.comkit.sample.base

import android.app.Application
import android.util.Log
import club.fdawei.comkit.annotation.AppDelegate
import club.fdawei.comkit.api.app.IAppDelegate
import club.fdawei.nrouter.api.NRouter

/**
 * Created by david on 2019/07/19.
 */
@AppDelegate
class CommonAppDelegate : IAppDelegate {
    override fun onCreate(app: Application) {
        Log.i("base", "CommonAppDelegate onCreate")

        NRouter.debug = BuildConfig.DEBUG
        NRouter.init(app)
    }
}