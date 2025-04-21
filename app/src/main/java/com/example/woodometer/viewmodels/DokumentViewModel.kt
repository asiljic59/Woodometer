package com.example.woodometer.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.woodometer.model.Dokument
import com.example.woodometer.repository.DokumentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class DokumentViewModel : ViewModel() {

    private val _dokumenti = MutableLiveData<List<Dokument>>()
    val dokumenti: LiveData<List<Dokument>> get() = _dokumenti


    private val _trenutniDokument = MutableStateFlow(Dokument())
    val trenutniDokument: StateFlow<Dokument> = _trenutniDokument.asStateFlow()
}