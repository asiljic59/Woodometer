package com.example.woodometer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.woodometer.R
import com.example.woodometer.fragments.CircleFragment
import com.example.woodometer.interfaces.CircleListener
import com.example.woodometer.model.Krug
import com.example.woodometer.model.Stablo
import com.google.android.material.card.MaterialCardView

class CircleAdapter(private var krugovi : MutableList<Krug>, private val listener : CircleListener) :
    RecyclerView.Adapter<CircleAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rbrTextView = view.findViewById<TextView>(R.id.stabloRbrTextView)
        val card = view.findViewById<MaterialCardView>(R.id.treeMaterialCardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.circle_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = krugovi[position]
        item.brKruga.toString().also { holder.rbrTextView.text = it }
        holder.card.setOnClickListener{listener.circleChanged(item)}
    }

    override fun getItemCount() = krugovi.size

    // Update data with DiffUtil for performance
    fun updateData(newData: MutableList<Krug>) {
        val sortedData = newData.sortedBy { it.brKruga }
        val diffResult = DiffUtil.calculateDiff(TreeDiffCallback(krugovi, newData))
        krugovi = sortedData.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }

    class TreeDiffCallback(
        private val oldList: List<Krug>,
        private val newList: List<Krug>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean {
            return oldList[oldPos].id == newList[newPos].id
        }

        override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean {
            return oldList[oldPos] == newList[newPos]
        }
    }
}