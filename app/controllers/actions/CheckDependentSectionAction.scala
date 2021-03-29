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

package controllers.actions

import controllers.routes
import models.DependentSection
import models.journeyDomain.UserAnswersReader
import models.requests.DataRequest
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionFilter, Result}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CheckDependentSectionCompletionAction(val dependentSection: DependentSection, implicit val executionContext: ExecutionContext)
    extends ActionFilter[DataRequest] {
  override protected def filter[A](request: DataRequest[A]): Future[Option[Result]] = {
    val reader: UserAnswersReader[_] = DependentSection.dependentSectionReader(dependentSection, request.userAnswers)
    if (reader.run(request.userAnswers).isDefined) {
      Future.successful(None)
    } else {
      Future.successful(Some(Redirect(routes.DeclarationSummaryController.onPageLoad(request.userAnswers.id))))
    }
  }
}

trait CheckDependentSectionAction {
  def apply(dependentSection: DependentSection): ActionFilter[DataRequest]
}

class CheckDependentSectionActionImpl @Inject()(ec: ExecutionContext) extends CheckDependentSectionAction {
  override def apply(dependentSection: DependentSection): ActionFilter[DataRequest] = new CheckDependentSectionCompletionAction(dependentSection, ec)
}