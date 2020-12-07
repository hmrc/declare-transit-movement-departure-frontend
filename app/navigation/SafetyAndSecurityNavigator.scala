/*
 * Copyright 2020 HM Revenue & Customs
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
import derivable.DeriveNumberOfCountryOfRouting
import javax.inject.{Inject, Singleton}
import models._
import pages.safetyAndSecurity.{PlaceOfUnloadingCodePage, _}
import pages.{ModeAtBorderPage, Page}
import play.api.mvc.Call

@Singleton
class SafetyAndSecurityNavigator @Inject()() extends Navigator {

  // format: off

  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddCircumstanceIndicatorPage             => ua => addCircumstanceIndicator(ua, NormalMode)
    case CircumstanceIndicatorPage                => ua => Some(routes.AddTransportChargesPaymentMethodController.onPageLoad(ua.id, NormalMode))
    case AddTransportChargesPaymentMethodPage     => ua => addTransportChargesPaymentMethod(ua, NormalMode)
    case TransportChargesPaymentMethodPage        => ua => Some(routes.AddCommercialReferenceNumberController.onPageLoad(ua.id, NormalMode))
    case AddCommercialReferenceNumberPage         => ua => addCommercialReferenceNumber(ua, NormalMode)
    case AddCommercialReferenceNumberAllItemsPage => ua => addCommercialReferenceNumberAllItems(ua, NormalMode)
    case CommercialReferenceNumberAllItemsPage    => ua => commercialReferenceNumberAllItems(ua)
    case AddConveyanceReferenceNumberPage         => ua => addConveyancerReferenceNumber(ua, NormalMode)
    case ConveyanceReferenceNumberPage            => ua => conveyanceReferenceNumber(ua)
    case AddPlaceOfUnloadingCodePage              => ua => addPlaceOfUnloadingCodePage(ua, NormalMode)
    case PlaceOfUnloadingCodePage                 => ua => placeOfUnloadingCode(ua)
    case CountryOfRoutingPage(_)                  => ua => Some(routes.AddAnotherCountryOfRoutingController.onPageLoad(ua.id, NormalMode))
    case AddAnotherCountryOfRoutingPage           => ua => addAnotherCountryOfRouting(ua, NormalMode)
  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddCircumstanceIndicatorPage             => ua => addCircumstanceIndicator(ua, CheckMode)
    case CircumstanceIndicatorPage                => ua => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
    case AddTransportChargesPaymentMethodPage     => ua => addTransportChargesPaymentMethod(ua, CheckMode)
    case TransportChargesPaymentMethodPage        => ua => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
    case AddCommercialReferenceNumberPage         => ua => addCommercialReferenceNumber(ua, CheckMode)
    case AddCommercialReferenceNumberAllItemsPage => ua => addCommercialReferenceNumberAllItems(ua, CheckMode)
    case CommercialReferenceNumberAllItemsPage    => ua => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
    case AddConveyanceReferenceNumberPage         => ua => addConveyancerReferenceNumber(ua, CheckMode)
    case ConveyanceReferenceNumberPage            => ua => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
    case AddPlaceOfUnloadingCodePage              => ua => addPlaceOfUnloadingCodePage(ua, CheckMode)
    case PlaceOfUnloadingCodePage                 => ua => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
    case CountryOfRoutingPage(_)                  => ua => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
    case AddAnotherCountryOfRoutingPage           => ua => addAnotherCountryOfRouting(ua, CheckMode)
  }

  private def addCircumstanceIndicator(ua: UserAnswers, mode: Mode): Option[Call] =
    (ua.get(AddCircumstanceIndicatorPage), mode) match {
      case (Some(true), CheckMode) if ua.get(CircumstanceIndicatorPage).isDefined => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
      case (Some(true), _)   => Some(routes.CircumstanceIndicatorController.onPageLoad(ua.id, mode))
      case (Some(false), NormalMode) => Some(routes.AddTransportChargesPaymentMethodController.onPageLoad(ua.id, NormalMode))
      case (Some(false), CheckMode)  => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
    }

  private def addTransportChargesPaymentMethod(ua: UserAnswers, mode: Mode): Option[Call] =
    (ua.get(AddTransportChargesPaymentMethodPage), mode) match {
      case (Some(true), CheckMode) if ua.get(TransportChargesPaymentMethodPage).isDefined => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
      case (Some(true),_)   => Some(routes.TransportChargesPaymentMethodController.onPageLoad(ua.id, mode))
      case (Some(false), NormalMode) => Some(routes.AddCommercialReferenceNumberController.onPageLoad(ua.id, NormalMode))
      case (Some(false), CheckMode)  => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
    }

  private def addCommercialReferenceNumber(ua: UserAnswers, mode: Mode): Option[Call] =
    (ua.get(AddCommercialReferenceNumberPage), ua.get(ModeAtBorderPage), mode) match {
      case (Some(true), _, CheckMode) if ua.get(AddCommercialReferenceNumberAllItemsPage).isDefined => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
      case (Some(true), _, _)                        => Some(routes.AddCommercialReferenceNumberAllItemsController.onPageLoad(ua.id, mode))
      case (Some(false), _, CheckMode)  => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
      case (Some(false), Some("4") | Some("40"), NormalMode)  => Some(routes.ConveyanceReferenceNumberController.onPageLoad(ua.id, NormalMode))
      case (Some(false), _, NormalMode)                       => Some(routes.AddConveyanceReferenceNumberController.onPageLoad(ua.id, NormalMode))
      case _                                      => None
    }

  private def addCommercialReferenceNumberAllItems(ua: UserAnswers, mode: Mode): Option[Call] =
    (ua.get(AddCommercialReferenceNumberAllItemsPage), ua.get(ModeAtBorderPage), mode) match {
      case (Some(true), _, CheckMode) if ua.get(CommercialReferenceNumberAllItemsPage).isDefined => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
      case (Some(true), _, _)                        => Some(routes.CommercialReferenceNumberAllItemsController.onPageLoad(ua.id, mode))
      case (Some(false), _, CheckMode) => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
      case (Some(false), Some("4") | Some("40"), NormalMode)  => Some(routes.ConveyanceReferenceNumberController.onPageLoad(ua.id, NormalMode))
      case (Some(false), _, NormalMode)                       => Some(routes.AddConveyanceReferenceNumberController.onPageLoad(ua.id, NormalMode))
      case _                                      => None
    }

  def commercialReferenceNumberAllItems(ua: UserAnswers): Option[Call] =
    (ua.get(CommercialReferenceNumberAllItemsPage), ua.get(ModeAtBorderPage)) match {
      case (Some(_), Some("4") | Some("40")) => Some(routes.ConveyanceReferenceNumberController.onPageLoad(ua.id, NormalMode))
      case (Some(_), _)                      => Some(routes.AddConveyanceReferenceNumberController.onPageLoad(ua.id, NormalMode))
      case _                                 => None
    }

  private def addConveyancerReferenceNumber(ua: UserAnswers, mode: Mode): Option[Call] =
    (ua.get(AddConveyanceReferenceNumberPage), ua.get(CircumstanceIndicatorPage), mode) match {
      case (Some(true), _, CheckMode) if ua.get(ConveyanceReferenceNumberPage).isDefined => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
      case (Some(true), _, _)            => Some(routes.ConveyanceReferenceNumberController.onPageLoad(ua.id, mode))
      case (Some(false), _, CheckMode) => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
      case (Some(false) , Some("E"), NormalMode)  => Some(routes.AddPlaceOfUnloadingCodeController.onPageLoad(ua.id, NormalMode))
      case (Some(false) , _, NormalMode)          => Some(routes.PlaceOfUnloadingCodeController.onPageLoad(ua.id, NormalMode ))
      case _                          => None
    }

  private def conveyanceReferenceNumber(ua: UserAnswers): Option[Call] =
    (ua.get(CircumstanceIndicatorPage), ua.get(ConveyanceReferenceNumberPage)) match {
      case (Some("E"), Some(_)) => Some(routes.AddPlaceOfUnloadingCodeController.onPageLoad(ua.id, NormalMode))
      case (_,         Some(_)) => Some(routes.PlaceOfUnloadingCodeController.onPageLoad(ua.id, NormalMode))
      case _                    => None
    }

  private def addPlaceOfUnloadingCodePage(ua: UserAnswers, mode: Mode): Option[Call] = {
    val totalNumberOfCountriesOfRouting = ua.get(DeriveNumberOfCountryOfRouting).getOrElse(0)

    (ua.get(AddPlaceOfUnloadingCodePage), mode) match {
      case (Some(true), CheckMode) if ua.get(PlaceOfUnloadingCodePage).isDefined => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
      case (Some(true), _) => Some(routes.PlaceOfUnloadingCodeController.onPageLoad(ua.id, mode))
      case (Some(false), CheckMode) => Some(routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id))
      case (Some(false), NormalMode) =>
        if (totalNumberOfCountriesOfRouting == 0) {
          Some(routes.CountryOfRoutingController.onPageLoad(ua.id, Index(0), NormalMode))
        } else {
          Some(routes.AddAnotherCountryOfRoutingController.onPageLoad(ua.id, NormalMode))
        }
    }
  }

  private def addAnotherCountryOfRouting(ua: UserAnswers, mode: Mode): Option[Call] = {
    val totalNumberOfCountriesOfRouting = ua.get(DeriveNumberOfCountryOfRouting)
    
    ua.get(AddAnotherCountryOfRoutingPage).map {
      case true   => routes.CountryOfRoutingController.onPageLoad(ua.id, Index(totalNumberOfCountriesOfRouting.getOrElse(0)), mode)
      case false if mode == CheckMode => routes.SafetyAndSecurityCheckYourAnswersController.onPageLoad(ua.id)
      case false  => routes.AddSafetyAndSecurityConsignorController.onPageLoad(ua.id, NormalMode)
    }
  }

  private def placeOfUnloadingCode(ua: UserAnswers): Option[Call] = {
    val totalNumberOfCountriesOfRouting = ua.get(DeriveNumberOfCountryOfRouting).getOrElse(0)

    ua.get(PlaceOfUnloadingCodePage).map {
      _ =>
        if (totalNumberOfCountriesOfRouting == 0) {
          routes.CountryOfRoutingController.onPageLoad(ua.id, Index(0), NormalMode)
        } else {
          routes.AddAnotherCountryOfRoutingController.onPageLoad(ua.id, NormalMode)
        }
    }
  }

  // format: on
}
