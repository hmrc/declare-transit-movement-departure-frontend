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

import play.api.libs.json.{JsString, Writes}

sealed trait Status

object Status {

  case object NotStarted extends Status

  case object InProgress extends Status

  case object Completed extends Status

  case object CannotStartYet extends Status

  implicit val writes: Writes[Status] = Writes[Status] {
    case NotStarted     => JsString("notStarted")
    case InProgress     => JsString("inProgress")
    case Completed      => JsString("completed")
    case CannotStartYet => JsString("cannotStartYet")
  }
}
