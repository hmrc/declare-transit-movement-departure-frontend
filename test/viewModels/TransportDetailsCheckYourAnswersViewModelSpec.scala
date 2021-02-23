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

package viewModels

import base.SpecBase
import models.reference.{Country, CountryCode, TransportMode}
import models.{CountryList, TransportModeList}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import uk.gov.hmrc.viewmodels.Text.{Literal, Message}

class TransportDetailsCheckYourAnswersViewModelSpec extends SpecBase with ScalaCheckPropertyChecks {

  val countryList = new CountryList(Seq(Country(CountryCode("FR"), "France")))

  val transportMode1: TransportMode     = TransportMode("1", "crossing border")
  val transportMode2: TransportMode     = TransportMode("2", "inland mode")
  val transportMode3: TransportMode     = TransportMode("3", "mode at border")
  val transportModes: TransportModeList = TransportModeList(Seq(transportMode1, transportMode2, transportMode3))

  val country1  = Country(CountryCode("GB"), "United Kingdom")
  val country2  = Country(CountryCode("AD"), "Andorra")
  val countries = CountryList(Seq(country1, country2))

  "TransportDetailsCheckYourAnswersViewModel" - {

    "display modeCrossingBorder" in {

      val updatedAnswers = emptyUserAnswers
        .set(ModeCrossingBorderPage, "1")
        .success
        .value
        .set(InlandModePage, "2")
        .success
        .value
        .set(ModeAtBorderPage, "3")
        .success
        .value

      val data = TransportDetailsCheckYourAnswersViewModel(updatedAnswers, countryList, transportModes)

      data.sections.head.sectionTitle must not be defined
      data.sections.length mustEqual 1
      data.sections.head.rows.length mustEqual 3
      data.sections.head.rows(0).value.content mustEqual Literal("(2) inland mode")
      data.sections.head.rows(1).value.content mustEqual Literal("mode at border")
      data.sections.head.rows(2).value.content mustEqual Literal("crossing border")
    }

    "display country" in {

      val updatedAnswers = emptyUserAnswers
        .set(InlandModePage, "1")
        .success
        .value
        .set(NationalityAtDeparturePage, country1.code)
        .success
        .value
        .set(NationalityCrossingBorderPage, country2.code)
        .success
        .value

      val data = TransportDetailsCheckYourAnswersViewModel(updatedAnswers, countryList, transportModes)

      data.sections.head.sectionTitle must not be defined
      data.sections.length mustEqual 1
      data.sections.head.rows.length mustEqual 3
      data.sections.head.rows(1).value.content mustEqual Literal("GB")
      data.sections.head.rows(2).value.content mustEqual Literal("AD")

    }
    "display changeAtBorder " in {
      val updatedAnswers = emptyUserAnswers
        .set(InlandModePage, "1")
        .success
        .value
        .set(ChangeAtBorderPage, true)
        .success
        .value
      val data = TransportDetailsCheckYourAnswersViewModel(updatedAnswers, countryList, transportModes)

      data.sections.head.sectionTitle must not be defined
      data.sections.length mustEqual 1
      data.sections.head.rows.length mustEqual 2
      val message: Message = data.sections.head.rows(1).value.content.asInstanceOf[Message]
      message.key mustBe "site.yes"
    }

    "display addIdAdDeparture " in {
      val updatedAnswers = emptyUserAnswers
        .set(InlandModePage, "1")
        .success
        .value
        .set(AddIdAtDeparturePage, true)
        .success
        .value

      val data = TransportDetailsCheckYourAnswersViewModel(updatedAnswers, countryList, transportModes)

      data.sections.head.sectionTitle must not be defined
      data.sections.length mustEqual 1
      data.sections.head.rows.length mustEqual 2
      val message: Message = data.sections.head.rows(1).value.content.asInstanceOf[Message]
      message.key mustBe "site.yes"
    }

    "display idAtDeparture " in {
      val updatedAnswers = emptyUserAnswers
        .set(InlandModePage, "1")
        .success
        .value
        .set(IdAtDeparturePage, "test")
        .success
        .value
      val data = TransportDetailsCheckYourAnswersViewModel(updatedAnswers, countryList, transportModes)

      data.sections.head.sectionTitle must not be defined
      data.sections.length mustEqual 1
      data.sections.head.rows.length mustEqual 2
      data.sections.head.rows(1).value.content mustEqual Literal("test")
    }

    "display idCrossingBorder " in {
      val updatedAnswers = emptyUserAnswers
        .set(InlandModePage, "1")
        .success
        .value
        .set(IdCrossingBorderPage, "test")
        .success
        .value
      val data = TransportDetailsCheckYourAnswersViewModel(updatedAnswers, countryList, transportModes)

      data.sections.head.sectionTitle must not be defined
      data.sections.length mustEqual 1
      data.sections.head.rows.length mustEqual 2
      data.sections.head.rows(1).value.content mustEqual Literal("test")
    }

  }
}
