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

package navigation

import controllers.addItems.routes
import derivable.{DeriveNumberOfDocuments, DeriveNumberOfPreviousAdministrativeReferences}
import models.{CheckMode, DeclarationType, Index, Mode, NormalMode, UserAnswers}
import pages.addItems._
import pages.{DeclarationTypePage, Page}
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class DocumentNavigator @Inject() () extends Navigator {

  // format: off
  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddDocumentsPage(index)                               => ua => addDocumentRoute(ua, index, NormalMode)
    case DocumentTypePage(index, documentIndex)                => ua => Some(routes.DocumentReferenceController.onPageLoad(ua.lrn, index, documentIndex, NormalMode))
    case DocumentReferencePage(index, documentIndex)           => ua => Some(routes.AddExtraDocumentInformationController.onPageLoad(ua.lrn, index, documentIndex, NormalMode))
    case AddExtraDocumentInformationPage(index, documentIndex) => ua => addExtraDocumentInformationRoute(ua, index, documentIndex, NormalMode)
    case DocumentExtraInformationPage(index, _)                => ua => Some(routes.AddAnotherDocumentController.onPageLoad(ua.lrn, index, NormalMode))
    case AddAnotherDocumentPage(index)                         => ua => addAnotherDocumentRoute(ua, index, NormalMode)
    case ConfirmRemoveDocumentPage(index, _)                   => ua => Some(confirmRemoveDocumentRoute(ua,index, NormalMode))
    case TIRCarnetReferencePage(index, documentIndex)          => ua => Some(routes.DocumentExtraInformationController.onPageLoad(ua.lrn, index, documentIndex, NormalMode))
  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddDocumentsPage(index)                               => ua => addDocumentRoute(ua, index, CheckMode)
    case DocumentTypePage(index, documentIndex)                => ua => Some(routes.DocumentReferenceController.onPageLoad(ua.lrn, index, documentIndex, CheckMode))
    case DocumentReferencePage(index, documentIndex)           => ua => Some(routes.AddExtraDocumentInformationController.onPageLoad(ua.lrn, index, documentIndex, CheckMode))
    case AddExtraDocumentInformationPage(index, documentIndex) => ua => addExtraDocumentInformationRoute(ua, index, documentIndex, CheckMode)
    case DocumentExtraInformationPage(index, _)                => ua => Some(controllers.addItems.routes.AddAnotherDocumentController.onPageLoad(ua.lrn, index, CheckMode))
    case AddAnotherDocumentPage(index)                         => ua => addAnotherDocumentRoute(ua, index, CheckMode)
    case ConfirmRemoveDocumentPage(index, _)                   => ua => Some(confirmRemoveDocumentRoute(ua,index, CheckMode))
    case TIRCarnetReferencePage(index, documentIndex)          => ua => Some(routes.DocumentExtraInformationController.onPageLoad(ua.lrn, index, documentIndex, CheckMode))
  }

  private def confirmRemoveDocumentRoute(ua: UserAnswers, index: Index, mode: Mode) =
    ua.get(DeriveNumberOfDocuments(index)).getOrElse(0) match {
      case 0 => routes.AddDocumentsController.onPageLoad(ua.lrn, index,  mode)
      case _ => routes.AddAnotherDocumentController.onPageLoad(ua.lrn, index, mode)
    }

  private def previousReferencesRoute(ua:UserAnswers, index:Index, mode:Mode) = {
    val declarationTypes = Seq(DeclarationType.Option2, DeclarationType.Option3)
    val isAllowedDeclarationType: Boolean = ua.get(DeclarationTypePage).fold(false)(declarationTypes.contains(_))
    val referenceIndex = ua.get(DeriveNumberOfPreviousAdministrativeReferences(index)).getOrElse(0)
    val countryOfDeparture: Option[Boolean] = ua.get(IsNonEuOfficePage)

    (countryOfDeparture, isAllowedDeclarationType) match {
      case (Some(true), true) => Some(controllers.addItems.previousReferences.routes.ReferenceTypeController.onPageLoad(ua.lrn, index, Index(referenceIndex), mode))
      case _ => Some(controllers.addItems.previousReferences.routes.AddAdministrativeReferenceController.onPageLoad(ua.lrn, index, mode))
    }
  }

  private def addAnotherDocumentRoute(ua:UserAnswers, index:Index, mode:Mode) =
    (ua.get(AddAnotherDocumentPage(index)), mode) match {
      case (Some(true), _) => Some(routes.DocumentTypeController.onPageLoad(ua.lrn, index, Index(count(index)(ua)), mode))
      case (Some(false), NormalMode) => previousReferencesRoute(ua, index, mode)
      case (Some(false), CheckMode) => Some(routes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
  }

  private def addExtraDocumentInformationRoute(ua:UserAnswers, index:Index, documentIndex:Index, mode:Mode) =
    ua.get(AddExtraDocumentInformationPage(index, documentIndex)) match {
      case Some(true) => Some(routes.DocumentExtraInformationController.onPageLoad(ua.lrn, index,documentIndex, mode))
      case Some(false) => Some(routes.AddAnotherDocumentController.onPageLoad(ua.lrn, index, mode))
    }

  private def addDocumentRoute(ua:UserAnswers, index: Index,  mode:Mode) =

    (ua.get(AddDocumentsPage(index)), mode) match {
      case (Some(true), NormalMode)  => Some(routes.DocumentTypeController.onPageLoad(ua.lrn, index, Index(count(index)(ua)), NormalMode))
      case (Some(false), NormalMode) => previousReferencesRoute(ua, index, mode)
      case (Some(true), CheckMode) if (count(index)(ua) == 0) => {
        Some(routes.DocumentTypeController.onPageLoad(ua.lrn, index, Index(count(index)(ua)), CheckMode))
      }
      case _ => Some(routes.ItemsCheckYourAnswersController.onPageLoad(ua.lrn, index))
    }
  
  private val count: Index => UserAnswers => Int =
    index => ua => ua.get(DeriveNumberOfDocuments(index)).getOrElse(0)

  // format: on
}
