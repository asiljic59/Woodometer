package com.example.woodometer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.SQLITE_DATA_TEXT
import com.example.woodometer.Woodometer
import com.example.woodometer.model.Biodiverzitet
import com.example.woodometer.model.Krug
import com.example.woodometer.model.MrtvoStablo
import com.example.woodometer.model.Stablo
import com.example.woodometer.dao.StabloDao
import com.example.woodometer.repository.KrugRepository
import com.example.woodometer.repository.StabloRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class KrugViewModel : ViewModel() {
    private var stabloRepository: StabloRepository = StabloRepository()
    private var krugRepository : KrugRepository = KrugRepository()

    private val _trenutniKrug = MutableLiveData<Krug>().apply {
        value = Krug()
    }

    private val _trenutnoStablo = MutableLiveData<Stablo>().apply {
        value = Stablo ()
    }
    private val _stablaKruga = MutableLiveData<MutableList<Stablo>>().apply {
        value = mutableListOf()
    }


    private val _trenutnaMrtvaStabla = MutableLiveData<List<MrtvoStablo>>(emptyList())

    private val _trenutnoMrtvoStablo = MutableLiveData<MrtvoStablo>().apply {
        value = MrtvoStablo(rbr = 1,krugId = _trenutniKrug.value?.id!!)
    }
    private val _biodiverzitet = MutableLiveData<Biodiverzitet>().apply {
        value = Biodiverzitet(_trenutniKrug.value?.id!!)
    }

    val trenutniKrug: LiveData<Krug> get() = _trenutniKrug
    val trenutnaMrtvaStabla: LiveData<List<MrtvoStablo>> = _trenutnaMrtvaStabla
    val trenutnoStablo: LiveData<Stablo> get() = _trenutnoStablo
    val stablaKruga : LiveData<MutableList<Stablo>> get() = _stablaKruga
    val mrtvoStablo : LiveData<MrtvoStablo> get() = _trenutnoMrtvoStablo
    val biodiverzitet : LiveData<Biodiverzitet> get() = _biodiverzitet


    fun updateTrenutnoStablo() {
        updateStablaKruga()
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                _trenutnoStablo.value?.let { stabloRepository.upsert(it) }
            }
        }
    }


    fun setTrenutnoStablo(stablo: Stablo) {
        _trenutnoStablo.value = stablo
    }

    fun setTrenutnoStabloKrugID() {
        val krugId: UUID = _trenutniKrug.value?.id!!
        if (trenutnoStablo.value?.krugId != krugId) {
            _trenutnoStablo.value?.krugId = krugId
        }
    }

    fun resetStablo() {
        val stablaList = _stablaKruga.value ?: emptyList()
        val rbr = stablaList.maxOfOrNull { it.rbr } ?: 1
        _trenutnoStablo.value = _trenutniKrug.value?.id?.let { Stablo(rbr = rbr+1, krugId = it) }
    }


    fun setTrenutnaMrtvaStabla(stabla: MutableList<MrtvoStablo>) {
        _trenutnaMrtvaStabla.value = stabla
    }
    //SETOVANJE NOVOG MRTVOG STABLA KOJE CE DA BUDE AZURIRANO
    fun setMrtvoStabloToEdit(item: MrtvoStablo) {
        _trenutnoMrtvoStablo.value = item // Important to copy to avoid reference issues
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
            _trenutnoMrtvoStablo.postValue(MrtvoStablo(rbr = nextRbr,krugId = trenutniKrug.value?.id!!))
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
        _trenutnoMrtvoStablo.value = MrtvoStablo(rbr = nextRbr,krugId = _trenutniKrug.value?.id!! )
    }

    fun resetKrug(dokumentId : UUID) {
        _trenutniKrug.value?.dokumentId = dokumentId
        addKrug()
        _stablaKruga.postValue(mutableListOf())
        _trenutnoStablo.postValue(Stablo(krugId = _trenutniKrug.value?.id!!))
        _trenutnoMrtvoStablo.postValue(MrtvoStablo(krugId = _trenutniKrug.value?.id!!))
        _trenutnaMrtvaStabla.postValue(mutableListOf())
        _biodiverzitet.postValue(Biodiverzitet(krugId = _trenutniKrug.value?.id!!))
    }

    private fun addKrug() {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                _trenutniKrug.value?.let { krugRepository.add(it) }
            }
        }
    }

    fun setTrenutniKrug(krug : Krug) {
        _trenutniKrug.postValue(krug)
    }

    fun setStablaKruga() {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                _stablaKruga.postValue(_trenutniKrug.value?.let { stabloRepository.getByKrug(it.id) })
            }
        }
    }

    fun updateStablaKruga(){
        _trenutnoStablo.value?.let {
            if (_stablaKruga.value?.none { it.id == _trenutnoStablo.value?.id } == true) {
                val updatedList = _stablaKruga.value?.toMutableList() ?: mutableListOf()
                updatedList.add(it)
                _stablaKruga.value = updatedList
            }
        }
    }

    fun setDefaultStablo() {
        if (_stablaKruga.value?.size!! > 0){_trenutnoStablo.value = _stablaKruga.value!![0]}
        else{setTrenutnoStabloKrugID()}
    }


}
