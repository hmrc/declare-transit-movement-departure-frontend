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
import models.{ConsignorAddress, CountryList}
import play.api.data.Form
import play.api.data.Forms._
import uk.gov.hmrc.play.mappers.StopOnFirstFail
import javax.inject.Inject

class ConsignorAddressFormProvider @Inject() extends Mappings {

  def apply(countryList: CountryList, consignorName: String): Form[ConsignorAddress] = Form(
    mapping(
      "AddressLine1" -> text("consignorAddress.error.AddressLine1.required", Seq(consignorName))
        .verifying(StopOnFirstFail[String](
          maxLength(35, "consignorAddress.error.AddressLine1.length", consignorName),
          regexp(stringFieldRegex, "consignorAddress.error.AddressLine1.invalid", Seq(consignorName))
        )),
      "AddressLine2" -> text("consignorAddress.error.AddressLine2.required", Seq(consignorName))
        .verifying(StopOnFirstFail[String](
          maxLength(35, "consignorAddress.error.AddressLine2.length", consignorName),
          regexp(stringFieldRegex, "consignorAddress.error.AddressLine2.invalid", Seq(consignorName))
        )),
      "AddressLine3" -> text("consignorAddress.error.postalCode.required", Seq(consignorName))
        .verifying(StopOnFirstFail[String](
          maxLength(9, "consignorAddress.error.postalCode.length", consignorName),
          regexp(alphaNumericWithSpaceRegex, "consignorAddress.error.postalCode.invalid", Seq(consignorName))
        )),
      "country" -> text("consignorAddress.error.country.required", Seq(consignorName))
        .transform[Country](value => countryList.fullList.find(_.code.code == value).get, _.code.code)
    )(ConsignorAddress.apply)(ConsignorAddress.unapply)
  )
}
