package com.example.woodometer.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.replace
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.woodometer.R
import com.example.woodometer.adapters.DocumentsAdapter
import com.example.woodometer.interfaces.DocumentsListener
import com.example.woodometer.model.Dokument
import com.example.woodometer.utils.PreferencesUtils
import com.example.woodometer.viewmodels.DokumentViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DocumentsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DocumentsFragment : Fragment(), DocumentsListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var documentsRecylerView : RecyclerView
    private lateinit var  adapter : DocumentsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    private lateinit var dokumentiViewModel : DokumentViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_documents, container, false)

        dokumentiViewModel = ViewModelProvider(requireActivity())[DokumentViewModel::class.java]
        val backButton = view.findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        documentsRecylerView = view.findViewById(R.id.docsRecyclerView)
        adapter = DocumentsAdapter(dokumentiViewModel.dokumenti.value!!,this)
        documentsRecylerView.adapter = adapter
        documentsRecylerView.layoutManager = LinearLayoutManager(context)
        dokumentiViewModel.dokumenti.observe(viewLifecycleOwner) { dokumenti ->
            adapter.updateData(dokumenti)
        }


        return view;
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DocumentsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DocumentsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun docClicked(dokument: Dokument) {
        dokumentiViewModel.setTrenuntniDokument(dokument)
        parentFragmentManager.beginTransaction().replace(R.id.main,StartMeasuringFragment()).addToBackStack(null).commit()
    }
}