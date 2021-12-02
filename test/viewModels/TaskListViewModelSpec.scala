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

package viewModels

import base.{GeneratorSpec, SpecBase}
import commonTestUtils.UserAnswersSpecHelper
import generators.{ModelGenerators, UserAnswersGenerator}
import models.DeclarationType.{Option1, Option2, Option4}
import models.ProcedureType.{Normal, Simplified}
import models.RepresentativeCapacity.Direct
import models.reference.{CountryCode, CountryOfDispatch, CustomsOffice}
import models.userAnswerScenarios.{Scenario1, Scenario3}
import models.{EoriNumber, GuaranteeType, Index, NormalMode, ProcedureType, Status}
import org.scalacheck.Arbitrary.arbitrary
import pages._
import pages.generalInformation._
import pages.guaranteeDetails.{GuaranteeReferencePage, GuaranteeTypePage, TIRGuaranteeReferencePage}
import pages.routeDetails._
import pages.safetyAndSecurity._
import pages.traderDetails.{AddConsigneePage, AddConsignorPage, IsPrincipalEoriKnownPage, WhatIsPrincipalEoriPage}
import play.api.libs.json.{JsObject, Json}

import java.time.{LocalDate, LocalDateTime}

class TaskListViewModelSpec extends SpecBase with GeneratorSpec with UserAnswersSpecHelper with UserAnswersGenerator with ModelGenerators {

  import TaskListViewModelSpec._

  private val movementSectionName          = "declarationSummary.section.movementDetails"
  private val tradersSectionName           = "declarationSummary.section.tradersDetails"
  private val transportSectionName         = "declarationSummary.section.transport"
  private val routeSectionName             = "declarationSummary.section.routes"
  private val addItemsSectionName          = "declarationSummary.section.addItems"
  private val goodsSummarySectionName      = "declarationSummary.section.goodsSummary"
  private val guaranteeSectionName         = "declarationSummary.section.guarantee"
  private val safetyAndSecuritySectionName = "declarationSummary.section.safetyAndSecurity"

  private val normalDeclarationUa     = Scenario1.userAnswers
  private val simplifiedDeclarationUa = Scenario3.userAnswers

  "TaskListViewModelSpec" - {

    "MovementDetails" - {
      "section task is always included" in {
        val viewModel = TaskListViewModel(emptyUserAnswers)

        viewModel.getSection(movementSectionName) must be(defined)
      }

      "status" - {
        "is Not started when there are no answers for the section" in {
          val viewModel = TaskListViewModel(emptyUserAnswers)

          viewModel.getStatus(movementSectionName).value mustEqual Status.NotStarted
        }

        "is InProgress when the first question for the section has been answered" - {
          "for Normal procedure" - {
            "when the first question (IsPrincipalEoriKnownPage) has been answered" in {

              val userAnswers = emptyUserAnswers
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                .unsafeSetVal(PreLodgeDeclarationPage)(false)

              val viewModel = TaskListViewModel(userAnswers)

              viewModel.getStatus(movementSectionName).value mustEqual Status.InProgress
            }
          }

          "for Simplified procedure" - {
            "when the first question (WhatIsPrincipalEoriPage) has been answered" in {
              val userAnswers = emptyUserAnswers
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Simplified)
                .unsafeSetVal(ContainersUsedPage)(true)

              val viewModel = TaskListViewModel(userAnswers)

              viewModel.getStatus(movementSectionName).value mustEqual Status.InProgress
            }
          }
        }

        "is Completed when all the answers are completed" in {

          val viewModel = TaskListViewModel(normalDeclarationUa)

          viewModel.getStatus(movementSectionName).value mustEqual Status.Completed
        }
      }

      "navigation" - {
        "when the status is Not started and 'Procedure Type is Normal', links to the first page" in {
          val userAnswers = emptyUserAnswers.unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
          val viewModel   = TaskListViewModel(userAnswers)

          val expectedHref: String = controllers.movementDetails.routes.PreLodgeDeclarationController.onPageLoad(lrn, NormalMode).url

          viewModel.getHref(movementSectionName).value mustEqual expectedHref
        }

        "when the status is Not started and 'Procedure Type is Simplified', links to the first page" in {
          val userAnswers = emptyUserAnswers.unsafeSetVal(ProcedureTypePage)(ProcedureType.Simplified)
          val viewModel   = TaskListViewModel(userAnswers)

          val expectedHref: String = controllers.movementDetails.routes.ContainersUsedController.onPageLoad(lrn, NormalMode).url

          viewModel.getHref(movementSectionName).value mustEqual expectedHref
        }

        "when the status is Not started and 'Procedure Type is Unknown', links to Session expired" in {
          val viewModel = TaskListViewModel(emptyUserAnswers)

          val expectedHref: String = controllers.routes.SessionExpiredController.onPageLoad().url

          viewModel.getHref(movementSectionName).value mustEqual expectedHref
        }

        "when the status is InProgress and 'Procedure Type is Normal', links to the first page" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
            .unsafeSetVal(IsPrincipalEoriKnownPage)(false)

          val viewModel = TaskListViewModel(userAnswers)

          val expectedHref: String = controllers.movementDetails.routes.PreLodgeDeclarationController.onPageLoad(lrn, NormalMode).url

          viewModel.getHref(movementSectionName).value mustEqual expectedHref

        }

        "when the status is InProgress and 'Procedure Type is Simplified', links to the first page" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(ProcedureTypePage)(ProcedureType.Simplified)
            .unsafeSetVal(IsPrincipalEoriKnownPage)(false)

          val viewModel = TaskListViewModel(userAnswers)

          val expectedHref: String = controllers.movementDetails.routes.ContainersUsedController.onPageLoad(lrn, NormalMode).url

          viewModel.getHref(movementSectionName).value mustEqual expectedHref

        }

        "when the status is InProgress and 'Procedure Type is Unknown', links to the Session expired" in {

          val userAnswers = emptyUserAnswers.unsafeSetVal(IsPrincipalEoriKnownPage)(false)

          val viewModel = TaskListViewModel(userAnswers)

          val expectedHref: String = controllers.routes.SessionExpiredController.onPageLoad().url

          viewModel.getHref(movementSectionName).value mustEqual expectedHref

        }

        "when the status is Completed, links to the Check your answers page for the section" in {

          val viewModel = TaskListViewModel(normalDeclarationUa)

          val expectedHref: String = controllers.movementDetails.routes.MovementDetailsCheckYourAnswersController.onPageLoad(lrn).url

          viewModel.getHref(movementSectionName).value mustEqual expectedHref
        }
      }
    }

    "RouteDetails" - {
      "section task is always included" in {
        val viewModel = TaskListViewModel(emptyUserAnswers)

        viewModel.getSection(routeSectionName) must be(defined)
      }

      "status" - {
        "is Not started when there are no answers for the section" in {
          val viewModel = TaskListViewModel(emptyUserAnswers)

          viewModel.getStatus(routeSectionName).value mustEqual Status.NotStarted
        }

        "is InProgress when the first question for the section has been answered" in {
          forAll(arbitrary[CountryOfDispatch]) {
            pageAnswer =>
              val userAnswers = emptyUserAnswers.unsafeSetVal(CountryOfDispatchPage)(pageAnswer)

              val viewModel = TaskListViewModel(userAnswers)

              viewModel.getStatus(routeSectionName).value mustEqual Status.InProgress
          }
        }

        "is Completed when all the answers are completed" in {

          val viewModel = TaskListViewModel(normalDeclarationUa)

          viewModel.getStatus(routeSectionName).value mustEqual Status.Completed
        }
      }

      "navigation" - {
        "when the status is Not started, links to the first page" in {
          val viewModel = TaskListViewModel(emptyUserAnswers)

          val expectedHref: String = controllers.routeDetails.routes.CountryOfDispatchController.onPageLoad(lrn, NormalMode).url

          viewModel.getHref(routeSectionName).value mustEqual expectedHref
        }

        "when the status is InProgress, links to the first page" in {
          forAll(arb[CountryOfDispatch]) {
            pageAnswer =>
              val userAnswers = emptyUserAnswers.unsafeSetVal(CountryOfDispatchPage)(pageAnswer)

              val viewModel = TaskListViewModel(userAnswers)

              val expectedHref: String = controllers.routeDetails.routes.CountryOfDispatchController.onPageLoad(lrn, NormalMode).url

              viewModel.getHref(routeSectionName).value mustEqual expectedHref
          }
        }

        "when the status is Completed, links to the Check your answers page for the section" in {

          val viewModel = TaskListViewModel(normalDeclarationUa)

          val expectedHref: String = controllers.routeDetails.routes.RouteDetailsCheckYourAnswersController.onPageLoad(lrn).url

          viewModel.getHref(routeSectionName).value mustEqual expectedHref
        }
      }
    }

    "TransportDetail" - {
      "section task is always included" in {
        val viewModel = TaskListViewModel(emptyUserAnswers)

        viewModel.getSection(transportSectionName) must be(defined)
      }

      "status" - {
        "when dependent section is incomplete" - {
          "is Cannot start yet when the dependent section is not complete" in {
            val viewModel = TaskListViewModel(emptyUserAnswers)

            viewModel.getStatus(transportSectionName).value mustEqual Status.CannotStartYet
          }
        }

        "when dependent section is complete" - {

          "is Not started when there are no answers for the section" in {

            val movementDetailsUa = emptyUserAnswers
              .unsafeSetVal(ProcedureTypePage)(Normal)
              .unsafeSetVal(DeclarationTypePage)(Option1)
              .unsafeSetVal(PreLodgeDeclarationPage)(false)
              .unsafeSetVal(ContainersUsedPage)(false)
              .unsafeSetVal(DeclarationPlacePage)("declarationPlace")
              .unsafeSetVal(DeclarationForSomeoneElsePage)(true)
              .unsafeSetVal(RepresentativeNamePage)("repName")
              .unsafeSetVal(RepresentativeCapacityPage)(Direct)

            val viewModel = TaskListViewModel(movementDetailsUa)

            viewModel.getStatus(transportSectionName).value mustEqual Status.NotStarted
          }

          "is InProgress when the first question for the section has been answered" in {

            val updatedUserAnswers = emptyUserAnswers
              .unsafeSetVal(ProcedureTypePage)(Normal)
              .unsafeSetVal(DeclarationTypePage)(Option1)
              .unsafeSetVal(PreLodgeDeclarationPage)(false)
              .unsafeSetVal(ContainersUsedPage)(false)
              .unsafeSetVal(DeclarationPlacePage)("declarationPlace")
              .unsafeSetVal(DeclarationForSomeoneElsePage)(true)
              .unsafeSetVal(RepresentativeNamePage)("repName")
              .unsafeSetVal(RepresentativeCapacityPage)(Direct)
              .unsafeSetVal(InlandModePage)("1")

            val viewModel = TaskListViewModel(updatedUserAnswers)

            viewModel.getStatus(transportSectionName).value mustEqual Status.InProgress
          }

          "is Completed when all the answers are completed" in {

            val viewModel = TaskListViewModel(normalDeclarationUa)

            viewModel.getStatus(transportSectionName).value mustEqual Status.Completed
          }
        }
      }

      "navigation" - {
        "when dependent section is incomplete" - {
          "when the status is Cannot start yet with no links" in {
            val viewModel = TaskListViewModel(emptyUserAnswers)

            viewModel.getHref(transportSectionName).value.isEmpty mustEqual true
          }
        }

        "when dependent section is complete" - {
          "when the status is Not started, links to the first page" in {

            val movementDetailsUa = emptyUserAnswers
              .unsafeSetVal(ProcedureTypePage)(Normal)
              .unsafeSetVal(DeclarationTypePage)(Option1)
              .unsafeSetVal(PreLodgeDeclarationPage)(false)
              .unsafeSetVal(ContainersUsedPage)(false)
              .unsafeSetVal(DeclarationPlacePage)("declarationPlace")
              .unsafeSetVal(DeclarationForSomeoneElsePage)(true)
              .unsafeSetVal(RepresentativeNamePage)("repName")
              .unsafeSetVal(RepresentativeCapacityPage)(Direct)

            val viewModel = TaskListViewModel(movementDetailsUa)

            val expectedHref: String = controllers.transportDetails.routes.InlandModeController.onPageLoad(lrn, NormalMode).url

            viewModel.getHref(transportSectionName).value mustEqual expectedHref
          }

          "when the status is InProgress, links to the first page" in {

            val movementDetailsUa = emptyUserAnswers
              .unsafeSetVal(ProcedureTypePage)(Normal)
              .unsafeSetVal(DeclarationTypePage)(Option1)
              .unsafeSetVal(PreLodgeDeclarationPage)(false)
              .unsafeSetVal(ContainersUsedPage)(false)
              .unsafeSetVal(DeclarationPlacePage)("declarationPlace")
              .unsafeSetVal(DeclarationForSomeoneElsePage)(true)
              .unsafeSetVal(RepresentativeNamePage)("repName")
              .unsafeSetVal(RepresentativeCapacityPage)(Direct)
              .unsafeSetVal(InlandModePage)("1")

            val viewModel = TaskListViewModel(movementDetailsUa)

            val expectedHref: String = controllers.transportDetails.routes.InlandModeController.onPageLoad(lrn, NormalMode).url

            viewModel.getHref(transportSectionName).value mustEqual expectedHref
          }

          "when the status is Completed, links to the Check your answers page for the section" in {

            val viewModel = TaskListViewModel(normalDeclarationUa)

            val expectedHref: String = controllers.transportDetails.routes.TransportDetailsCheckYourAnswersController.onPageLoad(lrn).url

            viewModel.getHref(transportSectionName).value mustEqual expectedHref
          }
        }
      }
    }

    "TraderDetails" - {
      "section task is always included" in {
        val viewModel = TaskListViewModel(emptyUserAnswers)

        viewModel.getSection(tradersSectionName) must be(defined)
      }

      "status" - {
        "is Not started when there are no answers for the section" in {
          val viewModel = TaskListViewModel(emptyUserAnswers)

          viewModel.getStatus(tradersSectionName).value mustEqual Status.NotStarted
        }

        "is InProgress" - {
          "for Normal procedure" - {
            "when the first question (IsPrincipalEoriKnownPage) has been answered" in {

              val userAnswers = emptyUserAnswers
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
                .unsafeSetVal(IsPrincipalEoriKnownPage)(false)

              val viewModel = TaskListViewModel(userAnswers)

              viewModel.getStatus(tradersSectionName).value mustEqual Status.InProgress
            }
          }

          "for Simplified procedure" - {
            "when the first question (WhatIsPrincipalEoriPage) has been answered" in {
              val eori = arb[EoriNumber].sample.value
              val userAnswers = emptyUserAnswers
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Simplified)
                .unsafeSetVal(WhatIsPrincipalEoriPage)(eori.value)

              val viewModel = TaskListViewModel(userAnswers)

              viewModel.getStatus(tradersSectionName).value mustEqual Status.InProgress
            }
          }
        }

        "is Completed when all the answers are completed" - {
          "for Normal procedure" in {
            forAll(genNormalScenarios) {
              normalScenarios =>
                val viewModel = TaskListViewModel(normalScenarios.userAnswers)

                viewModel.getStatus(tradersSectionName).value mustEqual Status.Completed
            }
          }

          "for Simplified procedure" in {
            forAll(genSimplifiedScenarios) {
              simplifiedScenarios =>
                val viewModel = TaskListViewModel(simplifiedScenarios.userAnswers)

                viewModel.getStatus(tradersSectionName).value mustEqual Status.Completed
            }
          }
        }
      }

      "navigation" - {
        "when the status is Not started and 'Procedure Type is Normal', links to the first page" in {
          val userAnswers = emptyUserAnswers.unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
          val viewModel   = TaskListViewModel(userAnswers)

          val expectedHref: String = controllers.traderDetails.routes.IsPrincipalEoriKnownController.onPageLoad(lrn, NormalMode).url

          viewModel.getHref(tradersSectionName).value mustEqual expectedHref
        }

        "when the status is Not started and 'Procedure Type is Simplified', links to the first page" in {
          val userAnswers = emptyUserAnswers.unsafeSetVal(ProcedureTypePage)(ProcedureType.Simplified)
          val viewModel   = TaskListViewModel(userAnswers)

          val expectedHref: String = controllers.traderDetails.routes.WhatIsPrincipalEoriController.onPageLoad(lrn, NormalMode).url

          viewModel.getHref(tradersSectionName).value mustEqual expectedHref
        }

        "when the status is Not started and 'Procedure Type is Unknown', links to Session expired" in {
          val viewModel = TaskListViewModel(emptyUserAnswers)

          val expectedHref: String = controllers.routes.SessionExpiredController.onPageLoad().url

          viewModel.getHref(tradersSectionName).value mustEqual expectedHref
        }

        "when the status is InProgress and 'Procedure Type is Normal', links to the first page" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(ProcedureTypePage)(ProcedureType.Normal)
            .unsafeSetVal(IsPrincipalEoriKnownPage)(false)

          val viewModel = TaskListViewModel(userAnswers)

          val expectedHref: String = controllers.traderDetails.routes.IsPrincipalEoriKnownController.onPageLoad(lrn, NormalMode).url

          viewModel.getHref(tradersSectionName).value mustEqual expectedHref
        }

        "when the status is InProgress and 'Procedure Type is Simplified', links to the first page" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(ProcedureTypePage)(ProcedureType.Simplified)
            .unsafeSetVal(IsPrincipalEoriKnownPage)(false)

          val viewModel = TaskListViewModel(userAnswers)

          val expectedHref: String = controllers.traderDetails.routes.WhatIsPrincipalEoriController.onPageLoad(lrn, NormalMode).url

          viewModel.getHref(tradersSectionName).value mustEqual expectedHref
        }

        "when the status is InProgress and 'Procedure Type is Unknown', links to the Session expired" in {

          val userAnswers = emptyUserAnswers.unsafeSetVal(IsPrincipalEoriKnownPage)(false)

          val viewModel = TaskListViewModel(userAnswers)

          val expectedHref: String = controllers.routes.SessionExpiredController.onPageLoad().url

          viewModel.getHref(tradersSectionName).value mustEqual expectedHref

        }

        "when the status is Completed and 'Procedure Type is Normal', links to the Check your answers page for the section" in {
          forAll(genNormalScenarios) {
            normalScenario =>
              val viewModel = TaskListViewModel(normalScenario.userAnswers)

              val expectedHref: String = controllers.traderDetails.routes.TraderDetailsCheckYourAnswersController.onPageLoad(lrn).url

              viewModel.getHref(tradersSectionName).value mustEqual expectedHref
          }
        }

        "when the status is Completed and 'Procedure Type is Simplified', links to the Check your answers page for the section" in {
          forAll(genSimplifiedScenarios) {
            simplifiedScenario =>
              val viewModel = TaskListViewModel(simplifiedScenario.userAnswers)

              val expectedHref: String = controllers.traderDetails.routes.TraderDetailsCheckYourAnswersController.onPageLoad(lrn).url

              viewModel.getHref(tradersSectionName).value mustEqual expectedHref
          }
        }
      }
    }

    "SecurityDetails" - {

      "section task" - {
        "is included when user has chosen to add Security Details" in {
          val useranswers = emptyUserAnswers.unsafeSetVal(AddSecurityDetailsPage)(true)

          val viewModel = TaskListViewModel(useranswers)

          viewModel.getSection(safetyAndSecuritySectionName) must be(defined)
        }

        "is not included when user has chosen to not add Security Details" in {
          val useranswers = emptyUserAnswers.unsafeSetVal(AddSecurityDetailsPage)(false)

          val viewModel = TaskListViewModel(useranswers)

          viewModel.getSection(safetyAndSecuritySectionName) must not be defined
        }
      }

      "status when section is required" - {
        "when dependent section is incomplete" - {
          "is Cannot start yet, when the dependent section is incomplete" in {
            val userAnswers = emptyUserAnswers
              .unsafeSetVal(AddSecurityDetailsPage)(true)

            val viewModel = TaskListViewModel(userAnswers)

            viewModel.getStatus(safetyAndSecuritySectionName).value mustEqual Status.CannotStartYet
          }
        }

        "when dependent section is complete" - {

          "is Not started when there are no answers for the section" in {

            val transportDetailsUa = emptyUserAnswers
              .unsafeSetVal(AddSecurityDetailsPage)(true)
              .unsafeSetVal(InlandModePage)("1")
              .unsafeSetVal(AddIdAtDeparturePage)(false)
              .unsafeSetVal(ChangeAtBorderPage)(false)

            val viewModel = TaskListViewModel(transportDetailsUa)

            viewModel.getStatus(safetyAndSecuritySectionName).value mustEqual Status.NotStarted
          }

          "is InProgress when the first question for the section has been answered" in {

            val updatedUserAnswers = emptyUserAnswers
              .unsafeSetVal(AddSecurityDetailsPage)(true)
              .unsafeSetVal(AddCircumstanceIndicatorPage)(false)
              .unsafeSetVal(InlandModePage)("1")
              .unsafeSetVal(AddIdAtDeparturePage)(false)
              .unsafeSetVal(ChangeAtBorderPage)(false)

            val viewModel = TaskListViewModel(updatedUserAnswers)

            viewModel.getStatus(safetyAndSecuritySectionName).value mustEqual Status.InProgress
          }

          "is Completed when all the answers are completed" in {

            val viewModel = TaskListViewModel(normalDeclarationUa)

            viewModel.getStatus(safetyAndSecuritySectionName).value mustEqual Status.Completed
          }
        }
      }

      "navigation when section is required" - {

        "when dependent section is incomplete" - {

          "when the status is cannot start yet, no link should be provided to start the journey" in {
            val updatedUserAnswers = emptyUserAnswers
              .unsafeSetVal(AddSecurityDetailsPage)(true)

            val viewModel = TaskListViewModel(updatedUserAnswers)

            viewModel.getHref(safetyAndSecuritySectionName).value.isEmpty mustEqual true
          }
        }

        "when dependent section is complete" - {
          "when the status is Not started, links to the first page" in {

            val transportDetailsUa = emptyUserAnswers
              .unsafeSetVal(AddSecurityDetailsPage)(true)
              .unsafeSetVal(InlandModePage)("1")
              .unsafeSetVal(AddIdAtDeparturePage)(false)
              .unsafeSetVal(ChangeAtBorderPage)(false)

            val viewModel = TaskListViewModel(transportDetailsUa)

            val expectedHref: String = controllers.safetyAndSecurity.routes.AddCircumstanceIndicatorController.onPageLoad(lrn, NormalMode).url

            viewModel.getHref(safetyAndSecuritySectionName).value mustEqual expectedHref
          }

          "when the status is InProgress, links to the first page" in {

            val updatedUserAnswers = emptyUserAnswers
              .unsafeSetVal(AddSecurityDetailsPage)(true)
              .unsafeSetVal(AddCircumstanceIndicatorPage)(false)
              .unsafeSetVal(InlandModePage)("1")
              .unsafeSetVal(AddIdAtDeparturePage)(false)
              .unsafeSetVal(ChangeAtBorderPage)(false)

            val viewModel = TaskListViewModel(updatedUserAnswers)

            val expectedHref: String = controllers.safetyAndSecurity.routes.AddCircumstanceIndicatorController.onPageLoad(lrn, NormalMode).url

            viewModel.getHref(safetyAndSecuritySectionName).value mustEqual expectedHref
          }
        }

        "when the status is Completed, links to the Check your answers page for the section" in {

          val viewModel = TaskListViewModel(normalDeclarationUa)

          val expectedHref: String = controllers.safetyAndSecurity.routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(lrn).url

          viewModel.getHref(safetyAndSecuritySectionName).value mustEqual expectedHref
        }
      }
    }
  }

  "ItemsDetails" - {
    "section task is always included" in {
      val viewModel = TaskListViewModel(emptyUserAnswers)

      viewModel.getSection(addItemsSectionName) must be(defined)
    }

    val dependantSections = emptyUserAnswers
      .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("Id", "name", CountryCode("GB"), None))
      // MovementDetails
      .unsafeSetVal(ProcedureTypePage)(Normal)
      .unsafeSetVal(DeclarationTypePage)(Option1)
      .unsafeSetVal(PreLodgeDeclarationPage)(false)
      .unsafeSetVal(ContainersUsedPage)(false)
      .unsafeSetVal(DeclarationPlacePage)("declarationPlace")
      .unsafeSetVal(DeclarationForSomeoneElsePage)(true)
      .unsafeSetVal(RepresentativeNamePage)("repName")
      .unsafeSetVal(RepresentativeCapacityPage)(Direct)
      // TraderDetails
      .unsafeSetVal(IsPrincipalEoriKnownPage)(true)
      .unsafeSetVal(WhatIsPrincipalEoriPage)("GBEoriNumber")
      .unsafeSetVal(AddConsignorPage)(false)
      .unsafeSetVal(AddConsigneePage)(false)
      // RouteDetails
      .unsafeSetVal(CountryOfDispatchPage)(CountryOfDispatch(CountryCode("GB"), true))
      .unsafeSetVal(DestinationCountryPage)(CountryCode("IT"))
      .unsafeSetVal(DestinationOfficePage)(CustomsOffice("id", "name", CountryCode("IT"), None))
      .unsafeSetVal(AddAnotherTransitOfficePage(index))("transitOffice")
      .unsafeSetVal(ArrivalDatesAtOfficePage(index))(LocalDateTime.now())
      // SafetyAndSecurity
      .unsafeSetVal(AddCircumstanceIndicatorPage)(false)
      .unsafeSetVal(AddTransportChargesPaymentMethodPage)(false)
      .unsafeSetVal(AddCommercialReferenceNumberPage)(false)
      .unsafeSetVal(AddCommercialReferenceNumberAllItemsPage)(false)
      .unsafeSetVal(AddConveyanceReferenceNumberPage)(false)
      .unsafeSetVal(PlaceOfUnloadingCodePage)("placeOfUnloading")
      .unsafeSetVal(AddSafetyAndSecurityConsignorPage)(false)
      .unsafeSetVal(AddSafetyAndSecurityConsigneePage)(false)
      .unsafeSetVal(AddCarrierPage)(false)
      .unsafeSetVal(CountryOfRoutingPage(Index(0)))(CountryCode("GB"))

    "status" - {
      "when dependent section is incomplete" - {
        "is Cannot start yet when there are no answers for the dependent sections" in {
          val viewModel = TaskListViewModel(emptyUserAnswers)

          viewModel.getStatus(addItemsSectionName).value mustEqual Status.CannotStartYet
        }

        "is InProgress when the first question for the section has been answered" - {

          "when procedure is Normal and safety and security is true" in {

            val userAnswers = dependantSections
              .unsafeSetVal(AddSecurityDetailsPage)(true)
              .unsafeSetVal(ProcedureTypePage)(Normal)
              .unsafeSetVal(ItemDescriptionPage(Index(0)))("description")

            val viewModel = TaskListViewModel(userAnswers)

            viewModel.getStatus(addItemsSectionName).value mustEqual Status.InProgress
          }

          "when procedure is Normal and safety and security is false" in {

            val userAnswers = dependantSections
              .unsafeSetVal(AddSecurityDetailsPage)(false)
              .unsafeSetVal(ProcedureTypePage)(Normal)
              .unsafeSetVal(ItemDescriptionPage(Index(0)))("description")

            val viewModel = TaskListViewModel(userAnswers)

            viewModel.getStatus(addItemsSectionName).value mustEqual Status.InProgress
          }

          "when procedure is Simplified and safety and security page is true" in {
            val userAnswers = dependantSections
              .unsafeSetVal(AddSecurityDetailsPage)(true)
              .unsafeSetVal(ProcedureTypePage)(Simplified)
              .unsafeSetVal(ItemDescriptionPage(Index(0)))("description")

            val viewModel = TaskListViewModel(userAnswers)

            viewModel.getStatus(addItemsSectionName).value mustEqual Status.InProgress
          }

          "when procedure is Simplified and safety and security page is false" in {
            val userAnswers = dependantSections
              .unsafeSetVal(AddSecurityDetailsPage)(false)
              .unsafeSetVal(ProcedureTypePage)(Simplified)
              .unsafeSetVal(ItemDescriptionPage(Index(0)))("description")

            val viewModel = TaskListViewModel(userAnswers)

            viewModel.getStatus(addItemsSectionName).value mustEqual Status.InProgress
          }
        }

        "is Completed when all the answers are completed" - {

          "when procedure is Normal" in {
            val viewModel = TaskListViewModel(normalDeclarationUa)

            viewModel.getStatus(addItemsSectionName).value mustEqual Status.Completed
          }

          "when procedure is Simplified" in {
            val viewModel = TaskListViewModel(simplifiedDeclarationUa)

            viewModel.getStatus(addItemsSectionName).value mustEqual Status.Completed
          }
        }
      }
    }

    "navigation" - {

      "when dependent section is incomplete" - {
        "when the status is Cannot start yet with links disable until trader details section is complete" in {
          val viewModel = TaskListViewModel(emptyUserAnswers)

          viewModel.getHref(addItemsSectionName).value.isEmpty mustEqual true
        }
      }

      "when dependent section is complete" - {

        "when the status is Not started, links to the confirm start add item page" in {

          val userAnswers = dependantSections
            .unsafeSetVal(AddSecurityDetailsPage)(true)

          val viewModel = TaskListViewModel(userAnswers)

          val expectedHref: String = controllers.addItems.itemDetails.routes.ConfirmStartAddItemsController.onPageLoad(lrn).url

          viewModel.getHref(addItemsSectionName).value mustEqual expectedHref
        }

        "when the status is InProgress, links to the item description page" in {

          val updatedUserAnswers = dependantSections
            .unsafeSetVal(AddSecurityDetailsPage)(true)
            .unsafeSetVal(ItemDescriptionPage(Index(0)))("description")

          val viewModel = TaskListViewModel(updatedUserAnswers)

          val expectedHref: String = controllers.addItems.itemDetails.routes.ItemDescriptionController.onPageLoad(lrn, Index(0), NormalMode).url

          viewModel.getHref(addItemsSectionName).value mustEqual expectedHref
        }

        "when the status is Completed, links to the Check your answers page for the section" - {
          "for Normal procedure" in {

            val viewModel = TaskListViewModel(normalDeclarationUa)

            val expectedHref: String = controllers.addItems.routes.AddAnotherItemController.onPageLoad(lrn).url

            viewModel.getHref(addItemsSectionName).value mustEqual expectedHref
          }

          "for Simplified procedure" in {

            val viewModel = TaskListViewModel(simplifiedDeclarationUa)

            val expectedHref: String = controllers.addItems.routes.AddAnotherItemController.onPageLoad(lrn).url

            viewModel.getHref(addItemsSectionName).value mustEqual expectedHref
          }
        }
      }
    }
  }

  "GoodsSummaryDetails" - {

    val dependantSections = emptyUserAnswers
      // MovementDetails
      .unsafeSetVal(ProcedureTypePage)(Normal)
      .unsafeSetVal(DeclarationTypePage)(Option1)
      .unsafeSetVal(PreLodgeDeclarationPage)(false)
      .unsafeSetVal(ContainersUsedPage)(false)
      .unsafeSetVal(DeclarationPlacePage)("declarationPlace")
      .unsafeSetVal(DeclarationForSomeoneElsePage)(true)
      .unsafeSetVal(RepresentativeNamePage)("repName")
      .unsafeSetVal(RepresentativeCapacityPage)(Direct)

    "section task is always included" in {
      val viewModel = TaskListViewModel(emptyUserAnswers)

      viewModel.getSection(goodsSummarySectionName) must be(defined)
    }

    "status" - {

      "is Not started when there are no answers for the section" in {
        val updatedUserAnswers = emptyUserAnswers.set(ProcedureTypePage, ProcedureType.Simplified).success.value
        val viewModel          = TaskListViewModel(updatedUserAnswers)

        viewModel.getStatus(goodsSummarySectionName).value mustEqual Status.NotStarted
      }

      "is Cannot start yet when there are no answers for the section" in {
        val updatedUserAnswers = emptyUserAnswers.set(ProcedureTypePage, ProcedureType.Normal).success.value
        val viewModel          = TaskListViewModel(updatedUserAnswers)

        viewModel.getStatus(goodsSummarySectionName).value mustEqual Status.CannotStartYet
      }

      "is InProgress" - {

        "when loading place page has been answered" in {

          forAll(arb[String]) {
            pageAnswer =>
              val userAnswers = emptyUserAnswers
                .unsafeSetVal(AddSecurityDetailsPage)(true)
                .unsafeSetVal(LoadingPlacePage)(pageAnswer)
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Simplified)

              val viewModel = TaskListViewModel(userAnswers)

              viewModel.getStatus(goodsSummarySectionName).value mustEqual Status.InProgress
          }
        }

        "when add custom approved location page has been answered" in {

          forAll(arb[Boolean]) {
            pageAnswer =>
              val movementDetailsUa = emptyUserAnswers
                .unsafeSetVal(ProcedureTypePage)(Normal)
                .unsafeSetVal(DeclarationTypePage)(Option1)
                .unsafeSetVal(PreLodgeDeclarationPage)(false)
                .unsafeSetVal(ContainersUsedPage)(false)
                .unsafeSetVal(DeclarationPlacePage)("declarationPlace")
                .unsafeSetVal(DeclarationForSomeoneElsePage)(true)
                .unsafeSetVal(RepresentativeNamePage)("repName")
                .unsafeSetVal(RepresentativeCapacityPage)(Direct)
                .unsafeSetVal(AddSecurityDetailsPage)(false)
                .unsafeSetVal(AddCustomsApprovedLocationPage)(pageAnswer)

              val viewModel = TaskListViewModel(movementDetailsUa)

              viewModel.getStatus(goodsSummarySectionName).value mustEqual Status.InProgress
          }
        }

        "when Add Agreed Location of Goods page has been answered" in {

          //ToDo: Change AddAgreedLocationOfGoodsPage to arbitary when CTCTRADERS-2454 is completed
          forAll(arb[Boolean]) {
            pageAnswer =>
              val movementDetailsUa = emptyUserAnswers
                .unsafeSetVal(ProcedureTypePage)(Normal)
                .unsafeSetVal(DeclarationTypePage)(Option1)
                .unsafeSetVal(PreLodgeDeclarationPage)(true)
                .unsafeSetVal(ContainersUsedPage)(false)
                .unsafeSetVal(DeclarationPlacePage)("declarationPlace")
                .unsafeSetVal(DeclarationForSomeoneElsePage)(true)
                .unsafeSetVal(RepresentativeNamePage)("repName")
                .unsafeSetVal(RepresentativeCapacityPage)(Direct)
                .unsafeSetVal(AddSecurityDetailsPage)(false)
                .unsafeSetVal(AddAgreedLocationOfGoodsPage)(true)

              val viewModel = TaskListViewModel(movementDetailsUa)

              viewModel.getStatus(goodsSummarySectionName).value mustEqual Status.InProgress
          }
        }

        "when authorised location code page has been answered" in {

          forAll(arb[String]) {
            pageAnswer =>
              val userAnswers = emptyUserAnswers
                .unsafeSetVal(AddSecurityDetailsPage)(false)
                .unsafeSetVal(ProcedureTypePage)(ProcedureType.Simplified)
                .unsafeSetVal(AuthorisedLocationCodePage)(pageAnswer)

              val viewModel = TaskListViewModel(userAnswers)

              viewModel.getStatus(goodsSummarySectionName).value mustEqual Status.InProgress
          }
        }
      }

      "is Completed" - {

        "procedure type is 'Normal' when all the answers are completed" in {

          val normalGoodsSummary = dependantSections
            .unsafeSetVal(ProcedureTypePage)(Normal)
            .unsafeSetVal(PreLodgeDeclarationPage)(false)
            .unsafeSetVal(AddSecurityDetailsPage)(true)
            .unsafeSetVal(LoadingPlacePage)("loadingPlace")
            .unsafeSetVal(AddCustomsApprovedLocationPage)(true)
            .unsafeSetVal(CustomsApprovedLocationPage)("approvedLocation")
            .unsafeSetVal(AddSealsPage)(false)

          val viewModel = TaskListViewModel(normalGoodsSummary)

          viewModel.getStatus(goodsSummarySectionName).value mustEqual Status.Completed

        }

        "procedure type is 'Simplified' when all the answers are completed" in {

          val simplfiedGoodsSummary = dependantSections
            .unsafeSetVal(ProcedureTypePage)(Simplified)
            .unsafeSetVal(AddSecurityDetailsPage)(false)
            .unsafeSetVal(AuthorisedLocationCodePage)("authLocation")
            .unsafeSetVal(ControlResultDateLimitPage)(LocalDate.now)
            .unsafeSetVal(AddSealsPage)(false)

          val viewModel = TaskListViewModel(simplfiedGoodsSummary)

          viewModel.getStatus(goodsSummarySectionName).value mustEqual Status.Completed

        }
      }

    }

    "navigation" - {
      "when the status is not started" - {
        "safety and security is yes, links to the loading place page" in {

          val answers = dependantSections.unsafeSetVal(AddSecurityDetailsPage)(true)

          val viewModel = TaskListViewModel(answers)

          val expectedHref: String = controllers.routes.LoadingPlaceController.onPageLoad(lrn, NormalMode).url

          viewModel.getHref(goodsSummarySectionName).value mustEqual expectedHref
        }

        "safety and security is no" - {

          "mode is simplified, links to the Authorised Location Code page" in {
            val userAnswers = emptyUserAnswers
              .unsafeSetVal(AddSecurityDetailsPage)(false)
              .unsafeSetVal(ProcedureTypePage)(ProcedureType.Simplified)
            val viewModel = TaskListViewModel(userAnswers)

            val expectedHref: String = controllers.goodsSummary.routes.AuthorisedLocationCodeController.onPageLoad(lrn, NormalMode).url

            viewModel.getHref(goodsSummarySectionName).value mustEqual expectedHref
          }

          "mode is normal" - {
            "pre-lodges is no, links to the Add Custom Approved Location page" in {

              val answers = dependantSections.unsafeSetVal(AddSecurityDetailsPage)(false).unsafeSetVal(PreLodgeDeclarationPage)(false)

              val viewModel = TaskListViewModel(answers)

              val expectedHref: String = controllers.goodsSummary.routes.AddCustomsApprovedLocationController.onPageLoad(lrn, NormalMode).url

              viewModel.getHref(goodsSummarySectionName).value mustEqual expectedHref
            }

            "pre-lodges is yes, links to the Add Agreed Location of Goods page" in {

              val answers = dependantSections.unsafeSetVal(AddSecurityDetailsPage)(false).unsafeSetVal(PreLodgeDeclarationPage)(true)

              val viewModel = TaskListViewModel(answers)

              val expectedHref: String = controllers.goodsSummary.routes.AddAgreedLocationOfGoodsController.onPageLoad(lrn, NormalMode).url

              viewModel.getHref(goodsSummarySectionName).value mustEqual expectedHref
            }
          }

        }
      }

      "when the status is InProgress" - {
        "safety and security is yes, links to the loading place page" in {
          forAll(arb[String]) {
            pageAnswer =>
              val userAnswers = emptyUserAnswers
                .unsafeSetVal(AddSecurityDetailsPage)(true)
                .unsafeSetVal(LoadingPlacePage)(pageAnswer)
              val viewModel = TaskListViewModel(userAnswers)

              val expectedHref: String = controllers.routes.LoadingPlaceController.onPageLoad(lrn, NormalMode).url

              viewModel.getHref(goodsSummarySectionName).value mustEqual expectedHref
          }
        }

        "safety and security is no" - {

          "mode is simplified, links to the Authorised Location Code page" in {
            forAll(arb[String]) {
              pageAnswer =>
                val userAnswers = emptyUserAnswers
                  .unsafeSetVal(AddSecurityDetailsPage)(false)
                  .unsafeSetVal(ProcedureTypePage)(ProcedureType.Simplified)
                  .unsafeSetVal(AuthorisedLocationCodePage)(pageAnswer)
                val viewModel = TaskListViewModel(userAnswers)

                val expectedHref: String = controllers.goodsSummary.routes.AuthorisedLocationCodeController.onPageLoad(lrn, NormalMode).url

                viewModel.getHref(goodsSummarySectionName).value mustEqual expectedHref
            }
          }

          "mode is normal" - {
            "pre-lodges is no, links to the Add Custom Approved Location page" in {
              val answers = dependantSections.unsafeSetVal(AddSecurityDetailsPage)(false).unsafeSetVal(PreLodgeDeclarationPage)(false)

              forAll(arb[Boolean]) {
                pageAnswer =>
                  val updatedUserAnswers = answers
                    .unsafeSetVal(AddCustomsApprovedLocationPage)(pageAnswer)

                  val viewModel = TaskListViewModel(updatedUserAnswers)

                  val expectedHref: String = controllers.goodsSummary.routes.AddCustomsApprovedLocationController.onPageLoad(lrn, NormalMode).url

                  viewModel.getHref(goodsSummarySectionName).value mustEqual expectedHref
              }
            }

            "pre-lodges is yes, links to the Add Agreed Location of Goods page" in {
              val answers = dependantSections.unsafeSetVal(AddSecurityDetailsPage)(false).unsafeSetVal(PreLodgeDeclarationPage)(true)

              forAll(arb[String]) {
                pageAnswer =>
                  val updatedUserAnswers = answers
                    .unsafeSetVal(AgreedLocationOfGoodsPage)(pageAnswer)
                  val viewModel = TaskListViewModel(updatedUserAnswers)

                  val expectedHref: String = controllers.goodsSummary.routes.AddAgreedLocationOfGoodsController.onPageLoad(lrn, NormalMode).url

                  viewModel.getHref(goodsSummarySectionName).value mustEqual expectedHref
              }
            }
          }

        }
      }

      "when the status is Completed, links to the Check your answers page for the section" in {

        val userAnswers = dependantSections
          .unsafeSetVal(ProcedureTypePage)(Simplified)
          .unsafeSetVal(TotalPackagesPage)(1)
          .unsafeSetVal(AddSecurityDetailsPage)(false)
          .unsafeSetVal(AuthorisedLocationCodePage)("authLocation")
          .unsafeSetVal(ControlResultDateLimitPage)(LocalDate.now)
          .unsafeSetVal(AddSealsPage)(false)

        val viewModel = TaskListViewModel(userAnswers)

        val expectedHref: String = controllers.goodsSummary.routes.GoodsSummaryCheckYourAnswersController.onPageLoad(lrn).url

        viewModel.getHref(goodsSummarySectionName).value mustEqual expectedHref

      }
    }
  }

  "GuaranteeDetails" - {

    val dependentSection = emptyUserAnswers
      .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("Id", "name", CountryCode("GB"), None))
      .unsafeSetVal(AddSecurityDetailsPage)(true)
      .unsafeSetVal(CountryOfDispatchPage)(CountryOfDispatch(CountryCode("GB"), true))
      .unsafeSetVal(DestinationCountryPage)(CountryCode("IT"))
      .unsafeSetVal(DestinationOfficePage)(CustomsOffice("id", "name", CountryCode("IT"), None))
      .unsafeSetVal(DeclarationTypePage)(Option1)
      .unsafeSetVal(AddAnotherTransitOfficePage(index))("transitOffice")
      .unsafeSetVal(ArrivalDatesAtOfficePage(index))(LocalDateTime.now)

    "section task is always included" in {
      val viewModel = TaskListViewModel(emptyUserAnswers)

      viewModel.getSection(guaranteeSectionName) must be(defined)
    }

    "status" - {

      "when dependent section is incomplete" - {
        "is Cannot start yet when the dependent section is incomplete" in {
          val viewModel = TaskListViewModel(emptyUserAnswers)

          viewModel.getStatus(guaranteeSectionName).value mustEqual Status.CannotStartYet
        }
      }

      "when dependent section is complete" - {

        "is Not started when there are no answers for the section" in {
          val viewModel = TaskListViewModel(dependentSection)

          viewModel.getStatus(guaranteeSectionName).value mustEqual Status.NotStarted
        }

        "is InProgress when the first question for the section has been answered for non TIR declaration" in {

          val updatedUserAnswers = dependentSection
            .unsafeSetVal(DeclarationTypePage)(Option2)
            .unsafeSetVal(GuaranteeTypePage(index))(GuaranteeType.GuaranteeWaiver)

          val viewModel = TaskListViewModel(updatedUserAnswers)

          viewModel.getStatus(guaranteeSectionName).value mustEqual Status.InProgress
        }

        "is Completed when all the answers are completed for the first Item" in {

          val updatedUserAnswers = dependentSection
            .unsafeSetVal(DeclarationTypePage)(Option2)
            .unsafeSetVal(GuaranteeTypePage(index))(GuaranteeType.GuaranteeWaiver)
            .unsafeSetVal(GuaranteeReferencePage(index))("refNumber")
            .unsafeSetVal(LiabilityAmountPage(index))("5000")
            .unsafeSetVal(AccessCodePage(index))("1234")

          val viewModel = TaskListViewModel(updatedUserAnswers)

          viewModel.getStatus(guaranteeSectionName).value mustEqual Status.Completed
        }

        "is Completed when the first question for the section has been answered for TIR declaration" in {

          val updatedUserAnswers = dependentSection
            .unsafeSetVal(DeclarationTypePage)(Option4)
            .unsafeSetVal(TIRGuaranteeReferencePage(index))("TIRGuarantee")

          val viewModel = TaskListViewModel(updatedUserAnswers)

          viewModel.getStatus(guaranteeSectionName).value mustEqual Status.Completed

        }
      }
    }

    "navigation" - {

      "when dependent section is incomplete" - {
        "when the status is Cannot start yet, user cannot start the journey" in {
          val viewModel = TaskListViewModel(emptyUserAnswers)

          viewModel.getHref(guaranteeSectionName).value.isEmpty mustEqual true
        }
      }

      "when dependent section is complete" - {

        "when the status is Not started, links to the first page for TIR declaration" in {

          val updatedUserAnswers = dependentSection
            .unsafeSetVal(DeclarationTypePage)(Option4)

          val viewModel = TaskListViewModel(updatedUserAnswers)

          val expectedHref: String = controllers.guaranteeDetails.routes.TIRGuaranteeReferenceController.onPageLoad(lrn, index, NormalMode).url

          viewModel.getHref(guaranteeSectionName).value mustEqual expectedHref
        }

        "when the status is Not started, links to the first page for non TIR declaration" in {

          val updatedUserAnswers = dependentSection
            .unsafeSetVal(DeclarationTypePage)(Option2)

          val viewModel = TaskListViewModel(updatedUserAnswers)

          val expectedHref: String = controllers.guaranteeDetails.routes.GuaranteeTypeController.onPageLoad(lrn, index, NormalMode).url

          viewModel.getHref(guaranteeSectionName).value mustEqual expectedHref
        }

        "when the status is InProgress, links to the first page for TIR declaration" in {
          val updatedUserAnswers = dependentSection
            .unsafeSetVal(DeclarationTypePage)(Option4)
            .unsafeSetVal(GuaranteeTypePage(index))(GuaranteeType.GuaranteeWaiver)

          val viewModel = TaskListViewModel(updatedUserAnswers)

          val expectedHref: String = controllers.guaranteeDetails.routes.TIRGuaranteeReferenceController.onPageLoad(lrn, index, NormalMode).url

          viewModel.getHref(guaranteeSectionName).value mustEqual expectedHref
        }

        "when the status is InProgress, links to the first page for non TIR declaration" in {
          val updatedUserAnswers = dependentSection
            .unsafeSetVal(DeclarationTypePage)(Option2)
            .unsafeSetVal(GuaranteeTypePage(index))(GuaranteeType.GuaranteeWaiver)

          val viewModel = TaskListViewModel(updatedUserAnswers)

          val expectedHref: String = controllers.guaranteeDetails.routes.GuaranteeTypeController.onPageLoad(lrn, index, NormalMode).url

          viewModel.getHref(guaranteeSectionName).value mustEqual expectedHref
        }

        "when the status is Completed, links to the add another guarantee page" in {
          val updatedUserAnswers = dependentSection
            .unsafeSetVal(DeclarationTypePage)(Option2)
            .unsafeSetVal(GuaranteeTypePage(index))(GuaranteeType.GuaranteeWaiver)
            .unsafeSetVal(GuaranteeReferencePage(index))("refNumber")
            .unsafeSetVal(LiabilityAmountPage(index))("5000")
            .unsafeSetVal(AccessCodePage(index))("1234")

          val viewModel = TaskListViewModel(updatedUserAnswers)

          val expectedHref: String = controllers.guaranteeDetails.routes.AddAnotherGuaranteeController.onPageLoad(lrn).url

          viewModel.getHref(guaranteeSectionName).value mustEqual expectedHref
        }
      }
    }
  }

}

object TaskListViewModelSpec {

  implicit class TaskListViewModelSpecHelper(vm: TaskListViewModel) {

    def getSection(sectionName: String): Option[JsObject] =
      Json
        .toJson(vm)
        .as[List[JsObject]]
        .find(
          section => (section \ "name").as[String] == sectionName
        )
        .map(_.as[JsObject])

    def getStatus(sectionName: String): Option[Status] =
      getSection(sectionName: String).map(
        section => (section \ "status").as[Status]
      )

    def getHref(sectionName: String): Option[String] =
      getSection(sectionName: String).map(
        section => (section \ "href").as[String]
      )

  }
}
