/*
 * Copyright 2022 HM Revenue & Customs
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

package models

import base.SpecBase
import generators.Generators
import models.reference.{CountryCode, CustomsOffice}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class CustomsOfficeListSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  "getAll" - {
    "return the full list of customs offices" in {
      forAll(nonEmptyListOf[CustomsOffice](10)) {
        customsOffices =>
          val customsOfficeList = CustomsOfficeList(customsOffices.toList)

          customsOfficeList.getAll must contain theSameElementsAs customsOffices.toList
      }
    }
  }

  "getById" - {
    "return a customs office if it exists" in {
      forAll(nonEmptyListOf[CustomsOffice](10)) {
        customsOffices =>
          val customsOfficeList = CustomsOfficeList(customsOffices.toList)

          val officeId = customsOffices.head.id

          customsOfficeList.getCustomsOffice(officeId).value mustEqual customsOffices.head
      }
    }

    "return a None if it does not exists" in {

      val customsOffices = Seq(
        CustomsOffice("1", "one", CountryCode("GB"), Some("phoneNumber")),
        CustomsOffice("2", "two", CountryCode("GB"), None),
        CustomsOffice("3", "three", CountryCode("FR"), None)
      )

      val customsOfficeList = CustomsOfficeList(customsOffices)

      val officeId: String = "4"

      customsOfficeList.getCustomsOffice(officeId) mustEqual None

    }
  }

  "filter" - {
    "return a list of customs offices without the office with matching id" in {
      val customsOffices = Seq(
        CustomsOffice("1", "one", CountryCode("GB"), Some("phoneNumber")),
        CustomsOffice("2", "two", CountryCode("GB"), None),
        CustomsOffice("3", "three", CountryCode("FR"), None),
        CustomsOffice("4", "four", CountryCode("AD"), Some("phoneNumber")),
        CustomsOffice("5", "five", CountryCode("IT"), None)
      )

      val customsOfficeList = CustomsOfficeList(customsOffices)

      val officeIds = Seq("3")

      val expectedOffices = Seq(
        CustomsOffice("1", "one", CountryCode("GB"), Some("phoneNumber")),
        CustomsOffice("2", "two", CountryCode("GB"), None),
        CustomsOffice("4", "four", CountryCode("AD"), Some("phoneNumber")),
        CustomsOffice("5", "five", CountryCode("IT"), None)
      )

      customsOfficeList.filterNot(officeIds) must contain theSameElementsAs expectedOffices

    }

    "return a list of customs offices without the offices with matching ids" in {
      val customsOffices = Seq(
        CustomsOffice("1", "one", CountryCode("GB"), Some("phoneNumber")),
        CustomsOffice("2", "two", CountryCode("GB"), None),
        CustomsOffice("3", "three", CountryCode("FR"), None),
        CustomsOffice("4", "four", CountryCode("AD"), Some("phoneNumber")),
        CustomsOffice("5", "five", CountryCode("IT"), None)
      )

      val customsOfficeList = CustomsOfficeList(customsOffices)

      val officeIds = Seq("5", "3")

      val expectedOffices = Seq(
        CustomsOffice("1", "one", CountryCode("GB"), Some("phoneNumber")),
        CustomsOffice("2", "two", CountryCode("GB"), None),
        CustomsOffice("4", "four", CountryCode("AD"), Some("phoneNumber"))
      )

      customsOfficeList.filterNot(officeIds) must contain theSameElementsAs expectedOffices

    }

    "return the full list of customs offices when there are no offices with matching id" in {
      val customsOffices = Seq(
        CustomsOffice("1", "one", CountryCode("GB"), Some("phoneNumber")),
        CustomsOffice("2", "two", CountryCode("GB"), None),
        CustomsOffice("3", "three", CountryCode("FR"), None),
        CustomsOffice("4", "four", CountryCode("AD"), Some("phoneNumber")),
        CustomsOffice("5", "five", CountryCode("IT"), None)
      )

      val customsOfficeList = CustomsOfficeList(customsOffices)

      val officeIds = Seq("13")

      customsOfficeList.filterNot(officeIds) must contain theSameElementsAs customsOffices

    }

  }

}
