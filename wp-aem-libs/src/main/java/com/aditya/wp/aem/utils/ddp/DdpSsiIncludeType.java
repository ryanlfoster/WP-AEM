/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.utils.ddp;

import java.util.Arrays;
import java.util.List;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public enum DdpSsiIncludeType {
    COLORS("colors"), //
    DISCLAIMER("disclaimer"), //
    FLEET_PRICE("fleet_price"), //
    FS_CATEGORIES("fs-categories"), //
    FSTABLE_CAPABILITIES("fstable-capabilities"), //
    FSTABLE_CAPABILITIES_EXPANDER("fstable-capabilities-exp"), //
    FSTABLE_DIMENSIONS("fstable-dimensions"), //
    FSTABLE_DIMENSIONS_EXPANDER("fstable-dimensions-exp"), //
    FSTABLE_ENGINES("fstable-engines"), //
    FSTABLE_ENGINES_EXPANDER("fstable-engines-exp"), //
    FSTABLE_OPTIONS("fstable-options"), //
    FSTABLE_OPTIONS_EXPANDER("fstable-options-exp"), //
    LONG_DESCRIPTION("long-descr"), //
    PRICE("price"), //
    PRICES("prices"), //
    NET_PRICE("price_net"), //
    ;

    private static final List<DdpSsiIncludeType> JSON_TYPES = Arrays.asList(COLORS, PRICES, FS_CATEGORIES);
    private static final List<DdpSsiIncludeType> ALLOW_SERIES_CODE = Arrays.asList(PRICE, NET_PRICE, FLEET_PRICE, LONG_DESCRIPTION, DISCLAIMER);

    private String name;

    private DdpSsiIncludeType(final String name) {
        this.name = name;
    }

    /**
     * Returns the name of the associated DDP artifact.
     * 
     * @return the artifact name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the data type of the associated DDP artifact.
     * 
     * @return "html" for most, but "json" for some include types.
     */
    public String getDataType() {
        return JSON_TYPES.contains(this) ? "json" : "html";
    }

    /**
     * Whether this include type requires a carline code in the SSI directive.
     * 
     * @return true if a carline code is required.
     */
    public boolean requiresCarlineCode() {
        // only PRICES does not require the carline code
        return !equals(PRICES);
    }

    /**
     * Whether this include type requires a bodystyle code in the SSI directive.
     * 
     * @return true if a bodystyle code is required.
     */
    public boolean requiresBodystyleCode() {
        // if a carline code is required, a bodystyle code is also required
        return requiresCarlineCode();
    }

    /**
     * Whether this include type allows a series code in the SSI directive.
     * 
     * @return true if a series code is allowed.
     */
    public boolean allowsSeriesCode() {
        return ALLOW_SERIES_CODE.contains(this);
    }

    /**
     * Returns the enum value for a given string value, if it exists.
     * 
     * @param value
     *            the "include type" value to resolve
     * @return the corresponding enum value if exists.
     * @throws an
     * @{@link IllegalArgumentException} if no matching value found.
     */
    public static DdpSsiIncludeType fromString(final String value) {
        for (DdpSsiIncludeType enumValue : values()) {
            if (enumValue.getName().equalsIgnoreCase(value)) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("Invalid include type: \"" + value + "\"! Possible values are " + Arrays.toString(values()));
    }
}
