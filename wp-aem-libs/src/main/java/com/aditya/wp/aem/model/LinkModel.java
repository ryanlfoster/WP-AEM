/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.gmwp.aem.services.config.LinkBehaviorService;
import com.aditya.gmwp.aem.services.core.ServiceProvider;
import com.aditya.gmwp.aem.utils.SelectorUtil;
import com.aditya.gmwp.aem.utils.html.LinkBehavior;
import com.aditya.gmwp.aem.utils.uri.UriBuilder;
import com.aditya.gmwp.aem.wrapper.DeepResolvingResourceUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class LinkModel {
	private static final String LINK_BEHAVIOR = "link_behavior";
	private static final Logger LOG = LoggerFactory.getLogger(LinkModel.class);
	protected String anchor;
	protected String directExternalLink;
	protected String externalLink;
	protected ExternalLinkModel externalLinkModel;
	protected String inPageLink;
	protected String internalLink;

	/** the glossary link pointing to a cnt_glossary_item_c1 resource path. */
	protected String glossaryLink;

	/** The link tracking data. */
	protected final List<LinkTrackingData> linkTrackingData = new ArrayList<LinkTrackingData>();

	protected Map<String, Set<String>> parameters;
	protected List<String> selectors;
	protected String text;
	protected String title;
	protected String disclaimerLink;
	protected boolean followRedirect = true;

	protected String id;
	private LinkBehavior behavior;

	public LinkBehavior getBehavior() {
		return this.behavior;
	}

	/**
	 * Creates a new instance invoking copy constructor.
	 *
	 * @param linkModel
	 *            the the link model
	 * @return link model
	 */
	public static LinkModel newInstance(final LinkModel linkModel) {
		return new LinkModel(linkModel);
	}

	/**
	 * Copy constructor.
	 *
	 * @param linkModel
	 *            the link model
	 */
	private LinkModel(final LinkModel linkModel) {
		this.anchor = linkModel.anchor;
		this.directExternalLink = linkModel.directExternalLink;
		this.disclaimerLink = linkModel.disclaimerLink;
		this.externalLink = linkModel.externalLink;
		this.externalLinkModel = linkModel.externalLinkModel;
		this.followRedirect = linkModel.followRedirect;
		this.glossaryLink = linkModel.glossaryLink;
		this.id = linkModel.id;
		this.inPageLink = linkModel.inPageLink;
		this.internalLink = linkModel.internalLink;
		this.linkTrackingData.addAll(linkModel.linkTrackingData);
		this.parameters = linkModel.parameters;
		this.selectors = linkModel.selectors;
		this.text = linkModel.text;
		this.title = linkModel.title;

	}

	/**
	 * Constructor for an empty LinkModel. Use setters to fill this Object.
	 */
	public LinkModel() {
	}

	/**
	 * Constructor to create a minimum valid LinkModel so the link tag can be used. Existing
	 * selectors on the external Link will be removed.
	 *
	 * @param title
	 *            the title
	 * @param internalLink
	 *            the internal link
	 */
	public LinkModel(final String title, final String internalLink) {
		setTitle(title);
		setInternalLink(internalLink);
	}

	/**
	 * @param title
	 *            the title
	 * @param internalLink
	 *            the internal link
	 * @param noEscape
	 *            boolean to not escape HTML
	 */
	public LinkModel(final String title, final String internalLink, final boolean noEscape) {
		// if true, do not escape html
		if (noEscape) {
			this.title = title;
			setInternalLink(internalLink);
		} else {
			setTitle(title);
			setInternalLink(internalLink);
		}
	}

	/**
	 * Constructor with resource.
	 *
	 * @param resource
	 *            resource of the link
	 */
	public LinkModel(final Resource resource) {
		this(DeepResolvingResourceUtil.getValueMap(resource), resource);
	}

	/**
	 * Constructor with custom properties.
	 *
	 * @param properties
	 *            resourceProperties custom properties
	 * @param resource
	 *            resource of the link
	 */
	public LinkModel(final ValueMap properties, final Resource resource) {
		initLinkBehavior(properties, resource);

		if (properties != null) {
			setTitle(properties.get("linkTitle", String.class));
			final String link = properties.get("internalLink", String.class);
			final String inPage = properties.get("inPageLink", String.class);
			if (link != null) {
				if (link.startsWith("/content")) {
					setInternalLink(link);
					setAnchor(properties.get("deeplinkParam", String.class));
					if (isLinkPointingToSamePage(link, getContainingPage(resource)) && null != inPage) {
						// deeplink into same page, e.g. tabs, anchors, see
						// com.gm.gssm.gmds.cq.services.linkwriter.LinkWriterService#rewriteLink
						setInternalLink(null);
					}
				} else {
					if (LOG.isInfoEnabled()) {
						LOG.info("the internal link ('" + link + "') is not a valid internal link");
					}
				}
			}

			this.glossaryLink = properties.get("glossaryLink", String.class);
			this.inPageLink = inPage;
			this.text = properties.get("linkText", String.class);
			this.externalLink = properties.get("externalLink", String.class);
			this.directExternalLink = properties.get("directExternalLink", String.class);
			setDisclaimerLink(properties.get("disclaimer", String.class));
			extractParamsFromLinkParamProperty(properties.get("link_params", String[].class));
			initExternalLinkModel(resource.getResourceResolver());
		}
	}

	public void initLinkBehavior(final ValueMap properties,
	                             final Resource resource) {
		LinkBehaviorService linkBehaviorService = ServiceProvider.INSTANCE.getService(LinkBehaviorService.class);
		if (linkBehaviorService != null) {
			String linkBehaviorName = properties.get(LINK_BEHAVIOR, String.class);
			LOG.debug("Config Name:" + linkBehaviorName);
			if (linkBehaviorName != null) {
				LinkBehavior linkBehavior = linkBehaviorService.getLinkConfigurationBehavior(getCurrentPage(resource), linkBehaviorName);
				setBehavior(linkBehavior);
			}
		}
	}

	private Page getCurrentPage(final Resource resource) {
		ResourceResolver resourceResolver = resource.getResourceResolver();
		PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
		String path = resource.getPath();
		return pageManager.getContainingPage(path);
	}

	/**
	 * Returns whether link is pointing to the same page.
	 *
	 * @param link
	 *            the link
	 * @param page
	 *            the page
	 * @return pointing to same page
	 */
	private boolean isLinkPointingToSamePage(final String link,
	                                         final Page page) {
		return null != page && link.equals(page.getPath());
	}

	/**
	 * Returns the containing page of the resource.
	 *
	 * @param resource
	 *            the resource
	 * @return page
	 */
	private Page getContainingPage(final Resource resource) {
		return resource.getResourceResolver().adaptTo(PageManager.class).getContainingPage(resource);
	}

	/**
	 * Adds all parameters (where each parameter can have multiple values).
	 *
	 * @param parameters
	 *            the parameters
	 */
	public final void addAllMultiParameters(final Map<String, Set<String>> parameters) {
		if (parameters != null) {
			for (final Map.Entry<String, Set<String>> entry : parameters.entrySet()) {
				if (entry != null && entry.getValue() != null) {
					for (final String value : entry.getValue()) {
						if (value != null) {
							addParameter(entry.getKey(), value);
						}
					}
				}
			}
		}
	}

	/**
	 * Adds all parameters.
	 *
	 * @param parameters
	 *            the parameters
	 */
	public final void addAllParameters(final Map<String, String> parameters) {
		if (parameters != null) {
			for (final Map.Entry<String, String> entry : parameters.entrySet()) {
				addParameter(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * Adds the link tracking data.
	 *
	 * @param data
	 *            link tracking data
	 */
	public final void addLinkTrackingData(final LinkTrackingData data) {
		this.linkTrackingData.add(data);
	}

	/**
	 * Adds the query parameter.
	 *
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public final void addParameter(final String key,
	                               final String value) {
		if (StringUtils.isNotEmpty(key)) {
			if (this.parameters == null) {
				this.parameters = new HashMap<String, Set<String>>();
			}
			Set<String> values = this.parameters.get(key);
			if (values == null) {
				values = new HashSet<String>();
			}
			values.add(value);
			this.parameters.put(key, values);
		}
	}

	/**
	 * adds a selector to the selector list.
	 *
	 * @param selector
	 *            the selector that has to be added
	 */
	public final void addSelector(final String selector) {
		getSelectorList().add(selector);
	}

	/**
	 * adds a key value pair as one String separated by a equal sign to the selector list.
	 *
	 * @param key
	 *            the key as String
	 * @param value
	 *            the value as String
	 */
	public final void addSelector(final String key,
	                              final String value) {
		getSelectorList().add(key + SelectorUtil.ASSIGNMENT_SYMBOL + value);
	}

	/**
	 * Extract selectors, params and anchor from link_params property.
	 *
	 * @param linkParams
	 *            the link params
	 */
	private void extractParamsFromLinkParamProperty(final String[] linkParams) {
		if (linkParams != null) {
			for (final String param : linkParams) {
				if (param != null && !"".endsWith(param)) {
					final char firstChar = param.charAt(0);
					if (firstChar == '#') {
						// anchor
						setAnchor(param);
					} else if (firstChar == '.') {
						// selector
						addSelector(param);
					} else if (param.contains("=")) {
						// parameter
						final String[] pair = param.split("=");
						if (pair.length == 2) {
							addParameter(pair[0], pair[1]);
						}
					} else {
						LOG.info("invalid link param '" + param + "'");
					}
				}
			}
		}
	}

	/**
	 * Gets the anchor.
	 *
	 * @return the anchor
	 */
	public final String getAnchor() {
		return this.anchor;
	}

	/**
	 * return external link.
	 *
	 * @return the externallink
	 */
	public final String getExternalLink() {
		return this.externalLink;
	}

	/**
	 * Gets the stored external link.
	 *
	 * @return the externalLinkModel
	 */
	public final ExternalLinkModel getExternalLinkModel() {
		return this.externalLinkModel;
	}

	/**
	 * Returns <code>true</code> if any of the following fields is set in the <code>LinkModel</code>
	 * .
	 * <ul>
	 * <li>an internal link
	 * <li>an external link
	 * <li>an in page link
	 * <li>a glossary link
	 * <li>a discalimer link
	 * </ul>
	 *
	 * @return <code>true</code> if the <code>LinkModel</code> has any content
	 */
	public final boolean getHasContent() {
		return this.internalLink != null || this.externalLinkModel != null || this.inPageLink != null || this.glossaryLink != null
		        || this.disclaimerLink != null;
	}

	/**
	 * Gets the in page link.
	 *
	 * @return the inPageLink
	 */
	public final String getInPageLink() {
		return this.inPageLink;
	}

	/**
	 * Returns the glossary link.
	 *
	 * @return glossary link
	 */
	public final String getGlossaryLink() {
		return this.glossaryLink;
	}

	/**
	 * Gets the stored internal link / content path.
	 *
	 * @return the internallink
	 */
	public final String getInternalLink() {
		return this.internalLink;
	}

	/**
	 * Returns the path-part of the external link, i.e. the returned string will not contain a
	 * file-extension or a query-string.
	 *
	 * @return see method description
	 */
	public final String getInternalLinkPath() {
		// added null check to avoid null-pointer when page-based layers are used
		if (null != this.internalLink) {
			if (this.internalLink.endsWith(".html")) {
				return this.internalLink.substring(0, this.internalLink.length() - ".html".length());
			} else if (this.internalLink.contains(".html?")) {
				final int index = this.internalLink.indexOf(".html?");
				return this.internalLink.substring(0, index);
			} else if (this.internalLink.contains(".html#")) {
				final int index = this.internalLink.indexOf(".html#");
				return this.internalLink.substring(0, index);
			}
		}
		return this.internalLink;
	}

	/**
	 * Gets the link tracking data.
	 *
	 * @return link tracking data.
	 */
	public final List<LinkTrackingData> getLinkTrackingData() {
		return this.linkTrackingData;
	}

	/**
	 * Gets the parameters.
	 *
	 * @return the parameters
	 */
	public final Map<String, Set<String>> getParameters() {
		return this.parameters;
	}

	public final void setSelectorList(final List<String> selectors) {
		this.selectors = selectors;
	}

	/**
	 * returns the selectors as a list.
	 *
	 * @return the <code>List</code> with the selectors
	 */
	public final List<String> getSelectorList() {
		if (this.selectors == null) {
			this.selectors = new ArrayList<String>();
		}
		return this.selectors;
	}

	/**
	 * returns all selectors from the selector list in one String.
	 *
	 * @return a <code>String</code> starting with a point and containing all selectors separated by
	 *         points
	 */
	public final String getSelectorListAsString() {
		final StringBuilder selectorString = new StringBuilder();
		if (!getSelectorList().isEmpty()) {
			final ListIterator<String> lit = getSelectorList().listIterator();
			while (lit.hasNext()) {
				selectorString.append("." + lit.next());
			}
		}
		return selectorString.toString();
	}

	/**
	 * Gets the text.
	 *
	 * @return the text maintained in the linkComponent
	 */
	public final String getText() {
		return this.text;
	}

	/**
	 * Gets the title. The alternative text of a Link.
	 *
	 * @return String title
	 */
	public final String getTitle() {
		return this.title;
	}

	/**
	 * Gets the id.
	 *
	 * @return String id
	 */
	public final String getId() {
		return this.id;
	}

	/**
	 * Checks for link tracking data.
	 *
	 * @param type
	 *            the type of link tracking data to check for.
	 * @return whether the list of link tracking data contains an instance of the given type
	 */
	public final boolean hasLinkTrackingData(final Class<? extends LinkTrackingData> type) {
		for (final LinkTrackingData ltd : this.linkTrackingData) {
			if (type.isAssignableFrom(ltd.getClass())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * init externallink Model.
	 *
	 * @param resolver
	 *            a resourceResolver
	 */
	private void initExternalLinkModel(final ResourceResolver resolver) {
		if (StringUtils.isNotEmpty(this.externalLink)) {

			final Resource externalLinkResource = resolver.getResource(this.externalLink);
			// set only external link, if the destination resource is not null
			if (externalLinkResource != null && !ResourceUtil.isNonExistingResource(externalLinkResource)) {
				setExternalLinkModel(new ExternalLinkModel(externalLinkResource));
			} else {
				if (LOG.isInfoEnabled()) {
					LOG.info("destination resource is a NonExistingResource:" + this.externalLink);
				}
			}
		} else if (StringUtils.isNotEmpty(this.directExternalLink)) {
			// set the direct external link which needs no external link library
			setExternalLinkModel(new ExternalLinkModel(this.directExternalLink));
		}
	}

	/**
	 * Returns if this Link should be internal or external. Internal links are preferred. If no
	 * internal or external link is set, the default is true.
	 *
	 * @return internal boolean
	 */
	public final boolean isInternal() {
		if (StringUtils.isNotEmpty(this.internalLink)) {
			return true;
		} else if (this.externalLinkModel == null || StringUtils.isNotEmpty(this.externalLinkModel.getPath())) {
			return false;
		} else if (StringUtils.isNotEmpty(this.directExternalLink)) {
			return false;
		}
		// default
		return true;
	}

	/**
	 * Sets the anchor.
	 *
	 * @param anchor
	 *            the anchor to set
	 */
	public final void setAnchor(final String anchor) {
		this.anchor = anchor;
	}

	/**
	 * Sets the direct external link. That's a direct external link without external link library.
	 *
	 * @param directExternalLink
	 *            the directExternalLink to set
	 */
	public final void setDirectExternalLink(final String directExternalLink) {
		this.directExternalLink = directExternalLink;
	}

	/**
	 * Sets the direct external link. That's a direct external link without external link library.
	 * and calls the initExternalLinkModel method Added 2014.03.04 - used by createExternalLinkModel
	 * in RichTextUtil.java to allow external links that are set with an internal link and hence a
	 * direct external link to display properly
	 *
	 * @param directExternalLink
	 *            the directExternalLink to set
	 */
	public final void setDirectExternalLink(final String directExternalLink,
	                                        final ResourceResolver resolver) {
		this.directExternalLink = directExternalLink;
		initExternalLinkModel(resolver);
	}

	/**
	 * set a external link.
	 *
	 * @param externallink
	 *            external link of the external link page
	 * @param resolver
	 *            a ResourceResolver
	 */
	public final void setExternalLink(final String externallink,
	                                  final ResourceResolver resolver) {
		this.externalLink = externallink;
		initExternalLinkModel(resolver);
	}

	/**
	 * Sets the external link model.
	 *
	 * @param externalLinkModel
	 *            the externalLinkModel to set
	 */
	public final void setExternalLinkModel(final ExternalLinkModel externalLinkModel) {
		this.externalLinkModel = externalLinkModel;
	}

	/**
	 * Sets the in page link.
	 *
	 * @param inPageLink
	 *            the inPageLink to set
	 */
	public final void setInPageLink(final String inPageLink) {
		this.inPageLink = inPageLink;
	}

	/**
	 * Sets the glossary link.
	 *
	 * @param glossaryLink
	 *            the glossaryLink to set
	 */
	public final void setGlossaryLink(final String glossaryLink) {
		this.glossaryLink = glossaryLink;
	}

	/**
	 * Sets the internal link and removes all selectors. Do NOT use this to append selectors, anchor
	 * or query parameters (use the setter and adder).
	 *
	 * @param internallink
	 *            the internallink to set
	 */
	public final void setInternalLink(final String internallink) {
		// validate the internal link. The Internal link has to start with "/content"
		if (StringUtils.startsWith(internallink, "/content")) {
			// internal link should be the pure, technical link and should not be used to add
			// selectors,
			// anchors or params
			final UriBuilder ub = new UriBuilder(internallink);
			this.internalLink = ub.getContentPath();
			setAnchor(ub.getAnchor());
			addAllMultiParameters(ub.getParameters());
			for (final String selector : ub.getSelectors()) {
				addSelector(selector);
			}

		} else {
			if (LOG.isInfoEnabled()) {
				LOG.info("the internal link ('" + internallink + "') is not a valid internal link");
			}
			this.internalLink = null;
		}
	}

	/**
	 * Sets the parameters.
	 *
	 * @param parameters
	 *            the parameters to set
	 */
	public final void setParameters(final Map<String, Set<String>> parameters) {
		this.parameters = parameters;
	}

	/**
	 * sets the selector String. This overwrites the existing selector list!
	 *
	 * @param selector
	 *            the selector that has to be set
	 */
	public final void setSelectorListAsString(final String selector) {
		List<String> selectors = getSelectorList();
		selectors.clear();
		// set the selector String complete without splitting it
		selectors.add(selector);
	}

	/**
	 * Sets the title. The alternative text of a Link.
	 *
	 * @param title
	 *            String
	 */
	public final void setTitle(final String title) {
		this.title = StringEscapeUtils.escapeHtml(title);
	}

	/**
	 * Sets the title. The alternative text of a Link. unescapes html if boolean noEscape is true if
	 * false, escapes html
	 *
	 * @param title
	 *            String
	 * @param noEscape
	 *            boolean
	 */
	public final void setTitle(final String title,
	                           final boolean noEscape) {
		if (noEscape) {
			this.title = title;
		} else {
			this.title = StringEscapeUtils.escapeHtml(title);
		}
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            String
	 */
	public final void setId(final String id) {
		this.id = StringEscapeUtils.escapeHtml(id);
	}

	/**
	 * Sets the id with unescaped html if boolean is true else sets the id with escaped html this
	 * method is used to show cyrillic characters as it is
	 *
	 * @param id
	 *            String
	 * @param noEscape
	 *            boolean
	 */
	public final void setId(final String id,
	                        final boolean noEscape) {

		if (noEscape) {
			this.id = id;
		} else {
			this.id = StringEscapeUtils.escapeHtml(id);
		}

	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("LinkModel [anchor=");
		builder.append(this.anchor);
		builder.append(", directExternalLink=");
		builder.append(this.directExternalLink);
		builder.append(", externalLink=");
		builder.append(this.externalLink);
		builder.append(", externalLinkModel=");
		builder.append(this.externalLinkModel);
		builder.append(", inPageLink=");
		builder.append(this.inPageLink);
		builder.append(", internalLink=");
		builder.append(this.internalLink);
		builder.append(", linkTrackingData=");
		builder.append(this.linkTrackingData);
		builder.append(", parameters=");
		builder.append(this.parameters);
		builder.append(", selectors=");
		builder.append(this.selectors);
		builder.append(", title=");
		builder.append(this.title);
		builder.append(", followRedirect=");
		builder.append(this.followRedirect);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Sets the disclaimer link and internalLink to null.
	 *
	 * @param disclaimerLink
	 *            the new disclaimer link
	 */
	public final void setDisclaimerLink(final String disclaimerLink) {
		this.disclaimerLink = disclaimerLink;
		if (StringUtils.isNotBlank(disclaimerLink)) {
			this.internalLink = null;
		}
	}

	/**
	 * Checks if is disclaimer link.
	 *
	 * @return true, if is disclaimer link
	 */
	public final boolean hasDisclaimerLink() {
		return StringUtils.isNotBlank(this.disclaimerLink);
	}

	/**
	 * Gets the disclaimer link.
	 *
	 * @return the disclaimer link
	 */
	public final String getDisclaimerLink() {
		return this.disclaimerLink;
	}

	/**
	 * Return whether following redirect target is allowed.
	 *
	 * @return is allowed
	 */
	public final boolean isFollowRedirectAllowed() {
		return this.followRedirect;
	}

	/**
	 * Sets the follow redirect.
	 *
	 * @param followRedirect
	 *            the followRedirect to set
	 */
	public final void setFollowRedirect(final boolean followRedirect) {
		this.followRedirect = followRedirect;
	}

	public void setBehavior(final LinkBehavior linkBehavior) {
		this.behavior = linkBehavior;
	}
}
