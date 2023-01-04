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

package forms.addItems.specialMentions

import forms.mappings.Mappings
import models.reference.SpecialMention
import models.{Index, SpecialMentionList}
import play.api.data.Form

import javax.inject.Inject

class SpecialMentionTypeFormProvider @Inject() extends Mappings {

  def apply(specialMentionList: SpecialMentionList, itemIndex: Index): Form[SpecialMention] =
    Form(
      "value" -> text("specialMentionType.error.required", args = Seq(itemIndex.display))
        .verifying("specialMentionType.error.required", value => specialMentionList.list.exists(_.code == value))
        .transform[SpecialMention](value => specialMentionList.getSpecialMention(value).get, _.code)
    )
}
