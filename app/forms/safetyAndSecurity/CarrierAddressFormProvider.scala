package forms.safetyAndSecurity

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class CarrierAddressFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("carrierAddress.error.required")
        .verifying(maxLength(10, "carrierAddress.error.length"))
    )
}
