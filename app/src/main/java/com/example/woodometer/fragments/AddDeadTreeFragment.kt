package com.example.woodometer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.example.woodometer.R
import com.example.woodometer.interfaces.KeyboardListener
import com.example.woodometer.interfaces.TreeTypeListener
import com.example.woodometer.model.enumerations.KeyboardField

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddDeadTreeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddDeadTreeFragment : DialogFragment(),KeyboardListener,TreeTypeListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var currentInputField : TextView
    private lateinit var keyboardTextViews : HashMap<ConstraintLayout,Triple<TextView,String,KeyboardField>>
    private lateinit var vrstaButton:TextView

    private var currentTreeType : Int = 0

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
        val view = inflater.inflate(R.layout.fragment_add_dead_tree, container, false)

        vrstaButton = view.findViewById(R.id.vrstaDrvetaButton)
        vrstaButton.setOnClickListener{
            val treeTypesFragment = TreeTypesFragment().apply {
                setListener(this@AddDeadTreeFragment)
            }
            treeTypesFragment.show(parentFragmentManager,null)
        }
        createKeyboardHashMap(view)

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddDeadTreeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddDeadTreeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun createKeyboardHashMap(view: View){
        keyboardTextViews = hashMapOf(
            view.findViewById<ConstraintLayout>(R.id.precnikLayout) to
                    Triple(view.findViewById<TextView>(R.id.precnikTextView),"Prečnik",
                        KeyboardField.PRECNIK),
            view.findViewById<ConstraintLayout>(R.id.visinaLayout) to
                    Triple(view.findViewById<TextView>(R.id.visinaTextView),"Visina", KeyboardField.VISINA),
            view.findViewById<ConstraintLayout>(R.id.polozajStablaLayout)  to
                    Triple(view.findViewById(R.id.polozajStablaTextView),"Polozaj stabla",KeyboardField.POLOZAJ_STABLA)
        )
        setupInputKeyboardClickListeners(keyboardTextViews)
    }

    private fun setupInputKeyboardClickListeners(inputFields: HashMap<ConstraintLayout,Triple<TextView,String,KeyboardField>>) {
        inputFields.keys.forEach { layout ->
            layout.setOnClickListener(){
                currentInputField = keyboardTextViews[layout]?.first!!
                openKeyboard(layout)
            }
        }
    }

    fun openKeyboard(layout: ConstraintLayout){
        val keyboardFragment = KeyboardFragment().apply {
            setKeyboardListener(this@AddDeadTreeFragment)
            keyboardTextViews[layout]?.let { setTitle(it.second) }
            keyboardTextViews[layout]?.third?.let { setField(it) }
        }
        currentInputField.text = ""
        keyboardFragment.show(childFragmentManager, null)
    }

    override fun onKeyPressed(key: String) {
        TODO()
    }

    override fun onEnterPressed(input: String) {
        currentInputField.text = input
    }


    override fun onClearPressed() {
        currentInputField.text = ""
    }

    override fun setTreeType(name: String, key: Int) {
        vrstaButton.text = name
        currentTreeType = key
    }
}