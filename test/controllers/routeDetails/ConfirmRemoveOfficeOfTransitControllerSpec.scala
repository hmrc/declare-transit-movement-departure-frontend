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

package controllers.routeDetails

import base.{AppWithDefaultMockFixtures, SpecBase}
import connectors.ReferenceDataConnector
import controllers.{routes => mainRoutes}
import forms.ConfirmRemoveOfficeOfTransitFormProvider
import matchers.JsonMatchers
import models.reference.{CountryCode, CustomsOffice}
import models.{Id, Index, NormalMode, UserAnswers}
import navigation.annotations.RouteDetails
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.routeDetails.AddAnotherTransitOfficePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import queries.OfficeOfTransitQuery
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.Future

class ConfirmRemoveOfficeOfTransitControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar with NunjucksSupport with JsonMatchers {

  val formProvider                                   = new ConfirmRemoveOfficeOfTransitFormProvider()
  val form                                           = formProvider()
  private val mockReferenceDataConnector             = mock[ReferenceDataConnector]
  private val customsOffice                          = CustomsOffice("id", "name", CountryCode("GB"), None)
  lazy val confirmRemoveOfficeOfTransitRoute: String = routes.ConfirmRemoveOfficeOfTransitController.onPageLoad(lrn, index, NormalMode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[RouteDetails]).toInstance(new FakeNavigator(onwardRoute)))
      .overrides(bind(classOf[ReferenceDataConnector]).toInstance(mockReferenceDataConnector))

  "ConfirmRemoveOfficeOfTransit Controller" - {

    "must return OK and the correct view for a GET" in {
      val userAnswers = emptyUserAnswers.set(AddAnotherTransitOfficePage(index), "id").toOption.value
      setUserAnswers(Some(userAnswers))
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockReferenceDataConnector.getCustomsOffice(any())(any(), any())).thenReturn(Future.successful(customsOffice))

      val request                                = FakeRequest(GET, confirmRemoveOfficeOfTransitRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"            -> form,
        "index"           -> index.display,
        "mode"            -> NormalMode,
        "officeOfTransit" -> s"${customsOffice.name} (${customsOffice.id})",
        "lrn"             -> lrn,
        "radios"          -> Radios.yesNo(form("value"))
      )

      templateCaptor.getValue mustEqual "confirmRemoveOfficeOfTransit.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must return error page when user tries to remove a office of transit that does not exists" in {
      val userAnswers = emptyUserAnswers
        .set(AddAnotherTransitOfficePage(index), "id")
        .toOption
        .value
        .remove(OfficeOfTransitQuery(index))
        .toOption
        .value
      setUserAnswers(Some(userAnswers))
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockReferenceDataConnector.getCustomsOffice(any())(any(), any())).thenReturn(Future.successful(customsOffice))

      val request                                = FakeRequest(GET, confirmRemoveOfficeOfTransitRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual NOT_FOUND

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "pageTitle"    -> msg"concurrent.remove.error.title".withArgs("officeOfTransit"),
        "pageHeading"  -> msg"concurrent.remove.error.heading".withArgs("officeOfTransit"),
        "linkText"     -> msg"concurrent.remove.error.noOfficeOfTransit.link.text",
        "redirectLink" -> ""
      )
      templateCaptor.getValue mustEqual "concurrentRemoveError.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must return error page when there are multiple office of transit and user tries to remove the last office of transit that is already removed" in {
      val updatedAnswers = emptyUserAnswers
        .set(AddAnotherTransitOfficePage(index), "id1")
        .success
        .value
        .set(AddAnotherTransitOfficePage(Index(1)), "id2")
        .success
        .value
        .remove(AddAnotherTransitOfficePage(Index(1)))
        .success
        .value
      setUserAnswers(Some(updatedAnswers))
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockReferenceDataConnector.getCustomsOffice(any())(any(), any())).thenReturn(Future.successful(customsOffice))

      val confirmRemoveRoute                     = routes.ConfirmRemoveOfficeOfTransitController.onPageLoad(lrn, Index(1), NormalMode).url
      val request                                = FakeRequest(GET, confirmRemoveRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual NOT_FOUND

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "pageTitle"    -> msg"concurrent.remove.error.title".withArgs("officeOfTransit"),
        "pageHeading"  -> msg"concurrent.remove.error.heading".withArgs("officeOfTransit"),
        "linkText"     -> msg"concurrent.remove.error.multipleOfficeOfTransit.link.text",
        "redirectLink" -> ""
      )

      templateCaptor.getValue mustEqual "concurrentRemoveError.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {
      val id          = Id()
      val userAnswers = emptyUserAnswers.set(AddAnotherTransitOfficePage(index), "id").toOption.value
      setUserAnswers(Some(userAnswers.copy(id = id)))
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, confirmRemoveOfficeOfTransitRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      val newUserAnswers = UserAnswers(
        lrn = userAnswers.lrn,
        eoriNumber = userAnswers.eoriNumber,
        userAnswers.remove(OfficeOfTransitQuery(index)).success.value.data,
        userAnswers.lastUpdated,
        id
      )

      verify(mockSessionRepository, times(1)).set(newUserAnswers)
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      val userAnswers = emptyUserAnswers.set(AddAnotherTransitOfficePage(index), "id").toOption.value
      setUserAnswers(Some(userAnswers))
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockReferenceDataConnector.getCustomsOffice(any())(any(), any())).thenReturn(Future.successful(customsOffice))

      val request                                = FakeRequest(POST, confirmRemoveOfficeOfTransitRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm                              = form.bind(Map("value" -> ""))
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"            -> boundForm,
        "index"           -> index.display,
        "mode"            -> NormalMode,
        "officeOfTransit" -> s"${customsOffice.name} (${customsOffice.id})",
        "lrn"             -> lrn,
        "radios"          -> Radios.yesNo(boundForm("value"))
      )

      templateCaptor.getValue mustEqual "confirmRemoveOfficeOfTransit.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setUserAnswers(None)

      val request = FakeRequest(GET, confirmRemoveOfficeOfTransitRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      setUserAnswers(None)

      val request =
        FakeRequest(POST, confirmRemoveOfficeOfTransitRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }
  }
}
