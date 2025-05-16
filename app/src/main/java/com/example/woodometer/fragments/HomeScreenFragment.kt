package com.example.woodometer.fragments

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.woodometer.R
import com.example.woodometer.adapters.CircleAdapter
import com.example.woodometer.utils.GlobalUtils.rotateMode
import com.example.woodometer.utils.NotificationsUtils.showInformationToast
import com.example.woodometer.utils.PreferencesUtils
import com.example.woodometer.viewmodels.DokumentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeScreenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeScreenFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var dokumentVM :DokumentViewModel

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
        val view : View = inflater.inflate(R.layout.fragment_home_screen, container, false)

        dokumentVM = ViewModelProvider(requireActivity())[DokumentViewModel::class.java]

        val dokumentButton = view.findViewById<Button>(R.id.dokumentButton)
        val startMerenjaButton = view.findViewById<Button>(R.id.startButton)

        startMerenjaButton.setOnClickListener{
            startMerenjaButtonClicked()
        }

        dokumentButton.setOnClickListener{
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main,DocumentsFragment())?.addToBackStack(null)?.commit()

        }
        val rotationButton = view.findViewById<ImageButton>(R.id.rotationButton)

        if (rotateMode){
            rotationButton.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.baseline_screen_rotation_24))
        }else{
            rotationButton.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.baseline_screen_lock_rotation_24))
        }

        rotationButton.setOnClickListener {
            rotationButtonClicked(view)
        }
        return view;
    }

    private fun rotationButtonClicked(view: View) {
        val rotationButton = view.findViewById<ImageButton>(R.id.rotationButton)
        if (rotateMode) {
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            rotationButton.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.baseline_screen_lock_rotation_24))
            showInformationToast(context,"Rotacija zaključana")
        } else{
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            rotationButton.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.baseline_screen_rotation_24))
            showInformationToast(context,"Rotacija otključana")
        }
        rotateMode = !rotateMode
    }


    private fun startMerenjaButtonClicked() {
        val oldID = dokumentVM.trenutniDokument.value?.id!!
        lifecycleScope.launch {
            dokumentVM.getNewest()
            if (dokumentVM.trenutniDokument.value!!.id != oldID){
                dokumentVM.getKrugovi()
            }
            // Create the fragment
            val fragment = StartMeasuringFragment()

            // Replace the fragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.main, fragment)
                .addToBackStack(null)
                .commit()

        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeScreenFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeScreenFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}