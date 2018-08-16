package jp.co.rjc.myindicater_android


import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog

import android.util.Log
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap

/**
 * Created by h_miyamoto on 2018/06/08.
 */

class ProgressDialogFragment : DialogFragment(){

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val rootView = activity.layoutInflater.inflate(R.layout.dialog_progress, null)
        val builder = AlertDialog.Builder(activity)
        builder.setCancelable(false).setView(rootView)
        return builder.create()
    }
}