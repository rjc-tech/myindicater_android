package jp.co.rjc.myindicater_android

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
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

    private val ARRIVAL_DISTANCE = 50
    private val KILO_METER_VALUE = 1000
    private val KILO_METER_UNIT = "km"
    private val KEY_DESTINATION_NAME = "key_destination_name"
    private val KEY_DESTINATION_LATITUDE = "key_destination_latitude"
    private val KEY_DESTINATION_LONGITUDE = "key_destination_longitude"
    private val KEY_DESTINATION_ID = "key_destination_id"
    private val KEY_DESTINATION_PLACE_ID = "key_destination_place_id"
    private var mIsShowArrivalToast: Boolean = false

    private var mMap: GoogleMap? = null
    private var mProgress: ProgressDialogFragment? = null

    val REQUEST_CODE = 1
    val REQUEST_PERMISSION = 2

    // 目的地のPlace
    private var mDestination: Place? = null

    // 現在地のPlace
    private var mHere: Place? = null

    private var mLayoutSearch: View? = null

    private var mLayoutDistance: View? = null

    private var mTextDistance: TextView? = null

    private var mLocationManager: LocationManager? = null

    private var mButtonMapType: Button? = null

    private var mIsFirstLocationChanged : Boolean = true

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
            // ソフトキーボードを非表示にする
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(window.decorView.
                    findViewById<View>(android.R.id.content).windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)

            val word = searchText.text.toString()
            if (!word.isEmpty() && isNetworkAvailable()) {

                // 検索開始時は不要なのでトルツメ
                mLayoutSearch?.visibility = View.GONE

                showProgress()

                val searchTask = SearchDestinationTask(this)
                searchTask.setOnSearchDestinationTaskListener(this)
                searchTask.execute(word)
            } else if (!isNetworkAvailable()){
                Toast.makeText(this, "インターネット接続状況をご確認ください",Toast.LENGTH_LONG).show()
            }
        })

        val currentButton = findViewById<Button>(R.id.button_current)
        currentButton!!.isEnabled = false
        currentButton.setOnClickListener({
            if(checkGps()){
                mHere.let { moveMapToPlace(mHere!!) }
            }
        })

        mButtonMapType = findViewById<Button>(R.id.button_map_type)
        mButtonMapType!!.isEnabled = false
        mButtonMapType!!.setOnClickListener({
            mButtonMapType.let {
                when (mMap?.mapType) {
                    GoogleMap.MAP_TYPE_NORMAL -> {
                        mMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
                        mButtonMapType?.setText("地図")
                    }
                    GoogleMap.MAP_TYPE_SATELLITE -> {
                        mMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
                        mButtonMapType?.setText("航空写真")
                    }
                    else -> {
                    }
                }
            }
        })

        mDestination = loadDestination()
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        if(checkGps()){
            if (checkPermission()) {
                mLocationManager?.requestLocationUpdates(GPS_PROVIDER,
                        0, 0f, this)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_PERMISSION)
            }
        } else {
            // GPSをONにさせる
            Toast.makeText(this, "GPS機能をONに設定してください",Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }

        super.onResume()

    }

    override fun onPause() {
        mLocationManager?.removeUpdates(this)
        super.onPause()
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION) {
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "位置情報取得許可がないためアプリを終了します", Toast.LENGTH_SHORT).show()
                    finish()
                    return
                }
            }
            mLocationManager?.requestLocationUpdates(GPS_PROVIDER,
                    0, 0f, this)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {

        if (mMap == null) {
            mMap = googleMap
            //現在地表示ボタンはオリジナルのものを表示するのでfalse
            mMap!!.uiSettings.isMyLocationButtonEnabled = false
            mMap!!.uiSettings.isZoomControlsEnabled = true

            mButtonMapType!!.isEnabled = true
            findViewById<Button>(R.id.button_current)!!.isEnabled = true
        }
        if (checkPermission()) {
            mMap?.isMyLocationEnabled = true
        }

    }

    /**
     * 2座標間を直線で結びます.
     */
    private fun drawPolyline(p0: LatLng, p1: LatLng) {
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
    private fun calcDistance(p0: LatLng, p1: LatLng): Float {
        val distance = FloatArray(1)
        Location.distanceBetween(p0.latitude, p0.longitude, p1.latitude, p1.longitude, distance)
        // 1の位を四捨五入
        return Math.round(distance[0]/10)*10.toFloat()
    }

    private fun showProgress() {
        dismissProgress()
        mProgress = ProgressDialogFragment()
        mProgress?.show(supportFragmentManager, "0")
    }

    private fun dismissProgress() {
        mProgress?.dismiss()
        mProgress = null
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            clearAllMarker()
            mIsShowArrivalToast = false
            mDestination = data!!.getParcelableExtra("place")
            updateMap()
            mDestination?.let {
                moveMapToPlace(it)
            }
        }
    }

    private fun updateMap() {
        clearAllMarker()

        mDestination?.let {
            addMarker(it)
        }

        // 現在地と目的地がそろっている場合は線で結ぶ
        if (mHere != null && mDestination != null) {
            drawPolyline(LatLng(mHere!!.latitude, mHere!!.longitude),
                    LatLng(mDestination!!.latitude, mDestination!!.longitude))

            // マップから取得できた距離(m)
            val distance = calcDistance(LatLng(mHere!!.latitude, mHere!!.longitude),
                    LatLng(mDestination!!.latitude, mDestination!!.longitude))

            mTextDistance?.text = (distance / KILO_METER_VALUE).toString() + KILO_METER_UNIT
            mLayoutDistance?.visibility = View.VISIBLE

            if (distance < ARRIVAL_DISTANCE && !mIsShowArrivalToast) {
                mIsShowArrivalToast = true
                Toast.makeText(this, "目的地付近に到着しました", Toast.LENGTH_LONG).show()
            }

        } else {
            mLayoutDistance?.visibility = View.GONE
        }

    }

    override fun onSuccess(list: ArrayList<Place>) {
        dismissProgress()
        val intent = Intent(this, SearchResultActivity::class.java)
        intent.putParcelableArrayListExtra("place_list", list)

        startActivityForResult(intent, REQUEST_CODE)

    }

    override fun onFailed() {
        dismissProgress()
        Toast.makeText(this, getString(R.string.failed_get_location), Toast.LENGTH_SHORT).show()
    }

    private fun clearAllMarker() {
        mMap?.clear()
    }

    private fun addMarker(place: Place) {
        val position = LatLng(place.latitude, place.longitude)
        mMap?.addMarker(MarkerOptions().position(position).title(place.name))

    }

    private fun moveMapToPlace(place: Place) {
        val position = LatLng(place.latitude, place.longitude)
        mMap?.moveCamera(CameraUpdateFactory.newLatLng(position))
    }

    private fun checkPermission(): Boolean {
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
    }

    private fun checkGps(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val locationMode = Settings.Secure.getInt(contentResolver, Settings.Secure.LOCATION_MODE)

            locationMode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY ||
                    locationMode == Settings.Secure.LOCATION_MODE_SENSORS_ONLY
        } else {
            val providers = Settings.Secure.getString(
                    contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED)
            providers.contains("gps")
        }

    }

    private fun loadDestination() : Place? {
        val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val place = Place()

        place.name = pref.getString(KEY_DESTINATION_NAME, null)
        if(place.name == null){
            return null
        }

        place.latitude = pref.getFloat(KEY_DESTINATION_LATITUDE, 0f).toDouble()
        place.longitude = pref.getFloat(KEY_DESTINATION_LONGITUDE, 0f).toDouble()
        place.id = pref.getString(KEY_DESTINATION_ID, "")
        place.place_id = pref.getString(KEY_DESTINATION_PLACE_ID, "")

        return place
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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

        // 初回のみ現在地にマップ移動する
        if (mIsFirstLocationChanged) {
            moveMapToPlace(mHere!!)
            mIsFirstLocationChanged = false
        }

        updateMap()

    }

    override fun onProviderDisabled(p0: String?) {
        // ignore
    }

    override fun onProviderEnabled(p0: String?) {
        // ignore
    }

    private fun isNetworkAvailable() : Boolean{
        val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo

        return networkInfo != null && networkInfo.isConnectedOrConnecting
    }

}