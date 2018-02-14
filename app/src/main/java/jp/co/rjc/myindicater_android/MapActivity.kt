package jp.co.rjc.myindicater_android

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val hello = findViewById(R.id.text)
    }
}
