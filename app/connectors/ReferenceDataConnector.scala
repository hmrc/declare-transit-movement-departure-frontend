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

package connectors

import config.FrontendAppConfig
import javax.inject.Inject
import models._
import models.reference._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

class ReferenceDataConnector @Inject()(config: FrontendAppConfig, http: HttpClient) {

  def getCustomsOffices()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[CustomsOfficeList] = {
    val serviceUrl = s"${config.referenceDataUrl}/customs-offices"
    http.GET[Seq[CustomsOffice]](serviceUrl).map(CustomsOfficeList(_))
  }

  def getCustomsOfficesOfTheCountry(countryCode: CountryCode)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[CustomsOfficeList] = {
    val serviceUrl = s"${config.referenceDataUrl}/customs-offices/${countryCode.code}"
    http.GET[Seq[CustomsOffice]](serviceUrl).map(CustomsOfficeList(_))
  }

  def getCountryList()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[CountryList] = {
    val serviceUrl = s"${config.referenceDataUrl}/countries-full-list"
    http.GET[Seq[Country]](serviceUrl).map(CountryList(_))
  }

  def getTransitCountryList()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[CountryList] = {
    val serviceUrl = s"${config.referenceDataUrl}/transit-countries"
    http.GET[Seq[Country]](serviceUrl).map(CountryList(_))
  }

  def getTransportModes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[TransportModeList] = {
    val serviceUrl = s"${config.referenceDataUrl}/transport-modes"
    http.GET[Seq[TransportMode]](serviceUrl).map(TransportModeList)
  }

  def getOfficeOfTransitList()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[OfficeOfTransitList] = {
    val serviceUrl = s"${config.referenceDataUrl}/office-transit"
    http.GET[Seq[OfficeOfTransit]](serviceUrl).map(OfficeOfTransitList)
  }

  def getOfficeOfTransit(id: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[OfficeOfTransit] = {
    val serviceUrl = s"${config.referenceDataUrl}/office-transit/$id"
    http.GET[OfficeOfTransit](serviceUrl)
  }

  def getPackageTypes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[PackageTypeList] = {
    val serviceUrl = s"${config.referenceDataUrl}/kinds-of-package"
    http.GET[Seq[PackageType]](serviceUrl).map(PackageTypeList(_))
  }

  def getPreviousReferencesDocumentTypes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[PreviousReferencesDocumentTypeList] = {
    val serviceUrl = s"${config.referenceDataUrl}/previous-document-type"
    http.GET[Seq[PreviousReferencesDocumentType]](serviceUrl).map(PreviousReferencesDocumentTypeList)
  }

  def getDocumentTypes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[DocumentTypeList] = {
    val serviceUrl = s"${config.referenceDataUrl}/document-types"
    http.GET[Seq[DocumentType]](serviceUrl).map(DocumentTypeList)
  }

  def getSpecialMention()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[SpecialMentionList] = {
    val serviceUrl = s"${config.referenceDataUrl}/special-mention"
    http.GET[Seq[SpecialMention]](serviceUrl).map(SpecialMentionList)
  }

  def getDangerousGoodsCodeList()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[DangerousGoodsCodeList] = {
    val serviceUrl = s"${config.referenceDataUrl}/dangerous-goods-code"
    http.GET[Seq[DangerousGoodsCode]](serviceUrl).map(DangerousGoodsCodeList)
  }

  def getDangerousGoodsCode(code: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[DangerousGoodsCode] = {
    val serviceUrl = s"${config.referenceDataUrl}/dangerous-goods-code/$code"
    http.GET[DangerousGoodsCode](serviceUrl)
  }

  def getMethodOfPaymentList()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[MethodOfPaymentList] = {
    val serviceUrl = s"${config.referenceDataUrl}/method-of-payment"
    http.GET[Seq[MethodOfPayment]](serviceUrl).map(MethodOfPaymentList)
  }

  def getCircumstanceIndicatorList()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[CircumstanceIndicatorList] = {
    val serviceUrl = s"${config.referenceDataUrl}/circumstance-indicators"
    http.GET[Seq[CircumstanceIndicator]](serviceUrl).map(CircumstanceIndicatorList)
  }

}
