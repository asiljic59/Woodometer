package com.example.woodometer.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.woodometer.Woodometer
import com.example.woodometer.model.Dokument
import com.example.woodometer.repository.DokumentDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DokumentViewModel : ViewModel() {
    val dokumentDao : DokumentDao = Woodometer.database.dokumentDao()

    private val _dokumenti = MutableLiveData<MutableList<Dokument>>().apply {
        value = mutableListOf()
    }
    val dokumenti: LiveData<MutableList<Dokument>> get() = _dokumenti


    private val _trenutniDokument = MutableLiveData<Dokument>().apply {
        value = Dokument()
    }
    val trenutniDokument: LiveData<Dokument> get() = _trenutniDokument

    fun setTrenuntniDokument(dokument: Dokument){
        _trenutniDokument.postValue(dokument)
    }

    init {
        getEarliest()
        getAll()
    }

    fun getEarliest(){
        viewModelScope.launch {
            val dokument = withContext(Dispatchers.IO) {
                dokumentDao.getEarliest()
            }
            _trenutniDokument.postValue(dokument ?: Dokument())
        }
    }

    fun getAll (){
        viewModelScope.launch {
            val dokumenti = withContext(Dispatchers.IO) {
                dokumentDao.getAll()
            }
            _dokumenti.postValue(dokumenti)
        }
    }

    fun exists(onResult : (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                trenutniDokument.value?.id?.let { dokumentDao.exists(it) } ?: false
            }
            onResult(result)
        }
    }

    fun add(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                trenutniDokument.value?.let {
                    dokumentDao.add(it)
                    _dokumenti.value?.add(it)
                }
            }
        }
    }


}