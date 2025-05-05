package com.example.woodometer.DTO

import com.example.woodometer.model.Dokument
import com.example.woodometer.model.Krug
import java.util.UUID

class DokumentDTO(
    var brOdeljenja: Int = 0,
    var odsek: String = "",
    var gazJedinica: Int = 0,
    var krugovi : MutableList<KrugDTO> = mutableListOf()
) {
    constructor(dokument: Dokument) : this(){
        this.brOdeljenja = dokument.brOdeljenja
        this.odsek = dokument.odsek
        this.gazJedinica = dokument.gazJedinica
    }
}