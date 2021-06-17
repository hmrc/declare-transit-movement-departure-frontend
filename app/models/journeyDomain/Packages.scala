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

package models.journeyDomain

import cats.implicits._
import models.Index
import models.reference.PackageType
import pages.PackageTypePage
import pages.addItems._

sealed trait Packages

object Packages {

  def packagesReader(itemIndex: Index, referenceIndex: Index): UserAnswersReader[Packages] =
    PackageTypePage(itemIndex, referenceIndex).reader.flatMap {
      case packageType if PackageType.unpackedCodes.contains(packageType.code) =>
        UnpackedPackages.unpackedPackagesReader(itemIndex, referenceIndex).widen[Packages]
      case packageType if PackageType.bulkCodes.contains(packageType.code) =>
        BulkPackages.bulkPackageReader(itemIndex, referenceIndex).widen[Packages]
      case _ =>
        OtherPackages.otherPackageReader(itemIndex, referenceIndex).widen[Packages]
    }

  final case class UnpackedPackages(
    packageType: PackageType,
    totalPieces: Int,
    markOrNumber: Option[String]
  ) extends Packages

  object UnpackedPackages {

    def unpackedPackagesReader(itemIndex: Index, referenceIndex: Index): UserAnswersReader[UnpackedPackages] =
      (
        PackageTypePage(itemIndex, referenceIndex).reader,
        TotalPiecesPage(itemIndex, referenceIndex).reader,
        AddMarkPage(itemIndex, referenceIndex).filterOptionalDependent(identity) {
          DeclareMarkPage(itemIndex, referenceIndex).reader
        }
      ).tupled.map((UnpackedPackages.apply _).tupled)
  }

  final case class BulkPackages(
    packageType: PackageType,
    markOrNumber: Option[String]
  ) extends Packages

  object BulkPackages {

    def bulkPackageReader(itemIndex: Index, referenceIndex: Index): UserAnswersReader[BulkPackages] =
      (
        PackageTypePage(itemIndex, referenceIndex).reader,
        AddMarkPage(itemIndex, referenceIndex).filterOptionalDependent(identity) {
          DeclareMarkPage(itemIndex, referenceIndex).reader
        }
      ).tupled.map((BulkPackages.apply _).tupled)
  }

  final case class OtherPackages(packageType: PackageType, howManyPackagesPage: Int, markOrNumber: String) extends Packages

  object OtherPackages {

    def otherPackageReader(itemIndex: Index, referenceIndex: Index): UserAnswersReader[OtherPackages] =
      (
        PackageTypePage(itemIndex, referenceIndex).reader,
        HowManyPackagesPage(itemIndex, referenceIndex).reader,
        DeclareMarkPage(itemIndex, referenceIndex).reader
      ).tupled.map((OtherPackages.apply _).tupled)
  }
}
