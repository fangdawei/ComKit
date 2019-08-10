package club.fdawei.comkit.sample.message

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import club.fdawei.nrouter.annotation.Autowired
import club.fdawei.nrouter.api.NRouter

class MessageActivity : AppCompatActivity() {

    @Autowired(path = "/message/fragment/home")
    var messageFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        NRouter.injector().inject(this)

        if (messageFragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.flContainer, messageFragment!!)
                .commit()
        }
    }
}
