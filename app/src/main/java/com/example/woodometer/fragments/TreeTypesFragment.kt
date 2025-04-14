package com.example.woodometer.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.view.WindowCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.woodometer.R
import com.example.woodometer.adapters.TreeTypesAdapter
import com.example.woodometer.interfaces.TreeTypeListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private val VRSTE_DRVECA = listOf(
    11 to "Bela vrba",      // 11 → Bela vrba
    12 to "Bademasta vrba", // 12 → Bademasta vrba
    13 to "Krta vrba",      // 13 → Krta vrba
    14 to "Siva vrba",      // 14 → Siva vrba
    21 to "Crna jova",       // 21 → Crna jova
    22 to "Bela jova"
)

/**
 * A simple [Fragment] subclass.
 * Use the [TreeTypesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TreeTypesFragment : DialogFragment(),TreeTypeListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //trenutni unos
    private var currentInput = StringBuilder()
    private lateinit var vrstaTextView : TextView

    //recycler view za vrste drveca koje ce se pojaviti u desnom uglu kao ponudjene
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter : TreeTypesAdapter

    //listener pomocu kojeg dobijamo vrednosti kliknute vrste u recycler view-u
    private var listener : TreeTypeListener? = null

    fun setListener(listener: TreeTypeListener){
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
        val view = inflater.inflate(R.layout.fragment_tree_types, container, false)
        view.setPadding(0, 0, 0, 0)

        vrstaTextView = view.findViewById(R.id.vrstaTextView)

        recyclerView = view.findViewById(R.id.treeTypesRecyclerView)
        adapter = TreeTypesAdapter(VRSTE_DRVECA,this)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        view.findViewById<Button>(R.id.btnX).setOnClickListener{
            clearDisplay()
        }

        setNumberButtons(view)

        return view
    }

    private fun clearDisplay() {
        currentInput = StringBuilder()
        updateDisplay()
        adapter.updateData(VRSTE_DRVECA)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TreeTypesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TreeTypesFragment().apply {
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
                fetchTreeTypes()
            }
        }
    }

    //Funkcija za azuriranje vrsti drveca
    private fun fetchTreeTypes() {
        if (currentInput.toString().length >= 2){
            var value : String = currentInput.toString().take(2)
            val match = VRSTE_DRVECA.find { it.first == value.toInt() }
            if (match != null) {
                adapter.updateData(listOf(match))
                setTreeType(match.second,match.first)
            }
        }
        val filteredList = VRSTE_DRVECA.filter { (key, _) ->
            key.toString().contains(currentInput)
        }
        if (filteredList.isEmpty()){return}
        adapter.updateData(filteredList)
    }

    //azuriranje trenutne unete vrednosti!
    private fun updateDisplay() {
        vrstaTextView.text = if (currentInput.isEmpty()) "" else "${currentInput}"
    }



    override fun onStart() {
        super.onStart()

        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun setTreeType(name: String, key: Int) {
        listener?.setTreeType(name,key)
        dismiss()
    }
}