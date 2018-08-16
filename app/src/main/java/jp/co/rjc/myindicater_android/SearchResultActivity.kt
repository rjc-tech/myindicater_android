package jp.co.rjc.myindicater_android

import android.app.Activity
import android.app.ListActivity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.AdapterView

class SearchResultActivity : ListActivity() {

    private val KEY_DESTINATION_NAME = "key_destination_name"
    private val KEY_DESTINATION_LATITUDE = "key_destination_latitude"
    private val KEY_DESTINATION_LONGITUDE = "key_destination_longitude"
    private val KEY_DESTINATION_ID = "key_destination_id"
    private val KEY_DESTINATION_PLACE_ID = "key_destination_place_id"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val list = intent.getSerializableExtra("place_list") as ArrayList<Place>
        listAdapter = PlaceAdapter(this, list)
        listView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, viewId ->

            val place = adapterView.getItemAtPosition(position) as Place
            val result = Intent()

            result.putExtra("place", place)

            saveDestinationCoordinate(place)

            setResult( Activity.RESULT_OK, result)
            finish()
        }

    }

    private fun saveDestinationCoordinate(place : Place){
        // 余裕があったらJSONで保存
        val editor = PreferenceManager.getDefaultSharedPreferences(applicationContext).edit()
        editor.putString(KEY_DESTINATION_NAME, place.name)
        editor.putFloat(KEY_DESTINATION_LATITUDE, place.latitude.toFloat())
        editor.putFloat(KEY_DESTINATION_LONGITUDE, place.longitude.toFloat())
        editor.putString(KEY_DESTINATION_ID, place.id)
        editor.putString(KEY_DESTINATION_PLACE_ID, place.place_id)
        editor.apply()
    }

}
