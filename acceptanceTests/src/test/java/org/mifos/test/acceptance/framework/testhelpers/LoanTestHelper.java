/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.test.acceptance.framework.testhelpers;

import com.thoughtworks.selenium.Selenium;
import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.mifos.test.acceptance.framework.AbstractPage;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.loan.ApplyChargePage;
import org.mifos.test.acceptance.framework.loan.ApplyPaymentConfirmationPage;
import org.mifos.test.acceptance.framework.loan.ApplyPaymentPage;
import org.mifos.test.acceptance.framework.loan.ChargeParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountEntryPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSubmitParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanConfirmationPage;
import org.mifos.test.acceptance.framework.loan.DisburseLoanPage;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.EditAccountStatusConfirmationPage;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountInformationPage;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountInformationParameters;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountStatusPage;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountStatusParameters;
import org.mifos.test.acceptance.framework.loan.EditPreviewLoanAccountPage;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.PaymentParameters;
import org.mifos.test.acceptance.framework.loan.QuestionResponseParameters;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalEntryPage;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalParameters;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalSchedulePreviewPage;
import org.mifos.test.acceptance.framework.loan.ViewInstallmentDetailsPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.loanproduct.EditLoanProductPage;
import org.mifos.test.acceptance.framework.loanproduct.EditLoanProductPreviewPage;
import org.mifos.test.acceptance.framework.loanproduct.LoanProductDetailsPage;
import org.mifos.test.acceptance.framework.loanproduct.ViewLoanProductsPage;
import org.mifos.test.acceptance.framework.login.LoginPage;
import org.mifos.test.acceptance.framework.search.SearchResultsPage;
import org.mifos.test.acceptance.questionnaire.QuestionResponsePage;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Holds methods common to most loan tests.
 *
 */
public class LoanTestHelper {

    private final Selenium selenium;
    private final NavigationHelper navigationHelper;

    public LoanTestHelper(Selenium selenium) {
        this.selenium = selenium;
        this.navigationHelper = new NavigationHelper(selenium);
    }

    public LoanTestHelper(Selenium selenium, NavigationHelper helper) {
        this.selenium = selenium;
        this.navigationHelper = helper;
    }

    /**
     * Creates a loan account.
     * @param searchParameters Parameters to find the client/group that will be the owner of the account.
     * @param submitAccountParameters The parameters for the loan account.
     * @return LoanAccountPage
     */
    public LoanAccountPage createLoanAccount(CreateLoanAccountSearchParameters searchParameters,
                                             CreateLoanAccountSubmitParameters submitAccountParameters) {
        return navigateToLoanAccountEntryPage(searchParameters)
                .submitAndNavigateToLoanAccountConfirmationPage(submitAccountParameters)
                .navigateToLoanAccountDetailsPage();
    }

    /**
     * Creates a loan account.
     * @param searchParameters Parameters to find the client/group that will be the owner of the account.
     * @param submitAccountParameters The parameters for the loan account.
     * @param questionResponseParameters The parameters for the create loan question responses.
     * @return LoanAccountPage
     */
    public LoanAccountPage createLoanAccount(CreateLoanAccountSearchParameters searchParameters,
                                             CreateLoanAccountSubmitParameters submitAccountParameters, QuestionResponseParameters questionResponseParameters) {
        return navigateToLoanAccountEntryPage(searchParameters)
                .submitAndNavigateToLoanAccountConfirmationPage(submitAccountParameters, questionResponseParameters)
                .navigateToLoanAccountDetailsPage();
    }

    private CreateLoanAccountEntryPage navigateToLoanAccountEntryPage(CreateLoanAccountSearchParameters searchParameters) {
        CreateLoanAccountSearchPage createLoanAccountSearchPage = navigateToCreateLoanAccountSearchPage();
        createLoanAccountSearchPage.verifyPage();
        CreateLoanAccountEntryPage createLoanAccountEntryPage = createLoanAccountSearchPage
        .searchAndNavigateToCreateLoanAccountPage(searchParameters);
        createLoanAccountEntryPage.verifyPage();
        return createLoanAccountEntryPage;
    }

    /**
     * TODO: Create a helper for creating a GLIM loan account.
     */


    /**
     * Edits the loan account with id loanId and updates its settings with the ones in params.
     * @param loanId The account id.
     * @param accountSubmitParameters The create loan parameters.
     * @param editAccountParameters The edit loan parameters.
     * @return edit preview loan account page
     */
    public EditPreviewLoanAccountPage changeLoanAccountInformation(String loanId, CreateLoanAccountSubmitParameters accountSubmitParameters, EditLoanAccountInformationParameters editAccountParameters) {
        LoanAccountPage loanAccountPage = navigationHelper.navigateToLoanAccountPage(loanId);

        EditLoanAccountInformationPage editAccountInformationPage = loanAccountPage.navigateToEditAccountInformation();

        editAccountInformationPage.editAccountParams(accountSubmitParameters, editAccountParameters);
        return editAccountInformationPage.submitAndNavigateToAccountInformationPreviewPage();
    }

    /**
     * Edits the loan account with id loanId and updates its settings with the ones in params.
     * @param loanId The account id.
     * @param params The status parameters.
     */
    public void changeLoanAccountStatus(String loanId, EditLoanAccountStatusParameters params) {
        changeLoanAccountStatusProvidingQuestionGroupResponses(loanId, params, null);
    }

    public void changeLoanAccountStatusProvidingQuestionGroupResponses(String loanId, EditLoanAccountStatusParameters params, QuestionResponseParameters responseParameters) {
        LoanAccountPage loanAccountPage = navigationHelper.navigateToLoanAccountPage(loanId);

        EditLoanAccountStatusPage editAccountStatusPage = loanAccountPage.navigateToEditAccountStatus();
        editAccountStatusPage.verifyPage();

        editAccountStatusPage.submitAndNavigateToNextPage(params);

        if (responseParameters != null) {
            populateQuestionGroupResponses(responseParameters);
        }

        new EditAccountStatusConfirmationPage(selenium).submitAndNavigateToLoanAccountPage();
    }

    private void populateQuestionGroupResponses(QuestionResponseParameters responseParameters) {
        QuestionResponsePage responsePage = new QuestionResponsePage(selenium);
        responsePage.populateAnswers(responseParameters);
        responsePage.navigateToNextPage();
    }


    /**
     * Disburses the loan with id <tt>loanId</tt>.
     * @param loanId The system/global id of the loan that'll be disbursed.
     * @param disburseParameters The disbursal parameters.
     * @return The loan account page of the loan account with id loanId.
     */
    public LoanAccountPage disburseLoan(String loanId, DisburseLoanParameters disburseParameters) {
        DisburseLoanPage disburseLoanPage = prepareToDisburseLoan(loanId);

        DisburseLoanConfirmationPage disburseLoanConfirmationPage = disburseLoanPage.submitAndNavigateToDisburseLoanConfirmationPage(disburseParameters);
        disburseLoanConfirmationPage.verifyPage();

        return disburseLoanConfirmationPage.submitAndNavigateToLoanAccountPage();
    }

    public void editLoanProduct(String loanProduct, boolean interestWaiver) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        ViewLoanProductsPage viewLoanProducts = adminPage.navigateToViewLoanProducts();
        LoanProductDetailsPage loanProductDetailsPage = viewLoanProducts.viewLoanProductDetails(loanProduct);
        EditLoanProductPage editLoanProductPage = loanProductDetailsPage.editLoanProduct();
        DefineNewLoanProductPage.SubmitFormParameters formParameters = new DefineNewLoanProductPage.SubmitFormParameters();
        formParameters.setInterestWaiver(interestWaiver);
        EditLoanProductPreviewPage editLoanProductPreviewPage = editLoanProductPage.submitInterestWaiverChanges(formParameters);
        editLoanProductPreviewPage.submit();
    }

    public void editLoanProduct(String loanProduct, String... questionGroup) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        ViewLoanProductsPage viewLoanProducts = adminPage.navigateToViewLoanProducts();
        LoanProductDetailsPage loanProductDetailsPage = viewLoanProducts.viewLoanProductDetails(loanProduct);
        EditLoanProductPage editLoanProductPage = loanProductDetailsPage.editLoanProduct();
        DefineNewLoanProductPage.SubmitFormParameters formParameters = new DefineNewLoanProductPage.SubmitFormParameters();
        formParameters.setQuestionGroups(Arrays.asList(questionGroup));
        EditLoanProductPreviewPage editLoanProductPreviewPage = editLoanProductPage.submitQuestionGroupChanges(formParameters);
        editLoanProductPreviewPage.submit();
    }

    public DisburseLoanPage prepareToDisburseLoan(String loanId) {
        LoanAccountPage loanAccountPage = navigationHelper.navigateToLoanAccountPage(loanId);

        DisburseLoanPage disburseLoanPage = loanAccountPage.navigateToDisburseLoan();
        disburseLoanPage.verifyPage();
        return disburseLoanPage;
    }

    /**
     * Applies a charge to the loan account with id <tt>loanId</tt>.
     * @param loanId The account id.
     * @param params The charge parameters (amount and type).
     * @return The loan account page for the loan account.
     */
    public LoanAccountPage applyCharge(String loanId, ChargeParameters params) {
        LoanAccountPage loanAccountPage = navigationHelper.navigateToLoanAccountPage(loanId);

        ApplyChargePage applyChargePage = loanAccountPage.navigateToApplyCharge();
        applyChargePage.verifyPage();

        loanAccountPage = applyChargePage.submitAndNavigateToApplyChargeConfirmationPage(params);

        return loanAccountPage;
    }

    /**
     * Applies a payment to the loan account with id <tt>loanId</tt>.
     * @param loanId The account id.
     * @param paymentParams The payment parameters.
     * @return The loan account page for the loan account.
     */
    public LoanAccountPage applyPayment(String loanId, PaymentParameters paymentParams) {
        LoanAccountPage loanAccountPage = navigationHelper.navigateToLoanAccountPage(loanId);

        ApplyPaymentPage applyPaymentPage = loanAccountPage.navigateToApplyPayment();
        applyPaymentPage.verifyPage();

        ApplyPaymentConfirmationPage applyPaymentConfirmationPage = applyPaymentPage.submitAndNavigateToApplyPaymentConfirmationPage(paymentParams);
        applyPaymentConfirmationPage.verifyPage();

        loanAccountPage = applyPaymentConfirmationPage.submitAndNavigateToLoanAccountDetailsPage();

        return loanAccountPage;
    }

    /**
     * Waive the fee associated with the loan account with id <tt>loanId</tt>.
     * @param loanId The loan account id.
     */
    public void waiveFee(String loanId) {
        LoanAccountPage loanAccountPage = navigationHelper.navigateToLoanAccountPage(loanId);

        ViewInstallmentDetailsPage installmentDetailsPage = loanAccountPage.navigateToViewInstallmentDetails();
        installmentDetailsPage.verifyPage();

        installmentDetailsPage.waiveFee();
    }

    /**
     * Waive the penalty associated with the loan account with id <tt>loanId</tt>.
     * @param loanId The loan account id.
     */
    public void waivePenalty(String loanId) {
        LoanAccountPage loanAccountPage = navigationHelper.navigateToLoanAccountPage(loanId);

        ViewInstallmentDetailsPage installmentDetailsPage = loanAccountPage.navigateToViewInstallmentDetails();
        installmentDetailsPage.verifyPage();

        installmentDetailsPage.waivePenalty();
    }

    /**
     * Redoes the loan disbursal.
     * @param clientName The name of the client.
     * @param loanProduct The name of the loan product.
     * @param paramsPastDate The parameters for the loan disbursal (past date).
     * @param paramsCurrentDate The parameters for the loan disbursal (current date).
     * @param amountPaid The amount typed in second pay row. Used to pay whole loan.
     * @return LoanAccountPage
     */
    public LoanAccountPage redoLoanDisbursal(String clientName, String loanProduct, RedoLoanDisbursalParameters paramsPastDate, RedoLoanDisbursalParameters paramsCurrentDate, int amountPaid) {
        RedoLoanDisbursalEntryPage dataEntryPage = navigationHelper
            .navigateToAdminPage()
            .navigateToRedoLoanDisbursal()
            .searchAndNavigateToRedoLoanDisbursalPage(clientName)
            .navigateToRedoLoanDisbursalChooseLoanProductPage(clientName)
            .submitAndNavigateToRedoLoanDisbursalEntryPage(loanProduct);

        if(paramsCurrentDate != null) { // tests current or future date if need to.
            dataEntryPage = dataEntryPage.submitFutureDateAndReloadPageWithInputError(paramsCurrentDate);
            dataEntryPage.verifyFutureDateInputError();
        }

        RedoLoanDisbursalSchedulePreviewPage schedulePreviewPage = dataEntryPage.submitAndNavigateToRedoLoanDisbursalSchedulePreviewPage(paramsPastDate);
        schedulePreviewPage.typeAmountPaid(amountPaid);

        return schedulePreviewPage
            .submitAndNavigateToRedoLoanDisbursalPreviewPage()
            .submitAndNavigateToLoanAccountConfirmationPage()
            .navigateToLoanAccountDetailsPage();
    }

    public LoanAccountPage navigateToLoanAccountPage(CreateLoanAccountSearchParameters searchParams) {
        String searchString = searchParams.getSearchString();

        LoginPage loginPage = new AppLauncher(selenium).launchMifos().logout();
        loginPage.verifyPage();
        HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
        homePage.verifyPage();
        SearchResultsPage searchResultsPage = homePage.search(searchString);

        return searchResultsPage.navigateToLoanAccountDetailPage(searchString);
    }

    private CreateLoanAccountSearchPage navigateToCreateLoanAccountSearchPage() {
        LoginPage loginPage = new AppLauncher(selenium).launchMifos().logout();
        loginPage.verifyPage();
        HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
        homePage.verifyPage();
        ClientsAndAccountsHomepage clientsAndAccountsPage = homePage.navigateToClientsAndAccountsUsingHeaderTab();
        clientsAndAccountsPage.verifyPage();
        return clientsAndAccountsPage.navigateToCreateLoanAccountUsingLeftMenu();
    }

    public CreateLoanAccountEntryPage navigateToCreateLoanAccountEntryPage(CreateLoanAccountSearchParameters searchParameters) {
        return navigateToLoanAccountEntryPage(searchParameters);
    }

    public CreateLoanAccountEntryPage navigateToCreateLoanAccountEntryPageWithoutLogout(CreateLoanAccountSearchParameters searchParameters) {
        ClientsAndAccountsHomepage clientsAndAccountsPage = new HomePage(selenium).navigateToClientsAndAccountsUsingHeaderTab();
        clientsAndAccountsPage.verifyPage();
        CreateLoanAccountSearchPage createLoanAccountSearchPage = clientsAndAccountsPage.navigateToCreateLoanAccountUsingLeftMenu();
        createLoanAccountSearchPage.verifyPage();
        CreateLoanAccountEntryPage createLoanAccountEntryPage = createLoanAccountSearchPage
        .searchAndNavigateToCreateLoanAccountPage(searchParameters);
        createLoanAccountEntryPage.verifyPage();
        return createLoanAccountEntryPage;
    }

    public DisburseLoanPage prepareToDisburseLoanWithoutLogout(HomePage homePage, String loanId) {
        SearchResultsPage searchResultsPage = homePage.search(loanId);
        searchResultsPage.verifyPage();
        LoanAccountPage loanAccountPage = searchResultsPage.navigateToLoanAccountDetailPage(loanId);

        DisburseLoanPage disburseLoanPage = loanAccountPage.navigateToDisburseLoan();
        disburseLoanPage.verifyPage();
        return disburseLoanPage;
    }

    public CreateLoanAccountEntryPage navigateToCreateLoanAccountEntryPageWithoutLogout(String clientName, String loanProductName) {
        return navigateToCreateLoanAccountEntryPageWithoutLogout(setLoanSearchParameters(clientName,loanProductName));
    }

    public CreateLoanAccountSearchParameters setLoanSearchParameters(String clientName, String loanProductName) {
        CreateLoanAccountSearchParameters accountSearchParameters = new CreateLoanAccountSearchParameters();
        accountSearchParameters.setSearchString(clientName);
        accountSearchParameters.setLoanProduct(loanProductName);
        return accountSearchParameters;
    }


    public AbstractPage setApplicationTime(DateTime systemDateTime) throws UnsupportedEncodingException {
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        dateTimeUpdaterRemoteTestingService.setDateTime(systemDateTime);
        return new AbstractPage(selenium);
    }

    public LoanAccountPage makePayment(DateTime paymentDate, String paymentAmount) throws UnsupportedEncodingException {
        PaymentParameters paymentParameters =setPaymentParams(paymentAmount, paymentDate);
        setApplicationTime(paymentDate).navigateBack();
        return new LoanAccountPage(selenium).navigateToApplyPayment().
                submitAndNavigateToApplyPaymentConfirmationPage(paymentParameters).
                submitAndNavigateToLoanAccountDetailsPage();
    }

    public PaymentParameters setPaymentParams(String amount, ReadableInstant paymentDate) {
        String dd = DateTimeFormat.forPattern("dd").print(paymentDate);
        String mm = DateTimeFormat.forPattern("MM").print(paymentDate);
        String yyyy = DateTimeFormat.forPattern("yyyy").print(paymentDate);

        PaymentParameters paymentParameters = new PaymentParameters();
        paymentParameters.setAmount(amount);
        paymentParameters.setTransactionDateDD(dd);
        paymentParameters.setTransactionDateMM(mm);
        paymentParameters.setTransactionDateYYYY(yyyy);
        paymentParameters.setPaymentType(PaymentParameters.CASH);
        return paymentParameters;
    }

    public DisburseLoanParameters setDisbursalParams(ReadableInstant validDisbursalDate) {
        DisburseLoanParameters disburseLoanParameters = new DisburseLoanParameters();
        String dd = DateTimeFormat.forPattern("dd").print(validDisbursalDate);
        String mm = DateTimeFormat.forPattern("MM").print(validDisbursalDate);
        String yyyy = DateTimeFormat.forPattern("yyyy").print(validDisbursalDate);

        disburseLoanParameters.setDisbursalDateDD(dd);
        disburseLoanParameters.setDisbursalDateMM(mm);
        disburseLoanParameters.setDisbursalDateYYYY(yyyy);
        disburseLoanParameters.setPaymentType(DisburseLoanParameters.CASH);
        return disburseLoanParameters;
    }

    public void disburseLoan(DateTime disbursalDate) throws UnsupportedEncodingException {
        setApplicationTime(disbursalDate).navigateBack();
        DisburseLoanParameters disburseLoanParameters = setDisbursalParams(disbursalDate);
        new LoanAccountPage(selenium).navigateToDisburseLoan().
        submitAndNavigateToDisburseLoanConfirmationPage(disburseLoanParameters).submitAndNavigateToLoanAccountPage();
    }

    public EditLoanAccountStatusParameters setApprovedStatusParameters() {
        EditLoanAccountStatusParameters loanAccountStatusParameters = new EditLoanAccountStatusParameters();
        loanAccountStatusParameters.setStatus(EditLoanAccountStatusParameters.APPROVED);
        loanAccountStatusParameters.setNote("test notes");
        return loanAccountStatusParameters;
    }

    public LoanAccountPage approveLoan(){
        return new LoanAccountPage(selenium).
                navigateToEditAccountStatus().
                submitAndNavigateToNextPage(setApprovedStatusParameters()).
                submitAndNavigateToLoanAccountPage();
    }

    public LoanAccountPage applyCharges(String feeName, String amount) {
        ChargeParameters chargeParameters = new ChargeParameters();
        chargeParameters.setType(feeName);
        chargeParameters.setAmount(amount);
        return new LoanAccountPage(selenium).navigateToApplyCharge().submitAndNavigateToApplyChargeConfirmationPage(chargeParameters);
    }
}
