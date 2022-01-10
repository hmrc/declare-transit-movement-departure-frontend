package controllers

import java.time.{LocalDate, ZoneOffset}

import base.{AppWithDefaultMockFixtures, SpecBase}
import base.AppWithDefaultMockFixtures
import forms.$className$FormProvider
import matchers.JsonMatchers
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import navigation.annotations.$navRoute$
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.$className$Page
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import repositories.SessionRepository
import uk.gov.hmrc.viewmodels.{DateInput, NunjucksSupport}

import scala.concurrent.Future

class $className$ControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar with NunjucksSupport with JsonMatchers {

  private val formProvider = new $className$FormProvider()
  private def form = formProvider()
  private val template = "$className;format="decap"$.njk"

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[$navRoute$]).toInstance(fakeNavigator))

  val validAnswer = LocalDate.now(ZoneOffset.UTC)

  lazy val $className;format="decap"$Route = routes.$className$Controller.onPageLoad(lrn, NormalMode).url

  def getRequest(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, $className;format="decap"$Route)

  def postRequest(): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, $className;format="decap"$Route)
      .withFormUrlEncodedBody(
        "value.day"   -> validAnswer.getDayOfMonth.toString,
        "value.month" -> validAnswer.getMonthValue.toString,
        "value.year"  -> validAnswer.getYear.toString
      )

  "$className$ Controller" - {

    "must return OK and the correct view for a GET" in {
      setUserAnswers(Some(emptyUserAnswers))
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject] = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, getRequest).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val viewModel = DateInput.localDate(form("value"))

      val expectedJson = Json.obj(
        "form" -> form,
        "mode" -> NormalMode,
        "lrn"  -> lrn,
        "date" -> viewModel
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = emptyUserAnswers.set($className$Page, validAnswer).success.value
      setUserAnswers(Some(userAnswers))

      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject] = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, getRequest).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(
        Map(
          "value.day"   -> validAnswer.getDayOfMonth.toString,
          "value.month" -> validAnswer.getMonthValue.toString,
          "value.year"  -> validAnswer.getYear.toString
        )
      )

      val viewModel = DateInput.localDate(filledForm("value"))

      val expectedJson = Json.obj(
        "form" -> filledForm,
        "mode" -> NormalMode,
        "lrn"  -> lrn,
        "date" -> viewModel
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must redirect to the next page when valid data is submitted" in {
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      setUserAnswers(Some(emptyUserAnswers))

      val result = route(app, postRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      setUserAnswers(Some(emptyUserAnswers))

      val request = FakeRequest(POST, $className;format="decap"$Route).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject] = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val viewModel = DateInput.localDate(boundForm("value"))

      val expectedJson = Json.obj(
        "form" -> boundForm,
        "mode" -> NormalMode,
        "lrn"  -> lrn,
        "date" -> viewModel
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setUserAnswers(None)

      val result = route(app, getRequest).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setUserAnswers(None)

      val result = route(app, postRequest).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

    }
  }
}
