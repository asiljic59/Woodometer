package com.example.woodometer.utils

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import com.example.woodometer.R
import com.example.woodometer.fragments.KeyboardFragment
import com.example.woodometer.interfaces.KeyboardListener
import com.example.woodometer.model.enumerations.KeyboardField
import com.google.android.material.textfield.TextInputEditText

object KeyboardUtils {
    var currentInputField: TextInputEditText? = null

    //U ovoj funkciji takodje resavamo problem klika na Edit Text to jest da ne registruje normalan klik na edit text
    //Vec omogucavamo da kada se klikne tacno na edit text bude otvoren keyboard!


    @SuppressLint("ClickableViewAccessibility")
    fun setupInputKeyboardClickListeners(
        inputFields: HashMap<ConstraintLayout,Triple<TextInputEditText,String, KeyboardField>>,
        parentFragmentManager: FragmentManager,
        listener: KeyboardListener) {
        inputFields.forEach { (layout, triple) ->
            val inputView = triple.first
            var clicked = false // flag to prevent double trigger
            layout.setOnClickListener {
                if (!clicked) {
                    clicked = true
                    currentInputField = triple.first
                    openKeyboard(layout,listener,parentFragmentManager,inputFields)

                    // reset the flag after a short delay to allow next click
                    layout.postDelayed({ clicked = false }, 200)
                }
            }
            inputView.setOnTouchListener { _, _ ->
                layout.performClick() // This still triggers the click listener above
                true
            }

        }
    }
    fun openKeyboard(layout: ConstraintLayout,listener: KeyboardListener,
                     parentFragmentManager : FragmentManager,
                     keyboardTextViews: HashMap<ConstraintLayout,Triple<TextInputEditText,String, KeyboardField>> ){
        val keyboardFragment = KeyboardFragment().apply {
            setKeyboardListener(listener)
            keyboardTextViews[layout]?.let { setTitle(it.second) }
            keyboardTextViews[layout]?.third?.let { setField(it) }
        }
        parentFragmentManager.beginTransaction()
            .add(R.id.main, keyboardFragment)
            .addToBackStack(null)
            .commit()
    }
}