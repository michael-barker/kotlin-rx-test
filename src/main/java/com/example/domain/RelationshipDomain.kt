package com.example.domain

class AccountInfo(val patientNumber: String)

class PatientProfile(val patientNumber: String, val name: String)

class CustomerProfile(val patientNumber: String, val name: String)

class PatientRelationship(
    val customerId: String,
    val patientNumber: String,
    val active: Boolean)

class UserRelationship private constructor(relationship: PatientRelationship) {
  val patientNumber: String?
  val active: Boolean
  var name: String? = null

  init {
    patientNumber = relationship.patientNumber
    active = relationship.active
  }

  constructor(relationship: PatientRelationship, patientProfile: PatientProfile?) : this(relationship) {
    this.name = patientProfile?.name
  }

  constructor(relationship: PatientRelationship, customerProfile: CustomerProfile?) : this(relationship) {
    this.name = customerProfile?.name
  }
}

class UserRelationships(
    val accountInfo: AccountInfo,
    val managedRelationships: List<UserRelationship>,
    val caregiverRelationships: List<UserRelationship>)

