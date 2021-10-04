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

import cats.data.NonEmptyList
import play.api.libs.json._

package object json {

  implicit object NonEmptyListOps {

    implicit def reads[A: Reads]: Reads[NonEmptyList[A]] =
      Reads
        .of[List[A]]
        .collect(
          JsonValidationError("expected a NonEmptyList but the list was empty")
        ) {
          case head :: tail => NonEmptyList(head, tail)
        }

    implicit def writes[A: Writes]: Writes[NonEmptyList[A]] =
      Writes
        .of[List[A]]
        .contramap(_.toList)

    implicit def format[A: Format]: Format[NonEmptyList[A]] =
      Format(reads, writes)
  }
}
