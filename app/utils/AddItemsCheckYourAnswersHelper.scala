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

import controllers.addItems.traderDetails.{routes => traderDetailsRoutes}
import controllers.addItems.routes
import models.{CheckMode, Index, LocalReferenceNumber, UserAnswers}
import pages._
import pages.addItems.traderDetails._
import pages.addItems.{AddItemsSameConsigneeForAllItemsPage, AddItemsSameConsignorForAllItemsPage, CommodityCodePage}
import uk.gov.hmrc.viewmodels.SummaryList.{Action, Key, Row, Value}
import uk.gov.hmrc.viewmodels._

class AddItemsCheckYourAnswersHelper(userAnswers: UserAnswers) {

  def addItemsSameConsignorForAllItems(index: Index): Option[Row] = userAnswers.get(AddItemsSameConsignorForAllItemsPage) map {
    answer =>
      Row(
        key   = Key(msg"addItemsSameConsignorForAllItems.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddItemsSameConsignorForAllItemsController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addItemsSameConsignorForAllItems.checkYourAnswersLabel"))
          )
        )
      )
  }

  def addItemsSameConsigneeForAllItems(index: Index): Option[Row] = userAnswers.get(AddItemsSameConsigneeForAllItemsPage) map {
    answer =>
      Row(
        key   = Key(msg"addItemsSameConsigneeForAllItems.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddItemsSameConsigneeForAllItemsController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addItemsSameConsigneeForAllItems.checkYourAnswersLabel"))
          )
        )
      )
  }

  def traderDetailsConsignorName(index: Index): Option[Row] = userAnswers.get(TraderDetailsConsignorNamePage) map {
    answer =>
      Row(
        key   = Key(msg"traderDetailsConsignorName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = traderDetailsRoutes.TraderDetailsConsignorNameController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsignorName.checkYourAnswersLabel"))
          )
        )
      )
  }

  def traderDetailsConsignorEoriNumber(index: Index): Option[Row] = userAnswers.get(TraderDetailsConsignorEoriNumberPage) map {
    answer =>
      Row(
        key   = Key(msg"traderDetailsConsignorEoriNumber.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = traderDetailsRoutes.TraderDetailsConsignorEoriNumberController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsignorEoriNumber.checkYourAnswersLabel"))
          )
        )
      )
  }

  def traderDetailsConsignorEoriKnown(index: Index): Option[Row] = userAnswers.get(TraderDetailsConsignorEoriKnownPage) map {
    answer =>
      Row(
        key   = Key(msg"traderDetailsConsignorEoriKnown.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = traderDetailsRoutes.TraderDetailsConsignorEoriKnownController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsignorEoriKnown.checkYourAnswersLabel"))
          )
        )
      )
  }

  def traderDetailsConsignorAddress(index: Index): Option[Row] = userAnswers.get(TraderDetailsConsignorAddressPage) map {
    answer =>
      Row(
        key   = Key(msg"traderDetailsConsignorAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = traderDetailsRoutes.TraderDetailsConsignorAddressController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsignorAddress.checkYourAnswersLabel"))
          )
        )
      )
  }

  def traderDetailsConsigneeName(index: Index): Option[Row] = userAnswers.get(TraderDetailsConsigneeNamePage) map {
    answer =>
      Row(
        key   = Key(msg"traderDetailsConsigneeName.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = traderDetailsRoutes.TraderDetailsConsigneeNameController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsigneeName.checkYourAnswersLabel"))
          )
        )
      )
  }

  def traderDetailsConsigneeEoriNumber(index: Index): Option[Row] = userAnswers.get(TraderDetailsConsigneeEoriNumberPage) map {
    answer =>
      Row(
        key   = Key(msg"traderDetailsConsigneeEoriNumber.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = traderDetailsRoutes.TraderDetailsConsigneeEoriNumberController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsigneeEoriNumber.checkYourAnswersLabel"))
          )
        )
      )
  }

  def traderDetailsConsigneeEoriKnown(index: Index): Option[Row] = userAnswers.get(TraderDetailsConsigneeEoriKnownPage) map {
    answer =>
      Row(
        key   = Key(msg"traderDetailsConsigneeEoriKnown.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = traderDetailsRoutes.TraderDetailsConsigneeEoriKnownController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsigneeEoriKnown.checkYourAnswersLabel"))
          )
        )
      )
  }

  def traderDetailsConsigneeAddress(index: Index): Option[Row] = userAnswers.get(TraderDetailsConsigneeAddressPage) map {
    answer =>
      Row(
        key   = Key(msg"traderDetailsConsigneeAddress.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = traderDetailsRoutes.TraderDetailsConsigneeAddressController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"traderDetailsConsigneeAddress.checkYourAnswersLabel"))
          )
        )
      )
  }

  def commodityCode(index: Index): Option[Row] = userAnswers.get(CommodityCodePage(index)) map {
    answer =>
      Row(
        key   = Key(msg"commodityCode.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.CommodityCodeController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"commodityCode.checkYourAnswersLabel"))
          )
        )
      )
  }

  def totalNetMass(index: Index): Option[Row] = userAnswers.get(TotalNetMassPage(index)) map {
    answer =>
      Row(
        key   = Key(msg"totalNetMass.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.TotalNetMassController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"totalNetMass.checkYourAnswersLabel".withArgs(index.display)))
          )
        )
      )
  }

  def isCommodityCodeKnown(index: Index): Option[Row] = userAnswers.get(IsCommodityCodeKnownPage(index)) map {
    answer =>
      Row(
        key   = Key(msg"isCommodityCodeKnown.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.IsCommodityCodeKnownController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"isCommodityCodeKnown.checkYourAnswersLabel"))
          )
        )
      )
  }

  def addTotalNetMass(index: Index): Option[Row] = userAnswers.get(AddTotalNetMassPage(index)) map {
    answer =>
      Row(
        key   = Key(msg"addTotalNetMass.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.AddTotalNetMassController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"addTotalNetMass.checkYourAnswersLabel"))
          )
        )
      )
  }

  def itemTotalGrossMass(index: Index): Option[Row] = userAnswers.get(ItemTotalGrossMassPage(index)) map {
    answer =>
      Row(
        key   = Key(msg"itemTotalGrossMass.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ItemTotalGrossMassController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"itemTotalGrossMass.checkYourAnswersLabel"))
          )
        )
      )
  }

  def itemDescription(index: Index): Option[Row] = userAnswers.get(ItemDescriptionPage(index)) map {
    answer =>
      Row(
        key   = Key(msg"itemDescription.checkYourAnswersLabel".withArgs(index.display), classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ItemDescriptionController.onPageLoad(lrn, index, CheckMode).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"itemDescription.checkYourAnswersLabel"))
          )
        )
      )
  }

  def lrn: LocalReferenceNumber = userAnswers.id

}
