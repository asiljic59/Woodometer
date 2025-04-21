package com.example.woodometer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.woodometer.R
import com.google.android.material.card.MaterialCardView

class DocumentsAdapter(private val docs : List<String>) :
    RecyclerView.Adapter<DocumentsAdapter.NumberViewHolder>() {

        class NumberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val numberText: TextView = itemView.findViewById(R.id.itemText)
            val optionCardView : MaterialCardView = itemView.findViewById(R.id.optionCardView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.document_item, parent, false)
            return NumberViewHolder(view)
        }

        override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
            holder.numberText.text = docs.get(position)
        }

        override fun getItemCount() = docs.size
}