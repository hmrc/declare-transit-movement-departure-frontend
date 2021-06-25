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

import controllers.movementDetails.routes
import javax.inject.{Inject, Singleton}
import models.ProcedureType._
import models._
import pages._
import pages.movementDetails.PreLodgeDeclarationPage
import play.api.mvc.Call

@Singleton
class MovementDetailsNavigator @Inject() () extends Navigator {

  // format: off
  override protected def normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case PreLodgeDeclarationPage       => ua => Some(routes.ContainersUsedPageController.onPageLoad(ua.id, NormalMode))
    case ContainersUsedPage            => ua => //TODO: revert logic when 2410 deployed successfully
      ua.get(DeclarationTypePage) match {
        case Some(_) => Some(routes.DeclarationPlaceController.onPageLoad(ua.id, NormalMode))
        case _ => Some(controllers.routes.DeclarationTypeController.onPageLoad(ua.id, NormalMode))
      }
    case DeclarationPlacePage          => ua => Some(routes.DeclarationForSomeoneElseController.onPageLoad(ua.id, NormalMode))
    case DeclarationForSomeoneElsePage => ua => Some(isDeclarationForSomeoneElse(ua, NormalMode))
    case RepresentativeNamePage        => ua => Some(routes.RepresentativeCapacityController.onPageLoad(ua.id, NormalMode))
    case RepresentativeCapacityPage    => ua => Some(routes.MovementDetailsCheckYourAnswersController.onPageLoad(ua.id))
  }

  override protected def checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case DeclarationForSomeoneElsePage => ua => Some(isDeclarationForSomeoneElse(ua, CheckMode))
    case _                             => ua => Some(routes.MovementDetailsCheckYourAnswersController.onPageLoad(ua.id))
  }

  private def isDeclarationForSomeoneElse(ua: UserAnswers, mode: Mode): Call =
    (ua.get(DeclarationForSomeoneElsePage), ua.get(RepresentativeNamePage), mode) match {
      case (Some(true), None, CheckMode) => routes.RepresentativeNameController.onPageLoad(ua.id, NormalMode)
      case (Some(true), _, NormalMode)   => routes.RepresentativeNameController.onPageLoad(ua.id, NormalMode)
      case _                             => routes.MovementDetailsCheckYourAnswersController.onPageLoad(ua.id)
    }
  // format: on
}
