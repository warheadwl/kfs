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
package org.kuali.module.cams.service.impl;

import static org.kuali.module.cams.CamsConstants.DOC_APPROVED;
import static org.kuali.module.cams.CamsConstants.EquipmentLoanOrReturn.DOCUMENT_HEADER;

import java.util.List;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetHeader;
import org.kuali.module.cams.bo.AssetRetirementDocument;
import org.kuali.module.cams.bo.EquipmentLoanOrReturn;
import org.kuali.module.cams.service.RetirementInfoService;

public class RetirementInfoServiceImpl implements RetirementInfoService {

    public void setRetirementInfo(Asset asset) {
        List<AssetHeader> assetHeaders = asset.getAssetHeaders();
        for (AssetHeader assetHeader : assetHeaders) {
            AssetRetirementDocument retirementDocument = assetHeader.getRetirementDocument();
            if (retirementDocument != null && isDocumentApproved(retirementDocument)) {
                asset.setRetirementInfo(retirementDocument);
                return;
            }
        }
    }

    private boolean isDocumentApproved(AssetRetirementDocument assetRetirementDocument) {
        assetRetirementDocument.refreshReferenceObject(DOCUMENT_HEADER);
        DocumentHeader documentHeader = assetRetirementDocument.getDocumentHeader();
        if (documentHeader != null && DOC_APPROVED.equals(documentHeader.getFinancialDocumentStatusCode())) {
            return true;
        }
        return false;
    }

}
