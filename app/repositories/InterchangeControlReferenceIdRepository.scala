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

package repositories

import com.google.inject.{Inject, Singleton}
import com.mongodb.client.model.ReturnDocument
import models.messages.InterchangeControlReference
import org.mongodb.scala.model.{Filters, FindOneAndUpdateOptions, Updates}
import play.api.Logging
import services.DateTimeService
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class InterchangeControlReferenceIdRepository @Inject() (
  mongoComponent: MongoComponent,
  dateTimeService: DateTimeService
)(implicit ec: ExecutionContext)
    extends PlayMongoRepository[InterchangeControlReference](
      mongoComponent = mongoComponent,
      collectionName = InterchangeControlReferenceIdRepository.collectionName,
      domainFormat = InterchangeControlReference.format,
      indexes = Nil
    )
    with Logging {

  def nextInterchangeControlReferenceId(): Future[InterchangeControlReference] = {
    val date   = dateTimeService.dateFormatted
    val filter = Filters.eq("_id", date)

    val update = Updates.inc("last-index", 1)

    val options = FindOneAndUpdateOptions()
      .upsert(true)
      .returnDocument(ReturnDocument.AFTER)

    collection
      .findOneAndUpdate(filter, update, options)
      .toFuture()
      .recover {
        case e: Throwable =>
          logger.error(s"Unable to generate InterchangeControlReferenceId for: $date", e)
          throw e
      }
  }
}

object InterchangeControlReferenceIdRepository {
  val collectionName: String = "interchange-control-reference-ids"
}
