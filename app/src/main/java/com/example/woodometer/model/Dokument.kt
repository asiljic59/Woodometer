package com.example.woodometer.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
class Dokument() {
    @PrimaryKey
    var id : UUID = UUID.randomUUID()
    var brOdeljenja: Int = 0
    var odsek: String = ""
    var gazJedinica: Int = 0
    var korisnik: Int = 0
    var timestamp: Long = System.currentTimeMillis()
}