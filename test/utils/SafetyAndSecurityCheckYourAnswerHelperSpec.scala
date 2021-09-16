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
import controllers.safetyAndSecurity.routes
import models.reference.{CircumstanceIndicator, Country, CountryCode, MethodOfPayment}
import models.{CheckMode, CircumstanceIndicatorList, CommonAddress, CountryList}
import pages.safetyAndSecurity._
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels.{Html, MessageInterpolators}

class SafetyAndSecurityCheckYourAnswerHelperSpec extends SpecBase with UserAnswersSpecHelper {

  "SafetyAndSecurityCheckYourAnswerHelper" - {

    "addCarrierEori" - {

      "return None" - {
        "AddCarrierEoriPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.addCarrierEori
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddCarrierEoriPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddCarrierEoriPage)(true)

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.addCarrierEori

          result mustBe Some(
            Row(
              key = Key(msg"addCarrierEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.AddCarrierEoriController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"addCarrierEori.checkYourAnswersLabel.visuallyHidden")
                )
              )
            )
          )
        }
      }
    }

    "addCarrier" - {

      "return None" - {
        "AddCarrierPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.addCarrier
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddCarrierPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddCarrierPage)(true)

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.addCarrier

          result mustBe Some(
            Row(
              key = Key(msg"addCarrier.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.AddCarrierController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"addCarrier.checkYourAnswersLabel.visuallyHidden")
                )
              )
            )
          )
        }
      }
    }

    "addCircumstanceIndicator" - {

      "return None" - {
        "AddCircumstanceIndicatorPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.addCircumstanceIndicator
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddCircumstanceIndicatorPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddCircumstanceIndicatorPage)(true)

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.addCircumstanceIndicator

          result mustBe Some(
            Row(
              key = Key(msg"addCircumstanceIndicator.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.AddCircumstanceIndicatorController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"addCircumstanceIndicator.checkYourAnswersLabel.visuallyHidden")
                )
              )
            )
          )
        }
      }
    }

    "addCommercialReferenceNumberAllItems" - {

      "return None" - {
        "AddCommercialReferenceNumberAllItemsPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.addCommercialReferenceNumberAllItems
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddCommercialReferenceNumberAllItemsPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddCommercialReferenceNumberAllItemsPage)(true)

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.addCommercialReferenceNumberAllItems

          result mustBe Some(
            Row(
              key = Key(msg"addCommercialReferenceNumberAllItems.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.AddCommercialReferenceNumberAllItemsController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"addCommercialReferenceNumberAllItems.checkYourAnswersLabel.visuallyHidden")
                )
              )
            )
          )
        }
      }
    }

    "addCommercialReferenceNumber" - {

      "return None" - {
        "AddCommercialReferenceNumberPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.addCommercialReferenceNumber
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddCommercialReferenceNumberPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddCommercialReferenceNumberPage)(true)

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.addCommercialReferenceNumber

          result mustBe Some(
            Row(
              key = Key(msg"addCommercialReferenceNumber.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.AddCommercialReferenceNumberController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"addCommercialReferenceNumber.checkYourAnswersLabel.visuallyHidden")
                )
              )
            )
          )
        }
      }
    }

    "addConveyanceReferenceNumber" - {

      "return None" - {
        "AddConveyanceReferenceNumberPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.addConveyanceReferenceNumber
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddConveyanceReferenceNumberPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddConveyanceReferenceNumberPage)(true)

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.addConveyanceReferenceNumber

          result mustBe Some(
            Row(
              key = Key(msg"addConveyancerReferenceNumber.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.AddConveyanceReferenceNumberController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"addConveyancerReferenceNumber.checkYourAnswersLabel.visuallyHidden")
                )
              )
            )
          )
        }
      }
    }

    "addPlaceOfUnloadingCode" - {

      "return None" - {
        "AddPlaceOfUnloadingCodePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.addPlaceOfUnloadingCode
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddPlaceOfUnloadingCodePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddPlaceOfUnloadingCodePage)(true)

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.addPlaceOfUnloadingCode

          result mustBe Some(
            Row(
              key = Key(msg"addPlaceOfUnloadingCode.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.AddPlaceOfUnloadingCodeController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"addPlaceOfUnloadingCode.checkYourAnswersLabel.visuallyHidden")
                )
              )
            )
          )
        }
      }
    }

    "addSafetyAndSecurityConsigneeEori" - {

      "return None" - {
        "AddSafetyAndSecurityConsigneeEoriPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.addSafetyAndSecurityConsigneeEori
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddSafetyAndSecurityConsigneeEoriPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddSafetyAndSecurityConsigneeEoriPage)(true)

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.addSafetyAndSecurityConsigneeEori

          result mustBe Some(
            Row(
              key = Key(msg"addSafetyAndSecurityConsigneeEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.AddSafetyAndSecurityConsigneeEoriController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"addSafetyAndSecurityConsigneeEori.checkYourAnswersLabel.visuallyHidden")
                )
              )
            )
          )
        }
      }
    }

    "addSafetyAndSecurityConsignee" - {

      "return None" - {
        "AddSafetyAndSecurityConsigneePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.addSafetyAndSecurityConsignee
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddSafetyAndSecurityConsigneePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddSafetyAndSecurityConsigneePage)(true)

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.addSafetyAndSecurityConsignee

          result mustBe Some(
            Row(
              key = Key(msg"addSafetyAndSecurityConsignee.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.AddSafetyAndSecurityConsigneeController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"addSafetyAndSecurityConsignor.checkYourAnswersLabel.visuallyHidden ")
                )
              )
            )
          )
        }
      }
    }

    "addSafetyAndSecurityConsignorEori" - {

      "return None" - {
        "AddSafetyAndSecurityConsignorEoriPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.addSafetyAndSecurityConsignorEori
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddSafetyAndSecurityConsignorEoriPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddSafetyAndSecurityConsignorEoriPage)(true)

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.addSafetyAndSecurityConsignorEori

          result mustBe Some(
            Row(
              key = Key(msg"addSafetyAndSecurityConsignorEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.AddSafetyAndSecurityConsignorEoriController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"addSafetyAndSecurityConsignorEori.checkYourAnswersLabel.visuallyHidden")
                )
              )
            )
          )
        }
      }
    }

    "addSafetyAndSecurityConsignor" - {

      "return None" - {
        "AddSafetyAndSecurityConsignorPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.addSafetyAndSecurityConsignor
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddSafetyAndSecurityConsignorPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddSafetyAndSecurityConsignorPage)(true)

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.addSafetyAndSecurityConsignor

          result mustBe Some(
            Row(
              key = Key(msg"addSafetyAndSecurityConsignor.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.AddSafetyAndSecurityConsignorController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"addSafetyAndSecurityConsignorEori.checkYourAnswersLabel.visuallyHidden")
                )
              )
            )
          )
        }
      }
    }

    "addTransportChargesPaymentMethod" - {

      "return None" - {
        "AddTransportChargesPaymentMethodPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.addTransportChargesPaymentMethod
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddTransportChargesPaymentMethodPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddTransportChargesPaymentMethodPage)(true)

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.addTransportChargesPaymentMethod

          result mustBe Some(
            Row(
              key = Key(msg"addTransportChargesPaymentMethod.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.AddTransportChargesPaymentMethodController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"addTransportChargesPaymentMethod.checkYourAnswersLabel.visuallyHidden")
                )
              )
            )
          )
        }
      }
    }

    "carrierAddress" - {

      val address: CommonAddress = CommonAddress("LINE 1", "LINE 2", "POSTCODE", Country(CountryCode("CODE"), "DESCRIPTION"))

      "return None" - {
        "CarrierAddressPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.carrierAddress
          result mustBe None
        }
      }

      "return Some(row)" - {
        "CarrierAddressPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(CarrierAddressPage)(address)

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.carrierAddress

          result mustBe Some(
            Row(
              key = Key(msg"carrierAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(Html(Seq(address.AddressLine1, address.AddressLine2, address.postalCode, address.country.description).mkString("<br>"))),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.CarrierAddressController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"carrierAddress.checkYourAnswersLabel.visuallyHidden")
                )
              )
            )
          )
        }
      }
    }

    "carrierEori" - {

      val eori: String = "EORI"

      "return None" - {
        "CarrierEoriPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.carrierEori
          result mustBe None
        }
      }

      "return Some(row)" - {
        "CarrierEoriPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(CarrierEoriPage)(eori)

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.carrierEori

          result mustBe Some(
            Row(
              key = Key(msg"carrierEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$eori"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.CarrierEoriController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"carrierEori.checkYourAnswersLabel.visuallyHidden")
                )
              )
            )
          )
        }
      }
    }

    "carrierName" - {

      val name: String = "NAME"

      "return None" - {
        "CarrierNamePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.carrierName
          result mustBe None
        }
      }

      "return Some(row)" - {
        "CarrierNamePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(CarrierNamePage)(name)

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.carrierName

          result mustBe Some(
            Row(
              key = Key(msg"carrierName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$name"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.CarrierNameController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"carrierName.checkYourAnswersLabel.visuallyHidden")
                )
              )
            )
          )
        }
      }
    }

    "circumstanceIndicator" - {

      val indicatorCode: String            = "INDICATOR CODE"
      val indicator: CircumstanceIndicator = CircumstanceIndicator(indicatorCode, "DESCRIPTION")

      "return None" - {
        "CircumstanceIndicatorPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.circumstanceIndicator(CircumstanceIndicatorList(Nil))
          result mustBe None
        }
      }

      "return Some(row)" - {
        "CircumstanceIndicatorPage defined at index" - {

          "circumstance indicator code not found" in {

            val answers = emptyUserAnswers.unsafeSetVal(CircumstanceIndicatorPage)(indicatorCode)

            val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
            val result = helper.circumstanceIndicator(CircumstanceIndicatorList(Nil))

            result mustBe Some(
              Row(
                key = Key(msg"circumstanceIndicator.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
                value = Value(lit"$indicatorCode"),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = routes.CircumstanceIndicatorController.onPageLoad(lrn, CheckMode).url,
                    visuallyHiddenText = Some(msg"circumstanceIndicator.checkYourAnswersLabel.visuallyHidden")
                  )
                )
              )
            )
          }

          "circumstance indicator code found" in {

            val answers = emptyUserAnswers.unsafeSetVal(CircumstanceIndicatorPage)(indicatorCode)

            val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
            val result = helper.circumstanceIndicator(CircumstanceIndicatorList(Seq(indicator)))

            result mustBe Some(
              Row(
                key = Key(msg"circumstanceIndicator.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
                value = Value(lit"(${indicator.code}) ${indicator.description}"),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = routes.CircumstanceIndicatorController.onPageLoad(lrn, CheckMode).url,
                    visuallyHiddenText = Some(msg"circumstanceIndicator.checkYourAnswersLabel.visuallyHidden")
                  )
                )
              )
            )
          }
        }
      }
    }

    "commercialReferenceNumberAllItems" - {

      val referenceNumber: String = "REFERENCE NUMBER"

      "return None" - {
        "CommercialReferenceNumberAllItemsPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.commercialReferenceNumberAllItems
          result mustBe None
        }
      }

      "return Some(row)" - {
        "CommercialReferenceNumberAllItemsPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(CommercialReferenceNumberAllItemsPage)(referenceNumber)

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.commercialReferenceNumberAllItems

          result mustBe Some(
            Row(
              key = Key(msg"commercialReferenceNumberAllItems.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$referenceNumber"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.CommercialReferenceNumberAllItemsController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"commercialReferenceNumberAllItems.checkYourAnswersLabel.visuallyHidden")
                )
              )
            )
          )
        }
      }
    }

    "conveyanceReferenceNumber" - {

      val referenceNumber: String = "REFERENCE NUMBER"

      "return None" - {
        "ConveyanceReferenceNumberPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.conveyanceReferenceNumber
          result mustBe None
        }
      }

      "return Some(row)" - {
        "ConveyanceReferenceNumberPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(ConveyanceReferenceNumberPage)(referenceNumber)

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.conveyanceReferenceNumber

          result mustBe Some(
            Row(
              key = Key(msg"conveyanceReferenceNumber.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$referenceNumber"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.ConveyanceReferenceNumberController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"conveyanceReferenceNumber.checkYourAnswersLabel.visuallyHidden")
                )
              )
            )
          )
        }
      }
    }

    "placeOfUnloadingCode" - {

      val locationCode: String = "CODE"

      "return None" - {
        "PlaceOfUnloadingCodePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.placeOfUnloadingCode
          result mustBe None
        }
      }

      "return Some(row)" - {
        "PlaceOfUnloadingCodePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(PlaceOfUnloadingCodePage)(locationCode)

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.placeOfUnloadingCode

          result mustBe Some(
            Row(
              key = Key(msg"placeOfUnloadingCode.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$locationCode"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.PlaceOfUnloadingCodeController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"placeOfUnloadingCode.checkYourAnswersLabel.visuallyHidden")
                )
              )
            )
          )
        }
      }
    }

    "safetyAndSecurityConsigneeAddress" - {

      val address: CommonAddress = CommonAddress("LINE 1", "LINE 2", "POSTCODE", Country(CountryCode("CODE"), "DESCRIPTION"))

      "return None" - {
        "SafetyAndSecurityConsigneeAddressPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.safetyAndSecurityConsigneeAddress
          result mustBe None
        }
      }

      "return Some(row)" - {
        "SafetyAndSecurityConsigneeAddressPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(SafetyAndSecurityConsigneeAddressPage)(address)

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.safetyAndSecurityConsigneeAddress

          result mustBe Some(
            Row(
              key = Key(msg"safetyAndSecurityConsigneeAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(Html(Seq(address.AddressLine1, address.AddressLine2, address.postalCode, address.country.description).mkString("<br>"))),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.SafetyAndSecurityConsigneeAddressController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"safetyAndSecurityConsigneeAddress.checkYourAnswersLabel.visuallyHidden")
                )
              )
            )
          )
        }
      }
    }

    "safetyAndSecurityConsigneeEori" - {

      val eori: String = "EORI"

      "return None" - {
        "SafetyAndSecurityConsigneeEoriPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.safetyAndSecurityConsigneeEori
          result mustBe None
        }
      }

      "return Some(row)" - {
        "SafetyAndSecurityConsigneeEoriPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(SafetyAndSecurityConsigneeEoriPage)(eori)

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.safetyAndSecurityConsigneeEori

          result mustBe Some(
            Row(
              key = Key(msg"safetyAndSecurityConsigneeEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$eori"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.SafetyAndSecurityConsigneeEoriController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"safetyAndSecurityConsigneeEori.checkYourAnswersLabel.visuallyHidden")
                )
              )
            )
          )
        }
      }
    }

    "safetyAndSecurityConsigneeName" - {

      val name: String = "NAME"

      "return None" - {
        "SafetyAndSecurityConsigneeNamePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.safetyAndSecurityConsigneeName
          result mustBe None
        }
      }

      "return Some(row)" - {
        "SafetyAndSecurityConsigneeNamePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(SafetyAndSecurityConsigneeNamePage)(name)

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.safetyAndSecurityConsigneeName

          result mustBe Some(
            Row(
              key = Key(msg"safetyAndSecurityConsigneeName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$name"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.SafetyAndSecurityConsigneeNameController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"safetyAndSecurityConsigneeName.checkYourAnswersLabel.visuallyHidden")
                )
              )
            )
          )
        }
      }
    }

    "safetyAndSecurityConsignorAddress" - {

      val address: CommonAddress = CommonAddress("LINE 1", "LINE 2", "POSTCODE", Country(CountryCode("CODE"), "DESCRIPTION"))

      "return None" - {
        "SafetyAndSecurityConsignorAddressPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.safetyAndSecurityConsignorAddress
          result mustBe None
        }
      }

      "return Some(row)" - {
        "SafetyAndSecurityConsignorAddressPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(SafetyAndSecurityConsignorAddressPage)(address)

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.safetyAndSecurityConsignorAddress

          result mustBe Some(
            Row(
              key = Key(msg"safetyAndSecurityConsignorAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(Html(Seq(address.AddressLine1, address.AddressLine2, address.postalCode, address.country.description).mkString("<br>"))),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.SafetyAndSecurityConsignorAddressController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"safetyAndSecurityConsignorAddress.checkYourAnswersLabel.visuallyHidden")
                )
              )
            )
          )
        }
      }
    }

    "safetyAndSecurityConsignorEori" - {

      val name: String = "EORI"

      "return None" - {
        "SafetyAndSecurityConsignorEoriPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.safetyAndSecurityConsignorEori
          result mustBe None
        }
      }

      "return Some(row)" - {
        "SafetyAndSecurityConsignorEoriPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(SafetyAndSecurityConsignorEoriPage)(name)

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.safetyAndSecurityConsignorEori

          result mustBe Some(
            Row(
              key = Key(msg"safetyAndSecurityConsignorEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$name"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.SafetyAndSecurityConsignorEoriController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"safetyAndSecurityConsignorEori.checkYourAnswersLabel.visuallyHidden")
                )
              )
            )
          )
        }
      }
    }

    "safetyAndSecurityConsignorName" - {

      val name: String = "NAME"

      "return None" - {
        "SafetyAndSecurityConsignorNamePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.safetyAndSecurityConsignorName
          result mustBe None
        }
      }

      "return Some(row)" - {
        "SafetyAndSecurityConsignorNamePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(SafetyAndSecurityConsignorNamePage)(name)

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.safetyAndSecurityConsignorName

          result mustBe Some(
            Row(
              key = Key(msg"safetyAndSecurityConsignorName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$name"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.SafetyAndSecurityConsignorNameController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"safetyAndSecurityConsignorName.checkYourAnswersLabel.visuallyHidden")
                )
              )
            )
          )
        }
      }
    }

    "transportChargesPaymentMethod" - {

      val paymentMethod: MethodOfPayment = MethodOfPayment("CODE", "DESCRIPTION")

      "return None" - {
        "TransportChargesPaymentMethodPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.transportChargesPaymentMethod()
          result mustBe None
        }
      }

      "return Some(row)" - {
        "TransportChargesPaymentMethodPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(TransportChargesPaymentMethodPage)(paymentMethod)

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.transportChargesPaymentMethod()

          result mustBe Some(
            Row(
              key = Key(msg"transportChargesPaymentMethod.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$paymentMethod"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.TransportChargesPaymentMethodController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(msg"transportChargesPaymentMethod.checkYourAnswersLabel.visuallyHidden")
                )
              )
            )
          )
        }
      }
    }

    "countryRow" - {

      val countryCode: CountryCode = CountryCode("CODE")
      val country: Country         = Country(countryCode, "DESCRIPTION")

      "return None" - {
        "CountryOfRoutingPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.countryRow(index, CountryList(Nil))
          result mustBe None
        }
      }

      "return Some(row)" - {
        "CountryOfRoutingPage defined at index" - {

          "country code not found" in {

            val answers = emptyUserAnswers.unsafeSetVal(CountryOfRoutingPage(index))(countryCode)

            val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
            val result = helper.countryRow(index, CountryList(Nil))

            val key = countryCode.code

            result mustBe Some(
              Row(
                key = Key(lit"$key"),
                value = Value(lit""),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = routes.CountryOfRoutingController.onPageLoad(lrn, index, CheckMode).url,
                    visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(key)),
                    attributes = Map("id" -> s"change-country-${index.display}")
                  ),
                  Action(
                    content = msg"site.delete",
                    href = routes.ConfirmRemoveCountryController.onPageLoad(lrn, index, CheckMode).url,
                    visuallyHiddenText = Some(msg"site.delete.hidden".withArgs(key)),
                    attributes = Map("id" -> s"remove-country-${index.display}")
                  )
                )
              )
            )
          }

          "country code found" in {

            val answers = emptyUserAnswers.unsafeSetVal(CountryOfRoutingPage(index))(countryCode)

            val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
            val result = helper.countryRow(index, CountryList(Seq(country)))

            val key = country.description

            result mustBe Some(
              Row(
                key = Key(lit"$key"),
                value = Value(lit""),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = routes.CountryOfRoutingController.onPageLoad(lrn, index, CheckMode).url,
                    visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(key)),
                    attributes = Map("id" -> s"change-country-${index.display}")
                  ),
                  Action(
                    content = msg"site.delete",
                    href = routes.ConfirmRemoveCountryController.onPageLoad(lrn, index, CheckMode).url,
                    visuallyHiddenText = Some(msg"site.delete.hidden".withArgs(key)),
                    attributes = Map("id" -> s"remove-country-${index.display}")
                  )
                )
              )
            )
          }
        }
      }
    }

    "countryOfRoutingRow" - {

      val countryCode: CountryCode = CountryCode("CODE")
      val country: Country         = Country(countryCode, "DESCRIPTION")

      "return None" - {
        "CountryOfRoutingPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
          val result = helper.countryOfRoutingRow(index, CountryList(Nil))
          result mustBe None
        }
      }

      "return Some(row)" - {
        "CountryOfRoutingPage defined at index" - {

          "country code not found" in {

            val answers = emptyUserAnswers.unsafeSetVal(CountryOfRoutingPage(index))(countryCode)

            val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
            val result = helper.countryOfRoutingRow(index, CountryList(Nil))

            val key = countryCode.code

            result mustBe Some(
              Row(
                key = Key(lit"$key"),
                value = Value(lit""),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = routes.AddAnotherCountryOfRoutingController.onPageLoad(lrn, CheckMode).url,
                    visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(key)),
                    attributes = Map("id" -> s"change-country-${index.display}")
                  )
                )
              )
            )
          }

          "country code found" in {

            val answers = emptyUserAnswers.unsafeSetVal(CountryOfRoutingPage(index))(countryCode)

            val helper = new SafetyAndSecurityCheckYourAnswerHelper(answers)
            val result = helper.countryOfRoutingRow(index, CountryList(Seq(country)))

            val key = country.description

            result mustBe Some(
              Row(
                key = Key(lit"$key"),
                value = Value(lit""),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = routes.AddAnotherCountryOfRoutingController.onPageLoad(lrn, CheckMode).url,
                    visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(key)),
                    attributes = Map("id" -> s"change-country-${index.display}")
                  )
                )
              )
            )
          }
        }
      }
    }

  }
}
