package com.ximedes.conto

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ContoApplication

fun main(args: Array<String>) {
    runApplication<ContoApplication>(*args)
}
