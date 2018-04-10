package jp.co.rjc.myindicater_android

import android.os.AsyncTask
import android.support.annotation.NonNull
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

/**
 * Created by h_miyamoto on 2018/04/10.
 */
class SearchDestinationTask : AsyncTask<String, Void, String>() {

    override fun onPreExecute(){
    }

    override fun doInBackground(@NonNull vararg word: String?): String {
        // TODO 確認用
        val url = URL("https://maps.googleapis.com/maps/api/place/radarsearch/json?location=51.503186,-0.126446&radius=5000&type=museum&key=AIzaSyBisg850iHlVD2SnEEqRC0zQDzWEV4j6L0")
        val urlConnection = url.openConnection()
        urlConnection.connect()

        val reader = BufferedReader(InputStreamReader(urlConnection.getInputStream()))
        var response = ""
        var line : String? = null
        while ({ line = reader.readLine(); line }() != null) {
            response += line
        }

        return "TEST OK:"+response
    }

    override fun onPostExecute(response : String){
        Log.i("PICK", "onPostExecute:"+response)

    }
}