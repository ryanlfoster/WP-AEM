/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.utils;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class GlossaryUtil {

    private GlossaryUtil() {
    }

    /**
     * Returns the anchor appended to the '#' for a glossary. The anchor name is of pattern
     * 'glossary-' + name of glossary.
     * 
     * @param glossary
     *            the glossary resource to get name from
     * @return anchor
     */
    public static String getSimpleGlossaryAnchor(final Resource glossary) {
        if (null == glossary) {
            throw new IllegalArgumentException("Parameter 'glossary' cannot be null.");
        }
        return "glossary-" + glossary.getName();
    }

    /**
     * Returns the anchor appended to the '#'. Either the maintained human readable one from the
     * cnt_glossary_item_c1 component or nav_az_item_c1 + cnt_glossary_item_c1 resource name as
     * fallback.
     * 
     * @param glossaryItem
     *            the glossary item resource
     * @return anchor
     */
    public static String getAnchorFromResource(final Resource glossaryItem) {
        if (null == glossaryItem) {
            throw new IllegalArgumentException("Parameter 'glossaryItem' cannot be null.");
        }
        String anchor = ResourceUtil.getValueMap(glossaryItem).get("anchor", String.class);
        if (!AnchorUtil.isValidId(anchor)) {
            String navAzItemC1ResourceName = glossaryItem.getParent().getParent().getName();
            anchor = navAzItemC1ResourceName + "-" + glossaryItem.getName();
        }
        return anchor;
    }
}
