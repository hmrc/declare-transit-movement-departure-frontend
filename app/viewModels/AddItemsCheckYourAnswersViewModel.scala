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

package viewModels

import derivable.{DeriveNumberOfPackages, DeriveNumberOfPreviousAdministrativeReferences}
import models.{Index, PreviousDocumentTypeList, UserAnswers}
import uk.gov.hmrc.viewmodels.{MessageInterpolators, SummaryList}
import utils.AddItemsCheckYourAnswersHelper
import viewModels.sections.Section

case class AddItemsCheckYourAnswersViewModel(sections: Seq[Section])

object AddItemsCheckYourAnswersViewModel {

  def apply(userAnswers: UserAnswers, index: Index, documentTypes: PreviousDocumentTypeList): AddItemsCheckYourAnswersViewModel = {

    val checkYourAnswersHelper = new AddItemsCheckYourAnswersHelper(userAnswers)

    val packageRows: Seq[SummaryList.Row] =
      List.range(0, userAnswers.get(DeriveNumberOfPackages(index)).getOrElse(0)).flatMap {
        packagePosition =>
          checkYourAnswersHelper.packageRow(index, Index(packagePosition), userAnswers)
      }

    val referencesRows: Seq[SummaryList.Row] =
      List.range(0, userAnswers.get(DeriveNumberOfPreviousAdministrativeReferences(index)).getOrElse(0)).flatMap {
        position =>
          checkYourAnswersHelper.previousReferenceRows(index, Index(position), documentTypes)
      }

    AddItemsCheckYourAnswersViewModel(
      Seq(
        Section(
          msg"addItems.checkYourAnswersLabel.itemDetails",
          Seq(
            checkYourAnswersHelper.itemDescription(index),
            checkYourAnswersHelper.itemTotalGrossMass(index),
            checkYourAnswersHelper.addTotalNetMass(index),
            checkYourAnswersHelper.totalNetMass(index),
            checkYourAnswersHelper.isCommodityCodeKnown(index),
            checkYourAnswersHelper.commodityCode(index),
            checkYourAnswersHelper.addItemsSameConsigneeForAllItems(index),
            checkYourAnswersHelper.addItemsSameConsignorForAllItems(index),
            checkYourAnswersHelper.traderDetailsConsignorName(index),
            checkYourAnswersHelper.traderDetailsConsignorEoriNumber(index),
            checkYourAnswersHelper.traderDetailsConsignorEoriKnown(index),
            checkYourAnswersHelper.traderDetailsConsignorAddress(index),
            checkYourAnswersHelper.traderDetailsConsigneeName(index),
            checkYourAnswersHelper.traderDetailsConsigneeEoriNumber(index),
            checkYourAnswersHelper.traderDetailsConsigneeEoriKnown(index),
            checkYourAnswersHelper.traderDetailsConsigneeAddress(index),
          ).flatten
        ),
        Section(
          msg"addItems.checkYourAnswersLabel.packages",
          packageRows,
          checkYourAnswersHelper.addAnotherPackage(index, msg"addItems.checkYourAnswersLabel.packages.addRemove")
        ),
        Section(
          msg"addItems.checkYourAnswersLabel.references",
          Seq(checkYourAnswersHelper.addAdministrativeReference(index).toSeq, referencesRows).flatten,
          checkYourAnswersHelper.addAnotherPreviousReferences(index, msg"addItems.checkYourAnswersLabel.references.addRemove")
        )
      )
    )
  }
}
