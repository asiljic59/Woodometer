package com.example.woodometer.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.woodometer.R
import com.example.woodometer.adapters.InfoItemAdapter
import com.example.woodometer.interfaces.InformationItemListener
import com.example.woodometer.interfaces.KeyboardListener
import com.example.woodometer.model.enumerations.KeyboardField
import com.example.woodometer.utils.GlobalUtils
import com.example.woodometer.utils.GlobalUtils.DIAMETER_HEIGHT_LIMIT
import com.example.woodometer.utils.GlobalUtils.POLOZAJ_STABLA
import com.example.woodometer.utils.GlobalUtils.SOCIJALNI_STATUSI
import com.example.woodometer.utils.GlobalUtils.STEPENI_SUSENJA
import com.example.woodometer.utils.GlobalUtils.TEHNICKE_KLASE
import com.example.woodometer.utils.NotificationsUtils
import com.example.woodometer.utils.NotificationsUtils.showErrToast
import com.example.woodometer.utils.NotificationsUtils.showWarningToast
import com.example.woodometer.viewmodels.DokumentViewModel
import com.example.woodometer.viewmodels.KrugViewModel
import com.google.android.material.button.MaterialButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



/**
 * A simple [Fragment] subclass.
 * Use the [KeyboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class KeyboardFragment : Fragment(),InformationItemListener {
    // TODO: Rename and change types of parameters
    private var text: String? = null
    private var diameter: Boolean? = null

    private var listener: KeyboardListener? = null

    private var title : String? = null

    private var field : KeyboardField? = null

    private lateinit var krugViewModel: KrugViewModel
    private lateinit var dokumentViewModel: DokumentViewModel

    private lateinit var tvInput: TextView
    private var currentInput = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            text = it.getString(ARG_PARAM1)
            diameter = it.getBoolean(ARG_PARAM2)
        }
    }

    fun setKeyboardListener(listener: KeyboardListener){
        this.listener = listener
    }

    fun setTitle (title : String){
        this.title = title
    }

    fun setField (field: KeyboardField){
        this.field = field
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view : View = inflater.inflate(R.layout.fragment_keyboard, container, false)
        krugViewModel = ViewModelProvider(requireActivity())[KrugViewModel::class.java]
        dokumentViewModel = ViewModelProvider(requireActivity())[DokumentViewModel::class.java]

        tvInput = view.findViewById<TextView>(R.id.tvInput)
        var textViewText = view.findViewById<TextView>(R.id.textViewText)
        val closeButton = view.findViewById<ImageButton>(R.id.closeKeyboardButton)


        textViewText.setText(title)

        //set numbers
        setNumberButtons(view)
        //namesti mernu jedinicu
        setUnitTextView(view)

        // Clear button (X)
        view.findViewById<Button>(R.id.btnX).setOnClickListener {
            currentInput.clear()
            updateDisplay()
        }

        // Save button
        view.findViewById<Button>(R.id.btnSave).setOnClickListener {
            val valid = saveInput()
            if (valid){listener?.onEnterPressed(currentInput.toString());}
        }

        closeButton.setOnClickListener{
            currentInput.clear()
            parentFragmentManager.popBackStack()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dozvoljeneVrednostiButton = view.findViewById<MaterialButton>(R.id.dozvoljeneVrednostiButton)
        if(field in listOf(KeyboardField.UZGOJNA_GRUPA,KeyboardField.STEPEN_SUSENJA,
                           KeyboardField.SOCIJALNI_STATUS,KeyboardField.TEHNICKA_KLASA,
                           KeyboardField.POLOZAJ_STABLA,KeyboardField.PROBNA_DOZNAKA))
        {
            dozvoljeneVrednostiButton.visibility = View.VISIBLE
        }
        dozvoljeneVrednostiButton.setOnClickListener{
            val transaction = parentFragmentManager.beginTransaction()
            transaction.setCustomAnimations(
                R.anim.enter_from_bottom,
                R.anim.exit_to_bottom
            )
            transaction.add(R.id.main, InformationFragment().apply {
                setField(field!!)
                setListener(this@KeyboardFragment)
            })
            transaction.addToBackStack(null)
            transaction.commit()
        }

        if (field == KeyboardField.AZIMUT){
            val compassButton = view.findViewById<MaterialButton>(R.id.compassButton)
            compassButton.visibility = View.VISIBLE
            compassButton.setOnClickListener{
                openCompass()
            }
        }

    }

    private fun openCompass() {
        val context = requireContext()
        val packageName = "menion.android.locus.gis"

        Log.d("MyApp", "Attempting to open Locus GIS")

        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(launchIntent)
            } catch (e: Exception) {
                Log.e("MyApp", "Failed to start Locus GIS", e)
                Toast.makeText(context, "Failed to launch Locus GIS", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Locus GIS is not installed", Toast.LENGTH_SHORT).show()
        }
    }




    private fun setUnitTextView(view: View) {
        val measurementUnitTextView = view.findViewById<TextView>(R.id.measurementUnitTextView)
        if (field in listOf(KeyboardField.PRECNIK,KeyboardField.PRECNIK_MRTVO_STABLO)){measurementUnitTextView.text = "cm"}
        else if (field in listOf(KeyboardField.VISINA,KeyboardField.DUZINA_DEBLA,KeyboardField.DUZINA)){measurementUnitTextView.text = "dm"}
        else if (field in listOf(KeyboardField.RAZDALJINA)) {measurementUnitTextView.text = "m"}
    }

    private fun setNumberButtons(view: View){
        // Set number buttons (0-9)
        listOf(
            R.id.btn0 to "0", R.id.btn1 to "1", R.id.btn2 to "2",
            R.id.btn3 to "3", R.id.btn4 to "4", R.id.btn5 to "5",
            R.id.btn6 to "6", R.id.btn7 to "7", R.id.btn8 to "8",
            R.id.btn9 to "9", R.id.btnDot to "."
        ).forEach { (id, value) ->
            view.findViewById<Button>(id).setOnClickListener {
                currentInput.append(value)
                updateDisplay()
            }
        }
    }

    private fun updateDisplay() {
        tvInput.text = if (currentInput.isEmpty()) "00" else "${currentInput}"
    }

    private fun saveInput() : Boolean {
        if (isValid()){
            parentFragmentManager.popBackStack()
            return true
        }
        return false
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment KeyboardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            KeyboardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    //Provera svih atributa koji se kucaju na tastaturi
    //Uvek cemo znati koji se atribut trenutno kuca jer saljemo keyboard fragmentu!
    private fun isValid() : Boolean{
        try {
            currentInput.toString().toDouble()
        } catch (e: NumberFormatException) {
            NotificationsUtils.showErrToast(requireContext(), "Nije uneta brojevna vrednost!")
            return false
        }
        if (field == KeyboardField.PRECNIK){
            return isPrecnikValid()
        }else if (field == KeyboardField.PRECNIK_MRTVO_STABLO){
            return isPrecnikMrtvoStabloValid()
        }else if (field == KeyboardField.AZIMUT){
            return isAzimutValid()
        }else if(field == KeyboardField.RAZDALJINA){
            return isDistanceValid()
        }else if(field in listOf(KeyboardField.ODELJENJE,KeyboardField.ID,KeyboardField.DUZINA)){
            return isInt()
        }else if (field in listOf(KeyboardField.SOCIJALNI_STATUS, KeyboardField.TEHNICKA_KLASA)){
            return isStatusValid()
        }else if (field == KeyboardField.STEPEN_SUSENJA){
            return isStepenSusenjaValid()
        } else if(field == KeyboardField.POLOZAJ_STABLA){
            return isPozicijaValid()
        }  else if (field == KeyboardField.BR_KRUG){
            return isBrKrugValid()
        } else if (field == KeyboardField.NAGIB){
            return isNagibValid()
        } else if (field == KeyboardField.UZGOJNA_GRUPA){
            return isUzgojnaGrupaValid()
        } else if (field == KeyboardField.PROBNA_DOZNAKA){
            return isProbnaDoznakaValid()
        }else if (field in listOf(KeyboardField.VISINA,KeyboardField.DUZINA_DEBLA)){
            return isVisinaValid()
        }

        return true
    }


    private fun isInt(): Boolean {
        try {
            currentInput.toString().toInt()
        }catch (e : NumberFormatException){
            NotificationsUtils.showErrToast(requireContext(),"Polje $title mora biti ceo broj!")
            return false
        }
        return true
    }

    private fun isStepenSusenjaValid(): Boolean {
        if (!isInt()){return false}
        if (currentInput.toString().toInt() !in 0..3) {
            NotificationsUtils.showErrToast(requireContext(), "$title mora biti između 0 i 3!")
            return false
        }
        return true
    }
    private fun isPozicijaValid(): Boolean {
        if (!isInt()) { return false }
        if (currentInput.toString().toInt() !in 0..4) {
            NotificationsUtils.showErrToast(requireContext(), "$title mora biti između 0 i 4!")
            return false
        }
        return true
    }

    private fun isStatusValid(): Boolean {
        if (!isInt()) { return false }
        if (currentInput.toString().toInt() !in 1..3) {
            NotificationsUtils.showErrToast(requireContext(), "$title mora biti između 1 i 3!")
            return false
        }
        return true
    }

    private fun isUzgojnaGrupaValid(): Boolean {
        if (!isInt()) { return false }
        if (currentInput.toString().toInt() !in 1..6) {
            NotificationsUtils.showErrToast(requireContext(), "$title mora biti između 1 i 6!")
            return false
        }
        return true
    }

    private fun isProbnaDoznakaValid(): Boolean {
        if (!isInt()) { return false }
        if (currentInput.toString().toInt() !in GlobalUtils.PROBNE_DOZNAKE.map { it.first }) {
            NotificationsUtils.showErrToast(requireContext(), "Invalidna $title!")
            return false
        }
        return true
    }


    private fun isBrKrugValid(): Boolean {
        isInt()
        if (currentInput.toString()
            .toInt() in (dokumentViewModel.krugovi.value?.map { it.brKruga } ?: listOf())){
            NotificationsUtils.showErrToast(requireContext(),"Krug broj $currentInput već postoji u dokumentu!")
            return false
        }else{
            return true
        }
    }

    private fun isNagibValid(): Boolean {
        if (!isDecimalValid()) { return false }
        if (currentInput.toString().toDouble() !in 0f..45f) {
            NotificationsUtils.showErrToast(requireContext(), "$title mora biti između 1 i 45!")
            return false
        }
        return true
    }

    private fun isPrecnikValid(): Boolean {
        if (!isDecimalValid()) { return false }
        val number = currentInput.toString().toDouble()
        if (number < GlobalUtils.DIAMETER_LOWER_LIMIT){
            NotificationsUtils.showErrToast(context,"Prečnik ne sme biti manji od 10cm!")
            return false
        }else if (number < DIAMETER_HEIGHT_LIMIT && krugViewModel.trenutnoStablo.value?.razdaljina!! > GlobalUtils.DISTANCE_UPPER_LIMIT_UNDER_30) {
            NotificationsUtils.showErrToast(requireContext(), "Za prečnik manji od 30cm razdaljina mora biti ispod ${GlobalUtils.DISTANCE_UPPER_LIMIT_UNDER_30}")
            return false
        }
        if (number > 100f) {
            showWarningToast(context,"Prečnik stabla premašuje očekivane granice.")
        }
        return true
    }

    //ZA MRTVO STABLO SAMO <10cm
    private fun isPrecnikMrtvoStabloValid() : Boolean {
        if (!isDecimalValid()) {return  false}
        val number = currentInput.toString().toDouble()
        if (number < GlobalUtils.DIAMETER_LOWER_LIMIT){
            showErrToast(context,"Prečnik ne sme biti manji od 10cm!")
            return false
        }
        return true
    }

    private fun isDistanceValid(): Boolean {
        if (!isDecimalValid()) { return false }
        val number = currentInput.toString().toDouble()
        val precnik = krugViewModel.trenutnoStablo.value?.precnik!!
        if (number > GlobalUtils.DISTANCE_UPPER_LIMIT_UNDER_30 && precnik != 0f && precnik <= DIAMETER_HEIGHT_LIMIT) {
            showErrToast(requireContext(), "Za prečnik manji od 30cm razdaljina mora biti ispod ${GlobalUtils.DISTANCE_UPPER_LIMIT_UNDER_30}")
            return false
        } else if (number > GlobalUtils.DISTANCE_UPPER_LIMIT) {
            showErrToast(requireContext(), "Razdaljina ne sme biti veća od ${GlobalUtils.DISTANCE_UPPER_LIMIT}")
            return false
        }
        return true
    }


    fun isAzimutValid(): Boolean {
        try {
            val number = currentInput.toString().toInt()
            if (number > 360) { throw NumberFormatException() }
            currentInput = StringBuilder(currentInput.toString().toInt().toString())
        } catch (e: NumberFormatException) {
            NotificationsUtils.showErrToast(requireContext(), "Azimut mora biti broj u opsegu 0-360!")
            return false
        }
        return true
    }

    fun isVisinaValid() : Boolean{
        if (!isInt()){return false;}
        val krug  = krugViewModel.trenutnoStablo.value
        if (field == KeyboardField.VISINA){
            if(krug?.duzDebla!=0 && currentInput.toString().toInt() < krug?.duzDebla!!){
                NotificationsUtils.showErrToast(context,"Visina ne sme biti manja od dužine debla")
                return false
            }
        }else if (krug?.visina != 0 && field == KeyboardField.DUZINA_DEBLA){
            if (currentInput.toString().toInt() > krug?.visina!!){
                NotificationsUtils.showErrToast(context,"Dužina debla ne sme biti veća od visine")
                return false
            }
        }
        return true
    }

    fun isDecimalValid(): Boolean {
        try {
            val number = currentInput.toString().toDouble()
            if (hasMoreThanTwoDecimal(number)) {
                currentInput = StringBuilder(trimToOneDecimal(number).toString())
            }
            currentInput = StringBuilder(currentInput.toString().toDouble().toString())
        } catch (e: NumberFormatException) {
            NotificationsUtils.showErrToast(requireContext(), "$title mora biti izražen na najviše dve decimale!")
            return false
        }
        return true
    }

    fun hasMoreThanTwoDecimal(number: Double): Boolean {
        val text = number.toString()
        val decimalIndex = text.indexOf('.')
        return decimalIndex != -1 && text.length - decimalIndex > 3
    }

    fun trimToOneDecimal(number: Double): Double {
        return "%.1f".format(number).toDouble()
    }

    override fun informationPicked(number: Int) {
        NotificationsUtils.showInformationToast(context,"$title : $number")
        listener?.onEnterPressed(number.toString())
        parentFragmentManager.popBackStack()
    }
}