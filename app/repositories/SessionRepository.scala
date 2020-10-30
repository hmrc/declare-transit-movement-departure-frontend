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

import javax.inject.Inject
import models.{EoriNumber, LocalReferenceNumber, UserAnswers}
import play.api.Configuration
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.bson.collection.BSONSerializationPack
import reactivemongo.api.indexes.Index.Aux
import reactivemongo.api.indexes.IndexType
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json.ImplicitBSONHandlers.JsObjectDocumentWriter
import reactivemongo.play.json.collection.JSONCollection
import utils.IndexUtils
import scala.concurrent.{ExecutionContext, Future}

class DefaultSessionRepository @Inject()(
  mongo: ReactiveMongoApi,
  config: Configuration
)(implicit ec: ExecutionContext)
    extends SessionRepository {

  private val collectionName: String = "user-answers"

  private val cacheTtl = config.get[Int]("mongodb.timeToLiveInSeconds")

  private def collection: Future[JSONCollection] =
    mongo.database.map(_.collection[JSONCollection](collectionName))

  private val lastUpdatedIndex: Aux[BSONSerializationPack.type] = IndexUtils.index(
    key     = Seq("lastUpdated" -> IndexType.Ascending),
    name    = Some("user-answers-last-updated-index"),
    options = BSONDocument("expireAfterSeconds" -> cacheTtl)
  )

  val started: Future[Unit] =
    collection
      .flatMap {
        _.indexesManager.ensure(lastUpdatedIndex)
      }
      .map(_ => ())

  override def get(id: LocalReferenceNumber, eoriNumber: EoriNumber): Future[Option[UserAnswers]] =
    collection.flatMap(_.find(Json.obj("_id" -> id.value, "eoriNumber" -> eoriNumber.value), None).one[UserAnswers])

  override def set(userAnswers: UserAnswers): Future[Boolean] = {

    val selector = Json.obj(
      "_id" -> userAnswers.id
    )

    val modifier = Json.obj(
      "$set" -> (userAnswers copy (lastUpdated = LocalDateTime.now))
    )

    collection.flatMap {
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

  val started: Future[Unit]

  def get(id: LocalReferenceNumber, eoriNumber: EoriNumber): Future[Option[UserAnswers]]

  def set(userAnswers: UserAnswers): Future[Boolean]
}
