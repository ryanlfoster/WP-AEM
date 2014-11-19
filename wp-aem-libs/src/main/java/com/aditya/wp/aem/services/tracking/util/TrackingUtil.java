/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.tracking.util;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.gmwp.aem.services.tracking.data.OmnitureVariableContainer;
import com.aditya.gmwp.aem.services.tracking.data.OmnitureVariables;
import com.aditya.gmwp.aem.services.tracking.data.TrackingData;
import com.aditya.gmwp.aem.services.tracking.model.TrackingModel;
import com.aditya.gmwp.aem.utils.html.HtmlFilterType;
import com.aditya.gmwp.aem.utils.html.HtmlUtil;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class TrackingUtil {

	private static final Logger LOG = LoggerFactory.getLogger(TrackingUtil.class);
	private static final String TRACKING_XML_PROP_NAME = "trackingxml";

	private TrackingUtil() {
	}

	/**
	 * Returns whether tracking model and basic things like s_account or s_code path are available.
	 * 
	 * @param model
	 *            the tracking model to check for
	 * @return available
	 */
	public static boolean isAvailable(final TrackingModel model) {
		if (null == model || StringUtils.isEmpty(model.getSAccount()) || StringUtils.isEmpty(model.getSCodePath())) {
			return false;
		}

		return true;
	}

	/**
	 * Retrieves the JCR data from this.currentPage.
	 * 
	 * @return a List of TrackingData if possible, null otherwise
	 */
	public static List<TrackingData> getTrackingDataListFrom(final Page page) {

		List<TrackingData> trackingDataList = null;
		try {
			final Resource trackingXmlResource = page.getContentResource(TRACKING_XML_PROP_NAME);

			if (null != trackingXmlResource && !(trackingXmlResource instanceof NonExistingResource)) {
				final Resource trackingXmlResourceChild = trackingXmlResource.getChild(JcrConstants.JCR_CONTENT);
				final Node node;
				if (null != trackingXmlResourceChild && !(trackingXmlResourceChild instanceof NonExistingResource)) {
					node = trackingXmlResourceChild.adaptTo(Node.class);
				} else {
					node = trackingXmlResource.adaptTo(Node.class);
				}
				final Property prop = node.getProperty(JcrConstants.JCR_DATA);
				if (null != prop) {
					final InputStream in = prop.getBinary().getStream();
					if (null != in) {
						trackingDataList = new TrackingXmlParser().parseXML(in);
					}
				}
			}
		} catch (RepositoryException e) {
			LOG.error("Unable to write tracking JSON, reading tracking XML data from CRX failed:", e);
		} catch (XMLStreamException e) {
			LOG.error("Unable to write tracking JSON, loading or parsing XML tracking data failed:", e);
		}

		return trackingDataList;
	}

	/**
	 * Initializes an empty map for the omniture variables.
	 * 
	 * @param variablesToBeInitialized
	 *            all variables for which an initial field should be created.
	 * @return the map
	 */
	public static SortedMap<OmnitureVariables, OmnitureVariableContainer> initializeVariableMap(final OmnitureVariables... variablesToBeInitialized) {

		SortedMap<OmnitureVariables, OmnitureVariableContainer> variables = new TreeMap<OmnitureVariables, OmnitureVariableContainer>();
		for (OmnitureVariables variable : variablesToBeInitialized) {
			variables.put(variable, new OmnitureVariableContainer(variable.getJavaScriptVariableName(), variable.getVariableDescription()));
		}

		return variables;
	}

	/**
	 * Returns a valid title to be used in tracking
	 * 
	 * @param title
	 *            the title to validate
	 * @return the validated title
	 */
	public static String getValidTitle(final String title) {
		if (StringUtils.isBlank(title)) {
			return title;
		}

		String t = HtmlUtil.executeFiltering(title, HtmlFilterType.STRIP_ALL, false);
		t = t.trim();
		t = t.toLowerCase();
		t = t.replaceAll("[&/\\ \r\n]", "_").replaceAll("amp;", "_");
		return t;
	}

	/**
	 * Identifies what area of a page a teaser is on.
	 * 
	 * @param path
	 * @return the {@link TeaserLocation} if identifiable, or {@link TeaserLocation#UNKNOWN} if not
	 */
	public static TeaserLocation identifyTeaserLocation(final String path) {
		if (!path.isEmpty()) {
			if (path.contains("/contentArea/")) {
				return TeaserLocation.IN_CONTENT_AREA;
			} else if (path.contains("/teaserArea/")) {
				return TeaserLocation.IN_TEASER_LINKLIST;
			} else if (path.contains("/catwalk/image_parsys/")) {
				return TeaserLocation.BELOW_SERVICE_ICONS;
			} else if (path.contains("/catwalk/mm_mos_c1")) {
				return TeaserLocation.SCROLLER_ON_CATWALK;
			} else if (path.contains("/nav_footer_c1")) {
				return TeaserLocation.NAV_FOOTER;
			}
		}

		return TeaserLocation.UNKNOWN;
	}

	/**
	 * Assembles the "page name" for the given page. The page name is not a name in the usual sense,
	 * but rather a concatenation of different attributes, mostly shortened to two-letter codes.
	 * Example: for the german index page at http://www.opel.de/, this attribute evaluates to
	 * "op:eu:de:de:index".
	 * 
	 * @param currentPage
	 *            the page to evaluate for.
	 * @param lslrLocale
	 *            the locale to include in the page name.
	 * @return the computed page name attribute
	 */
	public static String getPageName(final Page currentPage,
	                                 final Locale lslrLocale) {
		return new PagePathAssembler(currentPage.getPath()).createOmniturePageName(lslrLocale);
	}
}
