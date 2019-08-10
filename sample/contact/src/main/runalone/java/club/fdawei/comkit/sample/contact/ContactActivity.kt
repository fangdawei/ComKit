package club.fdawei.comkit.sample.contact

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import club.fdawei.nrouter.annotation.Autowired
import club.fdawei.nrouter.api.NRouter

class ContactActivity : AppCompatActivity() {

    @Autowired(path = "/contact/fragment/home")
    var contactFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        NRouter.injector().inject(this)

        if (contactFragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.flContainer, contactFragment!!)
                .commit()
        }
    }
}
