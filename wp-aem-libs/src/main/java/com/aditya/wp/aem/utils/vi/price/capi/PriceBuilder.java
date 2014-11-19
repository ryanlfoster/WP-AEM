/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.utils.vi.price.capi;

import com.aditya.gmwp.aem.services.vehicledata.data.BaseballcardData;
import com.aditya.gmwp.aem.services.vehicledata.data.VehicleData;
import com.aditya.gmwp.aem.utils.vi.VehiclePriceInformation;
import com.aditya.gmwp.aem.utils.vi.price.PriceConfigurationFactory;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface PriceBuilder {
    /**
     * Returns the price configuration factory.
     * 
     * @return price configuration factory
     */
    PriceConfigurationFactory getConfigurationFactory();

    /**
     * Returns a default {@link VehiclePriceInformation} object using values from {@link PriceConfiguration}.
     * 
     * @param priceConfiguration
     *            the {@link PriceConfiguration} to get values from
     * @return default {@link VehiclePriceInformation}
     */
    VehiclePriceInformation buildDefault(final PriceConfiguration priceConfiguration);

    /**
     * Returns the {@link VehiclePriceInformation} object that's used for price output using <code>PriceTag</code>.
     * 
     * @param priceConfiguration
     *            the {@link PriceConfiguration} to get default price related values from
     * @param baseballCardData
     *            the {@link BaseballcardData} to get values like bodystyle, carline code etc. from
     * @param vehicleData
     *            the {@link VehicleData} to get carline and bodystyle from to get formatted price
     * @return {@link VehiclePriceInformation}
     */
    VehiclePriceInformation build(final PriceConfiguration priceConfiguration,
                                  final BaseballcardData baseballCardData,
                                  final VehicleData vehicleData);
}
