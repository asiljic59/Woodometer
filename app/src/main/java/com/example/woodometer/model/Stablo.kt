package com.example.woodometer.model
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    foreignKeys = [ForeignKey(
        entity = Krug::class,
        parentColumns = ["id"],
        childColumns = ["krugId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("krugId")]
)
data class Stablo(
    @PrimaryKey
    var id : UUID = UUID.randomUUID(),
    var vrsta: Int = 11,
    var azimut: Int = 0,
    var razdaljina: Float = 0f,
    var precnik: Float = 0f,
    var visina: Int = 0,
    var duzDebla: Int = 0,
    var socStatus: Int = 0,
    var stepSusenja: Int = 0,
    var tehKlasa: Int = 0,
    var probDoznaka: Int = 30,
    var rbr : Int = 1,
    var krugId: UUID = UUID.randomUUID()
){
    fun hasAnyDefaultVal() : Boolean{
        return  azimut == 0 || razdaljina == 0f || precnik == 0f || duzDebla == 0 || visina == 0 ||
                socStatus == 0 || stepSusenja == 0 || tehKlasa == 0
    }
    fun hasAnyNonDefaultVal() : Boolean{
        return azimut != 0 || razdaljina != 0f || precnik != 0f || duzDebla != 0 || visina != 0 ||
                socStatus != 0 || stepSusenja != 0 || tehKlasa != 0
    }
}