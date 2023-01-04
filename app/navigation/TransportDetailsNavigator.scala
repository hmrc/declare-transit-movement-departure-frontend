/*
 * Copyright 2023 HM Revenue & Customs
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

import controllers.transportDetails.routes
import models._
import models.journeyDomain.TransportDetails.InlandMode._
import models.journeyDomain.TransportDetails.ModeCrossingBorder
import pages._
import pages.generalInformation.ContainersUsedPage
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class TransportDetailsNavigator @Inject() () extends Navigator {

  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddIdAtDepartureLaterPage =>
      ua => Some(routes.ChangeAtBorderController.onPageLoad(ua.lrn, NormalMode))
    case InlandModePage =>
      ua => Some(inlandModeRoute(ua, NormalMode))
    case IdAtDeparturePage =>
      ua => Some(idAtDepartureRoute(ua))
    case NationalityAtDeparturePage =>
      ua => Some(routes.ChangeAtBorderController.onPageLoad(ua.lrn, NormalMode))
    case ModeAtBorderPage =>
      ua => Some(routes.ModeCrossingBorderController.onPageLoad(ua.lrn, NormalMode))
    case IdCrossingBorderPage =>
      ua => Some(routes.NationalityCrossingBorderController.onPageLoad(ua.lrn, NormalMode))
    case ModeCrossingBorderPage =>
      ua => Some(modeCrossingBorderRoute(ua, NormalMode))
    case NationalityCrossingBorderPage =>
      ua => Some(routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.lrn))
    case AddIdAtDeparturePage =>
      ua => Some(addIdAtDepartureRoute(ua, NormalMode))
    case ChangeAtBorderPage =>
      ua => Some(changeAtBorderRoute(ua, NormalMode))
    case AddNationalityAtDeparturePage =>
      ua => Some(addNationalityAtDepartureRoute(ua))
    case _ =>
      _ => None
  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case InlandModePage =>
      ua => Some(inlandModeRoute(ua, CheckMode))
    case AddIdAtDeparturePage =>
      ua => Some(addIdAtDepartureRoute(ua, CheckMode))
    case IdAtDeparturePage =>
      ua => Some(idAtDepartureCheckModeRoute(ua))
    case AddIdAtDepartureLaterPage =>
      ua => Some(routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.lrn))
    case NationalityAtDeparturePage =>
      ua => Some(routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.lrn))
    case ChangeAtBorderPage =>
      ua => Some(changeAtBorderRoute(ua, CheckMode))
    case ModeAtBorderPage =>
      ua => Some(modeAtBorderRouteCheckMode(ua))
    case IdCrossingBorderPage =>
      ua => Some(idCrossingBorderRouteCheckMode(ua))
    case ModeCrossingBorderPage =>
      ua => Some(modeCrossingBorderRoute(ua, CheckMode))
    case AddNationalityAtDeparturePage =>
      ua => Some(addNationalityAtDepartureCheckModeRoute(ua))
    case NationalityCrossingBorderPage =>
      ua => Some(routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.lrn))

  }

  private def addIdAtDepartureRoute(ua: UserAnswers, mode: Mode): Call =
    ua.get(AddIdAtDeparturePage) match {
      case Some(true)  => routes.IdAtDepartureController.onPageLoad(ua.lrn, mode)
      case Some(false) => routes.AddIdAtDepartureLaterController.onPageLoad(ua.lrn)
      case _           => routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.lrn)
    }

  private def addNationalityAtDepartureRoute(ua: UserAnswers): Call =
    ua.get(AddNationalityAtDeparturePage) match {
      case Some(true)  => routes.NationalityAtDepartureController.onPageLoad(ua.lrn, NormalMode)
      case Some(false) => routes.ChangeAtBorderController.onPageLoad(ua.lrn, NormalMode)
      case _           => routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.lrn)
    }

  private def addNationalityAtDepartureCheckModeRoute(ua: UserAnswers): Call =
    ua.get(AddNationalityAtDeparturePage) match {
      case Some(true) => routes.NationalityAtDepartureController.onPageLoad(ua.lrn, CheckMode)
      case _          => routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.lrn)
    }

  private def changeAtBorderRoute(ua: UserAnswers, mode: Mode): Call =
    (ua.get(ChangeAtBorderPage), ua.get(ModeAtBorderPage), mode) match {
      case (Some(true), _, NormalMode)   => routes.ModeAtBorderController.onPageLoad(ua.lrn, NormalMode)
      case (Some(true), None, CheckMode) => routes.ModeAtBorderController.onPageLoad(ua.lrn, CheckMode)
      case _                             => routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.lrn)
    }

  private def modeAtBorderRouteCheckMode(ua: UserAnswers): Call =
    ua.get(ModeCrossingBorderPage) match {
      case None => routes.ModeCrossingBorderController.onPageLoad(ua.lrn, CheckMode)
      case _    => routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.lrn)
    }

  private def idCrossingBorderRouteCheckMode(ua: UserAnswers): Call =
    ua.get(NationalityCrossingBorderPage) match {
      case None => routes.NationalityCrossingBorderController.onPageLoad(ua.lrn, CheckMode)
      case _    => routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.lrn)
    }

  private def modeCrossingBorderRoute(ua: UserAnswers, mode: Mode): Call =
    (ua.get(ModeCrossingBorderPage), mode) match {
      case (Some(inlandMode), _) if ModeCrossingBorder.isExemptFromNationality(inlandMode) =>
        routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.lrn)
      case _ => routes.IdCrossingBorderController.onPageLoad(ua.lrn, mode)
    }

  private def idAtDepartureRoute(ua: UserAnswers): Call =
    (ua.get(InlandModePage), ua.get(ContainersUsedPage)) match {
      case (Some(inlandMode), Some(true)) if ModeCrossingBorder.isExemptFromNationality(inlandMode) =>
        routes.ChangeAtBorderController.onPageLoad(ua.lrn, NormalMode)
      case (Some(_), Some(true))  => routes.AddNationalityAtDepartureController.onPageLoad(ua.lrn, NormalMode)
      case (Some(_), Some(false)) => routes.NationalityAtDepartureController.onPageLoad(ua.lrn, NormalMode)
      case _                      => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def idAtDepartureCheckModeRoute(ua: UserAnswers): Call =
    (ua.get(InlandModePage), ua.get(ContainersUsedPage)) match {
      case (Some(inlandMode), _) if ModeCrossingBorder.isExemptFromNationality(inlandMode) =>
        routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.lrn)
      case (Some(_), Some(_)) if ua.get(NationalityAtDeparturePage).nonEmpty => routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.lrn)
      case _                                                                 => routes.AddNationalityAtDepartureController.onPageLoad(ua.lrn, CheckMode)
    }

  private def inlandModeRoute(ua: UserAnswers, mode: Mode): Call =
    (ua.get(InlandModePage), ua.get(ContainersUsedPage)) match {
      case (Some(x), _) if Mode5or7.Constants.codes.map(_.toString).contains(x) => routes.ChangeAtBorderController.onPageLoad(ua.lrn, mode)
      case (_, Some(true))                                                      => routes.AddIdAtDepartureController.onPageLoad(ua.lrn, mode)
      case (_, Some(false) | None)                                              => routes.IdAtDepartureController.onPageLoad(ua.lrn, mode)
      case _                                                                    => routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.lrn)
    }
}
