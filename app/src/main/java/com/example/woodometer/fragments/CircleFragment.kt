package com.example.woodometer.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import com.example.woodometer.R
import com.example.woodometer.databinding.FragmentCircleBinding
import com.example.woodometer.interfaces.KeyboardListener
import com.example.woodometer.interfaces.TreeTypeListener
import com.example.woodometer.model.enumerations.KeyboardField
import com.example.woodometer.utils.KeyboardUtils.currentInputField
import com.example.woodometer.utils.KeyboardUtils.setupInputKeyboardClickListeners
import com.example.woodometer.viewmodels.KrugViewModel
import com.google.android.material.textfield.TextInputEditText

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CircleFragment.newInstance] factory method to
 * c.reate an instance of this fragment.
 */
class CircleFragment : Fragment(), KeyboardListener,TreeTypeListener {

    private var _binding: FragmentCircleBinding? = null
    private val binding get() = _binding!! // Safe to use after onCreateView

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //polja koja otvaraju tastaturu
    private lateinit var keyboardTextViews : HashMap<ConstraintLayout,Triple<TextInputEditText,String,KeyboardField>>
    private lateinit var vrstaButton : Button
    private var currentTreeType = 0


    private lateinit var krugViewModel: KrugViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCircleBinding.inflate(inflater, container, false)

        // Initialize ViewModel
        krugViewModel = ViewModelProvider(requireActivity())[KrugViewModel::class.java]
        _binding!!.lifecycleOwner = viewLifecycleOwner
        binding.krugVM = krugViewModel

        // Setup keyboard
        createKeyboardHashMap(binding.root)
        setupInputKeyboardClickListeners(keyboardTextViews,parentFragmentManager,this)

        // Handle button click
        vrstaButton = binding.vrstaDrvetaButton
        vrstaButton.setOnClickListener {
            TreeTypesFragment().apply {
                setListener(this@CircleFragment)
            }.show(parentFragmentManager, null)
        }

        binding.mrtvaStablaButton.setOnClickListener{
            parentFragmentManager.beginTransaction().replace(R.id.main,DeadTreesFragment()).addToBackStack(null).commit()
        }
        binding.biodiverzitetButton.setOnClickListener{
            parentFragmentManager.beginTransaction().replace(R.id.main,BiodiversityFragment()).addToBackStack(null).commit()
        }

        return binding.root
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
            Triple(view.findViewById(R.id.precnikTextView),"Prečnik",KeyboardField.PRECNIK),
            view.findViewById<ConstraintLayout>(R.id.azimutLayout) to
            Triple(view.findViewById(R.id.azimutTextView),"Azimut",KeyboardField.AZIMUT),
            view.findViewById<ConstraintLayout>(R.id.razdaljinaLayout) to
            Triple(view.findViewById(R.id.razdaljinaTextView),"Razdaljina",KeyboardField.RAZDALJINA),
            view.findViewById<ConstraintLayout>(R.id.visinaLayout) to
            Triple(view.findViewById(R.id.visinaTextView),"Visina",KeyboardField.VISINA),
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


    override fun onEnterPressed(input: String) {
        currentInputField?.setText(input)
    }


    override fun onClearPressed() {
        currentInputField?.setText("")
    }

    override fun setTreeType(name: String, key: Int) {
        vrstaButton.text = name
        currentTreeType = key
    }
}