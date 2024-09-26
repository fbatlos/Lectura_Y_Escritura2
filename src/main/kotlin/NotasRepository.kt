package org.example.repository

import java.io.BufferedReader
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createFile
import kotlin.io.path.notExists

class CotizacionRepository {
//Una función que reciba el fichero de calificaciones y devuelva una lista de diccionarios, donde cada diccionario contiene la información de los exámenes y la asistencia de un alumno. La lista tiene que estar ordenada por apellidos.
    fun ObtenerInfo(paht:Path): MutableList<MutableMap<String, String?>> {
        val pahtFinal=paht.resolve("calificaciones.csv")
        val br:BufferedReader = Files.newBufferedReader(pahtFinal)

        var alumnadoInfo = mutableListOf<MutableMap<String, String?>>()

        if (pahtFinal.notExists()){
            pahtFinal.createFile()
        }
        br.readLine()
        br.use {
            it.forEachLine { line ->
                val allSpliteado = line.split(";")

                val map = mutableMapOf(Pair(first = "Apell", second = allSpliteado?.get(0)),
                    Pair("Asist",allSpliteado?.get(1)),
                    Pair("Asist",allSpliteado?.get(2)),
                    Pair("qualification",CalQualification(allSpliteado))
                )
                alumnadoInfo.add(map)

            }
        }
        return alumnadoInfo
    }

    fun CalQualification(allSpliteado: List<String>):String{

        var sumNote = 0.0
        var notaParcial1 = 0.0
        var notaParcial2 = 0.0
        var notaPracticas = 0.0
        var notaFinal = 0.0

        for (view in 3..8){
            if (allSpliteado[view].isEmpty()){
                sumNote+=0
            }else{
                sumNote += allSpliteado[view].replace(",",".").toDouble()
            }
        }
        return sumNote.toString()
    }

    fun MakeSummary(paht: Path,map: MutableMap< Int,List<String>>) {

        val pahtFinal = paht.resolve("calificacionesFinal.csv")
        if (pahtFinal.notExists()) {
            pahtFinal.createFile()
        }

        var numero = 0
        val bw = Files.newBufferedWriter(pahtFinal)

        bw.use {
            for (i in map) {
                val max = map.get(numero)?.get(2)?.replace(".", "")?.replace(",", ".")?.toDouble() ?: 0.0
                val min = map.get(numero)?.get(3)?.replace(".", "")?.replace(",", ".")?.toDouble() ?: 0.0
                val media = ((min + max) / 2).toString().format("%.2f").replace(",",".")
                it.write(
                    "Name : ${
                        map.get(numero)?.get(0)
                    }  |  maximus : ${max}  |  minimum : ${min} |  average : ${media}\n"
                )

                numero++
            }

        }
    }
}