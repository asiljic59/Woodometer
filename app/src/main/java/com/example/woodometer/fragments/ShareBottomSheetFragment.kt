package com.example.woodometer.fragments

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.woodometer.R
import com.example.woodometer.adapters.ShareAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ShareBottomSheetFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ShareBottomSheetFragment : BottomSheetDialogFragment() {

    private var uris: ArrayList<Uri> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            uris = it.getParcelableArrayList(Intent.EXTRA_STREAM) ?: arrayListOf()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_share_bottom_sheet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val pm = requireContext().packageManager

        val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "*/*"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val resolved = pm.queryIntentActivities(shareIntent, 0)

        val adapter = ShareAdapter(resolved) { resolveInfo ->
            val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                component = ComponentName(
                    resolveInfo.activityInfo.packageName,
                    resolveInfo.activityInfo.name
                )
                type = "*/*"
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(intent)
            dismiss()
        }

        view.findViewById<RecyclerView>(R.id.shareList).apply {
            layoutManager = GridLayoutManager(context,4)
            this.adapter = adapter
        }
    }

    companion object {
        fun newInstance(uris: ArrayList<Uri>) = ShareBottomSheetFragment().apply {
            arguments = Bundle().apply {
                putParcelableArrayList(Intent.EXTRA_STREAM, uris)
            }
        }
    }
}

