package jp.co.rjc.myindicater_android

import android.app.Activity
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney, Australia, and move the camera.
        val sydney = LatLng(-34.0, 151.0)
        mMap!!.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))

        // 東京駅
        val tokyo = LatLng(35.681298, 139.7640529)
        mMap!!.addMarker(MarkerOptions().position(tokyo).title("Marker in tokyo"))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(tokyo))

        drawPolyline(sydney, tokyo)
        findViewById<TextView>(R.id.distance).setText((calcDistance(sydney, tokyo)/1000).toString()+"km")

    }

    private fun drawPolyline(p0 : LatLng, p1 : LatLng){
        val options = PolylineOptions()
        options.add(p0)
                .add(p1)
                .color(Color.RED)
                .geodesic(false)

        mMap?.addPolyline(options)
    }

    private fun calcDistance(p0 : LatLng, p1 : LatLng) : Float{
        val distance = FloatArray(1)
        Location.distanceBetween(p0.latitude,p0.longitude,p1.latitude,p1.longitude,distance)

        return distance[0]
    }
}