package com.example.woodometer.utils

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.woodometer.R

object NotificationsUtils {
    fun showErrToast(context: Context?,message : String){
        showToast(context,message,R.drawable.baseline_error_24)
    }

    fun showWarningToast(context: Context?,message: String){
        showToast(context,message,R.drawable.baseline_warning_24)
    }

    fun showSuccessToast(context: Context?,message: String){
        showToast(context,message,R.drawable.baseline_check_circle_24)
    }


    fun showToast(context: Context?,message : String,iconRes:Int){
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.toast_layout, null)

        val imageView = layout.findViewById<ImageView>(R.id.toastIcon)
        val textView = layout.findViewById<TextView>(R.id.toastText)

        imageView.setImageResource(iconRes)
        textView.text = message

        val toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 100)
        toast.show()
    }
}