/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.core.link.aspects.utils;

import org.apache.commons.lang.StringUtils;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class PopupJsBuilder {
	   private static final int MIN_SIZE = 400;

	    private int width = MIN_SIZE;
	    private int height = MIN_SIZE;
	    private String scrollbars;
	    private String resizable;

	    /**
	     * Creates the Popup Javascript.
	     * 
	     * @return a JS call to window.open() using the passed values.
	     */
	    public String build() {
	        final StringBuilder js = new StringBuilder("window.open(this.href,'','");
	        js.append("width=" + this.width);
	        js.append(",height=" + this.height);
	        if (StringUtils.isNotEmpty(this.scrollbars)) {
	            js.append(",scrollbars=" + this.scrollbars);
	        }
	        if (StringUtils.isNotEmpty(this.resizable)) {
	            js.append(",resizable=" + this.resizable);
	        }
	        js.append("');");
	        return js.toString();
	    }

	    /**
	     * Sets the width of the popup.
	     * 
	     * @param value
	     *            the new width. Minimum is 400.
	     * @return the builder itself
	     */
	    public PopupJsBuilder width(final int value) {
	        this.width = value > MIN_SIZE ? value : MIN_SIZE;
	        return this;
	    }

	    /**
	     * Sets the height of the popup.
	     * 
	     * @param value
	     *            the new height. Minimum is 400.
	     * @return the builder itself
	     */
	    public PopupJsBuilder height(final int value) {
	        this.height = value > MIN_SIZE ? value : MIN_SIZE;
	        return this;
	    }

	    /**
	     * Sets whether to allow scrollbars. This may not work in all browsers.
	     * 
	     * @param allow
	     *            whether to allow scrollbars.
	     * @return the builder itself
	     */
	    public PopupJsBuilder scrollbars(final boolean allow) {
	        this.scrollbars = allow ? "yes" : "no";
	        return this;
	    }

	    /**
	     * Sets whether to allow window resizing. This may not work in all browsers.
	     * 
	     * @param allow
	     *            whether to allow window resizing.
	     * @return the builder itself
	     */
	    public PopupJsBuilder resizable(final boolean allow) {
	        this.resizable = allow ? "yes" : "no";
	        return this;
	    }
}
