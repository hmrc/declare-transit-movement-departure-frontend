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
import models.DeclarationType.Option1
import models.ProcedureType.Normal
import models.RepresentativeCapacity.Direct
import models.journeyDomain.PreTaskListDetails
import models.reference.{CountryCode, CustomsOffice}
import models.requests.DataRequest
import models.{DependentSection, EoriNumber, ProcedureType, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import pages._
import pages.generalInformation.{
  ContainersUsedPage,
  DeclarationForSomeoneElsePage,
  DeclarationPlacePage,
  PreLodgeDeclarationPage,
  RepresentativeCapacityPage,
  RepresentativeNamePage
}
import play.api.mvc.{AnyContent, Request, Result, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class CheckDependentSectionActionSpec extends SpecBase with GuiceOneAppPerSuite with Generators with UserAnswersSpecHelper {

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

      val dependentSections = emptyUserAnswers
        // PreTaskList
        .unsafeSetVal(ProcedureTypePage)(Normal)
        .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("code"), None))
        .unsafeSetVal(AddSecurityDetailsPage)(false)
        .unsafeSetVal(DeclarationTypePage)(Option1)
        // MovementDetails
        .unsafeSetVal(ProcedureTypePage)(Normal)
        .unsafeSetVal(DeclarationTypePage)(Option1)
        .unsafeSetVal(PreLodgeDeclarationPage)(false)
        .unsafeSetVal(ContainersUsedPage)(false)
        .unsafeSetVal(DeclarationPlacePage)("declarationPlace")
        .unsafeSetVal(DeclarationForSomeoneElsePage)(true)
        .unsafeSetVal(RepresentativeNamePage)("repName")
        .unsafeSetVal(RepresentativeCapacityPage)(Direct)

      val result: Future[Result] = harness(DependentSection.TransportDetails, dependentSections, request => request.userAnswers)
      status(result) mustBe OK
      redirectLocation(result) mustBe None
    }

    "return to task list page if dependent section is incomplete" in {

      val result = harness(DependentSection.TransportDetails, emptyUserAnswers, request => request.userAnswers)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.DeclarationSummaryController.onPageLoad(emptyUserAnswers.lrn).url)
    }
  }
}
