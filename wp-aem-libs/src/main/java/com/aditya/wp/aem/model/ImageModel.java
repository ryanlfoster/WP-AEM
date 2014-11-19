/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.model;

import java.awt.Dimension;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import com.aditya.gmwp.aem.utils.MediaUtil;
import com.aditya.gmwp.aem.wrapper.DeepResolvingResourceUtil;
import com.day.cq.wcm.foundation.ImageMap;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class ImageModel {

    private String alt;
    private String height;
    private String imageName = "";
    private LinkModel link;
    private String url;
    private String width;
    private String imageMapId;
    private ImageMap imageMap;
    private String title;

    /**
     * default constructor.
     */
    public ImageModel() {
    }

    /**
     * create image model by {@link Resource}, {@link Node}, {@link Page} and {@link ValueMap}.
     * 
     * @param resource
     *            the resource {@link Resource} of the current component
     */
    public ImageModel(final Resource resource) {
        this.createImageModel(resource);
    }

    /**
     * create image model by {@link Resource}, {@link Node}, {@link Page} and {@link ValueMap}.
     * 
     * @param resource
     *            the <code>Resource</code> to create <code>ImageModel</code> from
     * @param isRollover
     *            determine whether rollover image should be created or not
     */
    public ImageModel(final Resource resource, final boolean isRollover) {
        this.createImageModel(resource, isRollover);
    }

    /**
     * create image model by {@link Resource}, {@link Node}, {@link Page} and {@link ValueMap}.
     * 
     * @param resource
     *            the <code>Resource</code> to create <code>ImageModel</code> from
     * @param isRollover
     *            determine whether rollover image should be created or not
     * @param imageProperty
     *            the image property
     * @param altTextProperty
     *            the alternate text property for the image
     */
    public ImageModel(final Resource resource, final boolean isRollover, final String imageProperty, final String altTextProperty) {
        this.createImageModel(resource, isRollover, imageProperty, altTextProperty);
    }

    /**
     * Builds an image model from an image's path.
     * 
     * @param pathToImageInDAM
     *            The path to the Image in DAM.
     * @param resolver
     *            A resource Resolver for retrieval of properties.
     */
    public ImageModel(final String pathToImageInDAM, final ResourceResolver resolver) {
        final Dimension imgDimensions = MediaUtil.getImgDimensions(pathToImageInDAM, resolver);
        if (imgDimensions != null) {
            if (imgDimensions.getWidth() >= 0.0) {
                setWidth(Integer.toString(imgDimensions.width));
            }
            if (imgDimensions.getHeight() >= 0.0) {
                setHeight(Integer.toString(imgDimensions.height));
            }
        }
        this.url = pathToImageInDAM;

    }

    /**
     * create an image directly with a given url.
     * 
     * @param url
     *            the url to the linl
     * @param width
     *            width of the image
     * @param height
     *            height of the image
     */
    public ImageModel(final String url, final String width, final String height) {
        if (url != null) {
            this.url = url;
        }
        this.height = height;
        this.width = width;
    }

    /**
     * Helper method to set <code>ImageModel</code> values and eventually create it from.
     * 
     * @param resource
     *            the resource to get values from
     */
    private void createImageModel(final Resource resource) {
        this.createImageModel(resource, false);
    }

    /**
     * Helper method to set <code>ImageModel</code> values and eventually create it from.
     * 
     * @param resource
     *            the resource to get values from
     * @param isRollover
     *            determine whether rollover image should be retrieved or not
     */
    private void createImageModel(final Resource resource,
                                  final boolean isRollover) {
        final ValueMap properties = DeepResolvingResourceUtil.getValueMap(resource);
        if (resource != null) {
            this.imageName = resource.getName();
        } else {
            throw new IllegalArgumentException("Argument 'resource' may not be null!");
        }

        final String imageUrl = MediaUtil.getImgUrl(resource, properties, this.imageName, isRollover);
        if (imageUrl != null) {
            setUrl(imageUrl);
            setAlt(properties.get("altText", String.class));

            final Dimension imgDimensions = MediaUtil.getImgDimensions(resource, isRollover);
            if (imgDimensions != null) {
                if (imgDimensions.getWidth() >= 0.0) {
                    setWidth(imgDimensions.width + "");
                }
                if (imgDimensions.getHeight() >= 0.0) {
                    setHeight(imgDimensions.height + "");
                }
            }
        }

        createImageMap(properties);
    }

    /**
     * Helper method to set <code>ImageModel</code> values and eventually create it from.
     * 
     * @param resource
     *            the resource to get values from
     * @param isRollover
     *            determine whether rollover image should be retrieved or not
     * @param imageProperty
     *            the image property
     * @param altTextProperty
     *            the alternate text property for the image
     */
    private void createImageModel(final Resource resource,
                                  final boolean isRollover,
                                  final String imageProperty,
                                  final String altTextProperty) {

        if (resource != null) {
            this.imageName = resource.getName();
        } else {
            throw new IllegalArgumentException("Argument 'resource' may not be null!");
        }

        final String imageUrl = MediaUtil.getImgUrl(resource, imageProperty, this.imageName, isRollover);
        if (imageUrl != null) {
            setUrl(imageUrl);
            setAlt(altTextProperty);

            final Dimension imgDimensions = MediaUtil.getImgDimensions(resource, isRollover, imageProperty);
            if (imgDimensions != null) {
                if (imgDimensions.getWidth() >= 0.0) {
                    setWidth(imgDimensions.width + "");
                }
                if (imgDimensions.getHeight() >= 0.0) {
                    setHeight(imgDimensions.height + "");
                }
            }
        }

    }

    /**
     * As in com.day.cq.wcm.foundation.Image .
     * 
     * @param properties
     *            properties
     */
    private void createImageMap(final ValueMap properties) {
        if (!(properties.containsKey("imageMap"))) {
            return;
        }
        try {
            final String mapDefinition = properties.get("imageMap", "");
            if (StringUtils.isNotEmpty(mapDefinition)) {
                this.imageMap = ImageMap.fromString(mapDefinition);
                this.imageMapId = "map_" + Math.round(Math.random() * 2147483647.0D) + "_" + System.currentTimeMillis();
            }

        } catch (final IllegalArgumentException iae) {
            this.imageMap = null;
            this.imageMapId = null;
        }
    }

    /**
     * Gets the alt.
     * 
     * @return String alt
     */
    public final String getAlt() {
        return this.alt;
    }

    /**
     * return true, if the image object has a real content, otherwise return false.
     * 
     * @return return true, if the image object has a real content, otherwise return false
     */
    public final boolean getHasContent() {
        return this.url != null;
    }

    /**
     * Returns the height of the Image.
     * 
     * @return the Image's height
     */
    public final String getHeight() {
        return this.height;
    }

    /**
     * Gets the image name.
     * 
     * @return String image name
     */
    public final String getImageName() {
        return this.imageName;
    }

    /**
     * Gets the image type.
     * 
     * @return String image type (file ending)
     */
    public final String getImageType() {
        String imageType = null;

        if (this.url != null) {
            imageType = this.url.substring(this.url.lastIndexOf(".") + 1);
        }

        return imageType;
    }

    /**
     * Gets the link.
     * 
     * @return LinkModel link
     */
    public final LinkModel getLink() {
        return this.link;
    }

    /**
     * Gets the url.
     * 
     * @return String url
     */
    public final String getUrl() {
        return this.url;
    }

    /**
     * Gets the width.
     * 
     * @return String width
     */
    public final String getWidth() {
        return this.width;
    }

    /**
     * Sets the alt.
     * 
     * @param alt
     *            String
     */
    public final void setAlt(final String alt) {
        this.alt = alt;
    }

    /**
     * Sets the height.
     * 
     * @param height
     *            String
     */
    public final void setHeight(final String height) {
        this.height = height;
    }

    /**
     * set a link model.
     * 
     * @param link
     *            LinkModel
     */
    public final void setLink(final LinkModel link) {
        this.link = link;
    }

    /**
     * set the url of the image.
     * 
     * @param url
     *            String
     */
    public final void setUrl(final String url) {
        if (StringUtils.isNotBlank(url)) {
            this.url = url.replaceAll(" ", "%20");
        }
    }

    /**
     * Sets the width.
     * 
     * @param width
     *            String
     */
    public final void setWidth(final String width) {
        this.width = width;
    }

    /**
     * Returns the image map id.
     * 
     * @return image map id
     */
    public final String getImageMapId() {
        return this.imageMapId;
    }

    /**
     * Returns the image map.
     * 
     * @return image map
     */
    public final ImageMap getImageMap() {
        return this.imageMap;
    }

    /**
     * Gets the title.
     * 
     * @return the title
     */
    public final String getTitle() {
        return this.title;
    }

    /**
     * Sets the title.
     * 
     * @param title
     *            the new title
     */
    public final void setTitle(final String title) {
        this.title = title;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString() {
        return super.toString() + "{url:'" + this.url + "', alt:'" + this.alt + "', link:" + (this.link == null ? "null" : this.link.toString()) + "}";

    }

    /**
     * @return the file name taken from the image URL, or an empty string if the URL is null
     */
    public String getFileName() {
        if (this.url == null) {
            return "";
        }
        return this.url.substring(this.url.lastIndexOf("/") + 1);
    }
}