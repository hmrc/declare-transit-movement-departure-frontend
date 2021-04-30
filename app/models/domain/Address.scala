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

package models.domain

import models.reference.Country
import models.{CarrierAddress, ConsigneeAddress, ConsignorAddress, PrincipalAddress}
import monocle.Prism

case class Address(
  line1: String,
  line2: String,
  line3: String,
  country: Option[Country]
)

object Address {

  val prismAddressToPrincipalAddress: Prism[Address, PrincipalAddress] =
    Prism.partial[Address, PrincipalAddress]({
      case Address(numberAndStreet, town, postcode, None) =>
        PrincipalAddress(numberAndStreet, town, postcode)
    })({
      case PrincipalAddress(numberAndStreet, town, postcode) =>
        Address(numberAndStreet, town, postcode, None)
    })

  val prismAddressToConsigneeAddress: Prism[Address, ConsigneeAddress] =
    Prism.partial[Address, ConsigneeAddress]({
      case Address(numberAndStreet, town, postcode, Some(country)) =>
        ConsigneeAddress(numberAndStreet, town, postcode, country)
    })({
      case ConsigneeAddress(numberAndStreet, town, postcode, country) =>
        Address(numberAndStreet, town, postcode, Some(country))
    })

  val prismAddressToConsignorAddress: Prism[Address, ConsignorAddress] =
    Prism.partial[Address, ConsignorAddress]({
      case Address(numberAndStreet, town, postcode, Some(country)) =>
        ConsignorAddress(numberAndStreet, town, postcode, country)
    })({
      case ConsignorAddress(numberAndStreet, town, postcode, country) =>
        Address(numberAndStreet, town, postcode, Some(country))
    })

  val prismAddressToCarrierAddress: Prism[Address, CarrierAddress] =
    Prism.partial[Address, CarrierAddress]({
      case Address(numberAndStreet, town, postcode, Some(country)) =>
        CarrierAddress(numberAndStreet, town, postcode, country)
    })({
      case CarrierAddress(numberAndStreet, town, postcode, country) =>
        Address(numberAndStreet, town, postcode, Some(country))
    })

}
