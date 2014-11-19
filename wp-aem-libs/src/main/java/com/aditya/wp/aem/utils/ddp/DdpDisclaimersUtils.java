/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.utils.ddp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;

import com.aditya.gmwp.aem.global.GmdsRequestAttribute;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class DdpDisclaimersUtils {

    /**
     * Implementation of DdpDisclaimerRequestData.
     */
    static class DdpDisclaimerRequestDataImpl implements DdpDisclaimerRequestData {

        private String automaticNumberingInitialValue;

        private boolean automaticNumberingUseNumeric;

        private int automaticNumberungCurrentNumericValue;

        private String automaticNumberungCurrentStringValue;

        private final boolean ddpDisclaimersEnabled;

        private final List<Disclaimer> disclaimers;

        private final boolean putInParentheses;

        private final boolean useAutomaticNumbering;

        /**
         * Creates a new instance that is set to "not enabled".
         */
        DdpDisclaimerRequestDataImpl() {
            this.ddpDisclaimersEnabled = false;
            this.useAutomaticNumbering = false;
            this.putInParentheses = false;
            this.disclaimers = Collections.<Disclaimer> emptyList();
        }

        /**
         * Creates a new instance that initializes itself from the properties of the given value map.
         * 
         * @param resourceProperties
         *            the value map for the resource representing the pricelegallinks component.
         */
        DdpDisclaimerRequestDataImpl(final ValueMap resourceProperties) {
            if (null != resourceProperties) {

                // Whether enabled at all:
                this.ddpDisclaimersEnabled = "true".equals(resourceProperties.get("include_ddp_disclaimers",
                        String.class));

                // Whether to use automatic numbering or fetch reference symbols from BBCs:
                final String help = resourceProperties.get("reference_symbols", String.class);
                if ("bbc".equals(help)) {
                    this.useAutomaticNumbering = false;
                } else {
                    this.useAutomaticNumbering = true;
                }

                // The initial value for automatic numbering:
                this.automaticNumberingInitialValue = resourceProperties.get("reference_symbols_start_with",
                        String.class);
                if (StringUtils.isBlank(this.automaticNumberingInitialValue)) {
                    this.automaticNumberingInitialValue = AUTOMATIC_NUMBERING_DEFAULT_INITIAL_VALUE;
                }
                if (this.automaticNumberingInitialValue.length() > 1) {
                    // This should never happen because the maintenance dialog contains an according regex...
                    this.automaticNumberingInitialValue = this.automaticNumberingInitialValue.substring(0, 1);
                }
                final char c = this.automaticNumberingInitialValue.charAt(0);
                this.automaticNumberingUseNumeric = Character.getType(c) == Character.DECIMAL_DIGIT_NUMBER;
                if (this.automaticNumberingUseNumeric) {
                    this.automaticNumberungCurrentNumericValue = Integer.parseInt(this.automaticNumberingInitialValue);
                } else {
                    this.automaticNumberungCurrentStringValue = this.automaticNumberingInitialValue;
                }

                // Whether to put the reference symbols in parentheses:
                this.putInParentheses = "true".equals(resourceProperties.get("put_in_parentheses", String.class));

                this.disclaimers = new ArrayList<Disclaimer>();

            } else {
                this.ddpDisclaimersEnabled = false;
                this.useAutomaticNumbering = false;
                this.putInParentheses = false;
                this.disclaimers = Collections.<Disclaimer> emptyList();
            }
        }

        /*
         * (non-Javadoc)
         * @see com.gm.gssm.gmds.cq.utils.ddp.DdpDisclaimerRequestData#addDisclaimer(com.gm.gssm.gmds.cq.utils.ddp.
         * DdpDisclaimersUtils.Disclaimer)
         */
        @Override
        public void addDisclaimer(final Disclaimer disclaimer) {
            this.disclaimers.add(disclaimer);
        }

        /*
         * (non-Javadoc)
         * @see com.gm.gssm.gmds.cq.utils.ddp.DdpDisclaimerRequestData#getAllDisclaimers()
         */
        @Override
        public List<Disclaimer> getAllDisclaimers() {
            return this.disclaimers;
        }

        /*
         * (non-Javadoc)
         * @see com.gm.gssm.gmds.cq.utils.ddp.DdpDisclaimerRequestData#getDoIncludeDdpDisclaimers()
         */
        @Override
        public boolean getDoIncludeDdpDisclaimers() {
            return this.ddpDisclaimersEnabled;
        }

        /*
         * (non-Javadoc)
         * @see com.gm.gssm.gmds.cq.utils.ddp.DdpDisclaimerRequestData#getNextAutomaticNumberingSymbol()
         */
        @Override
        public String getNextAutomaticNumberingSymbol() {
            String symbol = "";

            if (!this.useAutomaticNumbering) {
                return symbol;
            }

            if (this.automaticNumberingUseNumeric) {
                symbol = "" + this.automaticNumberungCurrentNumericValue;
                this.automaticNumberungCurrentNumericValue++;
            } else {
                symbol = "" + this.automaticNumberungCurrentStringValue;
                this.automaticNumberungCurrentStringValue = incrementStringValue(//
                this.automaticNumberungCurrentStringValue);
            }

            if (this.putInParentheses) {
                symbol = "(" + symbol + ")";
            }
            return symbol;
        }

        /*
         * (non-Javadoc)
         * @see com.gm.gssm.gmds.cq.utils.ddp.DdpDisclaimerRequestData#getUseAutomaticNumbering()
         */
        @Override
        public boolean getUseAutomaticNumbering() {
            return this.useAutomaticNumbering;
        }

        /**
         * "Increments" a string value by replacing the last character in the string with the next alphabetic character.
         * If 'z' is reached, another character is appended to the string, starting with 'a'.
         * 
         * @param s
         *            the string to be "incremented"
         * @return see above.
         */
        private String incrementStringValue(final String s) {
            char lastChar = s.charAt(s.length() - 1);
            if (lastChar == 'z') {
                return s.substring(0, s.length() - 1) + "aa";
            } else if (lastChar == 'Z') {
                return s.substring(0, s.length() - 1) + "AA";
            } else {
                lastChar++;
                return s.substring(0, s.length() - 1) + lastChar;
            }
        }
    }

    /**
     * Returns whether rendering of DDP disclaimers has been enabled on the pricelegallinks-component on the current
     * page.
     * 
     * @param currentPage
     *            the current page
     * @param request
     *            the request
     * @return whether enabled.
     */
    public static boolean areDdpDisclaimersEnabled(final Page currentPage,
                                                   final HttpServletRequest request) {

        final DdpDisclaimerRequestData requestData = getDdpDisclaimerRequestDataFailsave(currentPage, request);
        return requestData.getDoIncludeDdpDisclaimers();
    }

    /**
     * Returns a DdpDisclaimerRequestData object by taking it from the request or by creating a new one.
     * 
     * @param currentPage
     *            the current page
     * @param request
     *            the request
     * @return see above
     */
    private static DdpDisclaimerRequestData getDdpDisclaimerRequestDataFailsave(final Page currentPage,
                                                                                final HttpServletRequest request) {
        DdpDisclaimerRequestData requestData = (DdpDisclaimerRequestData) GmdsRequestAttribute.DDP_DISCLAIMER_REQUEST_DATA.get(request);
        if (null == requestData) {
            requestData = initDdpDisclaimerRequestData(currentPage);
            GmdsRequestAttribute.DDP_DISCLAIMER_REQUEST_DATA.set(request, requestData);
        }
        return requestData;
    }

    /**
     * All disclaimers that have been added using the method @link.
     * 
     * @param currentPage
     *            the current page
     * @param request
     *            the request
     * @return see above {@link DdpDisclaimersUtils#prepareNewDdpDisclaimer(String, String, String, String, String)}.
     */
    public static List<Disclaimer> getDdpDisclaimers(final Page currentPage,
                                                     final HttpServletRequest request) {
        final DdpDisclaimerRequestData requestData = getDdpDisclaimerRequestDataFailsave(currentPage, request);
        return requestData.getAllDisclaimers();
    }

    /**
     * Generates and returns the next reference symbol when automatic numbering is used.
     * 
     * @param currentPage
     *            the current page
     * @param request
     *            the request
     * @return see above
     */
    public static String getNextAutomaticReferenceSymbol(final Page currentPage,
                                                         final SlingHttpServletRequest request) {
        final DdpDisclaimerRequestData requestData = getDdpDisclaimerRequestDataFailsave(currentPage, request);
        return requestData.getNextAutomaticNumberingSymbol();
    }

    /**
     * Initializes the DDP-disclaimer data object that is stored in the request.
     * 
     * @param currentPage
     *            the current page
     * @return an object containing all relevant data for DDP disclaimers.
     */
    @SuppressWarnings("deprecation")
    private static DdpDisclaimerRequestData initDdpDisclaimerRequestData(final Page currentPage) {

        final Resource priceLegalLinksRes = currentPage.getContentResource("pricelegallinks");
        if (null != priceLegalLinksRes) {
            final ValueMap properties = ResourceUtil.getValueMap(priceLegalLinksRes);
            if (null != properties) {
                return new DdpDisclaimerRequestDataImpl(properties);
            }
        }
        return new DdpDisclaimerRequestDataImpl(); // empty and inactive.
    }

    /**
     * Prepares a new DDP disclaimer to be output in a later phase of page rendering. * @param currentPage the current
     * page
     * 
     * @param currentPage
     *            the current page
     * @param request
     *            the request
     * @param referenceSymbol
     *            the reference symbol / prefix to be displayed in front of the disclaimer text
     * @param modelYear
     *            the model year of the car for which the disclaimer is rendered, may be null.
     * @param carlineCode
     *            the carline-code of the car for which the disclaimer is rendered, must not be null.
     * @param bodystyleCode
     *            the bodystyle-code of the car for which the disclaimer is rendered, must not be null.
     * @param seriesCode
     *            the series-code of the car for which the disclaimer is rendered, may be null.
     */
    public static void prepareNewDdpDisclaimer(final Page currentPage,
                                               final HttpServletRequest request,
                                               final String referenceSymbol,
                                               final String modelYear,
                                               final String carlineCode,
                                               final String bodystyleCode,
                                               final String seriesCode) {
        final DdpDisclaimerRequestData requestData = getDdpDisclaimerRequestDataFailsave(currentPage, request);
        requestData.addDisclaimer(new Disclaimer(referenceSymbol, modelYear, carlineCode, bodystyleCode, seriesCode));
    }

    /**
     * Whether the reference symbols used for disclaimers are created automatically or taken from the baseball-card.
     * 
     * @param currentPage
     *            the current page
     * @param request
     *            the request
     * @return see above
     */
    public static boolean useAutomaticReferenceSymbols(final Page currentPage,
                                                       final HttpServletRequest request) {
        final DdpDisclaimerRequestData requestData = getDdpDisclaimerRequestDataFailsave(currentPage, request);
        return requestData.getDoIncludeDdpDisclaimers() && requestData.getUseAutomaticNumbering();
    }

    /**
     * private constructor, this class provides only static methods.
     */
    private DdpDisclaimersUtils() {
        throw new AssertionError("This class is not ment to be instantiated.");
    }
}
