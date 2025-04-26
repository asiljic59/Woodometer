package com.example.woodometer.utils

import android.content.Context
import com.example.woodometer.model.Krug
import java.util.UUID

object PreferencesUtils {

    fun getListFromPrefs(fileName : String, context: Context?): MutableList<String> {
        val sharedPrefs = context?.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val joined = sharedPrefs?.getString(fileName, "") ?: ""

        // If the joined string is not empty, split it and return a mutable list, else return an empty mutable list
        return if (joined.isNotEmpty()) {
            joined.split(",").toMutableList()  // Convert the result of split() to a mutable list
        } else {
            mutableListOf()  // Return an empty mutable list if the joined string is empty
        }
    }
    fun saveListToPrefs(context: Context?, list: MutableList<String>,fileName: String) {
        val sharedPrefs = context?.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val joined = list.joinToString(",") // e.g. "123,456,789"
        sharedPrefs?.edit()?.putString(fileName, joined)?.apply()
    }

    fun getWorkingCircleFromPrefs(context: Context?) : String?{
        val sharedPrefs = context?.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        return sharedPrefs?.getString("radni_krug_id", "")
    }

    fun saveWorkingCircleToPrefs(context: Context?,id : UUID){
        val sharedPrefs = context?.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        sharedPrefs?.edit()?.putString("radni_krug_id",id.toString())?.apply()
    }

    fun clearWorkingCircleFromPrefs(context: Context?){
        val sharedPrefs = context?.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        sharedPrefs?.edit()?.putString("radni_krug_id","")?.apply()
    }
}