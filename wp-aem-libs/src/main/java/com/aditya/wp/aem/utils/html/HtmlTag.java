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
public enum HtmlTag {

    LI, OL, UL, SCRIPT, A, EM, I, B, BR, P, STRONG, SUB, SUP, SPAN;

    /**
     * Lazy init.
     */
    private static final class Holder {

        static final Map<String, HtmlTag> TAGS = new HashMap<String, HtmlTag>();
        static {
            for (HtmlTag ht : values()) {
                TAGS.put(ht.toString(), ht);
            }
        }

        /**
         * Constructor.
         */
        private Holder() {
        }
    }

    /**
     * Returns the <code>HtmlTag</code> from a string.
     * 
     * @param tag
     *            the tag string to get enum for
     * @return the <code>HtmlTag</code> or <code>null</code> if no appropriate enum found
     */
    public static final HtmlTag fromString(final String tag) {
        return Holder.TAGS.get(tag);
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
