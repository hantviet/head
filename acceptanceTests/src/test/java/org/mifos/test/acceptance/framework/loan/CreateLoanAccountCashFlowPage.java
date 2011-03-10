package org.mifos.test.acceptance.framework.loan;

import com.thoughtworks.selenium.Selenium;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.mifos.test.acceptance.framework.AbstractPage;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.testng.Assert;

import java.util.Locale;

public class CreateLoanAccountCashFlowPage extends AbstractPage{
    String totalCapital = "totalCapital";
    String totalLiability = "totalLiability";

    private final static int FIELD_MAX_DIGITS = 10;
    private final static int FIELD_MAX_DECIMAL_PLACES = 3;

    public CreateLoanAccountCashFlowPage(Selenium selenium) {
        super(selenium);
        this.verifyPage("captureCashFlow");
    }

    public CreateLoanAccountCashFlowPage verifyPage() {
        this.verifyPage("captureCashFlow");
        return this;
    }

    public CreateLoanAccountCashFlowPage validateCashFlowMonths(DateTime disbursalDate, int installment, int frequencyType) {
        DateTime firstInstallmentDate;
        DateTime lastInstallmentDate;
        if (frequencyType == DefineNewLoanProductPage.SubmitFormParameters.WEEKS) {
            firstInstallmentDate = disbursalDate.plusWeeks(1);
            lastInstallmentDate = disbursalDate.plusWeeks(installment);
        } else {
            firstInstallmentDate = disbursalDate.plusMonths(1);
            lastInstallmentDate = disbursalDate.plusMonths(installment);
        }

        DateTime firstCashFlowDate = firstInstallmentDate.minusMonths(1);
        DateTime lastCashFlowDate = lastInstallmentDate.plusMonths(1);

        DateTime currentIteratorDate = firstCashFlowDate;
        int rowCount = 1;
        while (!currentIteratorDate.isAfter(lastCashFlowDate)) {
            Assert.assertEquals(selenium.getText("//tr[" + rowCount + "]/td[1]"), DateTimeFormat.forPattern("MMMMMMMM yyyy").withLocale(Locale.ENGLISH).print(currentIteratorDate));
            currentIteratorDate = currentIteratorDate.plusMonths(1);
            rowCount++;
        }
        //ToDo last month is not accounted
        return this;
    }

    public void verifyTableExist() {
        Assert.assertEquals(selenium.getText("//thead/tr[1]/th[1]"), "Month");
        Assert.assertEquals(selenium.getText("//thead/tr[1]/th[2]"), "*Expense");
        Assert.assertEquals(selenium.getText("//thead/tr[1]/th[3]"), "*Revenue");
        Assert.assertEquals(selenium.getText("//thead/tr[1]/th[4]"), "Notes");
    }

    public void verifyInvalidTextTyped() {
        String keys = "qwertyuiop[]asdfghjkl;'zxcvbnm ?><{}|\\_+-=!@#$%^&*()";
        for(int i = 0; i < keys.length(); i++) {
            selenium.keyPress("monthlyCashFlows[0].expense", keys.substring(i, i+1));
            selenium.keyPress("monthlyCashFlows[0].revenue", keys.substring(i, i+1));
        }
        Assert.assertEquals(selenium.getText("monthlyCashFlows[0].expense"), "");
        Assert.assertEquals(selenium.getText("monthlyCashFlows[0].revenue"), "");
        selenium.typeKeys("monthlyCashFlows[0].expense", "");
        selenium.typeKeys("monthlyCashFlows[0].revenue", "");
    }

    public void verifyErrorsOnPage() {
        Assert.assertTrue(isTextPresentInPage("Please specify expense for "));
        Assert.assertTrue(isTextPresentInPage("Please specify revenue for "));
    }

    private String getNumbers(int length) {
        StringBuffer ret = new StringBuffer("");
        for(int i = 0; i < length; i++) {
            ret.append('1');
        }
        return ret.toString();
    }

    public void verifyErrorsOnFields() {
        String error = "Invalid amount. Maximum of "+FIELD_MAX_DIGITS+" digits and "+FIELD_MAX_DECIMAL_PLACES+" decimal places are supported.";
        String lengthMaxDigits = getNumbers(FIELD_MAX_DIGITS);
        String lengthMaxDecimal = "0."+getNumbers(FIELD_MAX_DECIMAL_PLACES);
        String lengthWrongDigits = getNumbers(FIELD_MAX_DIGITS+1);
        String lengthWrongDecimal = "0."+getNumbers(FIELD_MAX_DECIMAL_PLACES+1);

        selenium.typeKeysKeys("monthlyCashFlows[0].expense", lengthMaxDigits);
        selenium.fireEvent("monthlyCashFlows[0].expense", "blur");
        selenium.fireEvent("monthlyCashFlows[1].expense", "blur");
        Assert.assertFalse(isTextPresentInPage(error));
        selenium.typeKeys("monthlyCashFlows[0].expense", "");

        selenium.typeKeys("monthlyCashFlows[0].expense", lengthMaxDecimal);
        selenium.fireEvent("monthlyCashFlows[0].expense", "blur");
        selenium.fireEvent("monthlyCashFlows[1].expense", "blur");
        Assert.assertFalse(isTextPresentInPage(error));
        selenium.typeKeys("monthlyCashFlows[0].expense", "");

        selenium.typeKeys("monthlyCashFlows[0].expense", lengthWrongDecimal);
        selenium.fireEvent("monthlyCashFlows[0].expense", "blur");
        selenium.fireEvent("monthlyCashFlows[1].expense", "blur");
        Assert.assertTrue(isTextPresentInPage(error));
        selenium.typeKeys("monthlyCashFlows[0].expense", "");

        selenium.typeKeysKeys("monthlyCashFlows[0].expense", lengthWrongDigits);
        selenium.fireEvent("monthlyCashFlows[0].expense", "blur");
        selenium.fireEvent("monthlyCashFlows[1].expense", "blur");
        Assert.assertTrue(isTextPresentInPage(error));
        selenium.typeKeys("monthlyCashFlows[0].expense", "");
        selenium.fireEvent("monthlyCashFlows[0].expense", "focus");
    }

    public CreateLoanAccountCashFlowPage enterValidData(String expense, double incremental, int cashFlowBase, String totalCapital, String totalLiability) {
        int noOfMonths = selenium.getXpathCount("//input[contains(@id,'expense')]").intValue();
        for (int rowIndex = 1; rowIndex <= noOfMonths ; rowIndex++) {
            selenium.typeKeys("//tr[" + rowIndex + "]/td[2]/input", expense);
            selenium.typeKeys("//tr[" + rowIndex + "]/td[3]/input", String.valueOf(cashFlowBase + incremental));
            selenium.typeKeys("//tr[" + rowIndex + "]/td[4]/input","notes" + rowIndex);
        }
        if (totalCapital != null) {
            selenium.typeKeys(this.totalCapital, totalCapital);
        }
        if (totalLiability != null) {
            selenium.typeKeys(this.totalLiability, totalLiability);
        }
        return this;
    }

    private void submit() {
        selenium.click("_eventId_capture");
        waitForPageToLoad();
    }

    public CreateLoanAccountReviewInstallmentPage clickContinue() {
        submit();
        return new CreateLoanAccountReviewInstallmentPage(selenium);
    }

    public CreateLoanAccountCashFlowPage submitWithErrors() {
        submit();
        return this;
    }

    public CreateLoanAccountCashFlowPage verifyCashFlowFields() {
        Assert.assertTrue(selenium.getValue(totalCapital).equals(""));
        Assert.assertTrue(selenium.getValue(totalLiability).equals(""));
        clickContinue();
        Assert.assertTrue(isTextPresentInPage("Please specify the total capital"));
        Assert.assertTrue(isTextPresentInPage("Please specify the total liability"));
//        selenium.typeKeys(totalCapital,"abc");
//        selenium.typeKeys(totalLiability,"abc");
//        Assert.assertTrue(selenium.getValue(totalCapital).equals(""));
//        Assert.assertTrue(selenium.getValue(totalLiability).equals(""));
        return this;
    }

    public CreateLoanAccountCashFlowPage verifyInvalidIndebentRate(String maxValue, String capital, String liability) {
        selenium.typeKeys(totalCapital, capital);
        selenium.typeKeys(totalLiability, liability);
        clickContinue();
        Assert.assertTrue(isTextPresentInPage("Indebtedness rate of the client is 49.99 % which should be lesser than the allowable value of " + maxValue + " %"));
        return this;
    }

    public ClientsAndAccountsHomepage cancel() {
        selenium.click("_eventId_cancel");
        waitForPageToLoad();
        return new ClientsAndAccountsHomepage(selenium);
    }
}
