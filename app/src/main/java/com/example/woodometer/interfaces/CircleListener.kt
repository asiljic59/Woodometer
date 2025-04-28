package com.example.woodometer.interfaces

import com.example.woodometer.model.Krug

interface CircleListener {
    fun circleChanged(krug : Krug)
    fun finishConfirmed(finish: Boolean, rbr: Int)
    fun showEditDeleteDialog(krug: Krug)
}