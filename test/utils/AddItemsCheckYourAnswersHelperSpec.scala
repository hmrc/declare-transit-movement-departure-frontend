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
import models.{CheckMode, CommonAddress, DocumentTypeList, PreviousReferencesDocumentTypeList}
import pages._
import pages.addItems._
import pages.addItems.containers.ContainerNumberPage
import pages.addItems.securityDetails.{AddDangerousGoodsCodePage, CommercialReferenceNumberPage, DangerousGoodsCodePage, TransportChargesPage}
import pages.addItems.traderDetails._
import pages.addItems.traderSecurityDetails._
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels.{Html, MessageInterpolators}

class AddItemsCheckYourAnswersHelperSpec extends SpecBase with UserAnswersSpecHelper {

  "AddItemsCheckYourAnswersHelper" - {

    "transportCharges" - {

      val methodOfPayment: MethodOfPayment = MethodOfPayment("CODE", "DESCRIPTION")

      "return None" - {
        "TransportChargesPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.transportCharges(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "TransportChargesPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(TransportChargesPage(index))(methodOfPayment)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.transportCharges(index)

          result mustBe Some(
            Row(
              key = Key(msg"transportCharges.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$methodOfPayment"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = TransportChargesController.onPageLoad(lrn, itemIndex, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"transportCharges.checkYourAnswersLabel"))
                )
              )
            )
          )
        }
      }
    }

    "containerNumber" - {

      val containerNumber: String = "CONTAINER NUMBER"

      "return None" - {
        "ContainerNumberPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.containerRow(itemIndex, containerIndex)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "ContainerNumberPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(ContainerNumberPage(itemIndex, containerIndex))(containerNumber)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.containerRow(itemIndex, containerIndex)

          result mustBe Some(
            Row(
              key = Key(lit"$containerNumber"),
              value = Value(lit""),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = ContainerNumberController.onPageLoad(lrn, itemIndex, containerIndex, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(containerNumber)),
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.documentRow(itemIndex, documentIndex, DocumentTypeList(Nil), removable = true)
          result mustBe None
        }

        "document type not found" in {

          val answers = emptyUserAnswers.unsafeSetVal(DocumentTypePage(index, referenceIndex))(documentCode)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.documentRow(index, referenceIndex, DocumentTypeList(Nil), removable = true)

          result mustBe None

        }
      }

      "return Some(row)" - {

        "Option4 declaration type and first index" in {

          val answers = emptyUserAnswers
            .unsafeSetVal(DocumentTypePage(index, referenceIndex))(documentCode)
            .unsafeSetVal(DeclarationTypePage)(Option4)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.documentRow(index, referenceIndex, DocumentTypeList(Seq(document)), removable = true)

          val key = s"(${document.code}) ${document.description}"

          result mustBe Some(
            Row(
              key = Key(lit"$key"),
              value = Value(lit""),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = controllers.addItems.documents.routes.TIRCarnetReferenceController.onPageLoad(lrn, index, documentIndex, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(key)),
                  attributes = Map("id" -> s"change-document-${index.display}")
                )
              )
            )
          )
        }

        "non-Option4 declaration type" - {

          "removable" in {

            val answers = emptyUserAnswers
              .unsafeSetVal(DocumentTypePage(index, referenceIndex))(documentCode)
              .unsafeSetVal(DeclarationTypePage)(Option3)

            val helper = new AddItemsCheckYourAnswersHelper(answers)
            val result = helper.documentRow(index, referenceIndex, DocumentTypeList(Seq(document)), removable = true)

            result mustBe Some(
              Row(
                key = Key(lit"(${document.code}) ${document.description}"),
                value = Value(lit""),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = controllers.addItems.documents.routes.DocumentTypeController.onPageLoad(lrn, index, documentIndex, CheckMode).url,
                    visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(s"(${document.code}) ${document.description}")),
                    attributes = Map("id" -> s"change-document-${index.display}")
                  ),
                  Action(
                    content = msg"site.delete",
                    href = controllers.addItems.documents.routes.ConfirmRemoveDocumentController.onPageLoad(lrn, index, documentIndex, CheckMode).url,
                    visuallyHiddenText = Some(msg"site.delete.hidden".withArgs(s"(${document.code}) ${document.description}")),
                    attributes = Map("id" -> s"remove-document-${index.display}")
                  )
                )
              )
            )
          }

          "not removable" in {

            val answers = emptyUserAnswers
              .unsafeSetVal(DocumentTypePage(index, referenceIndex))(documentCode)
              .unsafeSetVal(DeclarationTypePage)(Option3)

            val helper = new AddItemsCheckYourAnswersHelper(answers)
            val result = helper.documentRow(index, referenceIndex, DocumentTypeList(Seq(document)), removable = false)

            val key = s"(${document.code}) ${document.description}"

            result mustBe Some(
              Row(
                key = Key(lit"$key"),
                value = Value(lit""),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = controllers.addItems.documents.routes.DocumentTypeController.onPageLoad(lrn, itemIndex, documentIndex, CheckMode).url,
                    visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(key)),
                    attributes = Map("id" -> s"change-document-${documentIndex.display}")
                  )
                )
              )
            )
          }
        }
      }
    }

    "itemRow" - {

      val itemDescription: String = "ITEM DESCRIPTION"

      "return None" - {
        "ItemDescriptionPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.itemRow(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "ItemDescriptionPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(ItemDescriptionPage(index))(itemDescription)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.itemRow(index)

          result mustBe Some(
            Row(
              key = Key(lit"$itemDescription"),
              value = Value(lit""),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.ItemsCheckYourAnswersController.onPageLoad(lrn, index).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(itemDescription)),
                  attributes = Map("id" -> s"change-item-${index.display}")
                ),
                Action(
                  content = msg"site.delete",
                  href = routes.ConfirmRemoveItemController.onPageLoad(lrn, index).url,
                  visuallyHiddenText = Some(msg"site.delete.hidden".withArgs(itemDescription)),
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.addDocuments(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddDocumentsPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddDocumentsPage(index))(true)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.addDocuments(index)

          result mustBe Some(
            Row(
              key = Key(msg"addDocuments.checkYourAnswersLabel".withArgs(itemIndex.display), classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = AddDocumentsController.onPageLoad(lrn, itemIndex, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addDocuments.checkYourAnswersLabel".withArgs(index.display)))
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.traderDetailsConsignorName(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "TraderDetailsConsignorNamePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(TraderDetailsConsignorNamePage(index))(consignorName)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.traderDetailsConsignorName(index)

          result mustBe Some(
            Row(
              key = Key(msg"traderDetailsConsignorName.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$consignorName"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = TraderDetailsConsignorNameController.onPageLoad(lrn, index, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsignorName.checkYourAnswersLabel".withArgs(index.display)))
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.traderDetailsConsignorEoriNumber(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "TraderDetailsConsignorNamePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(TraderDetailsConsignorEoriNumberPage(index))(eoriNumber)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.traderDetailsConsignorEoriNumber(index)

          result mustBe Some(
            Row(
              key = Key(msg"traderDetailsConsignorEoriNumber.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$eoriNumber"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = TraderDetailsConsignorEoriNumberController.onPageLoad(lrn, index, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsignorEoriNumber.checkYourAnswersLabel".withArgs(index.display)))
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.traderDetailsConsignorEoriKnown(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "TraderDetailsConsignorEoriKnownPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(TraderDetailsConsignorEoriKnownPage(index))(false)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.traderDetailsConsignorEoriKnown(index)

          result mustBe Some(
            Row(
              key = Key(msg"traderDetailsConsignorEoriKnown.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.no"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = TraderDetailsConsignorEoriKnownController.onPageLoad(lrn, index, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsignorEoriKnown.checkYourAnswersLabel".withArgs(index.display)))
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.traderDetailsConsignorAddress(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "TraderDetailsConsignorAddressPage defined at index" - {

          "TraderDetailsConsignorNamePage undefined at index" in {

            val consigneeName = msg"traderDetailsConsignorAddress.checkYourAnswersLabel.fallback"

            val answers = emptyUserAnswers.unsafeSetVal(TraderDetailsConsignorAddressPage(index))(address)

            val helper = new AddItemsCheckYourAnswersHelper(answers)
            val result = helper.traderDetailsConsignorAddress(index)

            result mustBe Some(
              Row(
                key = Key(msg"traderDetailsConsignorAddress.checkYourAnswersLabel".withArgs(consigneeName), classes = Seq("govuk-!-width-one-half")),
                value = Value(Html(Seq(address.AddressLine1, address.AddressLine2, address.postalCode, address.country.description).mkString("<br>"))),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = TraderDetailsConsignorAddressController.onPageLoad(lrn, itemIndex, CheckMode).url,
                    visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsignorAddress.checkYourAnswersLabel".withArgs(consigneeName)))
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

            val helper = new AddItemsCheckYourAnswersHelper(answers)
            val result = helper.traderDetailsConsignorAddress(index)

            result mustBe Some(
              Row(
                key = Key(msg"traderDetailsConsignorAddress.checkYourAnswersLabel".withArgs(consigneeName), classes = Seq("govuk-!-width-one-half")),
                value = Value(Html(Seq(address.AddressLine1, address.AddressLine2, address.postalCode, address.country.description).mkString("<br>"))),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = TraderDetailsConsignorAddressController.onPageLoad(lrn, itemIndex, CheckMode).url,
                    visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsignorAddress.checkYourAnswersLabel".withArgs(consigneeName)))
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.traderDetailsConsigneeName(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "TraderDetailsConsigneeNamePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(TraderDetailsConsigneeNamePage(index))(consigneeName)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.traderDetailsConsigneeName(index)

          result mustBe Some(
            Row(
              key = Key(msg"traderDetailsConsigneeName.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$consigneeName"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = TraderDetailsConsigneeNameController.onPageLoad(lrn, index, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsigneeName.checkYourAnswersLabel".withArgs(index.display)))
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.traderDetailsConsigneeEoriNumber(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "TraderDetailsConsigneeEoriNumberPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(TraderDetailsConsigneeEoriNumberPage(index))(eoriNumber)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.traderDetailsConsigneeEoriNumber(index)

          result mustBe Some(
            Row(
              key = Key(msg"traderDetailsConsigneeEoriNumber.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$eoriNumber"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = TraderDetailsConsigneeEoriNumberController.onPageLoad(lrn, index, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsigneeEoriNumber.checkYourAnswersLabel".withArgs(index.display)))
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.traderDetailsConsigneeEoriKnown(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "TraderDetailsConsignorEoriKnownPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(TraderDetailsConsigneeEoriKnownPage(index))(true)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.traderDetailsConsigneeEoriKnown(index)

          result mustBe Some(
            Row(
              key = Key(msg"traderDetailsConsigneeEoriKnown.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = TraderDetailsConsigneeEoriKnownController.onPageLoad(lrn, index, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsigneeEoriKnown.checkYourAnswersLabel".withArgs(index.display)))
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.traderDetailsConsigneeAddress(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "TraderDetailsConsigneeAddressPage defined at index" - {

          "TraderDetailsConsigneeNamePage undefined at index" in {

            val consigneeName = msg"traderDetailsConsigneeAddress.checkYourAnswersLabel.fallback"

            val answers = emptyUserAnswers.unsafeSetVal(TraderDetailsConsigneeAddressPage(index))(address)

            val helper = new AddItemsCheckYourAnswersHelper(answers)
            val result = helper.traderDetailsConsigneeAddress(index)

            result mustBe Some(
              Row(
                key = Key(msg"traderDetailsConsigneeAddress.checkYourAnswersLabel".withArgs(consigneeName), classes = Seq("govuk-!-width-one-half")),
                value = Value(Html(Seq(address.AddressLine1, address.AddressLine2, address.postalCode, address.country.description).mkString("<br>"))),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = TraderDetailsConsigneeAddressController.onPageLoad(lrn, itemIndex, CheckMode).url,
                    visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsigneeAddress.checkYourAnswersLabel".withArgs(consigneeName)))
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

            val helper = new AddItemsCheckYourAnswersHelper(answers)
            val result = helper.traderDetailsConsigneeAddress(index)

            result mustBe Some(
              Row(
                key = Key(msg"traderDetailsConsigneeAddress.checkYourAnswersLabel".withArgs(consigneeName), classes = Seq("govuk-!-width-one-half")),
                value = Value(Html(Seq(address.AddressLine1, address.AddressLine2, address.postalCode, address.country.description).mkString("<br>"))),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = TraderDetailsConsigneeAddressController.onPageLoad(lrn, itemIndex, CheckMode).url,
                    visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsigneeAddress.checkYourAnswersLabel".withArgs(consigneeName)))
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.commodityCode(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "CommodityCodePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(CommodityCodePage(index))(commodityCode)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.commodityCode(index)

          result mustBe Some(
            Row(
              key = Key(msg"commodityCode.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$commodityCode"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = CommodityCodeController.onPageLoad(lrn, index, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"commodityCode.checkYourAnswersLabel".withArgs(index.display))),
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.totalNetMass(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "TotalNetMassPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(TotalNetMassPage(index))(mass)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.totalNetMass(index)

          result mustBe Some(
            Row(
              key = Key(msg"totalNetMass.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$mass"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = TotalNetMassController.onPageLoad(lrn, index, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"totalNetMass.checkYourAnswersLabel".withArgs(index.display))),
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.isCommodityCodeKnown(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "IsCommodityCodeKnownPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(IsCommodityCodeKnownPage(index))(true)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.isCommodityCodeKnown(index)

          result mustBe Some(
            Row(
              key = Key(msg"isCommodityCodeKnown.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = controllers.addItems.itemDetails.routes.IsCommodityCodeKnownController.onPageLoad(lrn, index, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"isCommodityCodeKnown.checkYourAnswersLabel".withArgs(index.display))),
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.addTotalNetMass(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddTotalNetMassPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddTotalNetMassPage(index))(true)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.addTotalNetMass(index)

          result mustBe Some(
            Row(
              key = Key(msg"addTotalNetMass.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = controllers.addItems.itemDetails.routes.AddTotalNetMassController.onPageLoad(lrn, index, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addTotalNetMass.checkYourAnswersLabel".withArgs(index.display))),
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.itemTotalGrossMass(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "ItemTotalGrossMassPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(ItemTotalGrossMassPage(index))(mass)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.itemTotalGrossMass(index)

          result mustBe Some(
            Row(
              key = Key(msg"itemTotalGrossMass.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$mass"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = controllers.addItems.itemDetails.routes.ItemTotalGrossMassController.onPageLoad(lrn, index, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"itemTotalGrossMass.checkYourAnswersLabel".withArgs(index.display))),
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.itemDescription(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "ItemDescriptionPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(ItemDescriptionPage(index))(description)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.itemDescription(index)

          result mustBe Some(
            Row(
              key = Key(msg"itemDescription.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$description"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = controllers.addItems.itemDetails.routes.ItemDescriptionController.onPageLoad(lrn, index, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"itemDescription.checkYourAnswersLabel".withArgs(index.display))),
                  attributes = Map("id" -> "change-item-description")
                )
              )
            )
          )
        }
      }
    }

    "previousReferenceType" - {

      val referenceCode: String        = "REFERENCE CODE"
      val referenceDescription: String = "REFERENCE DESCRIPTION"

      "return None" - {
        "ReferenceTypePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.previousReferenceRow(index, referenceIndex, PreviousReferencesDocumentTypeList(Nil))
          result mustBe None
        }

        "previous reference type not found" in {

          val answers = emptyUserAnswers.unsafeSetVal(ReferenceTypePage(index, referenceIndex))(referenceCode)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.previousReferenceRow(index, referenceIndex, PreviousReferencesDocumentTypeList(Nil))

          result mustBe None

        }
      }

      "return Some(row)" - {
        "ReferenceTypePage defined at index and previous reference type found" - {

          "previous reference has no description" in {

            val answers = emptyUserAnswers.unsafeSetVal(ReferenceTypePage(index, referenceIndex))(referenceCode)

            val helper = new AddItemsCheckYourAnswersHelper(answers)
            val result = helper.previousReferenceRow(
              index,
              referenceIndex,
              PreviousReferencesDocumentTypeList(Seq(PreviousReferencesDocumentType(referenceCode, None)))
            )

            val key = s"($referenceCode) "

            result mustBe Some(
              Row(
                key = Key(lit"$key"),
                value = Value(lit""),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = ReferenceTypeController.onPageLoad(lrn, index, referenceIndex, CheckMode).url,
                    visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(key)),
                    attributes = Map("id" -> s"change-item-${index.display}")
                  )
                )
              )
            )
          }

          "previous reference has description" in {

            val answers = emptyUserAnswers.unsafeSetVal(ReferenceTypePage(index, referenceIndex))(referenceCode)

            val helper = new AddItemsCheckYourAnswersHelper(answers)
            val result = helper.previousReferenceRow(
              index,
              referenceIndex,
              PreviousReferencesDocumentTypeList(Seq(PreviousReferencesDocumentType(referenceCode, Some(referenceDescription))))
            )

            val key = s"($referenceCode) $referenceDescription"

            result mustBe Some(
              Row(
                key = Key(lit"$key"),
                value = Value(lit""),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = ReferenceTypeController.onPageLoad(lrn, index, referenceIndex, CheckMode).url,
                    visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(key)),
                    attributes = Map("id" -> s"change-item-${index.display}")
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.previousAdministrativeReferenceRow(index, referenceIndex, PreviousReferencesDocumentTypeList(Nil))
          result mustBe None
        }

        "previous reference type not found" in {

          val answers = emptyUserAnswers.unsafeSetVal(ReferenceTypePage(index, referenceIndex))(referenceCode)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.previousAdministrativeReferenceRow(index, referenceIndex, PreviousReferencesDocumentTypeList(Nil))

          result mustBe None

        }
      }

      "return Some(row)" - {
        "ReferenceTypePage defined at index and previous reference type found" - {

          "previous reference has no description" in {

            val answers = emptyUserAnswers.unsafeSetVal(ReferenceTypePage(index, referenceIndex))(referenceCode)

            val helper = new AddItemsCheckYourAnswersHelper(answers)
            val result = helper.previousAdministrativeReferenceRow(
              index,
              referenceIndex,
              PreviousReferencesDocumentTypeList(Seq(PreviousReferencesDocumentType(referenceCode, None)))
            )

            result mustBe Some(
              Row(
                key = Key(lit"($referenceCode) "),
                value = Value(lit""),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = ReferenceTypeController.onPageLoad(lrn, index, referenceIndex, CheckMode).url,
                    visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(s"($referenceCode) ")),
                    attributes = Map("id" -> s"change-reference-document-type-${index.display}")
                  ),
                  Action(
                    content = msg"site.delete",
                    href = ConfirmRemovePreviousAdministrativeReferenceController.onPageLoad(lrn, index, referenceIndex, CheckMode).url,
                    visuallyHiddenText = Some(msg"site.delete.hidden".withArgs(s"($referenceCode) ")),
                    attributes = Map("id" -> s"remove-reference-document-type-${index.display}")
                  )
                )
              )
            )
          }

          "previous reference has description" in {

            val answers = emptyUserAnswers.unsafeSetVal(ReferenceTypePage(index, referenceIndex))(referenceCode)

            val helper = new AddItemsCheckYourAnswersHelper(answers)
            val result = helper.previousAdministrativeReferenceRow(
              index,
              referenceIndex,
              PreviousReferencesDocumentTypeList(Seq(PreviousReferencesDocumentType(referenceCode, Some(referenceDescription))))
            )

            result mustBe Some(
              Row(
                key = Key(lit"($referenceCode) $referenceDescription"),
                value = Value(lit""),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = ReferenceTypeController.onPageLoad(lrn, index, referenceIndex, CheckMode).url,
                    visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(s"($referenceCode) $referenceDescription")),
                    attributes = Map("id" -> s"change-reference-document-type-${index.display}")
                  ),
                  Action(
                    content = msg"site.delete",
                    href = ConfirmRemovePreviousAdministrativeReferenceController.onPageLoad(lrn, index, referenceIndex, CheckMode).url,
                    visuallyHiddenText = Some(msg"site.delete.hidden".withArgs(s"($referenceCode) $referenceDescription")),
                    attributes = Map("id" -> s"remove-reference-document-type-${index.display}")
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.addAdministrativeReference(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddAdministrativeReferencePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddAdministrativeReferencePage(index))(true)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.addAdministrativeReference(index)

          result mustBe Some(
            Row(
              key = Key(msg"addAdministrativeReference.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = AddAdministrativeReferenceController.onPageLoad(lrn, index, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addAdministrativeReference.checkYourAnswersLabel"))
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.packageType(itemIndex, packageIndex)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "PackageTypePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(PackageTypePage(itemIndex, packageIndex))(packageType)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.packageType(itemIndex, packageIndex)

          result mustBe Some(
            Row(
              key = Key(msg"packageType.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$packageType"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = PackageTypeController.onPageLoad(lrn, itemIndex, packageIndex, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"packageType.checkYourAnswersLabel")),
                  attributes = Map("id" -> s"change-package-${packageIndex.display}")
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.numberOfPackages(itemIndex, packageIndex)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "HowManyPackagesPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(HowManyPackagesPage(itemIndex, packageIndex))(numberOfPackages)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.numberOfPackages(itemIndex, packageIndex)

          result mustBe Some(
            Row(
              key = Key(msg"declareNumberOfPackages.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$numberOfPackages"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = HowManyPackagesController.onPageLoad(lrn, itemIndex, packageIndex, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"declareNumberOfPackages.checkYourAnswersLabel"))
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.totalPieces(itemIndex, packageIndex)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "TotalPiecesPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(TotalPiecesPage(itemIndex, packageIndex))(totalPieces)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.totalPieces(itemIndex, packageIndex)

          result mustBe Some(
            Row(
              key = Key(msg"totalPieces.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$totalPieces"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = TotalPiecesController.onPageLoad(lrn, itemIndex, packageIndex, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"totalPieces.checkYourAnswersLabel"))
                )
              )
            )
          )
        }
      }
    }

    "commercialReferenceNumber" - {

      val referenceNumber: String = "REFERENCE NUMBER"

      "return None" - {
        "CommercialReferenceNumberPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.commercialReferenceNumber(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "CommercialReferenceNumberPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(CommercialReferenceNumberPage(index))(referenceNumber)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.commercialReferenceNumber(index)

          result mustBe Some(
            Row(
              key = Key(msg"commercialReferenceNumber.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$referenceNumber"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = CommercialReferenceNumberController.onPageLoad(lrn, itemIndex, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"commercialReferenceNumber.checkYourAnswersLabel"))
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.addDangerousGoodsCode(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddDangerousGoodsCodePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddDangerousGoodsCodePage(index))(true)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.addDangerousGoodsCode(index)

          result mustBe Some(
            Row(
              key = Key(msg"addDangerousGoodsCode.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = AddDangerousGoodsCodeController.onPageLoad(lrn, itemIndex, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addDangerousGoodsCode.checkYourAnswersLabel"))
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.dangerousGoodsCode(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "DangerousGoodsCodePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(DangerousGoodsCodePage(index))(code)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.dangerousGoodsCode(index)

          result mustBe Some(
            Row(
              key = Key(msg"dangerousGoodsCode.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$code"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = DangerousGoodsCodeController.onPageLoad(lrn, itemIndex, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"dangerousGoodsCode.checkYourAnswersLabel"))
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.addSecurityConsignorsEori(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddDangerousGoodsCodePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddSecurityConsignorsEoriPage(index))(true)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.addSecurityConsignorsEori(index)

          result mustBe Some(
            Row(
              key = Key(msg"addSecurityConsignorsEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = AddSecurityConsignorsEoriController.onPageLoad(lrn, index, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addSecurityConsignorsEori.checkYourAnswersLabel"))
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.addSecurityConsigneesEori(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddSecurityConsigneesEoriPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddSecurityConsigneesEoriPage(index))(true)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.addSecurityConsigneesEori(index)

          result mustBe Some(
            Row(
              key = Key(msg"addSecurityConsigneesEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = AddSecurityConsigneesEoriController.onPageLoad(lrn, index, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addSecurityConsigneesEori.checkYourAnswersLabel"))
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.securityConsigneeName(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "SecurityConsigneeNamePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(SecurityConsigneeNamePage(index))(consigneeName)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.securityConsigneeName(index)

          result mustBe Some(
            Row(
              key = Key(msg"securityConsigneeName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$consigneeName"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = SecurityConsigneeNameController.onPageLoad(lrn, index, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"securityConsigneeName.checkYourAnswersLabel"))
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.securityConsignorName(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "SecurityConsignorNamePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(SecurityConsignorNamePage(index))(consignorName)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.securityConsignorName(index)

          result mustBe Some(
            Row(
              key = Key(msg"securityConsignorName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$consignorName"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = SecurityConsignorNameController.onPageLoad(lrn, index, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"securityConsignorName.checkYourAnswersLabel"))
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.securityConsigneeAddress(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "SecurityConsigneeAddressPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(SecurityConsigneeAddressPage(index))(address)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.securityConsigneeAddress(index)

          result mustBe Some(
            Row(
              key = Key(msg"securityConsigneeAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(Html(Seq(address.AddressLine1, address.AddressLine2, address.postalCode, address.country.description).mkString("<br>"))),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = SecurityConsigneeAddressController.onPageLoad(lrn, index, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"securityConsigneeAddress.checkYourAnswersLabel"))
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.securityConsignorAddress(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "SecurityConsignorAddressPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(SecurityConsignorAddressPage(index))(address)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.securityConsignorAddress(index)

          result mustBe Some(
            Row(
              key = Key(msg"securityConsignorAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(Html(Seq(address.AddressLine1, address.AddressLine2, address.postalCode, address.country.description).mkString("<br>"))),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = SecurityConsignorAddressController.onPageLoad(lrn, index, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"securityConsignorAddress.checkYourAnswersLabel"))
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.securityConsigneeEori(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "SecurityConsigneeEoriPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(SecurityConsigneeEoriPage(index))(eoriNumber)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.securityConsigneeEori(index)

          result mustBe Some(
            Row(
              key = Key(msg"securityConsigneeEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$eoriNumber"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = SecurityConsigneeEoriController.onPageLoad(lrn, index, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"securityConsigneeEori.checkYourAnswersLabel"))
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

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.securityConsignorEori(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "SecurityConsignorEoriPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(SecurityConsignorEoriPage(index))(eoriNumber)

          val helper = new AddItemsCheckYourAnswersHelper(answers)
          val result = helper.securityConsignorEori(index)

          result mustBe Some(
            Row(
              key = Key(msg"securityConsignorEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$eoriNumber"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = SecurityConsignorEoriController.onPageLoad(lrn, index, CheckMode).url,
                  visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"securityConsignorEori.checkYourAnswersLabel"))
                )
              )
            )
          )
        }
      }
    }

  }
}
