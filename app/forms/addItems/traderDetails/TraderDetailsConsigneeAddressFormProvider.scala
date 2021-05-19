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

package forms.addItems.traderDetails

import forms.mappings.Mappings
import models.domain.StringFieldRegex.{alphaNumericWithSpaceRegex, stringFieldRegex}
import models.reference.Country
import models.{ConsigneeAddress, CountryList, Index}
import play.api.data.Form
import play.api.data.Forms.mapping
import uk.gov.hmrc.play.mappers.StopOnFirstFail
import javax.inject.Inject

class TraderDetailsConsigneeAddressFormProvider @Inject() extends Mappings {

  def apply(countryList: CountryList, consigneeName: String, index: Index): Form[ConsigneeAddress] = Form(
    mapping(
      "AddressLine1" -> text("traderDetailsConsigneeAddress.error.AddressLine1.required", Seq(consigneeName))
        .verifying(StopOnFirstFail[String](
          maxLength(35, "traderDetailsConsigneeAddress.error.AddressLine1.length", consigneeName),
          regexp(stringFieldRegex, "traderDetailsConsigneeAddress.error.AddressLine1.invalid", Seq(consigneeName))
        )),
      "AddressLine2" -> text("traderDetailsConsigneeAddress.error.AddressLine2.required", Seq(consigneeName))
        .verifying(StopOnFirstFail[String](
          maxLength(35, "traderDetailsConsigneeAddress.error.AddressLine2.length", consigneeName),
          regexp(stringFieldRegex, "traderDetailsConsigneeAddress.error.AddressLine2.invalid", Seq(consigneeName))
        )),
      "AddressLine3" -> text("traderDetailsConsigneeAddress.error.postalCode.required", Seq(consigneeName, index.display))
        .verifying(StopOnFirstFail[String](
          maxLength(9, "traderDetailsConsigneeAddress.error.postalCode.length", consigneeName, index.display),
          regexp(alphaNumericWithSpaceRegex, "traderDetailsConsigneeAddress.error.postalCode.invalid", Seq(consigneeName, index.display))
        )),
      "country" -> text("traderDetailsConsigneeAddress.error.country.required", Seq(consigneeName))
        .transform[Country](value => countryList.fullList.find(_.code.code == value).get, _.code.code)
    )(ConsigneeAddress.apply)(ConsigneeAddress.unapply)
  )
}
