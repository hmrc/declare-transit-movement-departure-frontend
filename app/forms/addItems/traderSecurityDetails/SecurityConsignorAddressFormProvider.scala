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

package forms.addItems.traderSecurityDetails

import forms.Constants.{addressMaxLength, consigneeNameMaxLength}
import forms.mappings.Mappings
import models.domain.StringFieldRegex.{alphaNumericWithSpaceRegex, stringFieldRegex}
import models.reference.Country
import models.{ConsignorAddress, CountryList, Index}
import play.api.data.Form
import play.api.data.Forms.mapping
import uk.gov.hmrc.play.mappers.StopOnFirstFail
import javax.inject.Inject

class SecurityConsignorAddressFormProvider @Inject() extends Mappings {

  def apply(countryList: CountryList, consignorName: String, index: Index): Form[ConsignorAddress] = Form(
    mapping(
      "AddressLine1" -> text("securityConsignorAddress.error.AddressLine1.required", Seq(consignorName))
        .verifying(StopOnFirstFail[String](
          regexp(stringFieldRegex, "securityConsignorAddress.error.AddressLine1.invalid", Seq(consignorName)),
          maxLength(addressMaxLength, "securityConsignorAddress.error.AddressLine1.length", consignorName)
        )),
      "AddressLine2" -> text("securityConsignorAddress.error.AddressLine2.required", Seq(consignorName))
        .verifying(StopOnFirstFail[String](
          regexp(stringFieldRegex, "securityConsignorAddress.error.AddressLine2.invalid", Seq(consignorName)),
          maxLength(addressMaxLength, "securityConsignorAddress.error.AddressLine2.length", consignorName)
        )),
      "AddressLine3" -> text("securityConsignorAddress.error.postalCode.required", Seq(consignorName, index.display))
        .verifying(StopOnFirstFail[String](
          regexp(alphaNumericWithSpaceRegex, "securityConsignorAddress.error.postalCode.invalid", Seq(consignorName, index.display)),
          maxLength(9, "securityConsignorAddress.error.postalCode.length", consignorName, index.display)
        )),
      "country" -> text("securityConsignorAddress.error.country.required", Seq(consignorName))
        .transform[Country](value => countryList.fullList.find(_.code.code == value).get, _.code.code)
    )(ConsignorAddress.apply)(ConsignorAddress.unapply)
  )
}
