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

import java.time.LocalDateTime

import models._
import models.domain.SealDomain
import models.domain.SealDomain.Constants
import models.reference.{Country, CountryCode, CustomsOffice, PackageType}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators {
  self: Generators =>

  // TODO turn PackageType into a trait with three sub classes for Bulk, Unpacked and normal
  implicit lazy val arbitraryPackageType: Arbitrary[PackageType] =
    Arbitrary {
      for {
        code        <- arbitrary[String].suchThat(!PackageType.bulkAndUnpackedCodes.contains(_))
        description <- arbitrary[String]
      } yield PackageType(code, description)
    }

  lazy val arbitraryBulkPackageType: Arbitrary[PackageType] = {
    val bulkCodes = PackageType.bulkCodes

    Arbitrary {
      for {
        code        <- Gen.oneOf(bulkCodes)
        description <- arbitrary[String]
      } yield PackageType(code, description)
    }
  }

  lazy val arbitraryUnPackedPackageType: Arbitrary[PackageType] = {
    val unpackedCodes = PackageType.unpackedCodes

    Arbitrary {
      for {
        code        <- Gen.oneOf(unpackedCodes)
        description <- arbitrary[String]
      } yield PackageType(code, description)
    }
  }

  lazy val arbitraryBulkOrUnpackedPackageType: Arbitrary[PackageType] =
    Arbitrary {
      for {
        bulk           <- arbitraryBulkPackageType.arbitrary
        unpacked       <- arbitraryUnPackedPackageType.arbitrary
        bulkOrUnpacked <- Gen.oneOf(Seq(bulk, unpacked))
      } yield bulkOrUnpacked
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
        addressLine1 <- stringsWithMaxLength(stringMaxLength)
        addressLine2 <- stringsWithMaxLength(stringMaxLength)
        addressLine3 <- stringsWithMaxLength(stringMaxLength)
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
      Gen
        .pick(CountryCode.Constants.countryCodeLength, 'A' to 'Z')
        .map(
          code => CountryCode(code.mkString)
        )
    }

  implicit lazy val arbitraryCountry: Arbitrary[Country] =
    Arbitrary {
      for {
        code <- arbitrary[CountryCode]
        name <- stringsWithMaxLength(stringMaxLength)
      } yield Country(code, name)
    }

  implicit lazy val arbitraryConsignorAddress: Arbitrary[ConsignorAddress] =
    Arbitrary {
      for {
        addressLine1 <- stringsWithMaxLength(stringMaxLength)
        addressLine2 <- stringsWithMaxLength(stringMaxLength)
        addressLine3 <- stringsWithMaxLength(stringMaxLength)
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

  implicit lazy val arbitraryEoriNumber: Arbitrary[EoriNumber] =
    Arbitrary {
      for {
        number <- stringsWithMaxLength(17)
      } yield EoriNumber(number)
    }

  implicit lazy val arbitraryCustomsOffice: Arbitrary[CustomsOffice] = {

    val genRoles = Gen.someOf(Seq("TRA", "DEP", "DES"))

    Arbitrary {
      for {
        id          <- stringsWithMaxLength(stringMaxLength)
        name        <- stringsWithMaxLength(stringMaxLength)
        roles       <- genRoles
        phoneNumber <- Gen.option(stringsWithMaxLength(stringMaxLength))
      } yield CustomsOffice(id, name, roles, phoneNumber)
    }
  }

  implicit lazy val arbitraryLocalDateTimeWithAMPM: Arbitrary[LocalDateTimeWithAMPM] =
    Arbitrary {
      for {
        dateTime <- arbitrary[LocalDateTime]
        amOrPm   <- Gen.oneOf("AM", "PM")
      } yield LocalDateTimeWithAMPM(dateTime, amOrPm)
    }

  implicit lazy val arbitraryTraderAddress: Arbitrary[Address] =
    Arbitrary {
      for {
        buildingAndStreet <- alphaStringsWithMaxLength(Address.Constants.buildingAndStreetLength)
        city              <- alphaStringsWithMaxLength(Address.Constants.cityLength)
        postcode          <- alphaStringsWithMaxLength(Address.Constants.postcodeLength)
      } yield Address(buildingAndStreet, city, postcode)
    }
}

object ModelGenerators extends ModelGenerators with Generators
