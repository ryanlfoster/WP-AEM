/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.baseballcard.model;

import java.io.Serializable;
import java.util.List;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class BaseballcardInfoItem implements Serializable {

    /** unique identifier */
    private static final long serialVersionUID = 1L;

    private String carlineCode;
    private String carlineText;
    private String familyLink;
    private String modelYear;
    private String modelYearSuffix;
    private String imageReferenceCarline;
    private String bbcThumbnailUrl;
    private String vehicleNavigationLabel;
    private String showCarlineText;
    private String legalPriceSuffixBbcN02b;
    private String legalPriceSuffixBbcN01;
    private String ddpPriceOverwrite;
    private String bbcConfiguration;
    private String bodyStyleCode;
    private String modeloverviewInternalLink;
    private String buildYourOwnLink;
    private String searchInventoryLink;
    private String comparetrimsInternalLink;
    private String competitivePhotoComparsionExternalLink;
    private String imageReferenceLarge;
    private String imageReferenceMedium;
    private String imageReferenceSmall;
    private String imageReferenceCustom;
    private String manualFreightChargePrice;
    private String price;
    private String pricePrefix;
    private String manualTotalPrice;
    private List<String> carlineResourcePaths;
    private List<String> bodystyleResourcePaths;
    private List<String> bodystyleManualResourcePaths;
    private List<String> bodystyleConfigurationResourcePaths;
    private String bbc_configuration_thumbnail_small;

    public String getCarlineCode() {
        return this.carlineCode;
    }

    public void setCarlineCode(final String carlineCode) {
        this.carlineCode = carlineCode;
    }

    public String getCarlineText() {
        return this.carlineText;
    }

    public void setCarlineText(final String carlineText) {
        this.carlineText = carlineText;
    }

    public String getModelYear() {
        return this.modelYear;
    }

    public void setModelYear(final String modelYear) {
        this.modelYear = modelYear;
    }

    public String getModelYearSuffix() {
        return this.modelYearSuffix;
    }

    public void setModelYearSuffix(final String modelYearSuffix) {
        this.modelYearSuffix = modelYearSuffix;
    }

    public String getImageReferenceCarline() {
        return this.imageReferenceCarline;
    }

    public void setImageReferenceCarline(final String imageReferenceCarline) {
        this.imageReferenceCarline = imageReferenceCarline;
    }

    public String getBbcThumbnailUrl() {
        return this.bbcThumbnailUrl;
    }

    public void setBbcThumbnailUrl(final String bbcThumbnailUrl) {
        this.bbcThumbnailUrl = bbcThumbnailUrl;
    }

    public String getVehicleNavigationLabel() {
        return this.vehicleNavigationLabel;
    }

    public void setVehicleNavigationLabel(final String vehicleNavigationLabel) {
        this.vehicleNavigationLabel = vehicleNavigationLabel;
    }

    public String getShowCarlineText() {
        return this.showCarlineText;
    }

    public void setShowCarlineText(final String showCarlineText) {
        this.showCarlineText = showCarlineText;
    }

    public String getBbcConfiguration() {
        return this.bbcConfiguration;
    }

    public void setBbcConfiguration(final String bbcConfiguration) {
        this.bbcConfiguration = bbcConfiguration;
    }

    public String getBodyStyleCode() {
        return this.bodyStyleCode;
    }

    public void setBodyStyleCode(final String bodyStyleCode) {
        this.bodyStyleCode = bodyStyleCode;
    }

    public String getFamilyLink() {
        return this.familyLink;
    }

    public void setFamilyLink(final String familyLink) {
        this.familyLink = familyLink;
    }

    public String getLegalPriceSuffixBbcN02b() {
        return this.legalPriceSuffixBbcN02b;
    }

    public void setLegalPriceSuffixBbcN02b(final String legalPriceSuffixBbcN02b) {
        this.legalPriceSuffixBbcN02b = legalPriceSuffixBbcN02b;
    }

    public String getLegalPriceSuffixBbcN01() {
        return this.legalPriceSuffixBbcN01;
    }

    public void setLegalPriceSuffixBbcN01(final String legalPriceSuffixBbcN01) {
        this.legalPriceSuffixBbcN01 = legalPriceSuffixBbcN01;
    }

    public String getDdpPriceOverwrite() {
        return this.ddpPriceOverwrite;
    }

    public void setDdpPriceOverwrite(final String ddpPriceOverwrite) {
        this.ddpPriceOverwrite = ddpPriceOverwrite;
    }

    public String getModeloverviewInternalLink() {
        return this.modeloverviewInternalLink;
    }

    public void setModeloverviewInternalLink(final String modeloverviewInternalLink) {
        this.modeloverviewInternalLink = modeloverviewInternalLink;
    }

    public String getBuildYourOwnLink() {
        return this.buildYourOwnLink;
    }

    public void setBuildYourOwnLink(final String buildYourOwnLink) {
        this.buildYourOwnLink = buildYourOwnLink;
    }

    public String getSearchInventoryLink() {
        return this.searchInventoryLink;
    }

    public void setSearchInventoryLink(final String searchInventoryLink) {
        this.searchInventoryLink = searchInventoryLink;
    }

    public String getComparetrimsInternalLink() {
        return this.comparetrimsInternalLink;
    }

    public void setComparetrimsInternalLink(final String comparetrimsInternalLink) {
        this.comparetrimsInternalLink = comparetrimsInternalLink;
    }

    public String getCompetitivePhotoComparsionExternalLink() {
        return this.competitivePhotoComparsionExternalLink;
    }

    public void setCompetitivePhotoComparsionExternalLink(final String competitivePhotoComparsionExternalLink) {
        this.competitivePhotoComparsionExternalLink = competitivePhotoComparsionExternalLink;
    }

    public String getImageReferenceLarge() {
        return this.imageReferenceLarge;
    }

    public void setImageReferenceLarge(final String imageReferenceLarge) {
        this.imageReferenceLarge = imageReferenceLarge;
    }

    public String getImageReferenceMedium() {
        return this.imageReferenceMedium;
    }

    public void setImageReferenceMedium(final String imageReferenceMedium) {
        this.imageReferenceMedium = imageReferenceMedium;
    }

    public String getImageReferenceSmall() {
        return this.imageReferenceSmall;
    }

    public void setImageReferenceSmall(final String imageReferenceSmall) {
        this.imageReferenceSmall = imageReferenceSmall;
    }

    public String getImageReferenceCustom() {
        return this.imageReferenceCustom;
    }

    public void setImageReferenceCustom(final String imageReferenceCustom) {
        this.imageReferenceCustom = imageReferenceCustom;
    }

    public String getManualFreightChargePrice() {
        return this.manualFreightChargePrice;
    }

    public void setManualFreightChargePrice(final String manualFreightChargePrice) {
        this.manualFreightChargePrice = manualFreightChargePrice;
    }

    public String getPrice() {
        return this.price;
    }

    public void setPrice(final String price) {
        this.price = price;
    }

    public String getPricePrefix() {
        return this.pricePrefix;
    }

    public void setPricePrefix(final String pricePrefix) {
        this.pricePrefix = pricePrefix;
    }

    public String getManualTotalPrice() {
        return this.manualTotalPrice;
    }

    public void setManualTotalPrice(final String manualTotalPrice) {
        this.manualTotalPrice = manualTotalPrice;
    }

    public List<String> getCarlineResourcePaths() {
        return this.carlineResourcePaths;
    }

    public void setCarlineResourcePaths(final List<String> carlineResourcePaths) {
        this.carlineResourcePaths = carlineResourcePaths;
    }

    public List<String> getBodystyleResourcePaths() {
        return this.bodystyleResourcePaths;
    }

    public void setBodystyleResourcePaths(final List<String> bodystyleResourcePaths) {
        this.bodystyleResourcePaths = bodystyleResourcePaths;
    }

    public List<String> getBodystyleManualResourcePaths() {
        return this.bodystyleManualResourcePaths;
    }

    public void setBodystyleManualResourcePaths(final List<String> bodystyleManualResourcePaths) {
        this.bodystyleManualResourcePaths = bodystyleManualResourcePaths;
    }

    public List<String> getBodystyleConfigurationResourcePaths() {
        return this.bodystyleConfigurationResourcePaths;
    }

    public void setBodystyleConfigurationResourcePaths(final List<String> bodystyleConfigurationResourcePaths) {
        this.bodystyleConfigurationResourcePaths = bodystyleConfigurationResourcePaths;
    }

    public String getBbc_configuration_thumbnail_small() {
        return this.bbc_configuration_thumbnail_small;
    }

    public void setBbc_configuration_thumbnail_small(final String bbc_configuration_thumbnail_small) {
        this.bbc_configuration_thumbnail_small = bbc_configuration_thumbnail_small;
    }
}
