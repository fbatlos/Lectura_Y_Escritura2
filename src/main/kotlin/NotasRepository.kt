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
                    Pair("Asist",allSpliteado?.get(2)?.replace("%","")),
                    Pair("Qualification",CalQualification(allSpliteado))
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


    fun MakeApproved(paht: Path, map: MutableList<MutableMap<String, String?>>) {

        val aprabados :MutableList<MutableMap<String, String?>> = mutableListOf()
        val suspensos :MutableList<MutableMap<String, String?>> = mutableListOf()

        for (alum in map){
            if ((alum.get("Qualification")?.toDoubleOrNull() ?: 0.0) > 5.0 && ((alum.get("Asist")?.toIntOrNull())?: 0) >= 75){
                aprabados.add(alum)
            }else{
                suspensos.add(alum)
            }
        }
        WriteApproved(paht,aprabados)
        WriteSuspend(paht,suspensos)
    }

    fun WriteApproved(paht: Path,aprabados :MutableList<MutableMap<String, String?>>){
        val pahtFinal = paht.resolve("AlumnosAprobados.csv")
        if (pahtFinal.notExists()) {
            pahtFinal.createFile()
        }

        val bw = Files.newBufferedWriter(pahtFinal,StandardOpenOption.APPEND)

        bw.use {
            bw.write("Apellido;Nombre;asistencia;Nota")
            for (alum in aprabados) {
                bw.newLine()
                bw.append("${alum.get("Apell")};${alum.get("Name")};${alum.get("Asist")};${alum.get("Qualification")};")

            }

        }
    }

    fun WriteSuspend(paht: Path,aprabados :MutableList<MutableMap<String, String?>>){
        val pahtFinal = paht.resolve("AlumnosSuspendidos.csv")
        if (pahtFinal.notExists()) {
            pahtFinal.createFile()
        }

        val bw = Files.newBufferedWriter(pahtFinal,StandardOpenOption.APPEND)

        bw.use {
            bw.write("Apellido;Nombre;asistencia;Nota")
            for (alum in aprabados) {
                bw.newLine()
                bw.append("${alum.get("Apell")};${alum.get("Name")};${alum.get("Asist")};${alum.get("Qualification")};")

            }

        }
    }
}