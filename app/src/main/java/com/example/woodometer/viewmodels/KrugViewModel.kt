package com.example.woodometer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.woodometer.model.Krug
import com.example.woodometer.model.MrtvoStablo
import com.example.woodometer.model.Stablo

class KrugViewModel : ViewModel() {
    private val _trenutniKrug = MutableLiveData<Krug>()
    private val _trenutnaMrtvaStabla = MutableLiveData<List<MrtvoStablo>>().apply {
        value = emptyList()
    }
    private val _trenutnoStablo = MutableLiveData<Stablo>().apply {
        value = Stablo()
    }

    val trenutniKrug: LiveData<Krug> get() = _trenutniKrug
    val trenutnaMrtvaStabla: LiveData<List<MrtvoStablo>> get() = _trenutnaMrtvaStabla
    val trenutnoStablo: LiveData<Stablo> get() = _trenutnoStablo

    fun setTrenutnaMrtvaStabla(stabla: List<MrtvoStablo>) {
        _trenutnaMrtvaStabla.value = stabla
    }


}
