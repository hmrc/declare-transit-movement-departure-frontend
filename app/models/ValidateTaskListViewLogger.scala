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

package models

import models.journeyDomain.ReaderError
import play.api.Logging

trait ValidateTaskListViewLogger extends Logging {

  object ValidateTaskListViewLogger {

    def apply(readerErrors: Seq[(String, ReaderError)]): Unit = {
      val logMessage = readerErrors.foldLeft("[AnswersCheck][TaskList] ") {
        case (message, (sectionName, ReaderError(page, optionalMessage))) =>
          val optionalErrorMessage = optionalMessage match {
            case Some(value) => s" with message: $value"
            case None        => ""
          }

          message + s"$sectionName: Failed on '$page'$optionalErrorMessage; "
      }

      if (readerErrors.nonEmpty) logger.info(logMessage)
    }
  }
}
