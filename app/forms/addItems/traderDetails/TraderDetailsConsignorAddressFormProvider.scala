/*
 * Copyright 2020 HM Revenue & Customs
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
import models.Address
import models.domain.TraderDomain.Constants.{cityLength, postCodeLength, streetAndNumberLength}
import models.domain.TraderDomain.inputRegex
import play.api.data.Form
import play.api.data.Forms.mapping

class TraderDetailsConsignorAddressFormProvider @Inject() extends Mappings {

  def apply(consignorName: String): Form[Address] = Form(
    mapping(
      "buildingAndStreet" -> text(
        "traderDetailsConsignorAddress.error.required",
        Seq(Address.Constants.Fields.buildingAndStreetName, consignorName)
      ).verifying(
          maxLength(
            streetAndNumberLength,
            "traderDetailsConsignorAddress.error.max_length",
            Seq(Address.Constants.Fields.buildingAndStreetName, consignorName)
          )
        )
        .verifying(
          minLength(
            1,
            "traderDetailsConsignorAddress.error.empty",
            Seq(Address.Constants.Fields.buildingAndStreetName, consignorName)
          )
        )
        .verifying(
          regexp(
            inputRegex,
            "traderDetailsConsignorAddress.error.invalid",
            Seq(Address.Constants.Fields.buildingAndStreetName, consignorName)
          )
        ),
      "city" -> text("traderDetailsConsignorAddress.error.required", args = Seq(Address.Constants.Fields.city, consignorName))
        .verifying(
          maxLength(cityLength, "traderDetailsConsignorAddress.error.max_length", args = Seq(Address.Constants.Fields.city, consignorName))
        )
        .verifying(
          minLength(1, "traderDetailsConsignorAddress.error.empty", Seq(Address.Constants.Fields.city, consignorName))
        )
        .verifying(
          regexp(
            inputRegex,
            "traderDetailsConsignorAddress.error.invalid",
            Seq("city", consignorName)
          )
        ),
      "postcode" -> text("traderDetailsConsignorAddress.error.postcode.required", args = Seq(consignorName))
        .verifying(maxLength(postCodeLength, "traderDetailsConsignorAddress.error.postcode.length", args = Seq(consignorName)))
        .verifying(minLength(1, "traderDetailsConsignorAddress.error.empty", args = Seq(Address.Constants.Fields.postcode, consignorName)))
        .verifying(regexp("[\\sa-zA-Z0-9]*".r, "traderDetailsConsignorAddress.error.postcode.invalid", args = Seq(consignorName)))
    )(Address.apply)(Address.unapply)
  )
}
