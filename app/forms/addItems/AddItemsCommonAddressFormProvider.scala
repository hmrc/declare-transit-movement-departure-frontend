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

package forms.addItems
import forms.mappings.Mappings
import models.domain.StringFieldRegex.{alphaNumericWithSpaceRegex, stringFieldRegex}
import models.reference.Country
import models.{CommonAddress, CountryList, Index}
import play.api.data.Form
import play.api.data.Forms.mapping
import uk.gov.hmrc.play.mappers.StopOnFirstFail

import javax.inject.Inject

class AddItemsCommonAddressFormProvider @Inject() extends Mappings {

  def apply(countryList: CountryList, name: String, index: Index): Form[CommonAddress] = Form(
    mapping(
      "AddressLine1" -> text("commonAddress.error.AddressLine1.required", Seq(name))
        .verifying(
          StopOnFirstFail[String](
            regexp(stringFieldRegex, "commonAddress.error.AddressLine1.invalidCharacters", Seq(name)),
            maxLength(35, "commonAddress.error.AddressLine1.length", name)
          )),
      "AddressLine2" -> text("commonAddress.error.AddressLine2.required", Seq(name))
        .verifying(
          StopOnFirstFail[String](
            regexp(stringFieldRegex, "commonAddress.error.AddressLine2.invalidCharacters", Seq(name)),
            maxLength(35, "commonAddress.error.AddressLine2.length", name)
          )),
      "AddressLine3" -> text("commonAddress.error.postalCode.required", Seq(name, index.display))
        .verifying(StopOnFirstFail[String](
          regexp(alphaNumericWithSpaceRegex, "commonAddress.error.postalCode.invalidCharacters", Seq(name, index.display)),
          maxLength(9, "commonAddress.error.postalCode.length", name, index.display)
        )),
      "country" -> text("commonAddress.error.country.required", Seq(name))
        .transform[Country](value => countryList.fullList.find(_.code.code == value).get, _.code.code)
    )(CommonAddress.apply)(CommonAddress.unapply)
  )
}
