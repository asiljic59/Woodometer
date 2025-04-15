package com.example.woodometer.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.woodometer.R
import com.example.woodometer.activities.MainActivity
import com.example.woodometer.databinding.FragmentAddDeadTreeBinding
import com.example.woodometer.interfaces.KeyboardListener
import com.example.woodometer.interfaces.TreeTypeListener
import com.example.woodometer.model.MrtvoStablo
import com.example.woodometer.model.enumerations.KeyboardField
import com.example.woodometer.viewmodels.KrugViewModel
import com.google.android.material.textfield.TextInputEditText

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddDeadTreeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddDeadTreeFragment : Fragment(),KeyboardListener,TreeTypeListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var isAddition : Boolean? = null

    fun setAddition(isAddition : Boolean){
        this.isAddition = isAddition
    }

    private lateinit var currentInputField : TextView
    private lateinit var keyboardTextViews : HashMap<ConstraintLayout,Triple<TextInputEditText,String,KeyboardField>>
    private lateinit var vrstaButton:TextView
    private lateinit var krugVM : KrugViewModel

    private var currentTreeType : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private var _binding: FragmentAddDeadTreeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddDeadTreeBinding.inflate(inflater, container, false)
        val view = binding.root
        view.setPadding(0, 0, 0, 0)


        krugVM = ViewModelProvider(requireActivity())[KrugViewModel::class.java]
        setVrstaButton()

        _binding!!.lifecycleOwner = viewLifecycleOwner
        binding.krugVM = krugVM

        createKeyboardHashMap(view)

        binding.saveButton.setOnClickListener {
            if (isDeadTreeValid()) {
                if (isAddition == true){krugVM.addMrtvoStablo()}
                else{krugVM.editMrtvoStablo()}
                parentFragmentManager.popBackStack()
            } else {
                Toast.makeText(context, "Morate uneti sve parametre mrtvog stabla!", Toast.LENGTH_SHORT).show()
            }
        }

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
    //podesavamo dogadjaje pritiska na dugme za vrste stabla kao i azuriranje teksta na samom dugmetu!
    fun setVrstaButton(){
        vrstaButton = binding.vrstaDrvetaButton
        vrstaButton.setOnClickListener {
            val treeTypesFragment = TreeTypesFragment().apply {
                setListener(this@AddDeadTreeFragment)
            }
            treeTypesFragment.show(parentFragmentManager, null)
        }
        if (isAddition == false){
            val activity = requireActivity() as MainActivity
            vrstaButton.text =
                activity.VRSTE_DRVECA.filter { it.first ==
                    krugVM.mrtvoStablo.value?.vrsta
                }.first().second
        }
    }

    fun isDeadTreeValid() : Boolean{
        val mrtvoStablo = krugVM.mrtvoStablo.value
        return (mrtvoStablo?.precnik != 0f || mrtvoStablo.visina != 0 || mrtvoStablo.vrsta != 0 || mrtvoStablo.polozaj != 0 )
    }

    private fun createKeyboardHashMap(view: View){
        keyboardTextViews = hashMapOf(
            view.findViewById<ConstraintLayout>(R.id.precnikLayout) to
                    Triple(view.findViewById<TextInputEditText>(R.id.precnikTextView),"Prečnik",
                        KeyboardField.PRECNIK),
            view.findViewById<ConstraintLayout>(R.id.visinaLayout) to
                    Triple(view.findViewById<TextInputEditText>(R.id.visinaTextView),"Visina", KeyboardField.VISINA),
            view.findViewById<ConstraintLayout>(R.id.polozajStablaLayout)  to
                    Triple(view.findViewById<TextInputEditText>(R.id.polozajStablaTextView),"Polozaj stabla",KeyboardField.POLOZAJ_STABLA)
        )
        setupInputKeyboardClickListeners(keyboardTextViews)
    }

    //U ovoj funkciji takodje resavamo problem klika na Edit Text to jest da ne registruje normalan klik na edit text
    //Vec omogucavamo da kada se klikne tacno na edit text bude otvoren keyboard!
    @SuppressLint("ClickableViewAccessibility")
    private fun setupInputKeyboardClickListeners(inputFields: HashMap<ConstraintLayout,Triple<TextInputEditText,String,KeyboardField>>) {
        inputFields.forEach { (layout, triple) ->
            val inputView = triple.first
            var clicked = false // flag to prevent double trigger
            layout.setOnClickListener {
                if (!clicked) {
                    clicked = true
                    currentInputField = inputView
                    openKeyboard(layout)

                    // reset the flag after a short delay to allow next click
                    layout.postDelayed({ clicked = false }, 200)
                }
            }
            inputView.setOnTouchListener { _, _ ->
                layout.performClick() // This still triggers the click listener above
                true
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
        parentFragmentManager.beginTransaction().add(R.id.main,keyboardFragment).addToBackStack(null).commit()
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