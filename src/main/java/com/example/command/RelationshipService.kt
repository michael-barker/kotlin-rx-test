package com.example.command

import com.example.domain.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.Observable

@Service
open class RelationshipService @Autowired constructor(val restService: RestService) {

  val noCaregiverRelationships: Observable<UserRelationship> = Observable.from(listOf<UserRelationship>())

  fun getManagedUserRelationships(customerId: String) =
      toUserRelationships(
          getAccountInfo(customerId),
          getManagedRelationships(customerId),
          noCaregiverRelationships)

  fun getActiveManagedUserRelationships(customerId: String) =
      toUserRelationships(
          getAccountInfo(customerId),
          getActiveManagedRelationships(customerId),
          noCaregiverRelationships)

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

  private fun getAccountInfo(customerId: String) = restService.getAccountInfo(customerId)

  private fun getManagedRelationships(customerId: String): Observable<UserRelationship> =
      restService.getManagedRelationships(customerId)
          .toList()
          .flatMap { relationships -> managedRelationshipsToUserRelationships(relationships) }

  private fun getCaregivers(customerId: String) =
      restService.getCaregiverRelationships(customerId).flatMap { relationship ->
        restService.getCustomerProfile(relationship.customerId).map { customerProfile ->
          UserRelationship(relationship, customerProfile)
        }
      }

  private fun getActiveManagedRelationships(customerId: String) =
      getManagedRelationships(customerId).filter { it.active }

  private fun managedRelationshipsToUserRelationships(relationships: List<PatientRelationship>)
      : Observable<UserRelationship> {
    val patientNumbers = relationships.map { it.patientNumber }.toList()
    return restService.getPatientProfiles(patientNumbers)
        .toList()
        .map { profiles ->
          relationships.map { relationship ->
            UserRelationship(relationship, getMatchingProfile(relationship.patientNumber, profiles))
          }
        }.flatMap { Observable.from(it) }
  }

  private fun getMatchingProfile(customerId: String, profiles: List<PatientProfile>) =
      profiles.find { it.patientNumber == customerId }

}