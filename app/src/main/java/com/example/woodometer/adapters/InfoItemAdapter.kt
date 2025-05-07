package com.example.woodometer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.woodometer.R
import com.example.woodometer.interfaces.InformationItemListener
import com.google.android.material.card.MaterialCardView

class InfoItemAdapter(private val items : List<Pair<Int,String>>,private val listener: InformationItemListener) :
    RecyclerView.Adapter<InfoItemAdapter.NumberViewHolder>() {

    class NumberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val infoTextView = itemView.findViewById<TextView>(R.id.infoTextView)
        val infoCard = itemView.findViewById<MaterialCardView>(R.id.infoCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.information_item, parent, false)
        return NumberViewHolder(view)
    }

    override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
        val item = items.get(position)
        "${item.first} - ${item.second}".also { holder.infoTextView.text = it }
        holder.infoCard.setOnClickListener{
            listener.informationPicked(item.first)
        }
    }

    override fun getItemCount() = items.size
}