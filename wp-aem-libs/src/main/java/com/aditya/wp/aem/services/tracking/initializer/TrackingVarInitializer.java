/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.tracking.initializer;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.gmwp.aem.services.tracking.data.OmnitureVariableContainer;
import com.aditya.gmwp.aem.services.tracking.data.OmnitureVariables;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public abstract class TrackingVarInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(TrackingVarInitializer.class);
    private Map<OmnitureVariables, OmnitureVariableContainer> variables;
    private Page currentPage;

    /**
     * @return the logger
     */
    protected static Logger getLog() {
        return LOG;
    }

    /**
     * @return the variables
     */
    protected Map<OmnitureVariables, OmnitureVariableContainer> getVariables() {
        return this.variables;
    }

    /**
     * @return the currentPage
     */
    protected Page getCurrentPage() {
        return this.currentPage;
    }

    /**
     * Initializes one or more tracking variables, using information taken from the current page. Entries in the Map
     * must already exists to be set.
     * 
     * @param currentPage
     *            the current page.
     * @param variables
     *            the {@link Map} to set the variables in.
     */
    public void initializeVariables(final Page currentPage,
                                    final Map<OmnitureVariables, OmnitureVariableContainer> variables) {
        this.currentPage = currentPage;
        this.variables = variables;

        initialize();
    }

    /**
     * Overwrite this method to specify behavior.
     */
    protected abstract void initialize();

    /**
     * Writes a debug message onto the logger, if debugging is enabled.
     * 
     * @param message
     *            the message to write
     */
    protected void printDebugMessage(final String message) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(message);
        }
    }

    /**
     * Sets any number of Omniture variables to be a given value.
     * 
     * @param value
     *            the value
     * @param variablesToBeSet
     *            the variables
     */
    protected void setVariables(final String value,
                                final OmnitureVariables... variablesToBeSet) {
        for (OmnitureVariables currentVariable : variablesToBeSet) {
            if (doesNotExist(currentVariable)) {
                createVariable(currentVariable);
            }
            setVariable(value, currentVariable);
        }
    }

    /**
     * @param value
     * @param currentVariable
     */
    private void setVariable(final String value,
                             final OmnitureVariables currentVariable) {
        this.variables.get(currentVariable).setValue(value);
    }

    /**
     * @param currentVariable
     * @return
     */
    private boolean doesNotExist(final OmnitureVariables currentVariable) {
        return null == this.variables.get(currentVariable);
    }

    /**
     * @param currentVariable
     */
    private void createVariable(final OmnitureVariables currentVariable) {
        this.variables.put(currentVariable, new OmnitureVariableContainer(currentVariable.getJavaScriptVariableName(),
                currentVariable.getVariableDescription()));
    }
}