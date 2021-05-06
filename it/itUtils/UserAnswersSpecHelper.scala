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

package itUtils

import models.UserAnswers
import pages.QuestionPage
import play.api.libs.json.{JsResultException, Json, Writes}

trait UserAnswersSpecHelper {

  implicit class UserAnswersSpecHelperOps(userAnswers: UserAnswers) {

    import models.RichJsObject

    private def unsafeSetWithOutCleanup[A: Writes](page: QuestionPage[A], value: A): UserAnswers =
      userAnswers.data
        .setObject(page.path, Json.toJson(value))
        .fold(
          errors => throw new JsResultException(errors),
          jsValue => userAnswers.copy(data = jsValue)
        )

    def unsafeSetVal[A: Writes](page: QuestionPage[A])(value: A): UserAnswers =
      unsafeSetWithOutCleanup(page, value)
  }

}
