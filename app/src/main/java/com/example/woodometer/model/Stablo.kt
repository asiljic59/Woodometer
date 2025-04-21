package com.example.woodometer.model
import java.util.UUID

class Stablo(var rbr : Int = 1,var krugId: UUID = UUID.randomUUID() ) {
    var id : UUID = UUID.randomUUID()
    var vrsta: Int = 0
    var azimut: Int = 0
    var razdaljina: Float = 0f
    var precnik: Float = 0f
    var visina: Int = 0
    var duzDebla: Int = 0
    var socStatus: Int = 0
    var stepSusenja: Int = 0
    var tehKlasa: Int = 0
    var probDoznaka: Int = 30
}