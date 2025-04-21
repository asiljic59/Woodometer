package com.example.woodometer.fragments

import ListOptionsAdapter
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.woodometer.R
import com.example.woodometer.interfaces.AddOptionListener
import com.example.woodometer.model.enumerations.ListOptionsField

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListOptionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListOptionsFragment : DialogFragment(), AddOptionListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var listItems : List<String> = emptyList()

    fun setListItems(listItems :  List<String>){
        this.listItems = listItems
    }

    private var field : ListOptionsField? = null

    fun setOptionField(field : ListOptionsField?){
        this.field = field;
    }

    private var listener : AddOptionListener? = null

    fun setListener(listener: AddOptionListener){
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list_options, container, false)

        //setting list items and adapter for recycler view
        val recyclerView : RecyclerView = view.findViewById(R.id.optionsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = ListOptionsAdapter(listItems,this)

        val addNew  = view.findViewById<TextView>(R.id.dodajNovuJed)

        addNew.setOnClickListener{
            dismiss();
            val dialog = NewOptionFragment().apply {
                setListener(this@ListOptionsFragment)
                setField(field)
            }
            dialog.show(parentFragmentManager, "NewUnitFragment")
        }

        return view;
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.let { window ->
            // Set position
            val params = window.attributes
            params.gravity = Gravity.START
            params.y = 0 // Offset from top in pixels (optional)

            // Convert 500dp to pixels
            val scale = resources.displayMetrics.density
            val widthInPx = (500 * scale + 0.5f).toInt()

            // Apply width and height
            window.setLayout(widthInPx, ViewGroup.LayoutParams.WRAP_CONTENT)
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            window.setLayout(widthInPx, height)

            // Apply position changes
            window.attributes = params
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ListOptionsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListOptionsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun addOption(option : String) {
        listener?.addOption(option)
    }

    override fun optionPicked(option: String) {
        listener?.optionPicked(option)
        dismiss()
    }
}