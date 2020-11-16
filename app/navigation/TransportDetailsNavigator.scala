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

import controllers.transportDetails.routes
import javax.inject.{Inject, Singleton}
import models._
import pages._
import play.api.mvc.Call
import models.journeyDomain.TransportDetails.InlandMode._
import models.journeyDomain.TransportDetails.InlandMode

@Singleton
class TransportDetailsNavigator @Inject()() extends Navigator {

  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddIdAtDepartureLaterPage =>
      ua =>
        Some(addItAtDepartureLaterRoute(ua, NormalMode))
    case InlandModePage =>
      ua =>
        Some(inlandModeRoute(ua, NormalMode))
    case IdAtDeparturePage =>
      ua =>
        Some(routes.NationalityAtDepartureController.onPageLoad(ua.id, NormalMode))
    case NationalityAtDeparturePage =>
      ua =>
        Some(routes.ChangeAtBorderController.onPageLoad(ua.id, NormalMode))
    case ModeAtBorderPage =>
      ua =>
        Some(routes.IdCrossingBorderController.onPageLoad(ua.id, NormalMode))
    case IdCrossingBorderPage =>
      ua =>
        Some(routes.ModeCrossingBorderController.onPageLoad(ua.id, NormalMode))
    case ModeCrossingBorderPage =>
      ua =>
        Some(modeCrossingBorderRoute(ua, NormalMode))
    case NationalityCrossingBorderPage =>
      ua =>
        Some(routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.id))
    case AddIdAtDeparturePage =>
      ua =>
        Some(addIdAtDepartureRoute(ua, NormalMode))
    case ChangeAtBorderPage =>
      ua =>
        Some(changeAtBorderRoute(ua, NormalMode))
    case _ =>
      _ =>
        None
  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case InlandModePage =>
      ua =>
        Some(inlandModeRoute(ua, CheckMode))
    case AddIdAtDeparturePage =>
      ua =>
        Some(addIdAtDepartureRoute(ua, CheckMode))
    case IdAtDeparturePage =>
      ua =>
        Some(routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.id))
    case AddIdAtDepartureLaterPage =>
      ua =>
        Some(addItAtDepartureLaterRoute(ua, NormalMode))
    case NationalityAtDeparturePage =>
      ua =>
        Some(routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.id))
    case ChangeAtBorderPage =>
      ua =>
        Some(changeAtBorderRoute(ua, CheckMode))
    case ModeAtBorderPage =>
      ua =>
        Some(modeAtBorderRoute(ua, CheckMode))
    case IdCrossingBorderPage =>
      ua =>
        Some(idCrossingBorderRoute(ua, CheckMode))
    case ModeCrossingBorderPage =>
      ua =>
        Some(modeCrossingBorderRoute(ua, CheckMode))
    case NationalityCrossingBorderPage =>
      ua =>
        Some(routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.id))

  }

  private def addIdAtDepartureRoute(ua: UserAnswers, mode: Mode): Call =
    (ua.get(AddIdAtDeparturePage), ua.get(IdAtDeparturePage), mode) match {
      case (Some(true), None, _)  => routes.IdAtDepartureController.onPageLoad(ua.id, mode)
      case (Some(false), None, _) => routes.AddIdAtDepartureLaterController.onPageLoad(ua.id)
      case _                      => routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }

  private def changeAtBorderRoute(ua: UserAnswers, mode: Mode): Call =
    (ua.get(ChangeAtBorderPage), ua.get(ModeAtBorderPage), mode) match {
      case (Some(true), _, NormalMode)   => routes.ModeAtBorderController.onPageLoad(ua.id, NormalMode)
      case (Some(true), None, CheckMode) => routes.ModeAtBorderController.onPageLoad(ua.id, CheckMode)
      case _                             => routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }

  private def modeAtBorderRoute(ua: UserAnswers, mode: Mode): Call =
    ua.get(IdCrossingBorderPage) match {
      case None => routes.IdCrossingBorderController.onPageLoad(ua.id, CheckMode)
      case _    => routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }

  private def idCrossingBorderRoute(ua: UserAnswers, mode: Mode): Call =
    ua.get(ModeCrossingBorderPage) match {
      case None => routes.ModeCrossingBorderController.onPageLoad(ua.id, CheckMode)
      case _    => routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }

  private def modeCrossingBorderRoute(ua: UserAnswers, mode: Mode): Call =
    (ua.get(ModeCrossingBorderPage), ua.get(NationalityCrossingBorderPage), mode) match {
      case (Some(x), _, NormalMode) if !InlandMode.Constants.codesSingleDigit.contains(x) =>
        routes.NationalityCrossingBorderController.onPageLoad(ua.id, mode)
      case (Some(x), None, CheckMode) if !InlandMode.Constants.codesSingleDigit.contains(x) =>
        routes.NationalityCrossingBorderController.onPageLoad(ua.id, CheckMode)
      case _ => routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }

  private def addItAtDepartureLaterRoute(ua: UserAnswers, mode: Mode): Call =
    ua.get(NationalityAtDeparturePage) match {
      case None => routes.NationalityAtDepartureController.onPageLoad(ua.id, mode)
      case _    => routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }

  private def inlandModeRoute(ua: UserAnswers, mode: Mode): Call =
    (ua.get(InlandModePage), ua.get(AddIdAtDeparturePage), mode) match {
      case (Some(x), _, _) if Rail.Constants.codes.contains(x)     => routes.ChangeAtBorderController.onPageLoad(ua.id, mode)
      case (Some(x), _, _) if Mode5or7.Constants.codes.contains(x) => routes.NationalityAtDepartureController.onPageLoad(ua.id, mode)
      case (_, Some(false), CheckMode)                             => routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.id)
      case (_, Some(true), CheckMode)                              => routes.TransportDetailsCheckYourAnswersController.onPageLoad(ua.id)
      case (_, _, _)                                               => routes.AddIdAtDepartureController.onPageLoad(ua.id, mode)
    }
}
