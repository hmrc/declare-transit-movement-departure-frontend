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

package repositories

import java.time.LocalDateTime

import javax.inject.{Inject, Singleton}
import models.{EoriNumber, LocalReferenceNumber, MongoDateTimeFormats, UserAnswers}
import play.api.libs.json._
import reactivemongo.play.json.ImplicitBSONHandlers.JsObjectDocumentWriter
import scala.concurrent.{ExecutionContext, Future}

@Singleton
private[repositories] class DefaultSessionRepository @Inject() (
  sessionCollection: SessionCollection
)(implicit ec: ExecutionContext)
    extends SessionRepository {

  override def get(id: LocalReferenceNumber, eoriNumber: EoriNumber): Future[Option[UserAnswers]] = {
    implicit val dateWriter: Writes[LocalDateTime] = MongoDateTimeFormats.localDateTimeWrite
    val selector = Json.obj(
      "_id"        -> id.value,
      "eoriNumber" -> eoriNumber.value
    )

    val modifier = Json.obj(
      "$set" -> Json.obj("lastUpdated" -> LocalDateTime.now)
    )

    sessionCollection().flatMap {
      _.findAndUpdate(selector, modifier)
        .map(_.value.map(_.as[UserAnswers]))
    }
  }

  override def set(userAnswers: UserAnswers): Future[Boolean] = {

    val selector = Json.obj(
      "_id" -> userAnswers.id
    )

    val modifier = Json.obj(
      "$set" -> (userAnswers copy (lastUpdated = LocalDateTime.now))
    )

    sessionCollection().flatMap {
      _.update(ordered = false)
        .one(selector, modifier, upsert = true)
        .map {
          lastError =>
            lastError.ok
        }
    }
  }

  override def remove(id: LocalReferenceNumber, eoriNumber: EoriNumber): Future[Unit] = sessionCollection().flatMap {
    _.findAndRemove(Json.obj("_id" -> id.toString, "eoriNumber" -> eoriNumber.value))
      .map(
        _ => ()
      )
  }

}

trait SessionRepository {

  def get(id: LocalReferenceNumber, eoriNumber: EoriNumber): Future[Option[UserAnswers]]

  def set(userAnswers: UserAnswers): Future[Boolean]

  def remove(id: LocalReferenceNumber, eoriNumber: EoriNumber): Future[Unit]

}
