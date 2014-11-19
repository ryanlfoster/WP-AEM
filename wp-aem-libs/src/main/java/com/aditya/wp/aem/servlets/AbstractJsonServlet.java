/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.servlets;

import java.io.IOException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.io.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public abstract class AbstractJsonServlet extends AbstractServlet {
	
    private static final long serialVersionUID = -5748470796176275455L;

    private static final Logger LOG = LoggerFactory.getLogger(AbstractJsonServlet.class);

    /**
     * Adds the json content.
     * 
     * @param request
     *            the request
     * @param response
     *            the response
     * @param writer
     *            the writer
     * @throws JSONException
     *             the jSON exception
     */
    public abstract void addJSONContent(final SlingHttpServletRequest request,
                                        final SlingHttpServletResponse response,
                                        final JSONWriter writer) throws JSONException;

    /*
     * (non-Javadoc)
     * @see org.apache.sling.api.servlets.SlingSafeMethodsServlet#service(org.apache.sling.api.SlingHttpServletRequest,
     * org.apache.sling.api.SlingHttpServletResponse)
     */
    @Override
    protected final void service(final SlingHttpServletRequest request,
                                 final SlingHttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8;");
        response.setCharacterEncoding("utf-8");

        final JSONWriter w = new JSONWriter(response.getWriter());

        try {
            addJSONContent(request, response, w);
        } catch (final JSONException e) {
            LOG.error("Unable to create JSON", e);
        }
    }

    /**
     * write a single object.
     * 
     * @param text
     *            the text
     * @param value
     *            its value
     * @param w
     *            the writer
     * @throws JSONException
     *             writer exception
     */
    protected final void writeTextValueObject(final String text,
                                              final String value,
                                              final JSONWriter w) throws JSONException {
        w.object();
        w.key("text").value(text);
        w.key("value").value(value);
        w.endObject();
    }

    /**
     * Returns a translated text form the localized resource bundle.
     * 
     * @param key
     *            key of the text
     * @param request
     *            current request where the bundle can be found
     * @return translated text
     */
    protected final String getI18nText(final String key,
                                       final SlingHttpServletRequest request) {
        final ResourceBundle bundle = request.getResourceBundle(request.getLocale());
        try {
            return bundle.getString(key);
        } catch (final MissingResourceException e) {
            return key;
        }
    }
}
