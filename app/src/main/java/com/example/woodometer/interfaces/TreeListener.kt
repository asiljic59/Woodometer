package com.example.woodometer.interfaces

import com.example.woodometer.model.MrtvoStablo
import com.example.woodometer.model.Stablo


//sluzi za rukovanje CRUD operacijama nad stablima/mrtvim stablima
interface TreeListener {
    fun changeTree(stablo: Stablo)
    fun deleteTree(rbr : Int)
    fun deleteConfirmed(deleted:Boolean,rbr : Int);
    fun editTree(item: MrtvoStablo)
}