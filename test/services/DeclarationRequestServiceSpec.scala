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
import commonTestUtils.UserAnswersSpecHelper
import generators.UserAnswersGenerator
import models.journeyDomain.GoodsSummary.GoodSummarySimplifiedDetails
import models.journeyDomain.{JourneyDomain, RouteDetailsLongJourney}
import models.journeyDomain.TransportDetails.InlandMode.Rail
import models.messages.InterchangeControlReference
import models.messages.trader.TraderPrincipalWithEori
import models.reference.{Country, CountryCode}
import models.userAnswerScenarios.{Scenario1, Scenario7}
import models.{CommonAddress, Index}
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Gen
import org.scalatest.BeforeAndAfterEach
import pages.movementDetails.PreLodgeDeclarationPage
import pages.{IsPrincipalEoriKnownPage, PrincipalAddressPage, PrincipalNamePage, WhatIsPrincipalEoriPage, _}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import repositories.InterchangeControlReferenceIdRepository
import java.time.LocalDateTime

import cats.data.NonEmptyList
import models.journeyDomain.RouteDetailsLongJourney.TransitInformation
import models.messages.customsoffice.CustomsOfficeTransit

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DeclarationRequestServiceSpec
    extends SpecBase
    with MockServiceApp
    with GeneratorSpec
    with UserAnswersGenerator
    with BeforeAndAfterEach
    with UserAnswersSpecHelper {

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

  "convert" - {
    "must return a DeclarationRequest model" - {
      "for a complete journey with all required questions answered" in {

        forAll(genUserAnswerScenario) {
          userAnswerScenario =>
            val service = new DeclarationRequestService(mockIcrRepository, mockDateTimeService)

            when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
            when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

            service.convert(userAnswerScenario.userAnswers).futureValue.isRight mustBe true
        }
      }

      "secHEA358" - {

        "Pass value for the secHEA358When based on Safety and Security answer" in {

          forAll(genUserAnswerScenario) {
            userAnswerScenario =>
              val service = new DeclarationRequestService(mockIcrRepository, mockDateTimeService)

              when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
              when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

              val result = service.convert(userAnswerScenario.userAnswers).futureValue

              result.isRight mustBe true

              if (userAnswerScenario.toModel.preTaskList.addSecurityDetails) {
                result.right.value.header.secHEA358 mustBe Some(1)
              } else {
                result.right.value.header.secHEA358 mustBe None
              }
          }
        }
      }

      "TotGroMasHEA307" - {
        "Pass the correct value when it has already been answers in Total Gross Mass Page" in {

          val service = new DeclarationRequestService(mockIcrRepository, mockDateTimeService)

          when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
          when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

          val updatedUserAnswer = Scenario1.userAnswers
            .unsafeSetVal(ItemTotalGrossMassPage(Index(0)))(100.123)
            .unsafeSetVal(ItemTotalGrossMassPage(Index(1)))(200.123)

          val result = service.convert(updatedUserAnswer).futureValue

          result.right.value.header.totGroMasHEA307 mustBe "300.246"
        }
      }

      "Normal Journey without Prelodge" - {

        "cusSubPlaHEA66" - {
          "must be set with customs approved location when not defined as prelodge and and CustomsApproved Location is added" in {

            forAll(genNormalScenarios) {
              userAnswerScenario =>
                val service = new DeclarationRequestService(mockIcrRepository, mockDateTimeService)

                when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
                when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

                val userAnswers = userAnswerScenario.userAnswers
                  .unsafeSetVal(PreLodgeDeclarationPage)(false)
                  .unsafeSetVal(ContainersUsedPage)(false)
                  .unsafeSetVal(AddCustomsApprovedLocationPage)(true)
                  .unsafeSetVal(CustomsApprovedLocationPage)("customsApprovedLocation")

                val result = service.convert(userAnswers).futureValue

//                result mustBe "customsApprovedLocation"

                result.right.value.header.cusSubPlaHEA66.value mustBe "customsApprovedLocation"
                result.right.value.header.autLocOfGooCodHEA41 mustBe None
                result.right.value.header.agrLocOfGooHEA39 mustBe None
                result.right.value.header.agrLocOfGooCodHEA38 mustBe None
            }
          }

          "must be None when not defined as prelodge and no CustomsApproved Location is added" in {

            forAll(genNormalScenarios) {
              userAnswerScenario =>
                val service = new DeclarationRequestService(mockIcrRepository, mockDateTimeService)

                when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
                when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

                val userAnswers = userAnswerScenario.userAnswers
                  .unsafeSetVal(PreLodgeDeclarationPage)(false)
                  .unsafeSetVal(AddAgreedLocationOfGoodsPage)(false)
                  .unsafeSetVal(ContainersUsedPage)(false)
                  .unsafeSetVal(AddCustomsApprovedLocationPage)(false)

                val result = service.convert(userAnswers).futureValue

                result.right.value.header.cusSubPlaHEA66 mustBe None
                result.right.value.header.autLocOfGooCodHEA41 mustBe None
                result.right.value.header.agrLocOfGooHEA39 mustBe None
                result.right.value.header.agrLocOfGooCodHEA38 mustBe None
            }
          }
        }

        "agrLocOfGooHEA39" - {

          "must be None when not defined as prelodge and no CustomsApproved Location is added and there is no Agreed Location of Goods" in {

            forAll(genNormalScenarios) {
              userAnswerScenario =>
                val service = new DeclarationRequestService(mockIcrRepository, mockDateTimeService)

                when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
                when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

                val userAnswers = userAnswerScenario.userAnswers
                  .unsafeSetVal(PreLodgeDeclarationPage)(false)
                  .unsafeSetVal(ContainersUsedPage)(false)
                  .unsafeSetVal(AddCustomsApprovedLocationPage)(false)
                  .unsafeSetVal(AddAgreedLocationOfGoodsPage)(false)

                val result = service.convert(userAnswers).futureValue

                result.right.value.header.cusSubPlaHEA66 mustBe None
                result.right.value.header.autLocOfGooCodHEA41 mustBe None
                result.right.value.header.agrLocOfGooHEA39 mustBe None
                result.right.value.header.agrLocOfGooCodHEA38 mustBe None
            }
          }

          "must be populated when not defined as prelodge and no CustomsApproved Location is added and there is an Agreed Location of Goods" in {

            forAll(genNormalScenarios) {
              userAnswerScenario =>
                val service = new DeclarationRequestService(mockIcrRepository, mockDateTimeService)

                when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
                when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

                val userAnswers = userAnswerScenario.userAnswers
                  .unsafeSetVal(PreLodgeDeclarationPage)(false)
                  .unsafeSetVal(ContainersUsedPage)(false)
                  .unsafeSetVal(AddCustomsApprovedLocationPage)(false)
                  .unsafeSetVal(AddAgreedLocationOfGoodsPage)(true)
                  .unsafeSetVal(AgreedLocationOfGoodsPage)("Agreed location of Goods")

                val result = service.convert(userAnswers).futureValue

                result.right.value.header.cusSubPlaHEA66 mustBe None
                result.right.value.header.autLocOfGooCodHEA41 mustBe None
                result.right.value.header.agrLocOfGooHEA39.value mustBe "Agreed location of Goods"
                result.right.value.header.agrLocOfGooCodHEA38 mustBe None
            }
          }
        }

        "agrLocOfGooCodHEA38" - {

          "must be defined as 'Pre-lodge' when there is prelodge" in {
            forAll(genNormalScenarios) {
              userAnswerScenario =>
                val service = new DeclarationRequestService(mockIcrRepository, mockDateTimeService)

                when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
                when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

                val userAnswers = userAnswerScenario.userAnswers
                  .unsafeSetVal(PreLodgeDeclarationPage)(true)
                  .unsafeSetVal(ContainersUsedPage)(false)
                  .unsafeSetVal(AddAgreedLocationOfGoodsPage)(false)

                val result = service.convert(userAnswers).futureValue

                result.right.value.header.agrLocOfGooCodHEA38.value mustBe "Pre-lodge"
            }
          }

          "must not be defined as 'Pre-lodge' when there is no prelodge" in {

            forAll(genNormalScenarios) {
              userAnswerScenario =>
                val service = new DeclarationRequestService(mockIcrRepository, mockDateTimeService)

                when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
                when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

                val userAnswers = userAnswerScenario.userAnswers
                  .unsafeSetVal(PreLodgeDeclarationPage)(false)
                  .unsafeSetVal(AddAgreedLocationOfGoodsPage)(false)
                  .unsafeSetVal(ContainersUsedPage)(false)
                  .unsafeSetVal(AddCustomsApprovedLocationPage)(false)

                val result = service.convert(userAnswers).futureValue

                result.right.value.header.agrLocOfGooCodHEA38 mustBe None
            }
          }
        }

        "Simplified Journey" - {

          "cusSubPlaHEA66,agrLocOfGooHEA39 and agrLocOfGooCodHEA38  must not be set" in {

            forAll(genSimplifiedScenarios) {
              userAnswerScenario =>
                when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
                when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

                val result = service.convert(userAnswerScenario.userAnswers).futureValue

                result.right.value.header.cusSubPlaHEA66 mustBe None
                result.right.value.header.agrLocOfGooHEA39 mustBe None
                result.right.value.header.agrLocOfGooCodHEA38 mustBe None
            }
          }
        }
      }

      "autLocOfGooCodHEA41" - {

        "Simplified Journey" - {

          "must be set with authorised location code" in {

            forAll(genSimplifiedScenarios) {
              userAnswerScenario =>
                when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
                when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

                val result = service.convert(userAnswerScenario.userAnswers).futureValue

                val expectedResult =
                  userAnswerScenario.toModel.goodsSummary.goodSummaryDetails.asInstanceOf[GoodSummarySimplifiedDetails].authorisedLocationCode

                result.right.value.header.autLocOfGooCodHEA41.value mustBe expectedResult
                result.right.value.header.cusSubPlaHEA66 mustBe None
            }
          }
        }

        "Normal Journey" - {

          "must not be set" in {

            forAll(genNormalScenarios) {
              userAnswerScenario =>
                when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
                when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

                val result = service.convert(userAnswerScenario.userAnswers).futureValue

                result.right.value.header.autLocOfGooCodHEA41 mustBe None
            }
          }
        }
      }

      "identityOfTransportAtCrossing" - {

        val service = new DeclarationRequestService(mockIcrRepository, mockDateTimeService)

        "must return id of crossing when the mode of crossing at the border is a ModeWithNationality" in {

          forAll(genUserAnswerScenario) {
            userAnswerScenario =>
              when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
              when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

              val userAnswers = userAnswerScenario.userAnswers
                .unsafeSetVal(ChangeAtBorderPage)(true)
                .unsafeSetVal(ModeAtBorderPage)("1")
                .unsafeSetVal(ModeCrossingBorderPage)("1")
                .unsafeSetVal(NationalityCrossingBorderPage)(CountryCode("GB"))
                .unsafeSetVal(IdCrossingBorderPage)("idCrossingBorder")

              val result = service.convert(userAnswers).futureValue

              result.right.value.header.transportDetails.ideOfMeaOfTraCroHEA85.value mustBe "idCrossingBorder"
          }
        }

        "must return id of crossing when the mode of crossing at the border is a ModeExemptNationality " in {

          forAll(genUserAnswerScenario) {
            userAnswerScenario =>
              when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
              when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

              val userAnswers = userAnswerScenario.userAnswers
                .unsafeSetVal(ChangeAtBorderPage)(true)
                .unsafeSetVal(ModeAtBorderPage)("2")
                .unsafeSetVal(ModeCrossingBorderPage)("2")

              val result = service.convert(userAnswers).futureValue

              result.right.value.header.transportDetails.ideOfMeaOfTraCroHEA85 mustBe None
          }
        }

        "must return id of departure when there are no new details at border and inlandMode is a nonSpecialMode" in {

          forAll(genUserAnswerScenario) {
            userAnswerScenario =>
              when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
              when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

              val userAnswers = userAnswerScenario.userAnswers
                .unsafeSetVal(ChangeAtBorderPage)(false)
                .unsafeSetVal(InlandModePage)("1")
                .unsafeSetVal(NationalityAtDeparturePage)(CountryCode("GB"))
                .unsafeSetVal(IdAtDeparturePage)("idAtDeparture")

              val result = service.convert(userAnswers).futureValue

              result.right.value.header.transportDetails.ideOfMeaOfTraCroHEA85 mustBe Some("idAtDeparture")
          }
        }

        "must return none when there are no id at departure or crossing" in {

          forAll(genUserAnswerScenario) {
            userAnswerScenario =>
              when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
              when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

              val userAnswers = userAnswerScenario.userAnswers
                .unsafeSetVal(ChangeAtBorderPage)(false)
                .unsafeSetVal(InlandModePage)("1")
                .unsafeSetVal(NationalityAtDeparturePage)(CountryCode("GB"))
                .unsafeRemove(IdAtDeparturePage)

              val result = service.convert(userAnswers).futureValue

              result.right.value.header.transportDetails.ideOfMeaOfTraCroHEA85 mustBe None
          }
        }
      }

      "identityOfTransportAtCrossing" - {

        "must return nationality of crossing when there are new details at border and the mode is a mode with nationality" in {

          forAll(genUserAnswerScenario) {
            userAnswerScenario =>
              when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
              when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

              val userAnswers = userAnswerScenario.userAnswers
                .unsafeSetVal(ChangeAtBorderPage)(true)
                .unsafeSetVal(InlandModePage)("1")
                .unsafeSetVal(ModeCrossingBorderPage)("1")
                .unsafeSetVal(ModeAtBorderPage)("1")
                .unsafeSetVal(NationalityAtDeparturePage)(CountryCode("GB"))
                .unsafeSetVal(NationalityCrossingBorderPage)(CountryCode("GB"))
                .unsafeSetVal(IdCrossingBorderPage)("idCrossingBorder")

              val result = service.convert(userAnswers).futureValue

              result.right.value.header.transportDetails.natOfMeaOfTraCroHEA87.value mustBe "GB"
          }
        }

        "must return None when there are new details at border and the mode is a mode that is exempt from nationality" in {

          forAll(genUserAnswerScenario) {
            userAnswerScenario =>
              when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
              when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

              val userAnswers = userAnswerScenario.userAnswers
                .unsafeSetVal(ChangeAtBorderPage)(true)
                .unsafeSetVal(InlandModePage)("2")
                .unsafeSetVal(ModeCrossingBorderPage)("2")
                .unsafeSetVal(ModeAtBorderPage)("2")

              val result = service.convert(userAnswers).futureValue

              result.right.value.header.transportDetails.natOfMeaOfTraCroHEA87 mustBe None
          }
        }

        "must return nationality of departure when there are no new details at border and the mode is NonSpecialMode" in {

          forAll(genUserAnswerScenario) {
            userAnswerScenario =>
              when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
              when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

              val userAnswers = userAnswerScenario.userAnswers
                .unsafeSetVal(ChangeAtBorderPage)(false)
                .unsafeSetVal(InlandModePage)("1")
                .unsafeSetVal(NationalityCrossingBorderPage)(CountryCode("GB"))
                .unsafeSetVal(NationalityAtDeparturePage)(CountryCode("ND"))
                .unsafeSetVal(IdCrossingBorderPage)("idCrossingBorder")

              val result = service.convert(userAnswers).futureValue

              result.right.value.header.transportDetails.natOfMeaOfTraCroHEA87.value mustBe "ND"
          }
        }

        "must return None when there are no new details at border and the mode is Rail" in {

          val railCodes = Gen.oneOf(Rail.Constants.codes)

          forAll(genUserAnswerScenario, railCodes) {
            (userAnswerScenario, railCode) =>
              when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
              when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

              val userAnswers = userAnswerScenario.userAnswers
                .unsafeSetVal(ChangeAtBorderPage)(false)
                .unsafeSetVal(InlandModePage)(railCode.toString)

              val result = service.convert(userAnswers).futureValue

              result.right.value.header.transportDetails.natOfMeaOfTraCroHEA87 mustBe None
          }
        }
      }

      "goodsSummaryDetails" - {
        "must populate controlResult and authorisedLocationOfGoods when Simplified" in {

          forAll(genSimplifiedScenarios) {
            userAnswerScenario =>
              when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
              when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

              val result = service.convert(userAnswerScenario.userAnswers).futureValue.right.value

              val expectedGoodsSummary = userAnswerScenario.toModel.goodsSummary.goodSummaryDetails.asInstanceOf[GoodSummarySimplifiedDetails]

              val expectedControlResult                 = expectedGoodsSummary.controlResultDateLimit
              val expectedAuthorisedLocationOfGoodsCode = expectedGoodsSummary.authorisedLocationCode

              result.controlResult.value.datLimERS69 mustBe expectedControlResult
              result.header.autLocOfGooCodHEA41.value mustBe expectedAuthorisedLocationOfGoodsCode
          }
        }

        "must not populate controlResult and authorisedLocationOfGoods when Normal" in {

          forAll(genNormalScenarios) {
            userAnswerScenario =>
              when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
              when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

              val result = service.convert(userAnswerScenario.userAnswers).futureValue.right.value

              result.controlResult mustBe None
              result.header.autLocOfGooCodHEA41 mustBe None
          }
        }
      }

      "principalTraderDetails" - {
        "has the postcode and city in the right field and the country is set to users answer" in {

          forAll(genNormalScenarios) {
            userAnswerScenario =>
              when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
              when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

              val userAnswers = userAnswerScenario.userAnswers
                .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
                .unsafeSetVal(PrincipalNamePage)("Jimmy")
                .unsafeSetVal(WhatIsPrincipalEoriPage)("ad123456789")
                .unsafeSetVal(PrincipalAddressPage)(CommonAddress("Line 1", "city", "PostCode", Country(CountryCode("XI"), "SomeDescription")))

              val result = service.convert(userAnswers).futureValue.right.value.traderPrincipal

              result mustBe TraderPrincipalWithEori(
                eori = "ad123456789",
                name = Some("Jimmy"),
                streetAndNumber = Some("Line 1"),
                postCode = Some("PostCode"),
                city = Some("city"),
                countryCode = Some("XI"),
                principalTirHolderId = None
              )
          }
        }
      }

      "customs office transit" - {
        "must be empty is declaration type is option 4 (TIR)" in {

          when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
          when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

          val result = service.convert(Scenario7.userAnswers).futureValue.right.value.customsOfficeTransit

          result mustBe Seq.empty
        }

        "populated if any declaration type" in {
          forAll(genUserAnswerScenario) {
            scenario =>
              when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
              when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

              val result: Seq[CustomsOfficeTransit] = service.convert(scenario.userAnswers).futureValue.right.value.customsOfficeTransit
              val exepectedResult: Option[List[CustomsOfficeTransit]] =
                scenario.toModel.routeDetails.asInstanceOf[RouteDetailsLongJourney].transitInformation.map {
                  x =>
                    x.map {
                      case TransitInformation(transitOffice, arrivalTime) => CustomsOfficeTransit(transitOffice, arrivalTime)
                    }.toList
                }

              result mustBe exepectedResult.value
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
