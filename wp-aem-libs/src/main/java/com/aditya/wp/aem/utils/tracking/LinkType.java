/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.utils.tracking;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public enum LinkType {
    DOWNLOAD('d'), //
    EXIT('e'), //
    GENERAL('o');

    private char linkTypeChar;

    /**
     * Constructor.
     * 
     * @param linkTypeChar
     *            d, e or o.
     */
    private LinkType(final char linkTypeChar) {
        this.linkTypeChar = linkTypeChar;
    }

    /**
     * Returns the type.
     * 
     * @return d, e or o.
     */
    public char getLinkTypeChar() {
        return this.linkTypeChar;
    }
}