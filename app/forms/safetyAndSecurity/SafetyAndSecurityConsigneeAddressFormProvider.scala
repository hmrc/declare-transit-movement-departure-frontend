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
import models.{ConsigneeAddress, CountryList}
import play.api.data.Form
import play.api.data.Forms.mapping
import uk.gov.hmrc.play.mappers.StopOnFirstFail
import javax.inject.Inject

class SafetyAndSecurityConsigneeAddressFormProvider @Inject() extends Mappings {

  val maxLength = 35

  def apply(countryList: CountryList, consigneeName: String): Form[ConsigneeAddress] = Form(
    mapping(
      "AddressLine1" -> text("safetyAndSecurityConsigneeAddress.error.required", Seq(1))
        .verifying(StopOnFirstFail[String](
          maxLength(maxLength, "safetyAndSecurityConsigneeAddress.error.length", 1),
          regexp(stringFieldRegex, "safetyAndSecurityConsigneeAddress.error.invalid", Seq(1))
        )),
      "AddressLine2" -> text("safetyAndSecurityConsigneeAddress.error.required", Seq(2))
        .verifying(StopOnFirstFail[String](
          maxLength(maxLength, "safetyAndSecurityConsigneeAddress.error.length", 2),
          regexp(stringFieldRegex, "safetyAndSecurityConsigneeAddress.error.invalid", Seq(2))
        )),
      "AddressLine3" -> text("safetyAndSecurityConsigneeAddress.postalCode.error.required", Seq(consigneeName))
        .verifying(StopOnFirstFail[String](
          maxLength(9, "safetyAndSecurityConsigneeAddress.postalCode.error.length", consigneeName),
          regexp(alphaNumericWithSpaceRegex, "safetyAndSecurityConsigneeAddress.postalCode.error.invalid", Seq(consigneeName))
        )),
      "country" -> text("safetyAndSecurityConsigneeAddress.error.country.required", Seq(consigneeName))
        .verifying("safetyAndSecurityConsigneeAddress.error.country.required", value => countryList.fullList.exists(_.code.code == value))
        .transform[Country](value => countryList.fullList.find(_.code.code == value).get, _.code.code)
    )(ConsigneeAddress.apply)(ConsigneeAddress.unapply)
  )
}
