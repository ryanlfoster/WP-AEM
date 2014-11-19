/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.vehicledata.data;

import java.text.ParseException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.gmwp.aem.properties.CompanyConfigProperties;
import com.aditya.gmwp.aem.services.config.CompanyService;
import com.aditya.gmwp.aem.services.config.ConfigService;
import com.aditya.gmwp.aem.utils.DBC;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public enum Brand {

    BUICK("buick", "Buick"), //
    BUICK_GLOBAL("buick-global", "Buick"), //
    CADILLAC("cadillac", "Cadillac"), //
    CHEVROLET("chevrolet", "Chevrolet"), //
    CHEVROLET_US("chevrolet_us", "Chevrolet US"), //
    CORVETTE("corvette", "Corvette"), //
    GMC("gmc", "GMC"), //
    GMDAEWOO("daewoo", "Daewoo"), //
    GMFLEET("gmfleet", "GM Fleet"), //
    HOLDEN("holden", "Holden"), //
    HUMMER("hummer", "Hummer"), //
    ISUZU("isuzu", "Isuzu"), //
    MCS("mcs", "MCS"),//
    NONBRANDED("non-branded", "Non-Branded"), //
    OPEL("opel", "Opel"), //
    PONTIAC("pontiac", "Pontiac"), //
    SAAB("saab", "Saab"), //
    SATURN("saturn", "Saturn"), //
    VAUXHALL("vauxhall", "Vauxhall");

    private static final Logger LOG = LoggerFactory.getLogger(Brand.class);

    /**
     * This method can be used to retrieve the brand-element from the enumeration by its name. Case is ignored.
     * 
     * @param id
     *            the brand id, which is always in lowercase.
     * @return a brand-element from the enumeration or null, of no matching element can be found.
     */
    public static Brand lookupBrandById(final String id) {
        for (Brand brand : Brand.values()) {
            if (brand.getId().equals(id)) {
                return brand;
            }
        }
        return null;
    }

    /**
     * Returns the brand by resource path.
     * 
     * @param path
     *            the resource path
     * @param service
     *            the config service
     * @return brand or <code>Brand.NONBRANDED</code> if retrieval fails
     */
    public static Brand fromPath(final String path,
                                 final ConfigService service) {
        DBC.assertNotNull(path, "path");
        DBC.assertNotNull(service, "configService");

        Brand brand = null;
        try {
            brand = service.getBrandNameFromPath(path);
        } catch (ParseException pEx) {
            LOG.warn("Can not parse brand from resource at path {}.", path);
            brand = Brand.NONBRANDED;
        }

        return brand;
    }

    /**
     * Returns the brand path (style) defined on the company template.
     * 
     * @param brand
     *            the brand that will be used instead if no brand style defined on the company template
     * @param page
     *            the current page to get brand style with help of the company service
     * @param service
     *            the company service
     * @return brand path
     */
    public static String getBrandPath(final Brand brand,
                                      final Page page,
                                      final CompanyService service) {
        DBC.assertNotNull(brand, "brand");
        DBC.assertNotNull(page, "page");
        DBC.assertNotNull(service, "service");
        final String brandPath = service.getConfigValue(page, CompanyConfigProperties.BRAND_STYLE);
        return (StringUtils.isEmpty(brandPath)) ? brand.getId() : brandPath;
    }

    private String id;

    private String name;

    /**
     * private constructor.
     * 
     * @param id
     *            the brand id
     * @param name
     *            the brand name
     */
    private Brand(final String id, final String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Returns the id.
     * 
     * @return the id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Returns the name.
     * 
     * @return the brand name.
     */
    public String getName() {
        return this.name;
    }
}
