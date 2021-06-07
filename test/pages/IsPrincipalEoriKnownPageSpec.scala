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

package pages

import models.ProcedureType.Normal
import models.{PrincipalAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class IsPrincipalEoriKnownPageSpec extends PageBehaviours {

  "IsPrincipalEoriKnownPage" - {

    beRetrievable[Boolean](IsPrincipalEoriKnownPage)

    beSettable[Boolean](IsPrincipalEoriKnownPage)

    //   beRemovable[Boolean](IsPrincipalEoriKnownPage)
  }

  "cleanup" - {

    "must remove PrincipalAddressPage and PrincipalNamePage when there is a change of the answer to 'Yes'" in {

      val principalAddress = arbitrary[PrincipalAddress].sample.value
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val result = userAnswers
            .set(ProcedureTypePage, Normal)
            .success
            .value
            .set(WhatIsPrincipalEoriPage, "GB223445")
            .success
            .value
            .set(PrincipalNamePage, "answer")
            .success
            .value
            .set(PrincipalAddressPage, principalAddress)
            .success
            .value
            .set(IsPrincipalEoriKnownPage, true)
            .success
            .value

          result.get(PrincipalNamePage) must not be defined
          result.get(PrincipalAddressPage) must not be defined
      }
    }

    "must remove PrincipalEoriPage when there is a change of the answer to 'No'" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val result = userAnswers
            .set(ProcedureTypePage, Normal)
            .success
            .value
            .set(WhatIsPrincipalEoriPage, "GB123456")
            .success
            .value
            .set(IsPrincipalEoriKnownPage, false)
            .success
            .value

          result.get(WhatIsPrincipalEoriPage) must not be defined
      }
    }
  }

}
