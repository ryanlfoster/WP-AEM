/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.vehicledata.data;

import com.aditya.gmwp.aem.model.ImageModel;
import com.aditya.gmwp.aem.properties.Properties;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface CarlineBaseballcardData extends BaseballcardData {
	
	/**
	 * Gets the carline thumbnail image.
	 * @return the thumbnail image.
	 */
    ImageModel getThumbnailImage();

    /**
     * Gets the legal suffix text.
     * @return the legal suffix text.
     */
    String getLegalSuffixText();

    /**
     * Returns the vehicle family link if defined.
     * @return the link to the vehicle family.
     */
    String getFamilyLink();

    /**
     * Returns the carline title. Uses the navigation title if defined and the page title if not.
     * 
     * @return the baseballcard title.
     */
    String getBaseballCardTitle();

    /**
     * Returns the carline text.
     * 
     * @return carline text
     */
    String getCarlineText();
}
