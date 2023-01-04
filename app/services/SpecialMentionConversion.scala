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

package services

import cats.data.NonEmptyList
import models.Convert
import models.journeyDomain.{GuaranteeDetails, SpecialMentionDomain}
import models.messages.goodsitem.SpecialMention

private[services] object SpecialMentionConversion
    extends Convert[(Option[NonEmptyList[SpecialMentionDomain]], NonEmptyList[GuaranteeDetails], Int), Seq[SpecialMention]] {

  override def apply(v1: (Option[NonEmptyList[SpecialMentionDomain]], NonEmptyList[GuaranteeDetails], Int)): Seq[SpecialMention] = {

    val (specialMentionDomain, guaranteeDetails, index) = v1

    val specialMentions = specialMentionDomain.map(UserDeclaredSpecialMentionConversion).getOrElse(Seq.empty)

    if (index == 0) {
      SpecialMentionGuaranteeLiabilityConversion(guaranteeDetails) ++ specialMentions
    } else {
      specialMentions
    }
  }
}
