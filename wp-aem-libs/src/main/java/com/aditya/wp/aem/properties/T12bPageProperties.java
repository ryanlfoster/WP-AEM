/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.properties;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public enum T12bPageProperties {

    PAGE_CHARACTERISTICS("pageCharacteristic"),
    SHOW_ALL_STORIES("show_all_stories"),
    SHOW_SUB_PAGES("show_sub_pages"),
    SHOW_SUB_AND_REFERENCED_PAGES("show_sub_and_referenced_pages"),
    MAIN_CATEGORY("mainCategory"),
    SHOW_SECTION_TITLES("showSectionTitles"),
    CATEGORY_CONFIGURATION("categoryConfiguration"),
    ID("id"),
    SELECTED("selected"),
    DISPLAY_TYPE("displayType"),
    DISPLAY_TYPE_STANDARD("standard"),
    DISPLAY_TYPE_SCROLLER("scroller"),
    MULTIMEDIA_MASTHEAD("mh_c1"),
    ENABLE_DYNAMIC_MASTHEAD("enableDynamicMastheads"),
    STORY_SCROLLER("story_scroller");

    private String name;

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Private constructor.
     * 
     * @param name
     *            the name.
     */
    private T12bPageProperties(final String name) {
        this.name = name;
    }

}