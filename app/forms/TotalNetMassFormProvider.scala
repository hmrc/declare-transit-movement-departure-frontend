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

package forms

import forms.mappings.Mappings
import models.Index
import models.domain.NetMass.Constants._
import play.api.data.Form
import javax.inject.Inject

class TotalNetMassFormProvider @Inject() extends Mappings {

  def apply(index: Index): Form[String] =
    Form(
      "value" -> text(requiredKeyNetMass, Seq(index.display))
        .verifying(
          StopOnFirstFail[String](
            maxLength(maxLengthNetMass, lengthKeyNetMass, index.display),
            regexp(totalNetMassInvalidCharactersRegex, invalidCharactersKeyNetMass),
            regexp(totalNetMassInvalidFormatRegex, invalidFormatKeyNetMass),
            minGrossMass(0, invalidAmountKeyNetMass, Seq.empty)
          )
        )
    )
}
