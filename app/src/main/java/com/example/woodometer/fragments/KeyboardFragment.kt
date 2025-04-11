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
import com.example.woodometer.R
import com.example.woodometer.interfaces.KeyboardListener

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        // Inflate the layout for this fragment
        val view : View = inflater.inflate(R.layout.fragment_keyboard, container, false)

        tvInput = view.findViewById<TextView>(R.id.tvInput)
        var textViewText = view.findViewById<TextView>(R.id.textViewText)
        val closeButton = view.findViewById<ImageButton>(R.id.closeKeyboardButton)

        

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
            parentFragmentManager.popBackStack()
        }


        return view
    }

    private fun updateDisplay() {
        tvInput.text = if (currentInput.isEmpty()) "00" else "${currentInput}"
    }

    private fun saveInput() {
        try{
            val number = currentInput.toString().toInt()
        }catch (e : NumberFormatException){
            Toast.makeText(context,"Odeljenje mora biti broj!",Toast.LENGTH_SHORT).show()
            return
        }

        currentInput = StringBuilder(currentInput.toString().toInt().toString())
        val measurement = currentInput.ifEmpty { "0" }
        Toast.makeText(context, "Sacuvano: ${measurement}", Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
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
}