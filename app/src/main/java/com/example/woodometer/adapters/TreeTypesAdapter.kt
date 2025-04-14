package com.example.woodometer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.woodometer.R
import com.example.woodometer.interfaces.TreeTypeListener
import com.google.android.material.card.MaterialCardView

class TreeTypesAdapter(private var items: List<Pair<Int, String>>,private val listener : TreeTypeListener) : RecyclerView.Adapter<TreeTypesAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.tree_type_item, parent, false)
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.elementAt(position)
        holder.view.findViewById<TextView>(R.id.treeTypeTextView).text = item.second
        holder.view.findViewById<TextView>(R.id.treeCodeTextView).text = item.first.toString()
        holder.view.findViewById<MaterialCardView>(R.id.treeTypeCardView).setOnClickListener{
            listener.setTreeType(item.second,item.first)
        }
    }

    override fun getItemCount() = items.size

    fun updateData(newData: List<Pair<Int, String>>) {
        items = newData
        notifyDataSetChanged() // Or use DiffUtil for better performance
    }
}