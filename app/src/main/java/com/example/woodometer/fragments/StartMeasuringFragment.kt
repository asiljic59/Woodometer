package com.example.woodometer.fragments

import android.content.Context
import android.content.SharedPreferences.Editor
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.woodometer.R
import com.example.woodometer.interfaces.AddOptionListener
import com.example.woodometer.interfaces.KeyboardListener
import com.example.woodometer.model.enumerations.ListOptionsField

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StartMeasuringFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StartMeasuringFragment : Fragment(), KeyboardListener,AddOptionListener  {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var currentInputField : TextView
    private lateinit var currentOptionField : TextView

    //polja koja otvaraju tastaturu
    private lateinit var keyboardTextViews : HashMap<ConstraintLayout,TextView>

    //polja koja otvaraju opcije
    private lateinit var optionsTextViews : HashMap<ConstraintLayout,TextView>

    //svaka promenljiva koja ima izbore mora imati i negde sacuvane izbore, tako da ovde cuvamo imena fajlova korelaciono sa atributima (poljima za tekst)
    private lateinit var optionsFileNames: HashMap<TextView,Pair<String,ListOptionsField>>

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
        val view = inflater.inflate(R.layout.fragment_start_measuring, container, false)



        val gazdinskaLayout = view.findViewById<ConstraintLayout>(R.id.gazdinskaLayout)
        val gazdinskaTextView = view.findViewById<TextView>(R.id.gazdinskaJedTextView)

        val odsekLayout = view.findViewById<ConstraintLayout>(R.id.odsekLayout)
        val odsekTextView = view.findViewById<TextView>(R.id.odsekTextView)

        val korisnikLayout = view.findViewById<ConstraintLayout>(R.id.korisnikLayout)
        val korisnikTextView = view.findViewById<TextView>(R.id.korisnikTextView)
        //OVA 3 IZNAD IZLAZE OPCIJE


        //samo ovo za tastaturu
        val odeljenjeLayout = view.findViewById<ConstraintLayout>(R.id.odeljenjeLayout)
        val odeljenjeTextView = view.findViewById<TextView>(R.id.odeljenjeTextView)

        keyboardTextViews = hashMapOf(
            odeljenjeLayout to odeljenjeTextView
        )
        optionsTextViews = hashMapOf(
            gazdinskaLayout to gazdinskaTextView,
            odsekLayout to odsekTextView,
            korisnikLayout to korisnikTextView
        )
        optionsFileNames = hashMapOf(
            gazdinskaTextView to Pair("gazdinske_jed",ListOptionsField.GAZDINSKA_JED),
            odsekTextView to Pair("odseci",ListOptionsField.ODSEK),
            korisnikTextView to Pair("korisnici",ListOptionsField.KORISNIK)
        )

        setupInputKeyboardClickListeners(keyboardTextViews)
        setupOptionsClickListener(optionsTextViews)

        return view;
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StartMeasuringFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StartMeasuringFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun setupInputKeyboardClickListeners(inputFields: HashMap<ConstraintLayout,TextView>) {
        inputFields.keys.forEach { layout ->
            layout.setOnClickListener(){
                currentInputField = keyboardTextViews[layout]!!
                openKeyboard()
            }
        }
    }

    private fun setupOptionsClickListener(optionFields: HashMap<ConstraintLayout,TextView>){
        optionFields.keys.forEach{ layout ->
            layout.setOnClickListener(){
                currentOptionField = optionFields[layout]!!
                openOptions()
            }
        }
    }

    //otvaranje tastature
    fun openKeyboard(){
        val keyboardFragment = KeyboardFragment().apply {
            setKeyboardListener(this@StartMeasuringFragment)
        }
        currentInputField.text = ""
        parentFragmentManager.beginTransaction()
            .add(R.id.main, keyboardFragment)
            .addToBackStack(null)
            .commit()
    }

    fun openOptions(){
        val options : List<String> = getListFromPrefs()
        val optionsFragment = ListOptionsFragment().apply {
            setListItems(options)
            setOptionField(optionsFileNames[currentOptionField]?.second)
            setListener(this@StartMeasuringFragment)
        }
        optionsFragment.show(parentFragmentManager,null)
    }

    fun getListFromPrefs(): MutableList<String> {
        val sharedPrefs = context?.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val joined = sharedPrefs?.getString(optionsFileNames[currentOptionField]?.first, "") ?: ""

        // If the joined string is not empty, split it and return a mutable list, else return an empty mutable list
        return if (joined.isNotEmpty()) {
            joined.split(",").toMutableList()  // Convert the result of split() to a mutable list
        } else {
            mutableListOf()  // Return an empty mutable list if the joined string is empty
        }
    }
    fun saveListToPrefs(context: Context?, list: MutableList<String>) {
        val sharedPrefs = context?.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val joined = list.joinToString(",") // e.g. "123,456,789"
        sharedPrefs?.edit()?.putString(optionsFileNames[currentOptionField]?.first, joined)?.apply()
    }

    override fun onKeyPressed(key: String) {
        currentInputField.append(key)
    }

    override fun onEnterPressed(input: String) {
        currentInputField.text = input
    }


    override fun onClearPressed() {
        currentInputField.text = ""
    }

    override fun addOption(option : String) {
        val newList = getListFromPrefs()
        newList.add(option)
        saveListToPrefs(context,newList)
    }
}