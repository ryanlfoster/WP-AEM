/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.utils.vi.price;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.aditya.gmwp.aem.services.core.ServiceProvider;
import com.aditya.gmwp.aem.utils.vi.price.capi.PriceConfiguration;
import com.aditya.gmwp.aem.utils.vi.price.impl.PriceConfigurationImpl;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class PriceConfigurationFactory {
    /** the price configuration. */
    private final ConcurrentMap<String, PriceConfiguration> store;

    /**
     * Constructor.
     */
    private PriceConfigurationFactory() {
        this.store = new ConcurrentHashMap<String, PriceConfiguration>();
    }

    /**
     * Returns a new instance of {@link PriceConfigurationFactory}, not singleton.
     * 
     * @return {@link PriceConfigurationFactory}
     */
    public static PriceConfigurationFactory getInstance() {
        return new PriceConfigurationFactory();
    }

    /**
     * This methods retrieves in a flyweight pattern way a {@link PriceConfiguration}.
     * 
     * @param serviceProvider
     *            the {@link ServiceProvider} to get services from
     * @param page
     *            the page and its path to get {@link PriceConfiguration}
     * @return {@link PriceConfiguration}
     */
    public PriceConfiguration getConfiguration(final ServiceProvider serviceProvider,
                                               final Page page) {
        final String path = page.getPath();
        if (!this.store.containsKey(path)) {
            this.store.put(path, new PriceConfigurationImpl(serviceProvider, page));
        }

        return this.store.get(path);
    }
}
