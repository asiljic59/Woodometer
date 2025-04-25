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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.woodometer.R
import com.example.woodometer.adapters.CircleAdapter
import com.example.woodometer.adapters.TreesAdapter
import com.example.woodometer.databinding.FragmentCircleBinding
import com.example.woodometer.databinding.FragmentStartMeasuringBinding
import com.example.woodometer.interfaces.AddOptionListener
import com.example.woodometer.interfaces.CircleListener
import com.example.woodometer.interfaces.KeyboardListener
import com.example.woodometer.model.Dokument
import com.example.woodometer.model.Krug
import com.example.woodometer.model.enumerations.KeyboardField
import com.example.woodometer.model.enumerations.ListOptionsField
import com.example.woodometer.utils.KeyboardUtils
import com.example.woodometer.utils.PreferencesUtils
import com.example.woodometer.utils.PreferencesUtils.getListFromPrefs
import com.example.woodometer.viewmodels.DokumentViewModel
import com.example.woodometer.viewmodels.KrugViewModel
import com.google.android.material.textfield.TextInputEditText
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

        setupCirclesRecyclerView()
        val view : View = binding.root
        val krugButton = view.findViewById<Button>(R.id.krugoviButton)

        krugButton.setOnClickListener{
            parentFragmentManager.beginTransaction().add(R.id.main,AddCircleFragment()).addToBackStack(null).commit()
        }

        val backButton = view.findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        //OVA 3 IZNAD IZLAZE OPCIJE
        keyboardTextViews = hashMapOf(
            view.findViewById<ConstraintLayout>(R.id.odeljenjeLayout) to Triple(view.findViewById(R.id.odeljenjeTextView),"ODELJENJE",KeyboardField.ODELJENJE)
        )
        KeyboardUtils.setupInputKeyboardClickListeners(keyboardTextViews,parentFragmentManager,this)
        setupOptionsClickListener(view)
        //stavljanje imena dokumenta itd itd
        setupMetaData()

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

    private fun setupCirclesRecyclerView(){
        recyclerView = binding.krugoviRecyclerView
        adapter = dokumentVM.krugovi.value?.let { CircleAdapter(it,this) }!!
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
    }

    private fun setupMetaData(){
        val dokument : Dokument = dokumentVM.trenutniDokument.value!!
        "${dokument.gazJedinica}${dokument.brOdeljenja}_${dokument.odsek}_${dokument.korisnik}".also { binding.dokumentNameTextView.text = it }
        binding.dateTimeTextView.text = formatTimestamp(dokument.timestamp)
    }

    fun formatTimestamp(timestamp: Long): String {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            .withZone(ZoneId.systemDefault())

        return formatter.format(Instant.ofEpochMilli(timestamp))
    }


    private fun setupOptionsClickListener(view: View){
        optionsTextViews = hashMapOf(
            view.findViewById<ConstraintLayout>(R.id.gazdinskaLayout) to Triple(view.findViewById(R.id.gazdinskaJedTextView),"gazdinske_jed",ListOptionsField.GAZDINSKA_JED),
            view.findViewById<ConstraintLayout>(R.id.odsekLayout) to Triple(view.findViewById(R.id.odsekTextView),"odseci",ListOptionsField.ODSEK),
            view.findViewById<ConstraintLayout>(R.id.korisnikLayout) to Triple(view.findViewById(R.id.korisnikTextView),"korisnici",ListOptionsField.KORISNIK)
        )

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
}