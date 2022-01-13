/*
 * Copyright 2022 HM Revenue & Customs
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

import base.{AppWithDefaultMockFixtures, SpecBase}
import controllers.{routes => mainRoute}
import forms.CommonAddressFormProvider
import matchers.JsonMatchers
import models.reference.{Country, CountryCode}
import models.{CommonAddress, CountryList, NormalMode}
import navigation.Navigator
import navigation.annotations.{SafetyAndSecurity, SafetyAndSecurityTraderDetails}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.safetyAndSecurity.{CarrierAddressPage, CarrierNamePage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import services.CountriesService
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.countryJsonList

import scala.concurrent.Future

class CarrierAddressControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar with NunjucksSupport with JsonMatchers {

  lazy val carrierAddressRoute = routes.CarrierAddressController.onPageLoad(lrn, NormalMode).url
  private val country          = Country(CountryCode("GB"), "United Kingdom")
  private val countryList      = CountryList(Seq(country))

  private val mockCountriesService: CountriesService = mock[CountriesService]

  private val formProvider = new CommonAddressFormProvider()
  private val form         = formProvider(countryList, carrierName)
  private val template     = "safetyAndSecurity/carrierAddress.njk"

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(
        bind(classOf[Navigator]).qualifiedWith(classOf[SafetyAndSecurity]).toInstance(fakeNavigator),
        bind(classOf[CountriesService]).toInstance(mockCountriesService)
      )
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[SafetyAndSecurityTraderDetails]).toInstance(fakeNavigator))

  "CarrierAddress Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockCountriesService.getCountries()(any()))
        .thenReturn(Future.successful(countryList))

      val userAnswers = emptyUserAnswers.set(CarrierNamePage, "carrierName").success.value

      setUserAnswers(Some(userAnswers))

      val request                                = FakeRequest(GET, carrierAddressRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "mode"        -> NormalMode,
        "form"        -> form,
        "carrierName" -> carrierName,
        "lrn"         -> lrn,
        "countries"   -> countryJsonList(form.value.map(_.country), countryList.countries)
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockCountriesService.getCountries()(any()))
        .thenReturn(Future.successful(countryList))

      val carrierAddress: CommonAddress = CommonAddress("Address line 1", "Address line 2", "Code", country)

      val userAnswers = emptyUserAnswers
        .set(CarrierNamePage, "carrierName")
        .success
        .value
        .set(CarrierAddressPage, carrierAddress)
        .success
        .value

      setUserAnswers(Some(userAnswers))

      val request                                = FakeRequest(GET, carrierAddressRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(
        Map(
          "AddressLine1" -> "Address line 1",
          "AddressLine2" -> "Address line 2",
          "AddressLine3" -> "Code",
          "country"      -> "GB"
        )
      )

      val expectedJson = Json.obj(
        "mode"        -> NormalMode,
        "form"        -> filledForm,
        "carrierName" -> carrierName,
        "lrn"         -> lrn,
        "countries"   -> countryJsonList(filledForm.value.map(_.country), countryList.countries)
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockCountriesService.getCountries()(any())).thenReturn(Future.successful(countryList))

      val userAnswers = emptyUserAnswers
        .set(CarrierNamePage, "carrierName")
        .success
        .value

      setUserAnswers(Some(userAnswers))

      val request =
        FakeRequest(POST, carrierAddressRoute)
          .withFormUrlEncodedBody(("AddressLine1", "value 1"), ("AddressLine2", "value 2"), ("AddressLine3", "value 3"), ("country", "GB"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockCountriesService.getCountries()(any()))
        .thenReturn(Future.successful(countryList))

      val userAnswers = emptyUserAnswers
        .set(CarrierNamePage, "carrierName")
        .success
        .value

      setUserAnswers(Some(userAnswers))

      val request                                = FakeRequest(POST, carrierAddressRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm                              = form.bind(Map("value" -> ""))
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

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

      setUserAnswers(None)

      val request = FakeRequest(GET, carrierAddressRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoute.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setUserAnswers(None)

      val request =
        FakeRequest(POST, carrierAddressRoute)
          .withFormUrlEncodedBody(("Address line 1", "value 1"), ("Address line 2", "value 2"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoute.SessionExpiredController.onPageLoad().url

    }
  }
}
