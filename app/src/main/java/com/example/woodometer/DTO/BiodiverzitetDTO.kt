package com.example.woodometer.DTO

import com.example.woodometer.model.Biodiverzitet
import java.util.UUID

class BiodiverzitetDTO(
    var dubeca: Int = 0,
    var osteceniVrh: Int = 0,
    var ostecenaKora: Int = 0,
    var gnezda: Int = 0,
    var supljine: Int = 0,
    var lisajevi: Int = 0,
    var mahovine: Int = 0,
    var gljive: Int  = 0,
    var izuzetnaDimenzija:Int = 0,
    var velikaUsalmljena: Int = 0,
) {
    constructor(biodiverzitet: Biodiverzitet) : this(
        dubeca = biodiverzitet.dubeca,
        osteceniVrh = biodiverzitet.osteceniVrh,
        ostecenaKora = biodiverzitet.ostecenaKora,
        gnezda = biodiverzitet.gnezda,
        supljine = biodiverzitet.supljine,
        lisajevi = biodiverzitet.lisajevi,
        mahovine = biodiverzitet.mahovine,
        gljive = biodiverzitet.gljive,
        izuzetnaDimenzija = biodiverzitet.izuzetnaDimenzija,
        velikaUsalmljena = biodiverzitet.velikaUsalmljena
    )
}