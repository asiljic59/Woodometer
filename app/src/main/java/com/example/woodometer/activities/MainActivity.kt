package com.example.woodometer.activities

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.woodometer.R
import com.example.woodometer.fragments.HomeScreenFragment
import com.example.woodometer.interfaces.CircleListener
import com.example.woodometer.interfaces.TreeListener
import com.example.woodometer.utils.NotificationsUtils
import com.example.woodometer.utils.PreferencesUtils
import com.example.woodometer.viewmodels.DokumentViewModel
import com.example.woodometer.viewmodels.KrugViewModel
import java.util.UUID


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

        adjustScreen()
        val krugVM  = ViewModelProvider(this)[KrugViewModel::class.java]

        //trazenje radnog kruga, ako postoji takav
        val id :  String? = PreferencesUtils.getWorkingCircleFromPrefs(this)
        if (id != ""){
            krugVM.setRadniKrug(UUID.fromString(id))
        }

        //skupljanje podataka o dokumentima pri pokretanju!
        val dokumentVM = ViewModelProvider(this)[DokumentViewModel::class.java]
        dokumentVM.refreshData()

        supportFragmentManager.beginTransaction().replace(R.id.main, HomeScreenFragment()).commit()


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


    private fun adjustScreen() {
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

    fun showEndCircleDialog(listener: CircleListener,rbr : Int,message: String){
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
            .setPositiveButton("Yes") { dialog, id ->
                listener.finishConfirmed(true,rbr)
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.dismiss()  // Just close the dialog if user cancels
                listener.finishConfirmed(false,rbr)
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
                NotificationsUtils.showSuccessToast(this,"Stablo $rbr obrisano.")
                onDelete()
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

    fun vibratePhone(){
        val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(200)
        }
    }


}