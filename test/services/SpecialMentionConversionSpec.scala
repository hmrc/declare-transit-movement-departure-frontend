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

package services

import base.{GeneratorSpec, SpecBase}
import cats.data.NonEmptyList
import generators.{JourneyModelGenerators, ModelGenerators}
import models.journeyDomain.SpecialMentionDomain
import models.messages.goodsitem.{SpecialMentionExportFromGB, SpecialMentionNoCountry}

class SpecialMentionConversionSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators with ModelGenerators {

  "SpecialMentionConversion" - {

    "must return a list of SpecialMentionExportFromGB when given a SpecialMentionDomain with a type of DG0 or DG1" in {

      val specialMentionDomain1            = SpecialMentionDomain("DG0", "Additional info")
      val specialMentionDomain2            = SpecialMentionDomain("DG1", "Additional info")
      val specialMentionDomainNonEmptyList = NonEmptyList(specialMentionDomain1, List(specialMentionDomain2))

      val expectedSpecialMention1   = SpecialMentionExportFromGB("DG0", "Additional info")
      val expectedSpecialMention2   = SpecialMentionExportFromGB("DG1", "Additional info")
      val expectedSpecialMentionSeq = Seq(expectedSpecialMention1, expectedSpecialMention2)

      SpecialMentionConversion(specialMentionDomainNonEmptyList) mustBe expectedSpecialMentionSeq

    }

    "must return a list of SpecialMentionNoCountry when given a SpecialMentionDomain with a type that is not DG0 or DG1" in {

      val specialMentionDomain1            = SpecialMentionDomain("ABC", "Additional info")
      val specialMentionDomainNonEmptyList = NonEmptyList(specialMentionDomain1, List.empty)

      val expectedSpecialMention1   = SpecialMentionNoCountry("ABC", "Additional info")
      val expectedSpecialMentionSeq = Seq(expectedSpecialMention1)

      SpecialMentionConversion(specialMentionDomainNonEmptyList) mustBe expectedSpecialMentionSeq
    }

    "must return a list of both SpecialMentionNoCountry and SpecialMentionExportFromGB" in {

      val specialMentionDomain1            = SpecialMentionDomain("DG0", "Additional info")
      val specialMentionDomain2            = SpecialMentionDomain("ABC", "Additional info")
      val specialMentionDomainNonEmptyList = NonEmptyList(specialMentionDomain1, List(specialMentionDomain2))

      val expectedSpecialMention1 = SpecialMentionExportFromGB("DG0", "Additional info")
      val expectedSpecialMention2 = SpecialMentionNoCountry("ABC", "Additional info")

      val expectedSpecialMentionSeq = Seq(expectedSpecialMention1, expectedSpecialMention2)

      SpecialMentionConversion(specialMentionDomainNonEmptyList) mustBe expectedSpecialMentionSeq
    }
  }

}
