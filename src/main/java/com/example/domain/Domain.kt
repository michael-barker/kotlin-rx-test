package com.example.domain

class AccountInfo(val patientNumber: String)

class PatientProfile(val patientNumber: String)

class PatientRelationship(
    val customerId: String,
    val patientNumber: String,
    val active: Boolean)

class UserRelationship(val relationship: PatientRelationship, val profile: PatientProfile)

class UserRelationships(
    val accountInfo: AccountInfo,
    val managedRelationships: List<UserRelationship>,
    val caregiverRelationships: List<UserRelationship>)

