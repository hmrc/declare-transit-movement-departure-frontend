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

package forms.safetyAndSecurity

import forms.mappings.Mappings
import models.domain.StringFieldRegex.{alphaNumericWithSpaceRegex, stringFieldRegex}
import models.reference.Country
import models.{CarrierAddress, CountryList}
import play.api.data.Form
import play.api.data.Forms.mapping
import uk.gov.hmrc.play.mappers.StopOnFirstFail
import javax.inject.Inject

class CarrierAddressFormProvider @Inject() extends Mappings {

  def apply(countryList: CountryList, carrierName: String): Form[CarrierAddress] = Form(
    mapping(
      "AddressLine1" -> text("carrierAddress.error.AddressLine1.required")
        .verifying(StopOnFirstFail[String](maxLength(35, "carrierAddress.error.AddressLine1.length"),
                                           regexp(stringFieldRegex, "carrierAddress.error.AddressLine1.invalid"))),
      "AddressLine2" -> text("carrierAddress.error.AddressLine2.required")
        .verifying(StopOnFirstFail[String](maxLength(35, "carrierAddress.error.AddressLine2.length"),
                                           regexp(stringFieldRegex, "carrierAddress.error.AddressLine2.invalid"))),
      "AddressLine3" -> text("carrierAddress.postalCode.error.required", Seq(carrierName))
        .verifying(StopOnFirstFail[String](
          maxLength(9, "carrierAddress.postalCode.error.length", carrierName),
          regexp(alphaNumericWithSpaceRegex, "carrierAddress.postalCode.error.invalid", Seq(carrierName))
        )),
      "country" -> text("carrierAddress.error.country.required")
        .verifying("eventCountry.error.required", value => countryList.fullList.exists(_.code.code == value))
        .transform[Country](value => countryList.fullList.find(_.code.code == value).get, _.code.code)
    )(CarrierAddress.apply)(CarrierAddress.unapply)
  )
}
