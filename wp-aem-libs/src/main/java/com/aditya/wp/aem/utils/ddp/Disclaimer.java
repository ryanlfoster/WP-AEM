/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.utils.ddp;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class Disclaimer {

    private final String bodystyleCode;

    private final String carlineCode;

    private final String modelYear;

    private final String referenceSymbol;

    private final String seriesCode;

    /**
     * Creates a new instance.
     * 
     * @param referenceSymbol
     *            the reference symbol
     * @param modelYear
     *            the model year
     * @param carlineCode
     *            the carline code
     * @param bodystyleCode
     *            the body-style code
     * @param seriesCode
     *            the series code
     */
    Disclaimer(final String referenceSymbol, final String modelYear, final String carlineCode,
            final String bodystyleCode, final String seriesCode) {
        this.referenceSymbol = referenceSymbol;
        this.modelYear = modelYear;
        this.carlineCode = carlineCode;
        this.bodystyleCode = bodystyleCode;
        this.seriesCode = seriesCode;
    }

    /**
     * Gets the bodystyle code.
     * 
     * @return the bodystyle code
     */
    public String getBodystyleCode() {
        return this.bodystyleCode;
    }

    /**
     * Gets the carline code.
     * 
     * @return the carline code
     */
    public String getCarlineCode() {
        return this.carlineCode;
    }

    /**
     * Gets the model year.
     * 
     * @return the model year
     */
    public String getModelYear() {
        return this.modelYear;
    }

    /**
     * Gets the reference symbol.
     * 
     * @return the reference symbol
     */
    public String getReferenceSymbol() {
        return this.referenceSymbol;
    }

    /**
     * Gets the series code.
     * 
     * @return the series code
     */
    public String getSeriesCode() {
        return this.seriesCode;
    }
}
