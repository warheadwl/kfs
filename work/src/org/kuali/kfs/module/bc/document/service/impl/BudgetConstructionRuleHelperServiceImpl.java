/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.bc.document.service.impl;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjCd;
import org.kuali.kfs.integration.businessobject.LaborLedgerObject;
import org.kuali.kfs.integration.service.LaborModuleService;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionRuleHelperService;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.MessageBuilder;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.spring.Logged;

/**
 * implementing the validation methods defined in BudgetConstructionRuleHelperService
 */
public class BudgetConstructionRuleHelperServiceImpl implements BudgetConstructionRuleHelperService {
    private static final Logger LOG = Logger.getLogger(BudgetConstructionRuleHelperServiceImpl.class);

    private DictionaryValidationService dictionaryValidationService;
    private LaborModuleService laborModuleService;
    private BudgetDocumentService budgetDocumentService;

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionRuleHelperService#hasDetailPositionRequiredObjectCode(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.core.util.ErrorMap)
     */
    @Logged
    public boolean hasDetailPositionRequiredObjectCode(PendingBudgetConstructionAppointmentFunding appointmentFunding, ErrorMap errorMap) {
        String objectCode = appointmentFunding.getFinancialObjectCode();
        ObjectCode financialObject = appointmentFunding.getFinancialObject();

        return this.isDetailPositionRequiredObjectCode(financialObject, objectCode, errorMap, KFSPropertyConstants.FINANCIAL_OBJECT);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionRuleHelperService#hasValidAccount(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.core.util.ErrorMap)
     */
    @Logged
    public boolean hasValidAccount(PendingBudgetConstructionAppointmentFunding appointmentFunding, ErrorMap errorMap) {
        String accountNumber = appointmentFunding.getAccountNumber();
        Account account = appointmentFunding.getAccount();
        return this.isValidAccount(account, accountNumber, errorMap, KFSPropertyConstants.ACCOUNT_NAME);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionRuleHelperService#hasValidChart(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.core.util.ErrorMap)
     */
    @Logged
    public boolean hasValidChart(PendingBudgetConstructionAppointmentFunding appointmentFunding, ErrorMap errorMap) {
        String chartOfAccountsCode = appointmentFunding.getChartOfAccountsCode();
        Chart chart = appointmentFunding.getChartOfAccounts();

        return this.isValidChart(chart, chartOfAccountsCode, errorMap, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionRuleHelperService#hasValidIncumbent(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.core.util.ErrorMap)
     */
    @Logged
    public boolean hasValidIncumbent(PendingBudgetConstructionAppointmentFunding appointmentFunding, ErrorMap errorMap) {
        String emplid = appointmentFunding.getEmplid();
        BudgetConstructionIntendedIncumbent intendedIncumbent = appointmentFunding.getBudgetConstructionIntendedIncumbent();

        return this.isValidIncumbent(intendedIncumbent, emplid, errorMap, KFSPropertyConstants.EMPLID);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionRuleHelperService#hasValidObjectCode(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.core.util.ErrorMap)
     */
    @Logged
    public boolean hasValidObjectCode(PendingBudgetConstructionAppointmentFunding appointmentFunding, ErrorMap errorMap) {
        String objectCode = appointmentFunding.getFinancialObjectCode();
        ObjectCode financialObject = appointmentFunding.getFinancialObject();

        return this.isValidObjectCode(financialObject, objectCode, errorMap, KFSPropertyConstants.FINANCIAL_OBJECT);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionRuleHelperService#hasValidPosition(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.core.util.ErrorMap)
     */
    @Logged
    public boolean hasValidPosition(PendingBudgetConstructionAppointmentFunding appointmentFunding, ErrorMap errorMap) {
        String positionNumber = appointmentFunding.getPositionNumber();
        BudgetConstructionPosition position = appointmentFunding.getBudgetConstructionPosition();

        return this.isValidPosition(position, positionNumber, errorMap, KFSPropertyConstants.POSITION_NUMBER);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionRuleHelperService#hasValidSubAccount(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.core.util.ErrorMap)
     */
    @Logged
    public boolean hasValidSubAccount(PendingBudgetConstructionAppointmentFunding appointmentFunding, ErrorMap errorMap) {
        String subAccountNumber = appointmentFunding.getSubAccountNumber();
        SubAccount subAccount = appointmentFunding.getSubAccount();

        return this.isValidSubAccount(subAccount, subAccountNumber, errorMap, KFSPropertyConstants.SUB_ACCOUNT_NAME);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionRuleHelperService#hasValidSubObjectCode(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.core.util.ErrorMap)
     */
    @Logged
    public boolean hasValidSubObjectCode(PendingBudgetConstructionAppointmentFunding appointmentFunding, ErrorMap errorMap) {
        String subObjectCode = appointmentFunding.getFinancialSubObjectCode();
        SubObjCd subObject = appointmentFunding.getFinancialSubObject();

        return this.isValidSubObjectCode(subObject, subObjectCode, errorMap, KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.impl.BudgetConstructionRuleHelperService#isAssociatedWithValidDocument(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.core.util.ErrorMap, java.lang.String)
     */
    @Logged
    public boolean isAssociatedWithValidDocument(PendingBudgetConstructionAppointmentFunding appointmentFunding, ErrorMap errorMap, String errorPropertyName) {
        BudgetConstructionDocument budgetConstructionDocument = budgetDocumentService.getBudgetConstructionDocument(appointmentFunding);

        if (ObjectUtils.isNull(budgetConstructionDocument)) {
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE);
            return false;
        }

        return this.isBudgetableDocument(budgetConstructionDocument, errorMap, errorPropertyName);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.impl.BudgetConstructionRuleHelperService#isBudgetableDocument(org.kuali.kfs.module.bc.document.BudgetConstructionDocument,
     *      org.kuali.core.util.ErrorMap, java.lang.String)
     */
    @Logged
    public boolean isBudgetableDocument(BudgetConstructionDocument budgetConstructionDocument, ErrorMap errorMap, String errorPropertyName) {
        boolean isBudgetAllowed = budgetDocumentService.isBudgetableDocument(budgetConstructionDocument);

        if (!isBudgetAllowed) {
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE);
        }

        return isBudgetAllowed;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionRuleHelperService#isDetailPositionRequiredObjectCode(org.kuali.kfs.coa.businessobject.ObjectCode,
     *      java.lang.String, org.kuali.core.util.ErrorMap, java.lang.String)
     */
    @Logged
    public boolean isDetailPositionRequiredObjectCode(ObjectCode financialObject, String currentValue, ErrorMap errorMap, String errorPropertyName) {
        if (ObjectUtils.isNull(financialObject)) {
            String errorMessage = MessageBuilder.buildErrorMessageWithDataDictionary(ObjectCode.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE, currentValue);
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, errorMessage);
            return false;
        }

        LaborLedgerObject laborObject = laborModuleService.retrieveLaborLedgerObject(financialObject);
        if (laborObject == null || laborObject.isDetailPositionRequiredIndicator()) {
            // TODO:
            String errorMessage = MessageBuilder.buildErrorMessageWithDataDictionary(ObjectCode.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE, currentValue);
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, errorMessage);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionRuleHelperService#isFieldFormatValid(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.core.util.ErrorMap)
     */
    @Logged
    public boolean isFieldFormatValid(PendingBudgetConstructionAppointmentFunding appointmentFunding, ErrorMap errorMap) {
        return dictionaryValidationService.isBusinessObjectValid(appointmentFunding);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.impl.BudgetConstructionRuleHelperService#isValidAccount(org.kuali.kfs.coa.businessobject.Account,
     *      java.lang.String, org.kuali.core.util.ErrorMap, java.lang.String)
     */
    @Logged
    public boolean isValidAccount(Account account, String currentValue, ErrorMap errorMap, String errorPropertyName) {
        if (ObjectUtils.isNull(account)) {
            String errorMessage = MessageBuilder.buildErrorMessageWithDataDictionary(Account.class, KFSPropertyConstants.ACCOUNT_NAME, currentValue);
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, errorMessage);
            return false;
        }

        if (account.isAccountClosedIndicator()) {
            String errorMessage = MessageBuilder.buildErrorMessageWithDataDictionary(Account.class, KFSPropertyConstants.ACCOUNT_NAME, currentValue);
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, errorMessage);
            return false;
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.impl.BudgetConstructionRuleHelperService#isValidChart(org.kuali.kfs.coa.businessobject.Chart,
     *      java.lang.String, org.kuali.core.util.ErrorMap, java.lang.String)
     */
    @Logged
    public boolean isValidChart(Chart chart, String currentValue, ErrorMap errorMap, String errorPropertyName) {
        if (ObjectUtils.isNull(chart)) {
            String errorMessage = MessageBuilder.buildErrorMessageWithDataDictionary(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, currentValue);
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, errorMessage);
            return false;
        }

        if (!chart.isActive()) {
            String errorMessage = MessageBuilder.buildErrorMessageWithDataDictionary(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, currentValue);
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, errorMessage);
            return false;
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.impl.BudgetConstructionRuleHelperService#isValidIncumbent(org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent,
     *      java.lang.String, org.kuali.core.util.ErrorMap, java.lang.String)
     */
    @Logged
    public boolean isValidIncumbent(BudgetConstructionIntendedIncumbent intendedIncumbent, String currentValue, ErrorMap errorMap, String errorPropertyName) {
        if (BCConstants.VACANT_EMPLID.equals(currentValue)) {
            return true;
        }

        if (ObjectUtils.isNull(intendedIncumbent)) {
            String errorMessage = MessageBuilder.buildErrorMessageWithDataDictionary(BudgetConstructionIntendedIncumbent.class, KFSPropertyConstants.EMPLID, currentValue);
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, errorMessage);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.impl.BudgetConstructionRuleHelperService#isValidObjectCode(org.kuali.kfs.coa.businessobject.ObjectCode,
     *      java.lang.String, org.kuali.core.util.ErrorMap, java.lang.String)
     */
    @Logged
    public boolean isValidObjectCode(ObjectCode objectCode, String currentValue, ErrorMap errorMap, String errorPropertyName) {
        if (ObjectUtils.isNull(objectCode)) {
            String errorMessage = MessageBuilder.buildErrorMessageWithDataDictionary(ObjectCode.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE, currentValue);
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, errorMessage);
            return false;
        }

        if (!objectCode.isFinancialObjectActiveCode()) {
            String errorMessage = MessageBuilder.buildErrorMessageWithDataDictionary(ObjectCode.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE, currentValue);
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, errorMessage);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.impl.BudgetConstructionRuleHelperService#isValidPosition(org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition,
     *      java.lang.String, org.kuali.core.util.ErrorMap, java.lang.String)
     */
    @Logged
    public boolean isValidPosition(BudgetConstructionPosition position, String currentValue, ErrorMap errorMap, String errorPropertyName) {
        if (ObjectUtils.isNull(position)) {
            String errorMessage = MessageBuilder.buildErrorMessageWithDataDictionary(BudgetConstructionPosition.class, KFSPropertyConstants.POSITION_NUMBER, currentValue);
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, errorMessage);
            return false;
        }

        if (!position.isEffective()) {
            String errorMessage = MessageBuilder.buildErrorMessageWithDataDictionary(BudgetConstructionPosition.class, KFSPropertyConstants.POSITION_NUMBER, currentValue);
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, errorMessage);
            return false;
        }

        if (!position.isBudgetedPosition()) {
            String errorMessage = MessageBuilder.buildErrorMessageWithDataDictionary(BudgetConstructionPosition.class, KFSPropertyConstants.POSITION_NUMBER, currentValue);
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, errorMessage);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.impl.BudgetConstructionRuleHelperService#isValidSubAccount(org.kuali.kfs.coa.businessobject.SubAccount,
     *      java.lang.String, org.kuali.core.util.ErrorMap, java.lang.String)
     */
    @Logged
    public boolean isValidSubAccount(SubAccount subAccount, String currentValue, ErrorMap errorMap, String errorPropertyName) {
        if (KFSConstants.getDashSubAccountNumber().equals(currentValue)) {
            return true;
        }

        if (ObjectUtils.isNull(subAccount)) {
            String errorMessage = MessageBuilder.buildErrorMessageWithDataDictionary(SubAccount.class, KFSPropertyConstants.SUB_ACCOUNT_NUMBER, currentValue);
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, errorMessage);
            return false;
        }

        if (!subAccount.isSubAccountActiveIndicator()) {
            String errorMessage = MessageBuilder.buildErrorMessageWithDataDictionary(SubAccount.class, KFSPropertyConstants.SUB_ACCOUNT_NUMBER, currentValue);
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_DOCUMENT_SUB_ACCOUNT_INACTIVE, errorMessage);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.impl.BudgetConstructionRuleHelperService#isValidSubObjectCode(org.kuali.kfs.coa.businessobject.SubObjCd,
     *      java.lang.String, org.kuali.core.util.ErrorMap, java.lang.String)
     */
    @Logged
    public boolean isValidSubObjectCode(SubObjCd subObjectCode, String currentValue, ErrorMap errorMap, String errorPropertyName) {
        if (KFSConstants.getDashFinancialSubObjectCode().equals(currentValue)) {
            return true;
        }

        if (ObjectUtils.isNull(subObjectCode)) {
            String errorMessage = MessageBuilder.buildErrorMessageWithDataDictionary(SubObjCd.class, KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, currentValue);
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, errorMessage);
            return false;
        }

        if (!subObjectCode.isFinancialSubObjectActiveIndicator()) {
            String errorMessage = MessageBuilder.buildErrorMessageWithDataDictionary(SubObjCd.class, KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, currentValue);
            errorMap.putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, errorMessage);
            return false;
        }
        return true;
    }

    /**
     * Sets the budgetDocumentService attribute value.
     * 
     * @param budgetDocumentService The budgetDocumentService to set.
     */
    public void setBudgetDocumentService(BudgetDocumentService budgetDocumentService) {
        this.budgetDocumentService = budgetDocumentService;
    }

    /**
     * Sets the dictionaryValidationService attribute value.
     * 
     * @param dictionaryValidationService The dictionaryValidationService to set.
     */
    public void setDictionaryValidationService(DictionaryValidationService dictionaryValidationService) {
        this.dictionaryValidationService = dictionaryValidationService;
    }

    /**
     * Sets the laborModuleService attribute value.
     * 
     * @param laborModuleService The laborModuleService to set.
     */
    public void setLaborModuleService(LaborModuleService laborModuleService) {
        this.laborModuleService = laborModuleService;
    }
}
