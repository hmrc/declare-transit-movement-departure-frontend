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

package generators

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.TryValues
import pages._
import pages.addItems._
import pages.addItems.specialMentions._
import pages.addItems.containers.{AddAnotherContainerPage, ContainerNumberPage}
import pages.addItems.specialMentions.RemoveSpecialMentionPage
import pages.addItems.traderDetails._
import pages.guaranteeDetails.{GuaranteeReferencePage, GuaranteeTypePage}
import pages.movementDetails.PreLodgeDeclarationPage
import play.api.libs.json.{JsValue, Json}

trait UserAnswersGenerator extends TryValues {
  self: Generators =>

  val generators: Seq[Gen[(QuestionPage[_], JsValue)]] =
    arbitrary[(ConfirmRemovePreviousAdministrativeReferencePage, JsValue)] ::
      arbitrary[(ExtraInformationPage, JsValue)] ::
      arbitrary[(AddAnotherPreviousAdministrativeReferencePage, JsValue)] ::
      arbitrary[(RemoveSpecialMentionPage, JsValue)] ::
      arbitrary[(AddAnotherSpecialMentionPage.type, JsValue)] ::
      arbitrary[(SpecialMentionAdditionalInfoPage.type, JsValue)] ::
      arbitrary[(SpecialMentionTypePage.type, JsValue)] ::
      arbitrary[(AddSpecialMentionPage, JsValue)] ::
      arbitrary[(AddAnotherPreviousAdministrativeReferencePage, JsValue)] ::
      arbitrary[(AddAnotherContainerPage.type, JsValue)] ::
      arbitrary[(ContainerNumberPage.type, JsValue)] ::
      arbitrary[(AddAnotherPreviousAdministrativeReferencePage, JsValue)] ::
      arbitrary[(AddItemsSameConsignorForAllItemsPage, JsValue)] ::
      arbitrary[(AddAnotherPreviousAdministrativeReferencePage, JsValue)] ::
      arbitrary[(PreviousReferencePage, JsValue)] ::
      arbitrary[(ExtraInformationPage, JsValue)] ::
      arbitrary[(PreviousReferencePage, JsValue)] ::
      arbitrary[(AddExtraInformationPage, JsValue)] ::
      arbitrary[(AddItemsSameConsignorForAllItemsPage, JsValue)] ::
      arbitrary[(HowManyPackagesPage, JsValue)] ::
      arbitrary[(AddAnotherPackagePage, JsValue)] ::
      arbitrary[(DeclareMarkPage, JsValue)] ::
      arbitrary[(AddMarkPage, JsValue)] ::
      arbitrary[(TotalPiecesPage, JsValue)] ::
      arbitrary[(DeclareNumberOfPackagesPage, JsValue)] ::
      arbitrary[(PreviousReferencePage, JsValue)] ::
      arbitrary[(AddExtraInformationPage, JsValue)] ::
      arbitrary[(ReferenceTypePage, JsValue)] ::
      arbitrary[(AddAdministrativeReferencePage, JsValue)] ::
      arbitrary[(ConfirmRemoveItemPage.type, JsValue)] ::
      arbitrary[(TotalPackagesPage.type, JsValue)] ::
      arbitrary[(TotalNetMassPage, JsValue)] ::
      arbitrary[(CommodityCodePage, JsValue)] ::
      arbitrary[(TotalNetMassPage, JsValue)] ::
      arbitrary[(TraderDetailsConsignorNamePage, JsValue)] ::
      arbitrary[(TraderDetailsConsignorEoriNumberPage, JsValue)] ::
      arbitrary[(TraderDetailsConsignorEoriKnownPage, JsValue)] ::
      arbitrary[(TraderDetailsConsignorAddressPage, JsValue)] ::
      arbitrary[(TraderDetailsConsigneeNamePage, JsValue)] ::
      arbitrary[(TraderDetailsConsigneeEoriNumberPage, JsValue)] ::
      arbitrary[(TraderDetailsConsigneeEoriKnownPage, JsValue)] ::
      arbitrary[(TraderDetailsConsigneeAddressPage, JsValue)] ::
      arbitrary[(TotalNetMassPage, JsValue)] ::
      arbitrary[(AddTotalNetMassPage, JsValue)] ::
      arbitrary[(IsCommodityCodeKnownPage, JsValue)] ::
      arbitrary[(AddTotalNetMassPage, JsValue)] ::
      arbitrary[(ItemDescriptionPage, JsValue)] ::
      arbitrary[(OtherReferenceLiabilityAmountPage.type, JsValue)] ::
      arbitrary[(ItemTotalGrossMassPage, JsValue)] ::
      arbitrary[(PreLodgeDeclarationPage.type, JsValue)] ::
      arbitrary[(ItemDescriptionPage, JsValue)] ::
      arbitrary[(OtherReferenceLiabilityAmountPage.type, JsValue)] ::
      arbitrary[(ConfirmRemoveSealsPage.type, JsValue)] ::
      arbitrary[(GuaranteeTypePage.type, JsValue)] ::
      arbitrary[(OtherReferencePage.type, JsValue)] ::
      arbitrary[(GuaranteeReferencePage.type, JsValue)] ::
      arbitrary[(ConfirmRemoveOfficeOfTransitPage.type, JsValue)] ::
      arbitrary[(SealsInformationPage.type, JsValue)] ::
      arbitrary[(AddAnotherTransitOfficePage, JsValue)] ::
      arbitrary[(ArrivalTimesAtOfficePage, JsValue)] ::
      arbitrary[(ControlResultDateLimitPage.type, JsValue)] ::
      arbitrary[(SealIdDetailsPage, JsValue)] ::
      arbitrary[(AddSealsPage.type, JsValue)] ::
      arbitrary[(CustomsApprovedLocationPage.type, JsValue)] ::
      arbitrary[(AddCustomsApprovedLocationPage.type, JsValue)] ::
      arbitrary[(TotalGrossMassPage.type, JsValue)] ::
      arbitrary[(AuthorisedLocationCodePage.type, JsValue)] ::
      arbitrary[(TotalPackagesPage.type, JsValue)] ::
      arbitrary[(DeclarePackagesPage.type, JsValue)] ::
      arbitrary[(AddTransitOfficePage.type, JsValue)] ::
      arbitrary[(ModeAtBorderPage.type, JsValue)] ::
      arbitrary[(NationalityCrossingBorderPage.type, JsValue)] ::
      arbitrary[(ModeCrossingBorderPage.type, JsValue)] ::
      arbitrary[(InlandModePage.type, JsValue)] ::
      arbitrary[(DestinationOfficePage.type, JsValue)] ::
      arbitrary[(AddIdAtDeparturePage.type, JsValue)] ::
      arbitrary[(IdCrossingBorderPage.type, JsValue)] ::
      arbitrary[(DestinationCountryPage.type, JsValue)] ::
      arbitrary[(ChangeAtBorderPage.type, JsValue)] ::
      arbitrary[(NationalityAtDeparturePage.type, JsValue)] ::
      arbitrary[(IdAtDeparturePage.type, JsValue)] ::
      arbitrary[(ConsigneeAddressPage.type, JsValue)] ::
      arbitrary[(PrincipalAddressPage.type, JsValue)] ::
      arbitrary[(ConsignorAddressPage.type, JsValue)] ::
      arbitrary[(OfficeOfDeparturePage.type, JsValue)] ::
      arbitrary[(ConsigneeNamePage.type, JsValue)] ::
      arbitrary[(WhatIsConsigneeEoriPage.type, JsValue)] ::
      arbitrary[(CountryOfDispatchPage.type, JsValue)] ::
      arbitrary[(ConsignorNamePage.type, JsValue)] ::
      arbitrary[(AddConsigneePage.type, JsValue)] ::
      arbitrary[(IsConsigneeEoriKnownPage.type, JsValue)] ::
      arbitrary[(ConsignorEoriPage.type, JsValue)] ::
      arbitrary[(AddConsignorPage.type, JsValue)] ::
      arbitrary[(IsConsignorEoriKnownPage.type, JsValue)] ::
      arbitrary[(PrincipalNamePage.type, JsValue)] ::
      arbitrary[(IsPrincipalEoriKnownPage.type, JsValue)] ::
      arbitrary[(WhatIsPrincipalEoriPage.type, JsValue)] ::
      arbitrary[(RepresentativeCapacityPage.type, JsValue)] ::
      arbitrary[(RepresentativeNamePage.type, JsValue)] ::
      arbitrary[(ContainersUsedPage.type, JsValue)] ::
      arbitrary[(DeclarationForSomeoneElsePage.type, JsValue)] ::
      arbitrary[(DeclarationPlacePage.type, JsValue)] ::
      arbitrary[(ProcedureTypePage.type, JsValue)] ::
      arbitrary[(DeclarationTypePage.type, JsValue)] ::
      arbitrary[(AddSecurityDetailsPage.type, JsValue)] ::
      arbitrary[(AccessCodePage.type, JsValue)] ::
      arbitrary[(OtherReferencePage.type, JsValue)] ::
      arbitrary[(LiabilityAmountPage.type, JsValue)] ::
      arbitrary[(GuaranteeReferencePage.type, JsValue)] ::
      arbitrary[(ConfirmRemoveOfficeOfTransitPage.type, JsValue)] ::
      arbitrary[(SealsInformationPage.type, JsValue)] ::
      arbitrary[(AddAnotherTransitOfficePage, JsValue)] ::
      arbitrary[(ArrivalTimesAtOfficePage, JsValue)] ::
      arbitrary[(ControlResultDateLimitPage.type, JsValue)] ::
      arbitrary[(SealIdDetailsPage, JsValue)] ::
      arbitrary[(AddSealsPage.type, JsValue)] ::
      arbitrary[(CustomsApprovedLocationPage.type, JsValue)] ::
      arbitrary[(AddCustomsApprovedLocationPage.type, JsValue)] ::
      arbitrary[(TotalGrossMassPage.type, JsValue)] ::
      arbitrary[(AuthorisedLocationCodePage.type, JsValue)] ::
      arbitrary[(TotalPackagesPage.type, JsValue)] ::
      arbitrary[(DeclarePackagesPage.type, JsValue)] ::
      arbitrary[(AddTransitOfficePage.type, JsValue)] ::
      arbitrary[(ModeAtBorderPage.type, JsValue)] ::
      arbitrary[(NationalityCrossingBorderPage.type, JsValue)] ::
      arbitrary[(ModeCrossingBorderPage.type, JsValue)] ::
      arbitrary[(InlandModePage.type, JsValue)] ::
      arbitrary[(DestinationOfficePage.type, JsValue)] ::
      arbitrary[(AddIdAtDeparturePage.type, JsValue)] ::
      arbitrary[(IdCrossingBorderPage.type, JsValue)] ::
      arbitrary[(DestinationCountryPage.type, JsValue)] ::
      arbitrary[(ChangeAtBorderPage.type, JsValue)] ::
      arbitrary[(NationalityAtDeparturePage.type, JsValue)] ::
      arbitrary[(IdAtDeparturePage.type, JsValue)] ::
      arbitrary[(ConsigneeAddressPage.type, JsValue)] ::
      arbitrary[(PrincipalAddressPage.type, JsValue)] ::
      arbitrary[(ConsignorAddressPage.type, JsValue)] ::
      arbitrary[(OfficeOfDeparturePage.type, JsValue)] ::
      arbitrary[(ConsigneeNamePage.type, JsValue)] ::
      arbitrary[(WhatIsConsigneeEoriPage.type, JsValue)] ::
      arbitrary[(CountryOfDispatchPage.type, JsValue)] ::
      arbitrary[(ConsignorNamePage.type, JsValue)] ::
      arbitrary[(AddConsigneePage.type, JsValue)] ::
      arbitrary[(IsConsigneeEoriKnownPage.type, JsValue)] ::
      arbitrary[(ConsignorEoriPage.type, JsValue)] ::
      arbitrary[(AddConsignorPage.type, JsValue)] ::
      arbitrary[(IsConsignorEoriKnownPage.type, JsValue)] ::
      arbitrary[(PrincipalNamePage.type, JsValue)] ::
      arbitrary[(IsPrincipalEoriKnownPage.type, JsValue)] ::
      arbitrary[(WhatIsPrincipalEoriPage.type, JsValue)] ::
      arbitrary[(RepresentativeCapacityPage.type, JsValue)] ::
      arbitrary[(RepresentativeNamePage.type, JsValue)] ::
      arbitrary[(ContainersUsedPage.type, JsValue)] ::
      arbitrary[(DeclarationForSomeoneElsePage.type, JsValue)] ::
      arbitrary[(DeclarationPlacePage.type, JsValue)] ::
      arbitrary[(ProcedureTypePage.type, JsValue)] ::
      arbitrary[(DeclarationTypePage.type, JsValue)] ::
      arbitrary[(AddSecurityDetailsPage.type, JsValue)] ::
      Nil

  implicit lazy val arbitraryUserData: Arbitrary[UserAnswers] = {

    import models._

    Arbitrary {
      for {
        id         <- arbitrary[LocalReferenceNumber]
        eoriNumber <- arbitrary[EoriNumber]
        data <- generators match {
          case Nil => Gen.const(Map[QuestionPage[_], JsValue]())
          case _   => Gen.mapOf(oneOf(generators))
        }
      } yield
        UserAnswers(
          id         = id,
          eoriNumber = eoriNumber,
          data = data.foldLeft(Json.obj()) {
            case (obj, (path, value)) =>
              obj.setObject(path.path, value).get
          }
        )
    }
  }
}
