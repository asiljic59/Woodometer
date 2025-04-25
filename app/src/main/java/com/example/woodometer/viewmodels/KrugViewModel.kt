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
import com.example.woodometer.repository.BiodiverzitetRepository
import com.example.woodometer.repository.KrugRepository
import com.example.woodometer.repository.MrtvoStabloRepository
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
    private val stabloRepository: StabloRepository = StabloRepository()
    private val krugRepository : KrugRepository = KrugRepository()
    private val biodiverzitetRepository : BiodiverzitetRepository = BiodiverzitetRepository()
    private val mrtvoStabloRepository : MrtvoStabloRepository = MrtvoStabloRepository()


    private val _trenutniKrug = MutableLiveData<Krug>().apply {
        value = Krug()
    }

    private val _trenutnoStablo = MutableLiveData<Stablo>().apply {
        value = Stablo ()
    }
    private val _stablaKruga = MutableLiveData<MutableList<Stablo>>().apply {
        value = mutableListOf()
    }


    private val _trenutnaMrtvaStabla = MutableLiveData<MutableList<MrtvoStablo>>(mutableListOf())

    private val _trenutnoMrtvoStablo = MutableLiveData<MrtvoStablo>().apply {
        value = MrtvoStablo(rbr = 1,krugId = _trenutniKrug.value?.id!!)
    }
    private val _biodiverzitet = MutableLiveData<Biodiverzitet>().apply {
        value = Biodiverzitet(_trenutniKrug.value?.id!!)
    }

    val trenutniKrug: LiveData<Krug> get() = _trenutniKrug
    val trenutnaMrtvaStabla: LiveData<MutableList<MrtvoStablo>> = _trenutnaMrtvaStabla
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


    fun resetStablo() {
        val stablaList = _stablaKruga.value ?: emptyList()
        val rbr = stablaList.maxOfOrNull { it.rbr } ?: 0
        _trenutnoStablo.value = _trenutniKrug.value?.id?.let { Stablo(rbr = rbr+1, krugId = it) }
    }
    fun deleteStablo(rbr: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val stablo = stablaKruga.value?.find { it.rbr == rbr }
                stabloRepository.delete(stablo!!)
                val updatedList = stablaKruga.value
                updatedList?.removeAll{it.rbr == rbr}
                _stablaKruga.postValue(updatedList)
            }
        }
    }

    ///MRTVA STABLA !!!!!!!!!!!!!
    fun setTrenutnaMrtvaStabla(stabla: MutableList<MrtvoStablo>) {
        _trenutnaMrtvaStabla.value = stabla
    }
    //SETOVANJE NOVOG MRTVOG STABLA KOJE CE DA BUDE AZURIRANO
    fun setMrtvoStabloToEdit(item: MrtvoStablo) {
        _trenutnoMrtvoStablo.value = item // Important to copy to avoid reference issues
    }
    //DODAVANJE NOVOG MRTVOG STABLA
    fun addMrtvoStablo() {

        viewModelScope.launch {
            withContext(Dispatchers.IO){
                mrtvoStablo.value?.let { newStablo ->
                    mrtvoStabloRepository.upsert(newStablo)
                    val updatedList = _trenutnaMrtvaStabla.value?.toMutableList() ?: mutableListOf()
                    // Racunamo novi redni broj tako sto uzimamo maximum iz liste +1
                    updatedList.add(newStablo)
                    val maxRbr = updatedList.maxOfOrNull { it.rbr } ?: 0  // If list is empty, start from 1
                    _trenutnaMrtvaStabla.postValue(updatedList)
                    // Update the _trenutnoMrtvoStablo LiveData
                    _trenutnoMrtvoStablo.postValue(MrtvoStablo(rbr = maxRbr + 1,krugId = trenutniKrug.value?.id!!))
                }
            }
        }
    }
    //BRISANJE MRTVOG STABLA
    fun deleteMrtvoStablo(rbr : Int){
        viewModelScope.launch {
            withContext(Dispatchers.IO){withContext(Dispatchers.IO){
                val stablo = trenutnaMrtvaStabla.value?.find { it.rbr == rbr }
                mrtvoStabloRepository.delete(stablo!!)
                val updatedList = trenutnaMrtvaStabla.value?.toMutableList() ?: mutableListOf()
                updatedList.removeAll{it.rbr == rbr}
                _trenutnaMrtvaStabla.postValue(updatedList)
            }
            }
        }
    }

    fun editMrtvoStablo() {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                mrtvoStablo.value?.let { mrtvoStabloRepository.upsert(it) }
                val updatedList = trenutnaMrtvaStabla.value?.toMutableList() ?: mutableListOf()
                updatedList.removeAll{it.rbr == mrtvoStablo.value?.rbr}
                mrtvoStablo.value?.let { updatedList.add(it) }
                _trenutnaMrtvaStabla.postValue(updatedList)
                val nextRbr = updatedList.maxOfOrNull { it.rbr } ?: 0
                _trenutnoMrtvoStablo.postValue(MrtvoStablo(rbr = nextRbr + 1,krugId = _trenutniKrug.value?.id!! ))
            }
        }
    }
    fun getMrtvaStablaByKrug() {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val mrtvaStabla = trenutniKrug.value?.let { mrtvoStabloRepository.getByKrug(it.id) }!!
                if (mrtvaStabla.isNotEmpty()){
                    _trenutnaMrtvaStabla.postValue(mrtvaStabla)
                }
                val maxRbr = mrtvaStabla.maxOfOrNull { it.rbr } ?: 0
                _trenutnoMrtvoStablo.postValue(MrtvoStablo(krugId = trenutniKrug.value?.id!!,rbr = maxRbr + 1 ))
            }
        }
    }
    ////MRTVA STABLA !!!! CRUD

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
                val stabla  = trenutniKrug.value?.let { stabloRepository.getByKrug(it.id) }
                if (stabla?.isEmpty() == false) {_trenutnoStablo.postValue(stabla[0])}
                _stablaKruga.postValue(stabla)
            }
        }
    }
    //dodavanje novog stabla u stabla kruga
    fun updateStablaKruga(){
        _trenutnoStablo.value?.let {
            if (_stablaKruga.value?.none { it.id == _trenutnoStablo.value?.id } == true) {
                val updatedList = _stablaKruga.value?.toMutableList() ?: mutableListOf()
                updatedList.add(it)
                _stablaKruga.value = updatedList
            }
        }
    }


    //BIODIVERZITET CRUD
    fun updateBiodiverzitet() {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                biodiverzitet.value?.let { biodiverzitetRepository.upsert(it) }
            }
        }
    }

    fun getBiodiverzitetByKrug() {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val biodiverzitet = trenutniKrug.value?.let { biodiverzitetRepository.getByKrug(it.id) }
                if (biodiverzitet == null){
                    _biodiverzitet.postValue(Biodiverzitet(krugId = trenutniKrug.value?.id!!))
                }else{
                    _biodiverzitet.postValue(biodiverzitet)
                }

            }
        }
    }



}
