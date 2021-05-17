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
import models.{ConsignorAddress, CountryList}
import play.api.data.Form
import play.api.data.Forms.mapping
import uk.gov.hmrc.play.mappers.StopOnFirstFail

import javax.inject.Inject

class SafetyAndSecurityConsignorAddressFormProvider @Inject() extends Mappings {

  val maxLength = 35

  def apply(countryList: CountryList, consignorName: String): Form[ConsignorAddress] = Form(
    mapping(
      "AddressLine1" -> text("safetyAndSecurityConsignorAddress.error.required", Seq(1))
        .verifying(StopOnFirstFail[String](
          regexp(stringFieldRegex, "safetyAndSecurityConsignorAddress.error.invalid", Seq(1)),
          maxLength(maxLength, "safetyAndSecurityConsignorAddress.error.length", 1)
        )),
      "AddressLine2" -> text("safetyAndSecurityConsignorAddress.error.required", Seq(2))
        .verifying(StopOnFirstFail[String](
          regexp(stringFieldRegex, "safetyAndSecurityConsignorAddress.error.invalid", Seq(2)),
          maxLength(maxLength, "safetyAndSecurityConsignorAddress.error.length", 2)
        )),
      "AddressLine3" -> text("safetyAndSecurityConsignorAddress.postalCode.error.required", Seq(consignorName))
        .verifying(StopOnFirstFail[String](
          regexp(alphaNumericWithSpaceRegex, "safetyAndSecurityConsignorAddress.postalCode.error.invalid", Seq(consignorName)),
          maxLength(9, "safetyAndSecurityConsignorAddress.postalCode.error.length", consignorName)
        )),
      "country" -> text("safetyAndSecurityConsignorAddress.error.country.required", Seq(consignorName))
        .verifying("safetyAndSecurityConsignorAddress.error.country.required", value => countryList.fullList.exists(_.code.code == value))
        .transform[Country](value => countryList.fullList.find(_.code.code == value).get, _.code.code)
    )(ConsignorAddress.apply)(ConsignorAddress.unapply)
  )
}
