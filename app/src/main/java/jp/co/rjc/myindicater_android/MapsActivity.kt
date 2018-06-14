package jp.co.rjc.myindicater_android

import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Toast
import android.location.LocationListener
import android.os.Looper
import android.support.v4.app.FragmentActivity
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult

class MapsActivity : FragmentActivity(), OnMapReadyCallback,
GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
LocationListener, GoogleMap.OnMyLocationButtonClickListener, LocationSource {

    // lateinit：初期化遅らせ　var：可変 val：固定
    private lateinit var mMap:GoogleMap
    private lateinit var mGoogleApiClient:GoogleApiClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var onLocationChangedListener: LocationSource.OnLocationChangedListener
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // ★★PRIORITY_HIGH_ACCURACY           → 出来る限り高精度の位置情報を表示する。
    // ★★PRIORITY_NO_POWER                → 受動的(自分からはリクエストしない)
    // ★★PRIORITY_BALANCED_POWER_ACCURACY → ブロックレベルの精度で得たい場合
    // ★★PRIORITY_LOW_POWER               → 都市レベル (10km)
    private val priority = arrayListOf(LocationRequest.PRIORITY_HIGH_ACCURACY, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY,
        LocationRequest.PRIORITY_LOW_POWER, LocationRequest.PRIORITY_NO_POWER)
    private var locationPriority:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("debug", "onCreate START")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);
        createLocationCallback()
        // LocationRequest を生成して精度、インターバルを設定
        // ★★参考URL:https://qiita.com/mattak/items/22be63f57b71287164bf
        // ★★位置情報取得用   プロパティによって精度や電力消費量などが変わるので、用途によって使い分けが必要
        locationRequest = LocationRequest.create()

        // 測位の精度、消費電力の優先度
        locationPriority = priority[1]

        if (locationPriority === priority[0]) {
            // 位置情報の精度を優先する場合(★★消費電力：大)
            locationRequest.setPriority(locationPriority)
            locationRequest.setInterval(5000)
            locationRequest.setFastestInterval(16)
            Log.d("debug", "locationPriority" + priority[0])
        } else if (locationPriority === priority[1]) {
            // 消費電力を考慮する場合
            locationRequest.setPriority(locationPriority)
            // ★★省電力時のインターバル
            locationRequest.setInterval(60000)
            // ★★素早く位置情報を取得したいときのインターバル
            locationRequest.setFastestInterval(16)
            Log.d("debug", "locationPriority" + priority[1])
        } else if (locationPriority === priority[2]) {
            // "city" level accuracy
            locationRequest.setPriority(locationPriority)
            Log.d("debug", "locationPriority" + priority[2])
        } else {
            // 外部からのトリガーでの測位のみ
            locationRequest.setPriority(locationPriority)
            Log.d("debug", "locationPriority ？？？")
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // ★★Googleが提供しているサービスに対して、接続するためのクライアント
        mGoogleApiClient =
                GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build()

        Log.d("debug", "onCreate END")
    }

    // locationのコールバックを受け取る
    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
            }
        }
    }

    // onResumeフェーズに入ったら接続
    override fun onResume() {
        super.onResume()
        mGoogleApiClient.connect()
    }

    // onPauseで切断
    public override fun onPause() {
        super.onPause()
        stopLocationUpdates()
       mGoogleApiClient.disconnect()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onMapReady(googleMap:GoogleMap) {
        Log.d("debug", "onMapReady START")
        // check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("debug", "permission granted")

            mMap = googleMap
            // default の LocationSource から自前のsourceに変更する
            mMap.setLocationSource(this)
            // ★★ マップの現在地レイヤーを有効 ⇒ マップ右上に現在地ボタンが表示される
            // ★★ [現在地ボタン](/maps/documentation/android-api/images/mylocationbutton.png "右上の現在地ボタン
            // ★★ UiSettings.setMyLocationButtonEnabled(false) を呼び出すと、ボタンをまとめて非表示にできます。
            mMap.setMyLocationEnabled(true)
            mMap.setOnMyLocationButtonClickListener(this)
        }
        else{
            Log.d("debug", "permission error")
            return
        }
        Log.d("debug", "onMapReady END")
    }

    // ★★ LocationServices.FusedLocationApi.requestLocationUpdatesで位置情報が更新されたら呼ばれる
    override fun onLocationChanged(location: Location) {
        Log.d("debug", "onLocationChanged START")
        if (onLocationChangedListener != null) {
            Log.d("debug", "onLocationChanged Listener on")
            onLocationChangedListener.onLocationChanged(location)

            // 緯度経度取得
            val lat = location.getLatitude()
            val lng = location.getLongitude()

            Toast.makeText(this, "location=$lat$lng", Toast.LENGTH_SHORT).show()

            // ★★ 更新された位置情報の緯度経度でマップを作成
            // Add a marker and move the camera
            var newLocation = LatLng(lat, lng)
            mMap.addMarker(MarkerOptions().position(newLocation).title("My Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation))
        }
        Log.d("debug", "onLocationChanged END")
    }

    // ★★ GoogleApiClient の接続に成功した場合に呼ばれる
    override fun onConnected(bundle: Bundle?) {
        Log.d("debug", "onConnected START")
        // check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("debug", "permission granted")

            // FusedLocationApi
            // ★★ 位置情報更新・取得処理？→ 位置情報が更新されたら「LocationListener.onLocationChanged」が呼ばれる
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        } else {
            Log.d("debug", "permission error")
            return
        }
        Log.d("debug", "onConnected END")
    }

    // ★★  GoogleApiClient の接続が中断された場合に呼ばれる
    override fun onConnectionSuspended(i:Int){
        Log.d("debug", "onConnectionSuspended")
    }

    // ★★   GoogleApiClient の接続に失敗した場合に呼ばれる
    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.d("debug", "onConnectionFailed")
    }

    // ★★ 現在地ボタンがクリックされた場合に呼ばれる
    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "現在地ボタン押された！！！", Toast.LENGTH_SHORT).show()
        Log.d("debug", "onMyLocationButtonClick END")
        return false
    }

    // OnLocationChangedListener calls activate() method
    override fun activate(onLocationChangedListener: LocationSource.OnLocationChangedListener) {
        this.onLocationChangedListener = onLocationChangedListener
    }

    override fun deactivate() {
        this.onLocationChangedListener.onLocationChanged(null)
    }

    // ★★ロケーションプロバイダが利用不可能になるとコールバックされるメソッド
    override fun onProviderDisabled(provider: String) {
        //ロケーションプロバイダーが使われなくなったらリムーブする必要がある
    }

    // ★★ロケーションプロバイダが利用可能になるとコールバックされるメソッド
    override fun onProviderEnabled(provider: String) {
        //プロバイダが利用可能になったら呼ばれる
    }

    // ★★ロケーションステータスが変わるとコールバックされるメソッド
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        // 利用可能なプロバイダの利用状態が変化したときに呼ばれる
    }
}