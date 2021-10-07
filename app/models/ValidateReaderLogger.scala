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

package models

import models.journeyDomain.UserAnswersReader
import play.api.Logging

trait ValidateReaderLogger extends Logging {

  object ValidateReaderLogger {

    def apply[A: UserAnswersReader](ua: UserAnswers): Unit =
      for (readerError <- UserAnswersReader[A].run(ua).left) {

        val message = readerError.message match {
          case Some(value) => s" with message: $value"
          case None        => ""
        }

        logger.info(s"[AnswersCheck][CYA] Failed on `${readerError.page.path}`" + message)
      }
  }
}
