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

package controllers.addItems.traderDetails

import base.{MockNunjucksRendererApp, SpecBase}
import connectors.ReferenceDataConnector
import controllers.{routes => mainRoutes}
import forms.addItems.traderDetails.TraderDetailsConsignorAddressFormProvider
import matchers.JsonMatchers
import models.reference.{Country, CountryCode}
import models.{ConsignorAddress, CountryList, NormalMode}
import navigation.annotations.AddItems
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.addItems.traderDetails.{TraderDetailsConsignorAddressPage, TraderDetailsConsignorNamePage}
import pages.addItems.traderSecurityDetails.SecurityConsignorNamePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class TraderDetailsConsignorAddressControllerSpec extends SpecBase with MockNunjucksRendererApp with MockitoSugar with NunjucksSupport with JsonMatchers {

  private def onwardRoute                                        = Call("GET", "/foo")
  private val country                                            = Country(CountryCode("GB"), "United Kingdom")
  private val countries                                          = CountryList(Seq(country))
  private val consignorName                                      = "consignorName"
  private val mockReferenceDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]

  private val formProvider = new TraderDetailsConsignorAddressFormProvider()
  private val form         = formProvider(countries, consignorName)

  lazy val traderDetailsConsignorAddressRoute = routes.TraderDetailsConsignorAddressController.onPageLoad(lrn, index, NormalMode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(
        bind(classOf[Navigator]).qualifiedWith(classOf[AddItems]).toInstance(new FakeNavigator(onwardRoute)),
        bind[ReferenceDataConnector].toInstance(mockReferenceDataConnector)
      )

  "TraderDetailsConsignorAddress Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockReferenceDataConnector.getCountryList()(any(), any()))
        .thenReturn(Future.successful(countries))

      val userAnswers = emptyUserAnswers.set(TraderDetailsConsignorNamePage(index), "foo").success.value
      dataRetrievalWithData(userAnswers)

      val request        = FakeRequest(GET, traderDetailsConsignorAddressRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"  -> form,
        "lrn"   -> lrn,
        "mode"  -> NormalMode,
        "index" -> index.display
      )

      templateCaptor.getValue mustEqual "addItems/traderDetails/traderDetailsConsignorAddress.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockReferenceDataConnector.getCountryList()(any(), any()))
        .thenReturn(Future.successful(countries))

      val tradersDetailsConsignorAddress: ConsignorAddress = ConsignorAddress("Address line 1", "Address line 2", "Address line 3", country)

      val userAnswers = emptyUserAnswers
        .set(TraderDetailsConsignorNamePage(index), "ConsignorName")
        .success
        .value
        .set(TraderDetailsConsignorAddressPage(index), tradersDetailsConsignorAddress)
        .success
        .value
      dataRetrievalWithData(userAnswers)

      val request        = FakeRequest(GET, traderDetailsConsignorAddressRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(
        Map(
          "AddressLine1" -> "Address line 1",
          "AddressLine2" -> "Address line 2",
          "AddressLine3" -> "Address line 3",
          "country"      -> "GB"
        )
      )

      val expectedJson = Json.obj(
        "form"  -> filledForm,
        "lrn"   -> lrn,
        "mode"  -> NormalMode,
        "index" -> index.display
      )

      templateCaptor.getValue mustEqual "addItems/traderDetails/traderDetailsConsignorAddress.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockReferenceDataConnector.getCountryList()(any(), any()))
        .thenReturn(Future.successful(countries))

      val userAnswers = emptyUserAnswers
        .set(TraderDetailsConsignorNamePage(index), "ConsignorName")
        .success
        .value
      dataRetrievalWithData(userAnswers)

      val request =
        FakeRequest(POST, traderDetailsConsignorAddressRoute)
          .withFormUrlEncodedBody(("AddressLine1", "value 1"), ("AddressLine2", "value 2"), ("AddressLine3", "value 3"), ("country", "GB"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockReferenceDataConnector.getCountryList()(any(), any()))
        .thenReturn(Future.successful(countries))

      val userAnswers = emptyUserAnswers.set(TraderDetailsConsignorNamePage(index), "ConsignorName").success.value
      dataRetrievalWithData(userAnswers)

      val request        = FakeRequest(POST, traderDetailsConsignorAddressRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm      = form.bind(Map("value" -> "invalid value"))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"  -> boundForm,
        "lrn"   -> lrn,
        "mode"  -> NormalMode,
        "index" -> index.display
      )

      templateCaptor.getValue mustEqual "addItems/traderDetails/traderDetailsConsignorAddress.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      dataRetrievalNoData()

      val request = FakeRequest(GET, traderDetailsConsignorAddressRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      dataRetrievalNoData()

      val request =
        FakeRequest(POST, traderDetailsConsignorAddressRoute)
          .withFormUrlEncodedBody(("Address line 1", "value 1"), ("Address line 2", "value 2"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }
  }
}
