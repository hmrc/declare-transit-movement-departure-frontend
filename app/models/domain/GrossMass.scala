/*
 * Copyright 2023 HM Revenue & Customs
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

case class GrossMass(grossMass: String)

object GrossMass {

  object Constants {

    val requiredKeyGrossMass                     = "itemTotalGrossMass.error.required"
    val lengthKeyGrossMass                       = "itemTotalGrossMass.error.length"
    val invalidCharactersKeyGrossMass            = "itemTotalGrossMass.error.invalidCharacters"
    val invalidFormatKeyGrossMass                = "itemTotalGrossMass.error.invalidFormat"
    val invalidAmountKeyGrossMass                = "itemTotalGrossMass.error.minimum"
    val maxLengthGrossMass                       = 15
    val totalGrossMassInvalidCharactersRegex     = "^[0-9.]*$"
    val totalGrossMassInvalidFormatRegex: String = "^[0-9]{1,11}(?:\\.[0-9]{1,3})?$"
    val requiredKeyTotalGrossMass                = "totalGrossMass.error.required"
    val lengthKeyTotalGrossMass                  = "totalGrossMass.error.length"
    val invalidCharactersTotalGrossMass          = "totalGrossMass.error.invalidCharacters"
    val minLengthTotalGrossMass                  = "totalGrossMass.error.minimum"
  }

}
