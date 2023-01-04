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

package services

import base.SpecBase
import connectors.ReferenceDataConnector
import models.MethodOfPaymentList
import models.reference.MethodOfPayment
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MethodsOfPaymentServiceSpec extends SpecBase with BeforeAndAfterEach {

  private val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]
  private val service                                      = new MethodsOfPaymentService(mockRefDataConnector)

  private val methodOfPayment1 = MethodOfPayment("1", "Payment in cash")
  private val methodOfPayment2 = MethodOfPayment("2", "Payment by credit card")

  override def beforeEach(): Unit = {
    reset(mockRefDataConnector)
    super.beforeEach()
  }

  "MethodsOfPaymentService" - {

    "getMethodsOfPayment" - {
      "must return a list of sorted methods of payment" in {

        when(mockRefDataConnector.getMethodsOfPayment()(any(), any()))
          .thenReturn(Future.successful(Seq(methodOfPayment1, methodOfPayment2)))

        service.getMethodsOfPayment().futureValue mustBe
          MethodOfPaymentList(Seq(methodOfPayment2, methodOfPayment1))

        verify(mockRefDataConnector).getMethodsOfPayment()(any(), any())
      }
    }

  }
}
