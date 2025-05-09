package com.example.woodometer.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.woodometer.R
import com.example.woodometer.activities.MainActivity
import com.example.woodometer.adapters.CircleAdapter
import com.example.woodometer.databinding.FragmentStartMeasuringBinding
import com.example.woodometer.interfaces.AddOptionListener
import com.example.woodometer.interfaces.CircleListener
import com.example.woodometer.interfaces.KeyboardListener
import com.example.woodometer.model.Dokument
import com.example.woodometer.model.Krug
import com.example.woodometer.model.enumerations.KeyboardField
import com.example.woodometer.model.enumerations.ListOptionsField
import com.example.woodometer.utils.ExportDataUtils
import com.example.woodometer.utils.ExportJsonUtils
import com.example.woodometer.utils.KeyboardUtils
import com.example.woodometer.utils.NotificationsUtils.showErrToast
import com.example.woodometer.utils.NotificationsUtils.showSuccessToast
import com.example.woodometer.utils.NotificationsUtils.showWarningToast
import com.example.woodometer.utils.PreferencesUtils
import com.example.woodometer.utils.PreferencesUtils.getListFromPrefs
import com.example.woodometer.viewmodels.DokumentViewModel
import com.example.woodometer.viewmodels.KrugViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StartMeasuringFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class StartMeasuringFragment : Fragment(), KeyboardListener,AddOptionListener,CircleListener  {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentStartMeasuringBinding? = null
    private val binding get() = _binding!! // Safe to use after onCreateView

    private lateinit var currentOptionField : TextView
    private lateinit var currentOptionsFile : String

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CircleAdapter

    private lateinit var dokumentVM : DokumentViewModel
    private lateinit var krugVM : KrugViewModel

    //polja koja otvaraju tastaturu
    private lateinit var keyboardTextViews : HashMap<ConstraintLayout,Triple<TextInputEditText,String,KeyboardField>>

    //polja koja otvaraju opcije
    private lateinit var optionsTextViews : HashMap<ConstraintLayout,Triple<TextInputEditText,String,ListOptionsField>>

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
        krugVM = ViewModelProvider(requireActivity())[KrugViewModel::class.java]

        binding.dokumentVM = dokumentVM
        binding.lifecycleOwner = viewLifecycleOwner



        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCirclesRecyclerView()
        lifecycleScope.launch {
            dokumentVM.krugovi.observe(viewLifecycleOwner) {krugovi ->
                adapter.updateData(krugovi)
            }
            dokumentVM.getKrugovi()

            val krugButton = view.findViewById<Button>(R.id.krugoviButton)

            krugButton.setOnClickListener{
                addCircleClicked()
            }

            val backButton = view.findViewById<ImageButton>(R.id.backButton)
            backButton.setOnClickListener{
                parentFragmentManager.popBackStack()
            }

            binding.noviDokumentButton.setOnClickListener{
                dokumentVM.setTrenuntniDokument(Dokument())
                dokumentVM.setTrenutniKrugovi(mutableListOf())
                setupMetaData()
            }
            binding.izvozTxtButton.setOnClickListener(){
                izvozTxtClicked()
            }

            setupInputListeners()
            //stavljanje imena dokumenta itd itd
            setupMetaData()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        lifecycleScope.launch {
            dokumentVM.existsAndSame { result ->
                if (!result && !dokumentVM.trenutniDokument.value?.hasAnyDefaultVal()!!) {dokumentVM.add()}
            }
        }
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
    private fun izvozTxtClicked(){
        if (dokumentVM.krugovi.value?.isEmpty() == true){
            showWarningToast(context,"Nemate nijedan krug u trenutnom dokumentu")
            return
        }else if (dokumentVM.existsKrug(krugVM.radniKrug.value)){
            showErrToast(context, "Radni krug broj ${krugVM.radniKrug.value?.brKruga} se nalazi u ovom dokumentu!")
            showWarningToast(context, "Svi krugovi moraju biti završeni pre izvoza!")
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            val file = ExportDataUtils.exportDocument(dokumentVM.trenutniDokument.value!!,dokumentVM.krugovi.value!!,activity)
            val jsonFile = ExportJsonUtils.exportToJson(dokumentVM.trenutniDokument.value!!,dokumentVM.krugovi.value!!,activity)
            withContext(Dispatchers.Main) {
                val fileUri = context?.let {
                    FileProvider.getUriForFile(
                        it,
                        "${activity?.packageName}.provider",
                        file
                    )
                }

                val fileJsonUri = context?.let {
                    FileProvider.getUriForFile(
                        it,
                        "${activity?.packageName}.provider",
                        jsonFile
                    )
                }

                val uris = ArrayList<Uri>()
                fileUri?.let { uris.add(it) }
                fileJsonUri?.let { uris.add(it) }

                val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                    type = "*/*"
                    putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                startActivity(Intent.createChooser(shareIntent, "Send file to..."))
            }
        }

    }

    //funkcija kojom omogucavamo korisniku da unosi podatke o dokumentu , AKO JE DOKUMENT NOV (KLIKNUTO DUGME NOVI DOKUMENT)
    private fun setupInputListeners(){
        keyboardTextViews = hashMapOf(
            binding.odeljenjeLayout to Triple(binding.odeljenjeTextView,"ODELJENJE",KeyboardField.ODELJENJE)
        )
        optionsTextViews = hashMapOf(
            binding.gazdinskaLayout to Triple(binding.gazdinskaJedTextView, "gazdinske_jed", ListOptionsField.GAZDINSKA_JED),
            binding.odsekLayout to Triple(binding.odsekTextView, "odseci", ListOptionsField.ODSEK),
            binding.korisnikLayout to Triple(binding.korisnikTextView, "korisnici", ListOptionsField.KORISNIK)
        )
        //OVA 3 IZNAD IZLAZE OPCIJE
        KeyboardUtils.setupInputKeyboardClickListeners(keyboardTextViews,parentFragmentManager,this@StartMeasuringFragment)
        setupOptionsClickListener()
    }

    private fun addCircleClicked(){
        if(!isDokumentValid()){
            showErrToast(context,"Svi podaci dokumenta se moraju uneti!")
            return
        }
        if (krugVM.radniKrug.value == null){
            krugVM.setTrenutniKrug(Krug(dokumentId = dokumentVM.trenutniDokument.value?.id!!))
            parentFragmentManager.beginTransaction().add(R.id.main,AddCircleFragment()).addToBackStack(null).commit()
        }else{
            (activity as MainActivity).showEndCircleDialog(this,krugVM.radniKrug.value!!.brKruga,"Krug broj ${krugVM.radniKrug.value!!.brKruga} nije zavšen. Da li želite završiti trenutni radni krug?")
        }
    }

    private fun isDokumentValid() : Boolean{
        val dokument : Dokument = dokumentVM.trenutniDokument.value!!
        return !dokument.hasAnyDefaultVal()
    }

    //Krugovi recycler view
    private fun setupCirclesRecyclerView(){
        recyclerView = binding.krugoviRecyclerView
        adapter = CircleAdapter(mutableListOf(),this)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
    }
    //meta data o dokumentu
    private fun setupMetaData(){
        dokumentVM.trenutniDokument.observe(viewLifecycleOwner){dokument ->
                "${dokument.gazJedinica}${dokument.brOdeljenja}_${dokument.odsek}_${dokument.korisnik}".also { binding.dokumentTextView.text = it }
                binding.dateTimeTextView.text = formatTimestamp(dokument.timestamp)
        }
    }

    fun formatTimestamp(timestamp: Long): String {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            .withZone(ZoneId.systemDefault())

        return formatter.format(Instant.ofEpochMilli(timestamp))
    }


    private fun setupOptionsClickListener(){
        optionsTextViews.keys.forEach{ layout ->
            layout.setOnClickListener(){
                currentOptionField = optionsTextViews[layout]?.first!!
                currentOptionsFile = optionsTextViews[layout]?.second!!
                openOptions(layout)
            }
        }
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
        KeyboardUtils.currentInputField?.setText(input)
    }


    override fun onClearPressed() {
        KeyboardUtils.currentInputField?.setText("")
    }

    override fun addOption(option : String) {
        val newList = getListFromPrefs(currentOptionsFile,context)
        newList.add(option)
        PreferencesUtils.saveListToPrefs(context,newList,currentOptionsFile)
    }

    override fun optionPicked(option: String) {
        currentOptionField.text = option
    }

    override fun circleChanged(krug: Krug) {
        krugVM.setTrenutniKrug(krug)
        parentFragmentManager.beginTransaction().setReorderingAllowed(true).replace(R.id.main,CircleFragment()).addToBackStack(null).commit()
    }

    override fun finishConfirmed(finish: Boolean, rbr: Int) {
        krugVM.setTrenutniKrug(krugVM.radniKrug.value!!)
        val isValid : Pair<Boolean,List<Int>> = krugVM.isKrugValid()
        if (!isValid.first){
            showErrToast(context,"Stabla broj ${isValid.second.joinToString(",") } su invalidna! ")
            return
        }
        if (finish){
            showSuccessToast(context, "Završen krug broj $rbr.")
            PreferencesUtils.clearWorkingCircleFromPrefs(context)
            krugVM.setDefaultRadniKrug()
        }
    }

    override fun showEditDeleteDialog(krug: Krug) {
        (activity as MainActivity).showEditDeleteDialog(onEdit = {editClicked(krug)}, onDelete = { deleteClicked(krug) })
    }

    private fun editClicked(krug: Krug){
        krugVM.setTrenutniKrug(krug)
        val fragment = AddCircleFragment().apply {
            setIsEdit(true)
        }
        parentFragmentManager.beginTransaction().add(R.id.main,fragment).addToBackStack(null).commit()
    }


    //BRISANJE KRUGA, KAO I BIRSANJE RADNOG KRUGA AKO JE IZBRISANI KRUG RADNI KRUG U TOM MOMENTU
    private fun deleteClicked(krug: Krug){
        (activity as MainActivity).showCircleDeleteConfirmationDialog(krug.brKruga,
            onDelete = {
                krugVM.setTrenutniKrug(krug)
                if (krugVM.trenutniKrug.value?.id == krugVM.radniKrug.value?.id){
                    PreferencesUtils.clearWorkingCircleFromPrefs(context)
                    krugVM.setDefaultRadniKrug()
                }
                val krugovi = dokumentVM.krugovi.value
                krugovi?.remove(krug)
                dokumentVM.setTrenutniKrugovi(krugovi!!)
                krugVM.deleteKrug()
            })
    }

}