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
public enum LinkStyle {
	SYSTEM_DEFAULT_STYLE("System Default Style", ""), //
	PRIMARY_BUTTON("Primary Button", "btn_prim"), //
	NBA_BUTTON("NBA Button", "btn_nba"), //
	SECONDARY_BUTTON("Secondary Button", "btn_sec"), //
	NAVIGATION_LINKS("Navigation Links", "ln_nav"), //
	LINK("Link", "ln"), //
	;

    private String name;
    private String cssClass;

    /**
     * Instantiates a new link style.
     * 
     * @param name
     *            the name
     * @param cssClass
     *            the css class
     */
    private LinkStyle(final String name, final String cssClass) {
        this.name = name;
        this.cssClass = cssClass;
    }

    /**
     * Gets the name.
     * 
     * @return the name of this LinkStyle as used in HTML forms
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the css class.
     * 
     * @return the CSS class corresponding to this LinkStyle.
     */
    public String getCssClass() {
        return this.cssClass;
    }
}
