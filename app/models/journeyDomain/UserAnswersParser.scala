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

package models.journeyDomain

import cats.data._
import models.UserAnswers

trait UserAnswersParser[F[_], A] {

  def run(ua: UserAnswers): F[A]

}

object UserAnswersParser {

  def apply[F[_], A](implicit parser: UserAnswersParser[F, A]): UserAnswersParser[F, A] = parser

}

class UserAnswersOptionalParser[A](reader: ReaderT[Option, UserAnswers, A]) extends UserAnswersParser[Option, A] {
  self =>

  override def run(ua: UserAnswers): Option[A] =
    reader.run(ua)

}

object UserAnswersOptionalParser {

  def apply[A, B](reader: ReaderT[Option, UserAnswers, A])(f: A => B): UserAnswersOptionalParser[B] =
    new UserAnswersOptionalParser(reader.map(f))
}
