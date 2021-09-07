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

package forms.addItems

import forms.mappings.Mappings
import play.api.data.Form

import javax.inject.Inject

class HowManyPackagesFormProvider @Inject() extends Mappings {

  def apply(itemIndex: Int): Form[Int] =
    Form(
      "value" -> int(
        "howManyPackages.error.required",
        "howManyPackages.error.wholeNumber",
        "howManyPackages.error.nonNumeric",
        Seq(itemIndex.toString)
      ).verifying(inRange(itemIndex, 0, 9999, "howManyPackages.error.outOfRange"))
    )
}
