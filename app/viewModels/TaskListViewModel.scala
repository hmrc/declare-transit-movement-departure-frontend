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

import cats.data.NonEmptyList
import cats.implicits._
import models.DeclarationType.Option4
import models.DependentSection._
import models.ProcedureType.{Normal, Simplified}
import models.journeyDomain.RouteDetails._
import models.journeyDomain.traderDetails.TraderDetails
import models.journeyDomain.{UserAnswersReader, _}
import models.{DependentSection, Index, NormalMode, ProcedureType, UserAnswers}
import pages._
import pages.generalInformation.{ContainersUsedPage, PreLodgeDeclarationPage}
import pages.guaranteeDetails.{GuaranteeTypePage, TIRGuaranteeReferencePage}
import pages.routeDetails.CountryOfDispatchPage
import pages.safetyAndSecurity.AddCircumstanceIndicatorPage
import pages.traderDetails.{IsPrincipalEoriKnownPage, WhatIsPrincipalEoriPage}
import play.api.libs.json._

import scala.language.existentials

private[viewModels] class TaskListViewModel(userAnswers: UserAnswers) {

  private val lrn         = userAnswers.lrn
  private val taskListDsl = new TaskListDslCollectSectionName(userAnswers)

  private def movementDetailsStartPage(procedureType: Option[ProcedureType]): String =
    procedureType match {
      case Some(Normal)     => controllers.movementDetails.routes.PreLodgeDeclarationController.onPageLoad(userAnswers.lrn, NormalMode).url
      case Some(Simplified) => controllers.movementDetails.routes.ContainersUsedController.onPageLoad(userAnswers.lrn, NormalMode).url
      case _                => controllers.routes.SessionExpiredController.onPageLoad().url
    }

  private def movementDetailsInProgressReader: UserAnswersReader[_] =
    ProcedureTypePage.reader.flatMap {
      case Normal     => PreLodgeDeclarationPage.reader
      case Simplified => ContainersUsedPage.reader
    }

  private val movementDetails =
    taskListDsl
      .sectionName("declarationSummary.section.movementDetails")
      .ifNoDependencyOnOtherSection
      .ifCompleted(
        UserAnswersReader[MovementDetails],
        controllers.movementDetails.routes.MovementDetailsCheckYourAnswersController.onPageLoad(userAnswers.lrn).url
      )
      .ifInProgress(
        movementDetailsInProgressReader,
        movementDetailsStartPage(userAnswers.get(ProcedureTypePage))
      )
      .ifNotStarted(movementDetailsStartPage(userAnswers.get(ProcedureTypePage)))

  private val routeDetails =
    taskListDsl
      .sectionName("declarationSummary.section.routes")
      .ifNoDependencyOnOtherSection
      .ifCompleted(
        UserAnswersReader[RouteDetails],
        controllers.routeDetails.routes.RouteDetailsCheckYourAnswersController.onPageLoad(lrn).url
      )
      .ifInProgress(
        CountryOfDispatchPage.reader,
        controllers.routeDetails.routes.CountryOfDispatchController.onPageLoad(lrn, NormalMode).url
      )
      .ifNotStarted(controllers.routeDetails.routes.CountryOfDispatchController.onPageLoad(lrn, NormalMode).url)

  private val transportDetails =
    taskListDsl
      .sectionName("declarationSummary.section.transport")
      .ifDependentSectionCompleted(dependentSectionReader(DependentSection.TransportDetails, userAnswers))
      .ifCompleted(
        UserAnswersReader[TransportDetails],
        controllers.transportDetails.routes.TransportDetailsCheckYourAnswersController.onPageLoad(lrn).url
      )
      .ifInProgress(
        InlandModePage.reader,
        controllers.transportDetails.routes.InlandModeController.onPageLoad(lrn, NormalMode).url
      )
      .ifNotStarted(controllers.transportDetails.routes.InlandModeController.onPageLoad(lrn, NormalMode).url)

  private def traderDetailsStartPage(procedureType: Option[ProcedureType]): String =
    procedureType match {
      case Some(Normal)     => controllers.traderDetails.routes.IsPrincipalEoriKnownController.onPageLoad(userAnswers.lrn, NormalMode).url
      case Some(Simplified) => controllers.traderDetails.routes.WhatIsPrincipalEoriController.onPageLoad(userAnswers.lrn, NormalMode).url
      case _                => controllers.routes.SessionExpiredController.onPageLoad().url
    }

  private def traderDetailsInProgressReader: UserAnswersReader[_] =
    ProcedureTypePage.reader.flatMap {
      case Normal     => IsPrincipalEoriKnownPage.reader
      case Simplified => WhatIsPrincipalEoriPage.reader.map(_.nonEmpty)
    }

  private val traderDetails =
    taskListDsl
      .sectionName("declarationSummary.section.tradersDetails")
      .ifNoDependencyOnOtherSection
      .ifCompleted(
        UserAnswersReader[TraderDetails],
        controllers.traderDetails.routes.TraderDetailsCheckYourAnswersController.onPageLoad(userAnswers.lrn).url
      )
      .ifInProgress(
        traderDetailsInProgressReader,
        traderDetailsStartPage(userAnswers.get(ProcedureTypePage))
      )
      .ifNotStarted(traderDetailsStartPage(userAnswers.get(ProcedureTypePage)))

  private val itemDetails =
    taskListDsl
      .sectionName("declarationSummary.section.addItems")
      .ifDependentSectionCompleted(dependentSectionReader(DependentSection.ItemDetails, userAnswers))
      .ifCompleted(
        UserAnswersReader[NonEmptyList[ItemSection]],
        controllers.addItems.routes.AddAnotherItemController.onPageLoad(userAnswers.lrn).url
      )
      .ifInProgress(
        ItemDescriptionPage(Index(0)).reader,
        controllers.addItems.itemDetails.routes.ItemDescriptionController.onPageLoad(userAnswers.lrn, Index(0), NormalMode).url
      )
      .ifNotStarted(controllers.addItems.itemDetails.routes.ConfirmStartAddItemsController.onPageLoad(userAnswers.lrn).url)

  private def goodsSummaryStartPage(procedureType: Option[ProcedureType], safetyAndSecurity: Option[Boolean], prelodgedDeclaration: Option[Boolean]): String =
    (procedureType, safetyAndSecurity, prelodgedDeclaration) match {
      case (_, Some(true), _) => controllers.routes.LoadingPlaceController.onPageLoad(userAnswers.lrn, NormalMode).url
      case (Some(Normal), Some(false), Some(false)) =>
        controllers.goodsSummary.routes.AddCustomsApprovedLocationController.onPageLoad(userAnswers.lrn, NormalMode).url
      case (Some(Normal), Some(false), Some(true)) =>
        controllers.goodsSummary.routes.AddAgreedLocationOfGoodsController.onPageLoad(userAnswers.lrn, NormalMode).url
      case (Some(Simplified), Some(false), _) => controllers.goodsSummary.routes.AuthorisedLocationCodeController.onPageLoad(userAnswers.lrn, NormalMode).url
      case _                                  => controllers.routes.SessionExpiredController.onPageLoad().url
    }

  private def goodsSummaryInProgressReader(procedureType: Option[ProcedureType],
                                           safetyAndSecurity: Option[Boolean],
                                           prelodgedDeclaration: Option[Boolean]
  ): UserAnswersReader[_] =
    (procedureType, safetyAndSecurity, prelodgedDeclaration) match {
      case (_, Some(true), _)                       => LoadingPlacePage.reader
      case (Some(Normal), Some(false), Some(false)) => AddCustomsApprovedLocationPage.reader
      case (Some(Normal), Some(false), Some(true))  => AddAgreedLocationOfGoodsPage.reader
      case (Some(Simplified), Some(false), _)       => AuthorisedLocationCodePage.reader.map(_.nonEmpty)
      case _                                        => AddSealsPage.reader
    }

  private val goodsSummaryDetails =
    taskListDsl
      .sectionName("declarationSummary.section.goodsSummary")
      .conditionalDependencyOnSection(dependentSectionReader(DependentSection.GoodsSummary, userAnswers))(
        userAnswers.get(ProcedureTypePage).contains(ProcedureType.Normal)
      )
      .ifCompleted(
        UserAnswersReader[GoodsSummary],
        controllers.goodsSummary.routes.GoodsSummaryCheckYourAnswersController.onPageLoad(lrn).url
      )
      .ifInProgress(
        goodsSummaryInProgressReader(userAnswers.get(ProcedureTypePage), userAnswers.get(AddSecurityDetailsPage), userAnswers.get(PreLodgeDeclarationPage)),
        goodsSummaryStartPage(userAnswers.get(ProcedureTypePage), userAnswers.get(AddSecurityDetailsPage), userAnswers.get(PreLodgeDeclarationPage))
      )
      .ifNotStarted(
        goodsSummaryStartPage(userAnswers.get(ProcedureTypePage), userAnswers.get(AddSecurityDetailsPage), userAnswers.get(PreLodgeDeclarationPage))
      )

  private def guaranteeDetailsInProgressReader(userAnswers: UserAnswers) =
    userAnswers.get(DeclarationTypePage) match {
      case Some(Option4) => TIRGuaranteeReferencePage(Index(0)).reader
      case _             => GuaranteeTypePage(Index(0)).reader
    }

  private def guaranteeDetailsStartPage(userAnswers: UserAnswers) =
    userAnswers.get(DeclarationTypePage) match {
      case Some(Option4) => controllers.guaranteeDetails.routes.TIRGuaranteeReferenceController.onPageLoad(lrn, Index(0), NormalMode).url
      case Some(_)       => controllers.guaranteeDetails.routes.GuaranteeTypeController.onPageLoad(lrn, Index(0), NormalMode).url
      case None          => controllers.routes.SessionExpiredController.onPageLoad().url
    }

  private val guaranteeDetails =
    taskListDsl
      .sectionName("declarationSummary.section.guarantee")
      .ifDependentSectionCompleted(dependentSectionReader(DependentSection.GuaranteeDetails, userAnswers))
      .ifCompleted(
        UserAnswersReader[NonEmptyList[GuaranteeDetails]],
        controllers.guaranteeDetails.routes.AddAnotherGuaranteeController.onPageLoad(lrn).url
      )
      .ifInProgress(
        guaranteeDetailsInProgressReader(userAnswers),
        guaranteeDetailsStartPage(userAnswers)
      )
      .ifNotStarted(guaranteeDetailsStartPage(userAnswers))

  private val safetyAndSecurityDetails = userAnswers.get(AddSecurityDetailsPage) match {
    case Some(true) =>
      Seq(
        taskListDsl
          .sectionName("declarationSummary.section.safetyAndSecurity")
          .ifDependentSectionCompleted(dependentSectionReader(DependentSection.SafetyAndSecurity, userAnswers))
          .ifCompleted(
            UserAnswersReader[SafetyAndSecurity],
            controllers.safetyAndSecurity.routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(lrn).url
          )
          .ifInProgress(
            AddCircumstanceIndicatorPage.reader,
            controllers.safetyAndSecurity.routes.AddCircumstanceIndicatorController.onPageLoad(lrn, NormalMode).url
          )
          .ifNotStarted(controllers.safetyAndSecurity.routes.AddCircumstanceIndicatorController.onPageLoad(lrn, NormalMode).url)
      )

    case _ => Seq.empty
  }

  private val sections: Seq[TaskListDsl[_, _, _]] = Seq(
    movementDetails,
    routeDetails,
    traderDetails,
    transportDetails
  ) ++ safetyAndSecurityDetails ++ Seq(
    itemDetails,
    goodsSummaryDetails,
    guaranteeDetails
  )

  private val sectionDetails                    = sections.map(_.section)
  val sectionErrors: Seq[(String, ReaderError)] = sections.flatMap(_.collectReaderErrors)
}

object TaskListViewModel {

  object Constants {
    val sections: String = "sections"
  }

  def apply(userAnswers: UserAnswers): TaskListViewModel = new TaskListViewModel(userAnswers)

  implicit val writes: Writes[TaskListViewModel] =
    taskListViewModel => Json.toJson(taskListViewModel.sectionDetails)

}
