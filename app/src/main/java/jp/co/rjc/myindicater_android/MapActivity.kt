package jp.co.rjc.myindicater_android

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import org.w3c.dom.Text
import android.content.Intent
import android.net.Uri


class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // 検索ボタンを押したら、入力された文字列から場所を特定してマップの該当の場所へズーム
        val searchButton = findViewById<Button>(R.id.search_button)
        searchButton.setOnClickListener {
            val place = findViewById<TextView>(R.id.search_destination)
            // googleのAPIでキーワードから場所検索
            // https://developers.google.com/maps/documentation/android-api/intents?hl=ja
            val gmmIntentUri = Uri.parse("geo:0,0?q=" + place)
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.`package` = "com.google.android.apps.maps"
            startActivity(mapIntent)
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney, Australia, and move the camera.
//        val sydney = LatLng(-34, 151)
//        mMap!!.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}