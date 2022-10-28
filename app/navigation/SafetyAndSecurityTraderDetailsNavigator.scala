/*
 * Copyright 2022 HM Revenue & Customs
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

import controllers.safetyAndSecurity.routes
import models._
import models.reference.{CountryCode, CustomsOffice}
import pages.safetyAndSecurity._
import pages.{OfficeOfDeparturePage, Page}
import play.api.Logging
import play.api.mvc.Call

import javax.inject.Singleton

@Singleton
class SafetyAndSecurityTraderDetailsNavigator extends Navigator with Logging {

  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddSafetyAndSecurityConsignorPage     => ua => addSafetyAndSecurityConsignorNormalRoute(ua)
    case AddSafetyAndSecurityConsignorEoriPage => ua => Some(addSafetyAndSecurityConsignorEoriRoute(ua, NormalMode))
    case SafetyAndSecurityConsignorEoriPage    => ua => Some(routes.AddSafetyAndSecurityConsigneeController.onPageLoad(ua.lrn, NormalMode))
    case SafetyAndSecurityConsignorNamePage    => ua => Some(routes.SafetyAndSecurityConsignorAddressController.onPageLoad(ua.lrn, NormalMode))
    case SafetyAndSecurityConsignorAddressPage => ua => Some(routes.AddSafetyAndSecurityConsigneeController.onPageLoad(ua.lrn, NormalMode))
    case AddSafetyAndSecurityConsigneePage     => ua => addSafetyAndSecurityConsigneeRouteNormalRoute(ua)
    case AddSafetyAndSecurityConsigneeEoriPage => ua => Some(addSafetyAndSecurityConsigneeEoriRoute(ua, NormalMode))
    case SafetyAndSecurityConsigneeEoriPage    => ua => Some(routes.AddCarrierController.onPageLoad(ua.lrn, NormalMode))
    case SafetyAndSecurityConsigneeNamePage    => ua => Some(routes.SafetyAndSecurityConsigneeAddressController.onPageLoad(ua.lrn, NormalMode))
    case SafetyAndSecurityConsigneeAddressPage => ua => Some(routes.AddCarrierController.onPageLoad(ua.lrn, NormalMode))
    case AddCarrierPage                        => ua => Some(addCarrierRoute(ua, NormalMode))
    case AddCarrierEoriPage                    => ua => Some(addCarrierEoriRoute(ua, NormalMode))
    case CarrierEoriPage                       => ua => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.lrn))
    case CarrierNamePage                       => ua => Some(routes.CarrierAddressController.onPageLoad(ua.lrn, NormalMode))
    case CarrierAddressPage                    => ua => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.lrn))
  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddSafetyAndSecurityConsignorPage     => ua => addSafetyAndSecurityConsignorCheckRoute(ua)
    case AddSafetyAndSecurityConsignorEoriPage => ua => Some(addSafetyAndSecurityConsignorEoriRoute(ua, CheckMode))
    case SafetyAndSecurityConsignorEoriPage    => ua => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.lrn))
    case SafetyAndSecurityConsignorNamePage    => ua => Some(safetyAndSecurityConsignorNameRoute(ua))
    case SafetyAndSecurityConsignorAddressPage => ua => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.lrn))
    case AddSafetyAndSecurityConsigneePage     => ua => addSafetyAndSecurityConsigneeRouteCheckRoute(ua)
    case AddSafetyAndSecurityConsigneeEoriPage => ua => Some(addSafetyAndSecurityConsigneeEoriRoute(ua, CheckMode))
    case SafetyAndSecurityConsigneeEoriPage    => ua => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.lrn))
    case SafetyAndSecurityConsigneeNamePage    => ua => Some(safetyAndSecurityConsigneeNameRoute(ua))
    case SafetyAndSecurityConsigneeAddressPage => ua => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.lrn))
    case AddCarrierPage                        => ua => Some(addCarrierRoute(ua, CheckMode))
    case AddCarrierEoriPage                    => ua => Some(addCarrierEoriRoute(ua, CheckMode))
    case CarrierNamePage                       => ua => Some(carrierNameRoute(ua))
    case CarrierEoriPage                       => ua => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.lrn))
    case CarrierAddressPage                    => ua => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.lrn))
  }

  private def safetyAndSecurityConsignorNameRoute(ua: UserAnswers) =
    ua.get(SafetyAndSecurityConsignorAddressPage) match {
      case Some(_) => routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.lrn)
      case None    => routes.SafetyAndSecurityConsignorAddressController.onPageLoad(ua.lrn, CheckMode)
    }

  private def safetyAndSecurityConsigneeNameRoute(ua: UserAnswers) =
    ua.get(SafetyAndSecurityConsigneeAddressPage) match {
      case Some(_) => routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.lrn)
      case None    => routes.SafetyAndSecurityConsigneeAddressController.onPageLoad(ua.lrn, CheckMode)
    }

  private def addSafetyAndSecurityConsignorNormalRoute(ua: UserAnswers): Option[Call] =
    ua.get(AddSafetyAndSecurityConsignorPage) match {
      case Some(true)  => consignorCircumstanceIndicatorCheck(ua, NormalMode)
      case Some(false) => Some(routes.AddSafetyAndSecurityConsigneeController.onPageLoad(ua.lrn, NormalMode))
      case _           => None
    }

  private def addSafetyAndSecurityConsignorCheckRoute(ua: UserAnswers): Option[Call] =
    (ua.get(AddSafetyAndSecurityConsignorPage), ua.get(AddSafetyAndSecurityConsignorEoriPage)) match {
      case (Some(true), None) => Some(routes.AddSafetyAndSecurityConsignorEoriController.onPageLoad(ua.lrn, CheckMode))
      case (Some(_), _)       => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.lrn))
      case _ =>
        logger.warn(s"[Navigation][SafetyAndSecurityTraderDetails] AddSafetyAndSecurityConsignorPage is a missing mandatory page")
        Some(controllers.routes.SessionExpiredController.onPageLoad())
    }

  private def addSafetyAndSecurityConsignorEoriRoute(ua: UserAnswers, mode: Mode): Call =
    (ua.get(AddSafetyAndSecurityConsignorEoriPage), mode) match {
      case (Some(true), NormalMode)  => routes.SafetyAndSecurityConsignorEoriController.onPageLoad(ua.lrn, NormalMode)
      case (Some(false), NormalMode) => routes.SafetyAndSecurityConsignorNameController.onPageLoad(ua.lrn, NormalMode)
      case (Some(true), CheckMode) if ua.get(SafetyAndSecurityConsignorEoriPage).isDefined =>
        routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.lrn)
      case (Some(true), CheckMode) => routes.SafetyAndSecurityConsignorEoriController.onPageLoad(ua.lrn, CheckMode)
      case (Some(false), CheckMode) if ua.get(SafetyAndSecurityConsignorNamePage).isDefined =>
        routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.lrn)
      case (Some(false), CheckMode) => routes.SafetyAndSecurityConsignorNameController.onPageLoad(ua.lrn, CheckMode)
      case _                        => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def addSafetyAndSecurityConsigneeRouteNormalRoute(ua: UserAnswers): Option[Call] =
    ua.get(AddSafetyAndSecurityConsigneePage).flatMap {
      case true  => circumstanceIndicatorCheck(ua, NormalMode)
      case false => Some(routes.AddCarrierController.onPageLoad(ua.lrn, NormalMode))
    }

  private def addSafetyAndSecurityConsigneeRouteCheckRoute(ua: UserAnswers): Option[Call] =
    (ua.get(AddSafetyAndSecurityConsigneePage), ua.get(AddSafetyAndSecurityConsigneeEoriPage)) match {
      case (Some(true), None) => circumstanceIndicatorCheck(ua, CheckMode)
      case (Some(_), _)       => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.lrn))
      case _ =>
        logger.warn(s"[Navigation][SafetyAndSecurityTraderDetails] AddSafetyAndSecurityConsigneePage is a missing mandatory page")
        Some(controllers.routes.SessionExpiredController.onPageLoad())
    }

  private def circumstanceIndicatorCheck(ua: UserAnswers, mode: Mode): Option[Call] =
    ua.get(OfficeOfDeparturePage).map {
      case CustomsOffice(_, _, CountryCode("XI"), _) => routes.AddSafetyAndSecurityConsigneeEoriController.onPageLoad(ua.lrn, mode)
      case _ =>
        ua.get(CircumstanceIndicatorPage) match {
          case Some("E") => routes.SafetyAndSecurityConsigneeEoriController.onPageLoad(ua.lrn, mode)
          case _         => routes.AddSafetyAndSecurityConsigneeEoriController.onPageLoad(ua.lrn, mode)
        }
    }

  private def consignorCircumstanceIndicatorCheck(ua: UserAnswers, mode: Mode): Option[Call] =
    ua.get(OfficeOfDeparturePage).map {
      case CustomsOffice(_, _, CountryCode("XI"), _) =>
        ua.get(CircumstanceIndicatorPage) match {
          case Some("E") => routes.SafetyAndSecurityConsignorEoriController.onPageLoad(ua.lrn, mode)
          case _         => routes.AddSafetyAndSecurityConsignorEoriController.onPageLoad(ua.lrn, mode)
        }
      case _ => routes.AddSafetyAndSecurityConsignorEoriController.onPageLoad(ua.lrn, mode)
    }

  private def addSafetyAndSecurityConsigneeEoriRoute(ua: UserAnswers, mode: Mode): Call =
    (ua.get(AddSafetyAndSecurityConsigneeEoriPage), mode) match {
      case (Some(true), NormalMode)  => routes.SafetyAndSecurityConsigneeEoriController.onPageLoad(ua.lrn, NormalMode)
      case (Some(false), NormalMode) => routes.SafetyAndSecurityConsigneeNameController.onPageLoad(ua.lrn, NormalMode)
      case (Some(true), CheckMode) if ua.get(SafetyAndSecurityConsigneeEoriPage).isDefined =>
        routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.lrn)
      case (Some(true), CheckMode) => routes.SafetyAndSecurityConsigneeEoriController.onPageLoad(ua.lrn, CheckMode)
      case (Some(false), CheckMode) if ua.get(SafetyAndSecurityConsigneeNamePage).isDefined =>
        routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.lrn)
      case (Some(false), CheckMode) => routes.SafetyAndSecurityConsigneeNameController.onPageLoad(ua.lrn, CheckMode)
      case _                        => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def addCarrierRoute(ua: UserAnswers, mode: Mode): Call =
    (ua.get(AddCarrierPage), mode) match {
      case (Some(true), NormalMode)                                        => routes.AddCarrierEoriController.onPageLoad(ua.lrn, NormalMode)
      case (Some(false), NormalMode)                                       => routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.lrn)
      case (Some(true), CheckMode) if ua.get(AddCarrierEoriPage).isDefined => routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.lrn)
      case (Some(true), CheckMode)                                         => routes.AddCarrierEoriController.onPageLoad(ua.lrn, CheckMode)
      case (Some(false), CheckMode)                                        => routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.lrn)
      case _                                                               => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def addCarrierEoriRoute(ua: UserAnswers, mode: Mode): Call =
    (ua.get(AddCarrierEoriPage), mode) match {
      case (Some(true), NormalMode)                                      => routes.CarrierEoriController.onPageLoad(ua.lrn, NormalMode)
      case (Some(false), NormalMode)                                     => routes.CarrierNameController.onPageLoad(ua.lrn, NormalMode)
      case (Some(true), CheckMode) if ua.get(CarrierEoriPage).isDefined  => routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.lrn)
      case (Some(true), CheckMode)                                       => routes.CarrierEoriController.onPageLoad(ua.lrn, CheckMode)
      case (Some(false), CheckMode) if ua.get(CarrierNamePage).isDefined => routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.lrn)
      case (Some(false), CheckMode)                                      => routes.CarrierNameController.onPageLoad(ua.lrn, CheckMode)
      case _                                                             => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def carrierNameRoute(ua: UserAnswers): Call =
    ua.get(CarrierAddressPage) match {
      case Some(_) => routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.lrn)
      case None    => routes.CarrierAddressController.onPageLoad(ua.lrn, CheckMode)
    }

}
