// TextInputBindingAdapters.kt
package com.example.woodometer.databinding

import android.widget.NumberPicker
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.textfield.TextInputEditText

object TextInputBindingAdapters {


    @InverseBindingAdapter(attribute = "floatText")
    @JvmStatic
    fun getFloatText(view: TextInputEditText): Float {
        return view.text?.toString()?.toFloatOrNull() ?: 0f
    }


    @BindingAdapter("floatText")
    @JvmStatic
    fun setFloatText(view: TextInputEditText, value: Float?) {
        if (!view.isFocusable && !view.isFocusableInTouchMode) {
            val newText = value?.toString() ?: ""
            if (view.text?.toString() != newText) {
                view.setText(newText)
            }
        }
    }

    @BindingAdapter("intText")
    @JvmStatic
    fun setIntText(view: TextInputEditText, value: Int?) {
        if (!view.isFocusable && !view.isFocusableInTouchMode) {
            val newText = value?.toString() ?: ""
            if (view.text?.toString() != newText) {
                view.setText(newText)
            }
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

    @BindingAdapter("numberPickerValue")
    @JvmStatic
    fun setNumberPickerValue(numberPicker: NumberPicker, value: Int) {
        if (numberPicker.value != value) {
            numberPicker.value = value
        }
    }

    @InverseBindingAdapter(attribute = "numberPickerValue", event = "numberPickerValueAttrChanged")
    @JvmStatic
    fun getNumberPickerValue(numberPicker: NumberPicker): Int {
        return numberPicker.value
    }

    @BindingAdapter("numberPickerValueAttrChanged")
    @JvmStatic
    fun setNumberPickerValueChangeListener(
        numberPicker: NumberPicker,
        listener: InverseBindingListener?
    ) {
        numberPicker.setOnValueChangedListener { _, _, _ ->
            listener?.onChange()
        }
    }

    @BindingAdapter("minValue")
    @JvmStatic
    fun setNumberPickerMinValue(numberPicker: NumberPicker, minValue: Int) {
        numberPicker.minValue = minValue
    }

    @BindingAdapter("maxValue")
    @JvmStatic
    fun setNumberPickerMaxValue(numberPicker: NumberPicker, maxValue: Int) {
        numberPicker.maxValue = maxValue
    }

    @BindingAdapter("numberPickerRange")
    @JvmStatic
    fun setNumberPickerRange(numberPicker: NumberPicker, range: Pair<Int, Int>?) {
        range?.let {
            numberPicker.minValue = it.first
            numberPicker.maxValue = it.second
        }
    }

}