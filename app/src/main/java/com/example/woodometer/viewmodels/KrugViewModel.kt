package com.example.woodometer.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.woodometer.model.Biodiverzitet
import com.example.woodometer.model.Krug
import com.example.woodometer.model.MrtvoStablo
import com.example.woodometer.model.Stablo

class KrugViewModel : ViewModel() {
    private val _trenutniKrug = MutableLiveData<Krug>()
    private val _trenutnaMrtvaStabla = MutableLiveData<List<MrtvoStablo>>(emptyList())
    private val _trenutnoStablo = MutableLiveData<Stablo>().apply {
        value = Stablo(rbr = 1)
    }
    private val _trenutnoMrtvoStablo = MutableLiveData<MrtvoStablo>().apply {
        value = MrtvoStablo(rbr = 1)
    }
    private val _biodiverzitet = MutableLiveData<Biodiverzitet>().apply {
        value = Biodiverzitet()
    }

    val trenutniKrug: LiveData<Krug> get() = _trenutniKrug
    val trenutnaMrtvaStabla: LiveData<List<MrtvoStablo>> = _trenutnaMrtvaStabla
    val trenutnoStablo: LiveData<Stablo> get() = _trenutnoStablo
    val mrtvoStablo : LiveData<MrtvoStablo> get() = _trenutnoMrtvoStablo
    val biodiverzitet : LiveData<Biodiverzitet> get() = _biodiverzitet

    fun setTrenutnaMrtvaStabla(stabla: MutableList<MrtvoStablo>) {
        _trenutnaMrtvaStabla.value = stabla
    }
    //SETOVANJE NOVOG MRTVOG STABLA KOJE CE DA BUDE AZURIRANO
    fun setMrtvoStabloToEdit(item: MrtvoStablo) {
        _trenutnoMrtvoStablo.value = item.copy() // Important to copy to avoid reference issues
    }
    //DODAVANJE NOVOG MRTVOG STABLA
    fun addMrtvoStablo() {
        mrtvoStablo.value?.let { newStablo ->
            val updatedList = _trenutnaMrtvaStabla.value?.toMutableList() ?: mutableListOf()
            // Racunamo novi redni broj tako sto uzimamo maximum iz liste +1
            val nextRbr = updatedList.size + 1  // If list is empty, start from 1
            newStablo.rbr = nextRbr
            updatedList.add(newStablo)
            _trenutnaMrtvaStabla.value = updatedList
            // Update the _trenutnoMrtvoStablo LiveData
            _trenutnoMrtvoStablo.value = MrtvoStablo(rbr = nextRbr)
        }
    }
    //BRISANJE MRTVOG STABLA
    fun deleteMrtvoStablo(rbr : Int){
        val updatedList = _trenutnaMrtvaStabla.value?.toMutableList() ?: mutableListOf()
        updatedList.removeAll{it.rbr == rbr}
        _trenutnaMrtvaStabla.value = updatedList
    }

    fun editMrtvoStablo() {
        val updatedList = _trenutnaMrtvaStabla.value?.toMutableList() ?: mutableListOf()
        updatedList.removeAll{it.rbr == mrtvoStablo.value?.rbr}
        mrtvoStablo.value?.let { updatedList.add(it) }
        _trenutnaMrtvaStabla.value = updatedList
        val nextRbr = updatedList.size + 1
        _trenutnoMrtvoStablo.value = MrtvoStablo(rbr = nextRbr )
    }


}
