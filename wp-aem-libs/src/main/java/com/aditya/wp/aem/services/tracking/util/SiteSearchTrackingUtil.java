/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.tracking.util;

import java.util.HashMap;
import java.util.Map;

import com.aditya.gmwp.aem.services.tracking.data.OmnitureVariables;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class SiteSearchTrackingUtil {

    private static Map<OmnitureVariables, String> values = new HashMap<OmnitureVariables, String>();

    /**
     * Set the Omniture variable with a value.
     * 
     * @param var
     *            OmnitureVariables
     * @param value
     *            value
     */
    public static void setValue(final OmnitureVariables var, final String value) {
        values.put(var, value);
    }

    /**
     * Get the Omniture variable's value.
     * 
     * @param var
     *            OmnitureVariables
     * @return value
     */
    public static String getValue(final OmnitureVariables var) {
        if (var == null || !values.containsKey(var)) {
            return "";
        }
        return values.get(var);
    }

    /**
     * Private constructor to prevent instantiation of this class.
     */
    private SiteSearchTrackingUtil() {
        throw new AssertionError("This class is not ment to be instantiated.");
    }
}
