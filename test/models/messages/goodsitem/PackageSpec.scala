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

package models.messages.goodsitem

import com.lucidchart.open.xtract.XmlReader
import generators.MessagesModelGenerators
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalacheck.Gen.alphaNumChar
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, StreamlinedXmlEquality}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsSuccess, Json}
import xml.XMLWrites._

import scala.xml.NodeSeq

class PackageSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with MessagesModelGenerators with StreamlinedXmlEquality with OptionValues {

  "Package" - {

    "JSON" - {

      "must deserialise to a Bulk Package" in {

        forAll(Gen.oneOf(BulkPackage.validCodes)) {
          kindOfPackage =>
            val json = Json.obj("kindOfPackage" -> kindOfPackage)

            val expectedPackage = BulkPackage(kindOfPackage, None)

            json.validate[Package] mustEqual JsSuccess(expectedPackage)
        }
      }

      "must deserialise to an Unpacked Package" in {

        val gen = for {
          kindOfPackage  <- Gen.oneOf(UnpackedPackage.validCodes)
          numberOfPieces <- Gen.choose(1, 99999)
        } yield (kindOfPackage, numberOfPieces)

        forAll(gen) {
          case (kindOfPackage, numberOfPieces) =>
            val json = Json.obj(
              "kindOfPackage"  -> kindOfPackage,
              "numberOfPieces" -> numberOfPieces
            )

            val expectedPackage = UnpackedPackage(kindOfPackage, numberOfPieces, None)

            json.validate[Package] mustEqual JsSuccess(expectedPackage)
        }
      }

      "must deserialise to a Regular Package" in {

        val gen = for {
          kindOfPackage    <- stringsWithMaxLength(3, alphaNumChar)
          numberOfPackages <- Gen.choose(1, 99999)
          marksAndNumbers  <- stringsWithMaxLength(42)
        } yield (kindOfPackage, numberOfPackages, marksAndNumbers)

        forAll(gen) {
          case (kindOfPackage, numberOfPackages, marksAndNumbers) =>
            whenever(!BulkPackage.validCodes.contains(kindOfPackage) && !UnpackedPackage.validCodes.contains(kindOfPackage)) {

              val json = Json.obj(
                "kindOfPackage"    -> kindOfPackage,
                "numberOfPackages" -> numberOfPackages,
                "marksAndNumbers"  -> marksAndNumbers
              )

              val expectedPackage = RegularPackage(kindOfPackage, numberOfPackages, marksAndNumbers)

              json.validate[Package] mustEqual JsSuccess(expectedPackage)
            }
        }
      }

      "must serialise from a Bulk Package" in {

        forAll(Gen.oneOf(BulkPackage.validCodes), stringsWithMaxLength(42, alphaNumChar)) {
          (kindOfPackage, marksAndNumbers) =>
            val json = Json.obj(
              "kindOfPackage"   -> kindOfPackage,
              "marksAndNumbers" -> marksAndNumbers
            )

            Json.toJson(BulkPackage(kindOfPackage, Some(marksAndNumbers)): Package) mustEqual json
        }
      }

      "must serialise from an Unpacked Package" in {

        forAll(Gen.oneOf(UnpackedPackage.validCodes), stringsWithMaxLength(42, alphaNumChar), Gen.choose(1, 99999)) {
          (kindOfPackage, marksAndNumbers, numberOfPieces) =>
            val json = Json.obj(
              "kindOfPackage"   -> kindOfPackage,
              "marksAndNumbers" -> marksAndNumbers,
              "numberOfPieces"  -> numberOfPieces
            )

            Json.toJson(UnpackedPackage(kindOfPackage, numberOfPieces, Some(marksAndNumbers)): Package) mustEqual json
        }
      }

      "must serialise from a Regular Package" in {

        forAll(Gen.oneOf(UnpackedPackage.validCodes), stringsWithMaxLength(42, alphaNumChar), Gen.choose(1, 99999)) {
          (kindOfPackage, marksAndNumbers, numberOfPackages) =>
            val json = Json.obj(
              "kindOfPackage"    -> kindOfPackage,
              "marksAndNumbers"  -> marksAndNumbers,
              "numberOfPackages" -> numberOfPackages
            )

            Json.toJson(RegularPackage(kindOfPackage, numberOfPackages, marksAndNumbers): Package) mustEqual json
        }
      }
    }

    "XML" - {

      "must serialise BulkPackage to xml" in {

        forAll(arbitrary[BulkPackage]) {
          bulkPackage =>
            val expectedResult =
              <PACGS2>
                {
                bulkPackage.marksAndNumbers.fold(NodeSeq.Empty) {
                  markAndNumbers =>
                    <MarNumOfPacGS21>{markAndNumbers}</MarNumOfPacGS21>
                }
              }
                <KinOfPacGS23>{bulkPackage.kindOfPackage}</KinOfPacGS23>
              </PACGS2>

            bulkPackage.toXml mustEqual expectedResult
        }
      }

      "must deserialise to a Bulk Package" in {

        forAll(arbitrary[BulkPackage]) {
          bulkPackage =>
            val xml =
              <PACGS2>
                {
                bulkPackage.marksAndNumbers.fold(NodeSeq.Empty) {
                  markAndNumbers =>
                    <MarNumOfPacGS21>{markAndNumbers}</MarNumOfPacGS21>
                }
              }
                <KinOfPacGS23>{bulkPackage.kindOfPackage}</KinOfPacGS23>
              </PACGS2>

            val result = XmlReader.of[Package].read(xml).toOption.value

            result mustBe bulkPackage

        }
      }

      "must serialise UnpackedPackage to xml" in {

        forAll(arbitrary[UnpackedPackage]) {
          unpackedPackage =>
            val expectedResult =
              <PACGS2>
                {
                unpackedPackage.marksAndNumbers.fold(NodeSeq.Empty) {
                  markAndNumbers =>
                    <MarNumOfPacGS21>{markAndNumbers}</MarNumOfPacGS21>
                }
              }
                <KinOfPacGS23>{unpackedPackage.kindOfPackage}</KinOfPacGS23>
                <NumOfPieGS25>{unpackedPackage.numberOfPieces}</NumOfPieGS25>
              </PACGS2>

            unpackedPackage.toXml mustEqual expectedResult
        }
      }

      "must deserialise to an Unpacked Package" in {

        forAll(arbitrary[UnpackedPackage]) {
          unpackedPackage =>
            val xml =
              <PACGS2>
                {
                unpackedPackage.marksAndNumbers.fold(NodeSeq.Empty) {
                  markAndNumbers =>
                    <MarNumOfPacGS21>{markAndNumbers}</MarNumOfPacGS21>
                }
              }
                <KinOfPacGS23>{unpackedPackage.kindOfPackage}</KinOfPacGS23>
                <NumOfPieGS25>{unpackedPackage.numberOfPieces}</NumOfPieGS25>
              </PACGS2>

            val result = XmlReader.of[Package].read(xml).toOption.value

            result mustBe unpackedPackage
        }
      }

      "must serialise RegularPackage to xml" in {

        forAll(arbitrary[RegularPackage]) {
          regularPackage =>
            val expectedResult =
              <PACGS2>
                <MarNumOfPacGS21>{regularPackage.marksAndNumbers}</MarNumOfPacGS21>
                <KinOfPacGS23>{regularPackage.kindOfPackage}</KinOfPacGS23>
                <NumOfPacGS24>{regularPackage.numberOfPackages}</NumOfPacGS24>
              </PACGS2>

            regularPackage.toXml mustEqual expectedResult
        }
      }

      "must deserialise to a Regular Package" in {
        forAll(arbitrary[RegularPackage]) {
          regularPackage =>
            val xml =
              <PACGS2>
                <MarNumOfPacGS21>{regularPackage.marksAndNumbers}</MarNumOfPacGS21>
                <KinOfPacGS23>{regularPackage.kindOfPackage}</KinOfPacGS23>
                <NumOfPacGS24>{regularPackage.numberOfPackages}</NumOfPacGS24>
              </PACGS2>

            val result = XmlReader.of[Package].read(xml).toOption.value

            result mustBe regularPackage
        }
      }

    }
  }
}
