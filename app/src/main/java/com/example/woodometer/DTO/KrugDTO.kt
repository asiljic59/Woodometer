package com.example.woodometer.DTO

import com.example.woodometer.model.Biodiverzitet
import com.example.woodometer.model.Krug
import com.example.woodometer.model.MrtvoStablo
import com.example.woodometer.model.Stablo
import java.util.UUID

class KrugDTO(
    var IdBroj : Int? = null,
    var brKruga: Int = 0,
    var permanentna: Boolean? = null,
    var pristupacnost : Boolean? = null,
    var nagib : Float = 0f,
    var gazTip : Int = 0,
    var uzgojnaGrupa : Int = 0,
    var stabla : List<StabloDTO> = listOf(),
    var mrtvaStabla : List<MrtvoStabloDTO> = listOf(),
    var biodiverzitet: BiodiverzitetDTO? = BiodiverzitetDTO()
) {
    constructor(krug : Krug) : this() {
        this.IdBroj = krug.IdBroj
        this.brKruga = krug.brKruga
        this.permanentna = krug.permanentna
        this.pristupacnost = krug.pristupacnost
        this.nagib = krug.nagib
        this.gazTip = krug.gazTip
        this.uzgojnaGrupa = krug.uzgojnaGrupa
    }

}