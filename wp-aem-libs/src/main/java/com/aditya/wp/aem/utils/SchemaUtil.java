/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.utils;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.BooleanUtils;

import com.aditya.gmwp.aem.global.AEMTemplateInfo;
import com.aditya.gmwp.aem.properties.LanguageConfigProperties;
import com.aditya.gmwp.aem.services.config.LanguageSLRService;
import com.aditya.gmwp.aem.services.core.ServiceProvider;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class SchemaUtil {

    /**
     * 
     */
    private SchemaUtil() {
    }

    /**
     * Gets whether schema.org markup is required for the given page
     * 
     * @param currentPage
     *            the page object
     * @param request
     *            the request object
     * @return true if schema.org markup should be written, false otherwise
     */
    public static boolean isSchemaEnabled(final Page currentPage,
                                          final HttpServletRequest request) {
        if (currentPage == null || request == null) {
            return false;
        }

        final LanguageSLRService lslrService = ServiceProvider.INSTANCE.fromSling(request).getLanguageSLRService();
        boolean schemaEnabled = BooleanUtils.toBoolean(lslrService.getConfigValue(currentPage, LanguageConfigProperties.SCHEMA_ORG_MARKUP_ENABLED));
        schemaEnabled &= isSchemaMarkupAvailable(currentPage);
        schemaEnabled &= !BooleanUtils.toBoolean(currentPage.getProperties().get("disableSchema", "false"));
        return schemaEnabled;
    }

    /**
     * Checks to see if Schema.org markup is available for the current page.
     * 
     * @param currentPage
     *            the page object
     * @return true if schema.org markup is available, false otherwise
     */
    public static boolean isSchemaMarkupAvailable(final Page currentPage) {
        boolean schemaAvailable = AEMTemplateInfo.TEMPLATE_T02.matchesTemplate(currentPage);
        schemaAvailable |= AEMTemplateInfo.TEMPLATE_T03.matchesTemplate(currentPage);
        schemaAvailable |= AEMTemplateInfo.TEMPLATE_T03b.matchesTemplate(currentPage);
        schemaAvailable |= AEMTemplateInfo.TEMPLATE_T03c.matchesTemplate(currentPage);
        schemaAvailable |= AEMTemplateInfo.TEMPLATE_T04a.matchesTemplate(currentPage);
        schemaAvailable |= AEMTemplateInfo.TEMPLATE_T04b.matchesTemplate(currentPage);
        schemaAvailable |= AEMTemplateInfo.TEMPLATE_T04c.matchesTemplate(currentPage);
        schemaAvailable |= AEMTemplateInfo.TEMPLATE_T05.matchesTemplate(currentPage);
        schemaAvailable |= AEMTemplateInfo.TEMPLATE_T05b.matchesTemplate(currentPage);
        schemaAvailable |= AEMTemplateInfo.TEMPLATE_T06b.matchesTemplate(currentPage);
        schemaAvailable |= AEMTemplateInfo.TEMPLATE_T06c.matchesTemplate(currentPage);
        schemaAvailable |= AEMTemplateInfo.TEMPLATE_T06d.matchesTemplate(currentPage);
        schemaAvailable |= AEMTemplateInfo.TEMPLATE_T07.matchesTemplate(currentPage);
        schemaAvailable |= AEMTemplateInfo.TEMPLATE_T07b.matchesTemplate(currentPage);
        schemaAvailable |= AEMTemplateInfo.TEMPLATE_T08.matchesTemplate(currentPage);
        schemaAvailable |= AEMTemplateInfo.TEMPLATE_T09.matchesTemplate(currentPage);
        schemaAvailable |= AEMTemplateInfo.TEMPLATE_T10.matchesTemplate(currentPage);
        schemaAvailable |= AEMTemplateInfo.TEMPLATE_T12.matchesTemplate(currentPage);
        schemaAvailable |= AEMTemplateInfo.TEMPLATE_T12b.matchesTemplate(currentPage);
        schemaAvailable |= AEMTemplateInfo.TEMPLATE_T13.matchesTemplate(currentPage);
        schemaAvailable |= AEMTemplateInfo.TEMPLATE_T13b.matchesTemplate(currentPage);
        schemaAvailable |= AEMTemplateInfo.TEMPLATE_T15_SITEMAP.matchesTemplate(currentPage);
        schemaAvailable |= AEMTemplateInfo.TEMPLATE_T15b_MANUAL_SITEMAP.matchesTemplate(currentPage);
        schemaAvailable |= AEMTemplateInfo.TEMPLATE_T16.matchesTemplate(currentPage);
        schemaAvailable |= AEMTemplateInfo.TEMPLATE_T16b.matchesTemplate(currentPage);
        schemaAvailable |= AEMTemplateInfo.TEMPLATE_T16w.matchesTemplate(currentPage);
        schemaAvailable |= AEMTemplateInfo.TEMPLATE_T20.matchesTemplate(currentPage);
        return schemaAvailable;
    }

    /**
     * Checks to see if the current page is an article page
     * 
     * @param currentPage
     *            the page object
     * @return true if article page, false otherwise
     */
    public static boolean isArticlePage(final Page currentPage) {
        return AEMTemplateInfo.TEMPLATE_T13b.matchesTemplate(currentPage);
    }
}
