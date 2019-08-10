package club.fdawei.comkit.sample.me

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import club.fdawei.nrouter.annotation.Autowired
import club.fdawei.nrouter.api.NRouter

class MeActivity : AppCompatActivity() {

    @Autowired(path = "/me/fragment/home")
    var meFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_me)

        NRouter.injector().inject(this)

        if (meFragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.flContainer, meFragment!!)
                .commit()
        }
    }
}
