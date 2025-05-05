package com.example.woodometer.DTO

import androidx.room.PrimaryKey
import com.example.woodometer.model.Stablo
import java.util.UUID

class StabloDTO(
    var vrsta: Int = 11,
    var azimut: Int = 0,
    var razdaljina: Float = 0f,
    var precnik: Float = 0f,
    var visina: Int = 0,
    var duzDebla: Int = 0,
    var socStatus: Int = 0,
    var stepSusenja: Int = 0,
    var tehKlasa: Int = 0,
    var probDoznaka: Int = 30,
    var rbr : Int = 1,
) {
    constructor(stablo: Stablo) : this(
        vrsta = stablo.vrsta,
        azimut = stablo.azimut,
        razdaljina = stablo.razdaljina,
        precnik = stablo.precnik,
        visina = stablo.visina,
        duzDebla = stablo.duzDebla,
        socStatus = stablo.socStatus,
        stepSusenja = stablo.stepSusenja,
        tehKlasa = stablo.tehKlasa,
        probDoznaka = stablo.probDoznaka,
        rbr = stablo.rbr
    )
}