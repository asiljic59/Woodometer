package com.example.woodometer.viewmodels

import androidx.lifecycle.MutableLiveData
import com.example.woodometer.model.Krug

class KrugViewModel {
    private lateinit var trenutniKrug : MutableLiveData<Krug>

    fun setTrenutniKrug(krug : Krug){
        trenutniKrug.value = krug
    }

    fun getTrenutniKrug (): Krug? {
        return trenutniKrug.value
    }


}
