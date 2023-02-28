package com.example.blogdb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BlogDbApplication

fun main(args: Array<String>) {
    runApplication<BlogDbApplication>(*args)
}
