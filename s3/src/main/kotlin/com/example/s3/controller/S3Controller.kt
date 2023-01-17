package com.example.s3.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class S3Controller {
    @GetMapping("/api/s3/{bucket}")
    fun get(@PathVariable bucket: String): ResponseEntity<String> {
        return ResponseEntity<String>(null, HttpStatus.OK)
    }

    @PostMapping("/api/s3/{bucket}")
    fun save(@PathVariable bucket: String): ResponseEntity<Void> {
        return ResponseEntity<Void>(HttpStatus.OK)
    }

    @DeleteMapping("/api/s3/{bucket}")
    fun delete(@PathVariable bucket: String): ResponseEntity<Void> {
        return ResponseEntity<Void>(HttpStatus.OK)
    }


}