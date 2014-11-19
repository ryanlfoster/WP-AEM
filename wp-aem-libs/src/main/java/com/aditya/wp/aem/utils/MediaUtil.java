/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.utils;

import java.awt.Dimension;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;

import com.aditya.gmwp.aem.wrapper.DeepResolvingResourceUtil;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class MediaUtil {
    private static final String PATH_TO_DAM = "/content/dam";

    /**
     * Returns the dimensions of a given image resource in DAM.
     * 
     * @param resource
     *            the resource of the image in DAM
     * @return returns the Dimension of the given DAM Resource or null if no such resource exists.
     */
    public static Dimension getImgDimensions(final Resource resource) {
        return getImgDimensions(resource, false);
    }

    /**
     * Returns the dimensions of a given image resource in DAM.
     * 
     * @param resource
     *            the resource of the image in DAM
     * @param isRollover
     *            determines whether rollover image dimension should be retrieved or not
     * @return returns the Dimension of the given DAM Resource or null if no such resource exists.
     */
    public static Dimension getImgDimensions(final Resource resource,
                                             final boolean isRollover) {
        final ValueMap properties = DeepResolvingResourceUtil.getValueMap(resource);
        final String imgRef = getImgRef(properties, isRollover);
        return getImgDimensions(imgRef, resource.getResourceResolver());
    }

    /**
     * Returns the dimensions of a given image resource in DAM.
     * 
     * @param resource
     *            the resource of the image in DAM
     * @param isRollover
     *            determines whether rollover image dimension should be retrieved or not
     * @param imageProperty
     *            the image property
     * @return returns the Dimension of the given DAM Resource or null if no such resource exists.
     */

    public static Dimension getImgDimensions(final Resource resource,
                                             final boolean isRollover,
                                             final String imageProperty) {
        final String imgRef = getImgRef(imageProperty, isRollover);
        return getImgDimensions(imgRef, resource.getResourceResolver());
    }

    /**
     * Returns the dimensions of a given image resource in DAM.
     * 
     * @param pathToImageInDAM
     *            path to the image in DAM
     * @param resolver
     *            A resource resolver.
     * @return The dimensions of the image (width and height).
     */
    public static Dimension getImgDimensions(final String pathToImageInDAM,
                                             final ResourceResolver resolver) {
        Dimension imgDimension = null;
        if (StringUtils.isNotBlank(pathToImageInDAM) && pathToImageInDAM.startsWith(PATH_TO_DAM)) {
            final Resource metaData = resolver.resolve(pathToImageInDAM + "/jcr:content/metadata");
            if (null != metaData && !(metaData instanceof NonExistingResource)) {
                final ValueMap properties = ResourceUtil.getValueMap(metaData);
                imgDimension = new Dimension();
                final Long height = properties.get("tiff:ImageLength", Long.class);
                final Long width = properties.get("tiff:ImageWidth", Long.class);
                if (null != height) {
                    imgDimension.height = height.intValue();
                }
                if (null != width) {
                    imgDimension.width = width.intValue();
                }
            }
        }
        return imgDimension;

    }

    /**
     * Returns image height.
     * 
     * @param resource
     *            resource
     * @param imgRef
     *            imgRef
     * @return imgHeight
     */
    private static String getImgHeight(final Resource resource,
                                       final String imgRef) {
        return getTiff(resource, imgRef, "tiff:ImageLength");
    }

    /**
     * Returns image width.
     * 
     * @param resource
     *            resource
     * @param imgRef
     *            imgRef
     * @return imgWidth
     */
    private static String getImgWidth(final Resource resource,
                                      final String imgRef) {
        return getTiff(resource, imgRef, "tiff:ImageWidth");
    }

    /**
     * Returns either value of tiff:ImageWidth or tiff:ImageLength, depending on passed property.
     * 
     * @param resource
     *            the current resource
     * @param imgRef
     *            the image reference
     * @param property
     *            the tiff property to get value from
     * @return value of tiff property
     */
    private static String getTiff(final Resource resource,
                                  final String imgRef,
                                  final String property) {
        String tiff = "";
        if (StringUtils.isNotEmpty(imgRef)) {
            final Resource img = resource.getResourceResolver().resolve(imgRef + "/jcr:content/metadata");
            if (null != img && !(img instanceof NonExistingResource)) {
                tiff = ResourceUtil.getValueMap(img).get(property, "");
            }
        }

        return tiff;
    }

    /**
     * Returns image height.
     * 
     * @param resource
     *            resource
     * @param properties
     *            properties
     * @return imgHeight
     */
    public static String getImgHeight(final Resource resource,
                                      final ValueMap properties) {
        return getImgHeight(resource, properties, false);
    }

    /**
     * Returns image height.
     * 
     * @param resource
     *            resource
     * @param properties
     *            properties
     * @param isRollover
     *            determines whether rollover image height should be retrieved or not
     * @return imgHeight
     */
    public static String getImgHeight(final Resource resource,
                                      final ValueMap properties,
                                      final boolean isRollover) {
        final String imgRef = getImgRef(properties, isRollover);
        return getImgHeight(resource, imgRef);
    }

    /**
     * Returns image url.
     * 
     * @param resource
     *            resource
     * @param imgRef
     *            imgRef
     * @param name
     *            name
     * @return imgUrl
     */
    private static String getImgUrl(final Resource resource,
                                    final String imgRef,
                                    final String name) {
        String imgUrl = null;
        if (StringUtils.isNotBlank(imgRef)) {
            if (imgRef.startsWith("/content/")) {
                if (!ResourceUtil.isNonExistingResource(resource.getResourceResolver().resolve(imgRef))) {
                    imgUrl = imgRef;
                }
            } else {
                imgUrl = imgRef;
            }
        } else {
            Resource img = resource.getResourceResolver().resolve(
                    resource.getPath() + (StringUtils.endsWith(resource.getPath(), "/image") ? "/jcr:content" : "/image/jcr:content"));
            if (null == img || ResourceUtil.isNonExistingResource(img)) {
                img = resource.getResourceResolver().resolve(
                        resource.getPath() + (StringUtils.endsWith(resource.getPath(), "/image") ? "/file/jcr:content" : "/image/file/jcr:content"));
            }
            if (null != img && !ResourceUtil.isNonExistingResource(img)) {
                String ext = "gif";
                final String mimeType = ResourceUtil.getValueMap(img).get("jcr:mimeType", String.class);
                if (null != mimeType) {
                    ext = mimeType.substring(mimeType.indexOf('/') + 1);
                }
                imgUrl = img.getPath() + ".res/" + name + "." + ext;
            }
        }
        return imgUrl;
    }

    /**
     * Returns image url.
     * 
     * @param resource
     *            resource
     * @param properties
     *            properties
     * @param name
     *            name
     * @return the img url or <code>null</code> if url can't be determined
     */
    public static String getImgUrl(final Resource resource,
                                   final ValueMap properties,
                                   final String name) {
        return getImgUrl(resource, properties, name, false);
    }

    /**
     * Returns image url.
     * 
     * @param resource
     *            resource
     * @param properties
     *            properties
     * @param name
     *            name
     * @param isRollover
     *            determines whether rollover image url should be retrieved or not
     * @return the img url or <code>null</code> if url can't be determined
     */
    public static String getImgUrl(final Resource resource,
                                   final ValueMap properties,
                                   final String name,
                                   final boolean isRollover) {
        final String imgRef = getImgRef(properties, isRollover);
        return getImgUrl(resource, imgRef, name);
    }

    /**
     * Returns the image url
     * 
     * @param resource
     *            the resource
     * @param imageProperty
     *            the image property
     * @param name
     *            the resource name
     * @param isRollover
     *            determines whether rollover image url should be retrieved or not
     * @return
     */
    public static String getImgUrl(final Resource resource,
                                   final String imageProperty,
                                   final String name,
                                   final boolean isRollover) {

        final String imgRef = getImgRef(imageProperty, isRollover);
        return getImgUrl(resource, imgRef, name);

    }

    /**
     * Returns image width.
     * 
     * @param resource
     *            resource
     * @param properties
     *            properties
     * @return imgWidth
     */
    public static String getImgWidth(final Resource resource,
                                     final ValueMap properties) {
        return getImgWidth(resource, properties, false);
    }

    /**
     * Returns image width.
     * 
     * @param resource
     *            resource
     * @param properties
     *            properties
     * @param isRollover
     *            determines whether rollover image width should be retrieved or not
     * @return imgWidth
     */
    public static String getImgWidth(final Resource resource,
                                     final ValueMap properties,
                                     final boolean isRollover) {
        final String imgRef = getImgRef(properties, isRollover);
        return getImgWidth(resource, imgRef);
    }

    /**
     * Returns image reference.
     * 
     * @param properties
     *            the value map
     * @param isRollover
     *            is rollover reference
     * @return image reference
     */
    private static String getImgRef(final ValueMap properties,
                                    final boolean isRollover) {
        String imgRef = StringUtils.EMPTY;
        if (isRollover) {
            if (properties.containsKey("imageRolloverReference")) {
                imgRef = properties.get("imageRolloverReference", "");
            } else if (properties.containsKey("fileRolloverReference")) {
                imgRef = properties.get("fileRolloverReference", "");
            }
        } else {
            if (properties.containsKey("imageReference")) {
                imgRef = properties.get("imageReference", "");
            } else if (properties.containsKey("fileReference")) {
                imgRef = properties.get("fileReference", "");
            }
        }
        return imgRef;
    }

    /**
     * Returns image Reference
     * 
     * @param propertyName
     *            String for image property
     * @param isRollover
     *            boolean true if isRollover
     * @return
     */
    private static String getImgRef(final String propertyName,
                                    final boolean isRollover) {
        String imgRef = StringUtils.EMPTY;

        if (StringUtils.isNotEmpty(propertyName)) {
            imgRef = propertyName;
        }

        return imgRef;
    }

    /**
     * Private constructor to prevent instantiation of this class.
     */
    private MediaUtil() {
        throw new AssertionError("This class is not ment to be instantiated.");
    }
}
