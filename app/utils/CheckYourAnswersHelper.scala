/*
 * Copyright 2020 HM Revenue & Customs
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

import java.time.format.DateTimeFormatter

import controllers.routes
import models.{CheckMode, CountryList, CustomsOfficeList, LocalReferenceNumber, UserAnswers}
import pages._
import play.api.i18n.Messages
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels._

class CheckYourAnswersHelper(userAnswers: UserAnswers) {

  def idCrossingBorder: Option[Row] = userAnswers.get(IdCrossingBorderPage) map {
    answer =>
      Row(
        key     = Key(msg"idCrossingBorder.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.IdCrossingBorderController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"idCrossingBorder.checkYourAnswersLabel"))
          )
        )
      )
  }

  def nationalityAtDeparture: Option[Row] = userAnswers.get(NationalityAtDeparturePage) map {
    answer =>
      Row(
        key     = Key(msg"nationalityAtDeparture.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.NationalityAtDepartureController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"nationalityAtDeparture.checkYourAnswersLabel"))
          )
        )
      )
  }

  def idAtDeparture: Option[Row] = userAnswers.get(IdAtDeparturePage) map {
    answer =>
      Row(
        key     = Key(msg"idAtDeparture.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.IdAtDepartureController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"idAtDeparture.checkYourAnswersLabel"))
          )
        )
      )
  }

  def consigneeAddress: Option[Row] = userAnswers.get(ConsigneeAddressPage) map {
    answer =>
      val address =   Html(Seq(answer.AddressLine1, answer.AddressLine2, answer.AddressLine3, answer.country.description)
        .mkString("<br>"))
      Row(
        key     = Key(msg"consigneeAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(address),
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
      val address =   Html(Seq(answer.numberAndStreet, answer.town, answer.postcode)
        .mkString("<br>"))
      Row(
        key     = Key(msg"principalAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(address),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.PrincipalAddressController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"principalAddress.checkYourAnswersLabel"))
          )
        )
      )
  }

  def officeOfDeparture(customsOfficeList: CustomsOfficeList): Option[Row] = userAnswers.get(OfficeOfDeparturePage) flatMap {
    answer =>
    customsOfficeList.getCustomsOffice(answer) map {
      customsOffice =>
      Row(
        key = Key(msg"officeOfDeparture.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"${customsOffice.name} (${customsOffice.id})"),
        actions = List(
          Action(
            content = msg"site.edit",
            href = routes.OfficeOfDepartureController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"officeOfDeparture.checkYourAnswersLabel"))
          )
        )
      )
    }
  }

  def consigneeName: Option[Row] = userAnswers.get(ConsigneeNamePage) map {
    answer =>
      Row(
        key     = Key(msg"consigneeName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
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
        key     = Key(msg"whatIsConsigneeEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.WhatIsConsigneeEoriController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"whatIsConsigneeEori.checkYourAnswersLabel"))
          )
        )
      )
  }

  def countryOfDispatch(codeList: CountryList): Option[Row] = userAnswers.get(CountryOfDispatchPage) map {
    answer =>
      val countryName = codeList.getCountry(answer).map(_.description).getOrElse(answer.code)

      Row(
        key     = Key(msg"countryOfDispatch.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$countryName"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.CountryOfDispatchController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"countryOfDispatch.checkYourAnswersLabel"))
          )
        )
      )
  }

  def isConsigneeEoriKnown: Option[Row] = userAnswers.get(IsConsigneeEoriKnownPage) map {
    answer =>
      Row(
        key     = Key(msg"isConsigneeEoriKnown.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
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
        key     = Key(msg"consignorName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
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
        key     = Key(msg"addConsignee.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddConsigneeController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addConsignee.checkYourAnswersLabel"))
          )
        )
      )
  }

  def consignorAddress: Option[Row] = userAnswers.get(ConsignorAddressPage) map {
    answer =>
   val address =   Html(Seq(answer.AddressLine1, answer.AddressLine2, answer.AddressLine3, answer.country.description)
        .mkString("<br>"))

      Row(
        key     = Key(msg"consignorAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(address),
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
        key     = Key(msg"consignorEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
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
        key     = Key(msg"addConsignor.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddConsignorController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addConsignor.checkYourAnswersLabel"))
          )
        )
      )
  }

  def isConsignorEoriKnown: Option[Row] = userAnswers.get(IsConsignorEoriKnownPage) map {
    answer =>
      Row(
        key     = Key(msg"isConsignorEoriKnown.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
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
        key     = Key(msg"principalName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
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
        key     = Key(msg"isPrincipalEoriKnown.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.IsPrincipalEoriKnownController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"isPrincipalEoriKnown.checkYourAnswersLabel"))
          )
        )
      )
  }

  def whatIsPrincipalEori: Option[Row] = userAnswers.get(WhatIsPrincipalEoriPage) map {
    answer =>
      Row(
        key     = Key(msg"whatIsPrincipalEori.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.WhatIsPrincipalEoriController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"whatIsPrincipalEori.checkYourAnswersLabel"))
          )
        )
      )
  }

  def addSecurityDetails: Option[Row] = userAnswers.get(AddSecurityDetailsPage) map {
    answer =>
      Row(
        key     = Key(msg"addSecurityDetails.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value   = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddSecurityDetailsController.onPageLoad(lrn, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addSecurityDetails.checkYourAnswersLabel")),
            attributes = Map("id" -> s"""change-add-security-details""")
          )
        )
      )
  }

  def lrn: LocalReferenceNumber = userAnswers.id
}

object CheckYourAnswersHelper {

  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
}
