package club.fdawei.comkit.sample.contact

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import club.fdawei.nrouter.annotation.Route
import kotlinx.android.synthetic.main.fragment_contact.*

/**
 * Created by david on 2019/07/16.
 */
@Route(path = "/contact/fragment/home")
class ContactFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun initListeners() {
        tvAdd.setOnClickListener {
            val intent = Intent(activity, ContactEditActivity::class.java)
            startActivity(intent)
        }
    }
}