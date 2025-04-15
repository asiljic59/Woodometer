package com.example.woodometer.interfaces


//sluzi za rukovanje povratne informacije o izabranoj vrsti stabla
interface TreeTypeListener {
    fun setTreeType(name: String, key: Int)
}