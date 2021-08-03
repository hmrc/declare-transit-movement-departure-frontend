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

package controllers.routeDetails

import java.time.{LocalDateTime, ZoneOffset}
import base.{GeneratorSpec, MockNunjucksRendererApp, SpecBase}
import connectors.ReferenceDataConnector
import controllers.{routes => mainRoutes}
import forms.ArrivalTimesAtOfficeFormProvider
import matchers.JsonMatchers
import models.NormalMode
import models.reference.{CountryCode, CustomsOffice}
import navigation.annotations.RouteDetails
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalacheck.Gen
import org.scalatestplus.mockito.MockitoSugar
import pages.routeDetails.{AddAnotherTransitOfficePage, ArrivalTimesAtOfficePage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport
import viewModels.DateTimeInput

import scala.concurrent.Future

class ArrivalTimesAtOfficeControllerSpec
    extends SpecBase
    with MockNunjucksRendererApp
    with MockitoSugar
    with NunjucksSupport
    with JsonMatchers
    with GeneratorSpec {

  val formProvider                 = new ArrivalTimesAtOfficeFormProvider()
  private val customsOffice        = CustomsOffice("1", "name", CountryCode("GB"), None)
  private def form                 = formProvider(customsOffice.name)
  private val mockRefDataConnector = mock[ReferenceDataConnector]

  def onwardRoute = Call("GET", "/foo")

  val validAnswer: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)

  lazy val arrivalTimesAtOfficeRoute = routes.ArrivalTimesAtOfficeController.onPageLoad(lrn, index, NormalMode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[RouteDetails]).toInstance(new FakeNavigator(onwardRoute)))
      .overrides(bind(classOf[ReferenceDataConnector]).toInstance(mockRefDataConnector))

  def getRequest(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, arrivalTimesAtOfficeRoute)

  def postRequest(): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, arrivalTimesAtOfficeRoute)
      .withFormUrlEncodedBody(
        "value.day"    -> validAnswer.getDayOfMonth.toString,
        "value.month"  -> validAnswer.getMonthValue.toString,
        "value.year"   -> validAnswer.getYear.toString,
        "value.hour"   -> convertTo12HourClock(validAnswer.getHour).toString,
        "value.minute" -> validAnswer.getMinute.toString,
        "value.amOrPm" -> {
          if (validAnswer.getHour > 12) "pm" else "am"
        }
      )

  private def convertTo12HourClock(hour: Int) =
    hour match {
      case 13     => 1
      case 14     => 2
      case 15     => 3
      case 16     => 4
      case 17     => 5
      case 18     => 6
      case 19     => 7
      case 20     => 8
      case 21     => 9
      case 22     => 10
      case 23     => 11
      case 24 | 0 => 12
      case _      => hour
    }

  "ArrivalTimesAtOffice Controller" - {

    "must return OK and the correct view for a GET" in {
      val userAnswers = emptyUserAnswers.set(AddAnotherTransitOfficePage(index), customsOffice.id).toOption.value
      dataRetrievalWithData(userAnswers)
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockRefDataConnector.getCustomsOffice(any())(any(), any())).thenReturn(Future.successful(customsOffice))

      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, getRequest()).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val viewModel = DateTimeInput.localDateTime(form("value"))

      val expectedJson = Json.obj(
        "form"     -> form,
        "mode"     -> NormalMode,
        "lrn"      -> lrn,
        "dateTime" -> viewModel
      )

      templateCaptor.getValue mustEqual "arrivalTimesAtOffice.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must populate the view correctly on a GET" in {

      val userAnswers = emptyUserAnswers
        .set(AddAnotherTransitOfficePage(index), customsOffice.id)
        .toOption
        .value
        .set(ArrivalTimesAtOfficePage(index), validAnswer)
        .success
        .value

      dataRetrievalWithData(userAnswers)
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockRefDataConnector.getCustomsOffice(any())(any(), any())).thenReturn(Future.successful(customsOffice))

      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, getRequest()).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(
        Map(
          "value.day"    -> validAnswer.getDayOfMonth.toString,
          "value.month"  -> validAnswer.getMonthValue.toString,
          "value.year"   -> validAnswer.getYear.toString,
          "value.hour"   -> convertTo12HourClock(validAnswer.getHour).toString,
          "value.minute" -> validAnswer.getMinute.toString,
          "value.amOrPm" -> {
            if (validAnswer.getHour > 12) "pm" else "am"
          }
        )
      )

      val viewModel = DateTimeInput.localDateTime(filledForm("value"))

      val expectedJson = Json.obj(
        "form"     -> filledForm,
        "mode"     -> NormalMode,
        "lrn"      -> lrn,
        "dateTime" -> viewModel
      )

      templateCaptor.getValue mustEqual "arrivalTimesAtOffice.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {
      val userAnswers = emptyUserAnswers.set(AddAnotherTransitOfficePage(index), customsOffice.id).toOption.value
      dataRetrievalWithData(userAnswers)
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockRefDataConnector.getCustomsOffice(any())(any(), any())).thenReturn(Future.successful(customsOffice))

      val result = route(app, postRequest()).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      val userAnswers = emptyUserAnswers.set(AddAnotherTransitOfficePage(index), customsOffice.id).toOption.value
      dataRetrievalWithData(userAnswers)
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockRefDataConnector.getCustomsOffice(any())(any(), any())).thenReturn(Future.successful(customsOffice))

      val request        = FakeRequest(POST, arrivalTimesAtOfficeRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm      = form.bind(Map("value" -> "invalid value"))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val viewModel = DateTimeInput.localDateTime(boundForm("value"))

      val expectedJson = Json.obj(
        "form"     -> boundForm,
        "mode"     -> NormalMode,
        "lrn"      -> lrn,
        "dateTime" -> viewModel
      )

      templateCaptor.getValue mustEqual "arrivalTimesAtOffice.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      dataRetrievalNoData()

      val result = route(app, getRequest).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      dataRetrievalNoData()

      val result = route(app, postRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }
  }
}
