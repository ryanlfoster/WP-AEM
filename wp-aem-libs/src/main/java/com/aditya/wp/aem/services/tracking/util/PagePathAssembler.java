/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.tracking.util;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class PagePathAssembler {

    private static final int HIERARCHY_ROOT_INDEX = 6;

    private static final Logger LOG = LoggerFactory.getLogger(PagePathAssembler.class);

    private static final int ALLOWED_OMNITURE_PAGENAME_LENGTH = 100;
    private static final int BRAND_INDEX = 1;
    private static final int REGION_INDEX = 2;
    private static final String SEPARATOR = ":";

    private final String[] pathSegments;

    private final String pagePath;

    /**
     * Creates a new PagePathAssembler.
     * 
     * @param pagePath
     *            the original page path to disassemble.
     */
    public PagePathAssembler(final String pagePath) {
        // backup path for logging purposes
        this.pagePath = pagePath;

        // strip "/content" prefix, if present
        String workingCopy = pagePath.startsWith("/content") ? pagePath.replaceFirst("/content", "") : pagePath;

        this.pathSegments = workingCopy.split("/");
    }

    /**
     * Creates an Omniture page name with all parts.
     * 
     * @param currentLocale
     *            the current locale
     * @return the Omniture page name
     */
    public String createOmniturePageName(final Locale currentLocale) {
        return createOmniturePageName(currentLocale, true, true, true, true);
    }

    /**
     * Creates an Omniture Page Name.
     * 
     * @param currentLocale
     *            the current locale
     * @param includeBrand
     *            whether to include brand in page name
     * @param includeRegion
     *            whether to include region in page name
     * @param includeCountry
     *            whether to include country in page name
     * @param includeLanguage
     *            whether to include language in page name
     * @return the omniture page name
     */
    public String createOmniturePageName(final Locale currentLocale,
                                         final boolean includeBrand,
                                         final boolean includeRegion,
                                         final boolean includeCountry,
                                         final boolean includeLanguage) {
        final StringBuilder output = new StringBuilder();

        // TODO: exchange the flag solution with a ContentPart-based one
        addIfSelected(output, getBrand(), includeBrand);
        addIfSelected(output, getRegion(), includeRegion);
        addIfSelected(output, currentLocale.getCountry(), includeCountry);
        addIfSelected(output, currentLocale.getLanguage(), includeLanguage);

        if (output.length() > 0) {
            output.append(SEPARATOR);
        }
        output.append(getAllFollowing());

        // Log WARN message if page name exceeds allowed Omniture page name length.
        if (LOG.isWarnEnabled() && output.length() > ALLOWED_OMNITURE_PAGENAME_LENGTH) {
            LOG.warn("initPageName() - Omniture Page Name for page '" + this.pagePath + "' contains more than "
                    + ALLOWED_OMNITURE_PAGENAME_LENGTH + " characters. Page Name is: '" + output.toString() + "' with "
                    + output.length() + " chars.");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("initPageName() - returns '" + output.toString() + "' with " + output.length() + " chars.");
        }

        return output.toString();
    }

    /**
     * Builds the page parts for all following content levels for the omniture page name.
     */
    public String getAllFollowing() {
        StringBuilder output = new StringBuilder();

        for (int i = HIERARCHY_ROOT_INDEX; i < this.pathSegments.length; i++) {
            addIfSelected(output, this.pathSegments[i], true);
        }

        return output.toString();
    }

    /**
     * Builds the brand for omniture page name using the first 2 characters of the CMS page name: e.g. for opel use
     * 'op', for gmc use 'gm', for chevrolet use 'ch'.
     */
    public String getBrand() {
        if (this.pathSegments.length > BRAND_INDEX && this.pathSegments[BRAND_INDEX].length() >= 2) {
            return this.pathSegments[BRAND_INDEX].substring(0, 2);
        }
        return "";
    }

    /**
     * Builds the region for omniture page name using the first 2 characters, e.g. for northamerica use 'no', for europe
     * 'eu, for middleast use 'mi'.
     */
    public String getRegion() {
        if (this.pathSegments.length > REGION_INDEX && this.pathSegments[REGION_INDEX].length() >= 2) {
            return this.pathSegments[REGION_INDEX].substring(0, 2);
        }
        return "";
    }

    /**
     * If selected, adds nameSegment to builder.
     * 
     * @param builder
     *            the {@link StringBuilder}
     * @param nameSegment
     *            the {@link String} to add
     * @param selected
     *            whether to add the String at all
     */
    private static void addIfSelected(final StringBuilder builder,
                                      final String nameSegment,
                                      final boolean selected) {
        if (selected) {
            if (builder.length() > 0) {
                builder.append(SEPARATOR);
            }
            builder.append(nameSegment);
        }
    }
}
