/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.tracking.data;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class TrackingDataVar {

    private String data;

    private String name;

    /**
     * Gets the data.
     * 
     * @return the data
     */
    public String getData() {
        return this.data;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the data.
     * 
     * @param data
     *            the data
     */
    public void setData(final String data) {
        this.data = data;
    }

    /**
     * Sets the name.
     * 
     * @param name
     *            the name
     */
    public void setName(final String name) {
        this.name = name;
    }
}
