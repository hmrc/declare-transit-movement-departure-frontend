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


import controllers.goodsSummary.routes
import derivable.DeriveNumberOfSeals
import javax.inject.{Inject, Singleton}
import models._
import pages._
import play.api.mvc.Call

@Singleton
class GoodsSummaryNavigator @Inject()() extends Navigator {

  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case AddSealsPage => ua => addSealsRoute(ua, NormalMode)
    case SealIdDetailsPage(_) => ua => Some(routes.SealsInformationController.onPageLoad(ua.id, NormalMode))
    case SealsInformationPage => ua => Some(sealsInformationRoute(ua, NormalMode))
    case ConfirmRemoveSealPage() => removeSeal(NormalMode)

  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = ???


  private def sealsInformationRoute(ua: UserAnswers, mode: Mode): Call = {
    ua.get(SealsInformationPage) match {
      case Some(true) =>
        val sealCount = ua.get(DeriveNumberOfSeals()).getOrElse(0)
        val sealIndex = Index(sealCount)
        sealCount match {
          case x if x < 10 => routes.SealIdDetailsController.onPageLoad(ua.id, sealIndex, mode)
          case _ => ???
        }
      case Some(false) => ???
//         routes.GoodsSummaryCheckYourAnswersController.onPageLoad(ua.id)//TODO not built yet
    }
  }

  private def removeSeal(mode: Mode)(ua: UserAnswers) = {
    ua.get(DeriveNumberOfSeals()).getOrElse(0) match {
      case  0 => Some(routes.AddSealsController.onPageLoad(ua.id, mode))
      case _ => Some(routes.SealsInformationController.onPageLoad(ua.id, mode))
    }
  }

  private def addSealsRoute(ua: UserAnswers, mode:Mode) = {
    ua.get(AddSealsPage) match {
      case Some(true) => Some(routes.SealIdDetailsController.onPageLoad(ua.id, Index(0), NormalMode))
      case _ => ???
    }
  }

}