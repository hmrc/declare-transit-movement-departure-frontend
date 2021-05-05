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

import org.scalatest.Suite

import scala.xml.{Node, NodeSeq}

trait XMLComparatorSpec {
  self: Suite =>
  case class XmlValue(node: String, value: String)

  implicit private class NodeOps(actual: Node){
    def flatter(root: String = "__"): Seq[XmlValue] = {
      if (actual.child.nonEmpty){
        actual.child.flatMap {
          case x if x.label == "#PCDATA" => Seq(XmlValue(root, x.text))
          case x if x.child.isEmpty => Seq(XmlValue(s"$root / ${x.label}", ""))
          case x => x.flatter(s"$root / ${x.label}")
        }
      } else {
        Nil
      }
    }
  }


  implicit class NodeSeqEq(actual: NodeSeq){

    private lazy val actualFields: Seq[XmlValue] = actual.flatMap(x => x.flatter(x.label))
    private lazy val actualFieldNodes: Seq[String] = actualFields.map(_.node)

    private lazy val fieldsMissingInExpected: Seq[String] => Seq[String] = actualFieldNodes diff
    private lazy val fieldsMissingInActual: Seq[String] => Seq[String] = _ diff actualFieldNodes

    private lazy val getCommonFields: XmlValue => Seq[XmlValue] = xml => actualFields.filter(_.node == xml.node)

    private lazy val fieldsWithDifferentValues: Seq[XmlValue] => Seq[String] = expectedFields => actualFields.collect{
      case actualField if (getCommonFields(actualField).length > 1 || expectedFields.count(_.node == actualField.node) > 1)
        && (getCommonFields(actualField).nonEmpty && expectedFields.count(_.node == actualField.node) > 0)  =>
        val fields = expectedFields.filter(_.node == actualField.node)
        val actualFieldLength = getCommonFields(actualField).length
        fields.length match {
          case x if x != actualFieldLength =>
            Some(s"Array field ${Console.CYAN}${actualField.node}${Console.RED} occurred '${actualFieldLength}' time(s) and expected '${x}' time(s) ")
          case `actualFieldLength`if getCommonFields(actualField) != fields =>
            Some(
              s"Array field ${Console.CYAN}${actualField.node}${Console.RED} did not full match all fields in expected xml:" +
              s"\n    actual: ${getCommonFields(actualField).map(_.value).mkString(", ")}" +
              s"\n    expected: ${fields.map(_.value).mkString(", ")}"
            )
          case _ => None
        }
      case actualField =>
        expectedFields.find(_.node == actualField.node).flatMap { field =>
          if (field.value == actualField.value) {
            None
          } else {
            Some(s"For field ${Console.CYAN}${actualField.node}${Console.RED} actual '${actualField.value}' did not equal expected '${field.value}'")
          }
        }
    }.flatten.distinct

    def xmlMustEqual(expected: NodeSeq): Unit ={
      lazy val expectedFields      = expected.flatMap(x => x.flatter(x.label))
      lazy val expectedFieldNodes  = expectedFields.map(_.node)
      lazy val missingFromActual   = fieldsMissingInActual(expectedFieldNodes)
      lazy val missingFromExpected = fieldsMissingInExpected(expectedFieldNodes)

      lazy val incorrectValues = fieldsWithDifferentValues(expectedFields)

      lazy val incorrectString = if(incorrectValues.isEmpty) "" else s"\n(${incorrectValues.length}) Fields with incorrect values:" +
        s"\n  ${incorrectValues.mkString("\n  ")}"
      lazy val  missingFromExpectedString = if(missingFromExpected.isEmpty) "" else s"\n(${missingFromExpected.length}) Fields missing from expected: " +
        s"\n  ${missingFromExpected.mkString("\n  ")} "
      lazy val missingFromActualString = if(missingFromActual.isEmpty) "" else s"\n(${missingFromActual.length}) Fields missing from actual: " +
        s"\n  ${missingFromActual.mkString("\n  ")}"

      if(expected != actual) {
        fail(s"The XMLs tested didn't match each other $incorrectString $missingFromActualString $missingFromExpectedString")
      }
    }
  }

}

/*
*
*  val expectedXml = <CC015B>
        <SynIdeMES1>UNOC</SynIdeMES1>
        <SynVerNumMES2>3</SynVerNumMES2>
        <MesSenMES3>NCTS</MesSenMES3>
        <MesRecMES6>NCTS</MesRecMES6>
        <DatOfPreMES9>20201212</DatOfPreMES9>
        <TimOfPreMES10>2030</TimOfPreMES10>
        <IntConRefMES11>DF20201212150</IntConRefMES11>
        <AppRefMES14>NCTS</AppRefMES14>
        <PriMES15></PriMES15>
        <AckReqMES16></AckReqMES16>
        <ComAgrIdMES17></ComAgrIdMES17>
        <TesIndMES18>0</TesIndMES18>
        <MesIdeMES19>1</MesIdeMES19>
        <MesTypMES20>GB015B</MesTypMES20>
        <ComAccRefMES21></ComAccRefMES21>
        <MesSeqNumMES22></MesSeqNumMES22>
        <HEAHEA>
          <RefNumHEA4>TestRefNumber</RefNumHEA4>
          <TypOfDecHEA24>T2</TypOfDecHEA24>
          <CouOfDesCodHEA30>DC</CouOfDesCodHEA30>
          <AgrLocOfGooCodHEA38></AgrLocOfGooCodHEA38>
          <AgrLocOfGooHEA39>Pre-lodge</AgrLocOfGooHEA39>
          <AgrLocOfGooHEA39LNG>EN</AgrLocOfGooHEA39LNG>
          <AutLocOfGooCodHEA41></AutLocOfGooCodHEA41>
          <PlaOfLoaCodHEA46>LoadPLace</PlaOfLoaCodHEA46>
          <CouOfDisCodHEA55>SC</CouOfDisCodHEA55>
          <CusSubPlaHEA66>CUSAPPLOC</CusSubPlaHEA66>
          <InlTraModHEA75>4</InlTraModHEA75>
          <TraModAtBorHEA76>4</TraModAtBorHEA76>
          <IdeOfMeaOfTraAtDHEA78></IdeOfMeaOfTraAtDHEA78>
          <NatOfMeaOfTraAtDHEA80>NDP</NatOfMeaOfTraAtDHEA80>
          <IdeOfMeaOfTraCroHEA85></IdeOfMeaOfTraCroHEA85>
          <NatOfMeaOfTraCroHEA87>NDP</NatOfMeaOfTraCroHEA87>
          <TypOfMeaOfTraCroHEA88>4</TypOfMeaOfTraCroHEA88>
          <ConIndHEA96>1</ConIndHEA96>
          <DiaLanIndAtDepHEA254>EN</DiaLanIndAtDepHEA254>
          <NCTSAccDocHEA601LNG>EN</NCTSAccDocHEA601LNG>
          <TotNumOfIteHEA305>2</TotNumOfIteHEA305>
          <TotNumOfPacHEA306>1</TotNumOfPacHEA306>
          <TotGroMasHEA307>12131415</TotGroMasHEA307>
          <DecDatHEA383>20201212</DecDatHEA383>
          <DecPlaHEA394>XX1 1XX</DecPlaHEA394>
          <DecPlaHEA394LNG>EN</DecPlaHEA394LNG>
          <SpeCirIndHEA1>A</SpeCirIndHEA1>
          <TraChaMetOfPayHEA1>Card</TraChaMetOfPayHEA1>
          <ComRefNumHEA></ComRefNumHEA>
          <SecHEA358>1</SecHEA358>
          <ConRefNumHEA>SomeConv</ConRefNumHEA>
          <CodPlUnHEA357>PlaceOfUnloadingPage</CodPlUnHEA357>
          <CodPlUnHEA357LNG>EN</CodPlUnHEA357LNG>
        </HEAHEA>
        <TRAPRIPC1>
          <NamPC17>PrincipalName</NamPC17>
          <StrAndNumPC122>PrincipalStreet</StrAndNumPC122>
          <PosCodPC123>AA1 1AA</PosCodPC123>
          <CitPC124>PrincipalTown</CitPC124>
          <CouPC125>GB</CouPC125>
          <NADLNGPC>EN</NADLNGPC>
        </TRAPRIPC1>
        <CUSOFFDEPEPT>
          <RefNumEPT1>OOD</RefNumEPT1>
        </CUSOFFDEPEPT>
        <CUSOFFTRARNS>
          <RefNumRNS1>AddAnotherTransitOfficePageOT1</RefNumRNS1>
          <ArrTimTRACUS085>202005050512</ArrTimTRACUS085>
        </CUSOFFTRARNS>
        <CUSOFFTRARNS>
          <RefNumRNS1>AddAnotherTransitOfficePageOT2</RefNumRNS1>
          <ArrTimTRACUS085>202005072112</ArrTimTRACUS085>
        </CUSOFFTRARNS>
        <CUSOFFDESEST>
          <RefNumEST1>DOP</RefNumEST1>
        </CUSOFFDESEST>
        <CONRESERS>
          <ConResCodERS16></ConResCodERS16>
          <DatLimERS69></DatLimERS69>
        </CONRESERS>
        <REPREP>
          <NamREP5>John Doe</NamREP5>
          <RepCapREP18>direct</RepCapREP18>
          <RepCapREP18LNG>EN</RepCapREP18LNG>
        </REPREP>
        <SEAINFSLI>
          <SeaNumSLI2>2</SeaNumSLI2>
          <SEAIDSID>
            <SeaIdeSID1>SEAL1</SeaIdeSID1>
            <SeaIdeSID1LNG>EN</SeaIdeSID1LNG>
          </SEAIDSID>
          <SEAIDSID>
            <SeaIdeSID1>SEAL2</SeaIdeSID1>
            <SeaIdeSID1LNG>EN</SeaIdeSID1LNG>
          </SEAIDSID>
        </SEAINFSLI>
        <GUAGUA>
          <GuaTypGUA1>1</GuaTypGUA1>
          <GUAREFREF>
            <GuaRefNumGRNREF1>GUA1Ref</GuaRefNumGRNREF1>
            <OthGuaRefREF4></OthGuaRefREF4>
            <AccCodeREF6></AccCodeREF6>
            <VALLIMECVLE>
              <NotValForECVLE1></NotValForECVLE1>
            </VALLIMECVLE>
            <VALLIMNONECLIM>
              <NotValForOthConPLIM2></NotValForOthConPLIM2>
            </VALLIMNONECLIM>
          </GUAREFREF>
        </GUAGUA>
        <GUAGUA>
          <GuaTypGUA1>0</GuaTypGUA1>
          <GUAREFREF>
            <GuaRefNumGRNREF1>GUA2Ref</GuaRefNumGRNREF1>
            <OthGuaRefREF4></OthGuaRefREF4>
            <AccCodeREF6></AccCodeREF6>
            <VALLIMECVLE>
              <NotValForECVLE1></NotValForECVLE1>
            </VALLIMECVLE>
            <VALLIMNONECLIM>
              <NotValForOthConPLIM2></NotValForOthConPLIM2>
            </VALLIMNONECLIM>
          </GUAREFREF>
        </GUAGUA>
        <GOOITEGDS>
          <IteNumGDS7>1</IteNumGDS7>
          <ComCodTarCodGDS10>ComoCode1</ComCodTarCodGDS10>
          <DecTypGDS15></DecTypGDS15>
          <GooDesGDS23>ItemOnesDescription</GooDesGDS23>
          <GooDesGDS23LNG>EN</GooDesGDS23LNG>
          <GroMasGDS46>25000</GroMasGDS46>
          <NetMasGDS48>12342</NetMasGDS48>
          <CouOfDisGDS58></CouOfDisGDS58>
          <CouOfDesGDS59></CouOfDesGDS59>
          <MetOfPayGDI12></MetOfPayGDI12>
          <ComRefNumGIM1>GD1CRN</ComRefNumGIM1>
          <UNDanGooCodGDI1>GD1DGGDSCODE</UNDanGooCodGDI1>
          <PREADMREFAR2>
            <PreDocTypAR21>GD1PREREF1</PreDocTypAR21>
            <PreDocRefAR26>GD1PREREF1Ref</PreDocRefAR26>
            <PreDocRefLNG>EN</PreDocRefLNG>
            <ComOfInfAR29>GD1PREREF1Info</ComOfInfAR29>
            <ComOfInfAR29LNG>EN</ComOfInfAR29LNG>
          </PREADMREFAR2>
          <PREADMREFAR2>
            <PreDocTypAR21>GD1PREREF2</PreDocTypAR21>
            <PreDocRefAR26>GD1PREREF2Ref</PreDocRefAR26>
            <PreDocRefLNG>EN</PreDocRefLNG>
          </PREADMREFAR2>
          <PRODOCDC2>
            <DocTypDC21>GD1DOC1</DocTypDC21>
            <DocRefDC23>GD1DOC1Ref</DocRefDC23>
            <DocRefDCLNG>EN</DocRefDCLNG>
            <ComOfInfDC25>GD1DOC1Info</ComOfInfDC25>
            <ComOfInfDC25LNG>EN</ComOfInfDC25LNG>
          </PRODOCDC2>
          <PRODOCDC2>
            <DocTypDC21>GD1DOC1</DocTypDC21>
            <DocRefDC23>GD1DOC1Ref</DocRefDC23>
            <DocRefDCLNG>EN</DocRefDCLNG>
          </PRODOCDC2>
          <SPEMENMT2>
            <AddInfMT21>10000EURGUA1Ref</AddInfMT21>
            <AddInfCodMT23>CAL</AddInfCodMT23>
            <ExpFroECMT24></ExpFroECMT24>
            <ExpFroCouMT25></ExpFroCouMT25>
          </SPEMENMT2>
          <SPEMENMT2>
            <AddInfMT21>500GBPGUA2Ref</AddInfMT21>
            <AddInfCodMT23>CAL</AddInfCodMT23>
            <ExpFroECMT24></ExpFroECMT24>
            <ExpFroCouMT25></ExpFroCouMT25>
          </SPEMENMT2>
          <SPEMENMT2>
            <AddInfMT21>GD1SPMT2Info</AddInfMT21>
            <AddInfCodMT23>GD1SPMT2</AddInfCodMT23>
            <ExpFroECMT24></ExpFroECMT24>
            <ExpFroCouMT25></ExpFroCouMT25>
          </SPEMENMT2>
          <TRACONCO2>
            <NamCO27>ConorName</NamCO27>
            <StrAndNumCO222>ConorLine1</StrAndNumCO222>
            <PosCodCO223>ConorLine3</PosCodCO223>
            <CitCO224>ConorLine2</CitCO224>
            <CouCO225>GD1</CouCO225>
            <NADLNGGTCO>EN</NADLNGGTCO>
            <TINCO259>Conor123</TINCO259>
          </TRACONCO2>
          <TRACONCE2>
            <NamCE27>ConeeName</NamCE27>
            <StrAndNumCE222>ConeeLine1</StrAndNumCE222>
            <PosCodCE223>ConeeLine3</PosCodCE223>
            <CitCE224>ConeeLine2</CitCE224>
            <CouCE225>GD1</CouCE225>
            <NADLNGGICE>EN</NADLNGGICE>
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
            <NumOfPacGS24>2</NumOfPacGS24>
            <NumOfPieGS25>12</NumOfPieGS25>
          </PACGS2>
          <PACGS2>
            <MarNumOfPacGS21>GD1PK2MK</MarNumOfPacGS21>
            <KinOfPacGS23>NE</KinOfPacGS23>
          </PACGS2>
          <PACGS2>
            <MarNumOfPacGS21>GD1PK3MK</MarNumOfPacGS21>
            <KinOfPacGS23>BAG</KinOfPacGS23>
          </PACGS2>
          <SGICODSD2>
            <SenGooCodSD22></SenGooCodSD22>
            <SenQuaSD23></SenQuaSD23>
          </SGICODSD2>
          <TRACORSECGOO021>
            <NamTRACORSECGOO025></NamTRACORSECGOO025>
            <StrNumTRACORSECGOO027></StrNumTRACORSECGOO027>
            <PosCodTRACORSECGOO026></PosCodTRACORSECGOO026>
            <CitTRACORSECGOO022></CitTRACORSECGOO022>
            <CouCodTRACORSECGOO023></CouCodTRACORSECGOO023>
            <TINTRACORSECGOO028></TINTRACORSECGOO028>
          </TRACORSECGOO021>
          <TRACONSECGOO013>
            <NamTRACONSECGOO017></NamTRACONSECGOO017>
            <StrNumTRACONSECGOO019></StrNumTRACONSECGOO019>
            <PosCodTRACONSECGOO018></PosCodTRACONSECGOO018>
            <CityTRACONSECGOO014></CityTRACONSECGOO014>
            <CouCodTRACONSECGOO015></CouCodTRACONSECGOO015>
            <TINTRACONSECGOO020></TINTRACONSECGOO020>
          </TRACONSECGOO013>
        </GOOITEGDS>
        <GOOITEGDS>
          <IteNumGDS7>2</IteNumGDS7>
          <DecTypGDS15></DecTypGDS15>
          <GooDesGDS23>ItemTwosDescription</GooDesGDS23>
          <GooDesGDS23LNG>EN</GooDesGDS23LNG>
          <GroMasGDS46>25001</GroMasGDS46>
          <CouOfDisGDS58></CouOfDisGDS58>
          <CouOfDesGDS59></CouOfDesGDS59>
          <MetOfPayGDI12></MetOfPayGDI12>
          <ComRefNumGIM1>GD2CRN</ComRefNumGIM1>
          <PREADMREFAR2>
            <PreDocTypAR21>GD2PREREF1</PreDocTypAR21>
            <PreDocRefAR26>GD2PREREF1Ref</PreDocRefAR26>
            <PreDocRefLNG>EN</PreDocRefLNG>
            <ComOfInfAR29>GD2PREREF1Info</ComOfInfAR29>
            <ComOfInfAR29LNG>EN</ComOfInfAR29LNG>
          </PREADMREFAR2>
          <PRODOCDC2>
            <DocTypDC21>GD2DOC1</DocTypDC21>
            <DocRefDC23>GD2DOC1Ref</DocRefDC23>
            <DocRefDCLNG>EN</DocRefDCLNG>
            <ComOfInfDC25>GD2DOC1Info</ComOfInfDC25>
            <ComOfInfDC25LNG>EN</ComOfInfDC25LNG>
          </PRODOCDC2>
          <SPEMENMT2>
            <AddInfMT21>GD2SPMT1Info</AddInfMT21>
            <AddInfCodMT23>GD2SPMT1</AddInfCodMT23>
            <ExpFroECMT24></ExpFroECMT24>
            <ExpFroCouMT25></ExpFroCouMT25>
          </SPEMENMT2>
          <TRACONCO2>
            <NamCO27>ConorName</NamCO27>
            <StrAndNumCO222>ConorLine1</StrAndNumCO222>
            <PosCodCO223>ConorLine3</PosCodCO223>
            <CitCO224>ConorLine2</CitCO224>
            <CouCO225>GD2</CouCO225>
            <NADLNGGTCO>EN</NADLNGGTCO>
          </TRACONCO2>
          <TRACONCE2>
            <NamCE27>ConeeName</NamCE27>
            <StrAndNumCE222>ConeeLine1</StrAndNumCE222>
            <PosCodCE223>ConeeLine3</PosCodCE223>
            <CitCE224>ConeeLine2</CitCE224>
            <CouCE225>GD2</CouCE225>
            <NADLNGGICE>EN</NADLNGGICE>
          </TRACONCE2>
          <CONNR2>
            <ConNumNR21>GD2CN1NUM1</ConNumNR21>
          </CONNR2>
          <PACGS2>
            <KinOfPacGS23>VQ</KinOfPacGS23>
            <NumOfPacGS24>2</NumOfPacGS24>
            <NumOfPieGS25>12</NumOfPieGS25>
          </PACGS2>
          <PACGS2>
            <MarNumOfPacGS21>GD2PK2MK</MarNumOfPacGS21>
            <KinOfPacGS23>NE</KinOfPacGS23>
          </PACGS2>
          <PACGS2>
            <MarNumOfPacGS21>GD2PK3MK</MarNumOfPacGS21>
            <KinOfPacGS23>BAG</KinOfPacGS23>
          </PACGS2>
          <SGICODSD2>
            <SenGooCodSD22></SenGooCodSD22>
            <SenQuaSD23></SenQuaSD23>
          </SGICODSD2>
          <TRACORSECGOO021>
            <NamTRACORSECGOO025></NamTRACORSECGOO025>
            <StrNumTRACORSECGOO027></StrNumTRACORSECGOO027>
            <PosCodTRACORSECGOO026></PosCodTRACORSECGOO026>
            <CitTRACORSECGOO022></CitTRACORSECGOO022>
            <CouCodTRACORSECGOO023></CouCodTRACORSECGOO023>
            <TINTRACORSECGOO028></TINTRACORSECGOO028>
          </TRACORSECGOO021>
          <TRACONSECGOO013>
            <NamTRACONSECGOO017></NamTRACONSECGOO017>
            <StrNumTRACONSECGOO019></StrNumTRACONSECGOO019>
            <PosCodTRACONSECGOO018></PosCodTRACONSECGOO018>
            <CityTRACONSECGOO014></CityTRACONSECGOO014>
            <CouCodTRACONSECGOO015></CouCodTRACONSECGOO015>
            <TINTRACONSECGOO020></TINTRACONSECGOO020>
          </TRACONSECGOO013>
        </GOOITEGDS>
        <ITI>
          <CouOfRouCodITI1>CORP1</CouOfRouCodITI1>
        </ITI>
        <ITI>
          <CouOfRouCodITI1>CORP2</CouOfRouCodITI1>
        </ITI>
        <CARTRA100>
          <NamCARTRA121></NamCARTRA121>
          <StrAndNumCARTRA254></StrAndNumCARTRA254>
          <PosCodCARTRA121></PosCodCARTRA121>
          <CitCARTRA789></CitCARTRA789>
          <CouCodCARTRA587></CouCodCARTRA587>
          <NADCARTRA121></NADCARTRA121>
          <TINCARTRA254>CarrierEori</TINCARTRA254>
        </CARTRA100>
      </CC015B>
* */