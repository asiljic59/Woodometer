package com.example.woodometer.model
import java.util.UUID

class Krug(var id: UUID = UUID.randomUUID()) {
    var IdBroj : Int? = null
    var brKruga: Int = 0
    var permanentna: Boolean? = null
    var pristupacnost : Boolean? = null
    var nagib : Float = 0f
    var gazTip : Int = 0
    var uzgojnaGrupa : Int = 0
}