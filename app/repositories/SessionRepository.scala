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

package repositories

import java.time.LocalDateTime

import javax.inject.{Inject, Singleton}
import models.{EoriNumber, LocalReferenceNumber, UserAnswers}
import play.api.libs.json._
import reactivemongo.play.json.ImplicitBSONHandlers.JsObjectDocumentWriter
import scala.concurrent.{ExecutionContext, Future}

@Singleton
private[repositories] class DefaultSessionRepository @Inject()(
  sessionCollection: SessionCollection
)(implicit ec: ExecutionContext)
    extends SessionRepository {

  override def get(id: LocalReferenceNumber, eoriNumber: EoriNumber): Future[Option[UserAnswers]] =
    sessionCollection().flatMap(_.find(Json.obj("_id" -> id.value, "eoriNumber" -> eoriNumber.value), None).one[UserAnswers])

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
}

trait SessionRepository {

  def get(id: LocalReferenceNumber, eoriNumber: EoriNumber): Future[Option[UserAnswers]]

  def set(userAnswers: UserAnswers): Future[Boolean]

}
