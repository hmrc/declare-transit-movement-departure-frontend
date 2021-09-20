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
import controllers.goodsSummary.routes
import models.CheckMode
import pages._
import uk.gov.hmrc.viewmodels.MessageInterpolators
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}

import java.time.LocalDate

class GoodsSummaryCheckYourAnswersHelperSpec extends SpecBase with UserAnswersSpecHelper {

  private val location: String = "LOCATION"

  "GoodsSummaryCheckYourAnswersHelper" - {

    "agreedLocationOfGoods" - {

      "return None" - {
        "AgreedLocationOfGoodsPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new GoodsSummaryCheckYourAnswersHelper(answers)
          val result = helper.agreedLocationOfGoods
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AgreedLocationOfGoodsPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AgreedLocationOfGoodsPage)(location)

          val helper = new GoodsSummaryCheckYourAnswersHelper(answers)
          val result = helper.agreedLocationOfGoods

          val label = msg"agreedLocationOfGoods.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$location"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.AgreedLocationOfGoodsController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "loadingPlace" - {

      "return None" - {
        "LoadingPlacePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new GoodsSummaryCheckYourAnswersHelper(answers)
          val result = helper.loadingPlace
          result mustBe None
        }
      }

      "return Some(row)" - {
        "LoadingPlacePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(LoadingPlacePage)(location)

          val helper = new GoodsSummaryCheckYourAnswersHelper(answers)
          val result = helper.loadingPlace

          val label = msg"loadingPlace.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$location"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = controllers.routes.LoadingPlaceController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "addAgreedLocationOfGoods" - {

      "return None" - {
        "AddAgreedLocationOfGoodsPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new GoodsSummaryCheckYourAnswersHelper(answers)
          val result = helper.addAgreedLocationOfGoods
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddAgreedLocationOfGoodsPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddAgreedLocationOfGoodsPage)(true)

          val helper = new GoodsSummaryCheckYourAnswersHelper(answers)
          val result = helper.addAgreedLocationOfGoods

          val label = msg"addAgreedLocationOfGoods.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.AddAgreedLocationOfGoodsController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "sealsInformation" - {

      "return None" - {
        "SealsInformationPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new GoodsSummaryCheckYourAnswersHelper(answers)
          val result = helper.sealsInformation
          result mustBe None
        }
      }

      "return Some(row)" - {
        "SealsInformationPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(SealsInformationPage)(true)

          val helper = new GoodsSummaryCheckYourAnswersHelper(answers)
          val result = helper.sealsInformation

          val label = msg"sealsInformation.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.SealsInformationController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(label)
                )
              )
            )
          )
        }
      }
    }

    "controlResultDateLimit" - {

      val dateLimit: LocalDate  = LocalDate.parse("2000-01-01")
      val formattedDate: String = "1 January 2000"

      "return None" - {
        "ControlResultDateLimitPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new GoodsSummaryCheckYourAnswersHelper(answers)
          val result = helper.controlResultDateLimit
          result mustBe None
        }
      }

      "return Some(row)" - {
        "SealsInformationPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(ControlResultDateLimitPage)(dateLimit)

          val helper = new GoodsSummaryCheckYourAnswersHelper(answers)
          val result = helper.controlResultDateLimit

          val label = msg"controlResultDateLimit.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$formattedDate"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.ControlResultDateLimitController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-control-result-date-limit")
                )
              )
            )
          )
        }
      }
    }

    "addSeals" - {

      "return None" - {
        "AddSealsPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new GoodsSummaryCheckYourAnswersHelper(answers)
          val result = helper.addSeals
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddSealsPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddSealsPage)(true)

          val helper = new GoodsSummaryCheckYourAnswersHelper(answers)
          val result = helper.addSeals

          val label = msg"addSeals.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.AddSealsController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-add-seals")
                )
              )
            )
          )
        }
      }
    }

    "customsApprovedLocation" - {

      "return None" - {
        "CustomsApprovedLocationPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new GoodsSummaryCheckYourAnswersHelper(answers)
          val result = helper.customsApprovedLocation
          result mustBe None
        }
      }

      "return Some(row)" - {
        "CustomsApprovedLocationPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(CustomsApprovedLocationPage)(location)

          val helper = new GoodsSummaryCheckYourAnswersHelper(answers)
          val result = helper.customsApprovedLocation

          val label = msg"customsApprovedLocation.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$location"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.CustomsApprovedLocationController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-customs-approved-location")
                )
              )
            )
          )
        }
      }
    }

    "addCustomsApprovedLocation" - {

      "return None" - {
        "AddCustomsApprovedLocationPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new GoodsSummaryCheckYourAnswersHelper(answers)
          val result = helper.addCustomsApprovedLocation
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AddCustomsApprovedLocationPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AddCustomsApprovedLocationPage)(true)

          val helper = new GoodsSummaryCheckYourAnswersHelper(answers)
          val result = helper.addCustomsApprovedLocation

          val label = msg"addCustomsApprovedLocation.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.AddCustomsApprovedLocationController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-add-customs-approved-location")
                )
              )
            )
          )
        }
      }
    }

    "authorisedLocationCode" - {

      "return None" - {
        "AuthorisedLocationCodePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new GoodsSummaryCheckYourAnswersHelper(answers)
          val result = helper.authorisedLocationCode
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AuthorisedLocationCodePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AuthorisedLocationCodePage)(location)

          val helper = new GoodsSummaryCheckYourAnswersHelper(answers)
          val result = helper.authorisedLocationCode

          val label = msg"authorisedLocationCode.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$location"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.AuthorisedLocationCodeController.onPageLoad(lrn, CheckMode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-authorised-location-code")
                )
              )
            )
          )
        }
      }
    }

  }
}
