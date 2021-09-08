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

import logging.Logging
import play.api.Configuration
import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.indexes.IndexType

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
private[repositories] class SessionCollectionIndexManagerImpl @Inject() (
  sessionCollection: SessionCollection,
  config: Configuration
)(implicit ec: ExecutionContext)
    extends SessionCollectionIndexManager
    with Logging {

  private val cacheTtl = config.get[Int]("mongodb.timeToLiveInSeconds")

  private val lastUpdatedIndex = SimpleMongoIndexConfig(
    key = Seq("lastUpdated" -> IndexType.Ascending),
    name = Some("user-answers-last-updated-index"),
    options = BSONDocument("expireAfterSeconds" -> cacheTtl)
  )

  private val eoriIndex = SimpleMongoIndexConfig(
    key = Seq("eoriNumber" -> IndexType.Ascending, "lrn" -> IndexType.Ascending),
    name = Some("eoriNumber-lrn-index")
  )

  val started: Future[Unit] =
    (for {
      collection             <- sessionCollection()
      lastUpdatedIndexResult <- collection.indexesManager.ensure(lastUpdatedIndex)
      eoriIndexResult        <- collection.indexesManager.ensure(eoriIndex)
    } yield {
      logger.info(IndexLogMessages.indexManagerResultLogMessage(sessionCollection.collectionName, lastUpdatedIndex.name.get, lastUpdatedIndexResult))
      logger.info(IndexLogMessages.indexManagerResultLogMessage(sessionCollection.collectionName, eoriIndex.name.get, eoriIndexResult))
    }).map(
      _ => ()
    ).recover {
      case e: Throwable =>
        val message = IndexLogMessages.indexManagerFailedKey(sessionCollection.collectionName) + " failed with exception"
        logger.error(message, e)
        throw e
    }
}

private[repositories] trait SessionCollectionIndexManager {

  def started: Future[Unit]

}
