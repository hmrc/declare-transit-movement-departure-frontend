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
import javax.inject.Inject
import models.domain.StringFieldRegex.{alphaNumericWithSpaceRegex, stringFieldRegex}
import models.reference.Country
import models.{ConsignorAddress, CountryList, Index}
import play.api.data.Form
import play.api.data.Forms.mapping
import uk.gov.hmrc.play.mappers.StopOnFirstFail

class TraderDetailsConsignorAddressFormProvider @Inject() extends Mappings {

  def apply(countryList: CountryList, consigneeName: String, index: Index): Form[ConsignorAddress] = Form(
    mapping(
      "AddressLine1" -> text("traderDetailsConsignorAddress.error.AddressLine1.required")
        .verifying(StopOnFirstFail[String](
          maxLength(35, "traderDetailsConsignorAddress.error.AddressLine1.length"),
          regexp(stringFieldRegex, "traderDetailsConsignorAddress.error.AddressLine1.invalid")
        )),
      "AddressLine2" -> text("traderDetailsConsignorAddress.error.AddressLine2.required")
        .verifying(StopOnFirstFail[String](
          maxLength(35, "traderDetailsConsignorAddress.error.AddressLine2.length"),
          regexp(stringFieldRegex, "traderDetailsConsignorAddress.error.AddressLine2.invalid")
        )),
      "AddressLine3" -> text("traderDetailsConsignorAddress.error.postalCode.required", Seq(consigneeName, index.position))
        .verifying(StopOnFirstFail[String](
          maxLength(9, "traderDetailsConsignorAddress.error.postalCode.length", consigneeName, index.position),
          regexp(alphaNumericWithSpaceRegex, "traderDetailsConsignorAddress.error.postalCode.invalid", Seq(consigneeName, index.position))
        )),
      "country" -> text("traderDetailsConsignorAddress.error.country.required")
        .verifying("eventCountry.error.required", value => countryList.fullList.exists(_.code.code == value))
        .transform[Country](value => countryList.fullList.find(_.code.code == value).get, _.code.code)
    )(ConsignorAddress.apply)(ConsignorAddress.unapply)
  )
}
