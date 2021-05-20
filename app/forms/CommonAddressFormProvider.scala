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
import models.Address.Constants.{buildingAndStreetLength, cityLength, postcodeLength}
import models.domain.StringFieldRegex.stringFieldRegex
import models.reference.Country
import models.{CommonAddress, CountryList}
import play.api.data.Form
import play.api.data.Forms.mapping
import uk.gov.hmrc.play.mappers.StopOnFirstFail

import javax.inject.Inject

class CommonAddressFormProvider @Inject() extends Mappings {

  def apply(countryList: CountryList, name: String): Form[CommonAddress] = Form(
    mapping(
      "AddressLine1" -> text("commonAddress.error.AddressLine1.required", Seq(name))
        .verifying(StopOnFirstFail[String](
          maxLength(buildingAndStreetLength, "commonAddress.error.AddressLine1.length", name),
          regexp(stringFieldRegex, "commonAddress.error.AddressLine1.invalidCharacters", Seq(name))
        )),
      "AddressLine2" -> text("commonAddress.error.AddressLine2.required", Seq(name))
        .verifying(StopOnFirstFail[String](
          maxLength(cityLength, "commonAddress.error.AddressLine2.length", name),
          regexp(stringFieldRegex, "commonAddress.error.AddressLine2.invalidCharacters", Seq(name))
        )),
      "AddressLine3" -> text("commonAddress.error.postalCode.required", Seq(name))
        .verifying(StopOnFirstFail[String](
          maxLength(postcodeLength, "commonAddress.error.postalCode.length", name),
          regexp(stringFieldRegex, "commonAddress.error.postalCode.invalidCharacters", Seq(name))
        )),
      "country" -> text("commonAddress.error.country.required", Seq(name))
        .transform[Country](value => countryList.fullList.find(_.code.code == value).get, _.code.code)
    )(CommonAddress.apply)(CommonAddress.unapply)
  )
}
