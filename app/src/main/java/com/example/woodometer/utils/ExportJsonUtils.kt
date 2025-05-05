package com.example.woodometer.utils

import android.app.Activity
import com.example.woodometer.DTO.BiodiverzitetDTO
import com.example.woodometer.DTO.DokumentDTO
import com.example.woodometer.DTO.KrugDTO
import com.example.woodometer.DTO.MrtvoStabloDTO
import com.example.woodometer.DTO.StabloDTO
import com.example.woodometer.model.Dokument
import com.example.woodometer.model.Krug
import com.example.woodometer.repository.BiodiverzitetRepository
import com.example.woodometer.repository.MrtvoStabloRepository
import com.example.woodometer.repository.StabloRepository
import com.google.gson.Gson
import java.io.File

object ExportJsonUtils {
    private val folderName  = "dokumenti_json"

    private val stabloRepository = StabloRepository()
    private val mrtvoStabloRepository = MrtvoStabloRepository()
    private val biodiverzitetRepository = BiodiverzitetRepository()

    private lateinit var dokument : Dokument
    private lateinit var krug : Krug
    private lateinit var currentFile : File

    private var dokumentDTO = DokumentDTO()
    fun exportToJson(dokument: Dokument,krugovi : List<Krug>,activity: Activity?) : File{
        this.dokument = dokument
        val fileName ="${dokument.gazJedinica}${dokument.brOdeljenja}${dokument.odsek}_${dokument.korisnik}_${GlobalUtils.formatDateDocument(dokument.timestamp
        )}.json"

        val folder = File (activity?.filesDir, folderName)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        currentFile = File(folder,fileName)
        if (currentFile.exists()) {
            currentFile.writeText("")
        }
        dokumentDTO = DokumentDTO(dokument)
        krugovi.forEach { krug ->
            this.krug = krug
            dokumentDTO.krugovi.add(createKrugDTO())
        }

        val gson = Gson()
        val jsonString = gson.toJson(dokumentDTO)

        currentFile.writeText(jsonString)

        return currentFile
    }
    fun createKrugDTO(): KrugDTO{
        val krugDTO = KrugDTO(krug)
        krugDTO.stabla = stabloRepository.getByKrug(krug.id).map { StabloDTO(it) }
        krugDTO.mrtvaStabla = mrtvoStabloRepository.getByKrug(krug.id).map { MrtvoStabloDTO(it) }
        krugDTO.biodiverzitet = biodiverzitetRepository.getByKrug(krug.id)
            ?.let { BiodiverzitetDTO(it) }
        return krugDTO
    }
}