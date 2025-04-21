package com.example.woodometer.interfaces


//sluzi za rukovanje dodavanja opcije u GJ, Korisinka ili Odseka
interface AddOptionListener {
    fun addOption(option : String)
    fun optionPicked(option : String)
}