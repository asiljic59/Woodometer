package com.example.woodometer.activities

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Message
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.woodometer.R
import com.example.woodometer.fragments.CircleFragment
import com.example.woodometer.fragments.HomeScreenFragment
import com.example.woodometer.fragments.KeyboardFragment
import com.example.woodometer.interfaces.CircleListener
import com.example.woodometer.interfaces.TreeListener
import com.example.woodometer.utils.NotificationsUtils
import com.example.woodometer.utils.PreferencesUtils
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

        val krugVM  = ViewModelProvider(this)[KrugViewModel::class.java]

        //trazenje radnog kruga, ako postoji takav
        val id :  String? = PreferencesUtils.getWorkingCircleFromPrefs(this)
        if (id != ""){
            krugVM.setRadniKrug(UUID.fromString(id))
        }

        supportFragmentManager.beginTransaction().replace(R.id.main, HomeScreenFragment()).commit()


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


}