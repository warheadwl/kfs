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
package org.kuali.kfs.sys.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.batch.service.WrappingBatchService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.report.BusinessObjectReportHelper;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Text output implementation of <code>ReportWriterService</code> interface. If you are a developer attempting to add a new business object for
 * error report writing, take a look at the Spring definitions for BusinessObjectReportHelper.<br>
 * This class CANNOT be used by 2 processes simultaneously. It is for very specific batch processes that should not run at the same
 * time, and initialize and destroy must be called and the beginning and end of each process that uses it.
 * @see org.kuali.kfs.sys.report.BusinessObjectReportHelper
 */
public class ReportWriterTextServiceImpl implements ReportWriterService, WrappingBatchService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReportWriterTextServiceImpl.class);
    
    // Changing the initial line number would only affect that a page break occurs early. It does not actually print in the
    // middle of the page. Hence changing this has little use.
    protected static final int INITIAL_LINE_NUMBER = 0;
    
    protected String filePath;
    protected String fileNamePrefix;
    protected String fileNameSuffix;
    protected String title;
    protected int pageWidth;
    protected int pageLength;
    protected int initialPageNumber;
    protected String statisticsLabel;
    protected String statisticsLeftPadding;
    protected String pageLabel;
    protected DateTimeService dateTimeService;

    // Local caching field to speed up the selection of formatting BusinessObjectReportHelper to use per configuration in Spring
    protected Map<Class<? extends BusinessObject>, BusinessObjectReportHelper> businessObjectReportHelpers;
    
    protected PrintStream printStream;
    protected int page;
    protected int line = INITIAL_LINE_NUMBER;
    protected String errorFormat;
    
    // Ensures that the statistics header isn't written multiple times. Does not check that a user doesn't write other stuff into the statistics
    // section. A developer is responsible for ensuring that themselves
    protected boolean modeStatistics = false;
    
    // So that writeError knows when to writeErrorHeader
    protected boolean newPage = true;
    
    // For printing new headers when the BO is changed
    protected Class<? extends BusinessObject> businessObjectClass;
    
    /**
     * @see org.kuali.kfs.sys.batch.service.WrappingBatchService#initialize()
     */
    public void initialize() {
        try {
            printStream = new PrintStream(filePath + File.separator + this.fileNamePrefix + dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate()) + fileNameSuffix);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        
        page = initialPageNumber;
        
        // Initial header
        this.writeHeader(title);
    }
    
    /**
     * @see org.kuali.kfs.sys.batch.service.WrappingBatchService#destroy()
     */
    public void destroy() {
        printStream.close();
        
        // reset variables that track state
        page = initialPageNumber;
        line = INITIAL_LINE_NUMBER;
        modeStatistics = false;
        newPage = true;
        businessObjectClass = null;
    }
    
    /**
     * @see org.kuali.kfs.sys.service.ReportWriterService#writeError(java.lang.Class, org.kuali.kfs.sys.Message)
     */
    public void writeError(BusinessObject businessObject, Message message) {
        // Check if we need to write a new table header. We do this if it hasn't been written before or if the businessObject changed
        if (newPage || businessObjectClass == null || !businessObjectClass.getName().equals(businessObject.getClass().getName())) {
            
            if (businessObjectClass != null && !businessObjectClass.getName().equals(businessObject.getClass().getName())) {
                // If it changed push a newline in for neater formatting
                this.writeFormattedMessageLine("");
            }
            
            this.writeErrorHeader(businessObject);
            newPage = false;
            businessObjectClass = businessObject.getClass();
        }
        
        // Get business object formatter that will be used
        BusinessObjectReportHelper businessObjectReportHelper = this.getBusinessObjectReportHelpers().get(businessObject.getClass());
        if (ObjectUtils.isNull(businessObjectReportHelper)) {
            throw new RuntimeException(businessObject.getClass().toString() + " is not handled");
        }
        
        // Print the values of the businessObject per formatting determined by writeErrorHeader
        List<Object> formatterArgs = new ArrayList<Object>();
        formatterArgs.addAll(businessObjectReportHelper.getValues(businessObject));
        formatterArgs.add(message.getMessage());
        this.writeFormattedMessageLine(errorFormat, formatterArgs.toArray());
    }
    
    /**
     * @see org.kuali.kfs.sys.service.ReportWriterService#writeError(java.lang.Class, java.util.List)
     */
    public void writeError(BusinessObject businessObject, List<Message> messages) {
        int i = 0;
        for (Iterator<Message> messagesIter = messages.iterator(); messagesIter.hasNext();) {
            Message message = messagesIter.next();
            
            if (i == 0) {
                // First one has its values written
                this.writeError(businessObject, message);
            } else {
                // Any consecutive one only has message written, hence use padding
                
                // Get business object formatter that will be used
                BusinessObjectReportHelper businessObjectReportHelper = this.getBusinessObjectReportHelpers().get(businessObject.getClass());
                if (ObjectUtils.isNull(businessObjectReportHelper)) {
                    throw new RuntimeException(businessObject.getClass().toString() + " is not handled");
                }
                
                // Print the values of the businessObject per formatting determined by writeErrorHeader
                List<Object> formatterArgs = new ArrayList<Object>();
                formatterArgs.addAll(businessObjectReportHelper.getBlankValues(businessObject));
                formatterArgs.add(message.getMessage());
                this.writeFormattedMessageLine(errorFormat, formatterArgs.toArray());
            }
            
            i++;
        }
    }
    
    /**
     * @see org.kuali.kfs.sys.service.ReportWriterService#writeNewLines(int)
     */
    public void writeNewLines(int lines) {
        for (int i = 0; i < lines; i++) {
            this.writeFormattedMessageLine("");
        }
    }
    
    /**
     * @see org.kuali.kfs.sys.service.ReportWriterService#writeStatisticLine(java.lang.String, java.lang.Object[])
     */
    public void writeStatisticLine(String message, Object ... args) {
        // Statistics header is only written if it hasn't been written before
        if (!modeStatistics) {
            this.modeStatistics = true;
            
            // If nothing has been written to the report we don't want to page break
            if (!(page == initialPageNumber && line == INITIAL_LINE_NUMBER)) {
                this.pageBreak();
            }
            
            this.writeFormattedMessageLine("*********************************************************************************************************************************");
            this.writeFormattedMessageLine("*********************************************************************************************************************************");
            this.writeFormattedMessageLine("*******************" + statisticsLabel + "*******************");
            this.writeFormattedMessageLine("*********************************************************************************************************************************");
            this.writeFormattedMessageLine("*********************************************************************************************************************************");
        }
        
        this.writeFormattedMessageLine(statisticsLeftPadding + message, args);
    }
    
    /**
     * @see org.kuali.kfs.sys.service.ReportWriterService#writeFormattedMessageLine(java.lang.String)
     */
    public void writeFormattedMessageLine(String format) {
        this.writeFormattedMessageLine(format, new Object());
    }
    
    /**
     * @see org.kuali.kfs.sys.service.ReportWriterService#writeFormattedMessageLine(java.lang.String, java.lang.Object[])
     */
    public void writeFormattedMessageLine(String format, Object ... args) {
        String message = String.format(format + "\n", args);
        
        // Log we are writing out of bounds. Would be nice to show message here but not so sure if it's wise to dump that data into logs
        if (message.length() > pageWidth) {
            LOG.warn("message is out of bounds writing anyway");
        }
        
        printStream.printf(message);
        
        line++;
        if (line >= pageLength) {
            this.pageBreak();
        }
    }
    
    /**
     * Helper method for breaking the page and writing a new header
     */
    protected void pageBreak() {
        // Intentionally not using writeFormattedMessageLine here since it would loop trying to page break ;)
        printStream.printf("%c\n",12);
        page++;
        line = INITIAL_LINE_NUMBER;
        newPage = true;
        
        this.writeHeader(title);
    }
    
    /**
     * Helper method to write a header for placement at top of new page
     * @param title that should be printed on this header
     */
    protected void writeHeader(String title) {
        String headerText = String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM",dateTimeService.getCurrentDate());
        int reportTitlePadding = pageWidth/2 - headerText.length() - title.length()/2;
        headerText = String.format("%s%" + (reportTitlePadding + title.length()) + "s%" + reportTitlePadding + "s", headerText, title, "");
        
        this.writeFormattedMessageLine("%s%s%,9d", headerText, pageLabel, page);
        this.writeFormattedMessageLine("");
    }
    
    /**
     * Helper method to write the error header
     * @param businessObject to print header for
     */
    protected void writeErrorHeader(BusinessObject businessObject) {
        BusinessObjectReportHelper businessObjectReportHelper = this.getBusinessObjectReportHelpers().get(businessObject.getClass());
        if (ObjectUtils.isNull(businessObjectReportHelper)) {
            throw new RuntimeException(businessObject.getClass().toString() + " is not handled");
        }
        List<String> errorHeader = businessObjectReportHelper.getTableHeader(pageWidth);
        
        // If we are at end of page and don't have space for table header, go ahead and page break
        if (errorHeader.size() + line >=  pageLength) {
            this.pageBreak();
        }
        
        // Print the header one by one. Note the last element is the formatter. So capture that seperately
        for (Iterator<String> headers = errorHeader.iterator(); headers.hasNext();) {
            String header = headers.next();
            
            if (headers.hasNext()) {
                this.writeFormattedMessageLine("%s", header);                
            } else {
                errorFormat = header;
            }
        }
    }
    
    /**
     * Initializes the businessObjectReportHelpers Map based on spring bean injections. This represents keys mapped to businessObjectReportHelpers that
     * are responsible for managing the reporting printing of that key. Note that several keys may map to the same businessObjectReportHelpers depending
     * on how it is defined in spring.
     * @return map of class by BusinessObjectReportHelper
     */
    public Map<Class<? extends BusinessObject>, BusinessObjectReportHelper> getBusinessObjectReportHelpers() {
        if (ObjectUtils.isNull(businessObjectReportHelpers)) {
            businessObjectReportHelpers = new HashMap<Class<? extends BusinessObject>, BusinessObjectReportHelper>();
            
            for (Iterator<BusinessObjectReportHelper> businessObjectReportHelpersIter = SpringContext.getBeansOfType(BusinessObjectReportHelper.class).values().iterator(); businessObjectReportHelpersIter.hasNext();) {
                BusinessObjectReportHelper businessObjectReportHelper = businessObjectReportHelpersIter.next();
                
                for (Iterator<Class<? extends BusinessObject>> responsibleClassesIter = businessObjectReportHelper.getResponsibleClasses().iterator(); responsibleClassesIter.hasNext();) {
                    Class<? extends BusinessObject> responsibleClass = responsibleClassesIter.next();
                    
                    businessObjectReportHelpers.put(responsibleClass, businessObjectReportHelper);
                }
            }            
        }
        
        return businessObjectReportHelpers;
    }

    /**
     * Sets the filePath
     * 
     * @param filePath The filePath to set.
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    /**
     * Sets the fileNamePrefix
     * 
     * @param fileNamePrefix The fileNamePrefix to set.
     */
    public void setFileNamePrefix(String fileNamePrefix) {
        this.fileNamePrefix = fileNamePrefix;
    }
    
    /**
     * Sets the fileNameSuffix
     * 
     * @param fileNameSuffix The fileNameSuffix to set.
     */
    public void setFileNameSuffix(String fileNameSuffix) {
        this.fileNameSuffix = fileNameSuffix;
    }

    /**
     * Sets the title
     * 
     * @param title The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * Sets the pageWidth
     * 
     * @param pageWidth The pageWidth to set.
     */
    public void setPageWidth(int pageWidth) {
        this.pageWidth = pageWidth;
    }

    /**
     * Sets the pageLength
     * 
     * @param pageLength The pageLength to set.
     */
    public void setPageLength(int pageLength) {
        this.pageLength = pageLength;
    }

    /**
     * Sets the initialPageNumber
     * 
     * @param initialPageNumber The initialPageNumber to set.
     */
    public void setInitialPageNumber(int initialPageNumber) {
        this.initialPageNumber = initialPageNumber;
    }

    /**
     * Sets the statisticsLabel
     * 
     * @param statisticsLabel The statisticsLabel to set.
     */
    public void setStatisticsLabel(String statisticsLabel) {
        this.statisticsLabel = statisticsLabel;
    }

    /**
     * Sets the statisticsLeftPadding
     * 
     * @param statisticsLeftPadding The statisticsLeftPadding to set.
     */
    public void setStatisticsLeftPadding(String statisticsLeftPadding) {
        this.statisticsLeftPadding = statisticsLeftPadding;
    }

    /**
     * Sets the pageLabel
     * 
     * @param pageLabel The pageLabel to set.
     */
    public void setPageLabel(String pageLabel) {
        this.pageLabel = pageLabel;
    }

    /**
     * Sets the DateTimeService
     * 
     * @param dateTimeService The DateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
