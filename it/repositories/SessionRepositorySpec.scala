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

import itSpecBase.ItSpecBase
import itUtils.MockDateTimeService
import models.{EoriNumber, LocalReferenceNumber, UserAnswers}
import org.scalatest.concurrent.IntegrationPatience
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.Json
import reactivemongo.api.indexes.IndexType
import reactivemongo.core.errors.DatabaseException
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global

class SessionRepositorySpec extends ItSpecBase
  with ItMongoSuite
  with BeforeAndAfterEach
  with GuiceOneAppPerSuite
  with MockDateTimeService
  with FailOnUnindexedQueries
  with OptionValues
  with IntegrationPatience {

  private val service = app.injector.instanceOf[SessionRepository]

  private val userAnswer1 = UserAnswers(LocalReferenceNumber("ABCD1111111111111").get, EoriNumber("EoriNumber1"), Json.obj("foo" -> "bar"))
  private val userAnswer2 = UserAnswers(LocalReferenceNumber("ABCD2222222222222").get, EoriNumber("EoriNumber2"), Json.obj("bar" -> "foo"))

  val eoriIndex = SimpleMongoIndexConfig(
    key = Seq("eoriNumber" -> IndexType.Ascending, "lrn" -> IndexType.Ascending),
    name = Some("eoriNumber-lrn-index")
  )

  override def beforeEach(): Unit = {
    super.beforeEach()
    database.flatMap {
      db =>
        val jsonCollection = db.collection[JSONCollection]("user-answers")
        jsonCollection.indexesManager.create(eoriIndex).flatMap {
          _ =>
            jsonCollection
              .insert(ordered = false)
              .many(Seq(userAnswer1, userAnswer2))
        }
    }.futureValue
  }

  override def afterEach(): Unit = {
    super.afterEach()
    database.flatMap(_.drop())
  }

  "SessionRepository" - {

    "get" - {

      "must return UserAnswers when given an LocalReferenceNumber and EoriNumber" in {

        val result = service.get(LocalReferenceNumber("ABCD1111111111111").get, EoriNumber("EoriNumber1")).futureValue

        result.value.lrn        mustBe userAnswer1.lrn
        result.value.eoriNumber mustBe userAnswer1.eoriNumber
        result.value.data       mustBe userAnswer1.data
      }

      "must return None when no UserAnswers match LocalReferenceNumber" in {

        val result = service.get(LocalReferenceNumber("ABCD3333333333333").get, EoriNumber("EoriNumber1")).futureValue

        result mustBe None
      }

      "must return None when UserAnswers match EoriNumber" in {

        val result = service.get(LocalReferenceNumber("ABCD1111111111111").get, EoriNumber("InvalidEori")).futureValue

        result mustBe None
      }
    }

    "set" - {

      "must create new document when given valid UserAnswers" in {

        val userAnswer = UserAnswers(LocalReferenceNumber("ABCD3333333333333").get, EoriNumber("EoriNumber3"), Json.obj("foo" -> "bar"))

        val setResult = service.set(userAnswer).futureValue

        val getResult = service.get(LocalReferenceNumber("ABCD3333333333333").get, EoriNumber("EoriNumber3")).futureValue.value


        setResult            mustBe true
        getResult.lrn        mustBe userAnswer.lrn
        getResult.eoriNumber mustBe userAnswer.eoriNumber
        getResult.data       mustBe userAnswer.data
      }

      "must fail when attempting to create using an existing id" in {

        val userAnswer = UserAnswers(LocalReferenceNumber("ABCD1111111111111").get, EoriNumber("EoriNumber1"), Json.obj("foo" -> "bar"))

        val setResult = service.set(userAnswer)

        whenReady(setResult.failed) {
          e =>
            e mustBe an[DatabaseException]
        }
      }
    }

    "remove" - {

      "must remove document when given a valid LocalReferenceNumber and EoriNumber" in {

        service.get(LocalReferenceNumber("ABCD1111111111111").get, EoriNumber("EoriNumber1")).futureValue mustBe defined

        service.remove(LocalReferenceNumber("ABCD1111111111111").get, EoriNumber("EoriNumber1")).futureValue

        service.get(LocalReferenceNumber("ABCD1111111111111").get, EoriNumber("EoriNumber1")).futureValue must not be defined
      }
    }
  }
}
