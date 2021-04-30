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

package models.messages.goodsitem

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader
import play.api.libs.json._
import xml.XMLWrites

import scala.xml.NodeSeq

sealed trait Package

object Package {

  object Constants {
    val packageCount = 99
  }

  implicit lazy val xmlReader: XmlReader[Package] =
    UnpackedPackage.xmlReader
      .or(RegularPackage.xmlReader)
      .or(BulkPackage.xmlReader)

  implicit lazy val reads: Reads[Package] = {

    implicit class ReadsWithContravariantOr[A](a: Reads[A]) {

      def or[B >: A](b: Reads[B]): Reads[B] =
        a.map[B](identity).orElse(b)
    }

    implicit def convertToSupertype[A, B >: A](a: Reads[A]): Reads[B] =
      a.map(identity)

    BulkPackage.reads or
      UnpackedPackage.reads or
      RegularPackage.reads
  }

  implicit lazy val writes: OWrites[Package] = OWrites {
    case b: BulkPackage     => Json.toJsObject(b)(BulkPackage.writes)
    case u: UnpackedPackage => Json.toJsObject(u)(UnpackedPackage.writes)
    case r: RegularPackage  => Json.toJsObject(r)(RegularPackage.writes)
  }
}

final case class BulkPackage(
  kindOfPackage: String, //CL017
  marksAndNumbers: Option[String]
) extends Package

object BulkPackage {

  implicit val xmlReader: XmlReader[BulkPackage] = {

    import com.lucidchart.open.xtract.__

    (
      (__ \ "KinOfPacGS23").read[String],
      (__ \ "MarNumOfPacGS21").read[String].optional
    ).mapN(apply)
  }

  val validCodes: Set[String] = Set("VQ", "VG", "VL", "VY", "VR", "VS", "VO")

  implicit lazy val reads: Reads[BulkPackage] = {

    import play.api.libs.functional.syntax._

    (__ \ "kindOfPackage")
      .read[String]
      .flatMap[String] {
        kind =>
          if (validCodes.contains(kind)) {
            Reads(
              _ => JsSuccess(kind)
            )
          } else {
            Reads(
              _ => JsError("kindOfPackage must indicate BULK")
            )
          }
      }
      .andKeep(
        (
          (__ \ "kindOfPackage").read[String] and
            (__ \ "marksAndNumbers").readNullable[String]
        )(BulkPackage(_, _))
      )
  }

  implicit lazy val writes: OWrites[BulkPackage] =
    Json.writes[BulkPackage]

  implicit def writesXml: XMLWrites[BulkPackage] = XMLWrites[BulkPackage] {
    packageType =>
      val marksAndNumbers = packageType.marksAndNumbers.fold(NodeSeq.Empty)(
        value => <MarNumOfPacGS21>{value}</MarNumOfPacGS21>
      )

      <PACGS2>
        {marksAndNumbers}
        <KinOfPacGS23>{packageType.kindOfPackage}</KinOfPacGS23>
      </PACGS2>
  }
}

final case class UnpackedPackage(
  kindOfPackage: String,
  numberOfPieces: Int,
  marksAndNumbers: Option[String]
) extends Package

object UnpackedPackage {

  implicit val xmlReader: XmlReader[UnpackedPackage] = {

    import com.lucidchart.open.xtract.__

    (
      (__ \ "KinOfPacGS23").read[String],
      (__ \ "NumOfPieGS25").read[Int],
      (__ \ "MarNumOfPacGS21").read[String].optional
    ).mapN(apply)
  }

  val validCodes: Set[String] = Set("NE", "NF", "NG")

  implicit lazy val reads: Reads[UnpackedPackage] = {

    import play.api.libs.functional.syntax._

    (__ \ "kindOfPackage")
      .read[String]
      .flatMap[String] {
        kind =>
          if (validCodes.contains(kind)) {
            Reads(
              _ => JsSuccess(kind)
            )
          } else {
            Reads(
              _ => JsError("kindOfPackage must indicate UNPACKED")
            )
          }
      }
      .andKeep(
        (
          (__ \ "kindOfPackage").read[String] and
            (__ \ "numberOfPieces").read[Int] and
            (__ \ "marksAndNumbers").readNullable[String]
        )(UnpackedPackage(_, _, _))
      )
  }

  implicit lazy val writes: OWrites[UnpackedPackage] =
    Json.writes[UnpackedPackage]

  implicit def writesXml: XMLWrites[UnpackedPackage] = XMLWrites[UnpackedPackage] {
    packageType =>
      val marksAndNumbers = packageType.marksAndNumbers.fold(NodeSeq.Empty)(
        value => <MarNumOfPacGS21>{value}</MarNumOfPacGS21>
      )

      <PACGS2>
        {marksAndNumbers}
        <KinOfPacGS23>{packageType.kindOfPackage}</KinOfPacGS23>
        <NumOfPieGS25>{packageType.numberOfPieces}</NumOfPieGS25>
      </PACGS2>
  }
}

final case class RegularPackage(
  kindOfPackage: String,
  numberOfPackages: Int,
  marksAndNumbers: String
) extends Package

object RegularPackage {

  implicit val xmlReader: XmlReader[RegularPackage] = {

    import com.lucidchart.open.xtract.__

    (
      (__ \ "KinOfPacGS23").read[String],
      (__ \ "NumOfPacGS24").read[Int],
      (__ \ "MarNumOfPacGS21").read[String]
    ).mapN(apply)
  }

  implicit lazy val reads: Reads[RegularPackage] = {

    import play.api.libs.functional.syntax._

    (__ \ "kindOfPackage")
      .read[String]
      .flatMap[String] {
        kind =>
          if (BulkPackage.validCodes.contains(kind) || UnpackedPackage.validCodes.contains(kind)) {
            Reads(
              _ => JsError("kindOfPackage must not indicate BULK or UNPACKED")
            )
          } else {
            Reads(
              _ => JsSuccess(kind)
            )
          }
      }
      .andKeep(
        (
          (__ \ "kindOfPackage").read[String] and
            (__ \ "numberOfPackages").read[Int] and
            (__ \ "marksAndNumbers").read[String]
        )(RegularPackage(_, _, _))
      )
  }

  implicit lazy val writes: OWrites[RegularPackage] =
    Json.writes[RegularPackage]

  implicit def writesXml: XMLWrites[RegularPackage] = XMLWrites[RegularPackage] {
    packageType =>
      <PACGS2>
        <MarNumOfPacGS21>{packageType.marksAndNumbers}</MarNumOfPacGS21>
        <KinOfPacGS23>{packageType.kindOfPackage}</KinOfPacGS23>
        <NumOfPacGS24>{packageType.numberOfPackages}</NumOfPacGS24>
      </PACGS2>
  }
}
