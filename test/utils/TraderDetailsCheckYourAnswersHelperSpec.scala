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
import controllers.traderDetails.routes
import models.reference.{Country, CountryCode}
import models.{CheckMode, CommonAddress}
import pages.traderDetails._
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels.{Html, MessageInterpolators}

class TraderDetailsCheckYourAnswersHelperSpec extends SpecBase with UserAnswersSpecHelper {

  "TraderDetailsCheckYourAnswersHelper" - {

    "principalTirHolderIdPage" - {

      val id: String = "ID"

      "return None" - {
        "PrincipalTirHolderIdPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new TraderDetailsCheckYourAnswersHelper(answers)
          val result = helper.principalTirHolderIdPage
          result mustBe None
        }
      }

      "return Some(row)" - {
        "PrincipalTirHolderIdPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(PrincipalTirHolderIdPage)(id)

          val helper = new TraderDetailsCheckYourAnswersHelper(answers)
          val result = helper.principalTirHolderIdPage

          val label = msg"principalTirHolderId.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$id"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.PrincipalTirHolderIdController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "consigneeAddress" - {

      val address: CommonAddress = CommonAddress("LINE 1", "LINE 2", "POSTCODE", Country(CountryCode("CODE"), "DESCRIPTION"))

      "return None" - {
        "ConsigneeAddressPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new TraderDetailsCheckYourAnswersHelper(answers)
          val result = helper.consigneeAddress
          result mustBe None
        }
      }

      "return Some(row)" - {
        "ConsigneeAddressPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(ConsigneeAddressPage)(address)

          val helper = new TraderDetailsCheckYourAnswersHelper(answers)
          val result = helper.consigneeAddress

          val label = msg"consigneeAddress.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(Html(Seq(address.AddressLine1, address.AddressLine2, address.postalCode, address.country.description).mkString("<br>"))),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.ConsigneeAddressController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "principalAddress" - {

      val address: CommonAddress = CommonAddress("LINE 1", "LINE 2", "POSTCODE", Country(CountryCode("CODE"), "DESCRIPTION"))

      "return None" - {
        "PrincipalAddressPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new TraderDetailsCheckYourAnswersHelper(answers)
          val result = helper.principalAddress
          result mustBe None
        }
      }

      "return Some(row)" - {
        "PrincipalAddressPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(PrincipalAddressPage)(address)

          val helper = new TraderDetailsCheckYourAnswersHelper(answers)
          val result = helper.principalAddress

          val label = msg"principalAddress.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(Html(Seq(address.AddressLine1, address.AddressLine2, address.postalCode, address.country.description).mkString("<br>"))),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.PrincipalAddressController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "consigneeName" - {

      val consigneeName: String = "CONSIGNEE NAME"

      "return None" - {
        "ConsigneeNamePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new TraderDetailsCheckYourAnswersHelper(answers)
          val result = helper.consigneeName
          result mustBe None
        }
      }

      "return Some(row)" - {
        "ConsigneeNamePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(ConsigneeNamePage)(consigneeName)

          val helper = new TraderDetailsCheckYourAnswersHelper(answers)
          val result = helper.consigneeName

          val label = msg"consigneeName.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$consigneeName"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.ConsigneeNameController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "whatIsConsigneeEori" - {

      val consigneeEori: String = "CONSIGNEE EORI"

      "return None" - {
        "WhatIsConsigneeEoriPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new TraderDetailsCheckYourAnswersHelper(answers)
          val result = helper.whatIsConsigneeEori
          result mustBe None
        }
      }

      "return Some(row)" - {
        "WhatIsConsigneeEoriPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(WhatIsConsigneeEoriPage)(consigneeEori)

          val helper = new TraderDetailsCheckYourAnswersHelper(answers)
          val result = helper.whatIsConsigneeEori

          val label = msg"whatIsConsigneeEori.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$consigneeEori"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.WhatIsConsigneeEoriController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "isConsigneeEoriKnown" - {

      "return None" - {
        "IsConsigneeEoriKnownPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new TraderDetailsCheckYourAnswersHelper(answers)
          val result = helper.isConsigneeEoriKnown
          result mustBe None
        }
      }

      "return Some(row)" - {
        "IsConsigneeEoriKnownPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(IsConsigneeEoriKnownPage)(true)

          val helper = new TraderDetailsCheckYourAnswersHelper(answers)
          val result = helper.isConsigneeEoriKnown

          val label = msg"isConsigneeEoriKnown.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.IsConsigneeEoriKnownController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "consignorName" - {

      val consignorName: String = "CONSIGNOR NAME"

      "return None" - {
        "ConsignorNamePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new TraderDetailsCheckYourAnswersHelper(answers)
          val result = helper.consignorName
          result mustBe None
        }
      }

      "return Some(row)" - {
        "ConsignorNamePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(ConsignorNamePage)(consignorName)

          val helper = new TraderDetailsCheckYourAnswersHelper(answers)
          val result = helper.consignorName

          val label = msg"consignorName.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$consignorName"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.ConsignorNameController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "addConsignee" - {

      "return None" - {
        "AddConsigneePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new TraderDetailsCheckYourAnswersHelper(answers)
          val result = helper.addConsignee
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddConsigneePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddConsigneePage)(true)

          val helper = new TraderDetailsCheckYourAnswersHelper(answers)
          val result = helper.addConsignee

          val label = msg"addConsignee.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.AddConsigneeController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-consignee-same-for-all-items")
                )
              )
            )
          )
        }
      }
    }

    "consignorAddress" - {

      val address: CommonAddress = CommonAddress("LINE 1", "LINE 2", "POSTCODE", Country(CountryCode("CODE"), "DESCRIPTION"))

      "return None" - {
        "ConsignorAddressPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new TraderDetailsCheckYourAnswersHelper(answers)
          val result = helper.consignorAddress
          result mustBe None
        }
      }

      "return Some(row)" - {
        "ConsignorAddressPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(ConsignorAddressPage)(address)

          val helper = new TraderDetailsCheckYourAnswersHelper(answers)
          val result = helper.consignorAddress

          val label = msg"consignorAddress.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(Html(Seq(address.AddressLine1, address.AddressLine2, address.postalCode, address.country.description).mkString("<br>"))),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.ConsignorAddressController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "consignorEori" - {

      val consignorEori: String = "CONSIGNOR EORI"

      "return None" - {
        "ConsignorEoriPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new TraderDetailsCheckYourAnswersHelper(answers)
          val result = helper.consignorEori
          result mustBe None
        }
      }

      "return Some(row)" - {
        "ConsignorEoriPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(ConsignorEoriPage)(consignorEori)

          val helper = new TraderDetailsCheckYourAnswersHelper(answers)
          val result = helper.consignorEori

          val label = msg"consignorEori.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$consignorEori"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.ConsignorEoriController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "addConsignor" - {

      "return None" - {
        "AddConsignorPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new TraderDetailsCheckYourAnswersHelper(answers)
          val result = helper.addConsignor
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddConsignorPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddConsignorPage)(true)

          val helper = new TraderDetailsCheckYourAnswersHelper(answers)
          val result = helper.addConsignor

          val label = msg"addConsignor.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.AddConsignorController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-consignor-same-for-all-items")
                )
              )
            )
          )
        }
      }
    }

    "isConsignorEoriKnown" - {

      "return None" - {
        "IsConsignorEoriKnownPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new TraderDetailsCheckYourAnswersHelper(answers)
          val result = helper.isConsignorEoriKnown
          result mustBe None
        }
      }

      "return Some(row)" - {
        "IsConsignorEoriKnownPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(IsConsignorEoriKnownPage)(true)

          val helper = new TraderDetailsCheckYourAnswersHelper(answers)
          val result = helper.isConsignorEoriKnown

          val label = msg"isConsignorEoriKnown.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.IsConsignorEoriKnownController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "principalName" - {

      val principalName: String = "PRINCIPAL NAME"

      "return None" - {
        "PrincipalNamePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new TraderDetailsCheckYourAnswersHelper(answers)
          val result = helper.principalName
          result mustBe None
        }
      }

      "return Some(row)" - {
        "PrincipalNamePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(PrincipalNamePage)(principalName)

          val helper = new TraderDetailsCheckYourAnswersHelper(answers)
          val result = helper.principalName

          val label = msg"principalName.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$principalName"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.PrincipalNameController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "isPrincipalEoriKnown" - {

      "return None" - {
        "IsPrincipalEoriKnownPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new TraderDetailsCheckYourAnswersHelper(answers)
          val result = helper.isPrincipalEoriKnown
          result mustBe None
        }
      }

      "return Some(row)" - {
        "IsPrincipalEoriKnownPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(IsPrincipalEoriKnownPage)(true)

          val helper = new TraderDetailsCheckYourAnswersHelper(answers)
          val result = helper.isPrincipalEoriKnown

          val label = msg"isPrincipalEoriKnown.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.IsPrincipalEoriKnownController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-is-principal-eori-known")
                )
              )
            )
          )
        }
      }
    }

    "whatIsPrincipalEori" - {

      val principalEori: String = "PRINCIPAL EORI"

      "return None" - {
        "WhatIsPrincipalEoriPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new TraderDetailsCheckYourAnswersHelper(answers)
          val result = helper.whatIsPrincipalEori
          result mustBe None
        }
      }

      "return Some(row)" - {
        "WhatIsPrincipalEoriPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(WhatIsPrincipalEoriPage)(principalEori)

          val helper = new TraderDetailsCheckYourAnswersHelper(answers)
          val result = helper.whatIsPrincipalEori

          val label = msg"whatIsPrincipalEori.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$principalEori"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.WhatIsPrincipalEoriController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

  }
}
