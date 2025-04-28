package com.example.woodometer.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.woodometer.R
import com.example.woodometer.activities.MainActivity
import com.example.woodometer.adapters.TreesAdapter
import com.example.woodometer.databinding.FragmentCircleBinding
import com.example.woodometer.interfaces.CircleListener
import com.example.woodometer.interfaces.KeyboardListener
import com.example.woodometer.interfaces.TreeListener
import com.example.woodometer.interfaces.TreeTypeListener
import com.example.woodometer.model.Krug
import com.example.woodometer.model.MrtvoStablo
import com.example.woodometer.model.Stablo
import com.example.woodometer.model.enumerations.KeyboardField
import com.example.woodometer.utils.GlobalUtils.VRSTE_DRVECA
import com.example.woodometer.utils.KeyboardUtils.currentInputField
import com.example.woodometer.utils.KeyboardUtils.setupInputKeyboardClickListeners
import com.example.woodometer.utils.NotificationsUtils.showErrToast
import com.example.woodometer.utils.NotificationsUtils.showSuccessToast
import com.example.woodometer.utils.PreferencesUtils
import com.example.woodometer.viewmodels.KrugViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CircleFragment.newInstance] factory method to
 * c.reate an instance of this fragment.
 */
class CircleFragment : Fragment(), KeyboardListener,TreeTypeListener,TreeListener,CircleListener {

    private var _binding: FragmentCircleBinding? = null
    private val binding get() = _binding!! // Safe to use after onCreateView

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //polja koja otvaraju tastaturu
    private lateinit var keyboardTextViews : HashMap<ConstraintLayout,Triple<TextInputEditText,String,KeyboardField>>
    private lateinit var vrstaButton : Button


    private lateinit var stablaRecyclerView : RecyclerView
    private lateinit var adapter: TreesAdapter


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
        krugViewModel = ViewModelProvider(requireActivity())[KrugViewModel::class.java]

        _binding!!.lifecycleOwner = viewLifecycleOwner
        binding.krugVM = krugViewModel

        binding.backButton.setOnClickListener{parentFragmentManager.popBackStack()}
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TreesAdapter(mutableListOf(),this)

        viewLifecycleOwner.lifecycleScope.launch {
            //Ako postoji bar jedno stablo u krugu, pamtimo njegov hash da bi kada dolazi do menjanja stabala znali da li da cuvamo ili ne
            krugViewModel.setStablaKruga()

            krugViewModel.stablaKruga.observe(viewLifecycleOwner) { stabla ->
                adapter.updateData(stabla)
            }
            // Handle tree type button click
            vrstaButton = binding.vrstaDrvetaButton
            vrstaButton.setOnClickListener {
                TreeTypesFragment().apply {
                    setListener(this@CircleFragment)
                    krugViewModel.trenutnoStablo.value?.vrsta?.let { it1 -> setStartTreeType(it1) }
                }.show(parentFragmentManager, null)
            }
            //menjanje vrste drveca i broja stabla kada se promeni stablo
            krugViewModel.trenutnoStablo.observe(viewLifecycleOwner){stablo ->
                vrstaButton.text = VRSTE_DRVECA.find{ it.first == stablo.vrsta}?.second
                binding.brojStablaTextView.text = stablo?.rbr.toString()
            }

            //prikaz stabla u scrollu/recycler view
            setupTreesRecyclerView()

            // Setup keyboard
            createKeyboardHashMap()
            setupInputKeyboardClickListeners(keyboardTextViews,parentFragmentManager,this@CircleFragment)

            binding.mrtvaStablaButton.setOnClickListener{
                parentFragmentManager.beginTransaction().replace(R.id.main,DeadTreesFragment()).addToBackStack(null).commit()
            }
            binding.biodiverzitetButton.setOnClickListener{
                parentFragmentManager.beginTransaction().replace(R.id.main,BiodiversityFragment()).addToBackStack(null).commit()
            }
            binding.addTreeButton.setOnClickListener{
                addTreeButtonClicked()
            }
            binding.saveTreeButton.setOnClickListener{
                saveTreeButtonClicked()
            }
            binding.endCircleButton.setOnClickListener{
                endCircleButtonClicked()
            }
            setupWorkingTreeButtons()

            binding.trenutniKrugTextView.text = krugViewModel.trenutniKrug.value?.brKruga.toString()
        }



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
    //Funkcija koja sluzi da sklonimo "zavrsi krug" ako je krug vec zavrsen kao i da se skloni "dodaj krug" dugme ako je krug vec uradjen
    private fun setupWorkingTreeButtons(){
        if (krugViewModel.trenutniKrug.value?.id != krugViewModel.radniKrug.value?.id){
            binding.endCircleButton.visibility = View.GONE
            binding.addTreeButton.visibility = View.INVISIBLE
        }
    }

    private fun setupTreesRecyclerView(){
        stablaRecyclerView = binding.treesRecyclerView
        adapter = TreesAdapter(mutableListOf(), this)
        stablaRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        stablaRecyclerView.adapter = adapter
    }

    private fun addTreeButtonClicked(){
        krugViewModel.resetStablo()
    }

    private fun saveTreeButtonClicked(){
        if (krugViewModel.trenutnoStablo.value?.hasAnyNonDefaultVal()!!){
            krugViewModel.updateTrenutnoStablo()
            showSuccessToast(context,"Sačuvano stablo ${krugViewModel.trenutnoStablo.value?.rbr}")
        }else{
            showErrToast(context, "Morate popuniti bar neku vrednost da biste kreirali novo stablo!")
        }
    }

    private fun endCircleButtonClicked(){
        (activity as MainActivity).showEndCircleDialog(this,krugViewModel.radniKrug.value!!.brKruga,"Da li ste sigurni da želite da završite krug broj ${krugViewModel.radniKrug.value!!.brKruga}")
    }

    private fun createKeyboardHashMap() {
        keyboardTextViews = hashMapOf(
            binding.precnikLayout to Triple(binding.precnikTextView, "Prečnik", KeyboardField.PRECNIK),
            binding.azimutLayout to Triple(binding.azimutTextView, "Azimut", KeyboardField.AZIMUT),
            binding.razdaljinaLayout to Triple(binding.razdaljinaTextView, "Razdaljina", KeyboardField.RAZDALJINA),
            binding.visinaLayout to Triple(binding.visinaTextView, "Visina", KeyboardField.VISINA),
            binding.duzinaDeblaLayout to Triple(binding.duzinaDeblaTextView, "Dužina debla", KeyboardField.DUZINA_DEBLA),
            binding.stepenSusenjaLayout to Triple(binding.stepenSusenjaTextView, "Stepen sušenja", KeyboardField.STEPEN_SUSENJA),
            binding.socStatusLayout to Triple(binding.socStatusTextView, "Socijalni status", KeyboardField.SOCIJALNI_STATUS),
            binding.tehnickaKlasaLayout to Triple(binding.tehKlasaTextView, "Tehnička klasa", KeyboardField.TEHNICKA_KLASA),
            binding.probnaDoznakaLayout to Triple(binding.probDoznakaTextView, "Probna doznaka", KeyboardField.PROBNA_DOZNAKA),
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
        binding.vrstaTextView.setText(key.toString())
    }

    override fun changeTree(stablo : Stablo) {
        if (stablo.id == krugViewModel.trenutnoStablo.value?.id){return}
        adapter.updateSelectedStablo(krugViewModel.trenutnoStablo.value?.rbr!!,stablo.rbr)
        krugViewModel.setTrenutnoStablo(stablo)
        binding.brojStablaTextView.text = krugViewModel.trenutnoStablo.value?.rbr.toString()
    }

    override fun deleteTree(rbr: Int) {
        val activity = requireActivity() as MainActivity
        activity.showDeleteConfirmationDialog(this,rbr)
    }

    override fun deleteConfirmed(deleted: Boolean, rbr: Int) {
        if (deleted){
            krugViewModel.deleteStablo(rbr)
            krugViewModel.setDefaultStablo()
        }
    }

    override fun editTree(item: MrtvoStablo) {
        TODO("Not yet implemented")
    }

    override fun circleChanged(krug: Krug) {
        TODO("Not yet implemented")
    }

    override fun finishConfirmed(finish: Boolean, rbr: Int) {
        val isValid : Pair<Boolean,List<Int>> = krugViewModel.isKrugValid()
        if (!isValid.first){
            showErrToast(context,"Stabla broj ${isValid.second.joinToString(",") } su invalidna! ")
            return
        }
        if (finish){
            showSuccessToast(context, "Završen krug broj $rbr.")
            PreferencesUtils.clearWorkingCircleFromPrefs(context)
            krugViewModel.setDefaultRadniKrug()
            parentFragmentManager.popBackStack()
        }
    }

    override fun showEditDeleteDialog(krug: Krug) {
        TODO("Not yet implemented")
    }
}