package com.example.controller

import com.example.command.AppService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RelationshipController @Autowired constructor(val appService: AppService) {

    @RequestMapping(value = "relationships")
    fun getRelationships() = appService.getUserRelationships("1111")

    @RequestMapping(value = "relationships/managed/active")
    fun getActiveManagedRelationships() = appService.getActiveManagedUserRelationships("1111")

    @RequestMapping(value = "relationships/managed")
    fun getManagedRelationships() = appService.getManagedUserRelationships("1111")

}