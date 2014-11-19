/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.utils.html;

import java.util.HashMap;
import java.util.Map;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public enum HtmlTagAttribute {

    CLASS, ID, TITLE, HREF, STYLE, TARGET, ONCLICK;

    /**
     * Lazy init.
     */
    private static final class Holder {

        static final Map<String, HtmlTagAttribute> ATTRIBUTES = new HashMap<String, HtmlTagAttribute>();
        static {
            for (HtmlTagAttribute hta : values()) {
                ATTRIBUTES.put(hta.toString(), hta);
            }
        }

        /**
         * Constructor.
         */
        private Holder() {
        }
    }

    /**
     * Returns the <code>HtmlTagAttribute</code> from a string.
     * 
     * @param attribute
     *            the attribute string to get enum for
     * @return the <code>HtmlTagAttribute</code> or <code>null</code> if no appropriate enum found
     */
    public static final HtmlTagAttribute fromString(final String attribute) {
        return Holder.ATTRIBUTES.get(attribute);
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
