/*
 * Copyright 2020 HM Revenue & Customs
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

package forms.safetyAndSecurity

import forms.mappings.Mappings
import javax.inject.Inject
import models.CircumstanceIndicatorList
import models.reference.CircumstanceIndicator
import play.api.data.Form

class CircumstanceIndicatorFormProvider @Inject() extends Mappings {

  def apply(indicatorList: CircumstanceIndicatorList): Form[CircumstanceIndicator] =
    Form(
      "value" -> text("circumstanceIndicator.error.required")
        .verifying("circumstanceIndicator.error.required", value => indicatorList.circumstanceIndicators.exists(_.code == value))
        .transform[CircumstanceIndicator](value => indicatorList.circumstanceIndicators.find(_.code == value).get, _.code)
    )
}
