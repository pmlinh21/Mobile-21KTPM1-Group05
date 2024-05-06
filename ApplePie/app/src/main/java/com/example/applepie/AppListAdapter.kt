package com.example.applepie

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppListAdapter(
    private val appList: List<ApplicationInfo>,
    private val selectedApps: MutableList<String>,
    private val packageManager: PackageManager
) : RecyclerView.Adapter<AppListAdapter.AppViewHolder>() {

    inner class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appName: TextView = itemView.findViewById(R.id.app_name)
        val appIcon: ImageView = itemView.findViewById(R.id.app_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app_list, parent, false)
        return AppViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val appInfo = appList[position]
        val packageName = appInfo.packageName
        val appIcon = appInfo.loadIcon(packageManager)

        holder.appName.text = appInfo.loadLabel(packageManager)
        holder.appIcon.setImageDrawable(appIcon)

    }

    override fun getItemCount(): Int = appList.size
}