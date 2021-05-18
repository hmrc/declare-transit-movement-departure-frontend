/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package services

import base.{GeneratorSpec, MockServiceApp, SpecBase}
import generators.{JourneyModelGenerators, ModelGenerators}
import models.journeyDomain.GoodsSummary.{GoodSummaryNormalDetailsWithPreLodge, GoodSummaryNormalDetailsWithoutPreLodge, GoodSummarySimplifiedDetails}
import models.journeyDomain.MovementDetails.NormalMovementDetails
import models.journeyDomain.TransportDetails.DetailsAtBorder.{NewDetailsAtBorder, SameDetailsAtBorder}
import models.journeyDomain.TransportDetails.InlandMode.{NonSpecialMode, Rail}
import models.journeyDomain.TransportDetails.ModeCrossingBorder.{ModeExemptNationality, ModeWithNationality}
import models.journeyDomain.{JourneyDomain, JourneyDomainSpec}
import models.messages.InterchangeControlReference
import models.{EoriNumber, LocalReferenceNumber, UserAnswers}
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import repositories.InterchangeControlReferenceIdRepository

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DeclarationRequestServiceSpec
    extends SpecBase
    with MockServiceApp
    with GeneratorSpec
    with JourneyModelGenerators
    with ModelGenerators
    with BeforeAndAfterEach {

  val mockIcrRepository: InterchangeControlReferenceIdRepository = mock[InterchangeControlReferenceIdRepository]
  val mockDateTimeService: DateTimeService                       = mock[DateTimeService]

  val service: DeclarationRequestService = app.injector.instanceOf[DeclarationRequestService]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[InterchangeControlReferenceIdRepository].toInstance(mockIcrRepository))
      .overrides(bind[DateTimeService].toInstance(mockDateTimeService))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockIcrRepository)
    reset(mockDateTimeService)
  }

  def removeConsignor(journeyDomain: JourneyDomain): JourneyDomain = {
    val traderDetails = journeyDomain.traderDetails.copy(consignor = None)
    journeyDomain.copy(traderDetails = traderDetails)
  }

  val journeyDomainNoConignorForAllItems =
    arb[JourneyDomain].map(removeConsignor)

  "convert" - {
    "must return a DeclarationRequest model" - {
      "for a complete journey with all required questions answered" in {
        forAll(arb[UserAnswers], arb[JourneyDomain]) {
          (userAnswers, journeyDomain) =>
            val service = new DeclarationRequestService(mockIcrRepository, mockDateTimeService)

            when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
            when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

            val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(journeyDomain)(userAnswers)
            service.convert(updatedUserAnswer).futureValue.isRight mustBe true
        }
      }

      "secHEA358" - {

        "Pass value for the secHEA358When based on Safety and Security answer" in {

          forAll(arb[UserAnswers], arb[JourneyDomain]) {
            (userAnswers, journeyDomain) =>
              when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
              when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

              val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(journeyDomain)(userAnswers)
              val result            = service.convert(updatedUserAnswer).futureValue

              result.isRight mustBe true

              if (journeyDomain.preTaskList.addSecurityDetails) {
                result.right.value.header.secHEA358 mustBe Some(1)
              } else {
                result.right.value.header.secHEA358 mustBe None
              }
          }
        }
      }

      "cusSubPlaHEA66" - {

        "Normal Journey" - {

          "must be set with customs approved location when not defined as prelodge" in {

            forAll(arb[UserAnswers], arbitraryNormalJourneyDomain, arb[NormalMovementDetails], arb[GoodSummaryNormalDetailsWithoutPreLodge]) {
              (userAnswers, normalJourneyDomain, normalMovementDetails, normalGoodsSummary) =>
                when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
                when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

                val movementDetailsWithoutPrelodge = normalMovementDetails.copy(prelodge                      = false, containersUsed = false)
                val updatedGoodsSummary            = normalJourneyDomain.goodsSummary.copy(goodSummaryDetails = normalGoodsSummary.copy(Some("customsApprovedLocation")))
                val updatedJourneyDomain           = normalJourneyDomain.copy(movementDetails                 = movementDetailsWithoutPrelodge, goodsSummary = updatedGoodsSummary)

                val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(updatedJourneyDomain)(userAnswers)
                val result            = service.convert(updatedUserAnswer).futureValue

                result.right.value.header.cusSubPlaHEA66.value mustBe "customsApprovedLocation"
                result.right.value.header.autLocOfGooCodHEA41 mustBe None
            }
          }

          "must be set with prelodge when defined as prelodge" in {

            forAll(arb[UserAnswers], arbitraryNormalJourneyDomain, arb[NormalMovementDetails]) {
              (userAnswers, normalJourneyDomain, normalMovementDetails) =>
                when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
                when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

                val movementDetailsWithPrelodge = normalMovementDetails.copy(prelodge      = true, containersUsed = false)
                val updatedJourneyDomain        = normalJourneyDomain.copy(movementDetails = movementDetailsWithPrelodge)

                val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(updatedJourneyDomain)(userAnswers)
                val result            = service.convert(updatedUserAnswer).futureValue

                result.right.value.header.cusSubPlaHEA66.value mustBe "Pre-lodge"
                result.right.value.header.autLocOfGooCodHEA41 mustBe None
            }
          }
        }

        "Simplified Journey" - {

          "must not be set" in {

            forAll(arb[UserAnswers], arbitrarySimplifiedJourneyDomain) {
              (userAnswers, simplifiedJourneyDomain) =>
                when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
                when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

                val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(simplifiedJourneyDomain)(userAnswers)
                val result            = service.convert(updatedUserAnswer).futureValue

                result.right.value.header.cusSubPlaHEA66 mustBe None
            }
          }
        }
      }

      "autLocOfGooCodHEA41" - {

        "Simplified Journey" - {

          "must be set with authorised location code" in {

            forAll(arb[UserAnswers], arbitrarySimplifiedJourneyDomain, arb[GoodSummarySimplifiedDetails]) {
              (userAnswers, simplifiedJourneyDomain, goodsSummarySimplifiedDetails) =>
                when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
                when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

                val updatedGoodsSummary  = simplifiedJourneyDomain.goodsSummary.copy(goodSummaryDetails = goodsSummarySimplifiedDetails)
                val updatedJourneyDomain = simplifiedJourneyDomain.copy(goodsSummary                    = updatedGoodsSummary)

                val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(updatedJourneyDomain)(userAnswers)
                val result            = service.convert(updatedUserAnswer).futureValue

                result.right.value.header.autLocOfGooCodHEA41.value mustBe goodsSummarySimplifiedDetails.authorisedLocationCode
                result.right.value.header.cusSubPlaHEA66 mustBe None
            }
          }

        }

        "Normal Journey" - {

          "must not be set" in {

            forAll(arb[UserAnswers], arbitraryNormalJourneyDomain) {
              (userAnswers, normalJourneyDomain) =>
                when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
                when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

                val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(normalJourneyDomain)(userAnswers)
                val result            = service.convert(updatedUserAnswer).futureValue

                result.right.value.header.autLocOfGooCodHEA41 mustBe None
            }
          }
        }
      }

      "identityOfTransportAtCrossing" - {

        val service = new DeclarationRequestService(mockIcrRepository, mockDateTimeService)

        "must return id of crossing when the mode of crossing at the border is a ModeWithNationality " in {

          forAll(arb[UserAnswers], arb[NewDetailsAtBorder], arb[JourneyDomain], arb[ModeWithNationality]) {
            (userAnswers, newDetailsAtBorder, journeyDomain, modeWithNationality) =>
              when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
              when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

              val updatedTransportDetails = journeyDomain.transportDetails
                .copy(detailsAtBorder = newDetailsAtBorder.copy(modeCrossingBorder = modeWithNationality))

              val updatedJourneyDomain = journeyDomain.copy(transportDetails = updatedTransportDetails)

              val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(updatedJourneyDomain)(userAnswers)

              val result = service.convert(updatedUserAnswer).futureValue

              result.right.value.header.transportDetails.ideOfMeaOfTraCroHEA85.value mustBe modeWithNationality.idCrossing
          }
        }

        "must return id of crossing when the mode of crossing at the border is a ModeExemptNationality " in {

          forAll(arb[UserAnswers], arb[NewDetailsAtBorder], arb[JourneyDomain], arb[ModeExemptNationality]) {
            (userAnswers, newDetailsAtBorder, journeyDomain, modeExemptNationality) =>
              when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
              when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

              val updatedTransportDetails = journeyDomain.transportDetails
                .copy(detailsAtBorder = newDetailsAtBorder.copy(modeCrossingBorder = modeExemptNationality))

              val updatedJourneyDomain = journeyDomain.copy(transportDetails = updatedTransportDetails)

              val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(updatedJourneyDomain)(userAnswers)

              val result = service.convert(updatedUserAnswer).futureValue

              result.right.value.header.transportDetails.ideOfMeaOfTraCroHEA85 mustBe None
          }
        }

        "must return id of departure when there are no new details at border and inlandMode is a nonSpecialMode" in {

          forAll(arb[JourneyDomain], arb[NonSpecialMode]) {
            (journeyDomain, nonSpecialMode) =>
              val userAnswers = UserAnswers(LocalReferenceNumber("lrn").value, EoriNumber("1"))
              when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
              when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

              val updatedTransportDetails = journeyDomain.transportDetails.copy(detailsAtBorder = SameDetailsAtBorder, inlandMode = nonSpecialMode)
              val updatedJourneyDomain    = journeyDomain.copy(transportDetails                 = updatedTransportDetails)

              val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(updatedJourneyDomain)(userAnswers)

              val result = service.convert(updatedUserAnswer).futureValue

              result.right.value.header.transportDetails.ideOfMeaOfTraCroHEA85 mustBe nonSpecialMode.departureId
          }
        }

        "must return none when there are no id at departure or crossing" in {

          forAll(arb[JourneyDomain], arb[Rail]) {
            (journeyDomain, rail) =>
              val userAnswers = UserAnswers(LocalReferenceNumber("lrn").value, EoriNumber("1"))
              when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
              when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

              val updatedTransportDetails = journeyDomain.transportDetails.copy(detailsAtBorder = SameDetailsAtBorder, inlandMode = rail)
              val updatedJourneyDomain    = journeyDomain.copy(transportDetails                 = updatedTransportDetails)

              val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(updatedJourneyDomain)(userAnswers)

              val result = service.convert(updatedUserAnswer).futureValue

              result.right.value.header.transportDetails.ideOfMeaOfTraCroHEA85 mustBe None
          }
        }
      }

      "identityOfTransportAtCrossing" - {

        "must return nationality of crossing when there are new details at border and the mode is a mode with nationality" in {

          forAll(arb[UserAnswers], arb[NewDetailsAtBorder], arb[JourneyDomain], arb[ModeWithNationality]) {
            (userAnswers, newDetailsAtBorder, journeyDomain, modeWithNationality) =>
              when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
              when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

              val updatedNewDetailsAtBorder = newDetailsAtBorder.copy(modeCrossingBorder          = modeWithNationality)
              val updatedTransportDetails   = journeyDomain.transportDetails.copy(detailsAtBorder = updatedNewDetailsAtBorder)
              val updatedJourneyDomain      = journeyDomain.copy(transportDetails                 = updatedTransportDetails)

              val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(updatedJourneyDomain)(userAnswers)

              val result = service.convert(updatedUserAnswer).futureValue

              result.right.value.header.transportDetails.natOfMeaOfTraCroHEA87.value mustBe modeWithNationality.nationalityCrossingBorder.code
          }
        }

        "must return None when there are new details at border and the mode is a mode that is exempt from nationality" in {

          forAll(arb[UserAnswers], arb[NewDetailsAtBorder], arb[JourneyDomain], arb[ModeExemptNationality]) {
            (userAnswers, newDetailsAtBorder, journeyDomain, modeExemptNationality) =>
              when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
              when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

              val updatedNewDetailsAtBorder = newDetailsAtBorder.copy(modeCrossingBorder          = modeExemptNationality)
              val updatedTransportDetails   = journeyDomain.transportDetails.copy(detailsAtBorder = updatedNewDetailsAtBorder)
              val updatedJourneyDomain      = journeyDomain.copy(transportDetails                 = updatedTransportDetails)

              val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(updatedJourneyDomain)(userAnswers)

              val result = service.convert(updatedUserAnswer).futureValue

              result.right.value.header.transportDetails.natOfMeaOfTraCroHEA87 mustBe None
          }
        }

        "must return nationality of departure when there are no new details at border and the mode is NonSpecialMode" in {

          forAll(arb[UserAnswers], arb[JourneyDomain], arb[NonSpecialMode]) {
            (userAnswers, journeyDomain, nonSpecialMode) =>
              when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
              when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

              val updatedTransportDetails = journeyDomain.transportDetails.copy(detailsAtBorder = SameDetailsAtBorder, inlandMode = nonSpecialMode)
              val updatedJourneyDomain    = journeyDomain.copy(transportDetails                 = updatedTransportDetails)

              val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(updatedJourneyDomain)(userAnswers)

              val result = service.convert(updatedUserAnswer).futureValue

              result.right.value.header.transportDetails.natOfMeaOfTraCroHEA87.get mustBe nonSpecialMode.nationalityAtDeparture.get.code
          }
        }

        "must return None when there are no new details at border and the mode is Rail" in {

          forAll(arb[UserAnswers], arb[JourneyDomain], arb[Rail]) {
            (userAnswers, journeyDomain, rail) =>
              when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
              when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

              val updatedTransportDetails = journeyDomain.transportDetails.copy(detailsAtBorder = SameDetailsAtBorder, inlandMode = rail)
              val updatedJourneyDomain    = journeyDomain.copy(transportDetails                 = updatedTransportDetails)

              val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(updatedJourneyDomain)(userAnswers)

              val result = service.convert(updatedUserAnswer).futureValue

              result.right.value.header.transportDetails.natOfMeaOfTraCroHEA87 mustBe None
          }
        }
      }

      "goodsSummaryDetails" - {
        "must populate controlResult and authorisedLocationOfGoods when Simplified" in {

          forAll(arb[UserAnswers], arbitrarySimplifiedJourneyDomain) {
            (userAnswers, journeyDomain) =>
              when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
              when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

              val updatedUserAnswer: UserAnswers = JourneyDomainSpec.setJourneyDomain(journeyDomain)(userAnswers)

              val result = service.convert(updatedUserAnswer).futureValue.right.value

              result.controlResult must be(defined)
              result.header.autLocOfGooCodHEA41 must be(defined)
          }
        }

        "must not populate controlResult and authorisedLocationOfGoods when Normal" in {

          forAll(arb[UserAnswers], arbitraryNormalJourneyDomain) {
            (userAnswers, journeyDomain) =>
              when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
              when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

              val updatedUserAnswer: UserAnswers = JourneyDomainSpec.setJourneyDomain(journeyDomain)(userAnswers)

              val result = service.convert(updatedUserAnswer).futureValue.right.value

              result.controlResult must not be defined
              result.header.autLocOfGooCodHEA41 must not be defined
          }
        }
      }

      "traderConsignor" - {
        "is defined when the user has provided a consignor for all items" in {
          forAll(arb[UserAnswers], arb[JourneyDomain]) {
            (userAnswers, journeyDomain) =>
              whenever(journeyDomain.traderDetails.consignor.isDefined) {
                val service = new DeclarationRequestService(mockIcrRepository, mockDateTimeService)

                when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
                when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

                val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(journeyDomain)(userAnswers)
                val result            = service.convert(updatedUserAnswer).futureValue.right.value
                result.traderConsignor must be(defined)
              }

          }
        }

        "is not defined when the user has not provided a consignor for all items" in {
          forAll(arb[UserAnswers], journeyDomainNoConignorForAllItems) {
            (userAnswers, journeyDomain) =>
              val service = new DeclarationRequestService(mockIcrRepository, mockDateTimeService)

              when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
              when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

              val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(journeyDomain)(userAnswers)
              val result            = service.convert(updatedUserAnswer).futureValue.right.value
              result.traderConsignor must not be defined
          }
        }
      }

    }

    "must fail when there are missing answers from mandatory pages" in {
      val service = new DeclarationRequestService(mockIcrRepository, mockDateTimeService)

      when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
      when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

      service.convert(emptyUserAnswers).futureValue.isLeft mustBe true
    }

  }

}
