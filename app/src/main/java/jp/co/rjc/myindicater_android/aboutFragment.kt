package jp.co.rjc.myindicater_android

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.Toast

class aboutFragment : Fragment(), View.OnClickListener {

    private var view1: View? = null

    init {
        var context: Context? = getContext()
        context = getContext()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    object ToastView {
        fun showToast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.goal_button -> {
                MapActivity.ToastView.showToast(context, "goal_button")
            }
            else -> {
                MapActivity.ToastView.showToast(context, "else")
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        view1 = inflater!!.inflate(R.layout.fragment_about, container, false)

        return view1
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item!!.itemId

        return super.onOptionsItemSelected(item)
    }
}