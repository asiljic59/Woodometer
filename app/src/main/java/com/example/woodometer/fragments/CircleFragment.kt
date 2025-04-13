package com.example.woodometer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.woodometer.R
import com.example.woodometer.interfaces.KeyboardListener
import com.example.woodometer.model.enumerations.KeyboardField

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CircleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CircleFragment : Fragment(), KeyboardListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var currentInputField : TextView
    //polja koja otvaraju tastaturu
    private lateinit var keyboardTextViews : HashMap<ConstraintLayout,Triple<TextView,String,KeyboardField>>

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
        val view = inflater.inflate(R.layout.fragment_circle, container, false)

        createKeyboardHashMap(view)
        setupInputKeyboardClickListeners(keyboardTextViews)

        return view;

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CircleFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CircleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun createKeyboardHashMap(view: View){
        keyboardTextViews = hashMapOf(
            view.findViewById<ConstraintLayout>(R.id.precnikLayout) to
            Triple(view.findViewById<TextView>(R.id.precnikTextView),"Prečnik",KeyboardField.PRECNIK),
            view.findViewById<ConstraintLayout>(R.id.azimutLayout) to
            Triple(view.findViewById<TextView>(R.id.azimutTextView),"Azimut",KeyboardField.AZIMUT),
            view.findViewById<ConstraintLayout>(R.id.razdaljinaLayout) to
            Triple(view.findViewById<TextView>(R.id.razdaljinaTextView),"Razdaljina",KeyboardField.RAZDALJINA),
            view.findViewById<ConstraintLayout>(R.id.visinaLayout) to
            Triple(view.findViewById<TextView>(R.id.visinaTextView),"Visina",KeyboardField.VISINA),
            view.findViewById<ConstraintLayout>(R.id.duzinaDeblaLayout) to
            Triple(view.findViewById(R.id.duzinaDeblaTextView),"Dužina debla", KeyboardField.DUZINA_DEBLA),
            view.findViewById<ConstraintLayout>(R.id.stepenSusenjaLayout) to
            Triple(view.findViewById(R.id.stepenSusenjaTextView),"Stepen sušenja", KeyboardField.STEPEN_SUSENJA),
            view.findViewById<ConstraintLayout>(R.id.socStatusLayout) to
            Triple(view.findViewById(R.id.socStatusTextView),"Socijalni status", KeyboardField.SOCIJALNI_STATUS),
            view.findViewById<ConstraintLayout>(R.id.tehnickaKlasaLayout) to
            Triple(view.findViewById(R.id.tehKlasaTextView),"Tehnička klasa", KeyboardField.TEHNICKA_KLASA),
            view.findViewById<ConstraintLayout>(R.id.probnaDoznakaLayout) to
            Triple(view.findViewById(R.id.probDoznakaTextView),"Probna doznaka", KeyboardField.PROBNA_DOZNAKA),
            )
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
            setKeyboardListener(this@CircleFragment)
            keyboardTextViews[layout]?.let { setTitle(it.second) }
            keyboardTextViews[layout]?.third?.let { setField(it) }
        }
        currentInputField.text = ""
        parentFragmentManager.beginTransaction()
            .add(R.id.main, keyboardFragment)
            .addToBackStack(null)
            .commit()
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
}