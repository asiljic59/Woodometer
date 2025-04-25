package com.example.woodometer.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.NumberPicker
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.example.woodometer.R
import com.example.woodometer.databinding.FragmentBiodiversityBinding
import com.example.woodometer.databinding.FragmentCircleBinding
import com.example.woodometer.viewmodels.KrugViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private val BIODIVERZITET = listOf(
    R.id.dubecaConstraintLayout to R.id.dubecaPicker,
    R.id.osteceniVrhConstraintLayout to R.id.osteceniVrhPicker,
    R.id.ostecenaKoraConstraintLayout to R.id.ostecenaKoraPicker,
    R.id.gnezdaConstraintLayout to R.id.gnezdaPicker,
    R.id.supljineConstraintLayout to R.id.supljinePicker,
    R.id.lisajeviConstraintLayout to R.id.lisajeviPicker,
    R.id.mahovineConstraintLayout to R.id.mahovinePicker,
    R.id.gljiveConstraintLayout to R.id.gljivePicker,
    R.id.izuzetnaDimenzijaConstraintLayout to R.id.izuzetnaDimenzijaPicker,
    R.id.velikaUsamljenaConstraintLayout to R.id.velikaUsamljenaPicker
)


/**
 * A simple [Fragment] subclass.
 * Use the [BiodiversityFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BiodiversityFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentBiodiversityBinding? = null
    private val binding get() = _binding!!
    private lateinit var krugViewModel: KrugViewModel


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
       _binding = FragmentBiodiversityBinding.inflate(inflater, container, false)

        krugViewModel = ViewModelProvider(requireActivity())[KrugViewModel::class.java]
        _binding!!.lifecycleOwner = viewLifecycleOwner
        binding.krugVM = krugViewModel


        setupLayouts(binding.root)
        binding.backButton.setOnClickListener{
            parentFragmentManager.popBackStack()
            krugViewModel.updateBiodiverzitet()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        krugViewModel.getBiodiverzitetByKrug()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BiodiversityFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BiodiversityFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun setupLayouts(view: View){
        BIODIVERZITET.forEach { (layout,picker) ->
            val numberPicker = view.findViewById<NumberPicker>(picker)
            numberPicker.minValue = 0
            numberPicker.maxValue = 10
            numberPicker.value = 0
            numberPicker.setBackgroundResource(R.drawable.number_picker_bg)
        }
    }

}