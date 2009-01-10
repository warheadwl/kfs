/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Created on Jul 5, 2004
 *
 */
package org.kuali.kfs.pdp.businessobject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.bo.Country;
import org.kuali.rice.kns.bo.PostalCode;
import org.kuali.rice.kns.bo.State;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.PostalCodeService;
import org.kuali.rice.kns.service.CountryService;
import org.kuali.rice.kns.service.StateService;
import org.kuali.rice.kns.bo.Campus;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.KualiInteger;

public class CustomerProfile extends PersistableBusinessObjectBase implements Inactivateable {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerProfile.class);

    private String achPaymentDescription; // ACH_PMT_DESC
    private String additionalCheckNoteTextLine1;
    private String additionalCheckNoteTextLine2;
    private String additionalCheckNoteTextLine3;
    private String additionalCheckNoteTextLine4;
    private String address1; // CUST_LN1_ADDR
    private String address2; // CUST_LN2_ADDR
    private String address3; // CUST_LN3_ADDR
    private String address4; // CUST_LN4_ADDR
    private boolean adviceCreate; // ADV_CRTE_IND
    private String adviceHeaderText; // ADV_HDR_TXT
    private String adviceSubjectLine;
    private String adviceReturnEmailAddr;
    private String chartCode; // FIN_COA_CD
    private String checkHeaderNoteTextLine1;
    private String checkHeaderNoteTextLine2;
    private String checkHeaderNoteTextLine3;
    private String checkHeaderNoteTextLine4;
    private String city; // CUST_CTY_NM
    private String contactFullName; // CUST_CNTC_FULL_NM
    private String countryCode; // CUST_CNTRY_NM
    private String customerDescription; // CUST_DESC
    private String defaultChartCode; // DFLT_COA_CD
    private String defaultAccountNumber; // DFLT_ACCT_NBR
    private String defaultSubAccountNumber; // DFLT_SUB_ACCT_NBR
    private String defaultObjectCode; // DFLT_OBJ_CD
    private String defaultPhysicalCampusProcessingCode; // DFLT_PHYS_CMP_PROC_CD
    private String defaultSubObjectCode; // DFLT_SUB_OBJ_CD
    private boolean employeeCheck; // CUST_EMP_CHK_IND
    private KualiDecimal fileThresholdAmount; // FL_THRSHLD_AMT
    private String fileThresholdEmailAddress; // CUST_FILE_THRSHLD_EMAIL_ADDR
    private KualiInteger id; // CUST_ID
    private boolean nraReview; // CUST_NRA_RVW_IND
    private String unitCode; // ORG_CD
    private boolean ownershipCodeRequired; // CUST_OWNR_CD_REQ_IND
    private boolean payeeIdRequired; // CUST_PAYEE_ID_REQ_IND
    private KualiDecimal paymentThresholdAmount; // PMT_THRSHLD_AMT
    private String paymentThresholdEmailAddress; // CUST_PMT_THRSHLD_EMAIL_ADDR
    private String processingEmailAddr; // CUST_PRCS_EMAIL_ADDR
    private String achTransactionType; 
    private String stateCode; // CUST_ST_CD
    private String subUnitCode; // SBUNT_CD
    private String zipCode; // CUST_ZIP_CD
    private boolean accountingEditRequired; // ACCTG_EDIT_REQ_IND
    private boolean relieveLiabilities;
    private boolean active; 
    private boolean selectedForFormat;

    private Chart chartOfAccounts;
    private Campus defaultProcessingCampus;
    private Chart defaultChart;
    private Account defaultAccount;
    private SubAccount defaultSubAccount;
    private ObjectCode defaultObject;
    private SubObjectCode defaultSubObject;
    private State state;
    private PostalCode postalCode;
    private Country country;
    private ACHTransactionType transactionType;

    private List<CustomerBank> customerBanks;


    public CustomerProfile() {
        super();
        customerBanks = new ArrayList<CustomerBank>();
    }

    public String getCustomerShortName() {
        return chartCode + "-" + unitCode + "-" + subUnitCode;
    }

    public void setCustomerShortName(String customerShortName) {
        
    }

    public String getSortName() {
        return (this.chartCode + this.unitCode + this.subUnitCode);
    }

    /**
     * @hibernate.property column="ACCTG_EDIT_REQ_IND" type="yes_no" not-null="true"
     * @return Returns the accountingEditRequird.
     */
    public boolean getAccountingEditRequired() {
        return accountingEditRequired;
    }

    /**
     * @param accountingEditRequird The accountingEditRequird to set.
     */
    public void setAccountingEditRequired(boolean accountingEditRequird) {
        this.accountingEditRequired = accountingEditRequird;
    }

    /**
     * @hibernate.property column="DFLT_SUB_ACCT_NBR" length="5" not-null="true"
     * @return Returns the defaultSubAccountNumber.
     */
    public String getDefaultSubAccountNumber() {
        return defaultSubAccountNumber;
    }

    /**
     * @param defaultSubAccountNumber The defaultSubAccountNumber to set.
     */
    public void setDefaultSubAccountNumber(String defaultSubAccountNumber) {
        this.defaultSubAccountNumber = defaultSubAccountNumber;
    }

    public List<CustomerBank> getCustomerBanks() {
        return customerBanks;
    }

    public CustomerBank getCustomerBankByDisbursementType(String dt) {
        for (CustomerBank element : customerBanks) {
            if (element.getDisbursementType().getCode().equals(dt)) {
                return element;
            }
        }

        return null;
    }

    public void setCustomerBanks(List<CustomerBank> cbs) {
        customerBanks = cbs;
    }

    public void addCustomerBank(CustomerBank cb) {
        customerBanks.add(cb);
    }

    public void deleteCustomerBank(CustomerBank cb) {
        customerBanks.remove(cb);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof CustomerProfile)) {
            return false;
        }
        CustomerProfile tc = (CustomerProfile) obj;
        return new EqualsBuilder().append(chartCode, tc.getChartCode()).append(unitCode, tc.getUnitCode()).append(subUnitCode, tc.getSubUnitCode()).isEquals();
    }

    /**
     * @hibernate.property column="ACH_PMT_DESC" length="100" not-null="false"
     * @return Returns the achPaymentDescription.
     */
    public String getAchPaymentDescription() {
        return achPaymentDescription;
    }

    /**
     * @hibernate.property column="ADDL_CHK_NTE_TXT_LN1" length="90" not-null="false"
     * @return Returns the additionalCheckNoteTextLine1.
     */
    public String getAdditionalCheckNoteTextLine1() {
        return additionalCheckNoteTextLine1;
    }

    /**
     * @hibernate.property column="ADDL_CHK_NTE_TXT_LN2" length="90" not-null="false"
     * @return Returns the additionalCheckNoteTextLine2.
     */
    public String getAdditionalCheckNoteTextLine2() {
        return additionalCheckNoteTextLine2;
    }

    /**
     * @hibernate.property column="ADDL_CHK_NTE_LN3_TXT" length="90" not-null="false"
     * @return Returns the additionalCheckNoteTextLine3.
     */
    public String getAdditionalCheckNoteTextLine3() {
        return additionalCheckNoteTextLine3;
    }

    /**
     * @hibernate.property column="ADDL_CHK_NTE_TXT_LN4" length="90" not-null="false"
     * @return Returns the additionalCheckNoteTextLine4.
     */
    public String getAdditionalCheckNoteTextLine4() {
        return additionalCheckNoteTextLine4;
    }

    /**
     * @hibernate.property column="CUST_LN1_ADDR" length="55" not-null="false"
     * @return Returns the address1.
     */
    public String getAddress1() {
        return address1;
    }

    /**
     * @hibernate.property column="CUST_LN2_ADDR" length="55" not-null="false"
     * @return Returns the address2.
     */
    public String getAddress2() {
        return address2;
    }

    /**
     * @hibernate.property column="CUST_LN3_ADDR" length="55" not-null="false"
     * @return Returns the address3.
     */
    public String getAddress3() {
        return address3;
    }

    /**
     * @hibernate.property column="CUST_LN4_ADDR" length="55" not-null="false"
     * @return Returns the address4.
     */
    public String getAddress4() {
        return address4;
    }

    /**
     * @hibernate.property column="ADV_HDR_TXT" length="200" not-null="false"
     * @return Returns the adviceHeaderText.
     */
    public String getAdviceHeaderText() {
        return adviceHeaderText;
    }

    /**
     * @hibernate.property column="FIN_COA_CD" length="2" not-null="true"
     * @return Returns the chartCode.
     */
    public String getChartCode() {
        return chartCode;
    }

    /**
     * @hibernate.property column="CHK_HDR_NTE_TXT_LN1" length="90" not-null="false"
     * @return Returns the checkHeaderNoteTextLine1.
     */
    public String getCheckHeaderNoteTextLine1() {
        return checkHeaderNoteTextLine1;
    }

    /**
     * @hibernate.property column="CHK_HDR_NTE_TXT_LN2" length="90" not-null="false"
     * @return Returns the checkHeaderNoteTextLine2.
     */
    public String getCheckHeaderNoteTextLine2() {
        return checkHeaderNoteTextLine2;
    }

    /**
     * @hibernate.property column="CHK_HDR_NTE_LN3_TXT" length="90" not-null="false"
     * @return Returns the checkHeaderNoteTextLine3.
     */
    public String getCheckHeaderNoteTextLine3() {
        return checkHeaderNoteTextLine3;
    }

    /**
     * @hibernate.property column="CHK_HDR_NTE_TXT_LN4" length="90" not-null="false"
     * @return Returns the checkHeaderNoteTextLine4.
     */
    public String getCheckHeaderNoteTextLine4() {
        return checkHeaderNoteTextLine4;
    }

    /**
     * @hibernate.property column="CUST_CTY_NM" length="30" not-null="false"
     * @return Returns the city.
     */
    public String getCity() {
        return city;
    }

    /**
     * @hibernate.property column="CUST_CNTC_FULL_NM" length="50" not-null="false"
     * @return Returns the contactFullName.
     */
    public String getContactFullName() {
        return contactFullName;
    }


    /**
     * @hibernate.property column="CUST_DESC" length="50" not-null="false"
     * @return Returns the customerDescription.
     */
    public String getCustomerDescription() {
        return customerDescription;
    }

    /**
     * @hibernate.property column="DFLT_ACCT_NBR" length="7" not-null="true"
     * @return Returns the defaultAccountNumber.
     */
    public String getDefaultAccountNumber() {
        return defaultAccountNumber;
    }

    /**
     * @hibernate.property column="DFLT_COA_CD" length="2" not-null="true"
     * @return Returns the defaultChartCode.
     */
    public String getDefaultChartCode() {
        return defaultChartCode;
    }

    /**
     * @hibernate.property column="DFLT_OBJ_CD" length="4" not-null="true"
     * @return Returns the defaultObjectCode.
     */
    public String getDefaultObjectCode() {
        return defaultObjectCode;
    }

    /**
     * @hibernate.property column="DFLT_PHYS_CMP_PROC_CD" length="2" not-null="false"
     * @return Returns the defaultPhysicalCampusProcessingCode.
     */
    public String getDefaultPhysicalCampusProcessingCode() {
        return defaultPhysicalCampusProcessingCode;
    }

    /**
     * @hibernate.property column="DFLT_SUB_OBJ_CD" length="3" not-null="true"
     * @return Returns the defaultSubObjectCode.
     */
    public String getDefaultSubObjectCode() {
        return defaultSubObjectCode;
    }

    /**
     * @hibernate.property column="FL_THRSHLD_AMT" not-null="false"
     * @return Returns the fileThresholdAmount.
     */
    public KualiDecimal getFileThresholdAmount() {
        return fileThresholdAmount;
    }

    /**
     * @hibernate.property column="CUST_FILE_THRSHLD_EMAIL_ADDR" length="50" not-null="false"
     * @return Returns the fileThresholdEmailAddress.
     */
    public String getFileThresholdEmailAddress() {
        return fileThresholdEmailAddress;
    }

    /**
     * @return Returns the id.
     * @hibernate.id column="CUST_ID" generator-class="sequence"
     * @hibernate.generator-param name="sequence" value="PDP.PDP_CUST_ID_SEQ"
     */
    public KualiInteger getId() {
        return id;
    }

    /**
     * @hibernate.property column="ORG_CD" length="4" not-null="true"
     * @return Returns the unitCode.
     */
    public String getUnitCode() {
        return unitCode;
    }

    /**
     * @hibernate.property column="PMT_THRSHLD_AMT" not-null="false"
     * @return Returns the paymentThresholdAmount.
     */
    public KualiDecimal getPaymentThresholdAmount() {
        return paymentThresholdAmount;
    }

    /**
     * @hibernate.property column="CUST_PMT_THRSHLD_EMAIL_ADDR" length="50" not-null="false"
     * @return Returns the paymentThresholdEmailAddress.
     */
    public String getPaymentThresholdEmailAddress() {
        return paymentThresholdEmailAddress;
    }

    /**
     * @hibernate.property column="CUST_PRCS_EMAIL_ADDR" length="50" not-null="false"
     * @return Returns the processingEmailAddr.
     */
    public String getProcessingEmailAddr() {
        return processingEmailAddr;
    }

    public String getFirstFiftyProcessingEmailAddr() {
        if ((processingEmailAddr != null) && (processingEmailAddr.length() > 50)) {
            return processingEmailAddr.substring(0, 50);
        }
        return processingEmailAddr;
    }

    /**
     * @hibernate.property column="CUST_ST_CD" length="30" not-null="false"
     * @return Returns the state.
     */
    public String getStateCode() {
        return stateCode;
    }

    /**
     * @hibernate.property column="SBUNT_CD" length="4" not-null="true"
     * @return Returns the subUnitCode.
     */
    public String getSubUnitCode() {
        return subUnitCode;
    }

    /**
     * @hibernate.property column="CUST_ZIP_CD" length="20" not-null="false"
     * @return Returns the zipCode.
     */
    public String getZipCode() {
        return zipCode;
    }

    public int hashCode() {
        return new HashCodeBuilder(59, 67).append(chartCode).append(unitCode).append(subUnitCode).toHashCode();
    }

    /**
     * @hibernate.property column="ADV_CRTE_IND" type="yes_no" not-null="false"
     * @return Returns the adviceCreate.
     */
    public boolean getAdviceCreate() {
        return adviceCreate;
    }

    /**
     * @hibernate.property column="ADV_SUBJ_LN_TXT" length="40" not-null="false"
     * @return Returns the adviceSubjectLine.
     */
    public String getAdviceSubjectLine() {
        return adviceSubjectLine;
    }

    /**
     * @hibernate.property column="ADV_RTRN_EMAIL_ADDR" length="50" not-null="false"
     * @return Returns the adviceReturnEmailAddr.
     */
    public String getAdviceReturnEmailAddr() {
        return adviceReturnEmailAddr;
    }

    /**
     * @hibernate.property column="CUST_EMP_CHK_IND" type="yes_no" not-null="false"
     * @return Returns the employeeCheck.
     */
    public boolean getEmployeeCheck() {
        return employeeCheck;
    }

    /**
     * @hibernate.property column="CUST_NRA_RVW_IND" type="yes_no" not-null="false"
     * @return Returns the nraReview.
     */
    public boolean getNraReview() {
        return nraReview;
    }

    /**
     * @hibernate.property column="CUST_OWNR_CD_REQ_IND" type="yes_no" not-null="false"
     * @return Returns the ownershipCodeRequired.
     */
    public boolean getOwnershipCodeRequired() {
        return ownershipCodeRequired;
    }

    /**
     * @hibernate.property column="CUST_PAYEE_ID_REQ_IND" type="yes_no" not-null="false"
     * @return Returns the payeeIdRequired.
     */
    public boolean getPayeeIdRequired() {
        return payeeIdRequired;
    }

    /**
     * @param achPaymentDescription The achPaymentDescription to set.
     */
    public void setAchPaymentDescription(String achPaymentDescription) {
        this.achPaymentDescription = achPaymentDescription;
    }

    /**
     * @param additionalCheckNoteTextLine1 The additionalCheckNoteTextLine1 to set.
     */
    public void setAdditionalCheckNoteTextLine1(String additionalCheckNoteTextLine1) {
        this.additionalCheckNoteTextLine1 = additionalCheckNoteTextLine1;
    }

    /**
     * @param additionalCheckNoteTextLine2 The additionalCheckNoteTextLine2 to set.
     */
    public void setAdditionalCheckNoteTextLine2(String additionalCheckNoteTextLine2) {
        this.additionalCheckNoteTextLine2 = additionalCheckNoteTextLine2;
    }

    /**
     * @param additionalCheckNoteTextLine3 The additionalCheckNoteTexLine3 to set.
     */
    public void setAdditionalCheckNoteTextLine3(String additionalCheckNoteTextLine3) {
        this.additionalCheckNoteTextLine3 = additionalCheckNoteTextLine3;
    }

    /**
     * @param additionalCheckNoteTextLine4 The additionalCheckNoteTextLine4 to set.
     */
    public void setAdditionalCheckNoteTextLine4(String additionalCheckNoteTextLine4) {
        this.additionalCheckNoteTextLine4 = additionalCheckNoteTextLine4;
    }

    /**
     * @param address1 The address1 to set.
     */
    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    /**
     * @param address2 The address2 to set.
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /**
     * @param address3 The address3 to set.
     */
    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    /**
     * @param address4 The address4 to set.
     */
    public void setAddress4(String address4) {
        this.address4 = address4;
    }

    /**
     * @param adviceCreate The adviceCreate to set.
     */
    public void setAdviceCreate(boolean adviceCreate) {
        this.adviceCreate = adviceCreate;
    }

    /**
     * @param adviceHeaderText The adviceHeaderText to set.
     */
    public void setAdviceHeaderText(String adviceHeaderText) {
        this.adviceHeaderText = adviceHeaderText;
    }

    /**
     * @param adviceSubjectLine The adviceSubjectLine to set.
     */
    public void setAdviceSubjectLine(String adviceSubjectLine) {
        this.adviceSubjectLine = adviceSubjectLine;
    }

    /**
     * @param adviceReturnEmailAddr The adviceReturnEmailAddr to set.
     */
    public void setAdviceReturnEmailAddr(String adviceReturnEmailAddr) {
        this.adviceReturnEmailAddr = adviceReturnEmailAddr;
    }

    /**
     * @param chartCode The chartCode to set.
     */
    public void setChartCode(String chartCode) {
        this.chartCode = chartCode;
    }

    /**
     * @param checkHeaderNoteTextLine1 The checkHeaderNoteTextLine1 to set.
     */
    public void setCheckHeaderNoteTextLine1(String checkHeaderNoteTextLine1) {
        this.checkHeaderNoteTextLine1 = checkHeaderNoteTextLine1;
    }

    /**
     * @param checkHeaderNoteTextLine2 The checkHeaderNoteTextLine2 to set.
     */
    public void setCheckHeaderNoteTextLine2(String checkHeaderNoteTextLine2) {
        this.checkHeaderNoteTextLine2 = checkHeaderNoteTextLine2;
    }

    /**
     * @param checkHeaderNoteTextLine3 The checkHeaderNoteTextLine3 to set.
     */
    public void setCheckHeaderNoteTextLine3(String checkHeaderNoteTextLine3) {
        this.checkHeaderNoteTextLine3 = checkHeaderNoteTextLine3;
    }

    /**
     * @param checkHeaderNoteTextLine4 The checkHeaderNoteTextLine4 to set.
     */
    public void setCheckHeaderNoteTextLine4(String checkHeaderNoteTextLine4) {
        this.checkHeaderNoteTextLine4 = checkHeaderNoteTextLine4;
    }

    /**
     * @param city The city to set.
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @param contactFullName The contactFullName to set.
     */
    public void setContactFullName(String contactFullName) {
        this.contactFullName = contactFullName;
    }

    /**
     * @param customerDescription The customerDescription to set.
     */
    public void setCustomerDescription(String customerDescription) {
        this.customerDescription = customerDescription;
    }

    /**
     * @param defaultAccountNumber The defaultAccountNumber to set.
     */
    public void setDefaultAccountNumber(String defaultAccountNumber) {
        this.defaultAccountNumber = defaultAccountNumber;
    }

    /**
     * @param defaultChartCode The defaultChartCode to set.
     */
    public void setDefaultChartCode(String defaultChartCode) {
        this.defaultChartCode = defaultChartCode;
    }

    /**
     * @param defaultObjectCode The defaultObjectCode to set.
     */
    public void setDefaultObjectCode(String defaultObjectCode) {
        this.defaultObjectCode = defaultObjectCode;
    }

    /**
     * @param defaultPhysicalCampusProcessingCode The defaultPhysicalCampusProcessingCode to set.
     */
    public void setDefaultPhysicalCampusProcessingCode(String defaultPhysicalCampusProcessingCode) {
        this.defaultPhysicalCampusProcessingCode = defaultPhysicalCampusProcessingCode;
    }

    /**
     * @param defaultSubObjectCode The defaultSubObjectCode to set.
     */
    public void setDefaultSubObjectCode(String defaultSubObjectCode) {
        this.defaultSubObjectCode = defaultSubObjectCode;
    }

    /**
     * @param employeeCheck The employeeCheck to set.
     */
    public void setEmployeeCheck(boolean employeeCheck) {
        this.employeeCheck = employeeCheck;
    }

    /**
     * @param fileThresholdAmount The fileThresholdAmount to set.
     */
    public void setFileThresholdAmount(KualiDecimal fileThresholdAmount) {
        this.fileThresholdAmount = fileThresholdAmount;
    }

    /**
     * @param fileThresholdEmailAddress The fileThresholdEmailAddress to set.
     */
    public void setFileThresholdEmailAddress(String fileThresholdEmailAddress) {
        this.fileThresholdEmailAddress = fileThresholdEmailAddress;
    }

    /**
     * @param id The id to set.
     */
    public void setId(KualiInteger id) {
        this.id = id;
    }

    /**
     * @param nraReview The nraReview to set.
     */
    public void setNraReview(boolean nraReview) {
        this.nraReview = nraReview;
    }

    /**
     * @param unitCode The unitCode to set.
     */
    public void setUnitCode(String orgCode) {
        this.unitCode = orgCode;
    }

    /**
     * @param ownershipCodeRequired The ownershipCodeRequired to set.
     */
    public void setOwnershipCodeRequired(boolean ownershipCodeRequired) {
        this.ownershipCodeRequired = ownershipCodeRequired;
    }

    /**
     * @param payeeIdRequired The payeeIdRequired to set.
     */
    public void setPayeeIdRequired(boolean payeeIdRequired) {
        this.payeeIdRequired = payeeIdRequired;
    }

    /**
     * @param paymentThresholdAmount The paymentThresholdAmount to set.
     */
    public void setPaymentThresholdAmount(KualiDecimal paymentThresholdAmount) {
        this.paymentThresholdAmount = paymentThresholdAmount;
    }

    /**
     * @param paymentThresholdEmailAddress The paymentThresholdEmailAddress to set.
     */
    public void setPaymentThresholdEmailAddress(String paymentThresholdEmailAddress) {
        this.paymentThresholdEmailAddress = paymentThresholdEmailAddress;
    }

    /**
     * @param processingEmailAddr The processingEmailAddr to set.
     */
    public void setProcessingEmailAddr(String processingEmailAddr) {
        this.processingEmailAddr = processingEmailAddr;
    }

    /**
     * @param state The state to set.
     */
    public void setStateCode(String state) {
        this.stateCode = state;
    }

    /**
     * @param subUnitCode The subUnitCode to set.
     */
    public void setSubUnitCode(String subUnitCode) {
        this.subUnitCode = subUnitCode;
    }

    /**
     * @param zipCode The zipCode to set.
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * @return Returns the relieveLiabilities.
     */
    public boolean getRelieveLiabilities() {
        return relieveLiabilities;
    }

    /**
     * @param relieveLiabilities The relieveLiabilities to set.
     */
    public void setRelieveLiabilities(boolean relieveLiabilities) {
        this.relieveLiabilities = relieveLiabilities;
    }

    public String toString() {
        return new ToStringBuilder(this).append("chartCode", this.chartCode).append("unitCode", this.unitCode).append("subUnitCode", this.subUnitCode).toString();
    }

    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }


    /**
     * Gets the defaultProcessingCampus attribute.
     * 
     * @return Returns the defaultProcessingCampus.
     */
    public Campus getDefaultProcessingCampus() {
        return defaultProcessingCampus;
    }

    /**
     * Sets the defaultProcessingCampus attribute value.
     * 
     * @param defaultProcessingCampus The defaultProcessingCampus to set.
     */
    public void setDefaultProcessingCampus(Campus defaultProcessingCampus) {
        this.defaultProcessingCampus = defaultProcessingCampus;
    }

    /**
     * Gets the defaultChart attribute.
     * 
     * @return Returns the defaultChart.
     */
    public Chart getDefaultChart() {
        return defaultChart;
    }

    /**
     * Sets the defaultChart attribute value.
     * 
     * @param defaultChart The defaultChart to set.
     */
    public void setDefaultChart(Chart defaultChart) {
        this.defaultChart = defaultChart;
    }

    /**
     * Gets the defaultAccount attribute.
     * 
     * @return Returns the defaultAccount.
     */
    public Account getDefaultAccount() {
        return defaultAccount;
    }

    /**
     * Sets the defaultAccount attribute value.
     * 
     * @param defaultAccount The defaultAccount to set.
     */
    public void setDefaultAccount(Account defaultAccount) {
        this.defaultAccount = defaultAccount;
    }

    /**
     * Gets the defaultSubAccount attribute.
     * 
     * @return Returns the defaultSubAccount.
     */
    public SubAccount getDefaultSubAccount() {
        return defaultSubAccount;
    }

    /**
     * Sets the defaultSubAccount attribute value.
     * 
     * @param defaultSubAccount The defaultSubAccount to set.
     */
    public void setDefaultSubAccount(SubAccount defaultSubAccount) {
        this.defaultSubAccount = defaultSubAccount;
    }

    /**
     * Gets the defaultObject attribute.
     * 
     * @return Returns the defaultObject.
     */
    public ObjectCode getDefaultObject() {
        return defaultObject;
    }

    /**
     * Sets the defaultObject attribute value.
     * 
     * @param defaultObject The defaultObject to set.
     */
    public void setDefaultObject(ObjectCode defaultObject) {
        this.defaultObject = defaultObject;
    }

    /**
     * Gets the defaultSubObject attribute.
     * 
     * @return Returns the defaultSubObject.
     */
    public SubObjectCode getDefaultSubObject() {
        return defaultSubObject;
    }

    /**
     * Sets the defaultSubObject attribute value.
     * 
     * @param defaultSubObject The defaultSubObject to set.
     */
    public void setDefaultSubObject(SubObjectCode defaultSubObject) {
        this.defaultSubObject = defaultSubObject;
    }

    /**
     * Gets the state attribute.
     * 
     * @return Returns the state.
     */
    public State getState() {
        state = SpringContext.getBean(StateService.class).getByPrimaryIdIfNecessary(this, this.stateCode, this.state);
        return state;
    }

    /**
     * Sets the state attribute value.
     * 
     * @param state The state to set.
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * Gets the postalCode attribute.
     * 
     * @return Returns the postalCode.
     */
    public PostalCode getPostalCode() {
        postalCode = SpringContext.getBean(PostalCodeService.class).getByPrimaryIdIfNecessary(this, this.zipCode, this.postalCode);
        return postalCode;
    }

    /**
     * Sets the postalCode attribute value.
     * 
     * @param postalCode The postalCode to set.
     */
    public void setPostalCode(PostalCode postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Gets the country attribute.
     * 
     * @return Returns the country.
     */
    public Country getCountry() {
        country = SpringContext.getBean(CountryService.class).getByPrimaryIdIfNecessary(this, this.countryCode, this.country);
        return country;
    }

    /**
     * Sets the country attribute value.
     * 
     * @param country The country to set.
     */
    public void setCountry(Country country) {
        this.country = country;
    }

    /**
     * @see org.kuali.rice.kns.bo.Inactivateable#isActive()
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @see org.kuali.rice.kns.bo.Inactivateable#setActive(boolean)
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the countryCode attribute. 
     * @return Returns the countryCode.
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Sets the countryCode attribute value.
     * @param countryCode The countryCode to set.
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * Gets the achTransactionType attribute.
     * 
     * @return Returns the achTransactionType.
     */
    public String getAchTransactionType() {
        return achTransactionType;
    }

    /**
     * Sets the achTransactionType attribute value.
     * 
     * @param achTransactionType The achTransactionType to set.
     */
    public void setAchTransactionType(String achTransactionType) {
        this.achTransactionType = achTransactionType;
    }

    /**
     * Gets the transactionType attribute.
     * 
     * @return Returns the transactionType.
     */
    public ACHTransactionType getTransactionType() {
        return transactionType;
    }

    /**
     * Sets the transactionType attribute value.
     * 
     * @param transactionType The transactionType to set.
     */
    public void setTransactionType(ACHTransactionType transactionType) {
        this.transactionType = transactionType;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.ID, this.id);
        
        return m;
    }

    /**
     * This method gets the selected for format flag.
     * @return selectedForFormat
     */
    public boolean isSelectedForFormat() {
        return selectedForFormat;
}
    /**
     * This method sets the selectedForFormat value.
     * @param sameCampus
     */
    public void setSelectedForFormat(boolean sameCampus) {
        this.selectedForFormat = sameCampus;
    }
    
}
