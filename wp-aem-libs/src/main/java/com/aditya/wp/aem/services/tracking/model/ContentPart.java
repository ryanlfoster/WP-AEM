/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.tracking.model;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public enum ContentPart {
    BRAND, REGION, COUNTRY, LANGUAGE;

    /**
     * Returns whether an enum isn't part of passed parts.
     * 
     * @param parts
     *            the parts
     * @return not part of
     */
    public boolean isNotPartOf(final ContentPart... parts) {
        return !isPartOf(parts);
    }

    /**
     * Returns whether an enum is part of passed parts.
     * 
     * @param parts
     *            the parts
     * @return part of
     */
    public boolean isPartOf(final ContentPart... parts) {
        if (null != parts) {
            for (ContentPart p : parts) {
                if (equals(p)) {
                    return true;
                }
            }
        }

        return false;
    }
}
