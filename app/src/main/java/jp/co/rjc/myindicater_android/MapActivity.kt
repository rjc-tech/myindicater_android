package jp.co.rjc.myindicater_android

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions


class MapActivity : AppCompatActivity(), OnMapReadyCallback,SearchDestinationTask.SearchDestinationTaskListener {

    private var mMap: GoogleMap? = null
    private var mProgress : ProgressDialogFragment? = null

    val REQUEST_CODE = 1

    private var mDestination : Place? = null

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
//                val mGoogleApiClient = GoogleApiClient.Builder(this)
//                        .addApi(Places.GEO_DATA_API)
//                        .addApi(Places.PLACE_DETECTION_API)
//                        .enableAutoManage(this, GoogleApiClient.OnConnectionFailedListener {
//                        } )
//                        .build()
//                mGoogleApiClient.connect()
            }
        })

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    /**
     * 2座標間を直線で結びます.
     */
    private fun drawPolyline(p0 : LatLng, p1 : LatLng){
        val options = PolylineOptions()
        options.add(p0)
                .add(p1)
                .color(Color.RED)
                .geodesic(false)

        mMap?.addPolyline(options)
    }

    /**
     * 2座標間の距離を求めます.
     *
     * return Float 2座標間の距離(m)
     */
    private fun calcDistance(p0 : LatLng, p1 : LatLng) : Float{
        val distance = FloatArray(1)
        Location.distanceBetween(p0.latitude,p0.longitude,p1.latitude,p1.longitude,distance)

        return distance[0]
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
            clearAllMarker()
            mDestination = data!!.getParcelableExtra("place")
            mDestination?.let { addMarker(it) }
            mDestination?.let { moveMapToPlace(it) }
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

    private fun clearAllMarker(){
        mMap?.clear()
    }

    private fun addMarker(place : Place){
        val position = LatLng(place.latitude, place.longitude)
        mMap?.addMarker(MarkerOptions().position(position).title(place.name))

    }

    private fun moveMapToPlace(place : Place){
        val position = LatLng(place.latitude, place.longitude)
        mMap?.moveCamera(CameraUpdateFactory.newLatLng(position))
    }

}