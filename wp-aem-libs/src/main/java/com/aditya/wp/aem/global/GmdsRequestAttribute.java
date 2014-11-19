/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.global;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.aditya.gmwp.aem.components.webwrapping.WebwrappingPreprocessor;
import com.aditya.gmwp.aem.services.config.model.Country;
import com.aditya.gmwp.aem.services.tracking.model.TrackingModel;
import com.aditya.gmwp.aem.services.vehicledata.data.VehicleData;
import com.aditya.gmwp.aem.utils.ddp.DdpDisclaimerRequestData;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public enum GmdsRequestAttribute {
    CONTENT_LAYER(Object.class), //
    CONTENT_LAYER_ID(Integer.class), //
    CONTENT_MMHOTSPOT_ID(Integer.class), //
    CONTENT_MMPAR_ID(Integer.class), //
    CONTENT_VI3_ID(Integer.class), //
    CONTENT_MMVIDEO_C2_ID(Integer.class), //
    CONTENT_UNIQUE_LAYER_ID(Object.class), //
    COUNTRY(Country.class), //
    CURRENT_PAGE_AREA(String.class), //
    CURRENT_PAGE_AREA_DEFINED_BY_LEVEL(Integer.class), //
    CURRENT_PAGE_MAIN_AREA(String.class), //
    DDP_DISCLAIMER_REQUEST_DATA(DdpDisclaimerRequestData.class), //
    GALLERY_ID(Integer.class), //
    MODAL_LAYER_ID(Integer.class), //
    NAVIGATION_DISPLAYS_GRANDPARENTS_SIBLINGS(Boolean.class), //
    NAVIGATION_ANCHOR_ID(Integer.class), //
    NAVTABLAY_NO(Integer.class), //
    OMNITURE_SPROP24_PAGE_PATH(String.class), //
    REQUIRES_ZIPCODE_JS(Boolean.class), //
    T12_TEASER_LIST(List.class), //
    T12_TEASER_TYPE(String.class), //
    TRACKING_MODEL(TrackingModel.class), //
    VEHICLE_DATA(VehicleData.class), //
    VEHICLE_DATA_LSLR_MAPPING(Map.class), //
    WEBWRAPPING_PREPROCESSOR(WebwrappingPreprocessor.class);

    protected static final String GMDS_NAMESPACE = "gmds.";

    private Class<? extends Object> type;

    /**
     * Creates a new instance.
     * 
     * @param type
     *            the type of this request attribute.
     */
    private GmdsRequestAttribute(final Class<? extends Object> type) {
        this.type = type;
    }

    /**
     * Convenience method that gets this request attribute from the request.
     * 
     * @param request
     *            the servlet request
     * @return the value from the request, may be null if no value has been set before.
     */
    public Object get(final HttpServletRequest request) {
        return request.getAttribute(getName());
    }

    /**
     * Gets the name of the request attribute.
     * 
     * @return the name of this request attribute.
     */
    public String getName() {
        return GMDS_NAMESPACE + name();
    }

    /**
     * Gets the type of the request attribute.
     * 
     * @return returns the type of the request attribute.
     */
    public Class<? extends Object> getType() {
        return this.type;
    }

    /**
     * Convenience method that sets this request attribute into the request.
     * 
     * @param request
     *            the servlet request
     * @param value
     *            the value to be set.
     */
    public void set(final HttpServletRequest request,
                    final Object value) {
        if (!(this.type.isInstance(value))) {
            throw new IllegalArgumentException("Cannot set value of type '" + value.getClass().getName()
                    + "' for request attrbute '" + getName() + "', because type of this attribute is set to '"
                    + this.type.getName() + "'.");
        }
        request.setAttribute(getName(), value);
    }
}
