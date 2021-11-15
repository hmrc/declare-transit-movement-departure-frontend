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

import models.domain.StringFieldRegex.alphaNumericWithSpaceRegex

import javax.inject.Inject
import scala.util.matching.Regex

class NameFormProvider @Inject() extends StringFormProvider {
  override val maximumLength: Int = 35
  override val regex: Regex       = alphaNumericWithSpaceRegex
}
