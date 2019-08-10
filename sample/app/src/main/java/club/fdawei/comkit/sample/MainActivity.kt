package club.fdawei.comkit.sample

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import club.fdawei.nrouter.annotation.Autowired
import club.fdawei.nrouter.api.NRouter
import kotlinx.android.synthetic.main.activity_main.*

const val PAGE_MESSAGE = 1
const val PAGE_CONTACT = 2
const val PAGE_ME = 3

class MainActivity : AppCompatActivity() {

    @Autowired(path = "/message/fragment/home")
    var messageFragment: Fragment? = null

    @Autowired(path = "/contact/fragment/home")
    var contactFragment: Fragment? = null

    @Autowired(path = "/me/fragment/home")
    var meFragment: Fragment? = null

    private var currPage: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvMsgTab.setOnClickListener { changePage(PAGE_MESSAGE) }
        tvContactTab.setOnClickListener { changePage(PAGE_CONTACT) }
        tvMeTab.setOnClickListener { changePage(PAGE_ME) }

        NRouter.injector().inject(this)

        changePage(PAGE_MESSAGE)
    }

    private fun changePage(page: Int) {
        if (currPage == page) {
            return
        }
        val transaction = supportFragmentManager.beginTransaction()
        when (page) {
            PAGE_MESSAGE -> messageFragment?.run {
                transaction.replace(R.id.flPageContainer, this)
            }
            PAGE_CONTACT -> contactFragment?.run {
                transaction.replace(R.id.flPageContainer, this)
            }
            PAGE_ME -> meFragment?.run {
                transaction.replace(R.id.flPageContainer, this)
            }
        }
        transaction.commit()
        currPage = page
    }
}
