package com.example.woodometer.adapters

import android.content.pm.ResolveInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.woodometer.R

class ShareAdapter(
    private val apps: List<ResolveInfo>,
    private val onAppSelected: (ResolveInfo) -> Unit
) : RecyclerView.Adapter<ShareAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.appIcon)
        val label: TextView = view.findViewById(R.id.appLabel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.share_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = apps.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = apps[position]
        val pm = holder.view.context.packageManager
        holder.icon.setImageDrawable(info.loadIcon(pm))
        holder.label.text = info.loadLabel(pm)

        holder.view.setOnClickListener {
            onAppSelected(info)
        }
    }
}
