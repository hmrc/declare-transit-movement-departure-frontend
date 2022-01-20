/*
 * Copyright 2022 HM Revenue & Customs
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
import models.DocumentTypeList
import models.reference.DocumentType
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DocumentTypesServiceSpec extends SpecBase with BeforeAndAfterEach {

  private val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]
  private val service                                      = new DocumentTypesService(mockRefDataConnector)

  private val documentType1 = DocumentType("3", "CERTIFICATE OF QUALITY", transportDocument = false)
  private val documentType2 = DocumentType("1", "Bill of lading", transportDocument = true)
  private val documentType3 = DocumentType("2", "Certificate of conformity", transportDocument = false)

  override def beforeEach(): Unit = {
    reset(mockRefDataConnector)
    super.beforeEach()
  }

  "DocumentTypesService" - {

    "getDocumentTypes" - {
      "must return a list of sorted package types" in {

        when(mockRefDataConnector.getDocumentTypes()(any(), any()))
          .thenReturn(Future.successful(Seq(documentType1, documentType2, documentType3)))

        service.getDocumentTypes().futureValue mustBe
          DocumentTypeList(Seq(documentType2, documentType3, documentType1))

        verify(mockRefDataConnector).getDocumentTypes()(any(), any())
      }
    }

  }
}
