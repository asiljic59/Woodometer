package com.example.woodometer.interfaces

import com.example.woodometer.model.MrtvoStablo


//sluzi za rukovanje CRUD operacijama nad stablima/mrtvim stablima
interface TreeListener {
    fun deleteTree(rbr : Int)
    fun deleteConfirmed(deleted:Boolean,rbr : Int);
    fun editTree(item: MrtvoStablo)
}