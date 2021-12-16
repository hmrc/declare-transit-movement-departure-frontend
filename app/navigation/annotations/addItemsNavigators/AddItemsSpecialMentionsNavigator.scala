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

package navigation.annotations.addItemsNavigators

import controllers.addItems.specialMentions.routes
import derivable.{DeriveNumberOfDocuments, DeriveNumberOfSpecialMentions}
import models.DeclarationType.Option4
import models.reference.CircumstanceIndicator
import models.{CheckMode, Index, Mode, NormalMode, UserAnswers}
import navigation.Navigator
import pages.addItems.specialMentions._
import pages.safetyAndSecurity.{AddCircumstanceIndicatorPage, AddCommercialReferenceNumberPage, CircumstanceIndicatorPage}
import pages.{AddSecurityDetailsPage, DeclarationTypePage, Page}
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class AddItemsSpecialMentionsNavigator @Inject() () extends Navigator {

  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] =
    Seq(
      addSpecialMentionPage,
      specialMentionTypePage,
      specialMentionAdditionalInfoPage,
      addAnotherSpecialMentionPage,
      removeSpecialMentionPage
    ).reduce(_ orElse _)

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddSpecialMentionPage(itemIndex) =>
      userAnswers =>
        (userAnswers.get(AddSpecialMentionPage(itemIndex)), count(itemIndex)(userAnswers)) match {
          case (Some(true), specialMentionCount) if specialMentionCount == 0 =>
            Some(routes.SpecialMentionTypeController.onPageLoad(userAnswers.lrn, itemIndex, Index(specialMentionCount), CheckMode))
          case (Some(true), _) => Some(routes.AddAnotherSpecialMentionController.onPageLoad(userAnswers.lrn, itemIndex, CheckMode))
          case _               => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(userAnswers.lrn, itemIndex))
        }
    case SpecialMentionTypePage(itemIndex, referenceIndex) =>
      userAnswers => Some(routes.SpecialMentionAdditionalInfoController.onPageLoad(userAnswers.lrn, itemIndex, referenceIndex, CheckMode))
    case SpecialMentionAdditionalInfoPage(itemIndex, referenceIndex) =>
      userAnswers => Some(routes.SpecialMentionCheckYourAnswersController.onPageLoad(userAnswers.lrn, itemIndex, referenceIndex, CheckMode))
    case AddAnotherSpecialMentionPage(itemIndex) =>
      userAnswers =>
        userAnswers.get(AddAnotherSpecialMentionPage(itemIndex)) match {
          case Some(true) => Some(routes.SpecialMentionTypeController.onPageLoad(userAnswers.lrn, itemIndex, Index(count(itemIndex)(userAnswers)), CheckMode))
          case _          => Some(controllers.addItems.routes.ItemsCheckYourAnswersController.onPageLoad(userAnswers.lrn, itemIndex))
        }
    case RemoveSpecialMentionPage(itemIndex, _) =>
      userAnswers =>
        if (count(itemIndex)(userAnswers) == 0) {
          Some(routes.AddSpecialMentionController.onPageLoad(userAnswers.lrn, itemIndex, CheckMode))
        } else {
          Some(routes.AddAnotherSpecialMentionController.onPageLoad(userAnswers.lrn, itemIndex, CheckMode))
        }
  }

  private def addSpecialMentionPage: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddSpecialMentionPage(itemIndex) =>
      userAnswers =>
        userAnswers.get(AddSpecialMentionPage(itemIndex)) match {
          case Some(true) => Some(routes.SpecialMentionTypeController.onPageLoad(userAnswers.lrn, itemIndex, Index(count(itemIndex)(userAnswers)), NormalMode))
          case _          => documentsJourney(userAnswers, itemIndex, NormalMode)
        }
  }

  private def specialMentionTypePage: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case SpecialMentionTypePage(itemIndex, referenceIndex) =>
      userAnswers => Some(routes.SpecialMentionAdditionalInfoController.onPageLoad(userAnswers.lrn, itemIndex, referenceIndex, NormalMode))
  }

  private def specialMentionAdditionalInfoPage: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case SpecialMentionAdditionalInfoPage(itemIndex, referenceIndex) =>
      userAnswers => Some(routes.SpecialMentionCheckYourAnswersController.onPageLoad(userAnswers.lrn, itemIndex, referenceIndex, NormalMode))
  }

  private def addAnotherSpecialMentionPage: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddAnotherSpecialMentionPage(itemIndex) =>
      userAnswers =>
        userAnswers.get(AddAnotherSpecialMentionPage(itemIndex)) match {
          case Some(true) => Some(routes.SpecialMentionTypeController.onPageLoad(userAnswers.lrn, itemIndex, Index(count(itemIndex)(userAnswers)), NormalMode))
          case _ =>
            documentsJourney(userAnswers, itemIndex, NormalMode)
        }
    case RemoveSpecialMentionPage(itemIndex, _) =>
      userAnswers =>
        if (count(itemIndex)(userAnswers) == 0) {
          Some(routes.AddSpecialMentionController.onPageLoad(userAnswers.lrn, itemIndex, NormalMode))
        } else {
          Some(routes.AddAnotherSpecialMentionController.onPageLoad(userAnswers.lrn, itemIndex, NormalMode))
        }
  }

  private def showDocumentTypePage(userAnswers: UserAnswers, itemIndex: Index): Option[Boolean] =
    (userAnswers.get(AddSecurityDetailsPage),
     userAnswers.get(AddCircumstanceIndicatorPage),
     userAnswers.get(AddCommercialReferenceNumberPage),
     itemIndex.position == 0
    ) match {
      case (Some(true), Some(false), Some(false), true) => Some(true)
      case (Some(true), Some(true), Some(false), true) =>
        userAnswers.get(CircumstanceIndicatorPage) map (CircumstanceIndicator.conditionalIndicators.contains(_))
      case (Some(_), _, _, _) => Some(false)
      case _                  => None
    }

  private def documentsJourney(userAnswers: UserAnswers, itemIndex: Index, mode: Mode): Option[Call] = {

    val documentIndex = Index(userAnswers.get(DeriveNumberOfDocuments(itemIndex)).getOrElse(0))

    userAnswers.get(DeclarationTypePage).flatMap {
      case Option4 if itemIndex.position == 0 && documentIndex.position == 0 =>
        Some(controllers.addItems.documents.routes.TIRCarnetReferenceController.onPageLoad(userAnswers.lrn, itemIndex, documentIndex, mode))
      case _ =>
        showDocumentTypePage(userAnswers, itemIndex).map {
          case true  => controllers.addItems.documents.routes.DocumentTypeController.onPageLoad(userAnswers.lrn, itemIndex, documentIndex, mode)
          case false => controllers.addItems.documents.routes.AddDocumentsController.onPageLoad(userAnswers.lrn, itemIndex, mode)
        }
    }
  }

  private def removeSpecialMentionPage(): PartialFunction[Page, UserAnswers => Option[Call]] = {
    case RemoveSpecialMentionPage(itemIndex, _) =>
      userAnswers =>
        if (count(itemIndex)(userAnswers) == 0) {
          Some(routes.AddSpecialMentionController.onPageLoad(userAnswers.lrn, itemIndex, NormalMode))
        } else {
          Some(routes.AddAnotherSpecialMentionController.onPageLoad(userAnswers.lrn, itemIndex, NormalMode))
        }
  }

  private val count: Index => UserAnswers => Int =
    index => userAnswers => userAnswers.get(DeriveNumberOfSpecialMentions(index)).getOrElse(0)
}
