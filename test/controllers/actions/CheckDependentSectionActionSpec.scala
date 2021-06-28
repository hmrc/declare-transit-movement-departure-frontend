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

import base.SpecBase
import generators.Generators
import models.journeyDomain.{MovementDetails, MovementDetailsSpec, UserAnswersReader}
import generators.{Generators, JourneyModelGenerators}
import models.journeyDomain.{MovementDetails, MovementDetailsSpec, PreTaskListDetails, UserAnswersReader}
import models.requests.DataRequest
import models.{DependentSection, EoriNumber, ProcedureType, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.mvc.{AnyContent, Request, Result, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewModels.TaskListViewModel

import scala.concurrent.Future

class CheckDependentSectionActionSpec extends SpecBase with GuiceOneAppPerSuite with Generators {

  def harness(reader: DependentSection, userAnswers: UserAnswers, f: DataRequest[AnyContent] => Unit): Future[Result] = {

    lazy val actionProvider = app.injector.instanceOf[CheckDependentSectionActionImpl]

    actionProvider(reader)
      .invokeBlock(
        DataRequest(FakeRequest(GET, "/").asInstanceOf[Request[AnyContent]], EoriNumber(""), userAnswers),
        {
          request: DataRequest[AnyContent] =>
            f(request)
            Future.successful(Results.Ok)
        }
      )
  }

  "CheckDependentSectionAction" - {

    "return unit if dependent section is complete" in {

      val procedureType      = arbitrary[ProcedureType].sample.value
      val movementDetails    = arbitraryMovementDetails(procedureType).arbitrary.sample.value
      val preTaskListDetails = arbitrary[PreTaskListDetails].sample.value

      val userAnswers = MovementDetailsSpec.setMovementDetails(movementDetails, preTaskListDetails)(emptyUserAnswers)

      val result: Future[Result] = harness(DependentSection.TransportDetails, userAnswers, request => request.userAnswers)
      status(result) mustBe OK
      redirectLocation(result) mustBe None

    }

    "return to task list page if dependent section is incomplete" in {

      val result = harness(DependentSection.TransportDetails, emptyUserAnswers, request => request.userAnswers)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.DeclarationSummaryController.onPageLoad(emptyUserAnswers.id).url)
    }

  }

}
