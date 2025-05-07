package com.example.woodometer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.replace
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.woodometer.R
import com.example.woodometer.adapters.InfoItemAdapter
import com.example.woodometer.interfaces.InformationItemListener
import com.example.woodometer.interfaces.KeyboardListener
import com.example.woodometer.model.enumerations.KeyboardField
import com.example.woodometer.utils.GlobalUtils.POLOZAJ_STABLA
import com.example.woodometer.utils.GlobalUtils.PROBNE_DOZNAKE
import com.example.woodometer.utils.GlobalUtils.SOCIJALNI_STATUSI
import com.example.woodometer.utils.GlobalUtils.STEPENI_SUSENJA
import com.example.woodometer.utils.GlobalUtils.TEHNICKE_KLASE
import com.example.woodometer.utils.GlobalUtils.UZGOJNE_GRUPE
import com.google.android.material.button.MaterialButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InformationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InformationFragment : Fragment(),InformationItemListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var field: KeyboardField;
    private var listener: InformationItemListener? = null

    private lateinit var infoRecyclerView : RecyclerView
    private lateinit var adapter: InfoItemAdapter
    fun setField(field: KeyboardField) {
        this.field = field
    }
    fun setListener(listener: InformationItemListener){
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
        val view = inflater.inflate(R.layout.fragment_information, container, false)

        fillInfoLayout(view)
        view.findViewById<MaterialButton>(R.id.nazadButton).setOnClickListener{
            parentFragmentManager.popBackStack()
        }
        return view;
    }

    private fun fillInfoLayout(view: View) {
        val list: List<Pair<Int, String>>;
        when (field) {
            KeyboardField.STEPEN_SUSENJA -> {
                list = STEPENI_SUSENJA
            }

            KeyboardField.SOCIJALNI_STATUS -> {
                list = SOCIJALNI_STATUSI
            }

            KeyboardField.TEHNICKA_KLASA -> {
                list = TEHNICKE_KLASE
            }

            KeyboardField.POLOZAJ_STABLA -> {
                list = POLOZAJ_STABLA
            }
            KeyboardField.UZGOJNA_GRUPA -> {
                list = UZGOJNE_GRUPE
            }
            KeyboardField.PROBNA_DOZNAKA -> {
                list = PROBNE_DOZNAKE
            }

            else -> {
                list = listOf()
            }
        }
        setupInfoRecyclerView(view, list)
    }

    private fun setupInfoRecyclerView(view: View, items : List<Pair<Int,String>>){
        infoRecyclerView = view.findViewById(R.id.informationRecyclerView)
        infoRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter = InfoItemAdapter(items,this)
        infoRecyclerView.adapter = adapter
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment InformationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InformationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun informationPicked(number: Int) {
        listener?.informationPicked(number)
        parentFragmentManager.popBackStack()
    }
}