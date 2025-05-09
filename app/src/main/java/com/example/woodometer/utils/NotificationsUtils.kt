package com.example.woodometer.utils

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.woodometer.R

object NotificationsUtils {
    fun showErrToast(context: Context?,message : String){
        showToast(context,message,R.drawable.baseline_error_24)
        vibratePhone(context!!)
    }

    fun showWarningToast(context: Context?,message: String){
        showToast(context,message,R.drawable.baseline_warning_24)
    }

    fun showSuccessToast(context: Context?,message: String){
        showToast(context,message,R.drawable.baseline_check_circle_24)
        vibratePhone(context!!)
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

    fun vibratePhone(context: Context){
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    fun playSound(){
        val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 60)
        toneGen.startTone(ToneGenerator.TONE_PROP_ACK, 150)
    }
}