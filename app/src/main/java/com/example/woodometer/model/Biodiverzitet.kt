package com.example.woodometer.model
import java.util.UUID

class Biodiverzitet(var krugId: UUID = UUID.randomUUID()){
    var id : UUID = UUID.randomUUID()
    var dubeca: Int = 0
    var osteceniVrh: Int = 0
    var ostecenaKora: Int = 0
    var gnezda: Int = 0
    var supljine: Int = 0
    var lisajevi: Int = 0
    var mahovine: Int = 0
    var gljive: Int  = 0
    var izuzetnaDimenzija:Int = 0
    var velikaUsalmljena: Int = 0
}