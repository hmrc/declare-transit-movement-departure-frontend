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

package generators

import models.SpecialMentionList
import models.reference.SpecialMention
import org.scalacheck.Arbitrary

trait ReferenceDataGenerators {
  self: Generators =>

  implicit lazy val arbitraryNonEmptySpecialMentionList: Arbitrary[SpecialMentionList] =
    Arbitrary {
      for {
        specialMentions <- nonEmptyListOf[SpecialMention](10)
      } yield SpecialMentionList(specialMentions.toList)
    }

  implicit lazy val arbitrarySpecialMention: Arbitrary[SpecialMention] =
    Arbitrary {
      for {
        code        <- nonEmptyString
        description <- nonEmptyString
      } yield SpecialMention(code, description)
    }
}
