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
import models.{CheckMode, CommonAddress, UserAnswers}
import pages.traderDetails._
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels._

class TraderDetailsCheckYourAnswersHelper(userAnswers: UserAnswers) extends CheckYourAnswersHelper(userAnswers) {

  def principalTirHolderIdPage: Option[Row] = getAnswerAndBuildRow[String](
    page = PrincipalTirHolderIdPage,
    format = x => lit"$x",
    prefix = "principalTirHolderId",
    id = None,
    call = routes.PrincipalTirHolderIdController.onPageLoad(lrn, CheckMode)
  )

  def consigneeAddress: Option[Row] = getAnswerAndBuildRow[CommonAddress](
    page = ConsigneeAddressPage,
    format = address,
    prefix = "consigneeAddress",
    id = None,
    call = routes.ConsigneeAddressController.onPageLoad(lrn, CheckMode)
  )

  def principalAddress: Option[Row] = getAnswerAndBuildRow[CommonAddress](
    page = PrincipalAddressPage,
    format = address,
    prefix = "principalAddress",
    id = None,
    call = routes.PrincipalAddressController.onPageLoad(lrn, CheckMode)
  )

  def consigneeName: Option[Row] = getAnswerAndBuildRow[String](
    page = ConsigneeNamePage,
    format = x => lit"$x",
    prefix = "consigneeName",
    id = None,
    call = routes.ConsigneeNameController.onPageLoad(lrn, CheckMode)
  )

  def whatIsConsigneeEori: Option[Row] = getAnswerAndBuildRow[String](
    page = WhatIsConsigneeEoriPage,
    format = x => lit"$x",
    prefix = "whatIsConsigneeEori",
    id = None,
    call = routes.WhatIsConsigneeEoriController.onPageLoad(lrn, CheckMode)
  )

  def isConsigneeEoriKnown: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = IsConsigneeEoriKnownPage,
    format = yesOrNo,
    prefix = "isConsigneeEoriKnown",
    id = None,
    call = routes.IsConsigneeEoriKnownController.onPageLoad(lrn, CheckMode)
  )

  def consignorName: Option[Row] = getAnswerAndBuildRow[String](
    page = ConsignorNamePage,
    format = x => lit"$x",
    prefix = "consignorName",
    id = None,
    call = routes.ConsignorNameController.onPageLoad(lrn, CheckMode)
  )

  def addConsignee: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddConsigneePage,
    format = yesOrNo,
    prefix = "addConsignee",
    id = Some("change-consignee-same-for-all-items"),
    call = routes.AddConsigneeController.onPageLoad(lrn, CheckMode)
  )

  def consignorAddress: Option[Row] = getAnswerAndBuildRow[CommonAddress](
    page = ConsignorAddressPage,
    format = address,
    prefix = "consignorAddress",
    id = None,
    call = routes.ConsignorAddressController.onPageLoad(lrn, CheckMode)
  )

  def consignorEori: Option[Row] = getAnswerAndBuildRow[String](
    page = ConsignorEoriPage,
    format = x => lit"$x",
    prefix = "consignorEori",
    id = None,
    call = routes.ConsignorEoriController.onPageLoad(lrn, CheckMode)
  )

  def addConsignor: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = AddConsignorPage,
    format = yesOrNo,
    prefix = "addConsignor",
    id = Some("change-consignor-same-for-all-items"),
    call = routes.AddConsignorController.onPageLoad(lrn, CheckMode)
  )

  def isConsignorEoriKnown: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = IsConsignorEoriKnownPage,
    format = yesOrNo,
    prefix = "isConsignorEoriKnown",
    id = None,
    call = routes.IsConsignorEoriKnownController.onPageLoad(lrn, CheckMode)
  )

  def principalName: Option[Row] = getAnswerAndBuildRow[String](
    page = PrincipalNamePage,
    format = x => lit"$x",
    prefix = "principalName",
    id = None,
    call = routes.PrincipalNameController.onPageLoad(lrn, CheckMode)
  )

  def isPrincipalEoriKnown: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = IsPrincipalEoriKnownPage,
    format = yesOrNo,
    prefix = "isPrincipalEoriKnown",
    id = Some("change-is-principal-eori-known"),
    call = routes.IsPrincipalEoriKnownController.onPageLoad(lrn, CheckMode)
  )

  def whatIsPrincipalEori: Option[Row] = getAnswerAndBuildRow[String](
    page = WhatIsPrincipalEoriPage,
    format = x => lit"$x",
    prefix = "whatIsPrincipalEori",
    id = None,
    call = routes.WhatIsPrincipalEoriController.onPageLoad(lrn, CheckMode)
  )
}
