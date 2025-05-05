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
import java.util.UUID

class TreesAdapter(var stabla : MutableList<Stablo>,val listener : TreeListener) : RecyclerView.Adapter<TreesAdapter.ViewHolder>(){
    var selectedStabloPosition: Int = 0
    var oldStabloPosition : Int = 0;
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

        if (position == selectedStabloPosition) {
            holder.card.strokeColor = holder.card.context.getColor(R.color.olive) // your defined highlight color
            holder.card.strokeWidth = 5
            holder.card.elevation = 10F
        }else{
            holder.card.strokeColor = holder.card.context.getColor(R.color.white)
            holder.card.strokeWidth = 5
        }

        holder.card.setOnClickListener {
            listener.changeTree(item)
        }
        holder.card.setOnLongClickListener{
            listener.deleteTree(item.rbr)
            true
        }
    }

    override fun getItemCount() = stabla.size

    // Update data with DiffUtil for performance
    fun updateData(newData: MutableList<Stablo>) {
        val sortedData = newData.sortedBy { it.rbr }
        val diffResult = DiffUtil.calculateDiff(TreeDiffCallback(stabla, newData))
        stabla = sortedData.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateSelectedStablo(newPosition : Int) {
        oldStabloPosition = selectedStabloPosition
        selectedStabloPosition = newPosition
        notifyItemChanged(oldStabloPosition)
        notifyItemChanged(newPosition)
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