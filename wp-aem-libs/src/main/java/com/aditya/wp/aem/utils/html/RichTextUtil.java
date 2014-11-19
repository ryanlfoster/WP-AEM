/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.utils.html;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.scripting.SlingScriptHelper;

import com.aditya.gmwp.aem.model.DisclaimerModel;
import com.aditya.gmwp.aem.model.LinkModel;
import com.aditya.gmwp.aem.properties.CompanyConfigProperties;
import com.aditya.gmwp.aem.properties.LanguageConfigProperties;
import com.aditya.gmwp.aem.services.config.CompanyService;
import com.aditya.gmwp.aem.services.config.LanguageSLRService;
import com.aditya.gmwp.aem.services.config.LinkBehaviorService;
import com.aditya.gmwp.aem.services.core.LinkWriterService;
import com.aditya.gmwp.aem.services.core.ServiceProvider;
import com.aditya.gmwp.aem.services.core.link.HTMLLink;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class RichTextUtil {

	/**
	 * Gets the link from disclaimer richtext if the corresponding flag is set on the lslr.
	 *
	 * @param slingScriptHelper
	 *            the sling script helper
	 * @param currentPage
	 *            the current page
	 * @param disclaimerIndication
	 *            the disclaimer indication
	 * @return the link from disclaimer richtext
	 */
	public static String getLinkFromDisclaimerRichtext(final SlingScriptHelper slingScriptHelper,
	                                                   final Page currentPage,
	                                                   final String disclaimerIndication) {

		final LanguageSLRService lslrService = slingScriptHelper.getService(LanguageSLRService.class);
		final boolean linkAttributeUnit = Boolean.parseBoolean(lslrService.getConfigValue(currentPage, LanguageConfigProperties.ATTRIBUTE_UNIT_LINK));

		String disclaimerLink = StringUtils.EMPTY;

		if (linkAttributeUnit) {
			String richtext = disclaimerIndication;
			if (richtext != null) {
				richtext = richtext.replaceAll("\n", StringUtils.EMPTY);
			}
			final String replacedRichtext = RichTextUtil.replaceLinkTags(richtext, slingScriptHelper.getRequest());

			if (replacedRichtext != null) {
				final Pattern pattern = Pattern.compile("<a href=\"#(.*?)\" class=\"ln disclaimer_1\" >.*?</a>");
				final Matcher m = pattern.matcher(replacedRichtext);
				while (m.find()) {
					disclaimerLink = m.group(1);
				}
			}
		}

		return disclaimerLink;

	}

	/**
	 * Hanles all links in the given richtext.
	 *
	 * @param richtext
	 *            input richtext
	 * @param request
	 *            sling request
	 * @return richtext with processed links
	 */
	public static String replaceLinkTags(final String richtext,
	                                     final SlingHttpServletRequest request) {
		if (StringUtils.isNotBlank(richtext)) {
			final StringBuilder sb = new StringBuilder(richtext);

			int index = 0;
			// match all links and call replaceLinkTag(...)
			while (true) {
				final int aOpenStart = sb.indexOf("<a", index);
				if (aOpenStart == -1) {
					break;
				}
				final int aOpenCloseTag = sb.indexOf("/>", aOpenStart);
				final int aOpenEnd = sb.indexOf(">", aOpenStart);
				if (aOpenCloseTag != -1 && aOpenCloseTag < aOpenEnd) {
					index = aOpenCloseTag;
					continue;
				}
				if (aOpenEnd == -1) {
					// HTML is not well formed!
					// throw new IllegalStateException();
					index = aOpenStart + 1;
					continue;
				}
				final int aCloseStart = sb.indexOf("</a", aOpenEnd);
				if (aCloseStart == -1) {
					throw new IllegalStateException();
				}
				final int aCloseEnd = sb.indexOf(">", aCloseStart);
				if (aCloseEnd == -1) {
					throw new IllegalStateException();
				}
				replaceLinkTag(sb, aOpenStart, aOpenEnd, aCloseStart, aCloseEnd, request);
				// set the index behind of the start tag
				index = aOpenStart + 1;
			}
			return sb.toString();
		} else {
			return richtext;
		}
	}

	/**
	 * Returns the text replacing the span class="colorName" with span style="color:<colorCode>".
	 *
	 * @param richText
	 *            the rich text
	 * @param slingScriptHelper
	 *            sling script helper object
	 * @param currentPage
	 *            the current page
	 * @return replaced rich text
	 */
	public static String replaceTextColorTags(final String richText,
	                                          final SlingScriptHelper slingScriptHelper,
	                                          final Page currentPage) {

		if (StringUtils.isBlank(richText)) {
			return richText;
		}

		final StringBuilder stringBuilder = new StringBuilder(richText);
		int index = 0;

		while (true) {
			final int spanOpenStart = stringBuilder.indexOf("<span", index);
			if (spanOpenStart == -1) {
				break;
			}
			final int spanEnd = stringBuilder.indexOf("/>", spanOpenStart);
			final int spanOpenEnd = stringBuilder.indexOf(">", spanOpenStart);
			if (spanEnd != -1 && spanEnd < spanOpenEnd) {
				index = spanEnd;
				continue;
			}
			if (spanOpenEnd == -1) {
				index = spanOpenStart + 1;
				continue;
			}
			final int spanCloseStart = stringBuilder.indexOf("</span", spanOpenEnd);
			if (spanCloseStart == -1) {
				throw new IllegalStateException();
			}
			final int spanCloseEnd = stringBuilder.indexOf(">", spanCloseStart);
			if (spanCloseEnd == -1) {
				throw new IllegalStateException();
			}
			replaceColorTag(slingScriptHelper, currentPage, stringBuilder, spanOpenStart, spanOpenEnd, spanCloseStart, spanCloseEnd);
			index = spanOpenStart + 1;
		}
		return stringBuilder.toString();
	}

	/**
	 * convert the rich text link to a HTML link and replace this. Example:
	 * <p>
	 * convert internal links <br>
	 * &lt;a internallink="/content/opel/europe/master/hq/en/index"&gt;Opel Homepage&lt;/a&gt;<br>
	 * to <br>
	 * &lt;a href="/content/opel/europe/master/hq/en/index"&gt;Opel Homepage&lt;/a&gt;<br>
	 * </p>
	 * <p>
	 * convert external links <br>
	 * &lt;a internallink="/content/opel/europe/master/hq/external_link_lib/gm_com"&gt;GM
	 * Homepage&lt;/a&gt;<br>
	 * to <br>
	 * &lt;a href="http://gm.com/"&gt;GM Homepage&lt;/a&gt;<br>
	 * </p>
	 *
	 * @param sb
	 *            rich text String
	 * @param openStart
	 *            start index of open tag
	 * @param openEnd
	 *            end index of open tag
	 * @param closeStart
	 *            start index of close tag
	 * @param closeEnd
	 *            end index of close tag * @param request current request
	 */
	protected static void replaceLinkTag(final StringBuilder sb,
	                                     final int openStart,
	                                     final int openEnd,
	                                     final int closeStart,
	                                     final int closeEnd,
	                                     final SlingHttpServletRequest request) {
		final String tag = sb.substring(openStart, openEnd);

		final String internalMarkup = "internallink=\"";
		final boolean isInternalLink = StringUtils.contains(tag, internalMarkup);
		final String externalMarkup = "externallink=\"";
		final boolean isExternalLink = StringUtils.contains(tag, externalMarkup);
		final String disclaimerMarkup = "disclaimerlink=\"";
		final boolean isDisclaimerLink = StringUtils.contains(tag, disclaimerMarkup);
		final String glossaryMarkup = "glossarylink=\"";
		final boolean isGlossaryLink = StringUtils.contains(tag, glossaryMarkup);
		final String deeplinkMarkup = "deeplinkparam=\"";
		final boolean hasDeepLinkParam = StringUtils.contains(tag, deeplinkMarkup);
		final String inPageMarkup = "inpagelink=\"";
		final boolean isInPageLink = StringUtils.contains(tag, inPageMarkup);
		final String linkBehaviorMarkup = "linkbehavior=\"";
		final boolean hasLinkBehavior = StringUtils.contains(tag, linkBehaviorMarkup);

		LinkModel link = null;
		if (isInternalLink) {
			link = new LinkModel();
			final String internallink = extractAttributeValueFrom(tag, internalMarkup);
			link.setInternalLink(internallink);
			if (hasDeepLinkParam) {
				link.setAnchor(extractAttributeValueFrom(tag, deeplinkMarkup));
			}
		} else if (isInPageLink) {
			link = new LinkModel();
			link.setInPageLink(extractAttributeValueFrom(tag, inPageMarkup));
		} else if (isDisclaimerLink) {
			final String disclaimerId = extractAttributeValueFrom(tag, disclaimerMarkup);
			link = new LinkModel();
			link.setDisclaimerLink("#" + DisclaimerModel.escapeDisclaimerID(disclaimerId));
			if (request != null) {
				DisclaimerModel.addReferencedDisclaimerId(request, disclaimerId);
			}
		} else if (isGlossaryLink) {
			link = new LinkModel();
			link.setGlossaryLink(extractAttributeValueFrom(tag, glossaryMarkup));
		} else if (isExternalLink) {
			final String externallink = extractAttributeValueFrom(tag, externalMarkup);
			link = createExternalLinkModel(externallink, request);
		}

		if (hasLinkBehavior) {
			LinkBehaviorService linkBehaviorService = ServiceProvider.INSTANCE.getService(LinkBehaviorService.class);
			String name = extractAttributeValueFrom(tag, linkBehaviorMarkup);
			LinkBehavior linkBehavior = linkBehaviorService.getLinkConfigurationBehavior(request, name);
			link.setBehavior(linkBehavior);
		}
		final int start = tag.indexOf("title=\"");
		if (link != null && start != -1) {
			final int end = tag.indexOf("\"", "title=\"".length() + start);
			final String title = tag.substring(start + "title=\"".length(), end);
			link.setTitle(title);
		}
		if (link != null) {
			final String text = sb.substring(openEnd + 1, closeStart);
			sb.delete(openStart, closeEnd + 1);
			sb.insert(openStart, convertLinkToString(link, text, request));
		}
	}

	/**
	 * Modify the color span tag to use the color specified on the company template.
	 *
	 * @param stringBuilder
	 * @param openStart
	 * @param openEnd
	 * @param closeStart
	 * @param closeEnd
	 */
	protected static void replaceColorTag(final SlingScriptHelper slingScriptHelper,
	                                      final Page currentPage,
	                                      final StringBuilder stringBuilder,
	                                      final int openStart,
	                                      final int openEnd,
	                                      final int closeStart,
	                                      final int closeEnd) {
		final CompanyService companyService = slingScriptHelper.getService(CompanyService.class);
		final String[] textColors = companyService.getConfigValueArray(currentPage, CompanyConfigProperties.TEXTCOLORS);
		if (textColors != null) {

			final String tag = stringBuilder.substring(openStart, openEnd);

			final String classMarkup = "class=\"";
			final boolean isClassSpecified = StringUtils.contains(tag, classMarkup);
			if (isClassSpecified) {
				final String colorClass = extractAttributeValueFrom(tag, classMarkup);

				for (String color : textColors) {
					final String[] colorParts = StringUtils.split(color, "|");
					if (colorParts == null || colorParts.length != 2) {
						continue;
					}
					if (StringUtils.equals(colorClass, colorParts[1])) {
						final String spanHtml = "<span style=\"color:#" + colorParts[0] + ";\">";
						final String text = stringBuilder.substring(openEnd + 1, closeStart);
						stringBuilder.delete(openStart, closeEnd + 1);
						stringBuilder.insert(openStart, spanHtml + text + "</span>");
					}
				}
			}
		}
	}

	/**
	 * convert link to attribute string with the {@link LinkAttributeBuilder}.
	 *
	 * @param link
	 *            {@link LinkModel} of the generated attribute string
	 * @param text
	 *            input text * @param request current request
	 * @return attribute of the {@link LinkModel} as Sting
	 */
	protected static String convertLinkToString(final LinkModel link,
	                                            final String text,
	                                            final SlingHttpServletRequest request) {

		final HTMLLink htmlLink;
		if (link.hasDisclaimerLink()) {
			htmlLink = new HTMLLink();
			htmlLink.setClazz("ln disclaimer_1");
			htmlLink.setHref(link.getDisclaimerLink());
		} else {
			final LinkWriterService linkRewriterService = ServiceProvider.INSTANCE.getService(LinkWriterService.class);
			htmlLink = linkRewriterService.rewriteLink(request, link);
		}

		return htmlLink.toStartTag() + text + htmlLink.toEndTag();
	}

	/**
	 * create the external link model and return it. - 2014.03.04 Added if/else inside
	 * if(request!=null) statement this will be used when a PDF file link is needed inside a RTE.
	 * Author will enter the internal PDF resource as an external link. This will look for an
	 * external link that is styled as an internal link ending with PDF. It will then treat that
	 * link as a direct external link opening in a new window.
	 *
	 * @param externallink
	 *            a link to a external link template
	 * @param request
	 *            current request
	 * @return the {@link LinkModel} from the give over link
	 */
	protected static LinkModel createExternalLinkModel(final String externallink,
	                                                   final SlingHttpServletRequest request) {
		final LinkModel model = new LinkModel();
		if (request != null) {
			if (StringUtils.endsWith(externallink, ".pdf") && StringUtils.startsWith(externallink, "/content")) {
				model.setDirectExternalLink(externallink, request.getResourceResolver());
				model.getExternalLinkModel().setWindowtype("newWindow");
			} else {
				model.setExternalLink(externallink, request.getResourceResolver());
			}
		}
		return model;
	}

	/**
	 * Extracts attribute value from tag.
	 *
	 * @param tag
	 *            html tag
	 * @param attributeName
	 *            attribute name
	 * @return attribute value
	 */
	protected static String extractAttributeValueFrom(final String tag,
	                                                  final String attributeName) {
		final int start = tag.indexOf(attributeName);
		final int end = tag.indexOf("\"", attributeName.length() + start);
		return tag.substring(start + attributeName.length(), end);
	}

	/**
	 * Private c'tor.
	 */
	private RichTextUtil() {
	}
}
