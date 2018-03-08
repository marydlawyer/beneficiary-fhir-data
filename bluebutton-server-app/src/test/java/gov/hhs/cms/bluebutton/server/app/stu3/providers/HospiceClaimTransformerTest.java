package gov.hhs.cms.bluebutton.server.app.stu3.providers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.hl7.fhir.dstu3.model.ExplanationOfBenefit;
import org.hl7.fhir.dstu3.model.ExplanationOfBenefit.ItemComponent;
import org.hl7.fhir.dstu3.model.codesystems.ClaimCareteamrole;
import org.hl7.fhir.exceptions.FHIRException;
import org.junit.Assert;
import org.junit.Test;

import gov.hhs.cms.bluebutton.data.codebook.data.CcwCodebookVariable;
import gov.hhs.cms.bluebutton.data.model.rif.HospiceClaim;
import gov.hhs.cms.bluebutton.data.model.rif.HospiceClaimLine;
import gov.hhs.cms.bluebutton.data.model.rif.samples.StaticRifResource;
import gov.hhs.cms.bluebutton.data.model.rif.samples.StaticRifResourceGroup;
import gov.hhs.cms.bluebutton.server.app.ServerTestUtils;

/**
 * Unit tests for {@link HospiceClaimTransformer}.
 */
public final class HospiceClaimTransformerTest {
	/**
	 * Verifies that {@link HospiceClaimTransformer#transform(Object)} works as
	 * expected when run against the {@link StaticRifResource#SAMPLE_A_HOSPICE}
	 * {@link HospiceClaim}.
	 * 
	 * @throws FHIRException
	 *             (indicates test failure)
	 */
	@Test
	public void transformSampleARecord() throws FHIRException {
		List<Object> parsedRecords = ServerTestUtils
				.parseData(Arrays.asList(StaticRifResourceGroup.SAMPLE_A.getResources()));
		HospiceClaim claim = parsedRecords.stream().filter(r -> r instanceof HospiceClaim).map(r -> (HospiceClaim) r)
				.findFirst().get();

		ExplanationOfBenefit eob = HospiceClaimTransformer.transform(claim);
		assertMatches(claim, eob);
	}

	/**
	 * Verifies that the {@link ExplanationOfBenefit} "looks like" it should, if
	 * it were produced from the specified {@link HospiceClaim}.
	 * 
	 * @param claim
	 *            the {@link HospiceClaim} that the {@link ExplanationOfBenefit}
	 *            was generated from
	 * @param eob
	 *            the {@link ExplanationOfBenefit} that was generated from the
	 *            specified {@link HospiceClaim}
	 * @throws FHIRException
	 *             (indicates test failure)
	 */
	static void assertMatches(HospiceClaim claim, ExplanationOfBenefit eob) throws FHIRException {
		// Test to ensure group level fields between all claim types match
		TransformerTestUtils.assertEobCommonClaimHeaderData(eob, claim.getClaimId(), claim.getBeneficiaryId(),
				ClaimType.HOSPICE, claim.getClaimGroupId().toPlainString(), MedicareSegment.PART_A,
				Optional.of(claim.getDateFrom()), Optional.of(claim.getDateThrough()),
				Optional.of(claim.getPaymentAmount()), claim.getFinalAction());

		// test the common field provider number is set as expected in the EOB
		TransformerTestUtils.assertProviderNumber(eob, claim.getProviderNumber());

		Assert.assertTrue(eob.getInformation().stream()
				.anyMatch(i -> TransformerTestUtils.isCodeInConcept(CcwCodebookVariable.NCH_PTNT_STUS_IND_CD,
						claim.getPatientStatusCd(), i.getCategory())));

		TransformerTestUtils.assertBenefitBalanceUsedEquals(TransformerConstants.CODING_BBAPI_BENEFIT_BALANCE_TYPE,
				TransformerConstants.CODED_BENEFIT_BALANCE_TYPE_SYSTEM_UTILIZATION_DAY_COUNT, claim.getUtilizationDayCount().intValue(),
				eob.getBenefitBalanceFirstRep().getFinancial());

		TransformerTestUtils.assertDateEquals(claim.getClaimHospiceStartDate().get(),
				eob.getHospitalization().getStartElement());
		TransformerTestUtils.assertDateEquals(claim.getBeneficiaryDischargeDate().get(),
				eob.getHospitalization().getEndElement());

		// Test to ensure common group fields between Inpatient, Outpatient HHA, Hospice
		// and SNF match
		TransformerTestUtils.assertEobCommonGroupInpOutHHAHospiceSNFEquals(eob, claim.getOrganizationNpi(),
				claim.getClaimFacilityTypeCode(), claim.getClaimFrequencyCode(), claim.getClaimNonPaymentReasonCode(),
				claim.getPatientDischargeStatusCode(), claim.getClaimServiceClassificationTypeCode(),
				claim.getClaimPrimaryPayerCode(), claim.getAttendingPhysicianNpi(), claim.getTotalChargeAmount(),
				claim.getPrimaryPayerPaidAmount(), claim.getFiscalIntermediaryNumber());
		
		// test common eob information between Inpatient, HHA, Hospice and SNF claims are set as expected
		TransformerTestUtils.assertEobCommonGroupInpHHAHospiceSNFEquals(eob, claim.getClaimHospiceStartDate(), 
				claim.getBeneficiaryDischargeDate(), Optional.of(claim.getUtilizationDayCount()));

		Assert.assertEquals(4, eob.getDiagnosis().size());
		Assert.assertEquals(1, eob.getItem().size());
		ItemComponent eobItem0 = eob.getItem().get(0);
		HospiceClaimLine claimLine1 = claim.getLines().get(0);
		Assert.assertEquals(claimLine1.getLineNumber(), new BigDecimal(eobItem0.getSequence()));

		TransformerTestUtils.assertExtensionQuantityEquals(CcwCodebookVariable.BENE_HOSPC_PRD_CNT,
				claim.getHospicePeriodCount(), eob.getHospitalization());

		Assert.assertEquals(claim.getProviderStateCode(), eobItem0.getLocationAddress().getState());

		TransformerTestUtils.assertHcpcsCodes(eobItem0, claimLine1.getHcpcsCode(),
				claimLine1.getHcpcsInitialModifierCode(), claimLine1.getHcpcsSecondModifierCode(), Optional.empty(), 0/* index */);

		TransformerTestUtils.assertAdjudicationEquals(TransformerConstants.CODED_ADJUDICATION_PROVIDER_PAYMENT_AMOUNT,
				claimLine1.getProviderPaymentAmount(), eobItem0.getAdjudication());
		TransformerTestUtils.assertAdjudicationEquals(
				TransformerConstants.CODED_ADJUDICATION_BENEFICIARY_PAYMENT_AMOUNT,
				claimLine1.getBenficiaryPaymentAmount(),
				eobItem0.getAdjudication());

		// Test to ensure common group field coinsurance between Inpatient, HHA, Hospice and SNF match
		TransformerTestUtils.assertEobCommonGroupInpHHAHospiceSNFCoinsuranceEquals(eobItem0, claimLine1.getDeductibleCoinsuranceCd());

		// Test to ensure item level fields between Inpatient, Outpatient, HHA, Hopsice
		// and SNF match
		TransformerTestUtils.assertEobCommonItemRevenueEquals(eobItem0, eob, claimLine1.getRevenueCenterCode(),
				claimLine1.getRateAmount(), claimLine1.getTotalChargeAmount(),
				claimLine1.getNonCoveredChargeAmount().get(), claimLine1.getUnitCount(),
				claimLine1.getNationalDrugCodeQuantity(), claimLine1.getNationalDrugCodeQualifierCode(),
				claimLine1.getRevenueCenterRenderingPhysicianNPI(), 1/* index */);

		TransformerTestUtils.assertCareTeamEquals(claimLine1.getRevenueCenterRenderingPhysicianNPI().get(),
				ClaimCareteamrole.PRIMARY, eob);
		
		// Test to ensure item level fields between Outpatient, HHA and Hospice match
		TransformerTestUtils.assertEobCommonItemRevenueOutHHAHospice(eobItem0, claimLine1.getRevenueCenterDate(),
				claimLine1.getPaymentAmount());
		
		// verify {@link
		// TransformerUtils#mapEobType(CodeableConcept,ClaimType,Optional,Optional)}
		// method worked as expected for this claim type
		TransformerTestUtils.assertMapEobType(eob.getType(), ClaimType.HOSPICE,
				Optional.of(org.hl7.fhir.dstu3.model.codesystems.ClaimType.INSTITUTIONAL),
				Optional.of(claim.getNearLineRecordIdCode()), Optional.of(claim.getClaimTypeCode()));
	}
}
