package jp.co.rjc.myindicater_android

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.support.annotation.NonNull
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URI
import java.net.URL

/**
 * Created by h_miyamoto on 2018/04/10.
 */
class SearchDestinationTask(context: Context) : AsyncTask<String, Void, String>() {

    private var mContext = context
    private var mListener: SearchDestinationTaskListener? = null

    interface SearchDestinationTaskListener {
        fun onSuccess(list: ArrayList<Place>)
        fun onFailed()
    }

    override fun onPreExecute() {
    }

    override fun doInBackground(@NonNull vararg word: String?): String {
        val uri = Uri.Builder()
        uri.scheme("https")
                .authority("maps.googleapis.com")
                // 周辺検索の場合はmaps/api/place/nearbysearch/json
                .path("maps/api/place/textsearch/json")
                .appendQueryParameter("query", word[0])
                .appendQueryParameter("language", "ja")
                .appendQueryParameter("radius", "5000")
                .appendQueryParameter("key", mContext.getString(R.string.google_maps_place_search_key))

        val urlConnection = URL(uri.toString()).openConnection()
        urlConnection.connect()

        val reader = BufferedReader(InputStreamReader(urlConnection.getInputStream()))
        var response = ""
        var line: String? = null
        while ({ line = reader.readLine(); line }() != null) {
            response += line
        }

        return response
    }

    override fun onPostExecute(response: String) {
        if (!response.isNullOrEmpty()) {
            val rootJson = JSONObject(response)
            val status = rootJson.get("status")
            if ("OK" == status) {
                val list = ArrayList<Place>()
                val array = rootJson.getJSONArray("results")
                for(i in 0..(array.length() -1)){
                    val item = array.getJSONObject(i)
                    val location = item.getJSONObject("geometry").getJSONObject("location")
                    val place =  Place()

                    place.name = item.getString("name")
                    place.id = item.getString("id")
                    place.place_id = item.getString("place_id")
                    place.latitude = location.getDouble("lat")
                    place.longitude = location.getDouble("lng")
                    list.add(place)
                }

                mListener?.onSuccess(list)

                return
            }
        }

        // 取得失敗時

        mListener?.onFailed()

    }

    fun setOnSearchDestinationTaskListener(listener: SearchDestinationTaskListener) {
        mListener = listener
    }
}