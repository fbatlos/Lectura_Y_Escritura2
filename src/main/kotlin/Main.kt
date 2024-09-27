package org.example

import org.example.repository.CotizacionRepository
import java.nio.file.Path

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val cotiza = CotizacionRepository()
    val path = Path.of("src").resolve("main").resolve("kotlin").resolve("ficheros")
    cotiza.ObtenerInfo(path).forEach { println(it)  }
    cotiza.MakeApproved(path,cotiza.ObtenerInfo(path))

}