package com.example.command

import com.example.domain.AccountInfo
import com.example.domain.PatientProfile
import com.example.domain.UserRelationship
import com.example.domain.UserRelationships
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.Observable

@Service
open class AppService @Autowired constructor(val restService: RestService) {

  fun getManagedUserRelationships(customerId: String) =
      toUserRelationships(
          getAccountInfo(customerId),
          getManagedRelationships(customerId),
          emptyUserRelationshipList())

  private fun emptyUserRelationshipList() = Observable.just(listOf<UserRelationship>()).flatMap { Observable.from(it) }

  fun getActiveManagedUserRelationships(customerId: String) =
      toUserRelationships(
          getAccountInfo(customerId),
          getActiveManagedRelationships(customerId),
          emptyUserRelationshipList())

  fun getUserRelationships(customerId: String) =
      toUserRelationships(
          getAccountInfo(customerId),
          getManagedRelationships(customerId),
          getCaregivers(customerId))

  private fun toUserRelationships(
      accountInfo: Observable<AccountInfo>,
      managedRelationships: Observable<UserRelationship>,
      caregivers: Observable<UserRelationship>)
      : UserRelationships =
      Observable.zip(
          accountInfo,
          managedRelationships.toList(),
          caregivers.toList(),
          ::UserRelationships)
          .toBlocking()
          .first()

  private fun getAccountInfo(customerId: String) =
      restService.getAccountInfo(customerId)

  private fun getManagedRelationships(customerId: String) =
      restService.getManagedRelationships(customerId)
          .toList()
          .flatMap { relationships ->
            val patientNumbers = relationships.map { it.patientNumber }.toList()
            restService.getPatientProfiles(patientNumbers)
                .toList()
                .map { profiles ->
                  relationships.map {
                    UserRelationship(it, getMatchingProfile(it.patientNumber, profiles))
                  }
                }
          }.flatMap { Observable.from(it) }

  private fun getCaregivers(customerId: String) =
      restService.getCaregiverRelationships(customerId).map {
        val customerProfile = restService.getCustomerProfile(it.customerId)
        UserRelationship(it, customerProfile)
      }

  private fun getActiveManagedRelationships(customerId: String) =
      getManagedRelationships(customerId).filter { it.active }

  private fun getMatchingProfile(customerId: String, profiles: List<PatientProfile>) =
      profiles.find { it.patientNumber == customerId }

}