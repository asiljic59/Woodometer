package com.example.woodometer.DTO

import androidx.room.PrimaryKey
import com.example.woodometer.model.MrtvoStablo
import java.util.UUID

class MrtvoStabloDTO (
    var vrsta: Int = 11,
    var polozaj: Int = 0,
    var precnik: Float = 0f,
    var visina: Int = 0,
    var rbr : Int = 1,
){
    constructor(mrtvoStablo: MrtvoStablo) : this(
        vrsta = mrtvoStablo.vrsta,
        polozaj = mrtvoStablo.polozaj,
        precnik = mrtvoStablo.precnik,
        visina = mrtvoStablo.visina,
        rbr = mrtvoStablo.rbr
    )
}