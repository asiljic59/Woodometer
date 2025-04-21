package com.example.woodometer.model
import java.util.UUID

class MrtvoStablo (
    var id : UUID = UUID.randomUUID(),
    var vrsta: Int = 11,
    var polozaj: Int = 1,
    var precnik: Float = 0f,
    var visina: Int = 0,
    var rbr : Int = 1,
    var krugId: UUID = UUID.randomUUID() // Added default value
)