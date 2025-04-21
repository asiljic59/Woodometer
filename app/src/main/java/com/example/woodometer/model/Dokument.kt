package com.example.woodometer.model
import java.util.UUID

class Dokument() {
    var id : UUID = UUID.randomUUID()
    var brOdeljenja: Int = 0
    var odsek: String = ""
    var gazJedinica: Int = 0
    var korisnik: Int = 0
    var timestamp: Long = System.currentTimeMillis()
}