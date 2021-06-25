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

package services

import commonTestUtils.UserAnswersSpecHelper
import models.userAnswerScenarios.{Scenario1, Scenario2, Scenario3, Scenario4}
import models.messages.InterchangeControlReference
import org.mockito.Mockito.{reset, when}
import org.scalatest.EitherValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import repositories.{InterchangeControlReferenceIdRepository, MongoSuite}
import utils.{MockDateTimeService, XMLComparatorSpec, XSDSchemaValidationSpec}
import xml.XMLWrites._

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Success

class UserAnswersToXmlConversionSpec
    extends AnyFreeSpec
    with Matchers
    with UserAnswersSpecHelper
    with XMLComparatorSpec
    with XSDSchemaValidationSpec
    with MongoSuite
    with ScalaFutures
    with GuiceOneAppPerSuite
    with IntegrationPatience
    with MockDateTimeService
    with EitherValues {

  private val mockInterchangeControlReference = mock[InterchangeControlReferenceIdRepository]

  implicit override lazy val app: Application = new GuiceApplicationBuilder()
    .overrides(
      bind[DateTimeService].toInstance(mockTimeService),
      bind[InterchangeControlReferenceIdRepository].toInstance(mockInterchangeControlReference)
    )
    .build()

  class Setup {

    val service: DeclarationRequestService = app.injector.instanceOf[DeclarationRequestService]

    reset(mockTimeService)

    when(mockInterchangeControlReference.nextInterchangeControlReferenceId())
      .thenReturn(Future.successful(InterchangeControlReference("20201212", 1)))

    when(mockTimeService.currentDateTime).thenReturn(LocalDateTime.of(2020, 12, 12, 20, 30))

    when(mockTimeService.dateFormatted).thenReturn("20201212")

    database.flatMap(_.drop()).futureValue
  }

  "UserAnswers to XML conversion" - {
    "Scenario 1: " in new Setup {

      val expectedXml = <CC015B>
        <SynIdeMES1>UNOC</SynIdeMES1>
        <SynVerNumMES2>3</SynVerNumMES2>
        <MesRecMES6>NCTS</MesRecMES6>
        <DatOfPreMES9>20201212</DatOfPreMES9>
        <TimOfPreMES10>2030</TimOfPreMES10>
        <IntConRefMES11>DF202012121</IntConRefMES11>
        <AppRefMES14>NCTS</AppRefMES14>
        <TesIndMES18>0</TesIndMES18>
        <MesIdeMES19>1</MesIdeMES19>
        <MesTypMES20>GB015B</MesTypMES20>
        <HEAHEA>
          <RefNumHEA4>ABCD1234567890123</RefNumHEA4>
          <TypOfDecHEA24>T2</TypOfDecHEA24>
          <CouOfDesCodHEA30>DC</CouOfDesCodHEA30>
          <PlaOfLoaCodHEA46>LoadPLace</PlaOfLoaCodHEA46>
          <CouOfDisCodHEA55>SC</CouOfDisCodHEA55>
          <CusSubPlaHEA66>CUSAPPLOC</CusSubPlaHEA66>
          <InlTraModHEA75>4</InlTraModHEA75>
          <TraModAtBorHEA76>4</TraModAtBorHEA76>
          <NatOfMeaOfTraAtDHEA80>ND</NatOfMeaOfTraAtDHEA80>
          <NatOfMeaOfTraCroHEA87>ND</NatOfMeaOfTraCroHEA87>
          <TypOfMeaOfTraCroHEA88>4</TypOfMeaOfTraCroHEA88>
          <ConIndHEA96>1</ConIndHEA96>
          <DiaLanIndAtDepHEA254>EN</DiaLanIndAtDepHEA254>
          <NCTSAccDocHEA601LNG>EN</NCTSAccDocHEA601LNG>
          <TotNumOfIteHEA305>2</TotNumOfIteHEA305>
          <TotNumOfPacHEA306>1</TotNumOfPacHEA306>
          <TotGroMasHEA307>50001.0</TotGroMasHEA307>
          <DecDatHEA383>20201212</DecDatHEA383>
          <DecPlaHEA394>XX1 1XX</DecPlaHEA394>
          <SpeCirIndHEA1>A</SpeCirIndHEA1>
          <SecHEA358>1</SecHEA358>
          <ConRefNumHEA>SomeConv</ConRefNumHEA>
          <CodPlUnHEA357>PlaceOfUnloadingPage</CodPlUnHEA357>
        </HEAHEA>
        <TRAPRIPC1>
          <NamPC17>PrincipalName</NamPC17>
          <StrAndNumPC122>PrincipalStreet</StrAndNumPC122>
          <PosCodPC123>AA1 1AA</PosCodPC123>
          <CitPC124>PrincipalTown</CitPC124>
          <CouPC125>FR</CouPC125>
        </TRAPRIPC1>
        <CUSOFFDEPEPT>
          <RefNumEPT1>OOD1234A</RefNumEPT1>
        </CUSOFFDEPEPT>
        <CUSOFFTRARNS>
          <RefNumRNS1>TOP12341</RefNumRNS1>
          <ArrTimTRACUS085>202005050512</ArrTimTRACUS085>
        </CUSOFFTRARNS>
        <CUSOFFTRARNS>
          <RefNumRNS1>TOP12342</RefNumRNS1>
          <ArrTimTRACUS085>202005072112</ArrTimTRACUS085>
        </CUSOFFTRARNS>
        <CUSOFFDESEST>
          <RefNumEST1>DOP1234A</RefNumEST1>
        </CUSOFFDESEST>
        <REPREP>
          <NamREP5>John Doe</NamREP5>
          <RepCapREP18>direct</RepCapREP18>
        </REPREP>
        <SEAINFSLI>
          <SeaNumSLI2>2</SeaNumSLI2>
          <SEAIDSID>
            <SeaIdeSID1>SEAL1</SeaIdeSID1>
          </SEAIDSID>
          <SEAIDSID>
            <SeaIdeSID1>SEAL2</SeaIdeSID1>
          </SEAIDSID>
        </SEAINFSLI>
        <GUAGUA>
          <GuaTypGUA1>1</GuaTypGUA1>
          <GUAREFREF>
            <GuaRefNumGRNREF1>GUA1Ref</GuaRefNumGRNREF1>
            <AccCodREF6>1234</AccCodREF6>
          </GUAREFREF>
        </GUAGUA>
        <GUAGUA>
          <GuaTypGUA1>0</GuaTypGUA1>
          <GUAREFREF>
            <GuaRefNumGRNREF1>GUA2Ref</GuaRefNumGRNREF1>
            <AccCodREF6>4321</AccCodREF6>
          </GUAREFREF>
        </GUAGUA>
        <GOOITEGDS>
          <IteNumGDS7>1</IteNumGDS7>
          <ComCodTarCodGDS10>ComoCode1</ComCodTarCodGDS10>
          <GooDesGDS23>ItemOnesDescription</GooDesGDS23>
          <GroMasGDS46>25000</GroMasGDS46>
          <NetMasGDS48>12342</NetMasGDS48>
          <MetOfPayGDI12>T</MetOfPayGDI12>
          <ComRefNumGIM1>GD1CRN</ComRefNumGIM1>
          <UNDanGooCodGDI1>GD1C</UNDanGooCodGDI1>
          <PREADMREFAR2>
            <PreDocTypAR21>GD1PR1</PreDocTypAR21>
            <PreDocRefAR26>GD1PR1Ref</PreDocRefAR26>
            <ComOfInfAR29>GD1PR1Info</ComOfInfAR29>
          </PREADMREFAR2>
          <PREADMREFAR2>
            <PreDocTypAR21>GD1PR2</PreDocTypAR21>
            <PreDocRefAR26>GD1PR2Ref</PreDocRefAR26>
          </PREADMREFAR2>
          <PRODOCDC2>
            <DocTypDC21>G1D1</DocTypDC21>
            <DocRefDC23>G1D1Ref</DocRefDC23>
            <ComOfInfDC25>G1D1Info</ComOfInfDC25>
          </PRODOCDC2>
          <PRODOCDC2>
            <DocTypDC21>G1D2</DocTypDC21>
            <DocRefDC23>G1D2Ref</DocRefDC23>
          </PRODOCDC2>
          <SPEMENMT2>
            <AddInfMT21>10000EURGUA1Ref</AddInfMT21>
            <AddInfCodMT23>CAL</AddInfCodMT23>
          </SPEMENMT2>
          <SPEMENMT2>
            <AddInfMT21>500GBPGUA2Ref</AddInfMT21>
            <AddInfCodMT23>CAL</AddInfCodMT23>
          </SPEMENMT2>
          <SPEMENMT2>
            <AddInfMT21>GD1SPMT1Info</AddInfMT21>
            <AddInfCodMT23>GD1S1</AddInfCodMT23>
          </SPEMENMT2>
          <SPEMENMT2>
            <AddInfMT21>GD1S2Info</AddInfMT21>
            <AddInfCodMT23>DG0</AddInfCodMT23>
            <ExpFroCouMT25>GB</ExpFroCouMT25>
          </SPEMENMT2>
          <TRACONCO2>
            <NamCO27>ConorName</NamCO27>
            <StrAndNumCO222>ConorLine1</StrAndNumCO222>
            <PosCodCO223>ConorL3</PosCodCO223>
            <CitCO224>ConorLine2</CitCO224>
            <CouCO225>GA</CouCO225>
            <TINCO259>Conor123</TINCO259>
          </TRACONCO2>
          <TRACONCE2>
            <NamCE27>ConeeName</NamCE27>
            <StrAndNumCE222>ConeeLine1</StrAndNumCE222>
            <PosCodCE223>ConeeL3</PosCodCE223>
            <CitCE224>ConeeLine2</CitCE224>
            <CouCE225>GA</CouCE225>
            <TINCE259>Conee123</TINCE259>
          </TRACONCE2>
          <CONNR2>
            <ConNumNR21>GD1CN1NUM1</ConNumNR21>
          </CONNR2>
          <CONNR2>
            <ConNumNR21>GD1CN2NUMS</ConNumNR21>
          </CONNR2>
          <PACGS2>
            <KinOfPacGS23>VQ</KinOfPacGS23>
          </PACGS2>
          <PACGS2>
            <MarNumOfPacGS21>GD1PK2MK</MarNumOfPacGS21>
            <KinOfPacGS23>NE</KinOfPacGS23>
            <NumOfPieGS25>12</NumOfPieGS25>
          </PACGS2>
          <PACGS2>
            <MarNumOfPacGS21>GD1PK3MK</MarNumOfPacGS21>
            <KinOfPacGS23>BAG</KinOfPacGS23>
            <NumOfPacGS24>2</NumOfPacGS24>
          </PACGS2>
          <TRACORSECGOO021>
            <TINTRACORSECGOO028>GD1SECCONOR</TINTRACORSECGOO028>
          </TRACORSECGOO021>
          <TRACONSECGOO013>
            <TINTRACONSECGOO020>GD1SECCONEE</TINTRACONSECGOO020>
          </TRACONSECGOO013>
        </GOOITEGDS>
        <GOOITEGDS>
          <IteNumGDS7>2</IteNumGDS7>
          <GooDesGDS23>ItemTwosDescription</GooDesGDS23>
          <GroMasGDS46>25001</GroMasGDS46>
          <MetOfPayGDI12>U</MetOfPayGDI12>
          <ComRefNumGIM1>GD2CRN</ComRefNumGIM1>
          <PREADMREFAR2>
            <PreDocTypAR21>GD2PR1</PreDocTypAR21>
            <PreDocRefAR26>GD2PR1Ref</PreDocRefAR26>
            <ComOfInfAR29>GD2PR1Info</ComOfInfAR29>
          </PREADMREFAR2>
          <PRODOCDC2>
            <DocTypDC21>G2D1</DocTypDC21>
            <DocRefDC23>G2D1Ref</DocRefDC23>
            <ComOfInfDC25>G2D1Info</ComOfInfDC25>
          </PRODOCDC2>
          <SPEMENMT2>
            <AddInfMT21>GD2S1Info</AddInfMT21>
            <AddInfCodMT23>GD2S1</AddInfCodMT23>
          </SPEMENMT2>
          <TRACONCO2>
            <NamCO27>ConorName</NamCO27>
            <StrAndNumCO222>ConorLine1</StrAndNumCO222>
            <PosCodCO223>ConorL3</PosCodCO223>
            <CitCO224>ConorLine2</CitCO224>
            <CouCO225>GB</CouCO225>
          </TRACONCO2>
          <TRACONCE2>
            <NamCE27>ConeeName</NamCE27>
            <StrAndNumCE222>ConeeLine1</StrAndNumCE222>
            <PosCodCE223>ConeeL3</PosCodCE223>
            <CitCE224>ConeeLine2</CitCE224>
            <CouCE225>GB</CouCE225>
          </TRACONCE2>
          <CONNR2>
            <ConNumNR21>GD2CN1NUM1</ConNumNR21>
          </CONNR2>
          <PACGS2>
            <KinOfPacGS23>VQ</KinOfPacGS23>
          </PACGS2>
          <PACGS2>
            <MarNumOfPacGS21>GD2PK2MK</MarNumOfPacGS21>
            <KinOfPacGS23>NE</KinOfPacGS23>
            <NumOfPieGS25>12</NumOfPieGS25>
          </PACGS2>
          <PACGS2>
            <MarNumOfPacGS21>GD2PK3MK</MarNumOfPacGS21>
            <KinOfPacGS23>BAG</KinOfPacGS23>
            <NumOfPacGS24>2</NumOfPacGS24>
          </PACGS2>
          <TRACORSECGOO021>
            <NamTRACORSECGOO025>GD2SECCONORName</NamTRACORSECGOO025>
            <StrNumTRACORSECGOO027>GD2CONORL1</StrNumTRACORSECGOO027>
            <PosCodTRACORSECGOO026>GD2CONL1</PosCodTRACORSECGOO026>
            <CitTRACORSECGOO022>GD2CONORL2</CitTRACORSECGOO022>
            <CouCodTRACORSECGOO023>GB</CouCodTRACORSECGOO023>
          </TRACORSECGOO021>
          <TRACONSECGOO013>
            <NamTRACONSECGOO017>GD2SECCONEEName</NamTRACONSECGOO017>
            <StrNumTRACONSECGOO019>GD2CONEEL1</StrNumTRACONSECGOO019>
            <PosCodTRACONSECGOO018>GD2CEEL1</PosCodTRACONSECGOO018>
            <CityTRACONSECGOO014>GD2CONEEL2</CityTRACONSECGOO014>
            <CouCodTRACONSECGOO015>GB</CouCodTRACONSECGOO015>
          </TRACONSECGOO013>
        </GOOITEGDS>
        <ITI>
          <CouOfRouCodITI1>CA</CouOfRouCodITI1>
        </ITI>
        <ITI>
          <CouOfRouCodITI1>CB</CouOfRouCodITI1>
        </ITI>
        <CARTRA100>
          <TINCARTRA254>CarrierEori</TINCARTRA254>
        </CARTRA100>
      </CC015B>

      val generatedXml = service.convert(Scenario1.userAnswers).futureValue.right.value.toXml.map(scala.xml.Utility.trim)

      validate(generatedXml.toString()) mustBe Success(())

      generatedXml xmlMustEqual expectedXml.map(scala.xml.Utility.trim)
    }
    "Scenario 2: " in new Setup {

      val expectedXml = <CC015B>
        <SynIdeMES1>UNOC</SynIdeMES1>
        <SynVerNumMES2>3</SynVerNumMES2>
        <MesRecMES6>NCTS</MesRecMES6>
        <DatOfPreMES9>20201212</DatOfPreMES9>
        <TimOfPreMES10>2030</TimOfPreMES10>
        <IntConRefMES11>DF202012121</IntConRefMES11>
        <AppRefMES14>NCTS</AppRefMES14>
        <TesIndMES18>0</TesIndMES18>
        <MesIdeMES19>1</MesIdeMES19>
        <MesTypMES20>GB015B</MesTypMES20>
        <HEAHEA>
          <RefNumHEA4>ABCD1234567890123</RefNumHEA4>
          <TypOfDecHEA24>T2</TypOfDecHEA24>
          <CouOfDesCodHEA30>DC</CouOfDesCodHEA30>
          <AutLocOfGooCodHEA41>AuthLocationCode</AutLocOfGooCodHEA41>
          <CouOfDisCodHEA55>SC</CouOfDisCodHEA55>
          <InlTraModHEA75>3</InlTraModHEA75>
          <TraModAtBorHEA76>3</TraModAtBorHEA76>
          <IdeOfMeaOfTraAtDHEA78>SomeIdAtDeparture</IdeOfMeaOfTraAtDHEA78>
          <NatOfMeaOfTraAtDHEA80>ND</NatOfMeaOfTraAtDHEA80>
          <IdeOfMeaOfTraCroHEA85>IDCBP</IdeOfMeaOfTraCroHEA85>
          <NatOfMeaOfTraCroHEA87>NC</NatOfMeaOfTraCroHEA87>
          <TypOfMeaOfTraCroHEA88>8</TypOfMeaOfTraCroHEA88>
          <ConIndHEA96>0</ConIndHEA96>
          <DiaLanIndAtDepHEA254>EN</DiaLanIndAtDepHEA254>
          <NCTSAccDocHEA601LNG>EN</NCTSAccDocHEA601LNG>
          <TotNumOfIteHEA305>1</TotNumOfIteHEA305>
          <TotNumOfPacHEA306>1</TotNumOfPacHEA306>
          <TotGroMasHEA307>25000.0</TotGroMasHEA307>
          <DecDatHEA383>20201212</DecDatHEA383>
          <DecPlaHEA394>XX1 1XX</DecPlaHEA394>
        </HEAHEA>
        <TRAPRIPC1>
          <TINPC159>PRINCEORI</TINPC159>
        </TRAPRIPC1>
        <TRACONCO1>
          <NamCO17>ConsignorName</NamCO17>
          <StrAndNumCO122>ConorLine1</StrAndNumCO122>
          <PosCodCO123>ConorL3</PosCodCO123>
          <CitCO124>ConorLine2</CitCO124>
          <CouCO125>CN</CouCO125>
        </TRACONCO1>
        <TRACONCE1>
          <NamCE17>ConsigneeName</NamCE17>
          <StrAndNumCE122>ConeeLine1</StrAndNumCE122>
          <PosCodCE123>ConeeL3</PosCodCE123>
          <CitCE124>ConeeLine2</CitCE124>
          <CouCE125>CN</CouCE125>
        </TRACONCE1>
        <CUSOFFDEPEPT>
          <RefNumEPT1>OOD1234A</RefNumEPT1>
        </CUSOFFDEPEPT>
        <CUSOFFTRARNS>
          <RefNumRNS1>TOP12341</RefNumRNS1>
        </CUSOFFTRARNS>
        <CUSOFFTRARNS>
          <RefNumRNS1>TOP12342</RefNumRNS1>
        </CUSOFFTRARNS>
        <CUSOFFDESEST>
          <RefNumEST1>DOP1234A</RefNumEST1>
        </CUSOFFDESEST>
        <CONRESERS>
          <ConResCodERS16>A3</ConResCodERS16>
          <DatLimERS69>20201212</DatLimERS69>
        </CONRESERS>
        <GUAGUA>
          <GuaTypGUA1>3</GuaTypGUA1>
          <GUAREFREF>
            <OthGuaRefREF4>GUA1Reference</OthGuaRefREF4>
          </GUAREFREF>
        </GUAGUA>
        <GOOITEGDS>
          <IteNumGDS7>1</IteNumGDS7>
          <GooDesGDS23>ItemOnesDescription</GooDesGDS23>
          <GroMasGDS46>25000</GroMasGDS46>
          <PACGS2>
            <KinOfPacGS23>VQ</KinOfPacGS23>
          </PACGS2>
          <PACGS2>
            <MarNumOfPacGS21>GD1PK2MK</MarNumOfPacGS21>
            <KinOfPacGS23>NE</KinOfPacGS23>
            <NumOfPieGS25>12</NumOfPieGS25>
          </PACGS2>
          <PACGS2>
            <MarNumOfPacGS21>GD1PK3MK</MarNumOfPacGS21>
            <KinOfPacGS23>BAG</KinOfPacGS23>
            <NumOfPacGS24>2</NumOfPacGS24>
          </PACGS2>
        </GOOITEGDS>
      </CC015B>

      val generatedXml = service.convert(Scenario2.userAnswers).futureValue.right.value.toXml.map(scala.xml.Utility.trim)

      validate(generatedXml.toString()) mustBe Success(())

      generatedXml xmlMustEqual expectedXml.map(scala.xml.Utility.trim)
    }
    "Scenario 3: " in new Setup {

      val expectedXml = <CC015B>
        <SynIdeMES1>UNOC</SynIdeMES1>
        <SynVerNumMES2>3</SynVerNumMES2>
        <MesRecMES6>NCTS</MesRecMES6>
        <DatOfPreMES9>20201212</DatOfPreMES9>
        <TimOfPreMES10>2030</TimOfPreMES10>
        <IntConRefMES11>DF202012121</IntConRefMES11>
        <AppRefMES14>NCTS</AppRefMES14>
        <TesIndMES18>0</TesIndMES18>
        <MesIdeMES19>1</MesIdeMES19>
        <MesTypMES20>GB015B</MesTypMES20>
        <HEAHEA>
          <RefNumHEA4>ABCD1234567890123</RefNumHEA4>
          <TypOfDecHEA24>T2</TypOfDecHEA24>
          <CouOfDesCodHEA30>DC</CouOfDesCodHEA30>
          <AgrLocOfGooCodHEA38>Pre-lodge</AgrLocOfGooCodHEA38>
          <PlaOfLoaCodHEA46>LoadPLace</PlaOfLoaCodHEA46>
          <CouOfDisCodHEA55>SC</CouOfDisCodHEA55>
          <InlTraModHEA75>5</InlTraModHEA75>
          <TraModAtBorHEA76>5</TraModAtBorHEA76>
          <TypOfMeaOfTraCroHEA88>5</TypOfMeaOfTraCroHEA88>
          <ConIndHEA96>1</ConIndHEA96>
          <DiaLanIndAtDepHEA254>EN</DiaLanIndAtDepHEA254>
          <NCTSAccDocHEA601LNG>EN</NCTSAccDocHEA601LNG>
          <TotNumOfIteHEA305>1</TotNumOfIteHEA305>
          <TotNumOfPacHEA306>1</TotNumOfPacHEA306>
          <TotGroMasHEA307>25000.0</TotGroMasHEA307>
          <DecDatHEA383>20201212</DecDatHEA383>
          <DecPlaHEA394>XX1 1XX</DecPlaHEA394>
          <SecHEA358>1</SecHEA358>
          <CodPlUnHEA357>PlaceOfUnloadingPage</CodPlUnHEA357>
        </HEAHEA>
        <TRAPRIPC1>
          <NamPC17>PrincipalName</NamPC17>
          <StrAndNumPC122>PrincipalStreet</StrAndNumPC122>
          <PosCodPC123>AA1 1AA</PosCodPC123>
          <CitPC124>PrincipalTown</CitPC124>
          <CouPC125>FR</CouPC125>
        </TRAPRIPC1>
        <TRACONCO1>
          <NamCO17>ConsignorName</NamCO17>
          <StrAndNumCO122>ConorLine1</StrAndNumCO122>
          <PosCodCO123>ConorL3</PosCodCO123>
          <CitCO124>ConorLine2</CitCO124>
          <CouCO125>CN</CouCO125>
          <TINCO159>ConorEori</TINCO159>
        </TRACONCO1>
        <TRACONCE1>
          <NamCE17>ConsigneeName</NamCE17>
          <StrAndNumCE122>ConeeLine1</StrAndNumCE122>
          <PosCodCE123>ConeeL3</PosCodCE123>
          <CitCE124>ConeeLine2</CitCE124>
          <CouCE125>CN</CouCE125>
          <TINCE159>ConeeEori</TINCE159>
        </TRACONCE1>
        <CUSOFFDEPEPT>
          <RefNumEPT1>OOD1234A</RefNumEPT1>
        </CUSOFFDEPEPT>
        <CUSOFFTRARNS>
          <RefNumRNS1>TOP12341</RefNumRNS1>
          <ArrTimTRACUS085>202005072112</ArrTimTRACUS085>
        </CUSOFFTRARNS>
        <CUSOFFDESEST>
          <RefNumEST1>DOP1234A</RefNumEST1>
        </CUSOFFDESEST>
        <REPREP>
          <NamREP5>John Doe</NamREP5>
          <RepCapREP18>direct</RepCapREP18>
        </REPREP>
        <SEAINFSLI>
          <SeaNumSLI2>1</SeaNumSLI2>
          <SEAIDSID>
            <SeaIdeSID1>SEAL1</SeaIdeSID1>
          </SEAIDSID>
        </SEAINFSLI>
        <GUAGUA>
          <GuaTypGUA1>1</GuaTypGUA1>
          <GUAREFREF>
            <GuaRefNumGRNREF1>GUA1Ref</GuaRefNumGRNREF1>
            <AccCodREF6>1234</AccCodREF6>
          </GUAREFREF>
        </GUAGUA>
        <GUAGUA>
          <GuaTypGUA1>0</GuaTypGUA1>
          <GUAREFREF>
            <GuaRefNumGRNREF1>GUA2Ref</GuaRefNumGRNREF1>
            <AccCodREF6>4321</AccCodREF6>
          </GUAREFREF>
        </GUAGUA>
        <GOOITEGDS>
          <IteNumGDS7>1</IteNumGDS7>
          <ComCodTarCodGDS10>ComoCode1</ComCodTarCodGDS10>
          <GooDesGDS23>ItemOnesDescription</GooDesGDS23>
          <GroMasGDS46>25000</GroMasGDS46>
          <NetMasGDS48>12342</NetMasGDS48>
          <MetOfPayGDI12>M</MetOfPayGDI12>
          <ComRefNumGIM1>GD1CRN</ComRefNumGIM1>
          <UNDanGooCodGDI1>GD1C</UNDanGooCodGDI1>
          <PREADMREFAR2>
            <PreDocTypAR21>GD1PR1</PreDocTypAR21>
            <PreDocRefAR26>GD1PR1Ref</PreDocRefAR26>
            <ComOfInfAR29>GD1PR1Info</ComOfInfAR29>
          </PREADMREFAR2>
          <PREADMREFAR2>
            <PreDocTypAR21>GD1PR2</PreDocTypAR21>
            <PreDocRefAR26>GD1PR2Ref</PreDocRefAR26>
          </PREADMREFAR2>
          <PRODOCDC2>
            <DocTypDC21>G1D1</DocTypDC21>
            <DocRefDC23>G1D1Ref</DocRefDC23>
            <ComOfInfDC25>G1D1Info</ComOfInfDC25>
          </PRODOCDC2>
          <PRODOCDC2>
            <DocTypDC21>G1D2</DocTypDC21>
            <DocRefDC23>G1D2Ref</DocRefDC23>
          </PRODOCDC2>
          <SPEMENMT2>
            <AddInfMT21>10000EURGUA1Ref</AddInfMT21>
            <AddInfCodMT23>CAL</AddInfCodMT23>
          </SPEMENMT2>
          <SPEMENMT2>
            <AddInfMT21>500GBPGUA2Ref</AddInfMT21>
            <AddInfCodMT23>CAL</AddInfCodMT23>
          </SPEMENMT2>
          <SPEMENMT2>
            <AddInfMT21>GD1SPMT1Info</AddInfMT21>
            <AddInfCodMT23>GD1S1</AddInfCodMT23>
          </SPEMENMT2>
          <SPEMENMT2>
            <AddInfMT21>GD1S2Info</AddInfMT21>
            <AddInfCodMT23>GD1S2</AddInfCodMT23>
          </SPEMENMT2>
          <CONNR2>
            <ConNumNR21>GD1CN1NUM1</ConNumNR21>
          </CONNR2>
          <CONNR2>
            <ConNumNR21>GD1CN2NUMS</ConNumNR21>
          </CONNR2>
          <PACGS2>
            <KinOfPacGS23>VQ</KinOfPacGS23>
          </PACGS2>
          <PACGS2>
            <MarNumOfPacGS21>GD1PK2MK</MarNumOfPacGS21>
            <KinOfPacGS23>NE</KinOfPacGS23>
            <NumOfPieGS25>12</NumOfPieGS25>
          </PACGS2>
          <PACGS2>
            <MarNumOfPacGS21>GD1PK3MK</MarNumOfPacGS21>
            <KinOfPacGS23>BAG</KinOfPacGS23>
            <NumOfPacGS24>2</NumOfPacGS24>
          </PACGS2>
        </GOOITEGDS>
        <ITI>
          <CouOfRouCodITI1>CA</CouOfRouCodITI1>
        </ITI>
        <TRACORSEC037>
          <NamTRACORSEC041>SafeSecName</NamTRACORSEC041>
          <StrNumTRACORSEC043>SecConorLine1</StrNumTRACORSEC043>
          <PosCodTRACORSEC042>SecCorL3</PosCodTRACORSEC042>
          <CitTRACORSEC038>SecConorLine2</CitTRACORSEC038>
          <CouCodTRACORSEC039>CN</CouCodTRACORSEC039>
        </TRACORSEC037>
        <TRACONSEC029>
          <NameTRACONSEC033>SafeSecName</NameTRACONSEC033>
          <StrNumTRACONSEC035>SecConeeLine1</StrNumTRACONSEC035>
          <PosCodTRACONSEC034>SecCeeL3</PosCodTRACONSEC034>
          <CitTRACONSEC030>SecConeeLine2</CitTRACONSEC030>
          <CouCodTRACONSEC031>CN</CouCodTRACONSEC031>
        </TRACONSEC029>
      </CC015B>

      val generatedXml = service.convert(Scenario3.userAnswers).futureValue.right.value.toXml.map(scala.xml.Utility.trim)

      validate(generatedXml.toString()) mustBe Success(())

      generatedXml xmlMustEqual expectedXml.map(scala.xml.Utility.trim)
    }
    "Scenario 4: " in new Setup {

      val expectedXml = <CC015B>
        <SynIdeMES1>UNOC</SynIdeMES1>
        <SynVerNumMES2>3</SynVerNumMES2>
        <MesRecMES6>NCTS</MesRecMES6>
        <DatOfPreMES9>20201212</DatOfPreMES9>
        <TimOfPreMES10>2030</TimOfPreMES10>
        <IntConRefMES11>DF202012121</IntConRefMES11>
        <AppRefMES14>NCTS</AppRefMES14>
        <TesIndMES18>0</TesIndMES18>
        <MesIdeMES19>1</MesIdeMES19>
        <MesTypMES20>GB015B</MesTypMES20>
        <HEAHEA>
          <RefNumHEA4>ABCD1234567890123</RefNumHEA4>
          <TypOfDecHEA24>T2</TypOfDecHEA24>
          <CouOfDesCodHEA30>DC</CouOfDesCodHEA30>
          <PlaOfLoaCodHEA46>LoadPLace</PlaOfLoaCodHEA46>
          <CouOfDisCodHEA55>SC</CouOfDisCodHEA55>
          <CusSubPlaHEA66>CUSAPPLOC</CusSubPlaHEA66>
          <InlTraModHEA75>2</InlTraModHEA75>
          <TraModAtBorHEA76>2</TraModAtBorHEA76>
          <TypOfMeaOfTraCroHEA88>2</TypOfMeaOfTraCroHEA88>
          <ConIndHEA96>1</ConIndHEA96>
          <DiaLanIndAtDepHEA254>EN</DiaLanIndAtDepHEA254>
          <NCTSAccDocHEA601LNG>EN</NCTSAccDocHEA601LNG>
          <TotNumOfIteHEA305>1</TotNumOfIteHEA305>
          <TotNumOfPacHEA306>1</TotNumOfPacHEA306>
          <TotGroMasHEA307>25000.0</TotGroMasHEA307>
          <DecDatHEA383>20201212</DecDatHEA383>
          <DecPlaHEA394>XX1 1XX</DecPlaHEA394>
          <SpeCirIndHEA1>E</SpeCirIndHEA1>
          <ComRefNumHEA>COMREFALL</ComRefNumHEA>
          <SecHEA358>1</SecHEA358>
        </HEAHEA>
        <TRAPRIPC1>
          <NamPC17>PrincipalName</NamPC17>
          <StrAndNumPC122>PrincipalStreet</StrAndNumPC122>
          <PosCodPC123>AA1 1AA</PosCodPC123>
          <CitPC124>PrincipalTown</CitPC124>
          <CouPC125>FR</CouPC125>
        </TRAPRIPC1>
        <CUSOFFDEPEPT>
          <RefNumEPT1>OOD1234A</RefNumEPT1>
        </CUSOFFDEPEPT>
        <CUSOFFTRARNS>
          <RefNumRNS1>TOP12341</RefNumRNS1>
          <ArrTimTRACUS085>202005072112</ArrTimTRACUS085>
        </CUSOFFTRARNS>
        <CUSOFFDESEST>
          <RefNumEST1>DOP1234A</RefNumEST1>
        </CUSOFFDESEST>
        <REPREP>
          <NamREP5>John Doe</NamREP5>
          <RepCapREP18>direct</RepCapREP18>
        </REPREP>
        <SEAINFSLI>
          <SeaNumSLI2>2</SeaNumSLI2>
          <SEAIDSID>
            <SeaIdeSID1>SEAL1</SeaIdeSID1>
          </SEAIDSID>
          <SEAIDSID>
            <SeaIdeSID1>SEAL2</SeaIdeSID1>
          </SEAIDSID>
        </SEAINFSLI>
        <GUAGUA>
          <GuaTypGUA1>1</GuaTypGUA1>
          <GUAREFREF>
            <GuaRefNumGRNREF1>GUA1Ref</GuaRefNumGRNREF1>
            <AccCodREF6>1234</AccCodREF6>
          </GUAREFREF>
        </GUAGUA>
        <GUAGUA>
          <GuaTypGUA1>0</GuaTypGUA1>
          <GUAREFREF>
            <GuaRefNumGRNREF1>GUA2Ref</GuaRefNumGRNREF1>
            <AccCodREF6>4321</AccCodREF6>
          </GUAREFREF>
        </GUAGUA>
        <GOOITEGDS>
          <IteNumGDS7>1</IteNumGDS7>
          <ComCodTarCodGDS10>ComoCode1</ComCodTarCodGDS10>
          <GooDesGDS23>ItemOnesDescription</GooDesGDS23>
          <GroMasGDS46>25000</GroMasGDS46>
          <NetMasGDS48>12342</NetMasGDS48>
          <MetOfPayGDI12>W</MetOfPayGDI12>
          <UNDanGooCodGDI1>GD1C</UNDanGooCodGDI1>
          <PREADMREFAR2>
            <PreDocTypAR21>GD1PR1</PreDocTypAR21>
            <PreDocRefAR26>GD1PR1Ref</PreDocRefAR26>
            <ComOfInfAR29>GD1PR1Info</ComOfInfAR29>
          </PREADMREFAR2>
          <PREADMREFAR2>
            <PreDocTypAR21>GD1PR2</PreDocTypAR21>
            <PreDocRefAR26>GD1PR2Ref</PreDocRefAR26>
          </PREADMREFAR2>
          <PRODOCDC2>
            <DocTypDC21>G1D1</DocTypDC21>
            <DocRefDC23>G1D1Ref</DocRefDC23>
            <ComOfInfDC25>G1D1Info</ComOfInfDC25>
          </PRODOCDC2>
          <PRODOCDC2>
            <DocTypDC21>G1D2</DocTypDC21>
            <DocRefDC23>G1D2Ref</DocRefDC23>
          </PRODOCDC2>
          <SPEMENMT2>
            <AddInfMT21>10000EURGUA1Ref</AddInfMT21>
            <AddInfCodMT23>CAL</AddInfCodMT23>
          </SPEMENMT2>
          <SPEMENMT2>
            <AddInfMT21>500GBPGUA2Ref</AddInfMT21>
            <AddInfCodMT23>CAL</AddInfCodMT23>
          </SPEMENMT2>
          <SPEMENMT2>
            <AddInfMT21>GD1SPMT1Info</AddInfMT21>
            <AddInfCodMT23>GD1S1</AddInfCodMT23>
          </SPEMENMT2>
          <SPEMENMT2>
            <AddInfMT21>GD1S2Info</AddInfMT21>
            <AddInfCodMT23>GD1S2</AddInfCodMT23>
          </SPEMENMT2>
          <TRACONCO2>
            <NamCO27>ConorName</NamCO27>
            <StrAndNumCO222>ConorLine1</StrAndNumCO222>
            <PosCodCO223>ConorL3</PosCodCO223>
            <CitCO224>ConorLine2</CitCO224>
            <CouCO225>GA</CouCO225>
            <TINCO259>Conor123</TINCO259>
          </TRACONCO2>
          <TRACONCE2>
            <NamCE27>ConeeName</NamCE27>
            <StrAndNumCE222>ConeeLine1</StrAndNumCE222>
            <PosCodCE223>ConeeL3</PosCodCE223>
            <CitCE224>ConeeLine2</CitCE224>
            <CouCE225>GA</CouCE225>
            <TINCE259>Conee123</TINCE259>
          </TRACONCE2>
          <CONNR2>
            <ConNumNR21>GD1CN1NUM1</ConNumNR21>
          </CONNR2>
          <CONNR2>
            <ConNumNR21>GD1CN2NUMS</ConNumNR21>
          </CONNR2>
          <PACGS2>
            <KinOfPacGS23>VQ</KinOfPacGS23>
          </PACGS2>
          <PACGS2>
            <MarNumOfPacGS21>GD1PK2MK</MarNumOfPacGS21>
            <KinOfPacGS23>NE</KinOfPacGS23>
            <NumOfPieGS25>12</NumOfPieGS25>
          </PACGS2>
          <PACGS2>
            <MarNumOfPacGS21>GD1PK3MK</MarNumOfPacGS21>
            <KinOfPacGS23>BAG</KinOfPacGS23>
            <NumOfPacGS24>2</NumOfPacGS24>
          </PACGS2>
        </GOOITEGDS>
        <ITI>
          <CouOfRouCodITI1>CA</CouOfRouCodITI1>
        </ITI>
        <CARTRA100>
          <NamCARTRA121>CarrierName</NamCARTRA121>
          <StrAndNumCARTRA254>CarAddL1</StrAndNumCARTRA254>
          <PosCodCARTRA121>CarAddL3</PosCodCARTRA121>
          <CitCARTRA789>CarAddL2</CitCARTRA789>
          <CouCodCARTRA587>CA</CouCodCARTRA587>
        </CARTRA100>
        <TRACORSEC037>
          <TINTRACORSEC044>SafeSecConorEori</TINTRACORSEC044>
        </TRACORSEC037>
        <TRACONSEC029>
          <TINTRACONSEC036>SafeSecConeeEori</TINTRACONSEC036>
        </TRACONSEC029>
      </CC015B>

      val generatedXml = service.convert(Scenario4.userAnswers).futureValue.right.value.toXml.map(scala.xml.Utility.trim)

      validate(generatedXml.toString()) mustBe Success(())

      generatedXml xmlMustEqual expectedXml.map(scala.xml.Utility.trim)
    }
  }
}
