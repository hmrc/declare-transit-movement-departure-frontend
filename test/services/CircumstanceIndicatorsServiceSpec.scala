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
import models.CircumstanceIndicatorList
import models.reference.CircumstanceIndicator
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CircumstanceIndicatorsServiceSpec extends SpecBase with BeforeAndAfterEach {

  private val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]
  private val service                                      = new CircumstanceIndicatorsService(mockRefDataConnector)

  private val circumstanceIndicator1 = CircumstanceIndicator("1", "Road mode of transport")
  private val circumstanceIndicator2 = CircumstanceIndicator("2", "Authorised economic operators")
  private val circumstanceIndicator3 = CircumstanceIndicator("3", "Rail mode of transport")

  override def beforeEach(): Unit = {
    reset(mockRefDataConnector)
    super.beforeEach()
  }

  "CircumstanceIndicatorsService" - {

    "getCircumstanceIndicators" - {
      "must return a list of sorted circumstance indicators" in {

        when(mockRefDataConnector.getCircumstanceIndicators()(any(), any()))
          .thenReturn(Future.successful(Seq(circumstanceIndicator1, circumstanceIndicator2, circumstanceIndicator3)))

        service.getCircumstanceIndicators().futureValue mustBe
          CircumstanceIndicatorList(Seq(circumstanceIndicator2, circumstanceIndicator3, circumstanceIndicator1))

        verify(mockRefDataConnector).getCircumstanceIndicators()(any(), any())
      }
    }

  }
}
