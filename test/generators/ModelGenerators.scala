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
import models.domain.SealDomain
import models.domain.SealDomain.Constants
import models.reference.{Country, CountryCode, CustomsOffice, PackageType}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators {

  self: Generators =>

  implicit lazy val arbitraryPackageType: Arbitrary[PackageType] = {
    Arbitrary {
      for {
        code        <- arbitrary[String]
        description <- arbitrary[String]
      } yield PackageType(code, description)
    }
  }

  implicit lazy val arbitraryGuaranteeType: Arbitrary[GuaranteeType] =
    Arbitrary {
      Gen.oneOf(GuaranteeType.values)
    }

  implicit lazy val arbitrarySealDomain: Arbitrary[SealDomain] =
    Arbitrary {
      for {
        sealNumber <- stringsWithMaxLength(Constants.sealNumberOrMarkLength)
      } yield SealDomain(sealNumber)
    }

  implicit lazy val arbitraryConsigneeAddress: Arbitrary[ConsigneeAddress] =
    Arbitrary {
      for {
        addressLine1 <- arbitrary[String]
        addressLine2 <- arbitrary[String]
        addressLine3 <- arbitrary[String]
        addressLine4 <- arbitrary[Country]
      } yield ConsigneeAddress(addressLine1, addressLine2, addressLine3, addressLine4)
    }

  implicit lazy val arbitraryPrincipalAddress: Arbitrary[PrincipalAddress] =
    Arbitrary {
      for {
        numberAndStreet <- stringsWithMaxLength(PrincipalAddress.Constants.numberAndStreetLength)
        town            <- stringsWithMaxLength(PrincipalAddress.Constants.townLength)
        postcode        <- stringsWithMaxLength(PrincipalAddress.Constants.postcodeLength)
      } yield PrincipalAddress(numberAndStreet, town, postcode)
    }

  implicit lazy val arbitraryCountryCode: Arbitrary[CountryCode] =
    Arbitrary {
      Gen.pick(CountryCode.Constants.countryCodeLength, 'A' to 'Z').map(code => CountryCode(code.mkString))
    }

  implicit lazy val arbitraryCountry: Arbitrary[Country] = {
    Arbitrary {
      for {
        code <- arbitrary[CountryCode]
        name <- arbitrary[String]
      } yield Country(code, name)
    }
  }

  implicit lazy val arbitraryConsignorAddress: Arbitrary[ConsignorAddress] =
    Arbitrary {
      for {
        addressLine1 <- arbitrary[String]
        addressLine2 <- arbitrary[String]
        addressLine3 <- arbitrary[String]
        addressLine4 <- arbitrary[Country]
      } yield ConsignorAddress(addressLine1, addressLine2, addressLine3, addressLine4)
    }

  implicit lazy val arbitraryRepresentativeCapacity: Arbitrary[RepresentativeCapacity] =
    Arbitrary {
      Gen.oneOf(RepresentativeCapacity.values)
    }

  implicit lazy val arbitraryProcedureType: Arbitrary[ProcedureType] =
    Arbitrary {
      Gen.oneOf(ProcedureType.values)
    }

  implicit lazy val arbitraryDeclarationType: Arbitrary[DeclarationType] =
    Arbitrary {
      Gen.oneOf(DeclarationType.values)
    }

  implicit lazy val arbitraryLocalReferenceNumber: Arbitrary[LocalReferenceNumber] =
    Arbitrary {
      for {
        lrn <- alphaNumericWithMaxLength(22)
      } yield new LocalReferenceNumber(lrn)
    }

  implicit lazy val arbitraryEoriNumber: Arbitrary[EoriNumber] = {
    Arbitrary {
      for {
        number <- stringsWithMaxLength(17)
      } yield EoriNumber(number)
    }
  }

  implicit lazy val arbitraryCustomsOffice: Arbitrary[CustomsOffice] = {

    val genRoles = Gen.someOf(Seq("TRA", "DEP", "DES"))

    Arbitrary {
      for {
        id          <- arbitrary[String]
        name        <- arbitrary[String]
        roles       <- genRoles
        phoneNumber <- Gen.option(arbitrary[String])
      } yield CustomsOffice(id, name, roles, phoneNumber)
    }
  }
}
