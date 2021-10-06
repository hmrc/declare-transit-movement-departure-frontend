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

import models.Mode._
import play.api.libs.json.{JsString, Writes}
import play.api.mvc.JavascriptLiteral

sealed trait Mode

case object NormalMode extends Mode {

  implicit val writes: Writes[NormalMode.type] = Writes {
    _ => JsString(NORMAL_MODE)
  }
}

case object CheckMode extends Mode {

  implicit val writes: Writes[CheckMode.type] = Writes {
    _ => JsString(CHECK_MODE)
  }
}

object Mode {

  final val NORMAL_MODE = "NormalMode"
  final val CHECK_MODE  = "CheckMode"

  implicit val jsLiteral: JavascriptLiteral[Mode] = new JavascriptLiteral[Mode] {

    override def to(value: Mode): String = value match {
      case NormalMode => s""""$NORMAL_MODE""""
      case CheckMode  => s""""$CHECK_MODE""""
    }
  }

  implicit val writes: Writes[Mode] = Writes {
    case NormalMode => JsString(NORMAL_MODE)
    case CheckMode  => JsString(CHECK_MODE)
  }
}
