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
import models.DependentSection._
import models.ProcedureType.{Normal, Simplified}
import models.journeyDomain.traderDetails.TraderDetails
import models.journeyDomain.{UserAnswersReader, _}
import models.{DependentSection, Index, NormalMode, ProcedureType, UserAnswers, ValidateTaskListViewLogger}
import pages._
import pages.guaranteeDetails.GuaranteeTypePage
import pages.movementDetails.PreLodgeDeclarationPage
import pages.safetyAndSecurity.AddCircumstanceIndicatorPage
import play.api.libs.json._

private[viewModels] class TaskListViewModel(userAnswers: UserAnswers) {

  private val lrn         = userAnswers.id
  private val taskListDsl = new TaskListDslCollectSectionName(userAnswers)

  private val movementDetails =
    taskListDsl
      .sectionName("declarationSummary.section.movementDetails")
      .ifNoDependencyOnOtherSection
      .ifCompleted(
        UserAnswersReader[MovementDetails],
        controllers.movementDetails.routes.MovementDetailsCheckYourAnswersController.onPageLoad(userAnswers.id).url
      )
      .ifInProgress(
        DeclarationTypePage.reader,
        controllers.movementDetails.routes.DeclarationTypeController.onPageLoad(userAnswers.id, NormalMode).url
      )
      .ifNotStarted(controllers.movementDetails.routes.DeclarationTypeController.onPageLoad(userAnswers.id, NormalMode).url)

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
      case Some(Normal)     => controllers.traderDetails.routes.IsPrincipalEoriKnownController.onPageLoad(userAnswers.id, NormalMode).url
      case Some(Simplified) => controllers.traderDetails.routes.WhatIsPrincipalEoriController.onPageLoad(userAnswers.id, NormalMode).url
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
        controllers.traderDetails.routes.TraderDetailsCheckYourAnswersController.onPageLoad(userAnswers.id).url
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
        controllers.addItems.routes.AddAnotherItemController.onPageLoad(userAnswers.id).url
      )
      .ifInProgress(
        ItemDescriptionPage(Index(0)).reader,
        controllers.addItems.routes.ItemDescriptionController.onPageLoad(userAnswers.id, Index(0), NormalMode).url
      )
      .ifNotStarted(controllers.addItems.routes.ItemDescriptionController.onPageLoad(userAnswers.id, Index(0), NormalMode).url)

  private def goodsSummaryStartPage(procedureType: Option[ProcedureType], safetyAndSecurity: Option[Boolean], prelodgedDeclaration: Option[Boolean]): String =
    (procedureType, safetyAndSecurity, prelodgedDeclaration) match {
      case (_, Some(true), _) => controllers.routes.LoadingPlaceController.onPageLoad(userAnswers.id, NormalMode).url
      case (Some(Normal), Some(false), Some(false)) =>
        controllers.goodsSummary.routes.AddCustomsApprovedLocationController.onPageLoad(userAnswers.id, NormalMode).url
      case (Some(Normal), Some(false), Some(true)) =>
        controllers.goodsSummary.routes.AddAgreedLocationOfGoodsController.onPageLoad(userAnswers.id, NormalMode).url
      case (Some(Simplified), Some(false), _) => controllers.goodsSummary.routes.AuthorisedLocationCodeController.onPageLoad(userAnswers.id, NormalMode).url
      case _                                  => controllers.routes.SessionExpiredController.onPageLoad().url
    }

  private def goodsSummaryInProgressReader(procedureType: Option[ProcedureType],
                                           safetyAndSecurity: Option[Boolean],
                                           prelodgedDeclaration: Option[Boolean]
  ): UserAnswersReader[Any] =
//    (procedureType, safetyAndSecurity, prelodgedDeclaration) match {
//      case (_, Some(true), _)                       => LoadingPlacePage.reader
//      case (Some(Normal), Some(false), Some(false)) => AddCustomsApprovedLocationPage.reader
//      case (Some(Normal), Some(false), Some(true))  => AddAgreedLocationOfGoodsPage.reader
//      case (Some(Simplified), Some(false), _)       => AuthorisedLocationCodePage.reader.map(_.nonEmpty)
//      case _                                        => AddSealsPage.reader
//    }
    LoadingPlacePage.reader.widen[Any] orElse AddCustomsApprovedLocationPage.reader.widen[Any] orElse AddAgreedLocationOfGoodsPage.reader
      .widen[Any] orElse AuthorisedLocationCodePage.reader.map(_.nonEmpty)

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

  private val guaranteeDetails =
    taskListDsl
      .sectionName("declarationSummary.section.guarantee")
      .ifDependentSectionCompleted(dependentSectionReader(DependentSection.GuaranteeDetails, userAnswers))
      .ifCompleted(
        UserAnswersReader[NonEmptyList[GuaranteeDetails]],
        controllers.guaranteeDetails.routes.AddAnotherGuaranteeController.onPageLoad(lrn).url
      )
      .ifInProgress(
        GuaranteeTypePage(Index(0)).reader,
        controllers.guaranteeDetails.routes.GuaranteeTypeController.onPageLoad(lrn, Index(0), NormalMode).url
      )
      .ifNotStarted(controllers.guaranteeDetails.routes.GuaranteeTypeController.onPageLoad(lrn, Index(0), NormalMode).url)

  private val safetyAndSecurityDetails =
    userAnswers
      .get(AddSecurityDetailsPage)
      .map({
        case true =>
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
      })
      .getOrElse(Seq.empty)

  private val sections = Seq(
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
