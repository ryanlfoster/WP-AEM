/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.config;

import java.util.Set;

import com.aditya.wp.aem.services.config.model.Country;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface DomainPathService {
    /**
     * returns the country map. Key is domain, value is content-path
     * 
     * @return the countries
     */
    Set<Country> getCountries();

    /**
     * Gets the Country enum whose domain matches the beginning of the given domain. If no matching is found null is
     * returned.
     * 
     * @param domain
     *            the domain (with or without 'www.')
     * @return the matching Country-String enum or null
     */
    Country getCountryByDomain(String domain);

    /**
     * Gets the Country enum whose content path matches the beginning of the defined content path. If no matching is
     * found null is returned.
     * 
     * @param path
     *            the content path
     * @return country or <code>null</code> if no match is found
     */
    Country getCountryByPath(String path);
}
