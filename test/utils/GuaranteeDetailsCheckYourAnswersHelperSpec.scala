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
import controllers.guaranteeDetails.routes
import generators.Generators
import models.DeclarationType.Option4
import models.reference.{CountryCode, CustomsOffice}
import models.{CheckMode, DeclarationType, GuaranteeType, Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.guaranteeDetails.{GuaranteeReferencePage, GuaranteeTypePage, TIRGuaranteeReferencePage}
import pages.routeDetails.DestinationOfficePage
import uk.gov.hmrc.viewmodels.MessageInterpolators
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}

class GuaranteeDetailsCheckYourAnswersHelperSpec extends SpecBase with UserAnswersSpecHelper with ScalaCheckPropertyChecks with Generators {

  val mode: Mode = CheckMode

  "GuaranteeDetailsCheckYourAnswersHelper" - {

    "defaultAmount" - {

      "return None" - {
        "DefaultAmountPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new GuaranteeDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.defaultAmount(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "DefaultAmountPage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(DefaultAmountPage(index))(true)

          val helper = new GuaranteeDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.defaultAmount(index)

          val label = msg"defaultAmount.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(msg"site.yes"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.DefaultAmountController.onPageLoad(lrn, index, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-default-amount")
                )
              )
            )
          )
        }
      }
    }

    "guaranteeType" - {

      val guaranteeType: GuaranteeType = GuaranteeType.GuaranteeWaiver

      "return None" - {

        "GuaranteeTypePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new GuaranteeDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.guaranteeType(index)
          result mustBe None
        }

        "Option4 declaration type and 0th index" in {

          val answers = emptyUserAnswers
            .unsafeSetVal(GuaranteeTypePage(index))(guaranteeType)
            .unsafeSetVal(DeclarationTypePage)(Option4)

          val helper = new GuaranteeDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.guaranteeType(index)
          result mustBe None
        }
      }

      "return Some(row)" - {

        "GuaranteeTypePage defined at index" - {

          "non-Option4 declaration type" in {

            forAll(arbitrary[Option[DeclarationType]].retryUntil(!_.contains(Option4))) {
              declarationType =>
                val answers = emptyUserAnswers
                  .unsafeSetVal(GuaranteeTypePage(index))(guaranteeType)
                  .unsafeSetOpt(DeclarationTypePage)(declarationType)

                val helper = new GuaranteeDetailsCheckYourAnswersHelper(answers, mode)
                val result = helper.guaranteeType(index)

                val label = msg"guaranteeType.checkYourAnswersLabel"

                result mustBe Some(
                  Row(
                    key = Key(label, classes = Seq("govuk-!-width-one-half")),
                    value = Value(msg"guaranteeType.${GuaranteeType.getId(guaranteeType.toString)}"),
                    actions = List(
                      Action(
                        content = msg"site.edit",
                        href = routes.GuaranteeTypeController.onPageLoad(lrn, index, mode).url,
                        visuallyHiddenText = Some(label),
                        attributes = Map("id" -> "change-guarantee-type")
                      )
                    )
                  )
                )
            }
          }

          "not 0th index" in {

            val index = Index(1)

            val answers = emptyUserAnswers
              .unsafeSetVal(GuaranteeTypePage(Index(0)))(guaranteeType)
              .unsafeSetVal(GuaranteeTypePage(index))(guaranteeType)
              .unsafeSetVal(DeclarationTypePage)(Option4)

            val helper = new GuaranteeDetailsCheckYourAnswersHelper(answers, mode)
            val result = helper.guaranteeType(index)

            val label = msg"guaranteeType.checkYourAnswersLabel"

            result mustBe Some(
              Row(
                key = Key(label, classes = Seq("govuk-!-width-one-half")),
                value = Value(msg"guaranteeType.${GuaranteeType.getId(guaranteeType.toString)}"),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = routes.GuaranteeTypeController.onPageLoad(lrn, index, mode).url,
                    visuallyHiddenText = Some(label),
                    attributes = Map("id" -> "change-guarantee-type")
                  )
                )
              )
            )
          }
        }
      }
    }

    "accessCode" - {

      val accessCode: String = "ACCESS CODE"

      "return None" - {
        "AccessCodePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new GuaranteeDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.accessCode(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "AccessCodePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(AccessCodePage(index))(accessCode)

          val helper = new GuaranteeDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.accessCode(index)

          val label = msg"accessCode.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"••••"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.AccessCodeController.onPageLoad(lrn, index, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-access-code")
                )
              )
            )
          )
        }
      }
    }

    "otherReference" - {

      val otherReference: String = "OTHER REFERENCE"

      "return None" - {
        "OtherReferencePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new GuaranteeDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.otherReference(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "OtherReferencePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(OtherReferencePage(index))(otherReference)

          val helper = new GuaranteeDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.otherReference(index)

          val label = msg"otherReference.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$otherReference"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.OtherReferenceController.onPageLoad(lrn, index, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-other-reference")
                )
              )
            )
          )
        }
      }
    }

    "tirLiabilityAmount" - {

      "return None" - {

        "LiabilityAmountPage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new GuaranteeDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.tirLiabilityAmount(index)
          result mustBe None
        }

        "non-Option4 declaration type" in {

          forAll(arbitrary[Option[DeclarationType]].retryUntil(!_.contains(Option4))) {
            declarationType =>
              val answers = emptyUserAnswers.unsafeSetOpt(DeclarationTypePage)(declarationType)

              val helper = new GuaranteeDetailsCheckYourAnswersHelper(answers, mode)
              val result = helper.tirLiabilityAmount(index)
              result mustBe None
          }
        }

        "not at 0th index" in {

          val index = Index(1)

          val answers = emptyUserAnswers
            .unsafeSetVal(LiabilityAmountPage(Index(0)))("AMOUNT 1")
            .unsafeSetVal(LiabilityAmountPage(index))("AMOUNT 2")

          val helper = new GuaranteeDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.tirLiabilityAmount(index)
          result mustBe None
        }
      }

      "return Some(row)" - {

        val amount: String = "AMOUNT"

        "LiabilityAmountPage defined at index, Option4 declaration type and at 0th index" in {

          val answers = emptyUserAnswers
            .unsafeSetVal(LiabilityAmountPage(index))(amount)
            .unsafeSetVal(DeclarationTypePage)(Option4)

          val helper = new GuaranteeDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.tirLiabilityAmount(index)

          val label = msg"liabilityAmount.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$amount"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.OtherReferenceLiabilityAmountController.onPageLoad(lrn, index, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-liability-amount")
                )
              )
            )
          )
        }
      }
    }

    "liabilityAmount" - {

      "return None" - {

        "OfficeOfDeparturePage undefined or " +
          "DestinationOfficePage undefined or " +
          "GuaranteeTypePage undefined at index or " +
          "guarantee type is a non-guarantee reference route" in {

            val gen = arbitrary[(Option[CustomsOffice], Option[CustomsOffice], Option[GuaranteeType])]

            forAll(gen.retryUntil {
              case (departureOffice, destinationOffice, guaranteeType) =>
                val conditionForSomeRow = departureOffice.isDefined &&
                  destinationOffice.isDefined &&
                  guaranteeType.isDefined &&
                  guaranteeType.fold(false)(GuaranteeType.guaranteeReferenceRoute.contains(_))

                !conditionForSomeRow
            }) {
              case (departureOffice, destinationOffice, guaranteeType) =>
                val answers = emptyUserAnswers
                  .unsafeSetOpt(OfficeOfDeparturePage)(departureOffice)
                  .unsafeSetOpt(DestinationOfficePage)(destinationOffice)
                  .unsafeSetOpt(GuaranteeTypePage(index))(guaranteeType)

                val helper = new GuaranteeDetailsCheckYourAnswersHelper(answers, mode)
                val result = helper.liabilityAmount(index)
                result mustBe None
            }
          }
      }

      "return Some(row)" - {

        val office: CustomsOffice   = CustomsOffice("ID", "NAME", CountryCode("CODE"), None)
        val gbOffice: CustomsOffice = office.copy(countryId = office.countryId.copy(code = "GB"))

        "OfficeOfDeparturePage defined and " +
          "DestinationOfficePage defined and " +
          "GuaranteeTypePage defined at index and " +
          "guarantee type is a guarantee reference route" - {

            "LiabilityAmountPage defined at index with non-blank value" in {

              forAll(arbitrary[(GuaranteeType, String)].suchThat {
                case (guaranteeType, amount) =>
                  GuaranteeType.guaranteeReferenceRoute.contains(guaranteeType) &&
                    amount.trim.nonEmpty
              }) {
                case (guaranteeType, amount) =>
                  val answers = emptyUserAnswers
                    .unsafeSetVal(OfficeOfDeparturePage)(office)
                    .unsafeSetVal(DestinationOfficePage)(office)
                    .unsafeSetVal(GuaranteeTypePage(index))(guaranteeType)
                    .unsafeSetVal(LiabilityAmountPage(index))(amount)

                  val helper = new GuaranteeDetailsCheckYourAnswersHelper(answers, mode)
                  val result = helper.liabilityAmount(index)

                  val label = msg"liabilityAmount.checkYourAnswersLabel"

                  result mustBe Some(
                    Row(
                      key = Key(label, classes = Seq("govuk-!-width-one-half")),
                      value = Value(lit"$amount"),
                      actions = List(
                        Action(
                          content = msg"site.edit",
                          href = routes.OtherReferenceLiabilityAmountController.onPageLoad(lrn, index, mode).url,
                          visuallyHiddenText = Some(label),
                          attributes = Map("id" -> "change-liability-amount")
                        )
                      )
                    )
                  )
              }
            }

            "LiabilityAmountPage undefined at index" in {

              forAll(arbitrary[GuaranteeType].retryUntil {
                guaranteeType =>
                  GuaranteeType.guaranteeReferenceRoute.contains(guaranteeType)
              }) {
                guaranteeType =>
                  val answers = emptyUserAnswers
                    .unsafeSetVal(OfficeOfDeparturePage)(office)
                    .unsafeSetVal(DestinationOfficePage)(gbOffice)
                    .unsafeSetVal(GuaranteeTypePage(index))(guaranteeType)

                  val helper = new GuaranteeDetailsCheckYourAnswersHelper(answers, mode)
                  val result = helper.liabilityAmount(index)

                  val label = msg"liabilityAmount.checkYourAnswersLabel"

                  result mustBe Some(
                    Row(
                      key = Key(label, classes = Seq("govuk-!-width-one-half")),
                      value = Value(msg"guaranteeDetailsCheckYourAnswers.defaultLiabilityAmount"),
                      actions = List(
                        Action(
                          content = msg"site.edit",
                          href = routes.OtherReferenceLiabilityAmountController.onPageLoad(lrn, index, mode).url,
                          visuallyHiddenText = Some(label),
                          attributes = Map("id" -> "change-liability-amount")
                        )
                      )
                    )
                  )
              }
            }

            "LiabilityAmountPage defined at index with blank value" in {

              forAll(arbitrary[(GuaranteeType, Int)].retryUntil {
                case (guaranteeType, characterCount) =>
                  GuaranteeType.guaranteeReferenceRoute.contains(guaranteeType) &&
                    characterCount < 100
              }) {
                case (guaranteeType, characterCount) =>
                  val answers = emptyUserAnswers
                    .unsafeSetVal(OfficeOfDeparturePage)(gbOffice)
                    .unsafeSetVal(DestinationOfficePage)(office)
                    .unsafeSetVal(GuaranteeTypePage(index))(guaranteeType)
                    .unsafeSetVal(LiabilityAmountPage(index))(" " * characterCount)

                  val helper = new GuaranteeDetailsCheckYourAnswersHelper(answers, mode)
                  val result = helper.liabilityAmount(index)

                  val label = msg"liabilityAmount.checkYourAnswersLabel"

                  result mustBe Some(
                    Row(
                      key = Key(label, classes = Seq("govuk-!-width-one-half")),
                      value = Value(msg"guaranteeDetailsCheckYourAnswers.defaultLiabilityAmount"),
                      actions = List(
                        Action(
                          content = msg"site.edit",
                          href = routes.OtherReferenceLiabilityAmountController.onPageLoad(lrn, index, mode).url,
                          visuallyHiddenText = Some(label),
                          attributes = Map("id" -> "change-liability-amount")
                        )
                      )
                    )
                  )
              }
            }

            "GB departure and destination offices" in {

              forAll(arbitrary[GuaranteeType].retryUntil {
                guaranteeType =>
                  GuaranteeType.guaranteeReferenceRoute.contains(guaranteeType)
              }) {
                guaranteeType =>
                  val answers = emptyUserAnswers
                    .unsafeSetVal(OfficeOfDeparturePage)(gbOffice)
                    .unsafeSetVal(DestinationOfficePage)(gbOffice)
                    .unsafeSetVal(GuaranteeTypePage(index))(guaranteeType)

                  val helper = new GuaranteeDetailsCheckYourAnswersHelper(answers, mode)
                  val result = helper.liabilityAmount(index)

                  val label = msg"liabilityAmount.checkYourAnswersLabel"

                  result mustBe Some(
                    Row(
                      key = Key(label, classes = Seq("govuk-!-width-one-half")),
                      value = Value(msg"guaranteeDetailsCheckYourAnswers.defaultLiabilityAmount"),
                      actions = List(
                        Action(
                          content = msg"site.edit",
                          href = routes.LiabilityAmountController.onPageLoad(lrn, index, mode).url,
                          visuallyHiddenText = Some(label),
                          attributes = Map("id" -> "change-liability-amount")
                        )
                      )
                    )
                  )
              }
            }
          }
      }
    }

    "guaranteeReference" - {

      val guaranteeReference: String = "GUARANTEE REFERENCE"

      "return None" - {
        "GuaranteeReferencePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new GuaranteeDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.guaranteeReference(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "GuaranteeReferencePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(GuaranteeReferencePage(index))(guaranteeReference)

          val helper = new GuaranteeDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.guaranteeReference(index)

          val label = msg"guaranteeReference.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$guaranteeReference"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.GuaranteeReferenceController.onPageLoad(lrn, index, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-guarantee-reference")
                )
              )
            )
          )
        }
      }
    }

    "tirGuaranteeReference" - {

      val guaranteeReference: String = "GUARANTEE REFERENCE"

      "return None" - {
        "TIRGuaranteeReferencePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new GuaranteeDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.tirGuaranteeReference(index)
          result mustBe None
        }
      }

      "return Some(row)" - {
        "TIRGuaranteeReferencePage defined at index" in {

          val answers = emptyUserAnswers.unsafeSetVal(TIRGuaranteeReferencePage(index))(guaranteeReference)

          val helper = new GuaranteeDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.tirGuaranteeReference(index)

          val label = msg"tirGuaranteeReference.checkYourAnswersLabel"

          result mustBe Some(
            Row(
              key = Key(label, classes = Seq("govuk-!-width-one-half")),
              value = Value(lit"$guaranteeReference"),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.TIRGuaranteeReferenceController.onPageLoad(lrn, index, mode).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> "change-tir-guarantee-reference")
                )
              )
            )
          )
        }
      }
    }

    "guaranteeRows" - {

      "return None" - {

        "is TIR and TIRGuaranteeReferencePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new GuaranteeDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.guaranteeRow(index, isTir = true)
          result mustBe None
        }

        "is not TIR and GuaranteeTypePage undefined at index" in {

          val answers = emptyUserAnswers

          val helper = new GuaranteeDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.guaranteeRow(index, isTir = false)
          result mustBe None
        }
      }

      "return Some(row)" - {

        "is TIR and TIRGuaranteeReferencePage defined at index" - {

          "0th index" in {

            val guaranteeReference: String = "GUARANTEE REFERENCE"

            val index = Index(0)

            val answers = emptyUserAnswers.unsafeSetVal(TIRGuaranteeReferencePage(index))(guaranteeReference)

            val helper = new GuaranteeDetailsCheckYourAnswersHelper(answers, mode)
            val result = helper.guaranteeRow(index, isTir = true)

            val label = lit"$guaranteeReference"

            result mustBe Some(
              Row(
                key = Key(label),
                value = Value(lit""),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(lrn, index).url,
                    visuallyHiddenText = Some(label),
                    attributes = Map("id" -> s"change-tir-carnet-${index.display}")
                  )
                )
              )
            )
          }

          "not 0th index" in {

            val guaranteeReference: String = "GUARANTEE REFERENCE 2"

            val index = Index(1)

            val answers = emptyUserAnswers
              .unsafeSetVal(TIRGuaranteeReferencePage(Index(0)))("GUARANTEE REFERENCE 1")
              .unsafeSetVal(TIRGuaranteeReferencePage(index))(guaranteeReference)

            val helper = new GuaranteeDetailsCheckYourAnswersHelper(answers, mode)
            val result = helper.guaranteeRow(index, isTir = true)

            val label = lit"$guaranteeReference"

            result mustBe Some(
              Row(
                key = Key(label),
                value = Value(lit""),
                actions = List(
                  Action(
                    content = msg"site.edit",
                    href = routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(lrn, index).url,
                    visuallyHiddenText = Some(label),
                    attributes = Map("id" -> s"change-tir-carnet-${index.display}")
                  ),
                  Action(
                    content = msg"site.delete",
                    href = routes.ConfirmRemoveGuaranteeController.onPageLoad(lrn, index).url,
                    visuallyHiddenText = Some(label),
                    attributes = Map("id" -> s"remove-tir-carnet-${index.display}")
                  )
                )
              )
            )
          }
        }

        "is not TIR and GuaranteeTypePage defined at index" in {

          val guaranteeType: GuaranteeType = GuaranteeType.GuaranteeWaiver

          val answers = emptyUserAnswers.unsafeSetVal(GuaranteeTypePage(index))(guaranteeType)

          val helper = new GuaranteeDetailsCheckYourAnswersHelper(answers, mode)
          val result = helper.guaranteeRow(index, isTir = false)

          val label = msg"guaranteeType.${GuaranteeType.getId(guaranteeType.toString)}"

          result mustBe Some(
            Row(
              key = Key(label),
              value = Value(lit""),
              actions = List(
                Action(
                  content = msg"site.edit",
                  href = routes.GuaranteeDetailsCheckYourAnswersController.onPageLoad(lrn, index).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> s"change-guarantee-${index.display}")
                ),
                Action(
                  content = msg"site.delete",
                  href = routes.ConfirmRemoveGuaranteeController.onPageLoad(lrn, index).url,
                  visuallyHiddenText = Some(label),
                  attributes = Map("id" -> s"remove-guarantee-${index.display}")
                )
              )
            )
          )
        }
      }
    }

  }
}
