package com.example.woodometer.model

class Krug (
    var gazJedinica : Int,
    var brOdeljenja: Int,
    var brKruga: Int,
    var odsek : String,
    var permanentna: Boolean,
    var pristupacnost : Boolean,
    var nagib : Float,
    var stabla : List<Stablo>,
    var biodiverzitet: Biodiverzitet
){}