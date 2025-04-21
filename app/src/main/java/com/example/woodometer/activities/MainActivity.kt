package com.example.woodometer.activities

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.woodometer.R
import com.example.woodometer.fragments.CircleFragment
import com.example.woodometer.fragments.HomeScreenFragment
import com.example.woodometer.fragments.KeyboardFragment
import com.example.woodometer.interfaces.TreeListener

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

        supportFragmentManager.beginTransaction().replace(R.id.main, HomeScreenFragment()).commit()


    }

    fun showDeleteConfirmationDialog(listener : TreeListener,rbr : Int){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Da li ste sigurni da želite da obrišete mrtvo stablo $rbr?")
            .setPositiveButton("Yes") { dialog, id ->
                Toast.makeText(this, "Mrtvo stablo $rbr obrisano.", Toast.LENGTH_SHORT).show()
                listener.deleteConfirmed(true,rbr)
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.dismiss()  // Just close the dialog if user cancels
                listener.deleteConfirmed(false,rbr)
            }

        val dialog = builder.create()
        dialog.show()
    }


}