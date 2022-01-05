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

import controllers.routes
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.Page
import play.api.mvc.Call

trait Navigator {
  type RouteMapping = PartialFunction[Page, UserAnswers => Option[Call]]

  protected def normalRoutes: RouteMapping

  protected def checkRoutes: RouteMapping

  protected def checkModeDefaultPage(userAnswers: UserAnswers): Call =
    routes.SessionExpiredController.onPageLoad()

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes.lift(page) match {
        case None       => routes.LocalReferenceNumberController.onPageLoad()
        case Some(call) => handleCall(userAnswers, call)
      }
    case CheckMode =>
      checkRoutes.lift(page) match {
        case None       => checkModeDefaultPage(userAnswers)
        case Some(call) => handleCall(userAnswers, call)
      }
  }

  private def handleCall(userAnswers: UserAnswers, call: UserAnswers => Option[Call]) =
    call(userAnswers) match {
      case Some(onwardRoute) => onwardRoute
      case None              => routes.SessionExpiredController.onPageLoad()
    }
}
