package com.example.woodometer.activities

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.woodometer.R
import com.example.woodometer.fragments.HomeScreenFragment
import com.example.woodometer.interfaces.CircleListener
import com.example.woodometer.interfaces.TreeListener
import com.example.woodometer.services.FloatingService
import com.example.woodometer.utils.GlobalUtils.lastDokument
import com.example.woodometer.utils.GlobalUtils.lastKrug
import com.example.woodometer.utils.NotificationsUtils
import com.example.woodometer.utils.PreferencesUtils
import com.example.woodometer.viewmodels.DokumentViewModel
import com.example.woodometer.viewmodels.KrugViewModel
import kotlinx.coroutines.launch
import java.util.UUID


class MainActivity : AppCompatActivity() {

    private var stopService : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val mainView = findViewById<View?>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, 1234)
            return
        }

        val krugVM = ViewModelProvider(this)[KrugViewModel::class.java]
        val id: String? = PreferencesUtils.getWorkingCircleFromPrefs(this)
        if (!id.isNullOrEmpty()) {
            krugVM.setRadniKrug(UUID.fromString(id))
        }

        val dokumentVM = ViewModelProvider(this)[DokumentViewModel::class.java]
        lifecycleScope.launch {
            dokumentVM.refreshData()
            lastDokument = null
            if (krugVM.radniKrug.value != null) {
                krugVM.setTrenutniKrug(krugVM.radniKrug.value!!)
                lastKrug = krugVM.trenutniKrug.value?.id
                krugVM.setStablaKruga()
            }
        }

        supportFragmentManager.beginTransaction().replace(R.id.main, HomeScreenFragment()).commit()
    }

    override fun onResume() {
        super.onResume()
        stopService(Intent(this, FloatingService::class.java))
    }

    override fun onPause() {
        super.onPause()
        startService(Intent(this, FloatingService::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, FloatingService::class.java))
    }


    private fun startFloatingService() {
        val intent = Intent(this, FloatingService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent) // Required for Android 8.0+
        } else {
            startService(intent)
        }

    }

    override fun attachBaseContext(newBase: Context) {
        if (isTrimbleDevice()) {
            val resources = newBase.resources
            val config = Configuration(resources.configuration)
            config.densityDpi = (resources.displayMetrics.densityDpi * 0.85f).toInt()
            val context = newBase.createConfigurationContext(config)
            super.attachBaseContext(context)
        } else {
            super.attachBaseContext(newBase)
        }
    }

    private fun isTrimbleDevice(): Boolean {
        val manufacturer = Build.MANUFACTURER?.trim()?.lowercase() ?: ""
        val model = Build.MODEL?.trim()?.lowercase() ?: ""
        return manufacturer.contains("trimble") || model.contains("tdc") || model.contains("trimble")
    }


    fun showDeleteConfirmationDialog(listener : TreeListener,rbr : Int){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Da li ste sigurni da želite da obrišete stablo $rbr?")
            .setPositiveButton("Yes") { dialog, id ->
                NotificationsUtils.showSuccessToast(this,"Stablo $rbr obrisano.")
                listener.deleteConfirmed(true,rbr)
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.dismiss()  // Just close the dialog if user cancels
                listener.deleteConfirmed(false,rbr)
            }

        val dialog = builder.create()
        dialog.show()
    }

    fun showEndCircleDialog(
        rbr: Int,
        message: String,
        onResult: (rbr: Int) -> Unit
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
            .setPositiveButton("Yes") { _, _ ->
                onResult(rbr)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }
    fun showEditDeleteDialog(onEdit: () -> Unit, onDelete: () -> Unit) {
        val options = arrayOf("Ažuriranje", "Brisanje")

        val builder = AlertDialog.Builder(this)
            .setTitle("Izaberite akciju")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> {
                        onEdit()
                    }
                    1 -> {
                        onDelete()
                    }
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss() // Dismiss dialog when "Cancel" is clicked
            }

        builder.create().show()
    }

    fun showCircleDeleteConfirmationDialog(rbr : Int,onDelete: () -> Unit){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Da li ste sigurni da želite da obrišete krug $rbr?")
            .setPositiveButton("Yes") { dialog, id ->
                NotificationsUtils.showSuccessToast(this,"Krug $rbr obrisan.")
                onDelete()
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }



}