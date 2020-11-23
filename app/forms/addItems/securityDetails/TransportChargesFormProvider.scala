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

package forms.addItems.securityDetails

import forms.mappings.Mappings
import javax.inject.Inject
import models.reference.MethodOfPayment
import models.{Index, MethodOfPaymentList}
import play.api.data.Form

class TransportChargesFormProvider @Inject() extends Mappings {

  def apply(methodOfPaymentList: MethodOfPaymentList): Form[MethodOfPayment] =
    Form(
      "value" -> text("transportCharges.error.required")
        .verifying("transportCharges.error.required", value => methodOfPaymentList.methodsOfPayment.exists(_.code == value))
        .transform[MethodOfPayment](value => methodOfPaymentList.getMethodOfPayment(value).get, _.code)
    )
}