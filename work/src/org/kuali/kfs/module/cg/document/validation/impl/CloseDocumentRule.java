/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.module.cg.rules;

import org.kuali.core.rules.TransactionalDocumentRuleBase;
import org.kuali.core.document.Document;
import org.kuali.core.util.GlobalVariables;
import org.kuali.module.cg.bo.Close;
import org.kuali.module.cg.document.CloseDocument;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.KeyConstants;

import java.sql.Date;

/**
 * User: Laran Evans <lc278@cornell.edu>
 * Date: Apr 17, 2007
 * Time: 5:35:09 PM
 */
public class CloseDocumentRule extends TransactionalDocumentRuleBase {

    @Override
    public boolean processSaveDocument(Document document) {
        boolean isOk = super.processSaveDocument(document);
        if(!isOk) {
            return isOk;
        }
        CloseDocument closeDocument = (CloseDocument) document;
        Date userDate = closeDocument.getUserInitiatedCloseDate();
        Date today = SpringServiceLocator.getDateTimeService().getCurrentSqlDateMidnight();
        isOk = null != userDate && today.getTime() <= userDate.getTime();
        if(!isOk) {
            GlobalVariables.getErrorMap().putError(
                    "userInitiatedCloseDate",
                    KeyConstants.ContractsAndGrants.USER_INITIATED_DATE_TOO_EARLY, userDate.toString());
        }
        return isOk;
    }
    
}
