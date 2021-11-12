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
import derivable.Derivable
import generators.Generators
import models.requests.DataRequest
import models.{DerivableSize, EoriNumber, Index, UserAnswers}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{JsArray, JsObject, JsPath, JsString, Json}
import play.api.mvc.{AnyContent, Request, Result, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class CheckValidIndexActionSpec extends SpecBase with GuiceOneAppPerSuite with Generators with UserAnswersSpecHelper {

  def harness(index: Index, derivableSize: DerivableSize, userAnswers: UserAnswers): Future[Result] = {

    lazy val actionProvider = app.injector.instanceOf[CheckValidIndexActionImpl]

    actionProvider(index, derivableSize)
      .invokeBlock(
        DataRequest(FakeRequest(GET, "/").asInstanceOf[Request[AnyContent]], EoriNumber(""), userAnswers),
        {
          _: DataRequest[AnyContent] =>
            Future.successful(Results.Ok)
        }
      )
  }

  "CheckValidIndexAction" - {

    case object DeriveNumberOfFoo extends DerivableSize {

      override def path: JsPath = JsPath \ "Foo"

    }

    val json = Json.obj(
      "Foo" -> Json.arr(
        Json.obj("Foo1" -> "value"),
        Json.obj("Foo2" -> "value"),
        Json.obj("Foo3" -> "value")
      )
    )

    val userAnswer = UserAnswers(lrn, eoriNumber, json)

    "must be None if first index in range" in {

      val result: Future[Result] = harness(Index(0), DeriveNumberOfFoo, userAnswer)
      status(result) mustBe OK
      redirectLocation(result) mustBe None
    }

    "must be None if index is within range" in {

      val result: Future[Result] = harness(Index(2), DeriveNumberOfFoo, userAnswer)
      status(result) mustBe OK
      redirectLocation(result) mustBe None
    }

    "must be None if index in out of range by 1" in {

      val result: Future[Result] = harness(Index(3), DeriveNumberOfFoo, userAnswer)
      status(result) mustBe OK
      redirectLocation(result) mustBe None
    }

    "must return internal server error if index is out of range" in {

      val result: Future[Result] = harness(Index(4), DeriveNumberOfFoo, userAnswer)
      status(result) mustBe INTERNAL_SERVER_ERROR
    }
  }

}
