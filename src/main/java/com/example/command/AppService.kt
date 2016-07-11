package com.example.command

import com.example.domain.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import rx.Observable

@Service
open class AppService @Autowired constructor(val restService: RestService) {

  fun getManagedRelationships(customerId: String) =
      toUserRelationships(customerId, restService.getManagedRelationships(customerId))

  fun getActiveManagedRelationships(customerId: String) =
      toUserRelationships(customerId, getActiveManagedPatientRelationships(customerId))

  fun getUserRelationships(customerId: String) =
      toUserRelationships(customerId, getAllPatientRelationships(customerId))

  fun toUserRelationships(customerId: String, relationships: Observable<List<PatientRelationship>>)
      : UserRelationships =
      relationships.flatMap { zipWithProfilesAndAccountInfo(customerId, it) }
          .map {
            val (relations, profiles, accountInfo) = it
            toUserRelationships(customerId, accountInfo, relations, profiles)
          }
          .toBlocking()
          .first()

  private fun getAllPatientRelationships(customerId: String) =
      Observable.merge(
          restService.getManagedRelationships(customerId),
          restService.getCaregiverRelationships(customerId))
          .toList()
          .map { it.flatten() }

  private fun getActiveManagedPatientRelationships(customerId: String) =
      restService.getManagedRelationships(customerId)
          .flatMap { Observable.from(it) }
          .filter { it.active }
          .toList()

  private fun zipWithProfilesAndAccountInfo(customerId: String, relationships: List<PatientRelationship>):
      Observable<Triple<List<PatientRelationship>, List<PatientProfile>, AccountInfo>> {
    val profiles = getProfiles(relationships)
    val accountInfo = restService.getAccountInfo(customerId)

    return Observable.zip(Observable.just(relationships), profiles, accountInfo) {
      relationships, profiles, accountInfo ->
      Triple(relationships, profiles, accountInfo)
    }
  }

  private fun getProfiles(relationships: List<PatientRelationship>) =
      restService.getPatientProfiles(relationships.map { it.patientNumber })

  private fun toUserRelationships(
      customerId: String,
      accountInfo: AccountInfo,
      relationships: List<PatientRelationship>,
      profiles: List<PatientProfile>)
      : UserRelationships {
    val relationshipsGroupedByManaged = groupRelationshipsByManaged(customerId, relationships)

    val managedRelationships = relationshipsGroupedByManaged[true]?.map {
      val profile = getMatchingProfile(customerId, profiles)
      mapToUserRelationship(it, profile!!)
    } ?: listOf()

    val caregiverRelationships = relationshipsGroupedByManaged[false]?.map {
      val profile = getMatchingProfile(customerId, profiles)
      mapToUserRelationship(it, profile!!)
    } ?: listOf()

    return UserRelationships(accountInfo, managedRelationships, caregiverRelationships)
  }

  private fun groupRelationshipsByManaged(customerId: String, relationships: List<PatientRelationship>)
      : Map<Boolean, List<PatientRelationship>> =
      relationships.groupBy { it.customerId == customerId }

  private fun mapToUserRelationship(relationship: PatientRelationship, profile: PatientProfile) =
      UserRelationship(relationship, profile)

  private fun getMatchingProfile(customerId: String, profiles: List<PatientProfile>) =
      profiles.find { it.patientNumber == customerId }

}