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

import controllers.guaranteeDetails.routes
import derivable.DeriveNumberOfGuarantees
import javax.inject.{Inject, Singleton}
import models.GuaranteeType._
import models._
import models.reference.CountryCode
import pages._
import pages.guaranteeDetails._
import play.api.mvc.Call

@Singleton
class GuaranteeDetailsNavigator @Inject() () extends Navigator {
// format: off

  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddAnotherGuaranteePage                  => ua => addAnotherGuaranteeRoute(ua)
    case ConfirmRemoveGuaranteePage               => ua => confirmRemoveGuaranteeRoute(ua)
    case GuaranteeTypePage(index)                 => ua => guaranteeTypeRoute(ua, index, NormalMode)
    case OtherReferencePage(index)                => ua => Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.id, index))
    case GuaranteeReferencePage(index)            => ua => guaranteeReferenceNormalRoutes(ua, index, NormalMode)
    case TIRGuaranteeReferencePage(index)         => ua => Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.id, index))
    case LiabilityAmountPage(index)               => ua => Some(routes.AccessCodeController.onPageLoad(ua.id, index, NormalMode))
    case OtherReferenceLiabilityAmountPage(index) => ua => otherReferenceLiablityAmountRoute(ua, index, NormalMode)
    case DefaultAmountPage(index)                 => ua => defaultAmountRoute(ua, index, NormalMode)
    case AccessCodePage(index)                    => ua => Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.id, index))
  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddAnotherGuaranteePage                  => ua => addAnotherGuaranteeRoute(ua)
    case ConfirmRemoveGuaranteePage               => ua => confirmRemoveGuaranteeRoute(ua)
    case GuaranteeTypePage(index)                 => ua => guaranteeTypeRoute(ua, index, CheckMode)
    case OtherReferencePage(index)                => ua => Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.id, index))
    case GuaranteeReferencePage(index)            => ua => guaranteeReferenceRoutes(ua, index)
    case TIRGuaranteeReferencePage(index)         => ua => Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.id, index))
    case LiabilityAmountPage(index)               => ua => liabilityAmountRoute(ua, index, CheckMode)
    case OtherReferenceLiabilityAmountPage(index) => ua => otherReferenceLiablityAmountRoute(ua, index, CheckMode)
    case DefaultAmountPage(index)                 => ua => defaultAmountRoute(ua, index, CheckMode)
    case AccessCodePage(index)                    => ua => Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.id, index))
    case _                                        => ua => Some(routes.AddAnotherGuaranteeController.onPageLoad(ua.id))
  }

  def otherReferenceLiablityAmountRoute(ua: UserAnswers, index: Index, mode: Mode) =
    (ua.get(LiabilityAmountPage(index)), ua.get(AccessCodePage(index)), mode) match {
      case (None, _, _)                  => Some(routes.DefaultAmountController.onPageLoad(ua.id, index, mode))
      case (Some(_), _, NormalMode)      => Some(routes.AccessCodeController.onPageLoad(ua.id, index, NormalMode))
      case (Some(_), Some(_), CheckMode) => Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.id, index))
      case (Some(_), None, CheckMode)    => Some(routes.AccessCodeController.onPageLoad(ua.id, index, CheckMode))
    }

  def guaranteeReferenceNormalRoutes(ua: UserAnswers, index: Index, mode: Mode) =
    (ua.get(OfficeOfDeparturePage), ua.get(DestinationOfficePage)) match {
      case (Some(departureOffice), Some(destinationOffice))
          if departureOffice.countryId == CountryCode("GB") && (destinationOffice.countryId == CountryCode("GB")) =>
        Some(routes.LiabilityAmountController.onPageLoad(ua.id, index, mode))
      case _ => Some(routes.OtherReferenceLiabilityAmountController.onPageLoad(ua.id, index, mode))
    }

  def guaranteeReferenceRoutes(ua: UserAnswers, index: Index) =
    (ua.get(LiabilityAmountPage(index)), ua.get(AccessCodePage(index))) match {
      case (None, _) => guaranteeReferenceNormalRoutes(ua: UserAnswers, index, CheckMode)
      case (_, None) => Some(routes.AccessCodeController.onPageLoad(ua.id, index, CheckMode))
      case _         => Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.id, index))
    }

  def liabilityAmountRoute(ua: UserAnswers, index: Index, mode: Mode) =
    (ua.get(GuaranteeTypePage(index)), ua.get(LiabilityAmountPage(index)), ua.get(AccessCodePage(index)), mode) match {
      case (Some(_), Some(""), _, NormalMode) => Some(routes.DefaultAmountController.onPageLoad(ua.id, index, NormalMode))
      case (Some(_), Some(_), _, NormalMode)  => Some(routes.AccessCodeController.onPageLoad(ua.id, index, NormalMode))
      case (Some(_), None, _, NormalMode)     => Some(routes.DefaultAmountController.onPageLoad(ua.id, index, NormalMode))
      case (_, _, None, CheckMode)            => Some(routes.AccessCodeController.onPageLoad(ua.id, index, CheckMode))
      case _                                  => Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.id, index))
    }

  def defaultAmountRoute(ua: UserAnswers, index: Index, mode: Mode) =
    (ua.get(DefaultAmountPage(index)), ua.get(AccessCodePage(index)), mode) match {
      case (Some(true), _, NormalMode)    => Some(routes.AccessCodeController.onPageLoad(ua.id, index, mode))
      case (Some(true), None, CheckMode)  => Some(routes.AccessCodeController.onPageLoad(ua.id, index, mode))
      case (Some(false), _, _)            => Some(routes.OtherReferenceLiabilityAmountController.onPageLoad(ua.id, index, mode))
      case _                              => Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.id, index))
    }

  def addAnotherGuaranteeRoute(ua: UserAnswers): Option[Call] = {
    val count = ua.get(DeriveNumberOfGuarantees).getOrElse(1)

    (count, ua.get(AddAnotherGuaranteePage)) match {
      case (AddAnotherGuaranteePage.maxAllowedGuarantees, _) | (_, Some(false)) => Some(controllers.routes.DeclarationSummaryController.onPageLoad(ua.id))
      case _                                                                    => Some(routes.GuaranteeTypeController.onPageLoad(ua.id, Index(count), NormalMode))
    }
  }

  def confirmRemoveGuaranteeRoute(ua: UserAnswers): Option[Call] = {
    val count = ua.get(DeriveNumberOfGuarantees).getOrElse(0)

    ua.get(ConfirmRemoveGuaranteePage).map {
      case true if count == 0 => routes.GuaranteeTypeController.onPageLoad(ua.id, Index(count), NormalMode)
      case _                  => routes.AddAnotherGuaranteeController.onPageLoad(ua.id)
    }
  }

  def guaranteeTypeRoute(ua: UserAnswers, index: Index, mode: Mode): Option[Call] =
    (ua.get(GuaranteeTypePage(index)), ua.get(GuaranteeReferencePage(index)), ua.get(OtherReferencePage(index)), mode) match {
      case (Some(guaranteeType), _, _, NormalMode) if guaranteeReferenceRoute.contains(guaranteeType) =>
        Some(routes.GuaranteeReferenceController.onPageLoad(ua.id, index, NormalMode))
      case (Some(guaranteeType), None, _, NormalMode) if nonGuaranteeReferenceRoute.contains(guaranteeType) =>
        Some(routes.OtherReferenceController.onPageLoad(ua.id, index, NormalMode))

      case (Some(guaranteeType), Some(_), _, CheckMode) if guaranteeReferenceRoute.contains(guaranteeType) =>
        Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.id, index))

      case (Some(guaranteeType), _, Some(_), CheckMode) if nonGuaranteeReferenceRoute.contains(guaranteeType) =>
        Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.id, index))

      case (Some(guaranteeType), _, None, CheckMode) if nonGuaranteeReferenceRoute.contains(guaranteeType) =>
        Some(routes.OtherReferenceController.onPageLoad(ua.id, index, CheckMode))

      case (Some(guaranteeType), None, None, CheckMode) if guaranteeReferenceRoute.contains(guaranteeType) =>
        Some(routes.GuaranteeReferenceController.onPageLoad(ua.id, index, CheckMode))

      case _ => Some(routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(ua.id, index))
    }

  // format: on
}
