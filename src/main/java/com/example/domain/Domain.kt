package com.example.domain

class AccountInfo(val patientNumber: String)

class PatientProfile(val patientNumber: String)

class PatientRelationship(
    val customerId: String,
    val patientNumber: String,
    val active: Boolean)

class UserRelationship(val profile: PatientProfile, val relationship: PatientRelationship)

class UserRelationships(
    val accountInfo: AccountInfo,
    val managedRelationships: List<UserRelationship>,
    val caregiverRelationships: List<UserRelationship>)

