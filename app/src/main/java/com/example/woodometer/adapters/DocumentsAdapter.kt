package com.example.woodometer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.woodometer.R
import com.example.woodometer.adapters.DeadTreesAdapter.TreeDiffCallback
import com.example.woodometer.interfaces.DocumentsListener
import com.example.woodometer.model.Dokument
import com.example.woodometer.model.MrtvoStablo
import com.google.android.material.card.MaterialCardView
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DocumentsAdapter(private var docs : List<Dokument>,private val documentsListener : DocumentsListener) :
    RecyclerView.Adapter<DocumentsAdapter.NumberViewHolder>() {

        class NumberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val numberText: TextView = itemView.findViewById(R.id.itemText)
            val date : TextView = itemView.findViewById(R.id.dateTextView)
            val documentCardView : MaterialCardView = itemView.findViewById(R.id.documentCardView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.document_item, parent, false)
            return NumberViewHolder(view)
        }

        override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
            holder.numberText.text = formatText(docs[position])
            holder.date.text = formatTimestamp(docs[position].timestamp)
            holder.documentCardView.setOnClickListener{
                documentsListener.docClicked(docs[position])
            }

        }
        fun formatText(dokument: Dokument) : String{
            return "${dokument.gazJedinica}${dokument.brOdeljenja}_${dokument.odsek}_${dokument.korisnik}"
        }
        fun formatTimestamp(timestamp: Long): String {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
                .withZone(ZoneId.systemDefault())

            return formatter.format(Instant.ofEpochMilli(timestamp))
        }

        override fun getItemCount() = docs.size

    fun updateData(newData: List<Dokument>) {
        val diffResult = DiffUtil.calculateDiff(TreeDiffCallback(docs, newData))
        docs = newData
        diffResult.dispatchUpdatesTo(this)
    }
    class TreeDiffCallback(
        private val oldList: List<Dokument>,
        private val newList: List<Dokument>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean {
            return oldList[oldPos].timestamp == newList[newPos].timestamp
        }

        override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean {
            return oldList[oldPos] == newList[newPos]
        }
    }
}