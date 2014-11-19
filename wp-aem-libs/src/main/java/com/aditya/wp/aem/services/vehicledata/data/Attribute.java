/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.vehicledata.data;

import java.io.Serializable;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface Attribute extends Serializable {

    /**
     * @return id.
     */
    String getId();

    /**
     * sets id.
     * 
     * @param id
     *            ddp id or custom id
     */
    void setId(final String id);

    /**
     * @return ddp value.
     */
    String getValue();

    /**
     * sets ddp value.
     * 
     * @param value
     *            ddp value.
     */
    void setValue(final String value);

    /**
     * @return ddp description.
     */
    String getDescription();

    /**
     * returns sets ddp description.
     * 
     * @param description
     *            ddp description.
     */
    void setDescription(final String description);

    /**
     * @return ddp unit.
     */
    String getUnit();

    /**
     * sets ddp unit.
     * 
     * @param unit
     *            ddp unit.
     */
    void setUnit(final String unit);

    /**
     * @return ddp filter type.
     */
    String getFilterType();

    /**
     * sets ddp filter type.
     * 
     * @param filterType
     *            filter type.
     */
    void setFilterType(final String filterType);

    @Override
    boolean equals(Object object);

    @Override
    int hashCode();
}
