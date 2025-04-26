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

    init {
        getEarliest()
        getAll()
        getKrugovi()
    }

    fun getKrugovi() {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val krugovi = _trenutniDokument.value?.let { krugRepository.getByDokument(it.id) }
                _krugovi.postValue(krugovi)
            }
        }
    }

    fun getEarliest(){
        viewModelScope.launch {
            val dokument = withContext(Dispatchers.IO) {
                dokumentRepository.getEarliest()
            }
            _trenutniDokument.postValue(dokument ?: Dokument())
        }
    }

    fun getAll (){
        viewModelScope.launch {
            val dokumenti = withContext(Dispatchers.IO) {
                dokumentRepository.getAll()
            }
            _dokumenti.postValue(dokumenti)
        }
    }

    fun exists(onResult : (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                trenutniDokument.value?.id?.let { dokumentRepository.exists(it) } ?: false
            }
            onResult(result)
        }
    }

    fun add(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                trenutniDokument.value?.let {
                    dokumentRepository.add(it)
                    _dokumenti.value?.add(it)
                }
            }
        }
    }

    fun setTrenutniKrugovi(krugovi : MutableList<Krug>){
        _krugovi.postValue(krugovi)
    }


}