/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.utils.vi;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.gmwp.aem.utils.ddp.DdpSsiIncludeType;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class VehiclePriceInformation extends VehicleInformation implements Serializable {
    private static final long serialVersionUID = -470614893539160882L;

    private static final Logger LOG = LoggerFactory.getLogger(VehiclePriceInformation.class);

    /**
     * Creates a new instance invoking copy constructor.
     * 
     * @param priceInfo
     *            the vehicle price information
     * @return vehicle price information
     */
    public static VehiclePriceInformation newInstance(final VehiclePriceInformation priceInfo) {
        return new VehiclePriceInformation(priceInfo);
    }

    /** the formatted fleet price from bbc bodystyle. */
    private String formattedFleetPrice;
    /** the formatted price from bbc bodystyle. */
    private String formattedPrice;
    private String formattedNetPrice;
    /** the legal price suffix maintained on bbc bodystyle. */
    private String legalPriceSuffix;
    private String netPriceSuffix;
    private String grossPriceSuffix;
    /** the manual price from bbc bodystyle. */
    private String manualPrice;
    private String manualNetPrice;
    /** overwrite ssi price with the either formatted price or formatted fleet price. */
    private boolean overwriteSsi;
    /** the price label e.g. "From". maintained on lslr. */
    private String priceLabel;
    /** whether the price label is placed to the right of the price. */
    private boolean priceLabelRightAligned;
    /** whether the formatted fleet price should be shown. */
    private boolean showFleetPrice;
    /** whether the price should be shown at all. */
    private boolean showPrice;
    /** the ssi include type. see <code>DdpSsiStatement</code> for available ones. */
    private DdpSsiIncludeType ssiIncludeType;
    /**
     * whether the prices are written out using javascript. see
     * <code>PriceTag getSsiJavascript()</code>.
     */
    private boolean useJsSsiInclude;
    private String overwrittenDdpPrice;
    /**
     * whether the overwritten price should be shown if "no selection" is selected from the
     * bodystyle list.
     */
    private boolean overwrittenPrice;

    private boolean useManualPrice;

    private boolean useManualNetPrice;

    private String formattedTotalPrice;

    private String formattedIncentivePrice;

    private String formattedDeliveryDate;

    private boolean useGrossAndNetPrice;

    public VehiclePriceInformation() {

    }

    /**
     * Copy constructor.
     * 
     * @param priceInfo
     *            the price info
     */
    public VehiclePriceInformation(final VehiclePriceInformation priceInfo) {
        super(priceInfo);
        this.formattedPrice = priceInfo.getFormattedPrice();
        this.formattedNetPrice = priceInfo.getFormattedNetPrice();
        this.formattedFleetPrice = priceInfo.getFormattedFleetPrice();
        this.formattedIncentivePrice = priceInfo.getFormattedIncentivePrice();
        this.formattedDeliveryDate = priceInfo.getFormattedDeliveryDate();
        this.formattedTotalPrice = priceInfo.getFormattedTotalPrice();
        this.legalPriceSuffix = priceInfo.getLegalPriceSuffix();
        this.manualPrice = priceInfo.getManualPrice();
        this.manualNetPrice = priceInfo.getManualNetPrice();
        this.overwriteSsi = priceInfo.getOverwriteSsi();
        this.priceLabel = priceInfo.getPriceLabel();
        this.priceLabelRightAligned = priceInfo.getPriceLabelRightAligned();
        this.showFleetPrice = priceInfo.getShowFleetPrice();
        this.showPrice = priceInfo.getShowPrice();
        this.ssiIncludeType = priceInfo.getSsiIncludeType();
        this.useJsSsiInclude = priceInfo.getUseJsSsiInclude();
        this.overwrittenDdpPrice = priceInfo.getOverwrittenDdpPrice();
    }

    public final String getOverwrittenDdpPrice() {
        return this.overwrittenDdpPrice;
    }

    /**
     * Returns whether ddp price is overwritten by value entered in richtext editor in
     * bbc_bodystyle_c1 component on the ts_baseballcard_bodystyle template.
     * 
     * @return is overwritten
     */
    public final boolean isDdpPriceOverwritten() {
        return StringUtils.isNotEmpty(getOverwrittenDdpPrice());
    }

    public final String getFormattedFleetPrice() {
        return this.formattedFleetPrice;
    }

    public final String getFormattedPrice() {
        return this.formattedPrice;
    }

    public final String getFormattedNetPrice() {
        return this.formattedNetPrice;
    }

    public final String getLegalPriceSuffix() {
        return this.legalPriceSuffix;
    }

    public final String getNetPriceSuffix() {
        return this.netPriceSuffix;
    }

    public final String getGrossPriceSuffix() {
        return this.grossPriceSuffix;
    }

    public final String getManualPrice() {
        return this.manualPrice;
    }

    public final String getManualNetPrice() {
        return this.manualNetPrice;
    }

    public final boolean getOverwriteSsi() {
        return this.overwriteSsi;
    }

    public final String getPriceLabel() {
        return this.priceLabel;
    }

    public final boolean getPriceLabelRightAligned() {
        return this.priceLabelRightAligned;
    }

    public final boolean getShowFleetPrice() {
        return this.showFleetPrice;
    }

    public final boolean getShowPrice() {
        return this.showPrice;
    }

    public final DdpSsiIncludeType getSsiIncludeType() {
        return this.ssiIncludeType;
    }

    public void setUseGrossAndNetPrice(final boolean useGrossAndNetPrice) {
        this.useGrossAndNetPrice = useGrossAndNetPrice;
    }

    public boolean getUseGrossAndNetPrice() {
        return this.useGrossAndNetPrice;
    }

    /**
     * Returns true for fallback BBCs or if the DDP/SSI prices should be overwritten.
     * 
     * @return whether a manual overwrite price is or should be present
     */
    public boolean hasManualOverwritePrice() {
        return getIsFallbackBaseballCard() || isDdpPriceOverwritten() || getOverwriteSsi() || this.useManualPrice || this.overwrittenPrice;
    }

    public boolean hasManualOverwriteNetPrice() {
        return this.useManualNetPrice;
    }

    public final boolean getUseJsSsiInclude() {
        return this.useJsSsiInclude;
    }

    /**
     * Returns the manual price for fallback BBCs, the overwritten DDP price if present, or the
     * appropriate formatted price.
     * 
     * @return the manual price
     */
    public String getManualOverwritePrice() {
        if (getIsFallbackBaseballCard() || this.useManualPrice) {
            return getManualPrice();
        } else if (isDdpPriceOverwritten()) {
            return getOverwrittenDdpPrice();
        } else {
            return getShowFleetPrice() ? getFormattedFleetPrice() : getFormattedPrice();
        }
    }

    public String getManualOverwriteNetPrice() {
        return getManualNetPrice();
    }

    public final String getFormattedIncentivePrice() {
        return this.formattedIncentivePrice;
    }

    public final String getFormattedDeliveryDate() {
        return this.formattedDeliveryDate;
    }

    public final String getFormattedTotalPrice() {
        return this.formattedTotalPrice;
    }

    /**
     * Calculates the price after incentives had been accounted for.
     * 
     * @param locale
     *            the locale
     * @return the final total price
     */
    public final String getFormattedTotalPriceAfterIncentives(final Locale locale) {

        Locale currentLocale = locale;
        if (currentLocale == null) {
            currentLocale = Locale.getDefault();
        }

        final String msrpPrice = getManualOverwritePrice();
        final NumberFormat currencyInstance = NumberFormat.getCurrencyInstance(currentLocale);

        if (StringUtils.isNotBlank(this.formattedIncentivePrice)) {
            try {
                final Number msrp = currencyInstance.parse(msrpPrice);
                final Number incentive = currencyInstance.parse(this.formattedIncentivePrice);
                if (msrp instanceof Long && incentive instanceof Long) {
                    final Long msrpLong = (Long) msrp;
                    final Long incentiveLong = (Long) incentive;
                    final Long value = msrpLong - incentiveLong;
                    this.formattedTotalPrice = currencyInstance.format(value);
                } else {
                    this.formattedTotalPrice = msrpPrice;
                }
            } catch (Exception e) {
                LOG.warn("Error occurred while processing msrp and incentive prices for " + getCarlineCode() + ":" + getBodystyleCode() + ":" + getSeriesCode()
                        + ".");
                this.formattedTotalPrice = msrpPrice;
            }
        } else {
            this.formattedTotalPrice = msrpPrice;
        }

        if (StringUtils.isNotBlank(this.formattedTotalPrice)) {
            final int idx = StringUtils.indexOf(this.formattedTotalPrice, '.');
            if (idx != StringUtils.INDEX_NOT_FOUND) {
                this.formattedTotalPrice = StringUtils.substring(this.formattedTotalPrice, 0, idx);
            }
        }

        return this.formattedTotalPrice;
    }

    public final void setOverwrittenDdpPrice(final String overwrittenDdpPrice) {
        this.overwrittenDdpPrice = overwrittenDdpPrice;
    }

    public final void setFormattedFleetPrice(final String formattedFleetPrice) {
        this.formattedFleetPrice = formattedFleetPrice;
    }

    public final void setFormattedPrice(final String formattedPrice) {
        this.formattedPrice = formattedPrice;
    }

    public final void setFormattedNetPrice(final String formattedNetPrice) {
        this.formattedNetPrice = formattedNetPrice;
    }

    public final void setLegalPriceSuffix(final String legalPriceSuffix) {
        this.legalPriceSuffix = legalPriceSuffix;
    }

    public final void setNetPriceSuffix(final String netPriceSuffix) {
        this.netPriceSuffix = netPriceSuffix;
    }

    public final void setGrossPriceSuffix(final String grossPriceSuffix) {
        this.grossPriceSuffix = grossPriceSuffix;
    }

    public final void setManualPrice(final String manualPrice) {
        this.manualPrice = manualPrice;
    }

    public final void setManualNetPrice(final String manualNetPrice) {
        this.manualNetPrice = manualNetPrice;
    }

    public final void setOverwriteSsi(final boolean overwriteSsi) {
        this.overwriteSsi = overwriteSsi;
    }

    public final void setPriceLabel(final String priceLabel) {
        this.priceLabel = priceLabel;
    }

    public final void setPriceLabelRightAligned(final boolean priceLabelRightAligned) {
        this.priceLabelRightAligned = priceLabelRightAligned;
    }

    public final void setShowFleetPrice(final boolean showFleetPrice) {
        this.showFleetPrice = showFleetPrice;
    }

    public final void setShowPrice(final boolean showPrice) {
        this.showPrice = showPrice;
    }

    public final void setSsiIncludeType(final DdpSsiIncludeType ssiIncludeType) {
        this.ssiIncludeType = ssiIncludeType;
    }

    public final void setUseJsSsiInclude(final boolean useJsSsiInclude) {
        this.useJsSsiInclude = useJsSsiInclude;
    }

    /**
     * Use manual price.
     * <p>
     * This method forces the usage of the manual price. It overwrites all other settings.
     * </p>
     */
    public void useManualPrice() {
        this.useManualPrice = true;
    }

    /**
     * Use manual net price.
     * <p>
     * This method forces the usage of the manual net price. It overwrites all other settings.
     * </p>
     */
    public void useManualNetPrice() {
        this.useManualNetPrice = true;
    }

    /**
     * Sets the formatted incentive price.
     * 
     * @param formattedIncentivePrice
     *            the new formatted incentive price
     */
    public final void setFormattedIncentivePrice(final String formattedIncentivePrice) {
        LOG.debug("Setting formatted incentive price to " + formattedIncentivePrice);
        this.formattedIncentivePrice = formattedIncentivePrice;
    }

    public final void setFormattedDeliveryDate(final String formattedDeliveryDate) {
        this.formattedDeliveryDate = formattedDeliveryDate;
    }

    /**
     * Checks if is overwritten price.
     * 
     * @return the overwrittenPrice
     */
    public boolean isOverwrittenPrice() {
        return this.overwrittenPrice;
    }

    public void setOverwrittenPrice(final boolean overwrittenPrice) {
        this.overwrittenPrice = overwrittenPrice;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString() {
        return "VehiclePriceInformation [formattedFleetPrice=" + this.formattedFleetPrice + ", formattedPrice=" + this.formattedPrice + ", legalPriceSuffix="
                + this.legalPriceSuffix + ", manualPrice=" + this.manualPrice + ", overwriteSsi=" + this.overwriteSsi + ", priceLabel=" + this.priceLabel
                + ", priceLabelRightAligned=" + this.priceLabelRightAligned + ", showFleetPrice=" + this.showFleetPrice + ", showPrice=" + this.showPrice
                + ", ssiIncludeType=" + this.ssiIncludeType + ", useJsSsiInclude=" + this.useJsSsiInclude + ", overwrittenDdpPrice=" + this.overwrittenDdpPrice
                + ", getBodystyleCode()=" + getBodystyleCode() + ", getCarlineCode()=" + getCarlineCode() + ", getConfigCode()=" + getConfigCode()
                + ", getIsFallbackBaseballCard()=" + getIsFallbackBaseballCard() + ", getModelYear()=" + getModelYear() + ", getModelYearSuffix()="
                + getModelYearSuffix() + ", getSeriesCode()=" + getSeriesCode() + ", getFormattedIncentivePrice() = " + getFormattedIncentivePrice()
                + ", getFormattedDeliveryDate() = " + getFormattedDeliveryDate() + "]";
    }
}
