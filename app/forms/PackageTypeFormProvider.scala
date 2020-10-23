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

package forms

import forms.mappings.Mappings
import javax.inject.Inject
import models.{CountryList, PackageTypeList}
import models.reference.{Country, PackageType}
import play.api.data.Form

class PackageTypeFormProvider @Inject() extends Mappings {

  def apply(packageTypes: PackageTypeList): Form[PackageType] =
    Form(
      "value" -> text("packageType.error.required")
        .verifying("packageType.error.required", value => packageTypes.packageTypeList.exists(_.code == value))
        .transform[PackageType](value => packageTypes.packageTypeList.find(_.code == value).get, _.code)
    )
}
