package com.example.woodometer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.woodometer.Woodometer
import com.example.woodometer.model.Dokument
import com.example.woodometer.dao.DokumentDao
import com.example.woodometer.model.Krug
import com.example.woodometer.model.Stablo
import com.example.woodometer.repository.DokumentRepository
import com.example.woodometer.repository.KrugRepository
import com.example.woodometer.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DokumentViewModel : ViewModel() {
    private var dokumentRepository: DokumentRepository = DokumentRepository()
    private var krugRepository : KrugRepository = KrugRepository()

    private val _dokumenti = MutableLiveData<MutableList<Dokument>>().apply {
        value = mutableListOf()
    }
    val dokumenti: LiveData<MutableList<Dokument>> get() = _dokumenti


    private val _trenutniDokument = MutableLiveData<Dokument>().apply {
        value = Dokument()
    }
    val trenutniDokument: LiveData<Dokument> get() = _trenutniDokument

    private val _krugovi = MutableLiveData<MutableList<Krug>>().apply {
        value = mutableListOf()
    }
    val krugovi : LiveData<MutableList<Krug>> get() = _krugovi

    fun setTrenuntniDokument(dokument: Dokument){
        _trenutniDokument.postValue(dokument)
    }

    suspend fun refreshData() {
        getNewest()
        getAll()
        getKrugovi()

    }

    suspend fun getKrugovi() {
        val krugovi = withContext(Dispatchers.IO) {
            _trenutniDokument.value?.let {
                krugRepository.getByDokument(it.id).sortedBy { it.brKruga }.toMutableList()
            }
        }
        _krugovi.value = krugovi
    }

    fun addKrug(krug: Krug){
        _krugovi.value?.add(krug)
    }


    suspend fun isEmpty() : Boolean{
        return withContext(Dispatchers.IO) {
            dokumentRepository.isEmpty()  // Calls the isEmpty function in the repository
        }
    }

    suspend fun getNewest(){
        val dokument = withContext(Dispatchers.IO) {
            dokumentRepository.getNewest() ?: Dokument()
        }
        _trenutniDokument.value = dokument

    }

    fun getAll (){
        viewModelScope.launch {
            val dokumenti = withContext(Dispatchers.IO) {
                dokumentRepository.getAll()
            }
            _dokumenti.postValue(dokumenti)
        }
    }

    fun existsAndSame(onResult : (Boolean) -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                var result = dokumentRepository.exists(trenutniDokument.value?.id!!)
                if (result){
                    val dokument = dokumentRepository.get(trenutniDokument.value?.id!!)
                    if (dokument.hashCode() != trenutniDokument.value.hashCode()){
                        result = false
                    }
                }
                onResult(result)
            }


        }
    }

    suspend fun add() = withContext(Dispatchers.IO) {
        val current = trenutniDokument.value ?: return@withContext
        dokumentRepository.add(current)

        val newDoc = trenutniDokument.value ?: return@withContext
        val currentList = _dokumenti.value.orEmpty()

        val updatedList = if (currentList.any { it.id == newDoc.id }) {
            currentList.map { if (it.id == newDoc.id) newDoc else it }.toMutableList()
        } else {
            (currentList + newDoc).toMutableList()
        }

        // Post back to LiveData safely from background thread
        _dokumenti.postValue(updatedList)
    }



    fun delete(dokument: Dokument){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val currentList = _dokumenti.value.orEmpty().toMutableList()
                currentList.removeIf { it.id == dokument.id }
                _dokumenti.postValue(currentList)
                dokumentRepository.delete(dokument)
            }
        }
    }

    fun setTrenutniKrugovi(krugovi : MutableList<Krug>){
        _krugovi.postValue(krugovi)
    }

    fun existsKrug(radniKrug: Krug?): Boolean {
        return krugovi.value?.any{it.id == radniKrug?.id}!!
    }


}