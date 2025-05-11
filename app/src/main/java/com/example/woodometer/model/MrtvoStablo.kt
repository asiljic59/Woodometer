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
data class MrtvoStablo (
    @PrimaryKey
    var id : UUID = UUID.randomUUID(),
    var vrsta: Int = 61,
    var polozaj: Int = 0,
    var precnik: Float = 0f,
    var visina: Int = 0,
    var rbr : Int = 1,
    var krugId: UUID = UUID.randomUUID() // Added default value
){
    fun hasAnyDefaultVal() : Boolean{
        return polozaj == 0 || precnik == 0f || visina == 0
    }
}