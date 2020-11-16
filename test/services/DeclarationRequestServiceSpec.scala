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

package services

import java.time.LocalDateTime

import base.{GeneratorSpec, SpecBase}
import generators.JourneyModelGenerators
import models.UserAnswers
import models.journeyDomain.{JourneyDomain, JourneyDomainSpec}
import models.messages.InterchangeControlReference
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import repositories.InterchangeControlReferenceIdRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DeclarationRequestServiceSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators with BeforeAndAfterEach {

  val mockIcrRepository   = mock[InterchangeControlReferenceIdRepository]
  val mockDateTimeService = mock[DateTimeService]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockIcrRepository)
    reset(mockDateTimeService)
  }

  "DomainModelToSubmissionModel" - {
    "must convert JourneyDomain model to DeclarationRequest model" in {

      forAll(arb[UserAnswers], arb[JourneyDomain]) {
        (userAnswers, journeyDomain) =>
          val service = new DeclarationRequestService(mockIcrRepository, mockDateTimeService)

          when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
          when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

          val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(journeyDomain)(userAnswers)
          service.convert(updatedUserAnswer).futureValue must be(defined)
      }
    }

    "must None when InterchangeControlReferenceIdRepository fails" in {

      forAll(arb[UserAnswers], arb[JourneyDomain]) {
        (userAnswers, journeyDomain) =>
          val service = new DeclarationRequestService(mockIcrRepository, mockDateTimeService)

          when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.failed(new Exception))
          when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

          val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(journeyDomain)(userAnswers)
          service.convert(updatedUserAnswer).failed.futureValue mustBe an[Exception]
      }
    }

    "must None when mandatory pages are missing" in {
      val service = new DeclarationRequestService(mockIcrRepository, mockDateTimeService)

      when(mockIcrRepository.nextInterchangeControlReferenceId()).thenReturn(Future.successful(InterchangeControlReference("20190101", 1)))
      when(mockDateTimeService.currentDateTime).thenReturn(LocalDateTime.now())

      service.convert(emptyUserAnswers).futureValue mustBe None
    }
  }

}
