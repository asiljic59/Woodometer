package com.example.woodometer.fragments

import android.content.Context
import android.content.SharedPreferences.Editor
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import com.example.woodometer.R
import com.example.woodometer.databinding.FragmentCircleBinding
import com.example.woodometer.databinding.FragmentStartMeasuringBinding
import com.example.woodometer.interfaces.AddOptionListener
import com.example.woodometer.interfaces.KeyboardListener
import com.example.woodometer.model.enumerations.KeyboardField
import com.example.woodometer.model.enumerations.ListOptionsField
import com.example.woodometer.utils.PreferencesUtils
import com.example.woodometer.utils.PreferencesUtils.getListFromPrefs
import com.example.woodometer.viewmodels.DokumentViewModel
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

    private var _binding: FragmentStartMeasuringBinding? = null
    private val binding get() = _binding!! // Safe to use after onCreateView

    private lateinit var currentInputField : TextView
    private lateinit var currentOptionField : TextView
    private lateinit var currentOptionsFile : String

    private lateinit var dokumentVM : DokumentViewModel

    //polja koja otvaraju tastaturu
    private lateinit var keyboardTextViews : HashMap<ConstraintLayout,Triple<TextView,String,KeyboardField>>

    //polja koja otvaraju opcije
    private lateinit var optionsTextViews : HashMap<ConstraintLayout,Triple<TextView,String,ListOptionsField>>

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
        _binding = FragmentStartMeasuringBinding.inflate(inflater,container,false)
        dokumentVM = ViewModelProvider(requireActivity())[DokumentViewModel::class.java]

        binding.dokumentVM = dokumentVM
        binding.lifecycleOwner = viewLifecycleOwner

        val view : View = binding.root
        val krugButton = view.findViewById<Button>(R.id.krugoviButton)

        krugButton.setOnClickListener{
            parentFragmentManager.beginTransaction().add(R.id.main,AddCircleFragment()).addToBackStack(null).commit()
        }

        val backButton = view.findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener{
            parentFragmentManager.popBackStack()
        }



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
            odeljenjeLayout to Triple(odeljenjeTextView,"ODELJENJE",KeyboardField.ODELJENJE)
        )
        optionsTextViews = hashMapOf(
            gazdinskaLayout to Triple(gazdinskaTextView,"gazdinske_jed",ListOptionsField.GAZDINSKA_JED),
            odsekLayout to Triple(odsekTextView,"odseci",ListOptionsField.ODSEK),
            korisnikLayout to Triple(korisnikTextView,"korisnici",ListOptionsField.KORISNIK)
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

    private fun setupInputKeyboardClickListeners(inputFields: HashMap<ConstraintLayout,Triple<TextView,String,KeyboardField>>) {
        inputFields.keys.forEach { layout ->
            layout.setOnClickListener(){
                currentInputField = keyboardTextViews[layout]?.first!!
                openKeyboard(layout)
            }
        }
    }

    private fun setupOptionsClickListener(optionFields: HashMap<ConstraintLayout,Triple<TextView,String,ListOptionsField>>){
        optionFields.keys.forEach{ layout ->
            layout.setOnClickListener(){
                currentOptionField = optionFields[layout]?.first!!
                currentOptionsFile = optionFields[layout]?.second!!
                openOptions(layout)
            }
        }
    }

    //otvaranje tastature
    fun openKeyboard(layout: ConstraintLayout){
        val keyboardFragment = KeyboardFragment().apply {
            setKeyboardListener(this@StartMeasuringFragment)
            keyboardTextViews[layout]?.let { setTitle(it.second) }
            keyboardTextViews[layout]?.third?.let { setField(it) }
        }
        currentInputField.text = ""
        parentFragmentManager.beginTransaction()
            .add(R.id.main, keyboardFragment)
            .addToBackStack(null)
            .commit()
    }
    fun openOptions(layout: ConstraintLayout){
        val options : List<String> = getListFromPrefs(currentOptionsFile,context)
        val optionsFragment = ListOptionsFragment().apply {
            setListItems(options)
            setOptionField(optionsTextViews[layout]?.third)
            setListener(this@StartMeasuringFragment)
        }
        optionsFragment.show(parentFragmentManager,null)
    }



    override fun onEnterPressed(input: String) {
        currentInputField.text = input
    }


    override fun onClearPressed() {
        currentInputField.text = ""
    }

    override fun addOption(option : String) {
        val newList = getListFromPrefs(currentOptionsFile,context)
        newList.add(option)
        PreferencesUtils.saveListToPrefs(context,newList,currentOptionsFile)
    }

    override fun optionPicked(option: String) {
        currentOptionField.text = option
    }
}