package jp.co.rjc.myindicater_android

import android.app.Activity
import android.app.DialogFragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import jp.co.rjc.myindicater_android.R
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapActivity : AppCompatActivity(), OnMapReadyCallback,SearchDestinationTask.SearchDestinationTaskListener {

    private var mMap: GoogleMap? = null
    private var mProgress : ProgressDialogFragment? = null

    val REQUEST_CODE = 1

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

                showProgress()

                val searchTask = SearchDestinationTask(this)
                searchTask.setOnSearchDestinationTaskListener(this)
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
        val sydney = LatLng(-34.0, 151.0)
        mMap!!.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        mMap!!.mapType = GoogleMap.MAP_TYPE_HYBRID
    }

    private fun showProgress(){
        dismissProgress()
        mProgress = ProgressDialogFragment()
        mProgress?.show(supportFragmentManager, "0")
    }

    private fun dismissProgress(){
        mProgress?.dismiss()
        mProgress = null
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            val place = data!!.getParcelableExtra<Place>("place")
        }
    }

    override fun onSuccess(list: ArrayList<Place>) {
        dismissProgress()
        val intent = Intent(this, SearchResultActivity::class.java)
        intent.putParcelableArrayListExtra("place_list",list)

        startActivityForResult(intent, REQUEST_CODE)

    }

    override fun onFailed() {
        dismissProgress()
    }


}