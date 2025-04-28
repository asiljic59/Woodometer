package com.example.woodometer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.woodometer.R
import com.example.woodometer.interfaces.SilviculturalTypeListener
import com.example.woodometer.model.Krug
import com.google.android.material.card.MaterialCardView

class SilviculturalTypeAdapter(private var gazTipovi : List<Pair<Int,String>>,private val listener: SilviculturalTypeListener) :
    RecyclerView.Adapter<SilviculturalTypeAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val descriptionTextView = view.findViewById<TextView>(R.id.descriptionTextView)
        val code = view.findViewById<TextView>(R.id.codeTextView)
        val card = view.findViewById<MaterialCardView>(R.id.silviculturalTypeCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.silvicultural_type_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = gazTipovi[position]
        holder.descriptionTextView.text = item.second
        holder.code.text = String.format(item.first.toString())
        holder.card.setOnClickListener {
            listener.setSilviculturalType(item.first)
        }
    }

    override fun getItemCount() = gazTipovi.size

    fun updateData(newData: List<Pair<Int,String>>) {
        gazTipovi = newData
        notifyDataSetChanged()
    }


}