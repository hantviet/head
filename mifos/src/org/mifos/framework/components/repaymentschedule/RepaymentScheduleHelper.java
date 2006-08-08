/**
 * RepaymentScheduleHelper.java version:1.0
 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.
 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the
 * License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license
 * and how it is applied.
 *
 */

package org.mifos.framework.components.repaymentschedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.loan.business.LoanFeeScheduleEntity;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.application.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.accounts.util.valueobjects.AccountActionDate;
import org.mifos.application.accounts.util.valueobjects.AccountFeesActionDetail;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerFeeScheduleEntity;
import org.mifos.application.customer.business.CustomerScheduleEntity;
import org.mifos.application.customer.util.valueobjects.CustomerActionDate;
import org.mifos.application.customer.util.valueobjects.CustomerFeesActionDetail;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.persistence.FeePersistence;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.framework.components.interestcalculator.InterestInputs;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.scheduler.SchedulerException;
import org.mifos.framework.components.scheduler.SchedulerIntf;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.Money;

/**
 * 
 * This class is the helper class to do repayment schedule related tasks
 */
public class RepaymentScheduleHelper {
	/**
	 * This method builds the schedular object
	 * 
	 * @param Meeting
	 * @param isHolidayRequired
	 * @return SchedularIntf
	 * @throws RepaymentScheduleException
	 */
	public static SchedulerIntf getSchedulerObject(Meeting meeting,
			boolean holidayWeekOffRequired) throws RepaymentScheduleException {

		try {
			return MeetingScheduleHelper.getSchedulerObject(meeting,
					holidayWeekOffRequired);
		} catch (SchedulerException scheduleException) {
			throw new RepaymentScheduleException(scheduleException.getKey(),
					scheduleException);
		}

	}

	/**
	 * This method builds the inputs required for interst calculation
	 * 
	 * @param RepaymentScheduleInputsIfc
	 * @return InterestInputs
	 * @throws RepaymentScheduleException
	 */
	public static InterestInputs buildInterestInputs(
			RepaymentScheduleInputsIfc repaymentScheduleInputs) {

		InterestInputs interestInputs = new InterestInputs();

		interestInputs.setPrincipal(repaymentScheduleInputs.getPrincipal());
		interestInputs.setInterestRate(repaymentScheduleInputs
				.getInterestRate());
		interestInputs.setDuration(repaymentScheduleInputs
				.getNoOfInstallments()
				* repaymentScheduleInputs.getRepaymentFrequency()
						.getMeetingDetails().getRecurAfter());
		interestInputs.setDurationType(MeetingScheduleHelper
				.getReccurence(repaymentScheduleInputs.getRepaymentFrequency()
						.getMeetingDetails().getRecurrenceType()
						.getRecurrenceId()));

		List<InstallmentDate> instDate = repaymentScheduleInputs
				.getInstallmentDate();

		interestInputs.setInstallmentStartDate(repaymentScheduleInputs
				.getDisbursementDate());
		interestInputs.setInstallmentEndDate(instDate.get(instDate.size() - 1)
				.getInstallmentDueDate());

		return interestInputs;

	}

	/**
	 * This method builds the inputs required for generating emi
	 * 
	 * @param RepaymentScheduleInputsIfc
	 * @return EMIInputs
	 * @throws RepaymentScheduleException
	 */
	public static EMIInputs buildEMIInputs(
			RepaymentScheduleInputsIfc repaymentScheduleInputs) {

		EMIInputs emiInputs = new EMIInputs();

		emiInputs.setGraceType(repaymentScheduleInputs.getGraceType());
		emiInputs.setGracePeriod(repaymentScheduleInputs.getGracePeriod());
		emiInputs.setIsInterestDedecutedAtDisburesement(repaymentScheduleInputs
				.getIsInterestDedecutedAtDisburesement());
		emiInputs.setIsPrincipalInLastPayment(repaymentScheduleInputs
				.getIsPrincipalInLastPayment());
		emiInputs.setPrincipal(repaymentScheduleInputs.getPrincipal());
		emiInputs.setNoOfInstallments(repaymentScheduleInputs
				.getNoOfInstallments());
		emiInputs.setLoanInterest(repaymentScheduleInputs.getLoanInterest());

		emiInputs.setPrincipal(repaymentScheduleInputs.getPrincipal());

		return emiInputs;

	}

	/**
	 * This method builds the inputs required for generating fee
	 * 
	 * @param RepaymentScheduleInputsIfc
	 * @return FeeInputs
	 * @throws RepaymentScheduleException
	 */
	public static FeeInputs buildFeeInputs(
			RepaymentScheduleInputsIfc repaymentScheduleInputs) {
		FeeInputs feeInputs = new FeeInputs();

		feeInputs.setAccountFees(repaymentScheduleInputs.getAccountFee());

		feeInputs.setInstallmentDate(repaymentScheduleInputs
				.getInstallmentDate());
		feeInputs.setRepaymentFrequency(repaymentScheduleInputs
				.getRepaymentFrequency());
		feeInputs.setLoanAmount(repaymentScheduleInputs.getPrincipal());
		feeInputs.setLoanInterest(repaymentScheduleInputs.getLoanInterest());
		feeInputs.setFeeStartDate(repaymentScheduleInputs.getFeeStartDate());
		feeInputs.setMeetingToConsider(repaymentScheduleInputs
				.getMeetingToConsider());
		feeInputs.setAccountFeesEntity(repaymentScheduleInputs
				.getAccountFeesEntity());

		return feeInputs;

	}

	/**
	 * This method builds the inputs required for getting grace period
	 * 
	 * @param RepaymentScheduleInputsIfc
	 * @return GraceInputs
	 * @throws RepaymentScheduleException
	 */
	public static GraceInputs buildGraceInputs(
			RepaymentScheduleInputsIfc repaymentScheduleInputs) {
		GraceInputs graceInputs = new GraceInputs();

		graceInputs.setGraceType(repaymentScheduleInputs.getGraceType());
		graceInputs.setGracePeriod(repaymentScheduleInputs.getGracePeriod());
		return graceInputs;
	}

	/**
	 * This method merges the meeting objects
	 * 
	 * @param repaymentFrequency
	 * @param meetingToMerge
	 * @return Meeting
	 * @throws RepaymentScheduleException
	 */
	public static Meeting mergeFrequency(Meeting repaymentFrequency,
			Meeting meetingToMerge) {

		return MeetingScheduleHelper.mergeFrequency(repaymentFrequency,
				meetingToMerge);

	}

	/**
	 * This method gets the value object from the repayamet schedule object
	 * 
	 * @param RepaymentSchedule
	 * @return Set
	 * @throws RepaymentScheduleException
	 */
	public static Set<AccountActionDate> getActionDateValueObject(
			RepaymentSchedule repaymentSchedule, String type) {

		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug(
				"RepamentScheduleHelper:getActionDateValueObject invoked ");
		Set accountActionDateSet = new HashSet();
		List<RepaymentScheduleInstallment> repaymentScheduleInstallmentList = repaymentSchedule
				.getRepaymentScheduleInstallment();
		RepaymentScheduleInstallment repaymentScheduleInstallment = null;

		int count = repaymentScheduleInstallmentList.size();
		AccountActionDate accountActionDate = null;
		for (int i = 0; i < count; i++) {
			repaymentScheduleInstallment = repaymentScheduleInstallmentList
					.get(i);
			accountActionDate = getInstallmentValueObject(
					repaymentScheduleInstallment, type);
			accountActionDateSet.add(accountActionDate);

		}
		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug(
				"RepamentScheduleHelper:getActionDateValueObject returning ");

		return accountActionDateSet;

	}

	public static AccountActionDate getInstallmentValueObject(
			RepaymentScheduleInstallment repaymentScheduleInstallment,
			String type) {
		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug(
				"RepamentScheduleHelper:getInstallmentValueObject invoked ");

		AccountActionDate accountActionDate = null;

		accountActionDate = new CustomerActionDate();

		accountActionDate.setInstallmentId(new Short(new Integer(
				repaymentScheduleInstallment.getInstallment()).shortValue()));
		accountActionDate.setActionDate(repaymentScheduleInstallment
				.getDueDate());
		accountActionDate.setPrincipal(repaymentScheduleInstallment
				.getPrincipal());
		accountActionDate.setInterest(repaymentScheduleInstallment
				.getInterest());
		accountActionDate.setDeposit(new Money());
		accountActionDate.setPenalty(new Money());
		accountActionDate
				.setMiscFee(repaymentScheduleInstallment.getMiscFees());
		accountActionDate.setMiscPenalty(repaymentScheduleInstallment
				.getMiscPenalty());
		Set<AccountFeesActionDetail> accountFeesActionDetailSet = getFeeDetail(
				accountActionDate, repaymentScheduleInstallment
						.getFeeInstallment(), type);
		accountActionDate
				.setAccountFeesActionDetail(accountFeesActionDetailSet);

		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug(
				"RepamentScheduleHelper:getInstallmentValueObject returning ");

		return accountActionDate;
	}

	public static Set<AccountFeesActionDetail> getFeeDetail(
			AccountActionDate accountActionDate, FeeInstallment feeInstallment,
			String type) {
		if (feeInstallment == null)
			return null;

		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug(
				"RepamentScheduleHelper:getFeeDetail invoked ");

		List<AccountFeeInstallment> accountFeeInstallmentList = feeInstallment
				.getSummaryAccountFeeInstallment();
		AccountFeeInstallment accountFeeInstallment = null;
		int count = accountFeeInstallmentList.size();
		AccountFeesActionDetail accountFeesActionDetail = null;

		Set<AccountFeesActionDetail> accountFeesActionDetailSet = new HashSet();

		for (int i = 0; i < count; i++) {

			accountFeesActionDetail = new CustomerFeesActionDetail();

			accountFeeInstallment = accountFeeInstallmentList.get(i);

			accountFeesActionDetail.setInstallmentId(accountActionDate
					.getInstallmentId());
			accountFeesActionDetail.setFeeId(new Short(accountFeeInstallment
					.getFeeId()));
			accountFeesActionDetail.setFeeAmount(accountFeeInstallment
					.getAccountFeeAmount());
			accountFeesActionDetail.setAccountFee(accountFeeInstallment
					.getAccountFee());
			accountFeeInstallment.getAccountFee().setLastAppliedDate(
					accountActionDate.getActionDate());
			accountFeesActionDetailSet.add(accountFeesActionDetail);
			if (accountActionDate.getAccountFeesActionDetail() != null
					&& accountActionDate.getAccountFeesActionDetail().size() > 0)
				accountFeesActionDetailSet.addAll(accountActionDate
						.getAccountFeesActionDetail());

		}

		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug(
				"RepamentScheduleHelper:getFeeDetail returning ");

		return accountFeesActionDetailSet;

	}

	public static List<AccountActionDate> getAccountActionFee(
			List<AccountActionDate> accountActionDateList,
			List<FeeInstallment> feeInstallmentList, String type) {

		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug(
				"RepamentScheduleHelper:getAccountActionFee invoked ");
		Iterator<FeeInstallment> iterFeeInstallment = feeInstallmentList
				.iterator();
		FeeInstallment feeInstallment = null;
		List<AccountActionDate> accountActionDateListReturn = new ArrayList();
		AccountActionDate accActionDate = null;
		Map<Short, AccountActionDate> accountActionDateMap = getAccountActionDateMap(accountActionDateList);
		Set<AccountFeesActionDetail> accountFeesActionDetailSet = null;

		while (iterFeeInstallment.hasNext()) {
			feeInstallment = iterFeeInstallment.next();
			accActionDate = accountActionDateMap.get(new Short(new Integer(
					feeInstallment.getInstallmentId()).shortValue()));
			accountFeesActionDetailSet = getFeeDetail(accActionDate,
					feeInstallment, type);
			accActionDate
					.setAccountFeesActionDetail(accountFeesActionDetailSet);
			accountActionDateListReturn.add(accActionDate);

		}
		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug(
				"RepamentScheduleHelper:getAccountActionFee returning ");
		return accountActionDateListReturn;

	}

	private static Map getAccountActionDateMap(
			List<AccountActionDate> accountActionDateList) {
		Map<Short, AccountActionDate> accountActionDateMap = new HashMap<Short, AccountActionDate>();
		int count = accountActionDateList.size();
		AccountActionDate accActionDate = null;

		for (int i = 0; i < count; i++) {
			accActionDate = accountActionDateList.get(i);
			accountActionDateMap.put(accActionDate.getInstallmentId(),
					accActionDate);
		}

		return accountActionDateMap;
	}

	/**
	 * This method gets the value object from the repayamet schedule object
	 * 
	 * @param RepaymentSchedule
	 * @return Set
	 * @throws RepaymentScheduleException
	 */
	public static Set<AccountActionDateEntity> getActionDateEntity(
			RepaymentSchedule repaymentSchedule, String type,
			AccountBO account, CustomerBO customer) {

		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug(
				"RepamentScheduleHelper:getActionDateEntity invoked ");
		Set<AccountActionDateEntity> accountActionDateEntitySet = new HashSet<AccountActionDateEntity>();
		List<RepaymentScheduleInstallment> repaymentScheduleInstallmentList = repaymentSchedule
				.getRepaymentScheduleInstallment();
		AccountActionDateEntity accountActionDateEntity = null;
		for (RepaymentScheduleInstallment repaymentScheduleInstallment : repaymentScheduleInstallmentList) {
			accountActionDateEntity = getInstallmentEntity(
					repaymentScheduleInstallment, type, account, customer);

			accountActionDateEntitySet.add(accountActionDateEntity);

		}
		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug(
				"RepamentScheduleHelper:getActionDateValueObject returning ");

		return accountActionDateEntitySet;

	}

	public static Set<AccountActionDateEntity> getActionDateEntity(
			RepaymentSchedule repaymentSchedule, String type,
			AccountBO account, CustomerBO customer, Short lastInstallment) {

		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug(
				"RepamentScheduleHelper:getActionDateEntity invoked ");
		Set<AccountActionDateEntity> accountActionDateEntitySet = new HashSet<AccountActionDateEntity>();
		List<RepaymentScheduleInstallment> repaymentScheduleInstallmentList = repaymentSchedule
				.getRepaymentScheduleInstallment();
		AccountActionDateEntity accountActionDateEntity = null;
		for (RepaymentScheduleInstallment repaymentScheduleInstallment : repaymentScheduleInstallmentList) {
			accountActionDateEntity = getInstallmentEntity(
					repaymentScheduleInstallment, type, account, customer,
					lastInstallment);
			accountActionDateEntitySet.add(accountActionDateEntity);

			// <<<<<<< .mine
			// accountActionDateEntity.setInstallmentId(new Short(new
			// Integer(repaymentScheduleInstallment.getInstallment()).shortValue()));
			// accountActionDateEntity.setActionDate(new
			// java.sql.Date(repaymentScheduleInstallment.getDueDate().getTime()));
			// accountActionDateEntity.setPrincipal(repaymentScheduleInstallment.getPrincipal());
			// accountActionDateEntity.setInterest(repaymentScheduleInstallment.getInterest());
			// accountActionDateEntity.setDeposit(new Money());
			// accountActionDateEntity.setPenalty(new Money());
			//			
			// accountActionDateEntity.setPenaltyPaid(new Money());
			// accountActionDateEntity.setMiscPenaltyPaid(new Money());
			//			
			// accountActionDateEntity.setMiscFee(repaymentScheduleInstallment.getMiscFees());
			// accountActionDateEntity.setMiscPenalty(repaymentScheduleInstallment.getMiscPenalty());
			// accountActionDateEntity.setPaymentStatus(YesNoFlag.NO.getValue());
			// if(repaymentScheduleInstallment.getFeeInstallment() != null)
			// setFeeDetailEntity(accountActionDateEntity,repaymentScheduleInstallment.getFeeInstallment());
			//			
			// =======
		}
		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug(
				"RepamentScheduleHelper:getActionDateValueObject returning ");

		return accountActionDateEntitySet;

	}

	public static AccountActionDateEntity getInstallmentEntity(
			RepaymentScheduleInstallment repaymentScheduleInstallment,
			String type, AccountBO account, CustomerBO customer,
			Short lastInstallment) {
		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug(
				"RepamentScheduleHelper:getInstallmentEntity invoked ");

		AccountActionDateEntity accountActionDateEntity;
		if (type.equals("Loan")) {
			LoanScheduleEntity loanScheduleEntity = new LoanScheduleEntity(
					account, customer, new Short(new Integer(
							repaymentScheduleInstallment.getInstallment()
									+ lastInstallment).shortValue()),
					new java.sql.Date(repaymentScheduleInstallment.getDueDate()
							.getTime()), PaymentStatus.UNPAID,
					repaymentScheduleInstallment.getPrincipal(),
					repaymentScheduleInstallment.getInterest());
			loanScheduleEntity.setPenalty(new Money());
			loanScheduleEntity.setMiscFee(repaymentScheduleInstallment
					.getMiscFees());
			loanScheduleEntity.setMiscPenalty(repaymentScheduleInstallment
					.getMiscPenalty());
			accountActionDateEntity = loanScheduleEntity;
		} else if (type.equals("Saving")) {
			accountActionDateEntity = new SavingsScheduleEntity(account,
					customer, new Short(new Integer(
							repaymentScheduleInstallment.getInstallment()
									+ lastInstallment).shortValue()),
					new java.sql.Date(repaymentScheduleInstallment.getDueDate()
							.getTime()), PaymentStatus.UNPAID, new Money());
		} else {
			CustomerScheduleEntity customerScheduleEntity = new CustomerScheduleEntity(
					account, customer, new Short(new Integer(
							repaymentScheduleInstallment.getInstallment()
									+ lastInstallment).shortValue()),
					new java.sql.Date(repaymentScheduleInstallment.getDueDate()
							.getTime()), PaymentStatus.UNPAID);
			customerScheduleEntity.setMiscFee(repaymentScheduleInstallment
					.getMiscFees());
			customerScheduleEntity.setMiscPenalty(repaymentScheduleInstallment
					.getMiscPenalty());
			accountActionDateEntity = customerScheduleEntity;
		}

		if (repaymentScheduleInstallment.getFeeInstallment() != null)
			setFeeDetailEntity(accountActionDateEntity,
					repaymentScheduleInstallment.getFeeInstallment(), type);

		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug(
				"RepamentScheduleHelper:getInstallmentValueObject returning ");

		return accountActionDateEntity;
	}

	public static AccountActionDateEntity getInstallmentEntity(
			RepaymentScheduleInstallment repaymentScheduleInstallment,
			String type, AccountBO account, CustomerBO customer) {
		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug(
				"RepamentScheduleHelper:getInstallmentEntity invoked ");

		AccountActionDateEntity accountActionDateEntity;
		if (type.equals("Loan")) {
			LoanScheduleEntity loanScheduleEntity = new LoanScheduleEntity(
					account, customer, new Short(new Integer(
							repaymentScheduleInstallment.getInstallment())
							.shortValue()),
					new java.sql.Date(repaymentScheduleInstallment.getDueDate()
							.getTime()), PaymentStatus.UNPAID,
					repaymentScheduleInstallment.getPrincipal(),
					repaymentScheduleInstallment.getInterest());
			loanScheduleEntity.setPenalty(new Money());
			loanScheduleEntity.setMiscFee(repaymentScheduleInstallment
					.getMiscFees());
			loanScheduleEntity.setMiscPenalty(repaymentScheduleInstallment
					.getMiscPenalty());
			accountActionDateEntity = loanScheduleEntity;
		} else if (type.equals("Saving")) {
			accountActionDateEntity = new SavingsScheduleEntity(account,
					customer, new Short(new Integer(
							repaymentScheduleInstallment.getInstallment())
							.shortValue()),
					new java.sql.Date(repaymentScheduleInstallment.getDueDate()
							.getTime()), PaymentStatus.UNPAID, new Money());
		} else {
			CustomerScheduleEntity customerScheduleEntity = new CustomerScheduleEntity(
					account, customer, new Short(new Integer(
							repaymentScheduleInstallment.getInstallment())
							.shortValue()),
					new java.sql.Date(repaymentScheduleInstallment.getDueDate()
							.getTime()), PaymentStatus.UNPAID);
			customerScheduleEntity.setMiscFee(repaymentScheduleInstallment
					.getMiscFees());
			customerScheduleEntity.setMiscPenalty(repaymentScheduleInstallment
					.getMiscPenalty());
			accountActionDateEntity = customerScheduleEntity;
		}

		if (repaymentScheduleInstallment.getFeeInstallment() != null)
			setFeeDetailEntity(accountActionDateEntity,
					repaymentScheduleInstallment.getFeeInstallment(), type);

		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug(
				"RepamentScheduleHelper:getInstallmentValueObject returning ");

		return accountActionDateEntity;
	}

public static void setFeeDetailEntity(
			AccountActionDateEntity accountActionDate,
			FeeInstallment feeInstallment, String type) {
		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug(
				"RepamentScheduleHelper:getFeeDetail invoked ");

		List<AccountFeeInstallment> accountFeeInstallmentList = feeInstallment
				.getSummaryAccountFeeInstallment();
		AccountFeeInstallment accountFeeInstallment = null;
		int count = accountFeeInstallmentList.size();
		AccountFeesActionDetailEntity accountFeesActionDetailEntity = null;

		for (int i = 0; i < count; i++) {

			accountFeeInstallment = accountFeeInstallmentList.get(i);
			AccountFeesEntity accountFeesEntity = getAccountFeesEntity(accountFeeInstallment
					.getAccountFee().getAccountFeeId());

			if(accountFeesEntity == null)
				accountFeesEntity = accountFeeInstallment.getAccountFeeEntity();
			if (type.equals("Loan")) {
				accountFeesActionDetailEntity = new LoanFeeScheduleEntity(
						accountActionDate,
						accountActionDate.getInstallmentId(),
						getFeesBO(new Short(accountFeeInstallment.getFeeId())),
						accountFeesEntity,
						accountFeeInstallment.getAccountFeeAmount());
				accountFeeInstallment.getAccountFee().setLastAppliedDate(
						accountActionDate.getActionDate());
				((LoanScheduleEntity) accountActionDate)
						.addAccountFeesAction(accountFeesActionDetailEntity);
			} else {
				accountFeesActionDetailEntity = new CustomerFeeScheduleEntity(
						accountActionDate,
						accountActionDate.getInstallmentId(),
						getFeesBO(new Short(accountFeeInstallment.getFeeId())),
						accountFeesEntity,
						accountFeeInstallment.getAccountFeeAmount());
				
				accountFeeInstallment.getAccountFee().setLastAppliedDate(
						accountActionDate.getActionDate());
				
				((CustomerScheduleEntity) accountActionDate)
						.addAccountFeesAction(accountFeesActionDetailEntity);
			}

		}

		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug(
				"RepamentScheduleHelper:getFeeDetail returning ");

	}	private static FeeBO getFeesBO(Short feeId) {
		FeePersistence feePersistenceService = new FeePersistence();
		return feePersistenceService.getFee(feeId);
	}

	private static AccountFeesEntity getAccountFeesEntity(
			Integer accountFeesEntityId) {
		try {
			AccountPersistanceService accountPersistanceService = new AccountPersistanceService();
			return accountPersistanceService
					.getAccountFeeEntity(accountFeesEntityId);
		} catch (PersistenceException pe) {
			return null;
		}
	}

}
