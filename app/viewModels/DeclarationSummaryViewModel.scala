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

package viewModels

import config.ManageTransitMovementsService
import models.journeyDomain.{EitherType, JourneyDomain, UserAnswersReader}
import models.{LocalReferenceNumber, UserAnswers}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.Call

class DeclarationSummaryViewModel(manageTransitMovementsService: ManageTransitMovementsService, userAnswers: UserAnswers) {
  import DeclarationSummaryViewModel.nextPage

  private val lrn: LocalReferenceNumber      = userAnswers.id
  private val sections: TaskListViewModel    = TaskListViewModel(userAnswers)
  private val backToTransitMovements: String = manageTransitMovementsService.service.fullServiceUrl

  private val journeyDomain: EitherType[JourneyDomain] = UserAnswersReader[JourneyDomain].run(userAnswers)

  private val isDeclarationComplete: Boolean = journeyDomain.isRight

  private val onSubmitUrl: Option[String] = if (isDeclarationComplete) {
    Some(nextPage(lrn).url)
  } else {
    None
  }

}

object DeclarationSummaryViewModel {

  def nextPage(localReferenceNumber: LocalReferenceNumber): Call = controllers.routes.DeclarationSummaryController.onSubmit(localReferenceNumber)

  def apply(manageTransitMovementsService: ManageTransitMovementsService, userAnswers: UserAnswers): DeclarationSummaryViewModel =
    new DeclarationSummaryViewModel(manageTransitMovementsService, userAnswers)

  def unapply(arg: DeclarationSummaryViewModel): Option[(LocalReferenceNumber, TaskListViewModel, String, Boolean, Option[String])] =
    Some((arg.lrn, arg.sections, arg.backToTransitMovements, arg.isDeclarationComplete, arg.onSubmitUrl))

  implicit val writes: OWrites[DeclarationSummaryViewModel] =
    ((__ \ "lrn").write[LocalReferenceNumber] and
      (__ \ "sections").write[TaskListViewModel] and
      (__ \ "backToTransitMovements").write[String] and
      (__ \ "isDeclarationComplete").write[Boolean] and
      (__ \ "onSubmitUrl").writeNullable[String])(unlift(unapply))
}
