/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.model;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.aditya.wp.aem.wrapper.DeepResolvingResourceUtil;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class AdvancedGalleryModel {

    /**
     * Enum holding possible indicator types their corresponding css class to set in frontend.
     */
    public enum Indicator {
        DOTS("dots", "indicator_dot", "ui_navigation_img") {

            /*
             * (non-Javadoc)
             * @see com.gm.gssm.gmds.cq.model.gallery.AdvancedGallery.Indicator#getIsDots()
             */
            @Override
            public boolean getIsDots() {
                return true;
            }
        },
        NUMBERS("numbers", "indicator_number", "ui_navigation_numbers") {

            /*
             * (non-Javadoc)
             * @see com.gm.gssm.gmds.cq.model.gallery.AdvancedGallery.Indicator#getIsNumbers()
             */
            @Override
            public boolean getIsNumbers() {
                return true;
            }
        },
        THUMBNAILS("thumbnails", "indicator_thumbnails", "ui_navigation_thumb") {

            /*
             * (non-Javadoc)
             * @see com.gm.gssm.gmds.cq.model.gallery.AdvancedGallery.Indicator#getIsThumbnails()
             */
            @Override
            public boolean getIsThumbnails() {
                return true;
            }
        },
        NO("no", StringUtils.EMPTY, "ui_navigation_none") {

            /*
             * (non-Javadoc)
             * @see com.gm.gssm.gmds.cq.model.gallery.AdvancedGallery.Indicator#getIsNo()
             */
            @Override
            public boolean getIsNo() {
                return true;
            }
        };

        /**
         * Returns the <code>Indicator</code> by type.
         * 
         * @param type
         *            the type to look for
         * @return the <code>Indicator</code> or <code>Indicator.DOTS</code> if no appropriate enum found
         */
        public static Indicator fromString(final String type) {
            Indicator indicator = Indicator.DOTS;
            for (final Indicator i : values()) {
                if (i.getType().equals(type)) {
                    indicator = i;
                    break;
                }
            }
            return indicator;
        }

        /** the default css class to set in frontend. */
        private final String cssClass;
        /** the indicator type. */
        private final String type;
        /** the navigation css class to set in frontend. */
        private final String navCssClass;

        /**
         * Constructor.
         * 
         * @param type
         *            the type
         * @param cssClass
         *            the css class
         * @param navCssClass
         *            the navigation css class
         */
        private Indicator(final String type, final String cssClass, final String navCssClass) {
            this.type = type;
            this.cssClass = cssClass;
            this.navCssClass = navCssClass;
        }

        /**
         * Returns the css class.
         * 
         * @return the cssClass
         */
        public final String getCssClass() {
            return this.cssClass;
        }

        /**
         * Returns the navigation css class.
         * 
         * @return the navCssClass
         */
        public final String getNavCssClass() {
            return this.navCssClass;
        }

        /**
         * Returns whether indicator is Indicator.DOTS.
         * 
         * @return is dots
         */
        public boolean getIsDots() {
            return false;
        }

        /**
         * Returns whether indicator is Indicator.NUMBERS.
         * 
         * @return is numbers
         */
        public boolean getIsNumbers() {
            return false;
        }

        /**
         * Returns whether indicator is Indicator.THUMBNAILS.
         * 
         * @return is thumbnails
         */
        public boolean getIsThumbnails() {
            return false;
        }

        /**
         * Returns whether indicator is Indicator.NO.
         * 
         * @return is no indicator
         */
        public boolean getIsNo() {
            return false;
        }

        /**
         * Returns the type.
         * 
         * @return the type
         */
        public final String getType() {
            return this.type;
        }
    }

    /**
     * Enum holding possible gallery types and their corresponding css class to set in frontend.
     */
    public enum Type {
        MULTIMEDIA("multimedia", StringUtils.EMPTY) {

            /*
             * (non-Javadoc)
             * @see com.gm.gssm.gmds.cq.model.gallery.AdvancedGallery.Type#getIsMultimedia()
             */
            @Override
            public boolean getIsMultimedia() {
                return true;
            }
        },
        SCROLLER("image_scroller", StringUtils.EMPTY) {

            /*
             * (non-Javadoc)
             * @see com.gm.gssm.gmds.cq.model.gallery.AdvancedGallery.Type#getIsScroller()
             */
            @Override
            public boolean getIsScroller() {
                return true;
            }
        },
        SCROLLER_WITH_LIGHTBOX("image_scroller_with_lightbox", "enlarge") {

            /*
             * (non-Javadoc)
             * @see com.gm.gssm.gmds.cq.model.gallery.AdvancedGallery.Type#getIsScrollerWithLightbox()
             */
            @Override
            public boolean getIsScrollerWithLightbox() {
                return true;
            }
        };

        /**
         * Returns the <code>Type</code> by type.
         * 
         * @param type
         *            the type to look for
         * @return the <code>Type</code> or <code>Type.MULTIMEDIA</code> if no appropriate enum found
         */
        public static Type fromString(final String type) {
            Type galleryType = Type.MULTIMEDIA;
            for (final Type t : values()) {
                if (t.getType().equals(type)) {
                    galleryType = t;
                    break;
                }
            }
            return galleryType;
        }

        /** the associated css class to set in frontend. */
        private final String cssClass;

        /** the gallery type. */
        private final String type;

        /**
         * Csontructor.
         * 
         * @param type
         *            the type
         * @param cssClass
         *            the cssClass
         */
        private Type(final String type, final String cssClass) {
            this.type = type;
            this.cssClass = cssClass;
        }

        /**
         * Returns the css class.
         * 
         * @return the cssClass
         */
        public final String getCssClass() {
            return this.cssClass;
        }

        /**
         * Returns whether type is Type.MULTIMEDIA.
         * 
         * @return is multimedia
         */
        public boolean getIsMultimedia() {
            return false;
        }

        /**
         * Returns whether type is Type.SCROLLER.
         * 
         * @return is scroller
         */
        public boolean getIsScroller() {
            return false;
        }

        /**
         * Returns whether type is Type.SCROLLER_WITH_LIGHTBOX.
         * 
         * @return is scroller with lightbox
         */
        public boolean getIsScrollerWithLightbox() {
            return false;
        }

        /**
         * Returns the type.
         * 
         * @return the type
         */
        public final String getType() {
            return this.type;
        }
    }

    /** the localized enlarge text. */
    private String enlargeTxt;

    /** the list of images to display in scroller and lightbox. */
    //private final LightboxImageScroller galleryItems = new LightboxImageScroller();

    /** the localized gallery text. */
    private String galleryTxt;

    /** the localized images text. */
    private String imagesTxt;

    /** the gallery indicator type. */
    private Indicator indicator;

    /** the t17d lightbox source to retrieve scroller images. */
    private String source;

    /** the gallery type. */
    private Type type;

    /**
     * Constructor. Creates an advanced gallery out of the current resource.
     * 
     * @param resource
     *            the resource
     */
    public AdvancedGalleryModel(final Resource resource) {
        createGallery(resource);
    }

    /**
     * Constructor. Creates an advanced gallery out of the passed parameter.
     * 
     * @param internalLink
     *            the internal link pointing to a t17d template.
     * @param type
     *            the gallery type
     * @param indicator
     *            the indicator type
     */
    public AdvancedGalleryModel(final String internalLink, final Type type, final Indicator indicator) {
        this.source = internalLink;
        this.type = type;
        this.indicator = indicator;
    }

    /**
     * Helper method to set <code>AdvancedGallery</code> values and eventually create it from.
     * 
     * @param resource
     *            the resource to get values from
     */
    private void createGallery(final Resource resource) {
        if (null == resource) {
            throw new IllegalArgumentException("Missing parameter: resource");
        }
        final ValueMap properties = DeepResolvingResourceUtil.getValueMap(resource);
        this.source = properties.get("advGallerySrc", String.class);
        this.type = Type.fromString(properties.get("advGalleryType", String.class));
        this.indicator = Indicator.fromString(properties.get("advGalleryIndicator", String.class));
    }

    /**
     * Returns the localized enlarge text.
     * 
     * @return the enlargeTxt
     */
    public final String getEnlargeTxt() {
        return this.enlargeTxt;
    }

    /**
     * Returns an unmodifiable list of gallery items.
     * 
     * @return list of images
     */
    //public final LightboxImageScroller getGalleryItems() {
    //    return this.galleryItems;
    //}

    /**
     * Returns the gallery text.
     * 
     * @return the galleryTxt
     */
    public final String getGalleryTxt() {
        return this.galleryTxt;
    }

    /**
     * Returns the localized images text.
     * 
     * @return the imagesTxt
     */
    public final String getImagesTxt() {
        return this.imagesTxt;
    }

    /**
     * Returns the gallery indicator.
     * 
     * @return the indicator
     */
    public final Indicator getIndicator() {
        return this.indicator;
    }

    /**
     * Returns the source.
     * 
     * @return the source
     */
    public final String getSource() {
        return this.source;
    }

    /**
     * Returns the gallery type.
     * 
     * @return the type
     */
    public final Type getType() {
        return this.type;
    }

    /**
     * Sets the localized enlarge text.
     * 
     * @param enlargeTxt
     *            the enlargeTxt to set
     */
    public final void setEnlargeTxt(final String enlargeTxt) {
        this.enlargeTxt = enlargeTxt;
    }

    /**
     * Adds a list of gallery items provided by <code>LightboxImageScroller</code> class.
     * 
     * @param galleryItems
     *            the gallery items to add
     */
    //public final void setGalleryItems(final LightboxImageScroller galleryItems) {
    //    this.galleryItems.addAll(galleryItems);
    //}

    /**
     * Sets the gallery text.
     * 
     * @param galleryTxt
     *            the galleryTxt to set
     */
    public final void setGalleryTxt(final String galleryTxt) {
        this.galleryTxt = galleryTxt;
    }

    /**
     * Sets the localized images text.
     * 
     * @param imagesTxt
     *            the imagesTxt to set
     */
    public final void setImagesTxt(final String imagesTxt) {
        this.imagesTxt = imagesTxt;
    }
}
