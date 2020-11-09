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

import forms.behaviours.StringFieldBehaviours
import models.ForeignAddress
import org.scalacheck.Gen
import play.api.data.FormError

class TraderDetailsConsigneeAddressFormProviderSpec extends StringFieldBehaviours {

  val requiredKey   = "traderDetailsConsigneeAddress.error.required"
  val lengthKey     = "traderDetailsConsigneeAddress.error.length"
  val consigneeName = "Test consignee"

  val form = new TraderDetailsConsigneeAddressFormProvider()(consigneeName)

  ".value" - {

    ".buildingAndStreet" - {

      val fieldName   = "buildingAndStreet"
      val requiredKey = "traderDetailsConsigneeAddress.error.required"
      val lengthKey   = "traderDetailsConsigneeAddress.error.max_length"
      val maxLength   = 35

      val validAdressOverLength: Gen[String] = for {
        num  <- Gen.chooseNum[Int](maxLength + 1, maxLength + 5)
        list <- Gen.listOfN(num, Gen.alphaNumChar)
      } yield list.mkString("")

      val args = Seq(ForeignAddress.Constants.Fields.line1, consigneeName)

      behave like fieldThatBindsValidData(
        form,
        fieldName,
        stringsWithMaxLength(maxLength)
      )

      val error = FormError(fieldName, lengthKey, Array(args))

      behave like fieldWithMaxLength(
        form,
        fieldName,
        maxLength   = maxLength,
        lengthError = error,
        validAdressOverLength
      )

      behave like mandatoryField(
        form,
        fieldName,
        requiredError = FormError(fieldName, requiredKey, args)
      )
    }

    ".city" - {

      val fieldName   = "city"
      val requiredKey = "traderDetailsConsigneeAddress.error.required"
      val lengthKey   = "traderDetailsConsigneeAddress.error.max_length"
      val maxLength   = 35

      val validAdressOverLength: Gen[String] = for {
        num  <- Gen.chooseNum[Int](maxLength + 1, maxLength + 5)
        list <- Gen.listOfN(num, Gen.alphaNumChar)
      } yield list.mkString("")

      val args = Seq(ForeignAddress.Constants.Fields.line2, consigneeName)

      behave like fieldThatBindsValidData(
        form,
        fieldName,
        stringsWithMaxLength(maxLength)
      )

      val error = FormError(fieldName, lengthKey, Array(args))

      behave like fieldWithMaxLength(
        form,
        fieldName,
        maxLength   = maxLength,
        lengthError = error,
        validAdressOverLength
      )

      behave like mandatoryField(
        form,
        fieldName,
        requiredError = FormError(fieldName, requiredKey, args)
      )
    }

    ".postcode" - {

      val fieldName   = "postcode"
      val requiredKey = "traderDetailsConsigneeAddress.error.postcode.required"
      val lengthKey   = "traderDetailsConsigneeAddress.error.postcode.length"
      val maxLength   = 9

      val validAdressOverLength: Gen[String] = for {
        num  <- Gen.chooseNum[Int](maxLength + 1, maxLength + 5)
        list <- Gen.listOfN(num, Gen.alphaNumChar)
      } yield list.mkString("")

      behave like fieldThatBindsValidData(
        form,
        fieldName,
        stringsWithMaxLength(maxLength)
      )

      val error = FormError(fieldName, lengthKey, Array(Seq(consigneeName)))

      behave like fieldWithMaxLength(
        form,
        fieldName,
        maxLength   = maxLength,
        lengthError = error,
        validAdressOverLength
      )

      behave like mandatoryField(
        form,
        fieldName,
        requiredError = FormError(fieldName, requiredKey, Seq(consigneeName))
      )
    }
  }
}
