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

import base.MonocleSpec
import generators.{Generators, ModelGenerators}
import models.reference.{Country, CountryCode}
import models.{CarrierAddress, ConsigneeAddress, ConsignorAddress, PrincipalAddress}
import monocle.law.discipline.PrismTests
import org.scalacheck.{Arbitrary, Cogen, Gen}
import play.api.libs.functional.syntax._

class AddressSpec extends MonocleSpec with Generators with ModelGenerators {
  import MonocleSpec.Implicits._

  implicit lazy val arbitraryAddress: Arbitrary[Address] =
    Arbitrary {
      for {
        l1 <- stringsWithMaxLength(10)
        l2 <- stringsWithMaxLength(10)
        l3 <- stringsWithMaxLength(10)
        pc <- Gen.option(Arbitrary.arbitrary[Country])
      } yield Address(l1, l2, l3, pc)
    }

  implicit val cogenCountryCode: Cogen[CountryCode] =
    Cogen[String].contramap(unlift(CountryCode.unapply))

  implicit val cogenCountry: Cogen[Country] =
    Cogen
      .tuple2[CountryCode, String]
      .contramap(unlift(Country.unapply))

  implicit val cogenAddress: Cogen[Address] =
    Cogen
      .tuple3[String, String, String]
      .contramap[Address] {
        case Address(line1, line2, line3, _) => (line1, line2, line3)
      }

  implicit val cogenPrincipalAddress: Cogen[PrincipalAddress] =
    Cogen
      .tuple3[String, String, String]
      .contramap[PrincipalAddress] {
        case PrincipalAddress(numberAndStreet, town, postcode) => (numberAndStreet, town, postcode)
      }

  implicit val cogenConsigneeAddress: Cogen[ConsigneeAddress] =
    Cogen
      .tuple4[String, String, String, Country]
      .contramap[models.ConsigneeAddress] {
        case ConsigneeAddress(numberAndStreet, town, postcode, country) => (numberAndStreet, town, postcode, country)
      }

  implicit val cogenConsignorAddress: Cogen[ConsignorAddress] =
    Cogen
      .tuple4[String, String, String, Country]
      .contramap[models.ConsignorAddress] {
        case ConsignorAddress(numberAndStreet, town, postcode, country) => (numberAndStreet, town, postcode, country)
      }

  implicit val cogenCarrierAddress: Cogen[CarrierAddress] =
    Cogen
      .tuple4[String, String, String, Country]
      .contramap[models.CarrierAddress] {
        case CarrierAddress(numberAndStreet, town, postcode, country) => (numberAndStreet, town, postcode, country)
      }

  describe("Address") {

    checkAll("Prism from Address to PrincipalAddress", PrismTests(Address.prismAddressToPrincipalAddress))

    checkAll("Prism from Address to ConsigneeAddress", PrismTests(Address.prismAddressToConsigneeAddress))

    checkAll("Prism from Address to ConsignorAddress", PrismTests(Address.prismAddressToConsignorAddress))

    checkAll("Prism from Address to CarrierAddress", PrismTests(Address.prismAddressToCarrierAddress))

  }

}
