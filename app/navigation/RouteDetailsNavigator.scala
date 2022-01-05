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

import cats.implicits._
import controllers.routeDetails.routes
import derivable.DeriveNumberOfOfficeOfTransits
import models.DeclarationType.Option4
import models._
import models.reference.CountryCode
import pages._
import pages.routeDetails._
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class RouteDetailsNavigator @Inject() () extends Navigator {

  override val normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case CountryOfDispatchPage => ua => Some(routes.DestinationCountryController.onPageLoad(ua.lrn, NormalMode))
    case DestinationCountryPage =>
      ua => Some(routes.MovementDestinationCountryController.onPageLoad(ua.lrn, NormalMode))
    case MovementDestinationCountryPage =>
      ua => Some(routes.DestinationOfficeController.onPageLoad(ua.lrn, NormalMode))
    case DestinationOfficePage =>
      ua => destinationOfficeRoute(ua, NormalMode)
    case OfficeOfTransitCountryPage(index) =>
      ua => Some(routes.AddAnotherTransitOfficeController.onPageLoad(ua.lrn, index, NormalMode))
    case AddOfficeOfTransitPage => ua => addOfficeOfTransitRoute(ua, NormalMode)
    case AddAnotherTransitOfficePage(index) =>
      ua => Some(redirectToAddTransitOfficeNextPage(ua, index, NormalMode))
    case AddTransitOfficePage =>
      ua => Some(addOfficeOfTransit(NormalMode, ua))
    case ArrivalDatesAtOfficePage(_) =>
      ua => Some(routes.AddTransitOfficeController.onPageLoad(ua.lrn, NormalMode))
    case ConfirmRemoveOfficeOfTransitPage =>
      ua => Some(removeOfficeOfTransit(NormalMode)(ua))
  }

  override val checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case OfficeOfTransitCountryPage(index) =>
      ua => Some(routes.AddAnotherTransitOfficeController.onPageLoad(ua.lrn, index, CheckMode))
    case page if isRouteDetailsSectionPage(page) =>
      ua => Some(routes.RouteDetailsCheckYourAnswersController.onPageLoad(ua.lrn))
    case AddOfficeOfTransitPage => ua => addOfficeOfTransitRoute(ua, CheckMode)
    case _ =>
      _ => None
  }

  private def addOfficeOfTransitRoute(ua: UserAnswers, mode: Mode) =
    ua.get(AddOfficeOfTransitPage) map {
      case true  => routes.OfficeOfTransitCountryController.onPageLoad(ua.lrn, Index(0), mode)
      case false => routes.RouteDetailsCheckYourAnswersController.onPageLoad(ua.lrn)
    }

  private def destinationOfficeRoute(ua: UserAnswers, mode: Mode) =
    (ua.get(OfficeOfDeparturePage), ua.get(DeclarationTypePage)).tupled.map {
      case (_, Option4)                                                                => routes.RouteDetailsCheckYourAnswersController.onPageLoad(ua.lrn)
      case (officeOfDeparture, _) if officeOfDeparture.countryId.code.startsWith("XI") => routes.AddOfficeOfTransitController.onPageLoad(ua.lrn, mode)
      case _                                                                           => routes.OfficeOfTransitCountryController.onPageLoad(ua.lrn, Index(0), mode)
    }

  private def redirectToAddTransitOfficeNextPage(ua: UserAnswers, index: Index, mode: Mode): Call =
    ua.get(AddSecurityDetailsPage) match {
      case Some(isSelected) if isSelected => routes.ArrivalDatesAtOfficeController.onPageLoad(ua.lrn, index, mode)
      case _                              => routes.AddTransitOfficeController.onPageLoad(ua.lrn, mode)
    }

  private def isRouteDetailsSectionPage(page: Page): Boolean =
    page match {
      case CountryOfDispatchPage | DestinationOfficePage | DestinationCountryPage | AddAnotherTransitOfficePage(_) | ArrivalDatesAtOfficePage(_) =>
        true
      case _ => false
    }

  private def addOfficeOfTransit(mode: Mode, userAnswers: UserAnswers): Call = {
    val count                     = userAnswers.get(DeriveNumberOfOfficeOfTransits).getOrElse(0)
    val maxNumberOfOfficesAllowed = 9
    userAnswers.get(AddTransitOfficePage) match {
      case Some(true) if count <= maxNumberOfOfficesAllowed =>
        val index = Index(count)
        routes.OfficeOfTransitCountryController.onPageLoad(userAnswers.lrn, index, mode)
      case _ =>
        routes.RouteDetailsCheckYourAnswersController.onPageLoad(userAnswers.lrn)
    }
  }

  private def removeOfficeOfTransit(mode: Mode)(ua: UserAnswers): Call = {
    val XICountryCode         = ua.get(OfficeOfDeparturePage).map(_.countryId).contains(CountryCode("XI"))
    val nonTIRDeclarationType = !ua.get(DeclarationTypePage).contains(Option4)
    ua.get(DeriveNumberOfOfficeOfTransits) match {
      case None | Some(0) if XICountryCode && nonTIRDeclarationType =>
        routes.AddOfficeOfTransitController.onPageLoad(ua.lrn, mode)
      case None | Some(0) =>
        routes.OfficeOfTransitCountryController.onPageLoad(ua.lrn, Index(0), mode)
      case _ =>
        routes.AddTransitOfficeController.onPageLoad(ua.lrn, mode)
    }
  }
}
