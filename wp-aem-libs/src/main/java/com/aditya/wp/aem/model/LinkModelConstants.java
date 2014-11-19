/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.model;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class LinkModelConstants {

    public static final String DEEPLINK_PARAM = "deeplinkParam";

    public static final String DISCLAIMER = "disclaimer";

    public static final String EXTERNAL_LINK = "externalLink";

    public static final String GLOSSARY_LINK = "glossaryLink";

    public static final String IN_PAGE_LINK = "inPageLink";

    public static final String INTERNAL_LINK = "internalLink";

    public static final String LINK_PARAMS = "link_params";

    public static final String LINK_TEXT = "linkText";

    public static final String LINK_TITLE = "linkTitle";

    /**
     * Private Constructor to prevent instantiation of constants class.
     */
    private LinkModelConstants() {
        throw new AssertionError("The LinkModelConstants class should not be initiated!");
    }
}
