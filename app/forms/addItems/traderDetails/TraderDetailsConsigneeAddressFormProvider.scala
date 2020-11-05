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
import play.api.data.Form
import play.api.data.Forms.mapping
import models.domain.TraderDomain.Constants._
import models.domain.TraderDomain.inputRegex

class TraderDetailsConsigneeAddressFormProvider @Inject() extends Mappings {

  def apply(consigneeName: String): Form[Address] = Form(
    mapping(
      "buildingAndStreet" -> text(
        "traderDetailsConsigneeAddress.error.required",
        Seq(Address.Constants.Fields.buildingAndStreetName, consigneeName)
      ).verifying(
          maxLength(
            streetAndNumberLength,
            "traderDetailsConsigneeAddress.error.max_length",
            Seq(Address.Constants.Fields.buildingAndStreetName, consigneeName)
          )
        )
        .verifying(
          minLength(
            1,
            "traderDetailsConsigneeAddress.error.empty",
            Seq(Address.Constants.Fields.buildingAndStreetName, consigneeName)
          )
        )
        .verifying(
          regexp(
            inputRegex,
            "traderDetailsConsigneeAddress.error.invalid",
            Seq(Address.Constants.Fields.buildingAndStreetName, consigneeName)
          )
        ),
      "city" -> text("traderDetailsConsigneeAddress.error.required", args = Seq(Address.Constants.Fields.city, consigneeName))
        .verifying(
          maxLength(cityLength, "traderDetailsConsigneeAddress.error.max_length", args = Seq(Address.Constants.Fields.city, consigneeName))
        )
        .verifying(
          minLength(1, "traderDetailsConsigneeAddress.error.empty", Seq(Address.Constants.Fields.city, consigneeName))
        )
        .verifying(
          regexp(
            inputRegex,
            "traderDetailsConsigneeAddress.error.invalid",
            Seq("city", consigneeName)
          )
        ),
      "postcode" -> text("traderDetailsConsigneeAddress.error.postcode.required", args = Seq(consigneeName))
        .verifying(maxLength(postCodeLength, "traderDetailsConsigneeAddress.error.postcode.length", args = Seq(consigneeName)))
        .verifying(minLength(1, "traderDetailsConsigneeAddress.error.empty", args = Seq(Address.Constants.Fields.postcode, consigneeName)))
        .verifying(regexp("[\\sa-zA-Z0-9]*".r, "traderDetailsConsigneeAddress.error.postcode.invalid", args = Seq(consigneeName)))
    )(Address.apply)(Address.unapply)
  )
}
