package com.example.woodometer.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.transition.Visibility
import com.example.woodometer.R
import com.example.woodometer.Woodometer
import com.example.woodometer.databinding.FragmentAddCircleBinding
import com.example.woodometer.databinding.FragmentAddDeadTreeBinding
import com.example.woodometer.interfaces.KeyboardListener
import com.example.woodometer.interfaces.SilviculturalTypeListener
import com.example.woodometer.model.Dokument
import com.example.woodometer.model.Krug
import com.example.woodometer.model.enumerations.KeyboardField
import com.example.woodometer.utils.GlobalUtils.lastDokument
import com.example.woodometer.utils.GlobalUtils.lastKrug
import com.example.woodometer.utils.KeyboardUtils
import com.example.woodometer.utils.KeyboardUtils.currentInputField
import com.example.woodometer.utils.NotificationsUtils
import com.example.woodometer.utils.PreferencesUtils
import com.example.woodometer.viewmodels.DokumentViewModel
import com.example.woodometer.viewmodels.KrugViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import com.example.woodometer.utils.NotificationsUtils.showSuccessToast

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddCircleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddCircleFragment : Fragment(), KeyboardListener,SilviculturalTypeListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var isEdit : Boolean? = null

    fun setIsEdit(isEdit : Boolean){
        this.isEdit = isEdit
    }

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

    private var initialKrugHash : Int? = null

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
        binding.gazdinskiTipLayout.setOnClickListener{setupSilviculturalTypeInput()}

        if(isEdit == true){
            initialKrugHash = krugVM.trenutniKrug.value.hashCode()
            binding.createButton.text = "SAČUVAJ"
            setRadioButtons()
        }
        createButtonClick()


        return binding.root
    }
    //funkcija pomocu koje otvaramo izbor za gazdinski tip!
    private fun setupSilviculturalTypeInput(){
        val fragment = SilviculturalTypesFragment().apply {
            setListener(this@AddCircleFragment)
        }
        parentFragmentManager.beginTransaction().add(R.id.main,fragment).addToBackStack(null).commit()
    }
    private fun createButtonClick(){
        binding.createButton.setOnClickListener {
                if (initialKrugHash == krugVM.trenutniKrug.value.hashCode()){
                    parentFragmentManager.popBackStack()
                    return@setOnClickListener
                }
                else if (isValid()) {
                    val message = if (isEdit == true) "Sačuvan krug ${krugVM.trenutniKrug.value?.brKruga}" else
                                  "Kreiran krug ${krugVM.trenutniKrug.value?.brKruga}"
                    showSuccessToast(context,message)
                    lifecycleScope.launch {
                        ensureDocument {
                            val dokumentId = dokumentVM.trenutniDokument.value?.id!!

                            if (isEdit == true){
                                krugVM.addKrug()
                            }else{
                                krugVM.resetKrug(dokumentId)
                            }

                            //cuvanje trenutnog kruga  AKO NIJE AZURIRANJE!!!
                            krugVM.trenutniKrug.value?.let { it1 ->
                                if (isEdit != true){
                                    PreferencesUtils.saveWorkingCircleToPrefs(context,
                                        it1.id)
                                }
                                dokumentVM.addKrug(it1)
                            }
                            lastKrug = null
                            parentFragmentManager.popBackStack()
                            if (isEdit != true){
                                withContext(Dispatchers.Main) {
                                    parentFragmentManager.beginTransaction()
                                        .replace(R.id.main, CircleFragment())
                                        .addToBackStack(null)
                                        .commit()
                                }
                            }
                        }
                    }

                } else {
                    NotificationsUtils.showErrToast(context,"Svi podaci se moraju popuniti!")
                    NotificationsUtils.showWarningToast(context,"Podsetnik : ID kruga mora biti unet ako je krug permanentan!")
                }

        }
    }

    suspend fun ensureDocument(onReady: suspend () -> Unit) {
        val existsAndSame = suspendCancellableCoroutine<Boolean> { cont ->
            dokumentVM.existsAndSame { exists -> cont.resume(exists, null) }
        }
        if (!existsAndSame) {
            dokumentVM.add()
        }
        onReady()
    }

    private fun setupKeyboardFields(){
        keyboardTextViews = hashMapOf(
            binding.nagibLayout to Triple (binding.nagibTextView,"Nagib",KeyboardField.NAGIB),
            binding.IDLayout to Triple (binding.IDTextView,"ID",KeyboardField.ID),
            binding.uzgojnaGrupaLayout to Triple (binding.uzgojnaGrupaTextView,"Uzgojna grupa",KeyboardField.UZGOJNA_GRUPA),
            binding.brojKrugaLayout to Triple (binding.brojKrugaTextView,"Broj kruga",KeyboardField.BR_KRUG),
        )
        KeyboardUtils.setupInputKeyboardClickListeners(
            keyboardTextViews, parentFragmentManager, this
        )
    }
        // AKO JE EDIT MODE STAVLJAMO VREDNOSTI KOJE SU VEC SACUVANE
    private fun setRadioButtons(){
        if(krugVM.trenutniKrug.value?.permanentna!!){
            binding.permanentButton.isChecked = true
        }else{
            binding.obicanButton.isChecked = true
        }
        if (krugVM.trenutniKrug.value?.pristupacnost!!){
            binding.yesButton.isChecked = true
        }else{
            binding.noButton.isChecked = true
        }
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
        return !krug.hasAnyDefaultVal()
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


    override fun setSilviculturalType(key: Int) {
        binding.gazdinskiTipTextView.setText(key.toString())
    }

}