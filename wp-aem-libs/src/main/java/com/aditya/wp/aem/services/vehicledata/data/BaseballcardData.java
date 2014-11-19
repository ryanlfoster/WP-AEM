/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.vehicledata.data;

import com.aditya.gmwp.aem.properties.Properties;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface BaseballcardData {

    /**
     * Gets a property that is stored in a baseballcard.
     * 
     * @param baseballCardProperty
     *            the baseballCard Property
     * @return property
     */
    String getBaseballcardProperty(Properties baseballCardProperty);
}