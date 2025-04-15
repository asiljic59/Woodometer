package com.example.woodometer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.woodometer.R
import com.example.woodometer.interfaces.TreeListener
import com.example.woodometer.model.MrtvoStablo

class DeadTreesAdapter(private var measurements: List<MrtvoStablo>,private var listener: TreeListener) :
    RecyclerView.Adapter<DeadTreesAdapter.ViewHolder>() {

    // ViewHolder for item layout
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layout : ConstraintLayout = view.findViewById(R.id.deadTreeLayout)
        val rbr: TextView = view.findViewById(R.id.pozicijaTextView)
        val precnik: TextView = view.findViewById(R.id.precnikTextView)
        val visina: TextView = view.findViewById(R.id.visinaTextView)
        val polozaj: TextView = view.findViewById(R.id.polozajStablaTextView)
        val vrsta : TextView = view.findViewById(R.id.vrstaTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.dead_tree_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = measurements[position]
        holder.polozaj.text = item.polozaj.toString()
        holder.precnik.text = item.precnik.toString()
        holder.visina.text = item.visina.toString()
        holder.rbr.text = item.rbr.toString()
        holder.vrsta.text = item.vrsta.toString()
        holder.layout.setOnLongClickListener{
            listener.deleteTree(item.rbr)
            true
        }
        holder.layout.setOnClickListener{
            listener.editTree(item)
        }
    }

    override fun getItemCount() = measurements.size

    // Update data with DiffUtil for performance
    fun updateData(newData: List<MrtvoStablo>) {
        val diffResult = DiffUtil.calculateDiff(TreeDiffCallback(measurements, newData))
        measurements = newData
        diffResult.dispatchUpdatesTo(this)
    }

    class TreeDiffCallback(
        private val oldList: List<MrtvoStablo>,
        private val newList: List<MrtvoStablo>
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