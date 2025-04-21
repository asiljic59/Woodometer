package com.example.woodometer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.woodometer.R
import com.example.woodometer.interfaces.KeyboardListener
import com.example.woodometer.model.enumerations.KeyboardField
import com.example.woodometer.utils.GlobalUtils
import com.example.woodometer.viewmodels.KrugViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



/**
 * A simple [Fragment] subclass.
 * Use the [KeyboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class KeyboardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var text: String? = null
    private var diameter: Boolean? = null

    private var listener: KeyboardListener? = null

    private var title : String? = null

    private var field : KeyboardField? = null

    private lateinit var krugViewModel: KrugViewModel

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
            listener?.onClearPressed()
            currentInput.clear()
            updateDisplay()
        }

        // Save button
        view.findViewById<Button>(R.id.btnSave).setOnClickListener {
            saveInput()
            listener?.onEnterPressed(currentInput.toString());
        }

        closeButton.setOnClickListener{
            currentInput.clear()
            listener?.onClearPressed()
            parentFragmentManager.popBackStack()
        }

        return view
    }

    private fun setUnitTextView(view: View) {
        val measurementUnitTextView = view.findViewById<TextView>(R.id.measurementUnitTextView)
        if (field == KeyboardField.PRECNIK){measurementUnitTextView.text = "cm"}
        else if (field in listOf(KeyboardField.VISINA,KeyboardField.DUZINA_DEBLA)){measurementUnitTextView.text = "dm"}
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

    private fun saveInput() {
        if (isValid()){
            val measurement = currentInput.ifEmpty { "0" }
            parentFragmentManager.popBackStack()
        }
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
        if (field == KeyboardField.PRECNIK){
            return isPrecnikValid()
        }else if (field == KeyboardField.AZIMUT){
            return isAzimutValid()
        }else if (field in listOf(KeyboardField.SOCIJALNI_STATUS, KeyboardField.TEHNICKA_KLASA,KeyboardField.STEPEN_SUSENJA)){
            return isStateValid()
        }else if(field == KeyboardField.RAZDALJINA){
            return isDistanceValid()
        }else if(field == KeyboardField.VISINA || field == KeyboardField.DUZINA_DEBLA){
            return isHeightValid()
        }else if(field == KeyboardField.POLOZAJ_STABLA){
            return isPositionValid()
        }

        return true
    }

    private fun isPrecnikValid(): Boolean {
        if (!isDecimalValid()) {return false}
        val number = currentInput.toString().toDouble()
        if(number < 30 && krugViewModel.trenutnoStablo.value?.razdaljina!! > GlobalUtils.DISTANCE_UPPER_LIMIT_UNDER_30 ){
            Toast.makeText(context,"Za prečnik veći od 30cm razdaljina mora biti preko ${GlobalUtils.DISTANCE_UPPER_LIMIT_UNDER_30}",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun isPositionValid(): Boolean {
        try{
            val number = currentInput.toString().toInt()
            if (number<1 || number >4){throw NumberFormatException()}
        }catch (e: NumberFormatException){
            Toast.makeText(context,"Pozicija stabla mora biti brojevna vrednost u opsegu 1-4!",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun isHeightValid(): Boolean {
        try{
            val number = currentInput.toString().toInt()
            currentInput = StringBuilder(currentInput.toString().toInt().toString())
        }catch (e : NumberFormatException){
            Toast.makeText(context,"$title mora biti brojevna vrednost izrazena u decimetrima!",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun isDistanceValid(): Boolean {
        if (!isDecimalValid()) {return false}
        val number = currentInput.toString().toDouble()
        if(number > GlobalUtils.DISTANCE_UPPER_LIMIT_UNDER_30 && krugViewModel.trenutnoStablo.value?.precnik!! < 30){
            Toast.makeText(context,"Za prečnik veći od 30cm razdaljina mora biti preko ${GlobalUtils.DISTANCE_UPPER_LIMIT_UNDER_30}",Toast.LENGTH_SHORT).show()
            return false
        }else if (number >= GlobalUtils.DISTANCE_UPPER_LIMIT){
            Toast.makeText(context,"Razdaljina ne sme biti veća od ${GlobalUtils.DISTANCE_UPPER_LIMIT}",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun isStateValid(): Boolean {
        try{
            val number = currentInput.toString().toInt()
            if (number > 3 || number <0){throw NumberFormatException()}
            currentInput = StringBuilder(currentInput.toString().toInt().toString())
        }catch (e : NumberFormatException){
            Toast.makeText(context,"${title} mora biti vrednost u opsegu 0-3!",Toast.LENGTH_SHORT).show()
            return false
        }
        return true

    }

    //Provera azimuta (da li je brojevna vrednost od 0 do 360)
    fun isAzimutValid() : Boolean{
        try{
            val number = currentInput.toString().toInt()
            if (number > 360) {throw NumberFormatException()}
            currentInput = StringBuilder(currentInput.toString().toInt().toString())
        }catch (e : NumberFormatException){
            Toast.makeText(context,"Azimut mora biti broj u opsegu 0-360!",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    //Provera precnika (da li je brojevna vrednost)
    fun isDecimalValid() : Boolean{
        try{
            val number = currentInput.toString().toDouble()
            if (hasMoreThanTwoDecimal(number)){currentInput= StringBuilder(trimToOneDecimal(number).toString())}
            currentInput = StringBuilder(currentInput.toString().toDouble().toString())
        }catch (e : NumberFormatException){
            Toast.makeText(context,"$title mora biti izražen na najviše dve decimale!",Toast.LENGTH_SHORT).show()
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
}