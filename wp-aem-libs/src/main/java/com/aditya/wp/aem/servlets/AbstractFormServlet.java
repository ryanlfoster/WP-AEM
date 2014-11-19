/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.servlets;

import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.io.JSONWriter;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public abstract class AbstractFormServlet extends AbstractJsonServlet {

    private static final long serialVersionUID = -4543171927033806219L;

	/**
     * Container for storing options with their values and texts.
     * 
     * @author chauzenberger, namics AG
     * @since GMWP Release 2.5
     */
    public static class Option {

        private final String value;
        private final String text;

        /**
         * Creates an option and assigns value and text.
         * 
         * @param value
         *            value
         * @param text
         *            text
         */
        public Option(final String value, final String text) {
            this.value = value;
            this.text = text;
        }

        /**
         * Returns the key.
         * 
         * @return key
         */
        public final String getValue() {
            return this.value;
        }

        /**
         * Returns the value.
         * 
         * @return value
         */
        public final String getText() {
            return this.text;
        }
    }

    @Override
    public void addJSONContent(final SlingHttpServletRequest request,
                               final SlingHttpServletResponse response,
                               final JSONWriter writer) throws JSONException {
        writer.array();
        for (Option entry : getOptions(request)) {
            writeTextValueObject(entry.text, entry.value, writer);
        }
        writer.endArray();

    }

    /**
     * Returns the form select options.
     * 
     * @param request
     *            current request
     * @return select options
     */
    protected abstract List<Option> getOptions(final SlingHttpServletRequest request);
}
