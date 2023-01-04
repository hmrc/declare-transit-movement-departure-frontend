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

import forms.mappings.StringEquivalence
import models.messages.Seal
import play.api.libs.json.{Json, OFormat}

case class SealDomain(numberOrMark: String)

object SealDomain {

  object Constants {
    val sealNumberOrMarkLength = 20
  }

  def domainSealToSeal(sealDomain: SealDomain): Seal = SealDomain.unapply(sealDomain).map(Seal.apply).get

  implicit val format: OFormat[SealDomain] = Json.format[SealDomain]

  implicit val sealStringEquivalenceCheck: StringEquivalence[SealDomain] =
    StringEquivalence[SealDomain](
      (seal, sealNumberOrMark) => seal.numberOrMark == sealNumberOrMark
    )
}
