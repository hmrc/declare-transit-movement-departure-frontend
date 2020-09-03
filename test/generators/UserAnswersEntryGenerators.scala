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

import models._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators {

  self: Generators =>
  implicit lazy val arbitraryInlandModeUserAnswersEntry: Arbitrary[(InlandModePage.type, JsValue)] =
      Arbitrary {
      for {
        page  <- arbitrary[InlandModePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPrincipalAddressUserAnswersEntry: Arbitrary[(PrincipalAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PrincipalAddressPage.type]
        value <- arbitrary[PrincipalAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryConsignorAddressUserAnswersEntry: Arbitrary[(ConsignorAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ConsignorAddressPage.type]
        value <- arbitrary[ConsignorAddress].map(Json.toJson(_))
      } yield (page, value)
    }


  implicit lazy val arbitraryOfficeOfDepartureUserAnswersEntry: Arbitrary[(OfficeOfDeparturePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[OfficeOfDeparturePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryConsigneeAddressUserAnswersEntry: Arbitrary[(ConsigneeAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ConsigneeAddressPage.type]
        value <- arbitrary[ConsigneeAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryCountryOfDispatchUserAnswersEntry: Arbitrary[(CountryOfDispatchPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[CountryOfDispatchPage.type]
        value <- stringsWithMaxLength(2).suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryConsigneeNameUserAnswersEntry: Arbitrary[(ConsigneeNamePage.type, JsValue)] =
      Arbitrary {
      for {
        page  <- arbitrary[ConsigneeNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }


  implicit lazy val arbitraryWhatIsConsigneeEoriUserAnswersEntry: Arbitrary[(WhatIsConsigneeEoriPage.type, JsValue)] =
      Arbitrary {
      for {
        page  <- arbitrary[WhatIsConsigneeEoriPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }


  implicit lazy val arbitraryConsignorNameUserAnswersEntry: Arbitrary[(ConsignorNamePage.type, JsValue)] =
      Arbitrary {
      for {
        page  <- arbitrary[ConsignorNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAddConsigneeUserAnswersEntry: Arbitrary[(AddConsigneePage.type, JsValue)] =
      Arbitrary {
      for {
        page  <- arbitrary[AddConsigneePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsConsigneeEoriKnownUserAnswersEntry: Arbitrary[(IsConsigneeEoriKnownPage.type, JsValue)] =
      Arbitrary {
      for {
        page  <- arbitrary[IsConsigneeEoriKnownPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAddConsignorUserAnswersEntry: Arbitrary[(AddConsignorPage.type, JsValue)] =
      Arbitrary {
      for {
        page  <- arbitrary[AddConsignorPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsConsignorEoriKnownUserAnswersEntry: Arbitrary[(IsConsignorEoriKnownPage.type, JsValue)] =
      Arbitrary {
      for {
        page  <- arbitrary[IsConsignorEoriKnownPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryConsignorEoriUserAnswersEntry: Arbitrary[(ConsignorEoriPage.type, JsValue)] =
      Arbitrary {
      for {
        page  <- arbitrary[ConsignorEoriPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPrincipalNameUserAnswersEntry: Arbitrary[(PrincipalNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PrincipalNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }


  implicit lazy val arbitraryIsPrincipalEoriKnownUserAnswersEntry: Arbitrary[(IsPrincipalEoriKnownPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsPrincipalEoriKnownPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhatIsPrincipalEoriUserAnswersEntry: Arbitrary[(WhatIsPrincipalEoriPage.type, JsValue)] =
      Arbitrary {
      for {
        page  <- arbitrary[WhatIsPrincipalEoriPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryRepresentativeCapacityUserAnswersEntry: Arbitrary[(RepresentativeCapacityPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[RepresentativeCapacityPage.type]
        value <- arbitrary[RepresentativeCapacity].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryRepresentativeNameUserAnswersEntry: Arbitrary[(RepresentativeNamePage.type, JsValue)] =
      Arbitrary {
      for {
        page  <- arbitrary[RepresentativeNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }


  implicit lazy val arbitraryContainersUsedPageUserAnswersEntry: Arbitrary[(ContainersUsedPage.type, JsValue)] =
    Arbitrary {
      for {
        page <- arbitrary[ContainersUsedPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDeclarationForSomeoneElseUserAnswersEntry: Arbitrary[(DeclarationForSomeoneElsePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DeclarationForSomeoneElsePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDeclarationPlaceUserAnswersEntry: Arbitrary[(DeclarationPlacePage.type, JsValue)] =
      Arbitrary {
      for {
        page  <- arbitrary[DeclarationPlacePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryProcedureTypeUserAnswersEntry: Arbitrary[(ProcedureTypePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ProcedureTypePage.type]
        value <- arbitrary[ProcedureType].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDeclarationTypeUserAnswersEntry: Arbitrary[(DeclarationTypePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DeclarationTypePage.type]
        value <- arbitrary[DeclarationType].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAddSecurityDetailsUserAnswersEntry: Arbitrary[(AddSecurityDetailsPage.type, JsValue)] =
      Arbitrary {
      for {
        page  <- arbitrary[AddSecurityDetailsPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryLocalReferenceNumberUserAnswersEntry: Arbitrary[(LocalReferenceNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[LocalReferenceNumberPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }
}
