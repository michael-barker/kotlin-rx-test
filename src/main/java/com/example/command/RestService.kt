package com.example.command

import com.example.domain.AccountInfo
import com.example.domain.CustomerProfile
import com.example.domain.PatientProfile
import com.example.domain.PatientRelationship
import org.springframework.stereotype.Service
import rx.Observable
import rx.Observable.just

@Service
open class RestService() {
  fun getAccountInfo(customerId: String): Observable<AccountInfo> = just(AccountInfo(customerId))

  fun getPatientProfiles(patientNumbers: List<String>): Observable<PatientProfile> =
      just(patientNumbers.map { PatientProfile(it, "$it name") }).flatMap { Observable.from(it) }

  fun getCustomerProfile(customerId: String) = CustomerProfile(customerId, "$customerId name")

  fun getManagedRelationships(customerId: String): Observable<PatientRelationship> =
      just(listOf(
          PatientRelationship(customerId, "1234", true),
          PatientRelationship(customerId, "4321", true),
          PatientRelationship(customerId, "0987", false)))
          .flatMap { Observable.from(it) }

  fun getCaregiverRelationships(customerId: String): Observable<PatientRelationship> =
      just(listOf(PatientRelationship("0987", "1111", true)))
          .flatMap { Observable.from(it) }
}