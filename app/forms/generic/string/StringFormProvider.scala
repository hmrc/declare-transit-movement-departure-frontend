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

package forms.generic.string

import forms.mappings.Mappings
import models.Index
import play.api.data.Form

import javax.inject.Inject
import scala.util.matching.Regex

abstract class StringFormProvider @Inject() extends Mappings {

  val maximumLength: Int
  val regex: Regex

  def apply(messageKeyPrefix: String): Form[String] =
    apply(messageKeyPrefix, Nil)

  def apply(messageKeyPrefix: String, index: Index): Form[String] =
    apply(messageKeyPrefix, Seq(index.display))

  def apply(messageKeyPrefix: String, args: Seq[Any]): Form[String] =
    Form(
      "value" -> text(s"$messageKeyPrefix.error.required", args)
        .verifying(
          forms.StopOnFirstFail[String](
            maxLength(maximumLength, s"$messageKeyPrefix.error.length"),
            regexp(regex, s"$messageKeyPrefix.error.invalid")
          )
        )
    )
}
