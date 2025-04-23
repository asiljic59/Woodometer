package com.example.woodometer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.woodometer.R
import com.example.woodometer.activities.MainActivity
import com.example.woodometer.adapters.DeadTreesAdapter
import com.example.woodometer.interfaces.TreeListener
import com.example.woodometer.model.MrtvoStablo
import com.example.woodometer.model.Stablo
import com.example.woodometer.viewmodels.KrugViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DeadTreesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DeadTreesFragment : Fragment(),TreeListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DeadTreesAdapter
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
        val view =  inflater.inflate(R.layout.fragment_dead_trees, container, false)

        krugViewModel = ViewModelProvider(requireActivity())[KrugViewModel::class.java]

        view.findViewById<Button>(R.id.addButton).setOnClickListener{
            val addTreeFragment = AddDeadTreeFragment().apply {
                setAddition(true)
            }
            parentFragmentManager.beginTransaction().add(R.id.main,addTreeFragment).addToBackStack(null).commit()
        }
        recyclerView = view.findViewById(R.id.deadTreesRecyclerView)
        adapter = krugViewModel.trenutnaMrtvaStabla.value?.let { DeadTreesAdapter(it,this@DeadTreesFragment) }!!
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        //pratimo promene u mrtvim stablima!!!
        krugViewModel.trenutnaMrtvaStabla.observe(viewLifecycleOwner) { mrtvaStabla ->
            mrtvaStabla?.let {
                adapter.updateData(mrtvaStabla)
            }
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
         * @return A new instance of fragment DeadTreesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DeadTreesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun changeTree(stablo: Stablo) {
        TODO("Not yet implemented")
    }

    //brisanje mrtvog stabla
    override fun deleteTree(rbr: Int) {
        val activity = requireActivity() as MainActivity
        activity.showDeleteConfirmationDialog(this,rbr)
    }

    //portvrda brisanja iz aktivnosti
    override fun deleteConfirmed(deleted: Boolean,rbr : Int) {
        if (deleted){krugViewModel.deleteMrtvoStablo(rbr)}
    }
    //azuriranje mrtvog stabla
    override fun editTree(item: MrtvoStablo) {
        krugViewModel.setMrtvoStabloToEdit(item)
        val addTreeFragment = AddDeadTreeFragment().apply {
            setAddition(false)
        }
        parentFragmentManager.beginTransaction().add(R.id.main,addTreeFragment).addToBackStack(null).commit()
    }
}