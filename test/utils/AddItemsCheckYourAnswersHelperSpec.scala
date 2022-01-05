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

package utils

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import controllers.addItems.containers.routes._
import controllers.addItems.documents.routes._
import controllers.addItems.itemDetails.routes._
import controllers.addItems.packagesInformation.routes._
import controllers.addItems.previousReferences.routes._
import controllers.addItems.routes
import controllers.addItems.securityDetails.routes._
import controllers.addItems.traderDetails.routes._
import controllers.addItems.traderSecurityDetails.routes._
import models.DeclarationType.{Option3, Option4}
import models.reference._
import models.{CheckMode, CommonAddress, DocumentTypeList, Mode, PreviousReferencesDocumentTypeList}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.addItems._
import pages.addItems.containers.ContainerNumberPage
import pages.addItems.securityDetails.{AddDangerousGoodsCodePage, CommercialReferenceNumberPage, DangerousGoodsCodePage, TransportChargesPage}
import pages.addItems.traderDetails._
import pages.addItems.traderSecurityDetails._
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels.Text.Literal
import uk.gov.hmrc.viewmodels.{Html, MessageInterpolators}
import viewModels.AddAnotherViewModel

class AddItemsCheckYourAnswersHelperSpec extends SpecBase with UserAnswersSpecHelper with ScalaCheckPropertyChecks {

  val mode: Mode = CheckMode

  "AddItemsCheckYourAnswersHelper" - {

    "transportCharges" - {

      val methodOfPayment: MethodOfPayment = MethodOfPayment("CODE", "DESCRIPTION")

      "return None" - {
        "TransportChargesPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.transportCharges(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "TransportChargesPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(TransportChargesPage(index))(methodOfPayment)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.transportCharges(index)

          val label = msg"transportCharges.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$methodOfPayment"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = TransportChargesController.onPageLoad(lrn, itemIndex, mode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "containerSectionRow" - {

      val containerNumber: String = "CONTAINER NUMBER"

      "return None" - {
        "ContainerNumberPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.containerSectionRow(itemIndex, containerIndex)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "ContainerNumberPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(ContainerNumberPage(itemIndex, containerIndex))(containerNumber)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.containerSectionRow(itemIndex, containerIndex)

          val label = msg"addAnotherContainer.containerList.label".withArgs(containerIndex.display)

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$containerNumber"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = ContainerNumberController.onPageLoad(lrn, itemIndex, containerIndex, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> s"change-container-${containerIndex.display}")
                )
              )
            )
          )
        }
      }
    }

    "documentRow" - {

      val documentCode: String   = "DOCUMENT CODE"
      val document: DocumentType = DocumentType(documentCode, "DESCRIPTION", transportDocument = true)

      "return None" - {

        "DocumentTypePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.documentRow(itemIndex, documentIndex, DocumentTypeList(Nil))
          result mustBe None
        }

        "document type not found" in {

          val answers = emptyUserAnswers.unsafeSetVal(DocumentTypePage(index, referenceIndex))(documentCode)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.documentRow(index, referenceIndex, DocumentTypeList(Nil))

          result mustBe None

        }
      }

      "return Some(row)" - {

        "Option4 declaration type and first index" in {

          val answers = emptyUserAnswers
            .unsafeSetVal(DocumentTypePage(index, referenceIndex))(documentCode)
            .unsafeSetVal(DeclarationTypePage)(Option4)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.documentRow(index, referenceIndex, DocumentTypeList(Seq(document)))

          val label = lit"(${document.code}) ${document.description}"

          result mustBe Some(
            Row(
              key = Key(label),
              value = Value(lit""),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = controllers.addItems.documents.routes.DocumentCheckYourAnswersController.onPageLoad(lrn, index, documentIndex, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> s"change-document-${documentIndex.display}")
                )
              )
            )
          )
        }

        "non-Option4 declaration type" in {

          val answers = emptyUserAnswers
            .unsafeSetVal(DocumentTypePage(index, referenceIndex))(documentCode)
            .unsafeSetVal(DeclarationTypePage)(Option3)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.documentRow(index, referenceIndex, DocumentTypeList(Seq(document)))

          val label = lit"(${document.code}) ${document.description}"

          result mustBe Some(
            Row(
              key = Key(label),
              value = Value(lit""),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = controllers.addItems.documents.routes.DocumentCheckYourAnswersController.onPageLoad(lrn, index, documentIndex, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> s"change-document-${documentIndex.display}")
                ),
                Action(
                  content = msg"site.delete",
                  href = controllers.addItems.documents.routes.ConfirmRemoveDocumentController.onPageLoad(lrn, index, documentIndex, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> s"remove-document-${documentIndex.display}")
                )
              )
            )
          )
        }
      }
    }

    "documentSectionRow" - {

      val documentCode: String   = "DOCUMENT CODE"
      val document: DocumentType = DocumentType(documentCode, "DESCRIPTION", transportDocument = true)

      "return None" - {

        "DocumentTypePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.documentSectionRow(itemIndex, documentIndex, DocumentTypeList(Nil))
          result mustBe None
        }

        "document type not found" in {

          val answers = emptyUserAnswers.unsafeSetVal(DocumentTypePage(index, referenceIndex))(documentCode)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.documentSectionRow(index, referenceIndex, DocumentTypeList(Nil))

          result mustBe None

        }
      }

      "return Some(row)" - {

        "Option4 declaration type and first index" in {

          val answers = emptyUserAnswers
            .unsafeSetVal(DocumentTypePage(index, referenceIndex))(documentCode)
            .unsafeSetVal(DeclarationTypePage)(Option4)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.documentSectionRow(index, referenceIndex, DocumentTypeList(Seq(document)))

          val label = msg"addDocuments.documentList.label".withArgs(documentIndex.display)

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"(${document.code}) ${document.description}"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = controllers.addItems.documents.routes.DocumentCheckYourAnswersController.onPageLoad(lrn, index, documentIndex, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> s"change-document-${documentIndex.display}")
                )
              )
            )
          )
        }

        "non-Option4 declaration type" in {

          val answers = emptyUserAnswers
            .unsafeSetVal(DocumentTypePage(index, referenceIndex))(documentCode)
            .unsafeSetVal(DeclarationTypePage)(Option3)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.documentSectionRow(index, referenceIndex, DocumentTypeList(Seq(document)))

          val label = msg"addDocuments.documentList.label".withArgs(documentIndex.display)

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"(${document.code}) ${document.description}"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = controllers.addItems.documents.routes.DocumentCheckYourAnswersController.onPageLoad(lrn, itemIndex, documentIndex, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> s"change-document-${documentIndex.display}")
                )
              )
            )
          )
        }
      }
    }

    "itemRow" - {

      val itemDescription: String = "ITEM DESCRIPTION"

      "return None" - {
        "ItemDescriptionPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.itemRow(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "ItemDescriptionPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(ItemDescriptionPage(index))(itemDescription)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.itemRow(index)

          val label = lit"$itemDescription"

          result mustBe Some(
            Row(
              key = Key(label),
              value = Value(lit""),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.ItemsCheckYourAnswersController.onPageLoad(lrn, index).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> s"change-item-${index.display}")
                ),
                Action(
                  content = msg"site.delete",
                  href = routes.ConfirmRemoveItemController.onPageLoad(lrn, index).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> s"remove-item-${index.display}")
                )
              )
            )
          )
        }
      }
    }

    "addDocuments" - {

      "return None" - {
        "AddDocumentsPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.addDocuments(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddDocumentsPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddDocumentsPage(index))(true)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.addDocuments(index)

          val label = msg"addDocuments.checkYourAnswersLabel".withArgs(itemIndex.display)

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = AddDocumentsController.onPageLoad(lrn, itemIndex, mode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "traderDetailsConsignorName" - {

      val consignorName: String = "CONSIGNOR NAME"

      "return None" - {
        "TraderDetailsConsignorNamePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.traderDetailsConsignorName(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "TraderDetailsConsignorNamePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(TraderDetailsConsignorNamePage(index))(consignorName)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.traderDetailsConsignorName(index)

          val label = msg"traderDetailsConsignorName.checkYourAnswersLabel".withArgs(index.display)

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$consignorName"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = TraderDetailsConsignorNameController.onPageLoad(lrn, index, mode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "traderDetailsConsignorEoriNumber" - {

      val eoriNumber: String = "EORI NUMBER"

      "return None" - {
        "TraderDetailsConsignorEoriNumberPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.traderDetailsConsignorEoriNumber(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "TraderDetailsConsignorNamePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(TraderDetailsConsignorEoriNumberPage(index))(eoriNumber)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.traderDetailsConsignorEoriNumber(index)

          val label = msg"traderDetailsConsignorEoriNumber.checkYourAnswersLabel".withArgs(index.display)

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$eoriNumber"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = TraderDetailsConsignorEoriNumberController.onPageLoad(lrn, index, mode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "traderDetailsConsignorEoriKnown" - {

      "return None" - {
        "TraderDetailsConsignorEoriKnownPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.traderDetailsConsignorEoriKnown(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "TraderDetailsConsignorEoriKnownPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(TraderDetailsConsignorEoriKnownPage(index))(false)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.traderDetailsConsignorEoriKnown(index)

          val label = msg"traderDetailsConsignorEoriKnown.checkYourAnswersLabel".withArgs(index.display)

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.no"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = TraderDetailsConsignorEoriKnownController.onPageLoad(lrn, index, mode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "traderDetailsConsignorAddress" - {

      val address: CommonAddress = CommonAddress("LINE 1", "LINE 2", "POSTCODE", Country(CountryCode("CODE"), "DESCRIPTION"))

      "return None" - {
        "TraderDetailsConsignorAddressPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.traderDetailsConsignorAddress(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "TraderDetailsConsignorAddressPage defined at index" - {

          "TraderDetailsConsignorNamePage undefined at index" in {

            val consigneeName = msg"traderDetailsConsignorAddress.checkYourAnswersLabel.fallback"
            val label         = msg"traderDetailsConsignorAddress.checkYourAnswersLabel".withArgs(consigneeName)

            val answers = emptyUserAnswers.unsafeSetVal(TraderDetailsConsignorAddressPage(index))(address)

            val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
            val result = helper.traderDetailsConsignorAddress(index)

            result mustBe Some(
              Row(
                key = Key(label, classes = Seq("govuk-!-width-one-half")),
                value = Value(Html(Seq(address.AddressLine1, address.AddressLine2, address.postalCode, address.country.description).mkString("<br>"))),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = TraderDetailsConsignorAddressController.onPageLoad(lrn, itemIndex, mode).url,
                    visuallyHiddenText = Some(label)
                  )
                )
              )
            )
          }

          "TraderDetailsConsignorNamePage defined at index" in {

            val consigneeName: String = "CONSIGNEE NAME"

            val answers = emptyUserAnswers
              .unsafeSetVal(TraderDetailsConsignorAddressPage(index))(address)
              .unsafeSetVal(TraderDetailsConsignorNamePage(index))(consigneeName)

            val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
            val result = helper.traderDetailsConsignorAddress(index)

            val label = msg"traderDetailsConsignorAddress.checkYourAnswersLabel".withArgs(consigneeName)

            result mustBe Some(
              Row(
                key = Key(label, classes = Seq("govuk-!-width-one-half")),
                value = Value(Html(Seq(address.AddressLine1, address.AddressLine2, address.postalCode, address.country.description).mkString("<br>"))),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = TraderDetailsConsignorAddressController.onPageLoad(lrn, itemIndex, mode).url,
                    visuallyHiddenText = Some(label)
                  )
                )
              )
            )
          }
        }
      }
    }

    "traderDetailsConsigneeName" - {

      val consigneeName: String = "CONSIGNEE NAME"

      "return None" - {
        "TraderDetailsConsigneeNamePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.traderDetailsConsigneeName(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "TraderDetailsConsigneeNamePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(TraderDetailsConsigneeNamePage(index))(consigneeName)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.traderDetailsConsigneeName(index)

          val label = msg"traderDetailsConsigneeName.checkYourAnswersLabel".withArgs(index.display)

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$consigneeName"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = TraderDetailsConsigneeNameController.onPageLoad(lrn, index, mode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "traderDetailsConsigneeEoriNumber" - {

      val eoriNumber: String = "EORI NUMBER"

      "return None" - {
        "TraderDetailsConsigneeEoriNumberPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.traderDetailsConsigneeEoriNumber(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "TraderDetailsConsigneeEoriNumberPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(TraderDetailsConsigneeEoriNumberPage(index))(eoriNumber)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.traderDetailsConsigneeEoriNumber(index)

          val label = msg"traderDetailsConsigneeEoriNumber.checkYourAnswersLabel".withArgs(index.display)

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$eoriNumber"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = TraderDetailsConsigneeEoriNumberController.onPageLoad(lrn, index, mode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "traderDetailsConsigneeEoriKnown" - {

      "return None" - {
        "TraderDetailsConsigneeEoriKnownPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.traderDetailsConsigneeEoriKnown(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "TraderDetailsConsignorEoriKnownPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(TraderDetailsConsigneeEoriKnownPage(index))(true)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.traderDetailsConsigneeEoriKnown(index)

          val label = msg"traderDetailsConsigneeEoriKnown.checkYourAnswersLabel".withArgs(index.display)

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = TraderDetailsConsigneeEoriKnownController.onPageLoad(lrn, index, mode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "traderDetailsConsigneeAddress" - {

      val address: CommonAddress = CommonAddress("LINE 1", "LINE 2", "POSTCODE", Country(CountryCode("CODE"), "DESCRIPTION"))

      "return None" - {
        "TraderDetailsConsigneeAddressPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.traderDetailsConsigneeAddress(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "TraderDetailsConsigneeAddressPage defined at index" - {

          "TraderDetailsConsigneeNamePage undefined at index" in {

            val consigneeName = msg"traderDetailsConsigneeAddress.checkYourAnswersLabel.fallback"

            val answers = emptyUserAnswers.unsafeSetVal(TraderDetailsConsigneeAddressPage(index))(address)

            val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
            val result = helper.traderDetailsConsigneeAddress(index)

            val label = msg"traderDetailsConsigneeAddress.checkYourAnswersLabel".withArgs(consigneeName)

            result mustBe Some(
              Row(
                key = Key(label, classes = Seq("govuk-!-width-one-half")),
                value = Value(Html(Seq(address.AddressLine1, address.AddressLine2, address.postalCode, address.country.description).mkString("<br>"))),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = TraderDetailsConsigneeAddressController.onPageLoad(lrn, itemIndex, mode).url,
                    visuallyHiddenText = Some(label)
                  )
                )
              )
            )
          }

          "TraderDetailsConsigneeNamePage defined at index" in {

            val consigneeName: String = "CONSIGNEE NAME"

            val answers = emptyUserAnswers
              .unsafeSetVal(TraderDetailsConsigneeAddressPage(index))(address)
              .unsafeSetVal(TraderDetailsConsigneeNamePage(index))(consigneeName)

            val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
            val result = helper.traderDetailsConsigneeAddress(index)

            val label = msg"traderDetailsConsigneeAddress.checkYourAnswersLabel".withArgs(consigneeName)

            result mustBe Some(
              Row(
                key = Key(label, classes = Seq("govuk-!-width-one-half")),
                value = Value(Html(Seq(address.AddressLine1, address.AddressLine2, address.postalCode, address.country.description).mkString("<br>"))),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = TraderDetailsConsigneeAddressController.onPageLoad(lrn, itemIndex, mode).url,
                    visuallyHiddenText = Some(label)
                  )
                )
              )
            )
          }
        }
      }
    }

    "commodityCode" - {

      val commodityCode: String = "COMMODITY CODE"

      "return None" - {
        "CommodityCodePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.commodityCode(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "CommodityCodePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(CommodityCodePage(index))(commodityCode)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.commodityCode(index)

          val label = msg"commodityCode.checkYourAnswersLabel".withArgs(index.display)

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$commodityCode"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = CommodityCodeController.onPageLoad(lrn, index, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-commodity-code")
                )
              )
            )
          )
        }
      }
    }

    "totalNetMass" - {

      val mass: String = "MASS"

      "return None" - {
        "TotalNetMassPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.totalNetMass(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "TotalNetMassPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(TotalNetMassPage(index))(mass)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.totalNetMass(index)

          val label = msg"totalNetMass.checkYourAnswersLabel".withArgs(index.display)

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$mass"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = TotalNetMassController.onPageLoad(lrn, index, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-total-net-mass")
                )
              )
            )
          )
        }
      }
    }

    "isCommodityCodeKnown" - {

      "return None" - {
        "IsCommodityCodeKnownPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.isCommodityCodeKnown(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "IsCommodityCodeKnownPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(IsCommodityCodeKnownPage(index))(true)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.isCommodityCodeKnown(index)

          val label = msg"isCommodityCodeKnown.checkYourAnswersLabel".withArgs(index.display)

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = controllers.addItems.itemDetails.routes.IsCommodityCodeKnownController.onPageLoad(lrn, index, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-is-commodity-known")
                )
              )
            )
          )
        }
      }
    }

    "addTotalNetMass" - {

      "return None" - {
        "AddTotalNetMassPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.addTotalNetMass(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddTotalNetMassPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddTotalNetMassPage(index))(true)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.addTotalNetMass(index)

          val label = msg"addTotalNetMass.checkYourAnswersLabel".withArgs(index.display)

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = controllers.addItems.itemDetails.routes.AddTotalNetMassController.onPageLoad(lrn, index, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-add-total-net-mass")
                )
              )
            )
          )
        }
      }
    }

    "itemTotalGrossMass" - {

      val mass: Double = 1.0

      "return None" - {
        "ItemTotalGrossMassPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.itemTotalGrossMass(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "ItemTotalGrossMassPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(ItemTotalGrossMassPage(index))(mass)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.itemTotalGrossMass(index)

          val label = msg"itemTotalGrossMass.checkYourAnswersLabel".withArgs(index.display)

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$mass"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = controllers.addItems.itemDetails.routes.ItemTotalGrossMassController.onPageLoad(lrn, index, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-item-total-gross-mass")
                )
              )
            )
          )
        }
      }
    }

    "itemDescription" - {

      val description: String = "MASS"

      "return None" - {
        "ItemDescriptionPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.itemDescription(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "ItemDescriptionPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(ItemDescriptionPage(index))(description)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.itemDescription(index)

          val label = msg"itemDescription.checkYourAnswersLabel".withArgs(index.display)

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$description"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = controllers.addItems.itemDetails.routes.ItemDescriptionController.onPageLoad(lrn, index, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-item-description")
                )
              )
            )
          )
        }
      }
    }

    "previousReferenceSectionRow" - {

      val referenceCode: String        = "REFERENCE CODE"
      val referenceDescription: String = "REFERENCE DESCRIPTION"

      "return None" - {
        "ReferenceTypePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.previousReferenceSectionRow(index, referenceIndex, PreviousReferencesDocumentTypeList(Nil))
          result mustBe None
        }

        "previous reference type not found" in {

          val answers = emptyUserAnswers.unsafeSetVal(ReferenceTypePage(index, referenceIndex))(referenceCode)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.previousReferenceSectionRow(index, referenceIndex, PreviousReferencesDocumentTypeList(Nil))

          result mustBe None

        }
      }

      "return Some(row)" - {
        "ReferenceTypePage defined at index and previous reference type found" - {

          "previous reference has no description" in {

            val answers = emptyUserAnswers.unsafeSetVal(ReferenceTypePage(index, referenceIndex))(referenceCode)

            val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
            val result = helper.previousReferenceSectionRow(
              index,
              referenceIndex,
              PreviousReferencesDocumentTypeList(Seq(PreviousReferencesDocumentType(referenceCode, None)))
            )

            val label = msg"addAdministrativeReference.administrativeReferenceList.label".withArgs(referenceIndex.display)

            result mustBe Some(
              Row(
                key = Key(label, classes = Seq("govuk-!-width-one-half")),
                value = Value(lit"($referenceCode) "),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = ReferenceCheckYourAnswersController.onPageLoad(lrn, index, referenceIndex, mode).url,
                    visuallyHiddenText = Some(label),
                    attributes = Map("id" -> s"change-reference-${referenceIndex.display}")
                  )
                )
              )
            )
          }

          "previous reference has description" in {

            val answers = emptyUserAnswers.unsafeSetVal(ReferenceTypePage(index, referenceIndex))(referenceCode)

            val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
            val result = helper.previousReferenceSectionRow(
              index,
              referenceIndex,
              PreviousReferencesDocumentTypeList(Seq(PreviousReferencesDocumentType(referenceCode, Some(referenceDescription))))
            )

            val label = msg"addAdministrativeReference.administrativeReferenceList.label".withArgs(referenceIndex.display)

            result mustBe Some(
              Row(
                key = Key(label, classes = Seq("govuk-!-width-one-half")),
                value = Value(lit"($referenceCode) $referenceDescription"),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = ReferenceCheckYourAnswersController.onPageLoad(lrn, index, referenceIndex, mode).url,
                    visuallyHiddenText = Some(label),
                    attributes = Map("id" -> s"change-reference-${referenceIndex.display}")
                  )
                )
              )
            )
          }
        }
      }
    }

    "previousAdministrativeReferenceType" - {

      val referenceCode: String        = "REFERENCE CODE"
      val referenceDescription: String = "REFERENCE DESCRIPTION"

      "return None" - {
        "ReferenceTypePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.previousAdministrativeReferenceRow(index, referenceIndex, PreviousReferencesDocumentTypeList(Nil))
          result mustBe None
        }

        "previous reference type not found" in {

          val answers = emptyUserAnswers.unsafeSetVal(ReferenceTypePage(index, referenceIndex))(referenceCode)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.previousAdministrativeReferenceRow(index, referenceIndex, PreviousReferencesDocumentTypeList(Nil))

          result mustBe None

        }
      }

      "return Some(row)" - {
        "ReferenceTypePage defined at index and previous reference type found" - {

          "previous reference has no description" in {

            val answers = emptyUserAnswers.unsafeSetVal(ReferenceTypePage(index, referenceIndex))(referenceCode)

            val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
            val result = helper.previousAdministrativeReferenceRow(
              index,
              referenceIndex,
              PreviousReferencesDocumentTypeList(Seq(PreviousReferencesDocumentType(referenceCode, None)))
            )

            val label = lit"($referenceCode) "

            result mustBe Some(
              Row(
                key = Key(label),
                value = Value(lit""),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = ReferenceCheckYourAnswersController.onPageLoad(lrn, index, referenceIndex, mode).url,
                    visuallyHiddenText = Some(label),
                    attributes = Map("id" -> s"change-reference-document-${referenceIndex.display}")
                  ),
                  Action(
                    content = msg"site.delete",
                    href = ConfirmRemovePreviousAdministrativeReferenceController.onPageLoad(lrn, index, referenceIndex, mode).url,
                    visuallyHiddenText = Some(label),
                    attributes = Map("id" -> s"remove-reference-document-${referenceIndex.display}")
                  )
                )
              )
            )
          }

          "previous reference has description" in {

            val answers = emptyUserAnswers.unsafeSetVal(ReferenceTypePage(index, referenceIndex))(referenceCode)

            val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
            val result = helper.previousAdministrativeReferenceRow(
              index,
              referenceIndex,
              PreviousReferencesDocumentTypeList(Seq(PreviousReferencesDocumentType(referenceCode, Some(referenceDescription))))
            )

            val label = lit"($referenceCode) $referenceDescription"

            result mustBe Some(
              Row(
                key = Key(label),
                value = Value(lit""),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = ReferenceCheckYourAnswersController.onPageLoad(lrn, index, referenceIndex, mode).url,
                    visuallyHiddenText = Some(label),
                    attributes = Map("id" -> s"change-reference-document-${referenceIndex.display}")
                  ),
                  Action(
                    content = msg"site.delete",
                    href = ConfirmRemovePreviousAdministrativeReferenceController.onPageLoad(lrn, index, referenceIndex, mode).url,
                    visuallyHiddenText = Some(label),
                    attributes = Map("id" -> s"remove-reference-document-${referenceIndex.display}")
                  )
                )
              )
            )
          }
        }
      }
    }

    "addAdministrativeReference" - {

      "return None" - {
        "AddAdministrativeReferencePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.addAdministrativeReference(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddAdministrativeReferencePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddAdministrativeReferencePage(index))(true)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.addAdministrativeReference(index)

          val label = msg"addAdministrativeReference.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = AddAdministrativeReferenceController.onPageLoad(lrn, index, mode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "addAnotherPreviousReferences" - {
      "create view model" - {

        val content = Literal("foo")

        "with href pointing to AddAnotherPreviousAdministrativeReferenceController" - {
          "when AddAdministrativeReferencePage is true" in {

            val answers = emptyUserAnswers.unsafeSetVal(AddAdministrativeReferencePage(itemIndex))(true)

            val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
            val result = helper.addAnotherPreviousReferences(itemIndex, content)
            result mustBe AddAnotherViewModel(
              href = AddAnotherPreviousAdministrativeReferenceController.onPageLoad(lrn, itemIndex, mode).url,
              content = content
            )
          }
        }

        "with href pointing to AddAdministrativeReferenceController" - {
          "when AddAdministrativeReferencePage is false or undefined" in {

            forAll(arbitrary[Option[Boolean]].retryUntil(!_.contains(true))) {
              maybeBool =>
                val answers = emptyUserAnswers.unsafeSetOpt(AddAdministrativeReferencePage(itemIndex))(maybeBool)

                val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
                val result = helper.addAnotherPreviousReferences(itemIndex, content)
                result mustBe AddAnotherViewModel(
                  href = AddAdministrativeReferenceController.onPageLoad(lrn, itemIndex, mode).url,
                  content = content
                )
            }
          }
        }
      }
    }

    "packageRow" - {

      val packageType: PackageType = PackageType("CODE", "DESCRIPTION")

      "return None" - {
        "PackageTypePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.packageRow(itemIndex, packageIndex)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "PackageTypePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(PackageTypePage(itemIndex, packageIndex))(packageType)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.packageRow(itemIndex, packageIndex)

          val label = lit"$packageType"

          result mustBe Some(
            Row(
              key = Key(label),
              value = Value(lit""),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = PackageCheckYourAnswersController.onPageLoad(lrn, itemIndex, packageIndex, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> s"change-package-${packageIndex.display}")
                ),
                Action(
                  content = msg"site.delete",
                  href = RemovePackageController.onPageLoad(lrn, itemIndex, packageIndex, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> s"remove-package-${packageIndex.display}")
                )
              )
            )
          )
        }
      }
    }

    "packageSectionRow" - {

      val description: String      = "DESCRIPTION"
      val code: String             = "CODE"
      val packageType: PackageType = PackageType(code, description)

      "return None" - {
        "PackageTypePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.packageSectionRow(itemIndex, packageIndex)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "PackageTypePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(PackageTypePage(itemIndex, packageIndex))(packageType)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.packageSectionRow(itemIndex, packageIndex)

          val label = msg"addAnotherPackage.packageList.label".withArgs(packageIndex.display)

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$description ($code)"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = PackageCheckYourAnswersController.onPageLoad(lrn, itemIndex, packageIndex, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> s"change-package-${packageIndex.display}")
                )
              )
            )
          )
        }
      }
    }

    "packageType" - {

      val packageType: PackageType = PackageType("CODE", "DESCRIPTION")

      "return None" - {
        "PackageTypePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.packageType(itemIndex, packageIndex)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "PackageTypePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(PackageTypePage(itemIndex, packageIndex))(packageType)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.packageType(itemIndex, packageIndex)

          val label = msg"packageType.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$packageType"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = PackageTypeController.onPageLoad(lrn, itemIndex, packageIndex, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> s"change-package-type")
                )
              )
            )
          )
        }
      }
    }

    "numberOfPackages" - {

      val numberOfPackages: Int = 1

      "return None" - {
        "HowManyPackagesPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.numberOfPackages(itemIndex, packageIndex)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "HowManyPackagesPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(HowManyPackagesPage(itemIndex, packageIndex))(numberOfPackages)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.numberOfPackages(itemIndex, packageIndex)

          val label = msg"howManyPackages.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$numberOfPackages"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = HowManyPackagesController.onPageLoad(lrn, itemIndex, packageIndex, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> s"change-number-of-packages")
                )
              )
            )
          )
        }
      }
    }

    "totalPieces" - {

      val totalPieces: Int = 1

      "return None" - {
        "TotalPiecesPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.totalPieces(itemIndex, packageIndex)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "TotalPiecesPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(TotalPiecesPage(itemIndex, packageIndex))(totalPieces)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.totalPieces(itemIndex, packageIndex)

          val label = msg"totalPieces.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$totalPieces"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = TotalPiecesController.onPageLoad(lrn, itemIndex, packageIndex, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> s"change-total-pieces")
                )
              )
            )
          )
        }
      }
    }

    "addMark" - {

      "return None" - {
        "AddMarkPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.addMark(itemIndex, packageIndex)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddMarkPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddMarkPage(itemIndex, packageIndex))(true)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.addMark(itemIndex, packageIndex)

          val label = msg"addMark.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = AddMarkController.onPageLoad(lrn, itemIndex, packageIndex, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-add-mark")
                )
              )
            )
          )
        }
      }
    }

    "mark" - {

      val markOrNumber: String = "mark"

      "return None" - {
        "DeclareMarkPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.mark(itemIndex, packageIndex)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "DeclareMarkPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(DeclareMarkPage(itemIndex, packageIndex))(markOrNumber)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.mark(itemIndex, packageIndex)

          val label = msg"declareMark.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$markOrNumber"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = DeclareMarkController.onPageLoad(lrn, itemIndex, packageIndex, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-mark")
                )
              )
            )
          )
        }
      }
    }

    "addAnotherPackage" - {
      "create view model" - {

        val content = Literal("foo")

        "with href pointing to AddAnotherPackageController" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddDocumentsPage(itemIndex))(true)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.addAnotherPackage(itemIndex, content)
          result mustBe AddAnotherViewModel(
            href = AddAnotherPackageController.onPageLoad(lrn, itemIndex, mode).url,
            content = content
          )
        }
      }
    }

    "addAnotherDocument" - {
      "create view model" - {

        val content = Literal("foo")

        "with href pointing to AddAnotherDocumentController" - {
          "when AddDocumentsPage is true" in {

            val answers = emptyUserAnswers.unsafeSetVal(AddDocumentsPage(itemIndex))(true)

            val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
            val result = helper.addAnotherDocument(itemIndex, content)
            result mustBe AddAnotherViewModel(
              href = AddAnotherDocumentController.onPageLoad(lrn, itemIndex, mode).url,
              content = content
            )
          }
        }

        "with href pointing to AddDocumentsController" - {
          "when AddDocumentsPage is false or undefined" in {

            forAll(arbitrary[Option[Boolean]].retryUntil(!_.contains(true))) {
              maybeBool =>
                val answers = emptyUserAnswers.unsafeSetOpt(AddDocumentsPage(itemIndex))(maybeBool)

                val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
                val result = helper.addAnotherDocument(itemIndex, content)
                result mustBe AddAnotherViewModel(
                  href = AddDocumentsController.onPageLoad(lrn, itemIndex, mode).url,
                  content = content
                )
            }
          }
        }
      }
    }

    "commercialReferenceNumber" - {

      val referenceNumber: String = "REFERENCE NUMBER"

      "return None" - {
        "CommercialReferenceNumberPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.commercialReferenceNumber(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "CommercialReferenceNumberPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(CommercialReferenceNumberPage(index))(referenceNumber)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.commercialReferenceNumber(index)

          val label = msg"commercialReferenceNumber.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$referenceNumber"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = CommercialReferenceNumberController.onPageLoad(lrn, itemIndex, mode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "addDangerousGoodsCode" - {

      "return None" - {
        "AddDangerousGoodsCodePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.addDangerousGoodsCode(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddDangerousGoodsCodePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddDangerousGoodsCodePage(index))(true)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.addDangerousGoodsCode(index)

          val label = msg"addDangerousGoodsCode.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = AddDangerousGoodsCodeController.onPageLoad(lrn, itemIndex, mode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "dangerousGoodsCode" - {

      val code: String = "CODE"

      "return None" - {
        "DangerousGoodsCodePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.dangerousGoodsCode(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "DangerousGoodsCodePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(DangerousGoodsCodePage(index))(code)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.dangerousGoodsCode(index)

          val label = msg"dangerousGoodsCode.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$code"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = DangerousGoodsCodeController.onPageLoad(lrn, itemIndex, mode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "addSecurityConsignorsEori" - {

      "return None" - {
        "AddDangerousGoodsCodePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.addSecurityConsignorsEori(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddDangerousGoodsCodePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddSecurityConsignorsEoriPage(index))(true)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.addSecurityConsignorsEori(index)

          val label = msg"addSecurityConsignorsEori.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = AddSecurityConsignorsEoriController.onPageLoad(lrn, index, mode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "addSecurityConsigneesEori" - {

      "return None" - {
        "AddSecurityConsigneesEoriPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.addSecurityConsigneesEori(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddSecurityConsigneesEoriPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddSecurityConsigneesEoriPage(index))(true)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.addSecurityConsigneesEori(index)

          val label = msg"addSecurityConsigneesEori.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = AddSecurityConsigneesEoriController.onPageLoad(lrn, index, mode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "securityConsigneeName" - {

      val consigneeName: String = "CONSIGNEE NAME"

      "return None" - {
        "SecurityConsigneeNamePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.securityConsigneeName(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "SecurityConsigneeNamePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(SecurityConsigneeNamePage(index))(consigneeName)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.securityConsigneeName(index)

          val label = msg"securityConsigneeName.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$consigneeName"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = SecurityConsigneeNameController.onPageLoad(lrn, index, mode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "securityConsignorName" - {

      val consignorName: String = "CONSIGNOR NAME"

      "return None" - {
        "SecurityConsignorNamePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.securityConsignorName(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "SecurityConsignorNamePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(SecurityConsignorNamePage(index))(consignorName)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.securityConsignorName(index)

          val label = msg"securityConsignorName.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$consignorName"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = SecurityConsignorNameController.onPageLoad(lrn, index, mode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "securityConsigneeAddress" - {

      val address: CommonAddress = CommonAddress("LINE 1", "LINE 2", "POSTCODE", Country(CountryCode("CODE"), "DESCRIPTION"))

      "return None" - {
        "SecurityConsigneeAddressPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.securityConsigneeAddress(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "SecurityConsigneeAddressPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(SecurityConsigneeAddressPage(index))(address)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.securityConsigneeAddress(index)

          val label = msg"securityConsigneeAddress.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(Html(Seq(address.AddressLine1, address.AddressLine2, address.postalCode, address.country.description).mkString("<br>"))),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = SecurityConsigneeAddressController.onPageLoad(lrn, index, mode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "securityConsignorAddress" - {

      val address: CommonAddress = CommonAddress("LINE 1", "LINE 2", "POSTCODE", Country(CountryCode("CODE"), "DESCRIPTION"))

      "return None" - {
        "SecurityConsignorAddressPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.securityConsignorAddress(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "SecurityConsignorAddressPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(SecurityConsignorAddressPage(index))(address)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.securityConsignorAddress(index)

          val label = msg"securityConsignorAddress.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(Html(Seq(address.AddressLine1, address.AddressLine2, address.postalCode, address.country.description).mkString("<br>"))),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = SecurityConsignorAddressController.onPageLoad(lrn, index, mode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "securityConsigneeEori" - {

      val eoriNumber: String = "EORI NUMBER"

      "return None" - {
        "SecurityConsigneeEoriPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.securityConsigneeEori(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "SecurityConsigneeEoriPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(SecurityConsigneeEoriPage(index))(eoriNumber)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.securityConsigneeEori(index)

          val label = msg"securityConsigneeEori.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$eoriNumber"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = SecurityConsigneeEoriController.onPageLoad(lrn, index, mode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "securityConsignorEori" - {

      val eoriNumber: String = "EORI NUMBER"

      "return None" - {
        "SecurityConsignorEoriPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.securityConsignorEori(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "SecurityConsignorEoriPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(SecurityConsignorEoriPage(index))(eoriNumber)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.securityConsignorEori(index)

          val label = msg"securityConsignorEori.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$eoriNumber"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = SecurityConsignorEoriController.onPageLoad(lrn, index, mode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "referenceTypeRow" - {

      val code = "code"

      "return None" - {

        "ReferenceTypePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.referenceTypeRow(index, referenceIndex, PreviousReferencesDocumentTypeList(Nil))
          result mustBe None
        }

        "reference type not found" in {

          val answers = emptyUserAnswers.unsafeSetVal(ReferenceTypePage(index, referenceIndex))(code)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.referenceTypeRow(index, referenceIndex, PreviousReferencesDocumentTypeList(Nil))

          result mustBe None
        }
      }

      "return Some(row)" - {
        "ReferenceTypePage defined at index" - {

          "description defined" in {

            val description = "description"
            val reference   = PreviousReferencesDocumentType(code, Some(description))

            val answers = emptyUserAnswers.unsafeSetVal(ReferenceTypePage(index, referenceIndex))(code)

            val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
            val result = helper.referenceTypeRow(index, referenceIndex, PreviousReferencesDocumentTypeList(Seq(reference)))

            val label = msg"referenceType.checkYourAnswersLabel"

            result mustBe Some(
              Row(
                key = Key(label, classes = Seq("govuk-!-width-one-half")),
                value = Value(lit"($code) $description"),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = ReferenceTypeController.onPageLoad(lrn, index, referenceIndex, mode).url,
                    visuallyHiddenText = Some(label),
                    attributes = Map("id" -> "change-reference-type")
                  )
                )
              )
            )
          }

          "description not defined" in {

            val reference = PreviousReferencesDocumentType(code, None)

            val answers = emptyUserAnswers.unsafeSetVal(ReferenceTypePage(index, referenceIndex))(code)

            val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
            val result = helper.referenceTypeRow(index, referenceIndex, PreviousReferencesDocumentTypeList(Seq(reference)))

            val label = msg"referenceType.checkYourAnswersLabel"

            result mustBe Some(
              Row(
                key = Key(label, classes = Seq("govuk-!-width-one-half")),
                value = Value(lit"(${reference.code}) "),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = ReferenceTypeController.onPageLoad(lrn, index, referenceIndex, mode).url,
                    visuallyHiddenText = Some(label),
                    attributes = Map("id" -> "change-reference-type")
                  )
                )
              )
            )
          }
        }
      }
    }

    "previousReferenceRow" - {

      val reference: String = "REFERENCE"

      "return None" - {
        "PreviousReferencePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.previousReferenceRow(index, referenceIndex)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "PreviousReferencePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(PreviousReferencePage(index, referenceIndex))(reference)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.previousReferenceRow(index, referenceIndex)

          val label = msg"previousReference.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$reference"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = PreviousReferenceController.onPageLoad(lrn, index, referenceIndex, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-previous-reference")
                )
              )
            )
          )
        }
      }
    }

    "addExtraReferenceInformationRow" - {

      "return None" - {
        "AddExtraInformationPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.addExtraReferenceInformationRow(index, referenceIndex)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddExtraInformationPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddExtraInformationPage(index, referenceIndex))(true)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.addExtraReferenceInformationRow(index, referenceIndex)

          val label = msg"addExtraInformation.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = AddExtraInformationController.onPageLoad(lrn, index, referenceIndex, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-add-extra-reference-information")
                )
              )
            )
          )
        }
      }
    }

    "extraReferenceInformationRow" - {

      val extraInfo: String = "INFO"

      "return None" - {
        "ExtraInformationPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.extraReferenceInformationRow(index, referenceIndex)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "ExtraInformationPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(ExtraInformationPage(index, referenceIndex))(extraInfo)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.extraReferenceInformationRow(index, referenceIndex)

          val label = msg"extraInformation.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$extraInfo"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = ExtraInformationController.onPageLoad(lrn, index, referenceIndex, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-extra-reference-information")
                )
              )
            )
          )
        }
      }
    }

    "tirCarnetReferenceRow" - {

      val reference: String = "REFERENCE"

      "return None" - {
        "TIRCarnetReferencePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.tirCarnetReferenceRow(index, referenceIndex)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "TIRCarnetReferencePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(TIRCarnetReferencePage(index, referenceIndex))(reference)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.tirCarnetReferenceRow(index, referenceIndex)

          val label = msg"tirCarnetReference.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$reference"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = TIRCarnetReferenceController.onPageLoad(lrn, index, referenceIndex, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-tir-carnet-reference")
                )
              )
            )
          )
        }
      }
    }

    "documentTypeRow" - {

      val documentCode: String   = "DOCUMENT CODE"
      val document: DocumentType = DocumentType(documentCode, "DESCRIPTION", transportDocument = true)

      "return None" - {

        "DocumentTypePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.documentTypeRow(index, referenceIndex, DocumentTypeList(Nil))
          result mustBe None
        }

        "document type not found" in {

          val answers = emptyUserAnswers.unsafeSetVal(DocumentTypePage(index, referenceIndex))(documentCode)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.documentTypeRow(index, referenceIndex, DocumentTypeList(Nil))

          result mustBe None
        }
      }

      "return Some(row)" - {
        "DocumentTypePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(DocumentTypePage(index, referenceIndex))(documentCode)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.documentTypeRow(index, referenceIndex, DocumentTypeList(Seq(document)))

          val label = msg"documentType.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"(${document.code}) ${document.description}"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = DocumentTypeController.onPageLoad(lrn, index, referenceIndex, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-document-type")
                )
              )
            )
          )
        }
      }
    }

    "documentReferenceRow" - {

      val reference: String = "REFERENCE"

      "return None" - {
        "DocumentReferencePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.documentReferenceRow(index, referenceIndex)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "DocumentReferencePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(DocumentReferencePage(index, referenceIndex))(reference)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.documentReferenceRow(index, referenceIndex)

          val label = msg"documentReference.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$reference"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = DocumentReferenceController.onPageLoad(lrn, index, referenceIndex, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-document-reference")
                )
              )
            )
          )
        }
      }
    }

    "addExtraDocumentInformationRow" - {

      "return None" - {
        "AddExtraDocumentInformationPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.addExtraDocumentInformationRow(index, referenceIndex)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddExtraDocumentInformationPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddExtraDocumentInformationPage(index, referenceIndex))(true)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.addExtraDocumentInformationRow(index, referenceIndex)

          val label = msg"addExtraDocumentInformation.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = AddExtraDocumentInformationController.onPageLoad(lrn, index, referenceIndex, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-add-extra-document-information")
                )
              )
            )
          )
        }
      }
    }

    "extraDocumentInformationRow" - {

      val information: String = "INFORMATION"

      "return None" - {
        "DocumentExtraInformationPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.extraDocumentInformationRow(index, referenceIndex)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "DocumentExtraInformationPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(DocumentExtraInformationPage(index, referenceIndex))(information)

          val helper = new AddItemsCheckYourAnswersHelper(answers, mode)
          val result = helper.extraDocumentInformationRow(index, referenceIndex)

          val label = msg"documentExtraInformation.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$information"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = DocumentExtraInformationController.onPageLoad(lrn, index, referenceIndex, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-extra-document-information")
                )
              )
            )
          )
        }
      }
    }

  }
}
