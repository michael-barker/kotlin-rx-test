package com.example

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class KotlinRxTestApplication {}

fun main(args: Array<String>) {
    SpringApplication.run(KotlinRxTestApplication::class.java, *args)
}