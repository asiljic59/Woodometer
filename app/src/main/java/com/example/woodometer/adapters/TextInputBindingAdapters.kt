// TextInputBindingAdapters.kt
package com.example.woodometer.databinding

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import com.google.android.material.textfield.TextInputEditText

object TextInputBindingAdapters {

    @BindingAdapter("floatText")
    @JvmStatic
    fun setFloatText(view: TextInputEditText, value: Float?) {
        val newText = value?.toString() ?: ""
        if (view.text?.toString() != newText) {
            view.setText(newText)
        }
    }

    @InverseBindingAdapter(attribute = "floatText")
    @JvmStatic
    fun getFloatText(view: TextInputEditText): Float {
        return view.text?.toString()?.toFloatOrNull() ?: 0f
    }


    @BindingAdapter("intText")
    @JvmStatic
    fun setIntText(view: TextInputEditText, value: Int?) {
        val newText = value?.toString() ?: ""
        if (view.text?.toString() != newText) {
            view.setText(newText)
        }
    }

    @InverseBindingAdapter(attribute = "intText")
    @JvmStatic
    fun getIntText(view: TextInputEditText): Int {
        return view.text?.toString()?.toIntOrNull() ?: 0
    }

    @BindingAdapter("floatTextAttrChanged")
    @JvmStatic
    fun setFloatTextListener(
        view: TextInputEditText,
        listener: androidx.databinding.InverseBindingListener?
    ) {
        if (listener != null) {
            view.addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    listener.onChange()
                }
                override fun afterTextChanged(s: android.text.Editable?) {}
            })
        }
    }

    @BindingAdapter("intTextAttrChanged")
    @JvmStatic
    fun setIntTextListener(
        view: TextInputEditText,
        listener: androidx.databinding.InverseBindingListener?
    ) {
        if (listener != null) {
            view.addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    listener.onChange()
                }
                override fun afterTextChanged(s: android.text.Editable?) {}
            })
        }
    }

}