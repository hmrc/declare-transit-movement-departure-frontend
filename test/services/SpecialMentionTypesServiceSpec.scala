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
import models.SpecialMentionList
import models.reference.SpecialMention
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SpecialMentionTypesServiceSpec extends SpecBase with BeforeAndAfterEach {

  private val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]
  private val service                                      = new SpecialMentionTypesService(mockRefDataConnector)

  private val specialMentionType1 = SpecialMention("1", "Export subject to restriction")
  private val specialMentionType2 = SpecialMention("2", "EXPORT")
  private val specialMentionType3 = SpecialMention("3", "Export subject to duties")

  override def beforeEach(): Unit = {
    reset(mockRefDataConnector)
    super.beforeEach()
  }

  "SpecialMentionTypesService" - {

    "getSpecialMentionTypes" - {
      "must return a list of sorted special mention types" in {

        when(mockRefDataConnector.getSpecialMentionTypes()(any(), any()))
          .thenReturn(Future.successful(Seq(specialMentionType1, specialMentionType2, specialMentionType3)))

        service.getSpecialMentionTypes().futureValue mustBe
          SpecialMentionList(Seq(specialMentionType2, specialMentionType3, specialMentionType1))

        verify(mockRefDataConnector).getSpecialMentionTypes()(any(), any())
      }
    }

  }
}
