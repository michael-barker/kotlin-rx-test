package com.example.controller

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.bind.annotation.RestController

@RestController("curly")
class CurlyBraceTestController() {

  @RequestMapping(value = "hello/{name}", method = arrayOf(POST))
  fun hello(@PathVariable name: String) =  "Hello, $name"

}
