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

package controllers.safetyAndSecurity

import base.{MockNunjucksRendererApp, SpecBase}
import connectors.ReferenceDataConnector
import controllers.{routes => mainRoute}
import forms.safetyAndSecurity.SafetyAndSecurityConsigneeAddressFormProvider
import matchers.JsonMatchers
import models.reference.{Country, CountryCode}
import models.{ConsigneeAddress, CountryList, NormalMode}
import navigation.annotations.SafetyAndSecurity
import models.NormalMode
import navigation.annotations.SafetyAndSecurityTraderDetails
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.safetyAndSecurity.{SafetyAndSecurityConsigneeAddressPage, SafetyAndSecurityConsigneeNamePage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.countryJsonList

import scala.concurrent.Future

class SafetyAndSecurityConsigneeAddressControllerSpec extends SpecBase with MockNunjucksRendererApp with MockitoSugar with NunjucksSupport with JsonMatchers {

  private def onwardRoute                                        = Call("GET", "/foo")
  private val country                                            = Country(CountryCode("GB"), "United Kingdom")
  private val countries                                          = CountryList(Seq(country))
  private val mockReferenceDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]

  private val formProvider = new SafetyAndSecurityConsigneeAddressFormProvider()
  private val form         = formProvider(countries)
  private val template     = "safetyAndSecurity/safetyAndSecurityConsigneeAddress.njk"

  lazy val safetyAndSecurityConsigneeAddressRoute = routes.SafetyAndSecurityConsigneeAddressController.onPageLoad(lrn, NormalMode).url

  override def beforeEach: Unit = {
    reset(mockReferenceDataConnector)
    super.beforeEach
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[SafetyAndSecurity]).toInstance(new FakeNavigator(onwardRoute)))
      .overrides(bind[ReferenceDataConnector].toInstance(mockReferenceDataConnector))
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[SafetyAndSecurityTraderDetails]).toInstance(new FakeNavigator(onwardRoute)))

  "SafetyAndSecurityConsigneeAddress Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockReferenceDataConnector.getCountryList()(any(), any()))
        .thenReturn(Future.successful(countries))

      val userAnswers = emptyUserAnswers.set(SafetyAndSecurityConsigneeNamePage, "ConsigneeName").success.value

      dataRetrievalWithData(userAnswers)

      val request        = FakeRequest(GET, safetyAndSecurityConsigneeAddressRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"          -> form,
        "mode"          -> NormalMode,
        "lrn"           -> lrn,
        "consigneeName" -> "ConsigneeName",
        "countries"     -> countryJsonList(form.value.map(_.country), countries.fullList)
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockReferenceDataConnector.getCountryList()(any(), any()))
        .thenReturn(Future.successful(countries))
      val consignorAddress: ConsigneeAddress = ConsigneeAddress("Address line 1", "Address line 2", "Address line 3", country)

      val userAnswers = emptyUserAnswers
        .set(SafetyAndSecurityConsigneeNamePage, "ConsigneeName")
        .success
        .value
        .set(SafetyAndSecurityConsigneeAddressPage, consignorAddress)
        .success
        .value
      dataRetrievalWithData(userAnswers)

      val request        = FakeRequest(GET, safetyAndSecurityConsigneeAddressRoute)
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
        "form"          -> filledForm,
        "lrn"           -> lrn,
        "mode"          -> NormalMode,
        "consigneeName" -> "ConsigneeName",
        "countries"     -> countryJsonList(filledForm.value.map(_.country), countries.fullList)
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockReferenceDataConnector.getCountryList()(any(), any()))
        .thenReturn(Future.successful(countries))

      val userAnswers = emptyUserAnswers
        .set(SafetyAndSecurityConsigneeNamePage, "ConsigneeName")
        .success
        .value

      dataRetrievalWithData(userAnswers)

      val request =
        FakeRequest(POST, safetyAndSecurityConsigneeAddressRoute)
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
      val userAnswers = emptyUserAnswers.set(SafetyAndSecurityConsigneeNamePage, "ConsigneeName").success.value
      dataRetrievalWithData(userAnswers)

      val request        = FakeRequest(POST, safetyAndSecurityConsigneeAddressRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm      = form.bind(Map("value" -> "invalid"))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> boundForm,
        "lrn"  -> lrn,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual template
      jsonCaptor.getValue must containJson(expectedJson)

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      dataRetrievalNoData()

      val request = FakeRequest(GET, safetyAndSecurityConsigneeAddressRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoute.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      dataRetrievalNoData()

      val request =
        FakeRequest(POST, safetyAndSecurityConsigneeAddressRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoute.SessionExpiredController.onPageLoad().url

    }
  }
}
