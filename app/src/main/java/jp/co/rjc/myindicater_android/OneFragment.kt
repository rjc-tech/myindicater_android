/**
 * Created by Parsania Hardik on 27-Jan-18.
 */
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import jp.co.rjc.myindicater_android.MapActivity
import jp.co.rjc.myindicater_android.R
import kotlinx.android.synthetic.*

class OneFragment : Fragment(), View.OnClickListener {

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
        view1 = inflater!!.inflate(R.layout.fragment_one, container, false)

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