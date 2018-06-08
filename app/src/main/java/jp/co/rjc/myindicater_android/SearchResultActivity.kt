package jp.co.rjc.myindicater_android

import android.app.Activity
import android.app.ListActivity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.AdapterView

class SearchResultActivity : ListActivity() {

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
        editor.putString("name",place.name)
                .putString("id",place.id)
                .putString("place_id",place.place_id)
                .putFloat("latitude",place.latitude.toFloat())
                .putFloat("longitude",place.longitude.toFloat())
                .apply()
    }

}
