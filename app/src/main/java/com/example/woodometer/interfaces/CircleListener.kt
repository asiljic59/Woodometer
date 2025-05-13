package com.example.woodometer.interfaces

import com.example.woodometer.model.Krug

interface CircleListener {
    fun circleChanged(krug : Krug)
    fun showEditDeleteDialog(krug: Krug)
}