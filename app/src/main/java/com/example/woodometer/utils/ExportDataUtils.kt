package com.example.woodometer.utils

import android.app.Activity
import com.example.woodometer.model.Dokument
import com.example.woodometer.model.Krug
import com.example.woodometer.repository.BiodiverzitetRepository
import com.example.woodometer.repository.MrtvoStabloRepository
import com.example.woodometer.repository.StabloRepository
import com.example.woodometer.utils.GlobalUtils.NAC_PRIR
import com.example.woodometer.utils.GlobalUtils.NAC_ZAPRE
import java.io.File

object ExportDataUtils {
    private val folderName  = "dokumenti"
    private val stabloRepository = StabloRepository()
    private val mrtvoStabloRepository = MrtvoStabloRepository()
    private val biodiverzitetRepository = BiodiverzitetRepository()

    private lateinit var dokument : Dokument
    private lateinit var krug : Krug
    private lateinit var currentFile : File

    private val queries : StringBuilder = StringBuilder()
    fun exportDocument(dokument: Dokument,krugovi : List<Krug>,activity: Activity?) : File{
        this.dokument = dokument
        val fileName ="${dokument.gazJedinica}${dokument.brOdeljenja}${dokument.odsek}_${dokument.korisnik}_${GlobalUtils.formatDateDocument(dokument.timestamp
        )}.txt"

        val folder = File (activity?.filesDir, folderName)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        currentFile = File(folder,fileName)
        if (currentFile.exists()) {
            currentFile.writeText("")
        }
        writeOpis()
        krugovi.forEach { krug ->
            this.krug = krug
            writeIndikacija()
            writeStabla()
            writeMrtvaStabla()
            writeBiodiverzitet()
        }

        queries.removeSuffix(System.lineSeparator())
        currentFile.writeText(queries.toString())

        return currentFile
    }

    private fun writeOpis() {
        val odeljenje: String = dokument.brOdeljenja.toString().padStart(3, '0')
        val gisOdsek: String = "${dokument.gazJedinica}${odeljenje}${dokument.odsek}"
        val insertQuery = "INSERT INTO OpisOpste (GJ, ODELJENJE, ODSEK, GisOdsek, NAC_ZAPRE, NAC_PRIR) " +
                "SELECT ${dokument.gazJedinica} AS GJ, ${dokument.brOdeljenja} AS ODELJENJE, '${dokument.odsek}' AS ODSEK, $gisOdsek as GisOdsek, $NAC_ZAPRE as NAC_ZAPRE, $NAC_PRIR as NAC_PRIR"+
                "FROM (SELECT COUNT(*) AS RecordCount FROM Indikacija " +
                "WHERE GJ = ${dokument.gazJedinica} AND ODELJENJE = ${dokument.brOdeljenja} AND ODSEK = '${dokument.odsek}'     AS Temp " +
                "WHERE Temp.RecordCount = 0;"
        queries.append(insertQuery).append(System.lineSeparator())
    }

    private fun writeIndikacija() {
        val permanentni = when {
            krug.permanentna == true -> 1
            else -> 0
        }
        val krugId = when {
            krug.IdBroj == null -> 0
            else -> krug.IdBroj
        }
        val insertQuery = "INSERT INTO Indikacija (GJ, ODELJENJE, ODSEK, BrKr, Stalna, Nagib, BrStalneTacke, Uz_Grupa, GTS) " +
                "SELECT ${dokument.gazJedinica} AS GJ, ${dokument.brOdeljenja} AS ODELJENJE, '${dokument.odsek}' AS ODSEK, ${krug.brKruga} AS BrKr, $permanentni as Stalna, ${krug.nagib} as Nagib, $krugId as BrStalneTacke, ${krug.uzgojnaGrupa} as Uz_Grupa, ${krug.gazTip} as GTS " +
                "FROM (SELECT COUNT(*) AS RecordCount FROM Indikacija " +
                "WHERE GJ = ${dokument.gazJedinica} AND ODELJENJE = ${dokument.brOdeljenja} AND ODSEK = '${dokument.odsek}' AND BrKr = ${krug.brKruga}) AS Temp " +
                "WHERE Temp.RecordCount = 0;"
        queries.append(insertQuery).append(System.lineSeparator())
    }

    fun writeStabla() {
        val stabla = stabloRepository.getByKrug(krug.id)
        stabla.forEach { stablo ->
            val upsertQuery = """
            UPDATE Krug SET
                VrsDrv = ${stablo.vrsta},
                Azimut = ${stablo.azimut},
                Razdaljina = ${stablo.razdaljina},
                Ds = ${stablo.precnik},
                Hs = ${stablo.visina},
                HKrune = ${stablo.duzDebla},
                SocStatus = ${stablo.socStatus},
                Klasa = ${stablo.tehKlasa},
                susenje = ${stablo.stepSusenja},
                doznaka = ${stablo.probDoznaka}
            WHERE GJ = ${dokument.gazJedinica}
              AND ODELJENJE = ${dokument.brOdeljenja}
              AND ODSEK = '${dokument.odsek}'
              AND BrKr = ${krug.brKruga}
              AND NoSt = ${stablo.rbr};
            INSERT INTO Krug (GJ, ODELJENJE, ODSEK, BrKr, NoSt, VrsDrv, Azimut, Razdaljina, Ds, Hs, HKrune, SocStatus, Klasa, susenje, doznaka)
            SELECT 
                ${dokument.gazJedinica}, 
                ${dokument.brOdeljenja}, 
                '${dokument.odsek}', 
                ${krug.brKruga}, 
                ${stablo.rbr}, 
                ${stablo.vrsta}, 
                ${stablo.azimut}, 
                ${stablo.razdaljina}, 
                ${stablo.precnik}, 
                ${stablo.visina}, 
                ${stablo.duzDebla}, 
                ${stablo.socStatus}, 
                ${stablo.tehKlasa}, 
                ${stablo.stepSusenja}, 
                ${stablo.probDoznaka}
            FROM (SELECT COUNT(*) AS RecordCount FROM Krug 
                  WHERE GJ = ${dokument.gazJedinica} 
                    AND ODELJENJE = ${dokument.brOdeljenja} 
                    AND ODSEK = '${dokument.odsek}' 
                    AND BrKr = ${krug.brKruga}
                    AND NoSt = ${stablo.rbr}) AS Temp
            WHERE Temp.RecordCount = 0;
        """.trimIndent()
            queries.append(upsertQuery).append(System.lineSeparator())
        }
    }

    private fun writeMrtvaStabla() {
        val mrtvaStabla = mrtvoStabloRepository.getByKrug(krug.id)
        mrtvaStabla.forEach { mrtvoStablo ->
            val upsertQuery = """
            UPDATE SuvaStabla SET
                VrsDrv = ${mrtvoStablo.vrsta},
                PolozajStabla = ${mrtvoStablo.polozaj},
                D = ${mrtvoStablo.precnik},
                L_H = ${mrtvoStablo.visina}
            WHERE GJ = ${dokument.gazJedinica}
              AND ODELJENJE = ${dokument.brOdeljenja}
              AND ODSEK = '${dokument.odsek}'
              AND BrKr = ${krug.brKruga}
              AND BrSt = ${mrtvoStablo.rbr};
            INSERT INTO SuvaStabla (GJ, ODELJENJE, ODSEK, BrKr, BrSt, VrsDrv, PolozajStabla, D, L_H)
            SELECT 
                ${dokument.gazJedinica}, 
                ${dokument.brOdeljenja}, 
                '${dokument.odsek}', 
                ${krug.brKruga}, 
                ${mrtvoStablo.rbr}, 
                ${mrtvoStablo.vrsta}, 
                ${mrtvoStablo.polozaj}, 
                ${mrtvoStablo.precnik}, 
                ${mrtvoStablo.visina}
            FROM (SELECT COUNT(*) AS RecordCount FROM SuvaStabla 
                  WHERE GJ = ${dokument.gazJedinica} 
                    AND ODELJENJE = ${dokument.brOdeljenja} 
                    AND ODSEK = '${dokument.odsek}' 
                    AND BrKr = ${krug.brKruga}
                    AND BrSt = ${mrtvoStablo.rbr}) AS Temp
            WHERE Temp.RecordCount = 0;
        """.trimIndent()
            queries.append(upsertQuery).append(System.lineSeparator())
        }
    }

    private fun writeBiodiverzitet() {
        var brStavke = 1
        val biodiverzitet = biodiverzitetRepository.getByKrug(krug.id) ?: return
        GlobalUtils.BIODIVERZITET_VREDNOSTI.forEach { (attr, text) ->
            val value = biodiverzitet.let { attr.get(it) }
            if (value == 0) { return@forEach }

            val upsertQuery = """
            UPDATE IndikacijaBiodiverzitet SET
                Tekst = '$text',
                Komada = $value
            WHERE Gj = ${dokument.gazJedinica}
              AND Odeljenje = ${dokument.brOdeljenja}
              AND Odsek = '${dokument.odsek}'
              AND BrKr = ${krug.brKruga}
              AND NoSt = $brStavke;
            
            INSERT INTO IndikacijaBiodiverzitet (Gj, Odeljenje, Odsek, BrKr, NoSt, Tekst, Komada)
            SELECT 
                ${dokument.gazJedinica}, 
                ${dokument.brOdeljenja}, 
                '${dokument.odsek}', 
                ${krug.brKruga}, 
                $brStavke, 
                '$text', 
                $value
            FROM (SELECT COUNT(*) AS RecordCount FROM IndikacijaBiodiverzitet 
                  WHERE Gj = ${dokument.gazJedinica} 
                    AND Odeljenje = ${dokument.brOdeljenja} 
                    AND Odsek = '${dokument.odsek}' 
                    AND BrKr = ${krug.brKruga}
                    AND NoSt = $brStavke) AS Temp
            WHERE Temp.RecordCount = 0;
        """.trimIndent()

            queries.append(upsertQuery).append(System.lineSeparator())
            brStavke++
        }
    }
}