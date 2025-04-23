package com.example.woodometer.model
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

@Entity(
    foreignKeys = [ForeignKey(
        entity = Dokument::class,
        parentColumns = ["id"],
        childColumns = ["dokumentId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("dokumentId")]
)
data class Krug(
    @PrimaryKey
    var id: UUID = UUID.randomUUID(),
    var IdBroj : Int? = null,
    var brKruga: Int = 0,
    var permanentna: Boolean? = null,
    var pristupacnost : Boolean? = null,
    var nagib : Float = 0f,
    var gazTip : Int = 0,
    var uzgojnaGrupa : Int = 0,
    var dokumentId : UUID = UUID.randomUUID()
) {
    fun hasAnyDefaultVal() : Boolean{
        return brKruga == 0 || permanentna == null
                || pristupacnost == null || gazTip == 0
                || uzgojnaGrupa == 0 || nagib == 0f || (permanentna == true && IdBroj == 0)
    }

}