/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.utils;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.wp.aem.global.AEMTemplateInfo;
import com.aditya.wp.aem.model.LinkModel;
import com.aditya.wp.aem.model.LinkModelConstants;
import com.aditya.wp.aem.wrapper.GMResource;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class PageUtil {

    private static final Logger LOG = LoggerFactory.getLogger(PageUtil.class);

    /**
     * Helper class holding cq page properties.
     */
    private static final class CQPage {

        /** the date the page was last modified. */
        private final Calendar lastModified;

        /** the date the page was last published. */
        private final Calendar lastPublished;

        /** the date the page was last replicated. */
        private final Calendar lastReplicated;

        /** the last replication action of a page. */
        private final String lastReplicationAction;

        /**
         * Constructor.
         * 
         * @param page
         *            the current page
         */
        private CQPage(final Page page) {
            final ValueMap properties = page.getProperties();
            this.lastReplicated = properties.get("cq:lastReplicated", Calendar.class);
            this.lastPublished = properties.get("cq:lastPublished", Calendar.class);
            this.lastModified = properties.get("cq:lastModified", Calendar.class);
            this.lastReplicationAction = properties.get("cq:lastReplicationAction", String.class);
        }
    }

    /**
     * Returns the navigation title. If the Navigation Title is <code>null</code> the page title is
     * returned.
     * 
     * @param page
     *            the page for usage. If page is <code>null</code>, a
     *            <code>NullPointerException</code>will be thrown.
     * @return the title for use in the navigation.
     */
    public static String getNavigationTitleFromPage(final Page page) {
        final String navigationTitle = page.getNavigationTitle();
        return (navigationTitle == null) ? page.getTitle() : navigationTitle;
    }

    /**
     * This method receives a page and iterates through all parent-pages in order to find a T12
     * page. The first T12 on its way is being returned.
     * 
     * @param currentPage
     *            the current page
     * @param parentResourceType
     *            the parent's resourceType
     * @return the first T12 page above the given page
     */
    public static Page findParentTemplate(final Page currentPage,
                                          final String parentResourceType) {
        Page parentPage = null;
        Page cPage = currentPage;
        // get the T12 parent page
        while (cPage != null) {
            final String rType = String.valueOf(cPage.getProperties().get("sling:resourceType"));
            if (parentResourceType.equals(rType)) {
                parentPage = cPage;
                break;
            }
            cPage = cPage.getParent();
        }

        if (parentPage == null) {
            LOG.warn("no parent page found! (resourceType: '" + parentResourceType + "')");
        }

        return parentPage;
    }

    /**
     * Finds parent Template .
     * 
     * @param childPage
     *            child page
     * @param template
     *            Template of page being sort
     * @return page page whose template matches
     */
    public static Page findPageByTemplateFromChild(final Page childPage,
                                                   final AEMTemplateInfo template) {
        Page page = childPage;
        while (page != null && template != null && !template.matchesTemplate(page)) {
            page = page.getParent();
        }
        if (template == null) {
            LOG.warn("no parent page found because template is null");
        } else if (page == null) {
            LOG.warn("no parent page found! (template '" + template.getTemplateName() + "')");
        }
        return (page != null && template.matchesTemplate(page)) ? page : null;
    }

    /**
     * This method checks if a resource is marked as active.
     * 
     * @param currentPage
     *            the current page to check if active
     * @return isPageActive {@link Boolean}
     */
    public static boolean isPageActive(final Page currentPage) {
        boolean isPageActive = false;
        if (null == currentPage) {
            return isPageActive;
        }

        final CQPage page = new CQPage(currentPage);
        if (null != page.lastReplicated && "Activate".equals(page.lastReplicationAction)) {
            isPageActive = true;
        } else if (null != page.lastPublished) {
            isPageActive = true;
        }

        return isPageActive;
    }

    /**
     * This method checks if a resource is marked as modified.
     * 
     * @param currentPage
     *            the current page to check if modified
     * @return isPageModified {@link Boolean}
     */
    public static boolean isPageModified(final Page currentPage) {
        boolean isPageModified = false;
        if (null == currentPage) {
            return isPageModified;
        }

        final CQPage page = new CQPage(currentPage);
        if (null != page.lastModified && null != page.lastReplicated && page.lastReplicated.before(page.lastModified)) {
            isPageModified = true;
        } else if (null != page.lastModified && null != page.lastPublished && page.lastPublished.before(page.lastModified)) {
            isPageModified = true;
        }

        return isPageModified;
    }

    /**
     * This method checks if a resource is marked as new.
     * 
     * @param currentPage
     *            the current page to check if new
     * @return isPageNew {@link Boolean}
     */
    public static boolean isPageNew(final Page currentPage) {
        boolean isPageNew = false;
        if (null == currentPage) {
            return isPageNew;
        }

        final CQPage page = new CQPage(currentPage);
        if (null != page.lastModified && null == page.lastPublished && null == page.lastReplicated) {
            isPageNew = true;
        }

        return isPageNew;
    }

    /**
     * Method that will lookup the property for the given propertyKey in the leafPage and give it
     * back. If the property is not found, it will go up the parent tree and try to find the same
     * property in the parent pages.
     * 
     * @param leafPage
     *            the leaf page
     * @param propertyKey
     *            the property key
     * @return the property from page including ancestors or the empty String
     */
    public static String getPropertyFromPageIncludingAncestors(final Page leafPage,
                                                               final String propertyKey) {

        String resultProperty = leafPage.getProperties().get(propertyKey, StringUtils.EMPTY);

        if (StringUtils.isBlank(resultProperty)) {
            Page ancestor = leafPage.getParent();

            while (ancestor != null && StringUtils.isBlank(resultProperty)) {
                resultProperty = ancestor.getProperties().get(propertyKey, StringUtils.EMPTY);
                ancestor = ancestor.getParent();
            }
        }

        return resultProperty;
    }

    /**
     * Returns a {@link LinkModel} that contains the internal or external link maintained in the
     * page properties and the navigation title as title.
     * 
     * @param page
     *            the {@link Page} where the requested link is maintained in the page properties
     * @return a <code>LinkModel</code> out of the page properties
     */
    public static LinkModel getLinkModelFromPageProperties(final Page page) {
        final LinkModel link = new LinkModel();

        if (page == null) {
            return link;
        }

        // set title
        link.setTitle(getNavigationTitleFromPage(page));

        // set either internal or external link
        final ValueMap properties = page.getProperties();
        if (properties.containsKey(LinkModelConstants.INTERNAL_LINK)) {
            link.setInternalLink(properties.get(LinkModelConstants.INTERNAL_LINK, String.class));
        } else if (properties.containsKey(LinkModelConstants.EXTERNAL_LINK)) {
            final Resource pageResource = page.adaptTo(Resource.class);
            link.setExternalLink(properties.get(LinkModelConstants.EXTERNAL_LINK, String.class), pageResource.getResourceResolver());
        }
        return link;
    }

    /**
     * Checks to see if the given {@link Page} matches the list of given {@link CQTemplateInfo}.
     * 
     * @param page
     *            the {@link Page} whose template is being checked.
     * @param cqTemplateInfos
     *            the list of {@link CQTemplateInfo} that are being checked against.
     * @return true if the given page matches one of the given templates, false otherwise.
     */
    public static boolean matchesTemplate(final Page page,
                                          final AEMTemplateInfo... aemTemplateInfos) {

        for (AEMTemplateInfo cti : aemTemplateInfos) {
            if (cti != null && cti.matchesTemplate(page)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Private constructor to prevent instantiation of this class.
     */
    private PageUtil() {
        throw new AssertionError("This class is not ment to be instantiated.");
    }

    /**
     * Starting from the passed page it follows every redirect/internal link of the page(s) up to a
     * page that has no internal redirect. Loops will be detected and the passed page will be
     * returned.
     * 
     * @param page
     *            the linked page, may be null
     * @return the final target page, may be the passed page or null
     */
    public static Page getFinalTargetPage(final Page page) {
        return getFinalTargetPage(page, new HashSet<Page>());
    }

    /**
     * Lists all the first level children of the given page that have schema.org markup available.
     * 
     * @param currentPage
     *            the page object
     * @param request
     *            the servlet request object
     * @return list of first level children of the given page
     */
    public static List<LinkModel> getFirstLevelChildren(final Page currentPage,
                                                        final HttpServletRequest request) {
        final List<LinkModel> children = new ArrayList<LinkModel>();

        if (currentPage == null) {
            return children;
        }

        final boolean schemaEnabled = SchemaUtil.isSchemaEnabled(currentPage, request);
        if (!schemaEnabled) {
            return children;
        }

        final Resource currentPageRes = currentPage.adaptTo(Resource.class);
        if (currentPageRes == null) {
            return children;
        }

        final Iterator<Resource> itr = currentPageRes.listChildren();
        while (itr.hasNext()) {
            final GMResource res = new GMResource(itr.next());
            if (res.isExisting() && !StringUtils.equals(res.getPath(), currentPage.getContentResource().getPath())) {
                final Page targetPage = getFinalTargetPage(res.adaptTo(Page.class));
                if (targetPage != null) {
                    final boolean schemaAvailable = SchemaUtil.isSchemaMarkupAvailable(targetPage)
                            && !BooleanUtils.toBoolean(targetPage.getProperties().get("disableSchema", "false"));
                    if (schemaAvailable) {
                        final LinkModel linkModel = new LinkModel(getNavigationTitleFromPage(targetPage), targetPage.getPath());
                        children.add(linkModel);
                    }
                }
            }
        }
        return children;
    }

    /**
     * Gets the final page in the link hierarchy for the given page.
     * 
     * @param page
     *            the page object
     * @param passedPages
     *            set of pages in the link hierarchy
     * @return the final target page
     */
    private static Page getFinalTargetPage(final Page page,
                                           final Set<Page> passedPages) {
        if (page != null) {
            passedPages.add(page);
            final String internalLink = page.getProperties().get("internalLink", String.class);
            if (internalLink != null) {
                final Page linkedPage = page.getPageManager().getPage(internalLink);
                if (linkedPage != null && passedPages.contains(linkedPage)) {
                    LOG.warn("Loop detected! '" + linkedPage.getPath() + "' links to itself in the end.");
                    return null;
                }
                return getFinalTargetPage(linkedPage, passedPages);
            }
        }
        return page;
    }
}
