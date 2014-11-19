/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.utils.tracking;

import java.util.HashMap;
import java.util.Map;

import com.aditya.gmwp.aem.model.OmnitureLinkTrackingData.OmnitureLinkTrackingEvents;
import com.aditya.gmwp.aem.services.tracking.data.OmnitureVariables;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class OmnitureJSBuilder {

    private static final String TEMPLATE = "if(typeof(Omniture_s) != 'undefined'){mrm.util.tagging.omniture_tl(%s, %s, '%s', %s, %s);}";

    private LinkType linkType = LinkType.GENERAL;
    private String clickTarget = "this";
    private String linkName = "this.href";
    private String event = "undefined";
    private Map<OmnitureVariables, String> vars = new HashMap<OmnitureVariables, String>();

    /**
     * Builds the finished JS snippet.
     * 
     * @return a valid function call to Omniture link tracking.
     */
    public String createJS() {
        return String.format(TEMPLATE, this.clickTarget, this.linkName, this.linkType.getLinkTypeChar(), this.event, JsonUtils.getAsJsString(this.vars));
    }

    /**
     * Sets the link type. Defaults to {@link LinkType#GENERAL}.
     * 
     * @param linkType
     *            the link type. null can be passed, but will be ignored.
     * @return this builder
     */
    public OmnitureJSBuilder setLinkType(final LinkType linkType) {
        if (linkType != null) {
            this.linkType = linkType;
        }
        return this;
    }

    /**
     * Disables the delay Omniture usually introduces to ensure all data on the page has been
     * collected. Use this only if the user will stay on the same page after clicking the link.
     * 
     * @return this builder
     */
    public OmnitureJSBuilder disableDelay() {
        // for some reason, delay is disabled by passing "true" as the click target.
        this.clickTarget = "true";
        return this;
    }

    /**
     * Sets the link name. Defaults to the link href.
     * 
     * @param linkName
     *            the link name. null can be passed, but will be ignored.
     * @return this builder
     */
    public OmnitureJSBuilder setLinkName(final String linkName) {
        if (linkName != null) {
            this.linkName = "'" + linkName + "'";
        }
        return this;
    }

    /**
     * Sets the link tracking event that should be transmitted.
     * 
     * @param event
     *            the event. null can be passed, but will be ignored.
     * @return this builder
     */
    public OmnitureJSBuilder setEvent(final OmnitureLinkTrackingEvents event) {
        if (event != null) {
            this.event = "'" + event.toString() + "'";
        }
        return this;
    }

    /**
     * Sets the omniture variables that should be transmitted.
     * 
     * @param vars
     *            the variable map. null can be passed, but will be ignored.
     * @return this builder
     */
    public OmnitureJSBuilder setVariables(final Map<OmnitureVariables, String> vars) {
        if (vars != null) {
            this.vars = vars;
        }
        return this;
    }
}