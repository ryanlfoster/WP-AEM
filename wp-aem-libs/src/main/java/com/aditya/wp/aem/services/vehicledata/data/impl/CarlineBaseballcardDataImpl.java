/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.vehicledata.data.impl;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;

import com.aditya.gmwp.aem.model.ImageModel;
import com.aditya.gmwp.aem.properties.BaseballcardCarlineProperties;
import com.aditya.gmwp.aem.properties.Properties;
import com.aditya.gmwp.aem.services.vehicledata.data.CarlineBaseballcardData;
import com.aditya.gmwp.aem.services.vehicledata.utils.CarlineUtil;
import com.aditya.gmwp.aem.utils.PageUtil;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class CarlineBaseballcardDataImpl implements CarlineBaseballcardData {

    private final Page carlinePage;
    private String title;
    private final String carlineText;
    private final String link;
    private final ValueMap carlineProperties;

    /**
     * Creates new Carline Baseballcard data for a page.
     * 
     * @param carlinePage
     *            carline page
     */
    public CarlineBaseballcardDataImpl(final Page carlinePage) {
        this.carlinePage = carlinePage;
        this.carlineProperties = null == this.carlinePage ? new ValueMapDecorator(new HashMap<String, Object>())
                : this.carlinePage.getProperties("./baseballcard_carline");
        this.carlineText = this.carlineProperties.get(BaseballcardCarlineProperties.CARLINE_TEXT.getPropertyName(),
                StringUtils.EMPTY);
        this.link = this.carlineProperties.get(BaseballcardCarlineProperties.FAMILY_LINK.getPropertyName(),
                StringUtils.EMPTY);
        initTitle();
    }

    @Override
    public ImageModel getThumbnailImage() {
        final String fileReferenceDAM = this.carlineProperties.get(
                BaseballcardCarlineProperties.THUMBNAIL_DAM.getPropertyName(), StringUtils.EMPTY);
         final String fileReferenceURL = this.carlineProperties.get(
                BaseballcardCarlineProperties.THUMBNAIL_URL_CARLINE.getPropertyName(), StringUtils.EMPTY);

	     if (StringUtils.isNotEmpty(fileReferenceURL)) {
	            return new ImageModel(fileReferenceURL, null, null);
	        } else if (StringUtils.isNotEmpty(fileReferenceDAM)) {
	            final ResourceResolver resourceResolver = this.carlinePage.getContentResource().getResourceResolver();
	            return new ImageModel(fileReferenceDAM, resourceResolver);
	      }

        return new ImageModel();
    }

    @Override
    public String getLegalSuffixText() {
        return this.carlineProperties.get(BaseballcardCarlineProperties.LEGAL_PRICE_SUFFIX.getPropertyName(),
                StringUtils.EMPTY);
    }

    /**
     * returns the link to the family page.
     * 
     * @return link
     */
    @Override
    public String getFamilyLink() {
        return this.link;
    }

    /**
     * Initializes the carline title. Uses either the navigation title (preferred) or the page title.
     */
    private void initTitle() {
        this.title = PageUtil.getNavigationTitleFromPage(this.carlinePage);
    }

    @Override
    public final String getBaseballCardTitle() {
        return this.title;
    }

    @Override
    public final String getBaseballcardProperty(final Properties baseballCardProperty) {
        String property = this.carlineProperties.get(baseballCardProperty.getPropertyName(), String.class);

        if (baseballCardProperty instanceof BaseballcardCarlineProperties) {
            final BaseballcardCarlineProperties carlineProp = (BaseballcardCarlineProperties) baseballCardProperty;
            if (BaseballcardCarlineProperties.CARLINE_CODE == carlineProp
                    || BaseballcardCarlineProperties.MODEL_YEAR == carlineProp
                    || BaseballcardCarlineProperties.MODEL_YEAR_SUFFIX == carlineProp) {
                if (BaseballcardCarlineProperties.CARLINE_CODE == carlineProp) {
                    property = CarlineUtil.getCarlineCode(this.carlineProperties);
                } else if (BaseballcardCarlineProperties.MODEL_YEAR == carlineProp) {
                    property = CarlineUtil.getModelYear(this.carlineProperties);
                } else {
                    property = CarlineUtil.getModelYearSuffix(this.carlineProperties);
                }
            }
        }

        return property;
    }

    @Override
    public String getCarlineText() {
        return this.carlineText;
    }
}