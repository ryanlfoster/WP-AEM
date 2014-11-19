/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.vehicledata.data.impl;

import com.aditya.gmwp.aem.services.vehicledata.data.Trim;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class TrimImpl implements Trim {

    private String seriesCode;
    private String title;
    private String price;
    private String netPrice;
    private String description;
    private String featureList;
    private boolean overrideLinkTarget;
    private String targetOfMoreDetailsLink;
    private String deeplinkingTarget;

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.impl.Trim#getSeriesCode()
     */
    @Override
    public String getSeriesCode() {
        return this.seriesCode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.impl.Trim#setSeriesCode(java.lang.String)
     */
    @Override
    public void setSeriesCode(final String seriesCode) {
        this.seriesCode = seriesCode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.impl.Trim#getTitle()
     */
    @Override
    public String getTitle() {
        return this.title;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.impl.Trim#setTitle(java.lang.String)
     */
    @Override
    public void setTitle(final String title) {
        this.title = title;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.impl.Trim#getPrice()
     */
    @Override
    public String getPrice() {
        return this.price;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.vehicledata.Trim#getNetPrice()
     */
    @Override
    public String getNetPrice() {
        return this.netPrice;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.impl.Trim#setPrice(java.lang.String)
     */
    @Override
    public void setPrice(final String price) {
        this.price = price;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.vehicledata.Trim#setNetPrice(java.lang.String)
     */
    @Override
    public void setNetPrice(final String netPrice) {
        this.netPrice = netPrice;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.impl.Trim#getDescription()
     */
    @Override
    public String getDescription() {
        return this.description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.impl.Trim#setDescription(java.lang.String)
     */
    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.impl.Trim#getFeatureList()
     */
    @Override
    public String getFeatureList() {
        return this.featureList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.impl.Trim#setFeatureList(java.lang.String)
     */
    @Override
    public void setFeatureList(final String featureList) {
        this.featureList = featureList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.impl.Trim#isOverrideLinkTarget()
     */
    @Override
    public boolean isOverrideLinkTarget() {
        return this.overrideLinkTarget;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.impl.Trim#setOverrideLinkTarget(boolean)
     */
    @Override
    public void setOverrideLinkTarget(final boolean overrideLinkTarget) {
        this.overrideLinkTarget = overrideLinkTarget;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.impl.Trim#getTargetOfMoreDetailsLink()
     */
    @Override
    public String getTargetOfMoreDetailsLink() {
        return this.targetOfMoreDetailsLink;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.gm.gssm.gmds.cq.services.vehicledata.impl.Trim#setTargetOfMoreDetailsLink(java.lang.String
     * )
     */
    @Override
    public void setTargetOfMoreDetailsLink(final String targetOfMoreDetailsLink) {
        this.targetOfMoreDetailsLink = targetOfMoreDetailsLink;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gm.gssm.gmds.cq.services.vehicledata.impl.Trim#getDeeplinkingTarget()
     */
    @Override
    public String getDeeplinkingTarget() {
        return this.deeplinkingTarget;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.gm.gssm.gmds.cq.services.vehicledata.impl.Trim#setDeeplinkingTarget(java.lang.String)
     */
    @Override
    public void setDeeplinkingTarget(final String deeplinkingTarget) {
        this.deeplinkingTarget = deeplinkingTarget;
    }
}