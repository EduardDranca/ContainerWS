package com.eddranca.containerws.controller

import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class SomeController {
    @GetMapping("/")
    fun getSome(@RequestHeader headers: HttpHeaders): String {
        return "Some"
    }
}
