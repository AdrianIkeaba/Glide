package com.example.fastchat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class SettingAdapter(private var activity: Settings, private var items: ArrayList<SettingModel>): BaseAdapter() {
    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View?
        val viewHolder: ViewHolder

        if (convertView == null) {
            val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.setting_list, null)
            viewHolder = ViewHolder(view)
            view?.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        val settings = items[position]
        viewHolder.settingName?.text = settings.settingName
        viewHolder.settingImage?.setImageResource(settings.settingImage)

        return view as View
    }
    private class ViewHolder(row: View?) {
        var settingName: TextView? = null
        var settingImage: ImageView? = null

        init {
            this.settingName = row?.findViewById(R.id.settName)
            this.settingImage = row?.findViewById(R.id.settImage)
        }
    }
}