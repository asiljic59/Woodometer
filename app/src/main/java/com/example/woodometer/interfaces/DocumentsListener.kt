package com.example.woodometer.interfaces

import com.example.woodometer.model.Dokument

interface DocumentsListener {
    fun docClicked(dokument : Dokument)
    fun docLongClicked(dokument: Dokument)
}