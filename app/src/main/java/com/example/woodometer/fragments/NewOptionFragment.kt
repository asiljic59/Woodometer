package com.example.woodometer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.woodometer.R
import com.example.woodometer.interfaces.AddOptionListener
import com.example.woodometer.model.enumerations.ListOptionsField

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NewOptionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewOptionFragment : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var field : ListOptionsField? = null

    fun setField(field : ListOptionsField?){
        this.field = field;
    }

    private var listener : AddOptionListener? = null

    fun setListener(listener: AddOptionListener){
        this.listener = listener
    }


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
        val view =  inflater.inflate(R.layout.fragment_new_option, container, false)

        val okButton = view.findViewById<Button>(R.id.okButton);
        val editText = view.findViewById<EditText>(R.id.editText)
        val title = view.findViewById<TextView>(R.id.titleTextView)

        okButton.setOnClickListener{
            if (isValid(editText)){
                listener?.addOption(editText.text.toString())
                dismiss()
            }
        }

        title.text = field.toString()

        return view;
    }

    private fun isValid(editText : EditText) : Boolean {
        //Da li je gazdinska i ako da da li ima 4 cifre
        if (field == ListOptionsField.GAZDINSKA_JED && editText.text.length != 4) {
            try{
                val number = editText.text.toString().toInt()
            }catch(e : NumberFormatException){
                Toast.makeText(context,"Gazdinska jedinica mora biti broj!", Toast.LENGTH_SHORT).show()
                return false
            }
            Toast.makeText(context, "Gazdinska jedinica mora imati tačno 4 cifre!", Toast.LENGTH_SHORT).show()
            return false
        } else {

        }
        return true
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NewUnitFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NewOptionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}