package com.example.command

import com.example.domain.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.Observable

@Service
open class RelationshipService @Autowired constructor(val relationshipService: RestService) {

  val noCaregiverRelationships: Observable<UserRelationship> = Observable.from(listOf<UserRelationship>())

  fun getManagedUserRelationships(customerId: String): UserRelationships =
      toUserRelationships(
          getAccountInfo(customerId),
          getManagedRelationships(customerId),
          noCaregiverRelationships)

  fun getActiveManagedUserRelationships(customerId: String): UserRelationships =
      toUserRelationships(
          getAccountInfo(customerId),
          getActiveManagedRelationships(customerId),
          noCaregiverRelationships)

  fun getUserRelationships(customerId: String): UserRelationships =
      toUserRelationships(
          getAccountInfo(customerId),
          getManagedRelationships(customerId),
          getCaregivers(customerId))

  private fun toUserRelationships(
      accountInfo: Observable<AccountInfo>,
      managedRelationships: Observable<UserRelationship>,
      caregivers: Observable<UserRelationship>) =
      Observable.zip(
          accountInfo,
          managedRelationships.toList(),
          caregivers.toList(),
          ::UserRelationships)
          .toBlocking()
          .first()

  private fun getAccountInfo(customerId: String) = relationshipService.getAccountInfo(customerId)

  private fun getManagedRelationships(customerId: String): Observable<UserRelationship> =
      relationshipService.getManagedRelationships(customerId)
          .toList()
          .flatMap { managedRelationshipsToUserRelationships(it) }

  private fun getActiveManagedRelationships(customerId: String) =
      getManagedRelationships(customerId).filter { it.active }

  private fun managedRelationshipsToUserRelationships(relationships: List<PatientRelationship>)
      : Observable<UserRelationship> {
    val patientNumbers = relationships.map { it.patientNumber }.toList()
    return relationshipService.getPatientProfiles(patientNumbers)
        .toList()
        .map { profiles ->
          relationships.map { relationship ->
            UserRelationship(relationship, getMatchingProfile(relationship.patientNumber, profiles))
          }
        }.flatMap { Observable.from(it) }
  }

  private fun getCaregivers(customerId: String) =
      relationshipService.getCaregiverRelationships(customerId)
          .flatMap { caregiverToUserRelationship(it) }

  private fun caregiverToUserRelationship(relationship: PatientRelationship) =
      getCustomerProfile(relationship.customerId)
          .map { UserRelationship(relationship, it) }

  private fun getCustomerProfile(customerId: String) = relationshipService.getCustomerProfile(customerId)

  private fun getMatchingProfile(customerId: String, profiles: List<PatientProfile>) =
      profiles.find { it.patientNumber == customerId }

}