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
import models.PreviousReferencesDocumentTypeList
import models.reference.PreviousReferencesDocumentType
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PreviousDocumentTypesServiceSpec extends SpecBase with BeforeAndAfterEach {

  private val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]
  private val service                                      = new PreviousDocumentTypesService(mockRefDataConnector)

  private val documentType1 = PreviousReferencesDocumentType("1", Some("Certificate of quality"))
  private val documentType2 = PreviousReferencesDocumentType("2", Some("Bill of lading"))
  private val documentType3 = PreviousReferencesDocumentType("3", Some("Certificate of conformity"))
  private val documentType4 = PreviousReferencesDocumentType("4", None)

  override def beforeEach(): Unit = {
    reset(mockRefDataConnector)
    super.beforeEach()
  }

  "PreviousDocumentTypesService" - {

    "getPackageTypes" - {
      "must return a list of sorted previous document types" in {

        when(mockRefDataConnector.getPreviousReferencesDocumentTypes()(any(), any()))
          .thenReturn(Future.successful(Seq(documentType1, documentType2, documentType3, documentType4)))

        service.getPreviousDocumentTypes().futureValue mustBe
          PreviousReferencesDocumentTypeList(Seq(documentType4, documentType2, documentType3, documentType1))

        verify(mockRefDataConnector).getPreviousReferencesDocumentTypes()(any(), any())
      }
    }

  }
}
