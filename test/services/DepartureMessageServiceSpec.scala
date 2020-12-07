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

import java.time.LocalDate

import base.{MockServiceApp, SpecBase}
import connectors.DepartureMovementConnector
import models.InvalidGuaranteeCode.G01
import models.{DeclarationRejectionMessage, GuaranteeNotValidMessage, InvalidGuaranteeReasonCode, MessagesLocation, MessagesSummary}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.must.Matchers
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DepartureMessageServiceSpec extends SpecBase with MockServiceApp with BeforeAndAfterEach with Matchers {

  private val mockDepartureConnector: DepartureMovementConnector = mock[DepartureMovementConnector]

  override def beforeEach: Unit = {
    super.beforeEach()
    reset(mockDepartureConnector)
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[DepartureMovementConnector].toInstance(mockDepartureConnector))

  private val messageService = app.injector.instanceOf[DepartureMessageService]

  "DepartureMessageService" - {
    "getGuaranteeNotValidMessage" - {
      "must return GuaranteeNotValidMessage for the input departureId" in {
        val notificationMessage = GuaranteeNotValidMessage(lrn.toString, Seq(InvalidGuaranteeReasonCode("ref", G01, None)))
        val messagesSummary =
          MessagesSummary(
            departureId,
            MessagesLocation(
              s"/movements/departures/${departureId.value}/messages/3",
              Some("/movements/departures/1234/messages/5"),
              Some("/movements/departures/1234/messages/7"),
              Some("/movements/departures/1234/messages/9"),
              Some("/movements/departures/1234/messages/11")
            )
          )

        when(mockDepartureConnector.getSummary(any())(any())).thenReturn(Future.successful(Some(messagesSummary)))
        when(mockDepartureConnector.getGuaranteeNotValidMessage(any())(any()))
          .thenReturn(Future.successful(Some(notificationMessage)))

        messageService.guaranteeNotValidMessage(departureId).futureValue mustBe Some(notificationMessage)
      }

      "must return None when getSummary fails to get guaranteeNotValid message" in {
        val messagesSummary =
          MessagesSummary(departureId, MessagesLocation(s"/movements/departures/${departureId.value}/messages/3", None, None, None, None))
        when(mockDepartureConnector.getSummary(any())(any())).thenReturn(Future.successful(Some(messagesSummary)))

        messageService.guaranteeNotValidMessage(departureId).futureValue mustBe None
      }

      "must return None when getSummary call fails to get MessagesSummary" in {
        when(mockDepartureConnector.getSummary(any())(any())).thenReturn(Future.successful(None))

        messageService.guaranteeNotValidMessage(departureId).futureValue mustBe None
      }
    }

    "DeclarationRejectionMessage" - {
      "must return DeclarationRejectionMessage for the input departureId" in {
        val notificationMessage = DeclarationRejectionMessage("", LocalDate.parse("2010-10-10"), Some(""), Seq.empty)
        val messagesSummary =
          MessagesSummary(
            departureId,
            MessagesLocation(
              s"/movements/departures/${departureId.value}/messages/3",
              Some("/movements/departures/1234/messages/5"),
              Some("/movements/departures/1234/messages/7"),
              Some("/movements/departures/1234/messages/9"),
              Some("/movements/departures/1234/messages/11")
            )
          )

        when(mockDepartureConnector.getSummary(any())(any())).thenReturn(Future.successful(Some(messagesSummary)))
        when(mockDepartureConnector.getDeclarationRejectionMessage(any())(any()))
          .thenReturn(Future.successful(Some(notificationMessage)))

        messageService.declarationRejectionMessage(departureId).futureValue mustBe Some(notificationMessage)
      }

      "must return None when getSummary fails to get DeclarationRejectionMessage message" in {
        val messagesSummary =
          MessagesSummary(departureId, MessagesLocation(s"/movements/departures/${departureId.value}/messages/3", None, None, None, None))
        when(mockDepartureConnector.getSummary(any())(any())).thenReturn(Future.successful(Some(messagesSummary)))

        messageService.declarationRejectionMessage(departureId).futureValue mustBe None
      }

      "must return None when getSummary call fails to get MessagesSummary" in {
        when(mockDepartureConnector.getSummary(any())(any())).thenReturn(Future.successful(None))

        messageService.declarationRejectionMessage(departureId).futureValue mustBe None
      }
    }
  }
}
