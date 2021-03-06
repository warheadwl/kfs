/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.purap.document.workflow;

import org.kuali.rice.kew.actionlist.CustomActionListAttribute;
import org.kuali.rice.kew.api.action.ActionSet;
import org.kuali.rice.kew.api.actionlist.DisplayParameters;

public class PurchaseOrderActionListAttribute implements CustomActionListAttribute {

    public ActionSet getLegalActions(String principalId, org.kuali.rice.kew.api.action.ActionItem actionItem) throws Exception {
        return ActionSet.Builder.create().build();
    }

    public DisplayParameters getDocHandlerDisplayParameters(String principalId, org.kuali.rice.kew.api.action.ActionItem actionItem) throws Exception {
        return null;
    }
    
    
}
