package com.example.controller

import com.example.command.AppService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.bind.annotation.RestController

@RestController
class CurlyBraceTestController @Autowired constructor(val appService: AppService) {

  @RequestMapping(value = "hello/{name}", method = arrayOf(GET, POST))
  fun hello(@PathVariable name: String) =  "Hello, $name"

}
