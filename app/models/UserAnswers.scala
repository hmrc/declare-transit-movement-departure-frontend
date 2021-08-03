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

import derivable.Derivable
import pages._
import play.api.libs.json._
import queries.Gettable

import java.time.LocalDateTime
import scala.util.{Failure, Success, Try}

final case class UserAnswers(
  lrn: LocalReferenceNumber,
  eoriNumber: EoriNumber,
  data: JsObject = Json.obj(),
  lastUpdated: LocalDateTime = LocalDateTime.now,
  id: Id = Id()
) {

  def get[A](gettable: Gettable[A])(implicit rds: Reads[A]): Option[A] =
    Reads.optionNoError(Reads.at(gettable.path)).reads(data).getOrElse(None)

  def get[A, B](derivable: Derivable[A, B])(implicit rds: Reads[A]): Option[B] =
    get(derivable: Gettable[A]).map(derivable.derive)

  def set[A](page: QuestionPage[A], value: A)(implicit writes: Writes[A], reads: Reads[A]): Try[UserAnswers] = {

    lazy val updatedData = data.setObject(page.path, Json.toJson(value)) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(errors) =>
        Failure(JsResultException(errors))
    }

    lazy val cleanup: JsObject => Try[UserAnswers] = d => {
      val updatedAnswers = copy(data = d)
      page.cleanup(Some(value), updatedAnswers)
    }

    get(page) match {
      case Some(`value`) => Success(this)
      case _             => updatedData flatMap cleanup
    }
  }

  def remove[A](page: QuestionPage[A]): Try[UserAnswers] = {

    val updatedData = data.removeObject(page.path) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(_) =>
        Success(data)
    }

    updatedData.flatMap {
      d =>
        val updatedAnswers = copy(data = d)
        page.cleanup(None, updatedAnswers)
    }
  }
}

object UserAnswers {

  implicit lazy val reads: Reads[UserAnswers] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "lrn").read[LocalReferenceNumber] and
        (__ \ "eoriNumber").read[EoriNumber] and
        (__ \ "data").read[JsObject] and
        (__ \ "lastUpdated").read(MongoDateTimeFormats.localDateTimeRead) and
        (__ \ "_id").read[Id]
    )(UserAnswers.apply _)
  }

  implicit lazy val writes: OWrites[UserAnswers] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "lrn").write[LocalReferenceNumber] and
        (__ \ "eoriNumber").write[EoriNumber] and
        (__ \ "data").write[JsObject] and
        (__ \ "lastUpdated").write(MongoDateTimeFormats.localDateTimeWrite) and
        (__ \ "_id").write[Id]
    )(unlift(UserAnswers.unapply))
  }
}
