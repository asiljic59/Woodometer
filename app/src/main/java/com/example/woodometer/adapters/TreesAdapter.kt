package com.example.woodometer.adapters

import android.graphics.drawable.Drawable
import android.provider.ContactsContract.CommonDataKinds.Im
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
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
    val colors = listOf(
        10 to R.color.stablo_buducnosti,
        21 to R.color.konkurent,
        30 to R.color.indiferentno,
        41 to R.color.loseg_kvaliteta,
        51 to R.color.zrelo_stablo
    )
    var selectedStabloPosition: Int = 0
    var oldStabloPosition : Int = 0;
    var doznakaMode : Boolean = false
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rbrTextView = view.findViewById<TextView>(R.id.stabloRbrTextView)
        val card = view.findViewById<MaterialCardView>(R.id.treeMaterialCardView)
        val imageView = view.findViewById<ImageView>(R.id.forestImageView)
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

        if (doznakaMode){
            setTreeDoznakaMode(position,holder,item)
        }else{
            setTreeNormalMode(position,holder)
        }
        holder.card.setOnClickListener {
            listener.changeTree(item)
        }
        holder.card.setOnLongClickListener{
            listener.deleteTree(item.rbr)
            true
        }
    }

    private fun setTreeNormalMode(position : Int, holder : ViewHolder){
        holder.imageView.setImageDrawable(ContextCompat.getDrawable(holder.card.context, R.drawable.javor_list))
        holder.card.background.setTint(holder.card.context.getColor(R.color.white))
        holder.rbrTextView.setTextColor(holder.card.context.getColor(R.color.black))
        if (position == selectedStabloPosition) {
            holder.card.strokeColor = holder.card.context.getColor(R.color.olive) // your defined highlight color
            holder.card.strokeWidth = 6
            holder.card.elevation = 20f
        }else{
            holder.card.strokeColor = holder.card.context.getColor(R.color.white)
            holder.card.strokeWidth = 5
        }
    }
    private fun setTreeDoznakaMode(position : Int, holder : ViewHolder,item: Stablo){
        holder.imageView.setImageDrawable(null)
        holder.card.strokeColor = holder.card.context.getColor(R.color.white)
        holder.rbrTextView.setTextColor(holder.card.context.getColor(R.color.white))
        if (position == selectedStabloPosition) {
            holder.card.background.setTint(holder.card.context.getColor(R.color.black))  // your defined highlight color
            holder.card.strokeWidth = 8
            holder.card.elevation = 20f
        }else{
            val colorResId = colors.firstOrNull { it.first == item.probDoznaka }?.second
            colorResId?.let { holder.card.context.getColor(it) }
                ?.let { holder.card.background.setTint(it) }
            holder.card.strokeWidth = 7
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

    fun changeDoznakaMode(doznakaMode : Boolean){
        this.doznakaMode = doznakaMode
        notifyDataSetChanged()
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