/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.vehicledata.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.gmwp.aem.global.AEMTemplateInfo;
import com.aditya.gmwp.aem.properties.BaseballcardCarlineProperties;
import com.aditya.gmwp.aem.services.vehicledata.data.Carline;
import com.aditya.gmwp.aem.services.vehicledata.data.CarlineBaseballcardData;
import com.aditya.gmwp.aem.services.vehicledata.data.VehicleData;
import com.aditya.gmwp.aem.utils.GenericSortableMultiGridUtil;
import com.aditya.gmwp.aem.wrapper.GMResource;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class CarlineUtil {

    /**
     * Enumeration for templates for which carline attributes are configured.
     * 
     * @author nperry, namics AG
     * @since GMWP Release 2.4
     */
    public enum CarlineAttributeResource {
        ATTRIBUTES_COMPONENT("baseballcard_carline_attributes"), FAMILY("family"), BASE_BALL_CARD("base_ball_card"), HELPME(
                "help_me");

        private String value;

        /**
         * Private constructor. Creates a new enum value.
         * 
         * @param value
         *            template name
         */
        private CarlineAttributeResource(final String value) {
            this.value = value;
        }

        /**
         * Returns the template name.
         * 
         * @return template name
         */
        public String value() {
            return this.value;
        }

    }

    /**
     * Model class for carline code information.
     * 
     * @author chauzenberger, namics AG
     * @since GMWP Release 2.4
     */
    public static final class CarlineCode {

        private final String carlineCode;

        private final String modelYear;

        private final String modelYearSuffix;

        /**
         * Creates a new carline code object.
         * 
         * @param carlineCode
         *            carline code
         * @param modelYear
         *            model year
         * @param modelYearSuffix
         *            model year suffix
         */
        private CarlineCode(final String carlineCode, final String modelYear, final String modelYearSuffix) {
            this.carlineCode = carlineCode;
            this.modelYear = modelYear;
            this.modelYearSuffix = modelYearSuffix;
        }

        /**
         * Returns the carline code.
         * 
         * @return carline code
         */
        public String getCarlineCode() {
            return this.carlineCode;
        }

        /**
         * Returns the model year.
         * 
         * @return model year
         */
        public String getModelYear() {
            return this.modelYear;
        }

        /**
         * Returns the model year suffix.
         * 
         * @return model year suffix
         */
        public String getModelYearSuffix() {
            return this.modelYearSuffix;
        }

    }

    private static final Logger LOG = LoggerFactory.getLogger(CarlineUtil.class);

    /**
     * Returns the carline specified by the carline page and vehicle data.
     * 
     * @param carlinePage
     *            carline page
     * @param vehicleData
     *            vehicle data
     * @return carline
     */
    public static Carline getCarline(final Page carlinePage,
                                     final VehicleData vehicleData) {

        if (vehicleData != null) {

            int modelYear = -1;
            final String modelYearString = getModelYear(carlinePage);
            if (StringUtils.isNotBlank(modelYearString)) {
                try {
                    modelYear = Integer.parseInt(modelYearString);
                } catch (final NumberFormatException e) {
                    LOG.warn("Model year is not a number: " + modelYearString);
                }
            }
            return vehicleData.getCarline(getCarlineCode(carlinePage), modelYear, getModelYearSuffix(carlinePage));
        }

        return null;
    }

    /**
     * Gets the carline code from a carline page.
     * 
     * @param carlinePage
     *            carline page
     * @return carline code
     */
    public static String getCarlineCode(final Page carlinePage) {
        return parseCarlineCodeProperty(carlinePage)[0];
    }

    /**
     * Returns the model year from a carline page.
     * 
     * @param carlinePage
     *            carline page
     * @return model year
     */
    public static String getModelYear(final Page carlinePage) {
        return parseCarlineCodeProperty(carlinePage)[1];
    }

    /**
     * Gets the model year suffix from a carline page.
     * 
     * @param carlinePage
     *            carline page
     * @return model year suffix
     */
    public static String getModelYearSuffix(final Page carlinePage) {
        return parseCarlineCodeProperty(carlinePage)[2];
    }

    /**
     * Gets the carline code from a carline properties.
     * 
     * @param carlineProperties
     *            carline properties
     * @return carline code
     */
    public static String getCarlineCode(final ValueMap carlineProperties) {
        return parseCarlineCodeProperty(carlineProperties)[0];
    }

    /**
     * Returns the model year from a carline properties.
     * 
     * @param carlineProperties
     *            carline properties
     * @return model year
     */
    public static String getModelYear(final ValueMap carlineProperties) {
        return parseCarlineCodeProperty(carlineProperties)[1];
    }

    /**
     * Gets the model year suffix from a carline properties.
     * 
     * @param carlineProperties
     *            carline properties
     * @return model year suffix
     */
    public static String getModelYearSuffix(final ValueMap carlineProperties) {
        return parseCarlineCodeProperty(carlineProperties)[2];
    }

    /**
     * Parses and splits the carline code property.
     * 
     * @param carlinePage
     *            carline page
     * @return split values
     */
    private static String[] parseCarlineCodeProperty(final Page carlinePage) {
        return null == carlinePage ? new String[] { null, null, null } : parseCarlineCodeProperty(carlinePage
                .getProperties("./baseballcard_carline"));
    }

    /**
     * Parses and splits the carline code property.
     * 
     * @param carlineProperties
     *            carline properties
     * @return split values
     */
    private static String[] parseCarlineCodeProperty(final ValueMap carlineProperties) {
        final String[] carlineCodeData = new String[] { null, null, null };

        if (null != carlineProperties) {
            final String code = carlineProperties.get(BaseballcardCarlineProperties.CARLINE_CODE.getPropertyName(),
                    StringUtils.EMPTY);
            if (StringUtils.isNotBlank(code)) {
                final String[] parts = code.split("~");
                carlineCodeData[0] = parts[0];
                if (parts.length > 1) {
                    carlineCodeData[1] = parts[1];
                }
                if (parts.length > 2) {
                    carlineCodeData[2] = parts[2];
                }
            }
        }

        return carlineCodeData;
    }

    /**
     * Parses the full carline code into carline code.
     * 
     * @param fullCarlineCode
     *            full carline code
     * @return first split value
     */
    public static String parseCarlineCode(final String fullCarlineCode) {
        return (StringUtils.isNotBlank(fullCarlineCode)) ? fullCarlineCode.split("~")[0] : "";
    }

    /**
     * Parses the carline code property.
     * 
     * @param carlinePage
     *            carline code property
     * @param whichResource
     *            defined which resource is allowed
     * @return split values
     */
    public static List<String> getAllowedCarlineAttributes(final Page carlinePage,
                                                           final CarlineAttributeResource whichResource) {
        if (carlinePage != null) {
            final Resource resource = carlinePage.getContentResource().getChild(CarlineAttributeResource.ATTRIBUTES_COMPONENT.value());
            return getAllowedCarlineAttributes(resource, whichResource.value);
        }
        return new ArrayList<String>();
    }

    /**
     * Returns allowed carline attributes of a resource.
     * 
     * @param resource
     *            resource that stores carline attributes
     * @param nodeName
     *            node that stores carline attributes
     * @return carline attributes
     */
    public static List<String> getAllowedCarlineAttributes(final Resource resource,
                                                           final String nodeName) {
        return GenericSortableMultiGridUtil.getSelectedIds(new GMResource(resource), nodeName);
    }

    /**
     * Returns the display title of a carline, based on the data of the carline page, bbc data and the information
     * whether the carline text should be displayed or not.
     * 
     * @param bbcData
     *            bbc data, containing data from ddp
     * @param carline
     *            data from carline page
     * @param showCarlineText
     *            if true, carline text is shown too
     * @return carline title
     */
    public static String getCarlineTitle(final CarlineBaseballcardData bbcData,
                                         final Carline carline,
                                         final boolean showCarlineText) {
        String title = bbcData.getBaseballCardTitle();
        if (carline != null && StringUtils.isNotBlank(carline.getTitle())) {
            title = carline.getTitle();
        }
        if (showCarlineText && StringUtils.isNotBlank(bbcData.getCarlineText())) {
            return bbcData.getCarlineText() + " " + title;
        } else {
            return title;
        }
    }

    /**
     * This method recursively searches for baseball-card carline pages from an entry point/page on.
     * 
     * @param carlinePages
     *            the list of found baseball-card carline pages
     * @param root
     *            the entry point/page to start searching for baseball-card carline pages
     * @param checkHideInNav
     *            whether to check if a page is hidden in navigation and thus not added to the list
     * @return list of baseball-card carline pages
     */
    private static List<Page> getBaseballCardCarlinePages(final List<Page> carlinePages,
                                                          final Page root,
                                                          final boolean checkHideInNav) {
        if (null != root) {
            final Iterator<Page> iterator = root.listChildren();
            while (iterator.hasNext()) {
                final Page child = iterator.next();

                if (null == child.getTemplate()) {
                    LOG.warn("Metadata for the following Page could not be retrieved: " + child.toString()
                            + ". This might indicate a broken jcr:content node");
                    continue;
                }

                if (!checkHideInNav || (checkHideInNav && !child.isHideInNav())) {
                    if (isBaseballCardNode(child)) {
                        getBaseballCardCarlinePages(carlinePages, child, checkHideInNav);
                    } else if (isCarlineTemplate(child)) {
                        carlinePages.add(child);
                    }
                }

            }
        }

        return carlinePages;
    }

    /**
     * This method recursively searches for baseball-card carline pages and returns them in a list. This function does
     * check whether any found baseball-card carline page is hidden in navigation and thus not adding them to the list.
     * 
     * @param root
     *            the page from where the search is started
     * @return list of pages
     */
    public static List<Page> getBaseballCardCarlinePages(final Page root) {
        return getBaseballCardCarlinePages(new ArrayList<Page>(), root, true);
    }

    /**
     * Returns whether a page is baseballcard node.
     * 
     * @param page
     *            the page
     * @return is baseballcard node
     */
    private static boolean isBaseballCardNode(final Page page) {
        return AEMTemplateInfo.TEMPLATE_BASEBALLCARD_NODE.matchesTemplate(page);
    }

    /**
     * Returns whether a page is carline template.
     * 
     * @param page
     *            the page
     * @return is carline template
     */
    private static boolean isCarlineTemplate(final Page page) {
        return AEMTemplateInfo.TEMPLATE_BASEBALLCARD_CARLINE.matchesTemplate(page);
    }

    /**
     * Private constructor to prevent instantiation of this class.
     */
    private CarlineUtil() {
        throw new AssertionError("This class is not ment to be instantiated.");
    }
}