package jp.co.rjc.myindicater_android

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import jp.co.rjc.myindicater_android.R
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast

class MapActivity : AppCompatActivity(), View.OnClickListener {

    private var toolbar: Toolbar? = null
    private var button1: Button? = null
    private var button2: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "TEST"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        button1 = findViewById(R.id.goal_button)
        button1?.setOnClickListener(this)
        button2 = findViewById(R.id.about_button)
        button2?.setOnClickListener(this)

    }

    object ToastView {
        fun showToast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.goal_button -> {
                ToastView.showToast(this, "goal_button")
            }
            R.id.about_button -> {
                ToastView.showToast(this, "about")
            }
            else -> {
                ToastView.showToast(this, "else")
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return false
    }
}
