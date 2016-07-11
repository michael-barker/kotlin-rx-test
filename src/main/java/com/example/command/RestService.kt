package com.example.command

import com.example.domain.AccountInfo
import com.example.domain.PatientProfile
import com.example.domain.PatientRelationship
import org.springframework.stereotype.Service
import rx.Observable
import rx.Observable.just

@Service
open class RestService() {
  fun getAccountInfo(customerId: String): Observable<AccountInfo> = just(AccountInfo("1111"))

  fun getPatientProfiles(patientNumbers: List<String>): Observable<List<PatientProfile>> =
      just(patientNumbers.map { PatientProfile(it) }.toList())

  fun getManagedRelationships(customerId: String): Observable<List<PatientRelationship>> =
      just(listOf(
          PatientRelationship("1111", "1234", true),
          PatientRelationship("1111", "4321", true),
          PatientRelationship("1111", "0987", false)))

  fun getCaregiverRelationships(customerId: String): Observable<List<PatientRelationship>> =
      just(listOf(PatientRelationship("0987", "1111", true)))
}