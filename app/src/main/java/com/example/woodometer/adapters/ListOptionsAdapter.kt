import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.woodometer.R
import com.example.woodometer.interfaces.AddOptionListener
import com.google.android.material.card.MaterialCardView

class ListOptionsAdapter(private val numbers: List<String>,private val listener : AddOptionListener) :
    RecyclerView.Adapter<ListOptionsAdapter.NumberViewHolder>() {

    class NumberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val numberText: TextView = itemView.findViewById(R.id.itemText)
        val optionCardView : MaterialCardView = itemView.findViewById(R.id.optionCardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return NumberViewHolder(view)
    }

    override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
        holder.numberText.text = numbers.get(position)
        holder.optionCardView.setOnClickListener{
            listener.optionPicked(holder.numberText.text.toString())
        }
    }

    override fun getItemCount() = numbers.size
}