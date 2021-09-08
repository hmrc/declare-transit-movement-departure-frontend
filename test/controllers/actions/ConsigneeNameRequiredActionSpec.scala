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
import commonTestUtils.UserAnswersSpecHelper
import generators.Generators
import models.requests.{ConsigneeNameRequest, DataRequest}
import models.{EoriNumber, Index, UserAnswers}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import pages.addItems.traderSecurityDetails.SecurityConsigneeNamePage
import play.api.mvc.{AnyContent, Request, Result, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class ConsigneeNameRequiredActionSpec extends SpecBase with GuiceOneAppPerSuite with Generators with UserAnswersSpecHelper {

  def harness(index: Index, userAnswers: UserAnswers, f: ConsigneeNameRequest[AnyContent] => Unit): Future[Result] = {

    lazy val actionProvider = app.injector.instanceOf[ConsigneeNameRequiredActionImpl]

    actionProvider(index)
      .invokeBlock(
        DataRequest(FakeRequest(GET, "/").asInstanceOf[Request[AnyContent]], EoriNumber(""), userAnswers),
        {
          request: ConsigneeNameRequest[AnyContent] =>
            f(request)
            Future.successful(Results.Ok)
        }
      )
  }

  "ConsigneeNameRequiredAction" - {

    "return unit if SecurityConsigneeNamePage is defined at index" in {

      val answers = emptyUserAnswers
        .unsafeSetVal(SecurityConsigneeNamePage(index))("Name")

      val result: Future[Result] = harness(index, answers, request => request.userAnswers)
      status(result) mustBe OK
      redirectLocation(result) mustBe None
    }

    "redirect to session expired if SecurityConsigneeNamePage undefined at index" in {

      val result = harness(index, emptyUserAnswers, request => request.userAnswers)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
