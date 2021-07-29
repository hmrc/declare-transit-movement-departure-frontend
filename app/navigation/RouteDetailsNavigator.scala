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

import controllers.routeDetails.routes
import derivable.DeriveNumberOfOfficeOfTransits

import javax.inject.{Inject, Singleton}
import models._
import pages._
import pages.routeDetails._
import play.api.mvc.Call

@Singleton
class RouteDetailsNavigator @Inject() () extends Navigator {

  override val normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case CountryOfDispatchPage => ua => Some(routes.DestinationCountryController.onPageLoad(ua.lrn, NormalMode))
    case DestinationCountryPage =>
      ua => Some(routes.MovementDestinationCountryController.onPageLoad(ua.lrn, NormalMode))
    case MovementDestinationCountryPage =>
      ua => Some(routes.DestinationOfficeController.onPageLoad(ua.lrn, NormalMode))
    case DestinationOfficePage =>
      ua => Some(destinationOfficeRoute(ua, NormalMode))
    case AddOfficeOfTransitPage => ua => Some(addOfficeOfTransitRoute(ua, NormalMode))
    case OfficeOfTransitCountryPage(index) =>
      ua => Some(routes.AddAnotherTransitOfficeController.onPageLoad(ua.lrn, index, NormalMode))
    case AddAnotherTransitOfficePage(index) =>
      ua => Some(redirectToAddTransitOfficeNextPage(ua, index, NormalMode))
    case AddTransitOfficePage =>
      ua => Some(addOfficeOfTransit(NormalMode, ua))
    case ArrivalTimesAtOfficePage(_) =>
      ua => Some(routes.AddTransitOfficeController.onPageLoad(ua.lrn, NormalMode))
    case ConfirmRemoveOfficeOfTransitPage =>
      ua => Some(removeOfficeOfTransit(NormalMode)(ua))

  }

  override val checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case MovementDestinationCountryPage =>
      ua => Some(routes.DestinationOfficeController.onPageLoad(ua.lrn, CheckMode))
    case OfficeOfTransitCountryPage(index) =>
      ua => Some(routes.AddAnotherTransitOfficeController.onPageLoad(ua.lrn, index, CheckMode))
    case page if isRouteDetailsSectionPage(page) =>
      ua => Some(routes.RouteDetailsCheckYourAnswersController.onPageLoad(ua.lrn))
    case AddOfficeOfTransitPage => ua => Some(addOfficeOfTransitRoute(ua, CheckMode))
    case _ =>
      _ => None
  }

  def addOfficeOfTransitRoute(ua: UserAnswers, mode: Mode) =
    (ua.get(AddOfficeOfTransitPage), mode) match {
      case (Some(true), NormalMode) => routes.OfficeOfTransitCountryController.onPageLoad(ua.lrn, Index(0), NormalMode)
      case (Some(true), CheckMode)  => routes.OfficeOfTransitCountryController.onPageLoad(ua.lrn, Index(0), CheckMode)
      case (Some(false), _)         => routes.RouteDetailsCheckYourAnswersController.onPageLoad(ua.lrn)

    }

  def destinationOfficeRoute(ua: UserAnswers, mode: Mode) =
    ua.get(OfficeOfDeparturePage) match {
      case Some(x) if x.countryId.code.startsWith("XI") => routes.AddOfficeOfTransitController.onPageLoad(ua.lrn, mode)
      case _                                            => routes.OfficeOfTransitCountryController.onPageLoad(ua.lrn, Index(0), mode)

    }

  def redirectToAddTransitOfficeNextPage(ua: UserAnswers, index: Index, mode: Mode): Call =
    ua.get(AddSecurityDetailsPage) match {
      case Some(isSelected) if isSelected => routes.ArrivalTimesAtOfficeController.onPageLoad(ua.lrn, index, mode)
      case _                              => routes.AddTransitOfficeController.onPageLoad(ua.lrn, mode)
    }

  private def isRouteDetailsSectionPage(page: Page): Boolean =
    page match {
      case CountryOfDispatchPage | DestinationOfficePage | DestinationCountryPage | AddAnotherTransitOfficePage(_) | ArrivalTimesAtOfficePage(_) =>
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

  private def removeOfficeOfTransit(mode: Mode)(ua: UserAnswers) =
    ua.get(DeriveNumberOfOfficeOfTransits) match {
      case None | Some(0) => routes.OfficeOfTransitCountryController.onPageLoad(ua.lrn, Index(0), mode)
      case _              => routes.AddTransitOfficeController.onPageLoad(ua.lrn, mode)
    }
}
