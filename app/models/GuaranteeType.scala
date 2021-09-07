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

package models

import play.api.data.Form
import uk.gov.hmrc.viewmodels._

sealed trait GuaranteeType

object GuaranteeType extends Enumerable.Implicits {

  case object GuaranteeWaiver extends WithName("0") with GuaranteeType
  case object ComprehensiveGuarantee extends WithName("1") with GuaranteeType
  case object IndividualGuarantee extends WithName("2") with GuaranteeType
  case object FlatRateVoucher extends WithName("4") with GuaranteeType
  case object CashDepositGuarantee extends WithName("3") with GuaranteeType
  case object GuaranteeNotRequired extends WithName("7") with GuaranteeType
  case object GuaranteeWaivedRedirect extends WithName("6") with GuaranteeType
  case object GuaranteeWaiverByAgreement extends WithName("A") with GuaranteeType
  case object GuaranteeWaiverSecured extends WithName("5") with GuaranteeType
  case object IndividualGuaranteeMultiple extends WithName("9") with GuaranteeType
  case object TIR extends WithName("B") with GuaranteeType

  val values: Seq[GuaranteeType] = Seq(
    GuaranteeWaiver,
    ComprehensiveGuarantee,
    IndividualGuarantee,
    FlatRateVoucher,
    CashDepositGuarantee,
    GuaranteeNotRequired,
    GuaranteeWaivedRedirect,
    GuaranteeWaiverByAgreement,
    GuaranteeWaiverSecured,
    IndividualGuaranteeMultiple,
    TIR
  )

  val guaranteeReferenceRoute: Seq[GuaranteeType] = Seq(
    GuaranteeWaiver,
    ComprehensiveGuarantee,
    IndividualGuarantee,
    FlatRateVoucher,
    IndividualGuaranteeMultiple
  )

  val nonGuaranteeReferenceRoute: Seq[GuaranteeType] = Seq(
    CashDepositGuarantee,
    GuaranteeNotRequired,
    GuaranteeWaivedRedirect,
    GuaranteeWaiverByAgreement,
    GuaranteeWaiverSecured,
    TIR
  )

  def getId(gtValue: String): String = gtValue match {
    case "0" => "GuaranteeWaiver"
    case "1" => "ComprehensiveGuarantee"
    case "2" => "IndividualGuarantee"
    case "4" => "FlatRateVoucher"
    case "3" => "CashDepositGuarantee"
    case "7" => "GuaranteeNotRequired"
    case "6" => "GuaranteeWaivedRedirect"
    case "A" => "GuaranteeWaiverByAgreement"
    case "5" => "GuaranteeWaiverSecured"
    case "9" => "IndividualGuaranteeMultiple"
    case "B" => "TIR"
    case _   => gtValue
  }

  def radios(form: Form[_]): Seq[Radios.Item] = {

    val field = form("value")

    val items = Seq(
      Radios.Radio(msg"guaranteeType.GuaranteeWaiver", GuaranteeWaiver.toString),
      Radios.Radio(msg"guaranteeType.ComprehensiveGuarantee", ComprehensiveGuarantee.toString),
      Radios.Radio(msg"guaranteeType.IndividualGuarantee", IndividualGuarantee.toString),
      Radios.Radio(msg"guaranteeType.FlatRateVoucher", FlatRateVoucher.toString),
      Radios.Radio(msg"guaranteeType.CashDepositGuarantee", CashDepositGuarantee.toString),
      Radios.Radio(msg"guaranteeType.GuaranteeNotRequired", GuaranteeNotRequired.toString),
      Radios.Radio(msg"guaranteeType.GuaranteeWaivedRedirect", GuaranteeWaivedRedirect.toString),
      Radios.Radio(msg"guaranteeType.GuaranteeWaiverByAgreement", GuaranteeWaiverByAgreement.toString),
      Radios.Radio(msg"guaranteeType.GuaranteeWaiverSecured", GuaranteeWaiverSecured.toString),
      Radios.Radio(msg"guaranteeType.IndividualGuaranteeMultiple", IndividualGuaranteeMultiple.toString)
    )

    Radios(field, items)
  }

  implicit val enumerable: Enumerable[GuaranteeType] =
    Enumerable(
      values.map(
        v => v.toString -> v
      ): _*
    )
}
