/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.tracking.data;

import java.util.ArrayList;
import java.util.List;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class TrackingDataEntry {

    private String id = "";

    private String name = "";

    private String type = "";

    private String url = "url";

    private List<TrackingDataVar> vars;

    /**
     * Adds the var.
     * 
     * @param var
     *            the var
     */
    public void addVar(final TrackingDataVar var) {
        if (null == this.vars) {
            this.vars = new ArrayList<TrackingDataVar>();
        }
        this.vars.add(var);
    }

    /**
     * Gets the id.
     * 
     * @return the id
     */
    public String getId() {
        return this.id;
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
     * Gets the type.
     * 
     * @return the type
     */
    public String getType() {
        return this.type;
    }

    /**
     * Returns the url.
     * 
     * @return the url
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * Gets the vars.
     * 
     * @return the vars
     */
    public List<TrackingDataVar> getVars() {
        if (null == this.vars) {
            this.vars = new ArrayList<TrackingDataVar>();
        }
        return this.vars;
    }

    /**
     * Sets the id.
     * 
     * @param id
     *            the id
     */
    public void setId(final String id) {
        this.id = id;

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

    /**
     * Sets the type.
     * 
     * @param type
     *            the type
     */
    public void setType(final String type) {
        this.type = type;

    }

    /**
     * Sets the url.
     * 
     * @param url
     *            the url to set
     */
    public void setUrl(final String url) {
        this.url = url;
    }
}
