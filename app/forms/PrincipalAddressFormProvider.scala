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
import models.PrincipalAddress
import models.PrincipalAddress.Constants.{numberAndStreetLength, postcodeLength, townLength}
import models.domain.StringFieldRegex.{postCodeRegex, stringFieldRegex, validPostcodeCharactersRegex}
import play.api.data.Form
import play.api.data.Forms._
import uk.gov.hmrc.play.mappers.StopOnFirstFail

import javax.inject.Inject

class PrincipalAddressFormProvider @Inject() extends Mappings {

  def apply(principalName: String): Form[PrincipalAddress] = Form(
    mapping(
      "numberAndStreet" -> text("principalAddress.error.numberAndStreet.required", Seq(principalName))
        .verifying(StopOnFirstFail[String](
          regexp(stringFieldRegex, "principalAddress.error.numberAndStreet.invalidCharacters", Seq(principalName)),
          maxLength(numberAndStreetLength, "principalAddress.error.numberAndStreet.length", principalName),
        )),
      "town" -> text("principalAddress.error.town.required", Seq(principalName))
        .verifying(StopOnFirstFail[String](
          regexp(stringFieldRegex, "principalAddress.error.town.invalidCharacters", Seq(principalName)),
          maxLength(townLength, "principalAddress.error.town.length", principalName),
        )),
      "postcode" -> text("principalAddress.error.postcode.required", Seq(principalName))
        .verifying(StopOnFirstFail[String](
          regexp(validPostcodeCharactersRegex, "principalAddress.error.postcode.invalidCharacters", principalName),
          regexp(postCodeRegex, "principalAddress.error.postcode.invalidFormat", principalName),
          maxLength(postcodeLength, "principalAddress.error.postcode.length", principalName),
        ))
    )(PrincipalAddress.apply)(PrincipalAddress.unapply)
  )
}
