package jp.co.rjc.myindicater_android

import android.support.v7.app.AppCompatActivity

import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import android.os.Bundle

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.annotation.NonNull
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Toast
import android.location.LocationManager
import android.location.Criteria
import android.location.LocationListener
import android.hardware.SensorManager.getAltitude
import android.location.Location
import android.location.LocationProvider
import com.google.android.gms.maps.model.LatLngBounds



class MapsActivity : AppCompatActivity(), OnMapReadyCallback,LocationListener {

    private lateinit var mMap: GoogleMap
    private val REQUEST_PERMISSION = 10
    private lateinit var myLocationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_main)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // 位置情報許可の確認
    fun checkPermission():Boolean {
        // 既に許可している
        if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.v("kyoka", "きょか")
            // プロバイダーから位置情報の更新
            val criteria = Criteria()
            myLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val provider:String = myLocationManager.getBestProvider(criteria, true)
            val lastLocation = myLocationManager.getLastKnownLocation(provider)

            if(lastLocation != null) {
                setLocation(lastLocation);
            }

            getlog(lastLocation,"kyoka")

            mMap.setMyLocationEnabled(true)
            Toast.makeText(this, "Provider=" + provider, Toast.LENGTH_SHORT).show();
            // 位置情報の更新（checkSelfPermission後じゃなきゃ定義不可）
            myLocationManager.requestLocationUpdates(provider, 0L, 0f, this)

            return true

//            locationActivity()
        } else {
            Log.v("test", "でふぉ")
            setDefaultLocation()
            requestLocationPermission()
            // 仮
            return false
        }// 拒否していた場合
    }

    // 許可を求める
    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION)
        } else {
            val toast = Toast.makeText(this,
                    "許可されないとアプリが実行できません", Toast.LENGTH_SHORT)
            toast.show()

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSION)
        }
    }

    // 結果の受け取り
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //locationActivity()
            } else {
                // それでも拒否された時の対応
                val toast = Toast.makeText(this,
                        "これ以上なにもできません", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        when (status) {
            LocationProvider.AVAILABLE -> Log.v("Status", "AVAILABLE")
            LocationProvider.OUT_OF_SERVICE -> Log.v("Status", "OUT_OF_SERVICE")
            LocationProvider.TEMPORARILY_UNAVAILABLE -> Log.v("Status", "TEMPORARILY_UNAVAILABLE")
        }
    }

    /**
     * 位置情報が更新されたことを検知（位置情報更新時に呼び出し）
     *
     * ＃ このコールバックメソッドはLocationListenerをimplementsする必要あり
     */
    override fun onLocationChanged(location: Location) {
        setLocation(location)
        getlog(location,"----------")
    }

    override fun onProviderDisabled(provider: String) {
    }
    override fun onProviderEnabled(provider: String) {
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        checkPermission()
            // Add a marker in Sydney and move the camera
//            val sydney = LatLng(35.709026, 139.731992)
            // マーカーに表示するメイン情報と補足情報追加し、マップに表示
//            mMap.addMarker(MarkerOptions().position(sydney).title("テストメイン").snippet("☆彡補足情報☆彡")).showInfoWindow()
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
//            val cUpdate = CameraUpdateFactory.newLatLngZoom(sydney, 15f)
//            mMap.moveCamera(cUpdate)
    }


    private fun setDefaultLocation() {
        val tokyo = LatLng(35.681298, 139.766247)
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tokyo, 15f))
        zoomMap(35.681298, 139.766247)
    }

    private fun setLocation(location: Location) {
        val myLocation = LatLng(location.latitude, location.longitude)
        mMap.addMarker(MarkerOptions().position(myLocation).title("てすとお"))
        zoomMap(location.latitude, location.longitude)
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f))
    }


    private fun zoomMap(latitude: Double, longitude: Double) {
        // 表示する東西南北の緯度経度を設定
        val south = latitude * (1 - 0.00005)
        val west = longitude * (1 - 0.00005)
        val north = latitude * (1 + 0.00005)
        val east = longitude * (1 + 0.00005)

        // LatLngBounds (LatLng southwest, LatLng northeast)
        val bounds = LatLngBounds.builder().include(LatLng(south, west)).include(LatLng(north, east)).build()

        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels

        // static CameraUpdate.newLatLngBounds(LatLngBounds bounds, int width, int height, int padding)
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, 0))

    }


    private fun getlog(location: Location, str:String) {
        Log.v("----------", str)
        Log.v("Latitude", location.latitude.toString())
        Log.v("Longitude", location.longitude.toString())
        Log.v("Accuracy", location.accuracy.toString())
        Log.v("Altitude", location.altitude.toString())
        Log.v("Time", location.time.toString())
        Log.v("Speed", location.speed.toString())
        Log.v("Bearing", location.bearing.toString())
    }
}