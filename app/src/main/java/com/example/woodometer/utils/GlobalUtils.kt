package com.example.woodometer.utils

import com.example.woodometer.model.Biodiverzitet
import com.example.woodometer.model.Dokument
import java.sql.Timestamp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object GlobalUtils {
    const val DISTANCE_UPPER_LIMIT = 12.62
    const val DISTANCE_UPPER_LIMIT_UNDER_30 = 7.98
    //najmanja moguca
    const val DIAMETER_LOWER_LIMIT = 10.1
    //najmanja moguca PREKO DISTANCE_UPPER_LIMIT_UNDER_30 = 7.98 do 12.62
    const val DIAMETER_HEIGHT_LIMIT = 30.1

    const val NAC_ZAPRE = 8
    const val NAC_PRIR = 6

    val  VRSTE_DRVECA = listOf(
        11 to "Bela vrba",      // 11 → Bela vrba
        12 to "Bademasta vrba", // 12 → Bademasta vrba
        13 to "Krta vrba",      // 13 → Krta vrba
        14 to "Siva vrba",      // 14 → Siva vrba
        21 to "Crna jova",       // 21 → Crna jova
        22 to "Bela jova",
        23 to "Bela topola",
        24 to "Crna topola",
        25 to "Topola roubsta",
        26 to "Topola serotina",
        27 to "Topola marilandika",
        28 to "Topola ostia",
        29 to "Topola I-154",
        30 to "Topola I-214",
        31 to "Deltoidna topola",
        32 to "Siva topola",
        33 to "Topola M1",
        34 to "Sibirski brest",
        37 to "Domaći orah",
        38 to "Poljski brest",
        39 to "Vez",
        40 to "Ostali meki lišćari",
        41 to "Poljski jasen",
        42 to "Lužnjak",
        43 to "Grab",
        44 to "Cer",
        45 to "Sitnolisna lipa",
        46 to "Krupnolisna lipa",
        47 to "Srebrna lipa",
        48 to "Koprivić",
        49 to "Sladun",
        50 to "Trešnja",
        51 to "Ostali tvrdi lišćari",
        52 to "Kesten",
        53 to "Medunac",
        54 to "Crni jasen",
        55 to "Grabić",
        56 to "Crni grab",
        57 to "Kitnjak",
        58 to "Jasika",
        59 to "Breza",
        60 to "Mečja leska",
        61 to "Bukva",
        62 to "Planinski brest",
        63 to "Beli jasen",
        64 to "Mleč",
        65 to "Javor",
        66 to "Planinski javor",
        67 to "Jela",
        68 to "Smrča",
        69 to "Omorika",
        70 to "Crni bor",
        71 to "Beli bor",
        72 to "Molika",
        73 to "Munika",
        74 to "Krivulj",
        75 to "Bagrem",
        76 to "Crni orah",
        77 to "Američki jasen",
        78 to "Gledicija",
        79 to "Crveni hrast",
        80 to "Platan",
        81 to "Kiselo drvo",
        82 to "Jasenoliki javor",
        83 to "Duglazija",
        84 to "Borovac",
        85 to "Grandisova jela",
        86 to "Normandiana",
        87 to "Ariš",
        88 to "Džefrej",
        89 to "Ponderoza",
        90 to "Kedar",
        91 to "Tisa",
        92 to "Katalpa",
        93 to "Ostali četinari",
        94 to "Jarebika",
        95 to "Klen",
        96 to "Sofora",
        97 to "Makedonski hrast",
        98 to "Virdžinijska borov. kleka",
        99 to "Brekinja",
    )

    val PROBNE_DOZNAKE = listOf(
        10 to "Potencijalno stablo budućnosti",
        21 to "Konkurent stablima budućnosti",
        30 to "Indiferentno stablo, bez uticaja",
        41 to "Hitno za seču, vrlo lošeg kvaliteta",
        51 to "Zrelo, seča obnavljanja"
    )

    val GAZ_TIPOVI = listOf(
        1110 to "Visoke mešovite šume OML",
        1120 to "Izdanačke mešovite šume OML",
        1121 to "Izdanačke mešovite šume OML - Visoke mešovite šume OML",
        1210 to "Veštački podignute plantaže topole",
        2310 to "Visoke mešovite šume poljskog jasena",
        2410 to "Visoke mešovite šume lužnjaka",
        2510 to "Visoke mešovite šume kitnjaka, sladuna i cera",
        2620 to "Izdanačke mešovite šume hrastova",
        2621 to "Izdanačke mešovite šume hrastova - Visoke šume hrastova i ostalih lišćara",
        2721 to "Izdanačke mešovite šume lipa - Visoke šume lipe i ostalih lišćara",
        2810 to "Visoke mešovite šume OTL",
        2820 to "Izdanačke mešovite šume OTL",
        2821 to "Izdanačke mešovite šume OTL - Visoke mešovite šume OTL",
        2920 to "Izdanačke mešovite šume bagrema",
        21010 to "Visoke mešovite šume javora i jasena",
        21110 to "Visoke mešovite šume bukve",
        21120 to "Izdanačke mešovite šume bukve",
        21121 to "Izdanačke mešovite šume bukve - Visoke šume bukve i ostalih lišćara",
        31210 to "Visoke mešovite šume borova",
        31211 to "Visoke mešovite šume borova - Visoke šume lišćara i četinara",
        31510 to "Visoke mešovite šume smrče",
        31511 to "Visoke mešovite šume smrče - Visoke šume četinara i lišćara",
        31610 to "Izdanačke mešovite šume bukve - Visoke šume bukve i ostalih lišćara",
        41310 to "Visoke šume bukve i jele",
        41410 to "Visoke šume bukve, jele i smrče",
        51730 to "Šibljaci, šikare i žbunasta vegetacija",
        51731 to "Šibljaci, šikare i žbunasta vegetacija za rekonstrukciju",

    )

    val BIODIVERZITET_VREDNOSTI = listOf(
        Biodiverzitet::dubeca to "Dubeća odumiruća stara stabla",
        Biodiverzitet::osteceniVrh to "Stara stabla sa oštećenim/polomljenim vrhom",
        Biodiverzitet::ostecenaKora to "Stara stabla sa oštećenom ili ispucalom korom",
        Biodiverzitet::gnezda to "Stabla sa gnezdima",
        Biodiverzitet::supljine to "Stabla sa šupljinama,pukotinama ili otvorima (D > 30cm)",
        Biodiverzitet::lisajevi to "Značajna zastupljenost lišajeva na deblu (>30%)",
        Biodiverzitet::mahovine to "Značajna zastupljenost mahovine na deblu (>40%)",
        Biodiverzitet::gljive to "Prisustvo gljiva na deblu",
        Biodiverzitet::izuzetnaDimenzija to "Stabla izuzetnih dimenzija",
        Biodiverzitet::velikaUsalmljena to "Velika usamljena, suncu izložena stabla"
    )

    val UZGOJNE_GRUPE = listOf(
        1 to "Podmladak",
        2 to "Rani mladik",
        3 to "Kasni mladik",
        4 to "Srednjedobna sastojina",
        5 to "Dozrevajuća sastojina",
        6 to "Zrela-Regeneracija"
    )
    val STEPENI_SUSENJA = listOf(
        0 to "Zdravo stablo bez pojave sušenja",
        1 to "Polusuvo stablo",
        2 to "Suvo stablo"
    )
    val SOCIJALNI_STATUSI = listOf(
        1 to "Predominantna i dominantna",
        2 to "Kodominantna",
        3 to "Potištena uključujići i suva stabla"
    )
    val TEHNICKE_KLASE = listOf(
        1 to "Stablo sa pravilno razvijenom krunom bez oštećenja i preloma sa deblom bez oštećenja",
        2 to "Stablo sa pravilno razvijenom krunom bez oštećenja i preloma sa deblom sa manjim oštećenjem - oštećena i malo kriva",
        3 to "Drvo sa vidno oštećenim i/ili krivim deblom (Drvo lošeg kvaliteta)"
    )

    val POLOZAJ_STABLA = listOf(
        1 to "Stablo u dubećem položaju",
        2 to "Stablo u ležećem položaju",
        3 to "Stablo je dubeće i prelomljeno",
        4 to "Deo ležećeg stabla"
    )
    fun formatDateDocument(timestamp: Long) : String{
        val formatter = DateTimeFormatter.ofPattern("ddMMyyyy")

        return Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .format(formatter)
    }

}