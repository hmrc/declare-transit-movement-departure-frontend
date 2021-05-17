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

package forms

import forms.mappings.Mappings
import models.domain.StringFieldRegex.{alphaNumericWithSpaceRegex, stringFieldRegex}
import models.reference.Country
import models.{ConsigneeAddress, CountryList}
import play.api.data.Form
import play.api.data.Forms._
import uk.gov.hmrc.play.mappers.StopOnFirstFail
import javax.inject.Inject

class ConsigneeAddressFormProvider @Inject() extends Mappings {

  def apply(countryList: CountryList, consigneeName: String): Form[ConsigneeAddress] = Form(
    mapping(
      "AddressLine1" -> text("consigneeAddress.error.AddressLine1.required", Seq(consigneeName))
        .verifying(StopOnFirstFail[String](
          maxLength(35, "consigneeAddress.error.AddressLine1.length", consigneeName),
          regexp(stringFieldRegex, "consigneeAddress.error.AddressLine1.invalid", Seq(consigneeName))
        )),
      "AddressLine2" -> text("consigneeAddress.error.AddressLine2.required", Seq(consigneeName))
        .verifying(StopOnFirstFail[String](
          maxLength(35, "consigneeAddress.error.AddressLine2.length", consigneeName),
          regexp(stringFieldRegex, "consigneeAddress.error.AddressLine2.invalid", Seq(consigneeName))
        )),
      "AddressLine3" -> text("consigneeAddress.error.postalCode.required", Seq(consigneeName))
        .verifying(StopOnFirstFail[String](
          maxLength(9, "consigneeAddress.error.postalCode.length", consigneeName),
          regexp(alphaNumericWithSpaceRegex, "consigneeAddress.error.postalCode.invalid", Seq(consigneeName))
        )),
      "country" -> text("consigneeAddress.error.country.required", Seq(consigneeName))
        .transform[Country](value => countryList.fullList.find(_.code.code == value).get, _.code.code)
    )(ConsigneeAddress.apply)(ConsigneeAddress.unapply)
  )
}
