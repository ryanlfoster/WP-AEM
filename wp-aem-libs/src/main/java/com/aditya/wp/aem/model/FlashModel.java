/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;

import com.aditya.gmwp.aem.global.AEMTemplateInfo;
import com.aditya.gmwp.aem.wrapper.DeepResolvingResourceUtil;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.foundation.Download;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class FlashModel {

    /** install path for flash. */
    public static final String XI_URL = "/static/cms/all/swf/expressinstall.swf";

    /** flash params. */
    private final Map<String, String> flashParams = new HashMap<String, String>();

    /** minimum of flash player. */
    private String flashVersion = "9.0.0";

    /** height of flash object. */
    private String height;

    /** div id for html. */
    private String id;

    /** url of flash object. */
    private String url;

    /** width of flash object. */
    private String width;

    /**
     * create a flash model from current resource.
     * 
     * @param resource
     *            current resource of the current component
     */
    @SuppressWarnings("deprecation")
    public FlashModel(final Resource resource) {
        final Download flashObj = new Download(resource);
        if (flashObj.hasContent()) {
            setUrl(flashObj.getHref());
            setFlashParameterByResource(resource);
        } else {
            final String externalflash = ResourceUtil.getValueMap(resource).get("externalflash", String.class);
            if (StringUtils.isNotEmpty(externalflash)) {
                Resource externalFlashResource = resource.getResourceResolver().getResource(externalflash);
                if (null != externalFlashResource
                        && !(externalFlashResource instanceof NonExistingResource)
                        && AEMTemplateInfo.TEMPLATE_EXTERNAL_FLASH.matchesTemplate(externalFlashResource.adaptTo(Page.class))) {
                    externalFlashResource = resource.getResourceResolver().getResource(
                            externalFlashResource.getPath() + "/jcr:content/external_flash_c1");
                    final ValueMap map = DeepResolvingResourceUtil.getValueMap(externalFlashResource);
                    final String urlMethodLocal = map.get("url", String.class);
                    if (StringUtils.isNotEmpty(urlMethodLocal)) {
                        setUrl(urlMethodLocal);
                        setFlashParameterByResource(resource);
                    }
                }
            }
        }
    }

    /**
     * Returns the flash parameter.
     * 
     * @return a unmodifiable map of flash parameter
     */
    public final Map<String, String> getFlashParams() {
        return Collections.unmodifiableMap(this.flashParams);
    }

    /**
     * Returns the flash version.
     * 
     * @return minimum of flash player
     */
    public final String getFlashVersion() {
        return this.flashVersion;
    }

    /**
     * return true, if the image object has a real content, otherwise return false.
     * 
     * @return true, if the image object has a real content, otherwise return false.
     */
    public final boolean getHasContent() {
        return this.url != null;
    }

    /**
     * return height of flash object.
     * 
     * @return height of flash object
     */
    public final String getHeight() {
        return this.height;
    }

    /**
     * div id for html.
     * 
     * @return div id for html
     */
    public final String getId() {
        return this.id;
    }

    /**
     * return url of flash object.
     * 
     * @return url of flash object
     */
    public final String getUrl() {
        return this.url;
    }

    /**
     * return width of flash object.
     * 
     * @return width of flash object
     */
    public final String getWidth() {
        return this.width;
    }

    /**
     * install path for flash.
     * 
     * @return install path for flash
     */
    public final String getXiUrl() {
        return XI_URL;
    }

    /**
     * Sets a flash parameter.
     * 
     * @param pam
     *            the pam
     * @param value
     *            of the flash parameter {@link FlashParameter}
     */
    public final void putFlashParam(final FlashParameter pam, final String value) {
        putFlashParam(pam.key(), value);
    }

    /**
     * Sets a flash parameter.
     * 
     * @param key
     *            key of the flash parameter
     * @param value
     *            of the flash parameter
     */
    public final void putFlashParam(final String key,
                                    final String value) {
        if (FlashParameter.FLASHVARS.key().equals(key)) {
            final String currFlashVars = this.flashParams.get(FlashParameter.FLASHVARS.key());
            if (StringUtils.isBlank(currFlashVars)) {
                this.flashParams.put(FlashParameter.FLASHVARS.key(), value);
            } else {
                if (!currFlashVars.contains(value)) {
                    this.flashParams.put(FlashParameter.FLASHVARS.key(), currFlashVars + "&" + value);
                }
            }
        } else {
            this.flashParams.put(key, value);
        }
    }

    /**
     * Sets a flash parameter from a resource.
     * 
     * @param resource
     *            resource of the flash include
     */
    protected final void setFlashParameterByResource(final Resource resource) {
        final ValueMap properties = DeepResolvingResourceUtil.getValueMap(resource);

        // id for a flash must be unique on the whole page.
        // so we generate an id using the "subpath" below "jcr:content" of the current resource path.
        String idMethodLocal = resource.getPath();
        idMethodLocal = idMethodLocal.substring(idMethodLocal.lastIndexOf(JcrConstants.JCR_CONTENT)
                + JcrConstants.JCR_CONTENT.length());

        // GMDSST-2499: do not use "_" as first character. So i remove the first underscores.
        idMethodLocal = idMethodLocal.replace("/", "");
        setId(idMethodLocal);

        final String widthMethodLocal = properties.get("flash_width", String.class);
        if (widthMethodLocal != null) {
            // GMDSST-2499: width="740px" is invalid XHTML, so i remove the "px" string
            // width = width.endsWith("%") ? width : width + "px";
            setWidth(widthMethodLocal);
        }
        final String heightMethodLocal = properties.get("flash_height", String.class);
        if (heightMethodLocal != null) {
            // GMDSST-2499: height="740px" is invalid XHTML, so i remove the "px" string
            // height = height.endsWith("%") ? height : height + "px";
            setHeight(heightMethodLocal);
        }

        final String flashVersionMethodLocal = properties.get("flashVersion", String.class);
        if (flashVersionMethodLocal != null) {
            setFlashVersion(flashVersionMethodLocal);
        }

        this.flashParams.putAll(FlashParameter.getFlashParameter(properties));
    }

    /**
     * Sets the flash version.
     * 
     * @param flashVersion
     *            minimum of flash player
     */
    public final void setFlashVersion(final String flashVersion) {
        this.flashVersion = flashVersion;
    }

    /**
     * set of flash object.
     * 
     * @param height
     *            of flash object
     */
    public final void setHeight(final String height) {
        this.height = height;
    }

    /**
     * set div id for html.
     * 
     * @param id
     *            div id for html
     */
    public final void setId(final String id) {
        this.id = id;
    }

    /**
     * set url of flash object.
     * 
     * @param url
     *            url of flash object
     */
    public final void setUrl(final String url) {
        this.url = url;
    }

    /**
     * set width of flash object.
     * 
     * @param width
     *            of flash object
     */
    public final void setWidth(final String width) {
        this.width = width;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString() {
        return super.toString() + "{url:'" + this.url + "', id:'" + this.id + "', width:'" + this.width + "', height:'"
                + this.height + "', flashParams:'" + this.flashParams + "'}";
    }
}
