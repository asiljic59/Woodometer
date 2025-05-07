package com.example.woodometer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.woodometer.R
import com.example.woodometer.adapters.SilviculturalTypeAdapter
import com.example.woodometer.adapters.TreeTypesAdapter
import com.example.woodometer.interfaces.SilviculturalTypeListener
import com.example.woodometer.interfaces.TreeTypeListener
import com.example.woodometer.utils.GlobalUtils.GAZ_TIPOVI
import com.example.woodometer.utils.GlobalUtils.VRSTE_DRVECA

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SilviculturalTypesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

//Fragment koji sluzi za prikaz i izbor gazdinskog tipa sume za dati krug!
class SilviculturalTypesFragment : Fragment(),SilviculturalTypeListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var currentInput = StringBuilder()
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter : SilviculturalTypeAdapter
    private lateinit var tipTextView : TextView

    private var listener : SilviculturalTypeListener? = null

    fun setListener(listener: SilviculturalTypeListener){
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
        val view =  inflater.inflate(R.layout.fragment_silvicultural_types, container, false)

        recyclerView = view.findViewById(R.id.silviculturalTypesRecyclerView)
        adapter = SilviculturalTypeAdapter(GAZ_TIPOVI,this)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        view.findViewById<Button>(R.id.btnX).setOnClickListener{
            clearDisplay()
        }
        tipTextView = view.findViewById(R.id.tipTextView)

        setNumberButtons(view)

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SilviculturalTypesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SilviculturalTypesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun setNumberButtons(view: View){
        // Set number buttons (0-9)
        listOf(
            R.id.btn0 to "0", R.id.btn1 to "1", R.id.btn2 to "2",
            R.id.btn3 to "3", R.id.btn4 to "4", R.id.btn5 to "5",
            R.id.btn6 to "6", R.id.btn7 to "7", R.id.btn8 to "8",
            R.id.btn9 to "9",
        ).forEach { (id, value) ->
            view.findViewById<Button>(id).setOnClickListener {
                currentInput.append(value)
                updateDisplay()
                fetchSilviculturalTypes()
            }
        }
    }

    private fun fetchSilviculturalTypes() {
        if (currentInput.toString().length >= 4){
            val value : String = currentInput.toString().take(currentInput.length)
            val match = GAZ_TIPOVI.find { it.first == value.toInt() }
            if (match != null) {
                adapter.updateData(listOf(match))
                setSilviculturalType(match.first)
            }
        }
        val filteredList = GAZ_TIPOVI.filter { (key, _) ->
            key.toString().startsWith(currentInput)
        }
        if (filteredList.isEmpty()){return}
        adapter.updateData(filteredList)
    }


    private fun clearDisplay() {
        currentInput = StringBuilder()
        updateDisplay()
        adapter.updateData(GAZ_TIPOVI)
    }

    private fun updateDisplay() {
        tipTextView.text = if (currentInput.isEmpty()) "" else "${currentInput}"
    }

    override fun setSilviculturalType(key: Int) {
        listener?.setSilviculturalType(key)
        parentFragmentManager.popBackStack()
    }

}