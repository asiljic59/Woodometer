package com.example.woodometer.interfaces


//one listener for all purposes of calling virtual keyboard
interface KeyboardListener {
    fun onKeyPressed(key : String)
    fun onEnterPressed (input: String)
    fun onClearPressed()
}