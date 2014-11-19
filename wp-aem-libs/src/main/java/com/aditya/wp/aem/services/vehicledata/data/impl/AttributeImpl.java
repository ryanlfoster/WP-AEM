/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.vehicledata.data.impl;

import org.apache.commons.lang.StringUtils;

import com.aditya.gmwp.aem.services.vehicledata.data.Attribute;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class AttributeImpl implements Attribute {

	private static final long serialVersionUID = 1L;

    private String id;

    private String value;

    private String description;

    private String unit;

    private String filterType;

    /**
     * Generates an Instance.
     * 
     * @param id
     *            ddp id
     * @param value
     *            ddp value
     * @param description
     *            ddp description
     * @param unit
     *            ddp unit
     * @param filterType
     *            ddp filterType
     */
    public AttributeImpl(final String id, final String value, final String description, final String unit,
            final String filterType) {
        super();
        this.id = id;
        this.value = value;
        this.description = description;
        this.unit = unit;
        this.filterType = filterType;
    }

    /**
     * Generates an Instance.
     * 
     * @param id
     *            ddp id
     * @param value
     *            ddp value
     * @param description
     *            ddp description
     */
    public AttributeImpl(final String id, final String value, final String description) {
        this(id, value, description, null, null);
    }

    /**
     * Generates an Instance.
     */
    public AttributeImpl() {

    }

    @Override
    public final String getId() {
        return this.id;
    }

    @Override
    public final void setId(final String id) {
        this.id = id;
    }

    @Override
    public final String getValue() {
        return this.value;
    }

    @Override
    public final void setValue(final String value) {
        this.value = value;
    }

    @Override
    public final String getDescription() {
        return this.description;
    }

    @Override
    public final void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public final boolean equals(final Object object) {
        return ((object instanceof Attribute) && StringUtils.equals(this.id, ((Attribute) object).getId()));
    }

    @Override
    public final String getUnit() {
        return this.unit;
    }

    @Override
    public final void setUnit(final String unit) {
        this.unit = unit;
    }

    @Override
    public final String getFilterType() {
        return this.filterType;
    }

    @Override
    public final void setFilterType(final String filterType) {
        this.filterType = filterType;
    }

    @Override
    public final int hashCode() {
        return getId().hashCode();
    }
}
