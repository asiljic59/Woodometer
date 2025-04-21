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
    indices = [Index("userId")]
)
data class Stablo(
    @PrimaryKey
    var id : UUID = UUID.randomUUID(),
    var vrsta: Int = 0,
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
)