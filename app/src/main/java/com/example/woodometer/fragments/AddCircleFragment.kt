package com.example.woodometer.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Visibility
import com.example.woodometer.R
import com.example.woodometer.Woodometer
import com.example.woodometer.databinding.FragmentAddCircleBinding
import com.example.woodometer.databinding.FragmentAddDeadTreeBinding
import com.example.woodometer.interfaces.KeyboardListener
import com.example.woodometer.model.Dokument
import com.example.woodometer.model.Krug
import com.example.woodometer.model.enumerations.KeyboardField
import com.example.woodometer.utils.KeyboardUtils
import com.example.woodometer.utils.KeyboardUtils.currentInputField
import com.example.woodometer.viewmodels.DokumentViewModel
import com.example.woodometer.viewmodels.KrugViewModel
import com.google.android.material.textfield.TextInputEditText

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddCircleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddCircleFragment : Fragment(), KeyboardListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    private lateinit var krugVM : KrugViewModel
    private lateinit var dokumentVM : DokumentViewModel
    private lateinit var keyboardTextViews : HashMap<ConstraintLayout,Triple<TextInputEditText,String, KeyboardField>>


    private var _binding: FragmentAddCircleBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddCircleBinding.inflate(inflater, container, false)
        krugVM = ViewModelProvider(requireActivity())[KrugViewModel::class.java]
        dokumentVM = ViewModelProvider(requireActivity())[DokumentViewModel::class.java]
        binding.krugVM = krugVM

        binding.lifecycleOwner = viewLifecycleOwner
        setRadioGroups()

        setupKeyboardFields()

        binding.closeButton.setOnClickListener { parentFragmentManager.popBackStack() }

        createButtonClick()

        return binding.root
    }

    private fun createButtonClick(){
        binding.createButton.setOnClickListener {
            if (isValid()) {
                parentFragmentManager.popBackStack()
                parentFragmentManager.beginTransaction().replace(R.id.main, CircleFragment())
                    .addToBackStack(null).commit()
                checkDocument()
            } else {
                Toast.makeText(
                    context,
                    "Svi podaci se moraju uneti! \n Podsetnik : Mora se uneti ID kruga ako je tačka permanentna!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun checkDocument(){
        dokumentVM.exists { exists ->
            if (!exists) {
                //dodajemo trenutni dokument
                dokumentVM.add()
                krugVM.trenutniKrug.value?.dokumentId = dokumentVM.trenutniDokument.value?.id!!
            }
        }
    }

    private fun setupKeyboardFields(){
        keyboardTextViews = hashMapOf(
            binding.nagibLayout to Triple (binding.nagibTextView,"Nagib",KeyboardField.NAGIB),
            binding.IDLayout to Triple (binding.IDTextView,"ID",KeyboardField.ID),
            binding.gazdinskiTipLayout to Triple (binding.gazdinskiTipTextView,"Gazdinski tip",KeyboardField.GAZ_TIP),
            binding.uzgojnaGrupaLayout to Triple (binding.uzgojnaGrupaTextView,"Uzgojna grupa",KeyboardField.UZGOJNA_GRUPA),
            binding.brojKrugaLayout to Triple (binding.brojKrugaTextView,"Broj kruga",KeyboardField.BR_KRUG),
        )
        KeyboardUtils.setupInputKeyboardClickListeners(
            keyboardTextViews, parentFragmentManager, this
        )
    }

    private fun setRadioGroups() {
        //PRISTUPACNOST
        binding.pristupacnostRadioGroup.setOnCheckedChangeListener{group,checkedId ->
            when(checkedId) {
                R.id.yesButton -> {krugVM.trenutniKrug.value?.pristupacnost = true}
                R.id.noButton -> {krugVM.trenutniKrug.value?.pristupacnost = false}
            }
        }

        //TIP KRUGA
        binding.tipRadioGroup.setOnCheckedChangeListener{group, checkedId ->
            when(checkedId){
                R.id.permanentButton -> {
                    krugVM.trenutniKrug.value?.permanentna = true
                    binding.IDLayout.visibility = View.VISIBLE
                }
                R.id.obicanButton -> {
                    krugVM.trenutniKrug.value?.permanentna = false
                    krugVM.trenutniKrug.value?.IdBroj = null
                    binding.IDLayout.visibility = View.GONE
                }
            }
        }
    }

    private fun isValid() : Boolean{
        val krug : Krug = krugVM.trenutniKrug.value!!
        val dokument : Dokument = dokumentVM.trenutniDokument.value!!
        return isKrugValid(krug) && isDokumentValid(dokument)
    }
    private fun isDokumentValid(dokument: Dokument) : Boolean{
        return !(dokument.brOdeljenja == 0 ||  dokument.gazJedinica == 0 || dokument.odsek == "" || dokument.korisnik == 0)
    }

    private fun isKrugValid(krug : Krug) : Boolean{
        return !(krug.brKruga == 0 || krug.permanentna == null
                || krug.pristupacnost == null || krug.gazTip == 0
                || krug.uzgojnaGrupa == 0 || krug.nagib == 0f || (krug.permanentna == true && krug.IdBroj == 0))
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddCircleFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddCircleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onEnterPressed(input: String) {
        currentInputField?.setText(input)
    }


    override fun onClearPressed() {
        currentInputField?.setText("")
    }

}