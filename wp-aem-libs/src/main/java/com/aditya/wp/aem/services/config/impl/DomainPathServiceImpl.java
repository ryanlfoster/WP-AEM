/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.config.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.jcr.query.Query;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.aditya.gmwp.aem.services.config.DomainPathService;
import com.aditya.gmwp.aem.services.config.model.Country;
import com.aditya.gmwp.aem.services.core.AbstractService;
import com.aditya.gmwp.aem.services.core.JcrService;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
@Service
@Component(name = "com.aditya.gmwp.aem.services.config.DomainPathService", label="GMWP Domain Path Service", metatype = true)
public class DomainPathServiceImpl extends AbstractService<DomainPathServiceImpl> implements DomainPathService {

	@Reference
	private transient final JcrService jcrService = null;
	
    private static final String QUERY_FOR_DOMAINMAPPINGCONFIGS = "select * from [nt:base] where isdescendentnode('/etc/admin/domainmapping_config/jcr:content/domainmappingConfigArea') and [sling:resourceType]='gmds/components/domainmapping/domainmappingconfigitem'";

    /*
     * (non-Javadoc)
     * @see com.gm.gssm.gmds.cq.services.config.DomainPathService#getCountries()
     */
    @Override
    public final Set<Country> getCountries() {
        final Set<Country> countries = new HashSet<Country>();
        // read all domain-contentPath-pair from domainmapping_config
        final Iterator<Resource> resources = this.jcrService.getResourceResolver().findResources(QUERY_FOR_DOMAINMAPPINGCONFIGS,
                Query.JCR_SQL2);
        while (resources.hasNext()) {
            final Resource resource = resources.next();
            final ValueMap props = resource.adaptTo(ValueMap.class);
            final String domain = props.get("domain", String.class);
            final String contentPath = props.get("contentPath", String.class);
            if (domain != null && contentPath != null) {
                countries.add(new Country(domain, contentPath));
            }
        }
        return countries;
    }

    /*
     * (non-Javadoc)
     * @see com.gm.gssm.gmds.cq.services.config.DomainPathService#getCountryByDomain(java.lang.String)
     */
    @Override
    public final Country getCountryByDomain(final String domain) {
        Country country = null;
        if (domain != null) {
            int numberOfChars = Integer.MIN_VALUE;
            // check if any domain from etc/admin/domainmapping_config starts with the domain
            for (Country c : getCountries()) {
                // if domain starts with "www." ignore this prefix in further comparison
                String domainNoWWW = domain;
                if (domain.indexOf("www.") == 0) {
                    domainNoWWW = domainNoWWW.replaceFirst("www\\.", "");
                }
                final String matchedDomain = c.getDomain();
                if (domainNoWWW.startsWith(matchedDomain)) {
                    // the longest matching domain wins
                    final int len = matchedDomain.length();
                    if (len > numberOfChars) {
                        numberOfChars = len;
                        country = c;
                    }
                }
            }
        }
        if (country == null) {
            getLog(this).warn("There is no configuration (in etc/admin/domainmapping_config) for the domain '" + domain + "'.");
        }
        return country;
    }

    /*
     * (non-Javadoc)
     * @see com.gm.gssm.gmds.cq.services.config.DomainPathService#getCountryByPath(java.lang.String)
     */
    @Override
    public final Country getCountryByPath(final String path) {
        Country country = null;

        for (Country c : getCountries()) {
            if (c.getContentPath().startsWith(path)) {
                country = c;
                break;
            }
        }

        if (null == country) {
            getLog(this).warn("There is no configuration (in etc/admin/domainmapping_config) for the path '" + path + "'.");
        }

        return country;
    }
}
