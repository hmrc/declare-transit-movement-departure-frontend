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

package forms.generic

import forms.behaviours.StringFieldBehaviours
import models.CountryList
import models.reference.{Country, CountryCode}
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.FormError

class CountryFormProviderSpec extends StringFieldBehaviours {

  private val messageKeyPrefix       = arbitrary[String].sample.value
  private val countries: CountryList = CountryList(Seq(Country(CountryCode("AD"), "Andorra")))
  private val form                   = new CountryFormProvider()(messageKeyPrefix, countries)

  ".value" - {

    val fieldName   = "value"
    val requiredKey = s"$messageKeyPrefix.error.required"
    val maxLength   = 2

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = stringsWithMaxLength(maxLength)
    )

    behave like mandatoryField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "not bind if country code does not exist in the country list" in {

      val boundForm = form.bind(Map("value" -> "foobar"))
      val field     = boundForm("value")
      field.errors mustNot be(empty)
    }

    "bind a country code which is in the list" in {

      val boundForm = form.bind(Map("value" -> "AD"))
      val field     = boundForm("value")
      field.errors must be(empty)
    }
  }

}
