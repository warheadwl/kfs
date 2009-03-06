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
package org.kuali.kfs.module.ld.batch;

import java.util.Date;

import org.kuali.kfs.gl.batch.service.BalancingService;
import org.kuali.kfs.sys.batch.AbstractStep;

/**
 * Generates a labor balancing report if data is present in the labor history tables. Instructions on how to test this process:
 * - Place an acceptable LD_SORTPOST.data into batchFileDirectoryName (see spring-ld.xml)<br>
 * - Run BatchStepRunner for laborPosterStep<br>
 * - Run BatchStepRunner for laborBalancingStep<br>
 * - Evaluate LaborBalancingServiceImpl.BALANCING_FILENAME in KFSConstants.REPORTS_DIRECTORY_KEY for results<br>
 */
public class LaborBalancingStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborBalancingStep.class);
    
    private BalancingService balancingService;

    /**
     * Perform this step of a batch job.
     * 
     * @param jobName the name of the job running the step
     * @param jobRunDate the time/date the job is executed
     * @return true if successful and continue the job, false if successful and stop the job
     * @throws Throwable if unsuccessful
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        return balancingService.runBalancing();
    }

    /**
     * Sets the BalancingService
     * 
     * @param balancingService The BalancingService to set.
     */
    public void setBalancingService(BalancingService balancingService) {
        this.balancingService = balancingService;
    }
}
