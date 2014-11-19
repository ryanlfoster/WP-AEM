/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.utils.html;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public enum HtmlEntities {

    AMPERSAND("&amp;", "&");

    private final String entity;
    private final String sign;

    /**
     * Constructor.
     * 
     * @param entity
     *            the entity
     * @param sign
     *            the sign
     */
    private HtmlEntities(final String entity, final String sign) {
        this.entity = entity;
        this.sign = sign;
    }

    /**
     * Returns the html entity.
     * 
     * @return html entity
     */
    public String getEntity() {
        return this.entity;
    }

    /**
     * Returns the sign.
     * 
     * @return sign
     */
    public String getSign() {
        return this.sign;
    }
}

