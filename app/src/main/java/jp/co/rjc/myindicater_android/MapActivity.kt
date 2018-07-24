package jp.co.rjc.myindicater_android

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions


class MapActivity : AppCompatActivity(), OnMapReadyCallback,
        SearchDestinationTask.SearchDestinationTaskListener,
        LocationListener {

    private var mMap: GoogleMap? = null
    private var mProgress : ProgressDialogFragment? = null

    val REQUEST_CODE = 1

    // 目的地のPlace
    private var mDestination : Place? = null

    // 現在地のPlace
    private var mHere : Place? = null

    private var mLayoutSearch: View? = null

    private var mLayoutDistance: View? = null

    private var mTextDistance: TextView? = null

    private var mLocationManager: LocationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        setSupportActionBar(findViewById(R.id.toolbar))

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mLocationManager = getSystemService(LOCATION_SERVICE) as LocationManager?

        mLayoutSearch = findViewById<LinearLayout>(R.id.layout_search)
        mLayoutDistance = findViewById<LinearLayout>(R.id.layout_distance)
        mTextDistance = findViewById(R.id.distance)

        val searchText = findViewById<EditText>(R.id.editText)
        val searchButton = findViewById<Button>(R.id.button3)
        searchButton.setOnClickListener({
            val word = searchText.text.toString()
            if(!word.isNullOrEmpty()){

                // 検索開始時は不要なのでトルツメ
                mLayoutSearch?.visibility = View.GONE

                showProgress()

                val searchTask = SearchDestinationTask(this)
                searchTask.setOnSearchDestinationTaskListener(this)
                searchTask.execute(word)

            }
        })

        val currentButton = findViewById<Button>(R.id.button_current)
        currentButton.setOnClickListener({
            mHere.let { moveMapToPlace(mHere!!) }
        })

    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        mLocationManager?.requestLocationUpdates(GPS_PROVIDER,
                0,0f,this)
        super.onResume()

    }

    override fun onPause() {
        mLocationManager?.removeUpdates(this)
        super.onPause()
    }

    override fun onMapReady(googleMap: GoogleMap) {

        if (mMap == null) {
            mMap = googleMap
            //現在地表示ボタンはオリジナルのものを表示するのでfalse
            mMap!!.uiSettings.isMyLocationButtonEnabled = false
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap?.isMyLocationEnabled = true
        }

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
            updateMap()
            mDestination?.let {
                moveMapToPlace(it)
            }
        }
    }

    private fun updateMap(){
        clearAllMarker()

        mDestination?.let {
            addMarker(it)
        }

        // 現在地と目的地がそろっている場合は線で結ぶ
        if(mHere != null && mDestination != null){
            drawPolyline(LatLng(mHere!!.latitude, mHere!!.longitude),
                    LatLng(mDestination!!.latitude, mDestination!!.longitude))

            // マップから取得できた距離(m)
            val distance = calcDistance(LatLng(mHere!!.latitude, mHere!!.longitude),
                    LatLng(mDestination!!.latitude, mDestination!!.longitude))/1000

            mTextDistance?.text = distance.toString() + "km"
            mLayoutDistance?.visibility = View.VISIBLE

        } else {
            mLayoutDistance?.visibility = View.GONE
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


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
        // このアプリについての処理
//            R.id.action_about ->
            R.id.action_search ->
                when (mLayoutSearch?.visibility) {
                    View.VISIBLE -> mLayoutSearch?.visibility = View.GONE
                    else -> mLayoutSearch?.visibility = View.VISIBLE
                }

        }
        return false
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
    }

    override fun onLocationChanged(p0: Location?) {
        mHere = Place()
        mHere?.latitude = p0?.latitude!!
        mHere?.longitude = p0?.longitude!!

        moveMapToPlace(mHere!!)
        updateMap()

    }

    override fun onProviderDisabled(p0: String?) {
    }

    override fun onProviderEnabled(p0: String?) {
    }


}