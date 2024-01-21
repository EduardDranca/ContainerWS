package com.eddranca.containerws.controller

import com.eddranca.containerws.service.UserService
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class SomeController(val userService: UserService) {
    @GetMapping("/some")
    fun getSome(@RequestHeader headers: HttpHeaders): String {
        return "Some"
    }
}
