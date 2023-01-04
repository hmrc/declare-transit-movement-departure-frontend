/*
 * Copyright 2023 HM Revenue & Customs
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

package base

import models.domain.SealDomain
import models.journeyDomain.EitherType
import models.reference.CountryCode
import models.{DepartureId, EoriNumber, Index, LocalReferenceNumber, PrincipalAddress, UserAnswers}
import org.scalactic.source
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.exceptions.{StackDepthException, TestFailedException}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OptionValues, TryValues}
import org.scalatestplus.mockito.MockitoSugar
import play.api.i18n.Messages
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.http.HeaderCarrier

trait SpecBase
    extends AnyFreeSpec
    with Matchers
    with OptionValues
    with EitherValues
    with TryValues
    with ScalaFutures
    with IntegrationPatience
    with MockitoSugar {

  val nonEUCountries =
    Seq(CountryCode("AD"), CountryCode("IS"), CountryCode("LI"), CountryCode("NO"), CountryCode("SM"), CountryCode("SJ"), CountryCode("CH"))

  val userAnswersId             = "id"
  val eoriNumber: EoriNumber    = EoriNumber("EoriNumber")
  val lrn: LocalReferenceNumber = LocalReferenceNumber("ABCD1234567890123").get
  val sealIndex: Index          = Index(0)
  val sealDomain: SealDomain    = SealDomain("sealNumber")
  val sealDomain2: SealDomain   = SealDomain("sealNumber2")
  val sealDomain3: SealDomain   = SealDomain("sealNumber3")

  val index: Index          = Index(0)
  val referenceIndex: Index = Index(0)
  val documentIndex: Index  = Index(0)

  val itemIndex: Index      = Index(0)
  val packageIndex: Index   = Index(0)
  val containerIndex: Index = Index(0)

  def fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")

  val emptyUserAnswers: UserAnswers = UserAnswers(lrn, eoriNumber, Json.obj())

  val principalName: String = "principalName"

  val carrierName: String = "carrierName"

  val principalAddress: PrincipalAddress = PrincipalAddress("numberAndStreet", "town", "SW1A 1AA")

  val configKey = "config"

  val departureId: DepartureId = DepartureId(1)

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit def messages: Messages = Helpers.stubMessages()

  implicit class UserAnswerReaderResultOps[R](userAnswersReaderResult: EitherType[R]) {

    def isSuccessful(implicit pos: source.Position): R =
      userAnswersReaderResult match {
        case Right(value) => value
        case Left(value) =>
          throw new TestFailedException((_: StackDepthException) => Some(s"Expected reader to be successful, reader failed on $value"), None, pos)

      }

  }
}
