package club.fdawei.comkit.sample

import android.app.Application
import android.util.Log
import club.fdawei.comkit.annotation.AppDelegate
import club.fdawei.comkit.api.app.IAppDelegate

/**
 * Created by david on 2019/07/22.
 */
@AppDelegate
class SampleAppDelegate : IAppDelegate {
    override fun onCreate(app: Application) {
        Log.i("sample", "SampleAppDelegate onCreate")
    }
}