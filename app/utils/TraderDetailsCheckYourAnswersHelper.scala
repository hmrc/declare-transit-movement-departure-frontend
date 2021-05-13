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

import controllers.traderDetails.routes
import models.{CheckMode, LocalReferenceNumber, UserAnswers}
import pages._
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels._

class TraderDetailsCheckYourAnswersHelper(userAnswers: UserAnswers) {

  def consigneeAddress: Option[Row] = userAnswers.get(ConsigneeAddressPage) map {
    answer =>
      val address = Html(
        Seq(answer.AddressLine1, answer.AddressLine2, answer.postalCode, answer.country.description)
          .mkString("<br>")
      )
      Row(
        key   = Key(msg"consigneeAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(address),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ConsigneeAddressController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"consigneeAddress.checkYourAnswersLabel"))
          )
        )
      )
  }

  def principalAddress: Option[Row] = userAnswers.get(PrincipalAddressPage) map {
    answer =>
      val address = Html(
        Seq(answer.numberAndStreet, answer.town, answer.postcode)
          .mkString("<br>")
      )
      Row(
        key   = Key(msg"principalAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(address),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.PrincipalAddressController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"principalAddress.checkYourAnswersLabel"))
          )
        )
      )
  }

  def consigneeName: Option[Row] = userAnswers.get(ConsigneeNamePage) map {
    answer =>
      Row(
        key   = Key(msg"consigneeName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ConsigneeNameController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"consigneeName.checkYourAnswersLabel"))
          )
        )
      )
  }

  def whatIsConsigneeEori: Option[Row] = userAnswers.get(WhatIsConsigneeEoriPage) map {
    answer =>
      Row(
        key   = Key(msg"whatIsConsigneeEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.WhatIsConsigneeEoriController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"whatIsConsigneeEori.checkYourAnswersLabel"))
          )
        )
      )
  }

  def isConsigneeEoriKnown: Option[Row] = userAnswers.get(IsConsigneeEoriKnownPage) map {
    answer =>
      Row(
        key   = Key(msg"isConsigneeEoriKnown.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.IsConsigneeEoriKnownController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"isConsigneeEoriKnown.checkYourAnswersLabel"))
          )
        )
      )
  }

  def consignorName: Option[Row] = userAnswers.get(ConsignorNamePage) map {
    answer =>
      Row(
        key   = Key(msg"consignorName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ConsignorNameController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"consignorName.checkYourAnswersLabel"))
          )
        )
      )
  }

  def addConsignee: Option[Row] = userAnswers.get(AddConsigneePage) map {
    answer =>
      Row(
        key   = Key(msg"addConsignee.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddConsigneeController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addConsignee.checkYourAnswersLabel")),
            attributes         = Map("id" -> "change-consignee-same-for-all-items")
          )
        )
      )
  }

  def consignorAddress: Option[Row] = userAnswers.get(ConsignorAddressPage) map {
    answer =>
      val address = Html(
        Seq(answer.AddressLine1, answer.AddressLine2, answer.AddressLine3, answer.country.description)
          .mkString("<br>")
      )

      Row(
        key   = Key(msg"consignorAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(address),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ConsignorAddressController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"consignorAddress.checkYourAnswersLabel"))
          )
        )
      )
  }

  def consignorEori: Option[Row] = userAnswers.get(ConsignorEoriPage) map {
    answer =>
      Row(
        key   = Key(msg"consignorEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ConsignorEoriController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"consignorEori.checkYourAnswersLabel"))
          )
        )
      )
  }

  def addConsignor: Option[Row] = userAnswers.get(AddConsignorPage) map {
    answer =>
      Row(
        key   = Key(msg"addConsignor.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddConsignorController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addConsignor.checkYourAnswersLabel")),
            attributes         = Map("id" -> "change-consignor-same-for-all-items")
          )
        )
      )
  }

  def isConsignorEoriKnown: Option[Row] = userAnswers.get(IsConsignorEoriKnownPage) map {
    answer =>
      Row(
        key   = Key(msg"isConsignorEoriKnown.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.IsConsignorEoriKnownController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"isConsignorEoriKnown.checkYourAnswersLabel"))
          )
        )
      )
  }

  def principalName: Option[Row] = userAnswers.get(PrincipalNamePage) map {
    answer =>
      Row(
        key   = Key(msg"principalName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.PrincipalNameController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"principalName.checkYourAnswersLabel"))
          )
        )
      )
  }

  def isPrincipalEoriKnown: Option[Row] = userAnswers.get(IsPrincipalEoriKnownPage) map {
    answer =>
      Row(
        key   = Key(msg"isPrincipalEoriKnown.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.IsPrincipalEoriKnownController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"isPrincipalEoriKnown.checkYourAnswersLabel")),
            attributes         = Map("id" -> s"""change-is-principal-eori-known""")
          )
        )
      )
  }

  def whatIsPrincipalEori: Option[Row] = userAnswers.get(WhatIsPrincipalEoriPage) map {
    answer =>
      Row(
        key   = Key(msg"whatIsPrincipalEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.WhatIsPrincipalEoriController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"whatIsPrincipalEori.checkYourAnswersLabel"))
          )
        )
      )
  }

  def lrn: LocalReferenceNumber = userAnswers.id
}
