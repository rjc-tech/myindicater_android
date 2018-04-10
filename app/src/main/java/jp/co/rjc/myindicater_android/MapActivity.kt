package jp.co.rjc.myindicater_android

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import jp.co.rjc.myindicater_android.R
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.Places


class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val searchText = findViewById<EditText>(R.id.editText)
        val searchButton = findViewById<Button>(R.id.button3)
        searchButton.setOnClickListener({
            val word = searchText.text.toString()
            if(!word.isNullOrEmpty()){
                val searchTask = SearchDestinationTask()
                searchTask.execute(word)

                // TODO
                val mGoogleApiClient = GoogleApiClient.Builder(this)
                        .addApi(Places.GEO_DATA_API)
                        .addApi(Places.PLACE_DETECTION_API)
                        .enableAutoManage(this, GoogleApiClient.OnConnectionFailedListener {
                        } )
                        .build()
                mGoogleApiClient.connect()
            }
        })

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney, Australia, and move the camera.
//        val sydney = LatLng(-34, 151)
//        mMap!!.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}