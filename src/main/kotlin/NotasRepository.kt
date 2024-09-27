package org.example.repository

import jdk.dynalink.StandardOperation
import java.io.BufferedReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
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
                    Pair("Name",allSpliteado?.get(1)),
                    Pair("Asist",allSpliteado?.get(2)),
                    Pair("qualification",CalQualification(allSpliteado))
                )
                alumnadoInfo.add(map)

            }
        }
        return alumnadoInfo
    }
//Una función que reciba una lista de diccionarios como la que devuelve la función anterior y añada a cada diccionario un nuevo par con la nota final del curso. El peso de cada parcial de teoría en la nota final es de un 30% mientras que el peso del examen de prácticas es de un 40%.
    fun CalQualification(allSpliteado: List<String>):String{

        var notaFinal = 0.0


        //primer parcial
        if ((allSpliteado[3].replace(",",".").toDoubleOrNull() ?: 0.0) > (allSpliteado[5].replace(",",".").toDoubleOrNull() ?: 0.0)){
            notaFinal+=CalNoteParcial(allSpliteado[3].replace(",",".").toDoubleOrNull() ?: 0.0)
        }else{
            notaFinal+=CalNoteParcial(allSpliteado[5].replace(",",".").toDoubleOrNull() ?: 0.0)
        }
        //segundo parcial
        if ((allSpliteado[4].replace(",",".").toDoubleOrNull() ?: 0.0) > (allSpliteado[6].replace(",",".").toDoubleOrNull() ?: 0.0)){
            notaFinal+=CalNoteParcial(allSpliteado[4].replace(",",".").toDoubleOrNull() ?: 0.0)
        }else{
            notaFinal+=CalNoteParcial(allSpliteado[6].replace(",",".").toDoubleOrNull() ?: 0.0)
        }
        //Practicas
        if ((allSpliteado[7].replace(",",".").toDoubleOrNull() ?: 0.0) > (allSpliteado[8].replace(",",".").toDoubleOrNull() ?: 0.0)){
            notaFinal+=CalNotePractice(allSpliteado[7].replace(",",".").toDoubleOrNull() ?: 0.0)
        }else{
            notaFinal+=CalNotePractice(allSpliteado[8].replace(",",".").toDoubleOrNull() ?: 0.0)
        }

        return String.format("%.2f", notaFinal)
    }

    fun CalNotePractice(notePractice:Double):Double{
        return notePractice*0.4
    }

    fun CalNoteParcial(noteParcial:Double):Double{

        return noteParcial*0.3

    }


    fun MakeSummary(paht: Path, map: MutableList<MutableMap<String, String?>>) {

        val pahtFinal = paht.resolve("calificacionesFinal.csv")
        if (pahtFinal.notExists()) {
            pahtFinal.createFile()
        }

        var numero = 0
        val bw = Files.newBufferedWriter(pahtFinal)

        bw.use {
            bw.write("pan;cebolla;sepia")
            for (i in map) {
                bw.append()

                numero++
            }

        }
    }
}