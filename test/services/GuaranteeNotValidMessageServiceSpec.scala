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

package services

import base.{MockServiceApp, SpecBase}
import connectors.DepartureMovementConnector
import models.InvalidGuaranteeCode.G01
import models.{DepartureId, GuaranteeNotValidMessage, InvalidGuaranteeReasonCode, MessagesLocation, MessagesSummary}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.must.Matchers
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GuaranteeNotValidMessageServiceSpec extends SpecBase with MockServiceApp with BeforeAndAfterEach with Matchers {

  private val mockDepartureConnector: DepartureMovementConnector = mock[DepartureMovementConnector]
  private val departureId                                        = DepartureId(1)

  override def beforeEach: Unit = {
    super.beforeEach()
    reset(mockDepartureConnector)
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[DepartureMovementConnector].toInstance(mockDepartureConnector))

  private val messageService = app.injector.instanceOf[GuaranteeNotValidMessageService]

  "GuaranteeNotValidMessageService" - {
    "must return GuaranteeNotValidMessage for the input departureId" in {
      val notificationMessage = GuaranteeNotValidMessage(lrn.toString, Seq(InvalidGuaranteeReasonCode("ref", G01, None)))
      val messagesSummary =
        MessagesSummary(departureId, MessagesLocation(s"/movements/departures/${departureId.value}/messages/3", Some("/movements/departures/1234/messages/5")))

      when(mockDepartureConnector.getSummary(any())(any())).thenReturn(Future.successful(Some(messagesSummary)))
      when(mockDepartureConnector.getGuaranteeNotValidMessage(any())(any()))
        .thenReturn(Future.successful(Some(notificationMessage)))

      messageService.guaranteeNotValidMessage(departureId).futureValue mustBe Some(notificationMessage)
    }

    "must return None when getSummary fails to get guaranteeNotValid message" in {
      val messagesSummary =
        MessagesSummary(departureId, MessagesLocation(s"/movements/departures/${departureId.value}/messages/3", None))
      when(mockDepartureConnector.getSummary(any())(any())).thenReturn(Future.successful(Some(messagesSummary)))

      messageService.guaranteeNotValidMessage(departureId).futureValue mustBe None
    }

    "must return None when getSummary call fails to get MessagesSummary" in {
      when(mockDepartureConnector.getSummary(any())(any())).thenReturn(Future.successful(None))

      messageService.guaranteeNotValidMessage(departureId).futureValue mustBe None
    }
  }
}
