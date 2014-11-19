/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.baseballcard.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.aditya.wp.aem.global.AEMTemplateInfo;
import com.aditya.wp.aem.properties.BaseballcardBodystyleProperties;
import com.aditya.wp.aem.properties.BaseballcardCarlineProperties;
import com.aditya.wp.aem.properties.BaseballcardConfigurationProperties;
import com.aditya.wp.aem.services.baseballcard.model.BaseballcardInfoItem;
import com.aditya.wp.aem.services.baseballcard.model.BaseballcardInfoModel;
import com.aditya.wp.aem.services.core.QueryService;
import com.aditya.wp.aem.services.core.ServiceProvider;
import com.aditya.wp.aem.wrapper.GMResource;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class BaseballcardInfoModelImpl implements BaseballcardInfoModel {

    private static final String AEM_TEMPLATE = "cq:template";
    private static final String TILDA = "~";
    private static final String BASEBALLCARD_CARLINE = "baseballcard_carline";
    private static final String BASEBALLCARD_BODYSTYLE = "baseballcard_bodystyle";
    private static final String BASEBALLCARD_CONFIGURATION = "bbc_configuration";
    private static final String JCR_CONTENT = "jcr:content";

    private Map<String, BaseballcardInfoItem> bbCardItemMap;
    private Set<String> carlineResourcePaths;
    private Set<String> bodystyleResourcePaths;
    private Set<String> bodystyleManualResourcePaths;
    private Set<String> bodystyleConfigurationResourcePaths;

    @Override
    public void update(final Page lslrPage) {

        final QueryService queryService = ServiceProvider.INSTANCE.getService(QueryService.class);
        this.bbCardItemMap = new HashMap<String, BaseballcardInfoItem>();

        this.carlineResourcePaths = new HashSet<String>();
        this.bodystyleResourcePaths = new HashSet<String>();
        this.bodystyleManualResourcePaths = new HashSet<String>();
        this.bodystyleConfigurationResourcePaths = new HashSet<String>();

        updateCarlineInfo(queryService, this.bbCardItemMap, lslrPage);

    }

    @Override
    public Set<String> getBodystyleResourcePaths() {
        return this.bodystyleResourcePaths;
    }

    @Override
    public Set<String> getCarlineResourcePaths() {
        return this.carlineResourcePaths;
    }

    @Override
    public Set<String> getConfigurationResourcePaths() {
        return this.bodystyleConfigurationResourcePaths;
    }

    @Override
    public Set<String> getManualBodystyleResourcePaths() {
        return this.bodystyleManualResourcePaths;
    }

    /**
     * @param carlineCode
     * @param modelYear
     * @return BaseballCardInfoItem object which has the carline information for the key
     */
    public BaseballcardInfoItem getCarlineInfo(final String carlineCode,
                                               final String modelYear) {
        return this.bbCardItemMap.get(modelYear + "/" + carlineCode);
    }

    /**
     * @param carlineCode
     * @param modelYear
     * @param bodyStyleCode
     * @return BaseballCardInfoItem object which has the bodystyle information for the key
     */
    public BaseballcardInfoItem getBodystyleInfo(final String carlineCode,
                                                 final String modelYear,
                                                 final String bodyStyleCode) {
        return this.bbCardItemMap.get(modelYear + "/" + carlineCode + "/" + bodyStyleCode);
    }

    /**
     * @param carlineCode
     * @param modelYear
     * @param pageName
     * @return BaseballCardInfoItem object which has the bodystyle manual information for the key
     */
    public BaseballcardInfoItem getBodystyleManualInfo(final String carlineCode,
                                                       final String modelYear,
                                                       final String pageName) {
        return this.bbCardItemMap.get(modelYear + "/" + carlineCode + "/" + pageName);
    }

    /**
     * @param carlineCode
     * @param modelYear
     * @param bodyStyleCode
     * @param configurationCode
     * @return BaseballCardInfoItem object which has the bodystyle configuration information for the
     *         key
     */
    public BaseballcardInfoItem getBodystyleConfigurationInfo(final String carlineCode,
                                                              final String modelYear,
                                                              final String bodyStyleCode,
                                                              final String configurationCode) {
        return this.bbCardItemMap.get(modelYear + "/" + carlineCode + "/" + bodyStyleCode + "/" + configurationCode);
    }

    /**
     * @param queryService
     * @param carlineResource
     * @return List<GMResource> - returns a list of GMResource which are for various bodystyles for
     *         the carline resource path passed in
     */
    private List<GMResource> getBodystyleResourceList(final QueryService queryService,
                                                      final GMResource carlineResource) {
        final String bodystylePropertyValue = AEMTemplateInfo.TEMPLATE_BASEBALLCARD_BODYSTYLE.getTemplatePath();
        final String bodystyleManualPropertyValue = AEMTemplateInfo.TEMPLATE_BASEBALLCARD_BODYSTYLE_MANUAL.getTemplatePath();
        final String bodyStyleConfigurationPropertyValue = AEMTemplateInfo.TEMPLATE_BASEBALLCARD_CONFIGURATION.getTemplatePath();

        final List<String> propertyValues = new ArrayList<String>();
        propertyValues.add(bodystylePropertyValue);
        propertyValues.add(bodystyleManualPropertyValue);
        propertyValues.add(bodyStyleConfigurationPropertyValue);

        final List<GMResource> bodyStyleResources = queryService.findAllByMultipleKeyValues(AEM_TEMPLATE, propertyValues, carlineResource.getParent().getPath());

        return bodyStyleResources;
    }

    /**
     * updates the bodystyle standard and bodystyle manual information
     * 
     * @param bodyStyleResources
     * @param carlineCode
     * @param modelYear
     */
    private void updateBodystyleInfo(final List<GMResource> bodystyleResources,
                                     final String carlineCode,
                                     final String modelYear) {

        for (final GMResource bsResource : bodystyleResources) {
            if (bsResource.getChild(BASEBALLCARD_BODYSTYLE).isExisting()) {
                final BaseballcardInfoItem bbcBodystyleItem = new BaseballcardInfoItem();

                final GMResource bodyStyleResource = bsResource.getChild(BASEBALLCARD_BODYSTYLE);
                final String template = bsResource.getPropertyAsString(AEM_TEMPLATE);

                if (StringUtils.isNotEmpty(template)
                        && (StringUtils.equals(template, AEMTemplateInfo.TEMPLATE_BASEBALLCARD_BODYSTYLE.getTemplatePath())
                        	|| StringUtils.equals(template, AEMTemplateInfo.TEMPLATE_BASEBALLCARD_BODYSTYLE_MANUAL.getTemplatePath()))) {
                    if (StringUtils.equals(template, AEMTemplateInfo.TEMPLATE_BASEBALLCARD_BODYSTYLE_MANUAL.getTemplatePath())) {
                        this.bodystyleManualResourcePaths.add(bsResource.getPath());
                    }

                    if (StringUtils.equals(template, AEMTemplateInfo.TEMPLATE_BASEBALLCARD_BODYSTYLE.getTemplatePath())) {
                    	this.bodystyleResourcePaths.add(bsResource.getPath());
                        final String bodyStyleCode = bodyStyleResource.getPropertyAsString(BaseballcardBodystyleProperties.BODYSTYLE_CODE.getPropertyName());
                        bbcBodystyleItem.setBodyStyleCode(bodyStyleCode);

                        if (StringUtils.isEmpty(bodyStyleCode)) {
                            continue;
                        }

	                    setModelOverviewInternalLink(bbcBodystyleItem, bodyStyleResource);
	                    setBuildYourOwnLink(bbcBodystyleItem, bodyStyleResource);
	                    setSearchInventoryLink(bbcBodystyleItem, bodyStyleResource);
	                    setCompareTrimsInternalLink(bbcBodystyleItem, bodyStyleResource);
	                    setCompetitivePhotoComparsionExternalLink(bbcBodystyleItem, bodyStyleResource);
	                    setImageReferenceLarge(bbcBodystyleItem, bodyStyleResource);
	                    setImageReferenceMedium(bbcBodystyleItem, bodyStyleResource);
	                    setImageReferenceSmall(bbcBodystyleItem, bodyStyleResource);
	                    setImageReferenceCustom(bbcBodystyleItem, bodyStyleResource);
	                    setManualFreightChargePrice(bbcBodystyleItem, bodyStyleResource);
	                    setPrice(bbcBodystyleItem, bodyStyleResource);
	                    setPricePrefix(bbcBodystyleItem, bodyStyleResource);
	                    setManualTotalPrice(bbcBodystyleItem, bodyStyleResource);

                    	final String pageName = bsResource.getContainingPage().getName();

	                    if (StringUtils.equals(template, AEMTemplateInfo.TEMPLATE_BASEBALLCARD_BODYSTYLE.getTemplatePath())) {
	                        this.bbCardItemMap.put(modelYear + "/" + carlineCode + "/" + bodyStyleCode, bbcBodystyleItem);
	                    }

	                    if (StringUtils.equals(template, AEMTemplateInfo.TEMPLATE_BASEBALLCARD_BODYSTYLE_MANUAL.getTemplatePath())) {
	                        this.bbCardItemMap.put(modelYear + "/" + carlineCode + "/" + pageName, bbcBodystyleItem);
	                    }
                    }
                }

            }

            updateBodystyleConfigurationInfo(bsResource, carlineCode, modelYear);
        }
    }

    /**
     * @param bbcBodystyleItem
     * @param bodyStyleResource
     */
    private void setBuildYourOwnLink(final BaseballcardInfoItem bbcBodystyleItem,
                                     final GMResource bodyStyleResource) {
        final String buildYourOwnLink = bodyStyleResource.getPropertyAsString(BaseballcardBodystyleProperties.BUILD_YOUR_OWN_LINK.getPropertyName());
        bbcBodystyleItem.setBuildYourOwnLink(buildYourOwnLink);
    }

    /**
     * @param bbcBodystyleItem
     * @param bodyStyleResource
     */
    private void setModelOverviewInternalLink(final BaseballcardInfoItem bbcBodystyleItem,
                                              final GMResource bodyStyleResource) {
        final String modelOverviewInternalLink = bodyStyleResource.getPropertyAsString(BaseballcardBodystyleProperties.MODEL_OVERVIEW_LINK.getPropertyName());
        bbcBodystyleItem.setModeloverviewInternalLink(modelOverviewInternalLink);
    }

    /**
     * @param bbcBodystyleItem
     * @param bodyStyleResource
     */
    private void setSearchInventoryLink(final BaseballcardInfoItem bbcBodystyleItem,
                                        final GMResource bodyStyleResource) {
        final String searchInventoryLink = bodyStyleResource.getPropertyAsString(BaseballcardBodystyleProperties.SEARCH_INVENTORY_LINK.getPropertyName());
        bbcBodystyleItem.setSearchInventoryLink(searchInventoryLink);
    }

    /**
     * @param bbcBodystyleItem
     * @param bodyStyleResource
     */
    private void setCompareTrimsInternalLink(final BaseballcardInfoItem bbcBodystyleItem,
                                             final GMResource bodyStyleResource) {
        final String compareTrimsInternalLink = bodyStyleResource.getPropertyAsString(BaseballcardBodystyleProperties.COMPARE_TRIMS_LINK.getPropertyName());
        bbcBodystyleItem.setComparetrimsInternalLink(compareTrimsInternalLink);
    }

    /**
     * @param bbcBodystyleItem
     * @param bodyStyleResource
     */
    private void setCompetitivePhotoComparsionExternalLink(final BaseballcardInfoItem bbcBodystyleItem,
                                                           final GMResource bodyStyleResource) {

        final String competitivePhotoComparsionExternalLink = bodyStyleResource.getPropertyAsString(BaseballcardBodystyleProperties.COMPETITIVE_PHOTO_COMPARISON_EXTERNAL_LINK.getPropertyName());
        bbcBodystyleItem.setCompetitivePhotoComparsionExternalLink(competitivePhotoComparsionExternalLink);
    }

    /**
     * @param bbcBodystyleItem
     * @param bodyStyleResource
     */
    private void setImageReferenceLarge(final BaseballcardInfoItem bbcBodystyleItem,
                                        final GMResource bodyStyleResource) {

        final String imageReferenceLarge = bodyStyleResource.getPropertyAsString(BaseballcardBodystyleProperties.THUMBNAIL_DAM_BBC_LARGE.getPropertyName());
        bbcBodystyleItem.setImageReferenceLarge(imageReferenceLarge);
    }

    /**
     * @param bbcBodystyleItem
     * @param bodyStyleResource
     */
    private void setImageReferenceMedium(final BaseballcardInfoItem bbcBodystyleItem,
                                         final GMResource bodyStyleResource) {

        final String imageReferenceMedium = bodyStyleResource.getPropertyAsString(BaseballcardBodystyleProperties.THUMBNAIL_DAM_MEDIUM.getPropertyName());
        bbcBodystyleItem.setImageReferenceMedium(imageReferenceMedium);
    }

    /**
     * @param bbcBodystyleItem
     * @param bodyStyleResource
     */
    private void setImageReferenceSmall(final BaseballcardInfoItem bbcBodystyleItem,
                                        final GMResource bodyStyleResource) {

        final String imageReferenceSmall = bodyStyleResource.getPropertyAsString(BaseballcardBodystyleProperties.THUMBNAIL_DAM_SMALL.getPropertyName());
        bbcBodystyleItem.setImageReferenceSmall(imageReferenceSmall);
    }

    /**
     * @param bbcBodystyleItem
     * @param bodyStyleResource
     */
    private void setImageReferenceCustom(final BaseballcardInfoItem bbcBodystyleItem,
                                         final GMResource bodyStyleResource) {

        final String imageReferenceCustom = bodyStyleResource.getPropertyAsString(BaseballcardBodystyleProperties.THUMBNAIL_DAM_CUSTOM.getPropertyName());
        bbcBodystyleItem.setImageReferenceCustom(imageReferenceCustom);
    }

    /**
     * @param bbcBodystyleItem
     * @param bodyStyleResource
     */
    private void setManualFreightChargePrice(final BaseballcardInfoItem bbcBodystyleItem,
                                             final GMResource bodyStyleResource) {

        final String manualFreightChargePrice = bodyStyleResource.getPropertyAsString(BaseballcardBodystyleProperties.MANUAL_FREIGHT_CHARGE_PRICE.getPropertyName());
        bbcBodystyleItem.setManualFreightChargePrice(manualFreightChargePrice);
    }

    /**
     * @param bbcBodystyleItem
     * @param bodyStyleResource
     */
    private void setPrice(final BaseballcardInfoItem bbcBodystyleItem,
                          final GMResource bodyStyleResource) {

        final String price = bodyStyleResource.getPropertyAsString(BaseballcardBodystyleProperties.MANUAL_PRICE.getPropertyName());
        bbcBodystyleItem.setPrice(price);
    }

    /**
     * @param bbcBodystyleItem
     * @param bodyStyleResource
     */
    private void setPricePrefix(final BaseballcardInfoItem bbcBodystyleItem,
                                final GMResource bodyStyleResource) {

        final String pricePrefix = bodyStyleResource.getPropertyAsString(BaseballcardBodystyleProperties.PRICE_PREFIX.getPropertyName());
        bbcBodystyleItem.setPricePrefix(pricePrefix);
    }

    /**
     * @param bbcBodystyleItem
     * @param bodyStyleResource
     */
    private void setManualTotalPrice(final BaseballcardInfoItem bbcBodystyleItem,
                                     final GMResource bodyStyleResource) {
        final String manualTotalPrice = bodyStyleResource.getPropertyAsString(BaseballcardBodystyleProperties.MANUAL_TOTAL_PRICE.getPropertyName());
        bbcBodystyleItem.setManualTotalPrice(manualTotalPrice);
    }

    /**
     * updates the bodystyle configuration information
     * 
     * @param bsResource
     * @param carlineCode
     * @param modelYear
     */
    private void updateBodystyleConfigurationInfo(final GMResource bsResource,
                                                  final String carlineCode,
                                                  final String modelYear) {

        if (bsResource.getChild(BASEBALLCARD_CONFIGURATION).isExisting()) {
            final String bodystylecode = bsResource.getParent().getParent().getChild(JCR_CONTENT).getChild(BASEBALLCARD_BODYSTYLE).getPropertyAsString(BaseballcardBodystyleProperties.BODYSTYLE_CODE.getPropertyName());
            this.bodystyleConfigurationResourcePaths.add(bsResource.getPath());

            final BaseballcardInfoItem bbcBodystyleConfigurationItem = new BaseballcardInfoItem();

            final GMResource bodyStyleConfigurationResource = bsResource.getChild(BASEBALLCARD_CONFIGURATION);

            final String configurationCode = bodyStyleConfigurationResource.getPropertyAsString(BaseballcardConfigurationProperties.CONFIGURATION_CODE.getPropertyName());
            bbcBodystyleConfigurationItem.setBbcConfiguration(configurationCode);

            final String ddp_price_overwrite = bodyStyleConfigurationResource.getPropertyAsString(BaseballcardConfigurationProperties.DDP_PRICE_OVERWRITE.getPropertyName());
            bbcBodystyleConfigurationItem.setDdpPriceOverwrite(ddp_price_overwrite);

            final String imageReferenceSmall = bodyStyleConfigurationResource.getPropertyAsString(BaseballcardConfigurationProperties.THUMBNAIL_DAM_SMALL.getPropertyName());
            bbcBodystyleConfigurationItem.setImageReferenceSmall(imageReferenceSmall);

            final String bbc_configuration_thumbnail_small = bodyStyleConfigurationResource.getPropertyAsString(BaseballcardConfigurationProperties.THUMBNAIL_SMALL.getPropertyName());
            bbcBodystyleConfigurationItem.setBbc_configuration_thumbnail_small(bbc_configuration_thumbnail_small);

            if (StringUtils.isNotEmpty(configurationCode)) {
                this.bbCardItemMap.put(modelYear + "/" + carlineCode + "/" + bodystylecode + "/" + configurationCode, bbcBodystyleConfigurationItem);
            }
        }
    }

    /**
     * updates the carline information
     * 
     * @param queryService
     * @param bbCardItemMap
     * @param lslrPage
     */
    private void updateCarlineInfo(final QueryService queryService,
                                   final Map<String, BaseballcardInfoItem> bbCardItemMap,
                                   final Page lslrPage) {

        final String carlinePropertyValue = AEMTemplateInfo.TEMPLATE_BASEBALLCARD_CARLINE.getTemplatePath();
        final List<GMResource> carlineResources = queryService.findAllByKeyValue(AEM_TEMPLATE, carlinePropertyValue, lslrPage.getPath());
        for (GMResource resource : carlineResources) {
            if (resource.getChild(BASEBALLCARD_CARLINE).isExisting()) {
                final BaseballcardInfoItem bbcCarlineItem = new BaseballcardInfoItem();

                this.carlineResourcePaths.add(resource.getPath());

                final GMResource carlineResource = resource.getChild(BASEBALLCARD_CARLINE);

                final String carlineCodeUnparsed = carlineResource.getPropertyAsString(BaseballcardCarlineProperties.CARLINE_CODE.getPropertyName());

                parseCarlineCode(carlineCodeUnparsed, bbcCarlineItem);

                final String carlineCode = bbcCarlineItem.getCarlineCode();
                final String modelYear = bbcCarlineItem.getModelYear();

                if (StringUtils.isEmpty(carlineCode) || StringUtils.isEmpty(modelYear)) {
                    continue;
                }

                final String showCarlineText = carlineResource.getPropertyAsString(BaseballcardCarlineProperties.SHOW_CARLINE_TEXT.getPropertyName());
                bbcCarlineItem.setShowCarlineText(showCarlineText);

                final String carlineText = carlineResource.getPropertyAsString(BaseballcardCarlineProperties.CARLINE_TEXT.getPropertyName());
                bbcCarlineItem.setCarlineText(carlineText);

                final String familyLink = carlineResource.getPropertyAsString(BaseballcardCarlineProperties.FAMILY_LINK.getPropertyName());
                bbcCarlineItem.setFamilyLink(familyLink);

                final String legalPriceSuffixBbcN02b = carlineResource.getPropertyAsString(BaseballcardCarlineProperties.LEGAL_PRICE_SUFFIX.getPropertyName());
                bbcCarlineItem.setLegalPriceSuffixBbcN02b(legalPriceSuffixBbcN02b);

                final String legalPriceSuffixBbcN01 = carlineResource.getPropertyAsString(BaseballcardCarlineProperties.LEGAL_PRICE_SUFFIX_N01.getPropertyName());
                bbcCarlineItem.setLegalPriceSuffixBbcN01(legalPriceSuffixBbcN01);

                final String ddpPriceOverwrite = carlineResource.getPropertyAsString(BaseballcardCarlineProperties.DDP_PRICE_OVERWRITE.getPropertyName());
                bbcCarlineItem.setDdpPriceOverwrite(ddpPriceOverwrite);

                this.bbCardItemMap.put(modelYear + "/" + carlineCode, bbcCarlineItem);

                updateBodystyleInfo(getBodystyleResourceList(queryService, resource), carlineCode, modelYear);
            }
        }
    }

    /**
     * parses the carline code set against carline_code property for carline resource
     * 
     * @param carlineCodeUnparsed
     * @param bbcCarlineItem
     */
    private void parseCarlineCode(final String carlineCodeUnparsed,
                                  final BaseballcardInfoItem bbcCarlineItem) {
        if (StringUtils.isNotEmpty(carlineCodeUnparsed)) {
            final String[] carlineCodeParts = carlineCodeUnparsed.split(TILDA);

            if (carlineCodeParts.length >= 1 && StringUtils.isNotEmpty(carlineCodeParts[0])) {
                final String carlineCode = carlineCodeParts[0];
                bbcCarlineItem.setCarlineCode(carlineCode);
            }
            if (carlineCodeParts.length >= 2 && StringUtils.isNotEmpty(carlineCodeParts[1])) {
                final String modelYear = carlineCodeParts[1];
                bbcCarlineItem.setModelYear(modelYear);
            }
            if (carlineCodeParts.length >= 3 && StringUtils.isNotEmpty(carlineCodeParts[2])) {
                final String modelYearSuffix = carlineCodeParts[2];
                bbcCarlineItem.setModelYearSuffix(modelYearSuffix); //
            }
        }
    }
}
