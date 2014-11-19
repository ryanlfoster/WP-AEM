/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.model;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.gmwp.aem.utils.StringUtil;
import com.aditya.gmwp.aem.wrapper.DeepResolvingResourceUtil;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class ExternalLinkModel {

    /** The Constant DEFAULT_LAYER_SIZE. */
    private static final int DEFAULT_LAYER_SIZE = 400;

    private static final String DEFAULT_VALUE = "yes";

    private static final String DEFAULT_CONTENT_DISPLAY_STATUS = "always";

    private static final String BLANK_STRING = "";

    private static final String WINDOW_TYPE = "windowtype";

    private static final String MAIL_TO = "mailto";

    private static final Logger LOG = LoggerFactory.getLogger(ExternalLinkModel.class);

    /** The value of the Content Display Toggle. */
    private String contentDisplayStatus;

    private String eVarParam;

    private String eVarValue;

    /** height of popupwindow. */
    private int height;

    private String icon;

    private boolean isEdxEnable;

    private String parameters;

    /** target link. */
    private String path;

    /** resize of window allowed. */
    private String resizeable;

    /** scrolling of popupwindow allowed. */
    private String scroll;

    /** width of popupwindow. */
    private int width;

    /** target window of link. */
    private String windowtype;

    /** Enable nofollow attribute */
    private String rel;

    /**
     * Constructor with a Page. Properties for the ExternalLinkModel will be read from the root
     * property called "/external_link_c1".
     * 
     * @param resource
     *            resource of the external link page.
     */
    public ExternalLinkModel(final Resource resource) {
        final ResourceResolver resolver = resource.getResourceResolver();

        final String externalLinkResourcePath = resource.getPath() + "/jcr:content/external_link_c1";
        LOG.debug("externalLinkResource Path: {}", externalLinkResourcePath);

        final Resource externalLinkResource = resolver.getResource(externalLinkResourcePath);
        LOG.debug("externalLinkResource: {}", externalLinkResource);

        // check if eDX-tagging is enable for the external link
        final String enableEdxTagging = DeepResolvingResourceUtil.getValueMap(resolver.getResource(resource.getPath() + "/jcr:content/")).get("edx_enabled",
                String.class);
        if (null != enableEdxTagging && "true".equals(enableEdxTagging)) {
            setEdxEnable(true);
        }

        isEdxEnable();
        final ValueMap map = DeepResolvingResourceUtil.getValueMap(externalLinkResource);
        initExternalLinkProperties(map);
    }

    /**
     * Constructs an ExternalLinkModel from a url/path.
     * 
     * @param path
     *            The absolute path of the external Link.
     */
    public ExternalLinkModel(final String path) {
        if (StringUtils.isEmpty(path)) {
            throw new IllegalArgumentException("The Path cannot be null or an empty string.");
        }
        this.path = path;
        setWindowtype("_self");
    }

    /**
     * Constructor with ValueMap. Properties for the LinkModel will be read from this ValueMap.
     * 
     * @param properties
     *            ValueMap with properties.
     */
    public ExternalLinkModel(final ValueMap properties) {
        initExternalLinkProperties(properties);
    }

    /**
     * Contains path domain.
     * 
     * @param path
     *            of the link
     * @param domain
     *            example youtube.com
     * @return true if the given path contains the domain
     */
    private boolean containsPathDomain(final String path,
                                       final String domain) {
        return path.matches("^(https?://)?(.+\\.)?" + domain + ".*");
    }

    /**
     * Gets the automatic icon.
     * 
     * @param path
     *            of the link
     * @return the css class of the detected icon
     */
    private String getAutomaticIcon(final String path) {
        String iconClass = BLANK_STRING;
        if (containsPathDomain(path, "youtube\\.com")) {
            iconClass = "ln_youtube";
        } else if (containsPathDomain(path, "twitter\\.com")) {
            iconClass = "ln_twitter";
        } else if (containsPathDomain(path, "facebook\\.com")) {
            iconClass = "ln_facebook";
        } else if (containsPathDomain(path, "pinterest\\.com")) {
            iconClass = "ln_pinterest";
        } else if (containsPathDomain(path, "vk\\.com")) {
            iconClass = "ln_vk";
        } else if (containsPathDomain(path, "google\\.com")) {
            iconClass = "ln_google";
        } else if (containsPathDomain(path, "foursquare\\.com")) {
            iconClass = "ln_foursquare";
        } else if (containsPathDomain(path, "flickr\\.com")) {
            iconClass = "ln_flickr";
        } else if (path.startsWith("mail:")) {
            iconClass = "ln_mail";
        }
        return iconClass;
    }

    /**
     * Gets the status of the Content Display toggle.
     * 
     * @return the contentDisplay
     */
    public final String getContentDisplayStatus() {
        return this.contentDisplayStatus;
    }

    /**
     * Gets the e var param.
     * 
     * @return the eVarParam
     */
    public final String geteVarParam() {
        return this.eVarParam;
    }

    /**
     * Gets the e var value.
     * 
     * @return the eVarValue
     */
    public final String geteVarValue() {
        return this.eVarValue;
    }

    /**
     * Gets the height.
     * 
     * @return Integer height
     */
    public final int getHeight() {
        return this.height;
    }

    /**
     * Gets the icon.
     * 
     * @return the icon
     */
    public final String getIcon() {
        return this.icon;
    }

    /**
     * Gets the parameters.
     * 
     * @return String parameters
     */
    public final String getParameters() {
        return this.parameters;
    }

    /**
     * Gets the path.
     * 
     * @return String path
     */
    public final String getPath() {
        return this.path;
    }

    /**
     * Gets the rel.
     * 
     * @return String rel
     */
    public final String getRel() {
        return this.rel;
    }

    /**
     * Gets the resizeable.
     * 
     * @return String resizeable
     */
    public final String getResizeable() {
        return this.resizeable;
    }

    /**
     * Gets the scroll.
     * 
     * @return String scroll
     */
    public final String getScroll() {
        return this.scroll;
    }

    /**
     * Gets the width.
     * 
     * @return Integer width
     */
    public final int getWidth() {
        return this.width;
    }

    /**
     * Gets the windowtype.
     * 
     * @return String windowtype
     */
    public final String getWindowtype() {
        return this.windowtype;
    }

    /**
     * Init properties for external link.
     * 
     * @param properties
     *            ValueMap properties
     */
    private void initExternalLinkProperties(final ValueMap properties) {
        if (properties == null) {
            return;
        }

        setParameters(properties.get("parameters", String.class));
        setPath(properties.get("path", String.class));
        setRel(properties.get("enable_nofollow", false));
        if (StringUtils.isNotEmpty(this.path) && StringUtils.isNotEmpty(this.parameters)
                && !MAIL_TO.equalsIgnoreCase(properties.get(WINDOW_TYPE, String.class))) {
            final String pathSegment1 = stripTrailingParameterSymbolsFrom(this.path);
            final String pathSegment2 = stripLeadingParameterSymbolsFrom(this.parameters);
            final String separatorSymbol = pathSegment1.contains("?") ? "&" : "?";
            setPath(pathSegment1 + separatorSymbol + pathSegment2);
        }
        if (properties.get(WINDOW_TYPE, String.class) == null) {
            setWindowtype("_self");
        } else {
            setWindowtype(properties.get(WINDOW_TYPE, String.class));
        }

        final String _width = properties.get("width", String.class);
        final String _height = properties.get("height", String.class);
        if (_width != null && _height != null) {
            try {
                setWidth(Integer.parseInt(_width));
                setHeight(Integer.parseInt(_height));
            } catch (NumberFormatException e) {
                LOG.info("Unable to parse external link width & height. Please check the values: " + _width + " x " + _height + ". ", e);
                setWidth(DEFAULT_LAYER_SIZE);
                setHeight(DEFAULT_LAYER_SIZE);
            }
        } else if (isWindowtypeLayer()) {
            setWidth(DEFAULT_LAYER_SIZE);
            setHeight(DEFAULT_LAYER_SIZE);
        }

        String showIcon;
        if (StringUtils.isEmpty(properties.get("show_icon", BLANK_STRING)) && this.path != null) {
            showIcon = getAutomaticIcon(this.path);
        } else {
            showIcon = properties.get("show_icon", BLANK_STRING);
        }

        setIcon(showIcon);
        seteVarParam(properties.get("eVarParam", String.class));
        seteVarValue(properties.get("eVarValue", String.class));
        setContentDisplayStatus(properties.get("content_display_status", String.class));
        setScroll(DEFAULT_VALUE);
        setResizeable(DEFAULT_VALUE);
    }

    /**
     * Strips the trailing parameter symbol from the path.
     * 
     * @param str
     *            the str
     * @return the new string.
     */
    private String stripTrailingParameterSymbolsFrom(final String str) {
        return StringUtil.endsWith(str, '?') || StringUtil.endsWith(str, '&') ? str.substring(0, str.length() - 1) : str;
    }

    /**
     * Strips the leading parameter symbols from the parameters.
     * 
     * @param str
     *            the str
     * @return the new string.
     */
    private String stripLeadingParameterSymbolsFrom(final String str) {
        return StringUtil.startsWith(str, '?') || StringUtil.startsWith(str, '&') ? str.substring(1) : str;
    }

    /**
     * Checks if is edx enable.
     * 
     * @return true, if is edx enable
     */
    public final boolean isEdxEnable() {
        return this.isEdxEnable;
    }

    /**
     * Checks if is embedded content. Embedded content means a social media link as external link.
     * 
     * @return true, if is embedded content
     */
    public final boolean isEmbeddedContent() {
        return "embedded".equals(getWindowtype());
    }

    /**
     * Checks if is windowtype layer.
     * 
     * @return true if the windowtype is layer.
     */
    public final boolean isWindowtypeLayer() {
        return "layer".equals(getWindowtype());
    }

    /**
     * Checks if is windowtype mail to.
     * 
     * @return true if the windowtype is mailto.
     */
    public final boolean isWindowtypeMailTo() {
        return MAIL_TO.equals(getWindowtype());
    }

    /**
     * Checks if is windowtype pop up.
     * 
     * @return true if the windowtype is popup.
     */
    public final boolean isWindowtypePopUp() {
        return "popup".equals(getWindowtype());
    }

    /**
     * Sets the value of the Content Display toggle. If the value of paramm contentDisplay is null
     * set a default value of 'always'.
     * 
     * @param contentDisplayStatus
     *            the new content display status
     */
    public final void setContentDisplayStatus(final String contentDisplayStatus) {
        if (contentDisplayStatus != null) {
            this.contentDisplayStatus = contentDisplayStatus;
        } else {
            this.contentDisplayStatus = DEFAULT_CONTENT_DISPLAY_STATUS;
        }
    }

    /**
     * Sets the edx enable.
     * 
     * @param isEdxEnable
     *            the new edx enable
     */
    public final void setEdxEnable(final boolean isEdxEnable) {
        this.isEdxEnable = isEdxEnable;
    }

    /**
     * Sets the e var param.
     * 
     * @param eVarParam
     *            the eVarParam to set
     */
    public final void seteVarParam(final String eVarParam) {
        this.eVarParam = eVarParam;
    }

    /**
     * Sets the e var value.
     * 
     * @param eVarValue
     *            the eVarValue to set
     */
    public final void seteVarValue(final String eVarValue) {
        this.eVarValue = eVarValue;
    }

    /**
     * Sets the height.
     * 
     * @param height
     *            Integer
     */
    public final void setHeight(final int height) {
        LOG.debug("setHeight({})", height);
        this.height = height;
    }

    /**
     * Sets the icon.
     * 
     * @param icon
     *            the icon to set
     */
    public final void setIcon(final String icon) {
        this.icon = icon;
    }

    /**
     * Sets the parameters.
     * 
     * @param parameters
     *            String
     */
    public final void setParameters(final String parameters) {
        LOG.debug("setParameters({})", parameters);
        this.parameters = parameters;
    }

    /**
     * Sets the path.
     * 
     * @param path
     *            String
     */
    public final void setPath(final String path) {
        LOG.debug("setPath({})", path);
        this.path = path;
    }

    /**
     * Sets the nofollow option.
     * 
     * @param path
     *            String
     */
    public final void setRel(final boolean flag) {
        LOG.debug("setNofollow({})", flag);
        if (flag) {
            this.rel = "nofollow";
        }
    }

    /**
     * Sets the resizeable.
     * 
     * @param resizeable
     *            String
     */
    public final void setResizeable(final String resizeable) {
        LOG.debug("setResizeable({})", resizeable);
        this.resizeable = resizeable;
    }

    /**
     * Sets the scroll.
     * 
     * @param scroll
     *            String
     */
    public final void setScroll(final String scroll) {
        LOG.debug("setScroll({})", scroll);
        this.scroll = scroll;
    }

    /**
     * Sets the width.
     * 
     * @param width
     *            Integer
     */
    public final void setWidth(final int width) {
        LOG.debug("setWidth({})", width);
        this.width = width;
    }

    /**
     * Sets the windowtype.
     * 
     * @param windowtype
     *            String
     */
    public final void setWindowtype(final String windowtype) {
        LOG.debug("setWindowtype({})", windowtype);
        this.windowtype = windowtype;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString() {
        return super.toString() + "{path: " + this.path + ", parameters: " + this.parameters + ", width: " + this.width + ", height: " + this.height
                + ", windowtype: " + this.windowtype + ", scroll: " + this.scroll + " , nofollow:" + this.rel + "}";
    }
}
