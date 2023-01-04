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

package models.journeyDomain

import base.SpecBase
import cats.data.NonEmptyList
import models.UserAnswers
import play.api.libs.json._
import queries.Gettable

class UserAnswersReaderSpec extends SpecBase {

  case class TestData(field1: Int, field2: String)

  implicit val jsonReads: Reads[TestData] = Json.reads[TestData]

  val passingGettable1: Gettable[Int] = new Gettable[Int] {
    override def path: JsPath = __ \ "passingGettable1Path"
  }

  val passingGettable2: Gettable[TestData] = new Gettable[TestData] {
    override def path: JsPath = __ \ "passingGettable2Path"
  }

  val passingGettable3: Gettable[List[String]] = new Gettable[List[String]] {
    override def path: JsPath = __ \ "passingGettable3Path"
  }

  val failingListGettable: Gettable[List[String]] = new Gettable[List[String]] {
    override def path: JsPath = __ \ "failingGettablePath"
  }

  val failingGettable: Gettable[Int] = new Gettable[Int] {
    override def path: JsPath = __ \ "failingGettablePath"
  }

  val testData = UserAnswers(
    lrn,
    eoriNumber,
    Json.obj(
      "passingGettable1Path" -> 1,
      "passingGettable2Path" -> Json.obj(
        "field1" -> 1,
        "field2" -> "asdf"
      ),
      "passingGettable3Path" -> Json.arr(
        "listEntry1",
        "listEntry2",
        "listEntry3"
      )
    )
  )

  val testDataWithEmptyList = UserAnswers(
    lrn,
    eoriNumber,
    Json.obj(
      "passingGettable3Path" -> JsArray.empty
    )
  )

  "reader" - {
    "when a reader for a gettable is run" - {
      "passes and reads data when present" in {
        passingGettable1.reader.run(testData).value mustEqual 1
      }

      "fails when not present" in {
        failingGettable.reader.run(testData).isLeft mustBe true
      }

    }
  }

  "optionalReader" - {
    "when a reader for a gettable" - {
      "passes when the data is defined" in {
        passingGettable1.optionalReader.run(testData).value mustBe Some(1)
      }

      "passes when the data is not defined" in {
        failingGettable.optionalReader.run(testData).value mustBe None
      }
    }
  }

  "filterOptionalDependent" - {
    "when the first reader passes" - {
      "and the second reader has data that is defined, then the full reader passes" in {
        val testReaders = passingGettable1.filterOptionalDependent(_ == 1) {
          passingGettable2.reader
        }

        val result = testReaders.run(testData)

        result.value mustBe Some(TestData(1, "asdf"))
      }

      "and the second reader has data that is missing, then the full reader fails" in {
        val testReaders = passingGettable1.filterOptionalDependent(_ == 1) {
          failingGettable.reader
        }

        val result = testReaders.run(testData)

        result.isLeft mustBe true
      }
    }

    "when the first reader fails" - {
      "then the full reader fails" in {
        val testReaders = failingGettable.filterOptionalDependent(_ == 1) {
          passingGettable2.reader
        }

        val result = testReaders.run(testData)

        result.isLeft mustBe true
      }

      "when the full reader fails due to not matching predicate" in {
        val testReaders = passingGettable1.filterOptionalDependent(_ == 2) {
          passingGettable2.reader
        }

        val result = testReaders.run(testData)

        result.value mustBe None
      }
    }
  }

  "filterMandatoryDependent" - {
    "when the first reader passes" - {
      "and the second reader has data that is defined, then the full reader passes" in {
        val testReaders = passingGettable1.filterMandatoryDependent(_ == 1) {
          passingGettable2.reader
        }

        val result = testReaders.run(testData)

        result.value mustEqual TestData(1, "asdf")
      }

      "and the second reader has data that is missing, then the full reader fails" in {
        val testReaders = passingGettable1.filterMandatoryDependent(_ == 1) {
          failingGettable.reader
        }

        val result = testReaders.run(testData)

        result.isLeft mustBe true
      }
    }

    "when the first reader fails" - {
      "then the full reader fails" in {
        val testReaders = failingGettable.filterMandatoryDependent(_ == 1) {
          passingGettable2.reader
        }

        val result = testReaders.run(testData)

        result.isLeft mustBe true
      }

      "when the full reader fails due to not matching predicate" in {
        val testReaders = passingGettable1.filterMandatoryDependent(_ == 2) {
          passingGettable2.reader
        }

        val result = testReaders.run(testData)

        result.isLeft mustEqual true
      }
    }
  }

  "mandatoryNonEmptyListReader" - {
    "passes and converts list to nonEmptyList" in {
      passingGettable3.mandatoryNonEmptyListReader.run(testData).value mustBe NonEmptyList("listEntry1", List("listEntry2", "listEntry3"))
    }

    "fails list is empty" in {
      passingGettable3.mandatoryNonEmptyListReader.run(testDataWithEmptyList).isLeft mustBe true
    }

    "fails when gettable cannot be found" in {
      failingListGettable.mandatoryNonEmptyListReader.run(testData).isLeft mustBe true
    }
  }

  "optionalNonEmptyListReader" - {
    "passes and converts list to nonEmptyList" in {
      passingGettable3.optionalNonEmptyListReader.run(testData).value.value mustBe NonEmptyList("listEntry1", List("listEntry2", "listEntry3"))
    }

    "returns None when list is empty" in {
      passingGettable3.optionalNonEmptyListReader.run(testDataWithEmptyList).value mustBe None
    }

    "fails when gettable cannot be found" in {
      failingListGettable.optionalNonEmptyListReader.run(testData).isLeft mustBe true
    }
  }

  "returnOptionalDependant" - {
    "when the first reader passes" - {
      "then we return the value of the reader when the predicate passes" in {
        val testReaders = passingGettable1.returnOptionalDependant(_ == 1)
        val result      = testReaders.run(testData)

        result.value mustBe Some(1)
      }

      "then return None when the predicate fails" in {
        val testReaders = passingGettable1.returnOptionalDependant(_ != 1)
        val result      = testReaders.run(testData)

        result.value mustBe None
      }
    }

    "when the first reader fails" - {
      "then the full reader fails" in {
        val testReaders = failingGettable.returnOptionalDependant(_ == 1)
        val result      = testReaders.run(testData)

        result.isLeft mustBe true
      }

    }
  }

  "returnMandatoryDependant" - {
    "when the first reader passes" - {
      "then we return the value of the reader when the predicate passes" in {
        val testReaders = passingGettable1.returnMandatoryDependent(_ == 1)
        val result      = testReaders.run(testData)

        result.value mustBe 1
      }

      "then fail when the predicate fails" in {
        val testReaders = passingGettable1.returnMandatoryDependent(_ != 1)
        val result      = testReaders.run(testData)

        result.isLeft mustBe true
      }
    }

    "when the first reader fails" - {
      "then the full reader fails" in {
        val testReaders = failingGettable.returnMandatoryDependent(_ == 1)
        val result      = testReaders.run(testData)

        result.isLeft mustBe true
      }

    }
  }
}
