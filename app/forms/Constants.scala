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

package forms

import models.{Index, Mode, UserAnswers}
import pages.safetyAndSecurity.CircumstanceIndicatorPage

object Constants {

  lazy val maxLengthEoriNumber: Int    = 17
  lazy val vehicleIdMaxLength          = 27
  lazy val consigneeNameMaxLength: Int = 35
  lazy val addressMaxLength: Int       = 35
  lazy val loadingPlaceMaxLength: Int  = 35
  lazy val addressRegex: String        = "^[a-zA-Z0-9/@?%,.\\- ]*$"

  def circumstanceIndicatorCheck(ua: UserAnswers, index: Index, mode: Mode) =
    ua.get(CircumstanceIndicatorPage) match {
      case Some("E") => controllers.addItems.traderSecurityDetails.routes.SecurityConsigneeEoriController.onPageLoad(ua.id, index, mode)
      case _         => controllers.addItems.traderSecurityDetails.routes.AddSecurityConsigneesEoriController.onPageLoad(ua.id, index, mode)
    }
}
