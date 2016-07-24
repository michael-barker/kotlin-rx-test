package com.example.command

import com.example.KotlinRxTestApplication
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner::class)
@SpringApplicationConfiguration(KotlinRxTestApplication::class)
open class AppServiceTest() {

  @Autowired
  lateinit var appService: AppService

  @Test
  fun getUserRelationships() {
    val userRelationships = appService.getUserRelationships("1111")

    assert(userRelationships.managedRelationships.size == 3)
    assert(userRelationships.caregiverRelationships.size == 1)
  }

  @Test
  fun getActiveManagedRelationships() {
    val userRelationships = appService.getActiveManagedUserRelationships("1111")

    assert(userRelationships.managedRelationships.size == 2)
    assert(userRelationships.caregiverRelationships.size == 0)
  }

  @Test
  fun getManagedRelationships() {
    val userRelationships = appService.getManagedUserRelationships("1111")

    assert(userRelationships.managedRelationships.size == 3)
    assert(userRelationships.caregiverRelationships.size == 0)
  }

}