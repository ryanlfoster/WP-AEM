/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.core.link;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.aditya.wp.aem.utils.html.HtmlEntities;
import com.aditya.wp.aem.utils.html.HtmlFilterType;
import com.aditya.wp.aem.utils.html.HtmlUtil;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class HTMLLink {
	private static final String ESCAPED_QUOTE = "\" ";
	public static final String BODYSTYLE = "bodystyle";

	private String clazz;
	private String href;
	private String id;
	private String onclick;
	private String rel;
	private String script;
	private String style;
	private String target;
	private String title;
	private boolean isRichSnippet;
	private final Map<String, String> dataAttributes;

	public HTMLLink() {
		this.dataAttributes = new HashMap<String, String>();
	}

	// GMDSPLM-19727-this variable adds "mporgnav" in
	// the anchor link for motionpoint
	private String motionPoint;

	/**
	 * Adds more code to the onclick attribute.
	 * 
	 * @param onclick
	 *            the onclick event
	 */
	public final void addOnclick(final String onclick) {
		if (null == this.onclick) {
			this.onclick = onclick;
		} else {
			this.onclick = this.onclick + onclick;
		}
	}

	/**
	 * Gets the clazz.
	 * 
	 * @return class attribute
	 */
	public final String getClazz() {
		return this.clazz;
	}

	/**
	 * Gets the href.
	 * 
	 * @return href attribute
	 */

	public final String getHref() {
		return this.href;
	}

	/**
	 * Gets the id.
	 * 
	 * @return id attribute
	 */

	public final String getId() {
		return this.id;
	}

	/**
	 * Gets the onclick.
	 * 
	 * @return onclick attribute
	 */

	public final String getOnclick() {
		return this.onclick;
	}

	/**
	 * Gets the rel.
	 * 
	 * @return rel attribute
	 */
	public final String getRel() {
		return this.rel;
	}

	/**
	 * Gets the data toggle.
	 * 
	 * @return the data toggle
	 */
	public final String getDataToggle() {
		return getDataAttribute("toggle");
	}

	/**
	 * Gets the script.
	 * 
	 * @return script attribute
	 */
	public final String getScript() {
		return this.script;
	}

	/**
	 * Gets the style.
	 * 
	 * @return style attribute
	 */
	public final String getStyle() {
		return this.style;
	}

	/**
	 * Gets the target.
	 * 
	 * @return target attribute
	 */
	public final String getTarget() {
		return this.target;
	}

	/**
	 * Gets the title.
	 * 
	 * @return title attribute
	 */
	public final String getTitle() {
		return this.title;
	}

	/**
	 * Gets the carline.
	 * 
	 * @return carline attribute
	 */
	public final String getCarline() {
		return getDataAttribute("carline");
	}

	/**
	 * Gets the bodystyle.
	 * 
	 * @return bodystyle attribute
	 */
	public final String getBodystyle() {
		return getDataAttribute("bodystyle");
	}

	/**
	 * Gets the model year.
	 * 
	 * @return modelyear attribute
	 */
	public final String getModelyear() {
		return getDataAttribute("modelyear");
	}

	/**
	 * Gets the application symbolic name.
	 * 
	 * @return the application
	 */
	public final String getAppSymbolicName() {
		return getDataAttribute("app");
	}

	/**
	 * Gets whether link is rich snippet
	 * 
	 * @return the isRichSnippet
	 */
	public final boolean getIsRichSnippet() {
		return this.isRichSnippet;
	}

	/**
	 * Gets the motionPoint string in the anchor link "mporgnav"
	 * 
	 * @return the string set for motionPoint "mporgnav"
	 */
	public String getMotionPoint() {
		return this.motionPoint;
	}

	/**
	 * Set class attribute.
	 * 
	 * @param clazz
	 *            the class attribute
	 */
	public final void setClazz(final String clazz) {
		this.clazz = clazz;
	}

	/**
	 * Set href attribute.
	 * 
	 * @param href
	 *            the href attribute
	 */
	public final void setHref(final String href) {
		this.href = href;
	}

	/**
	 * Set id attribute.
	 * 
	 * @param id
	 *            the id attribute
	 */
	public final void setId(final String id) {
		this.id = id;
	}

	/**
	 * Sets the data ngdoe callback.
	 * 
	 * @param dataNgdoeCallback
	 *            the new data ngdoe callback
	 */
	public final void setDataNgdoeCallback(final String dataNgdoeCallback) {
		this.dataAttributes.put("ngdoe-callback", dataNgdoeCallback);
	}

	/**
	 * Sets the data glossary callback.
	 * 
	 * @param dataGlossaryCallback
	 *            the data glossary callback
	 */
	public final void setDataGlossaryCallback(final String dataGlossaryCallback) {
		this.dataAttributes.put("glossary-callback", dataGlossaryCallback);
	}

	/**
	 * Set rel attribute.
	 * 
	 * @param rel
	 *            the rel attribute
	 */
	public final void setRel(final String rel) {
		this.rel = rel;
	}

	/**
	 * Sets the data-toggle attribute.
	 * 
	 * @param dataToggle
	 *            the new data-toggle
	 */
	public final void setDataToggle(final String dataToggle) {
		this.dataAttributes.put("toggle", dataToggle);
	}

	/**
	 * Set script tag.
	 * 
	 * @param script
	 *            the script tag
	 */
	public final void setScript(final String script) {
		this.script = script;
	}

	/**
	 * Set style attribute.
	 * 
	 * @param style
	 *            the style attribute
	 */
	public final void setStyle(final String style) {
		this.style = style;
	}

	/**
	 * Set target attribute.
	 * 
	 * @param target
	 *            the target attribute
	 */
	public final void setTarget(final String target) {
		this.target = target;
	}

	/**
	 * Set title attribute.
	 * 
	 * @param title
	 *            the title attribute
	 */
	public final void setTitle(final String title) {
		setTitle(title, false);
	}

	/**
	 * Set the title attribute in the anchor - escapes Html if boolean is false, if boolean to true
	 * do not escape Html to display characters like cyrillic as it is for SEO
	 * 
	 * @param title
	 *            the title attribute
	 * @param noEscape
	 *            boolean to determine escape Html or not
	 */
	public final void setTitle(final String title,
	                           final boolean noEscape) {

		if (noEscape) {
			final String replacedTitle = HtmlUtil.executeFiltering(title, HtmlFilterType.STRIP_ALL, false);
			if (replacedTitle != null) {
				final String replacedTitleWithEntities = replacedTitle.replaceAll(HtmlEntities.AMPERSAND.getSign(), HtmlEntities.AMPERSAND.getEntity());
				this.title = replacedTitleWithEntities;
			} else {
				this.title = replacedTitle;
			}
		} else {
			this.title = StringEscapeUtils.escapeHtml(title);
		}

	}

	/**
	 * Set carline attribute.
	 * 
	 * @param carline
	 *            the carline attribute
	 */
	public final void setCarline(final String carline) {
		this.dataAttributes.put("carline", carline);
	}

	/**
	 * Set bodystyle attribute.
	 * 
	 * @param bodystyle
	 *            the bodystyle attribute
	 */
	public final void setBodystyle(final String bodystyle) {
		this.dataAttributes.put("bodystyle", bodystyle);
	}

	/**
	 * Set model year attribute.
	 * 
	 * @param modelyear
	 *            the modelyear attribute
	 */
	public final void setModelyear(final String modelyear) {
		this.dataAttributes.put("modelyear", modelyear);
	}

	/**
	 * Sets the application symbolic name attribute
	 * 
	 * @param appName
	 *            the applicationName attribute
	 */
	public final void setAppSymbolicName(final String appName) {
		this.dataAttributes.put("app", appName);
	}

	public final void setIsRichSnippet(final boolean isRichSnippet) {
		this.isRichSnippet = isRichSnippet;
	}

	/**
	 * Sets the motionpoint String "mporgnav"
	 * 
	 * @param motionPoint
	 *            motionPoint String "mporgnav"
	 */
	public void setMotionPoint(final String motionPoint) {
		this.motionPoint = motionPoint;
	}

	/**
	 * Return a string representation of the end tag of this {@link HTMLLink}.
	 * 
	 * @return string representation of the end tag of this object.
	 */
	public final String toEndTag() {
		final StringBuilder sb = new StringBuilder();
		if (this.href == null && this.onclick == null) {
			return "";
		}
		sb.append("</a>");
		if (StringUtils.isNotBlank(this.script)) {
			sb.append("<script type=\"text/javascript\">\n");
			sb.append("//<![CDATA[\n");
			sb.append(this.script);
			sb.append("\n//]]>\n");
			sb.append("</script>\n");
		}
		return sb.toString();
	}

	/**
	 * Return a string representation of the start tag of this {@link HTMLLink}.
	 * 
	 * @return string representation of the start tag of this object.
	 */
	public final String toStartTag() {
		final StringBuilder sb = new StringBuilder();
		if (this.href == null && this.onclick == null) {
			return "";
		}
		sb.append("<a ");

		if (StringUtils.isNotBlank(this.motionPoint)) {
			sb.append(this.motionPoint).append("=\"").append(ESCAPED_QUOTE);
		}
		if (StringUtils.isNotBlank(this.href)) {
			sb.append("href=\"").append(this.href).append(ESCAPED_QUOTE);
		}
		if (StringUtils.isNotBlank(this.onclick)) {
			sb.append("onclick=\"").append(this.onclick).append(ESCAPED_QUOTE);
		}
		if (StringUtils.isNotBlank(this.clazz)) {
			sb.append("class=\"").append(this.clazz).append(ESCAPED_QUOTE);
		}
		if (StringUtils.isNotBlank(this.id)) {
			sb.append("id=\"").append(this.id).append(ESCAPED_QUOTE);
		}
		if (StringUtils.isNotBlank(this.rel)) {
			sb.append("rel=\"").append(this.rel).append(ESCAPED_QUOTE);
		}
		if (StringUtils.isNotBlank(this.style)) {
			sb.append("style=\"").append(this.style).append(ESCAPED_QUOTE);
		}
		if (StringUtils.isNotBlank(this.target)) {
			sb.append("target=\"").append(this.target).append(ESCAPED_QUOTE);
		}
		if (StringUtils.isNotBlank(this.title)) {
			sb.append("title=\"").append(this.title).append(ESCAPED_QUOTE);
		}
		if (this.isRichSnippet) {
			sb.append("itemprop=\"url\" ");
		}

		if (!this.dataAttributes.isEmpty()) {
			for (Map.Entry<String, String> entry : this.dataAttributes.entrySet()) {
				String key = entry.getKey();
				String value = StringEscapeUtils.escapeHtml(entry.getValue());
				sb.append(String.format("data-%s=\"%s\" ", key, value));
			}

		}
		sb.append(">");
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toStartTag() + toEndTag();
	}

	public void addDataAttribute(final String name,
	                             final String value) {
		this.dataAttributes.put(name, value);

	}

	public String getDataAttribute(final String name) {
		return this.dataAttributes.get(name);
	}
}
