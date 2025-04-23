package com.example.woodometer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.woodometer.R
import com.example.woodometer.adapters.DeadTreesAdapter.ViewHolder
import com.example.woodometer.interfaces.TreeListener
import com.example.woodometer.model.MrtvoStablo
import com.example.woodometer.model.Stablo
import com.google.android.material.card.MaterialCardView

class TreesAdapter(var stabla : MutableList<Stablo>,val listener : TreeListener) : RecyclerView.Adapter<TreesAdapter.ViewHolder>(){
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rbrTextView = view.findViewById<TextView>(R.id.stabloRbrTextView)
        val card = view.findViewById<MaterialCardView>(R.id.treeMaterialCardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tree_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = stabla[position]
        item.rbr.toString().also { holder.rbrTextView.text = it }
        holder.card.setOnClickListener{listener.changeTree(item)}
    }

    override fun getItemCount() = stabla.size

    // Update data with DiffUtil for performance
    fun updateData(newData: MutableList<Stablo>) {
        val sortedData = newData.sortedBy { it.rbr }
        val diffResult = DiffUtil.calculateDiff(TreeDiffCallback(stabla, newData))
        stabla = sortedData.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }

    class TreeDiffCallback(
        private val oldList: List<Stablo>,
        private val newList: List<Stablo>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean {
            return oldList[oldPos].rbr == newList[newPos].rbr
        }

        override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean {
            return oldList[oldPos] == newList[newPos]
        }
    }

}