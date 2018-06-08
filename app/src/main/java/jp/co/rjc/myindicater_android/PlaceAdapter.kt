package jp.co.rjc.myindicater_android

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListAdapter
import android.widget.TextView

/**
 * Created by h_miyamoto on 2018/06/08.
 */
class PlaceAdapter(activity: Activity, list:List<Place>) : BaseAdapter(){

    private var mActivty = activity
    private var mList = list

    private class ViewHolder{
        var nameView : TextView? = null
        var latitudeView : TextView? = null
        var longitudeView : TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var holder = ViewHolder()
        var rootView = convertView

        if(rootView == null){
            rootView = LayoutInflater.from(mActivty).inflate(R.layout.adapter_place, null)
            holder.nameView = rootView.findViewById(R.id.name)
            holder.latitudeView = rootView.findViewById(R.id.latitude)
            holder.longitudeView = rootView.findViewById(R.id.longitude)

            rootView!!.tag = holder

        } else holder = rootView.tag as ViewHolder

        holder.nameView?.text = getItem(position).name
        holder.latitudeView?.text = getItem(position).latitude.toString()
        holder.longitudeView?.text = getItem(position).longitude.toString()

        return rootView
    }

    override fun getItem(position: Int): Place {
        return mList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mList.size
    }


}