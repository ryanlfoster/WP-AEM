/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.vehicledata.data.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.gmwp.aem.global.AEMTemplateInfo;
import com.aditya.gmwp.aem.global.LegalPriceContext;
import com.aditya.gmwp.aem.global.ShoppingLinkContext;
import com.aditya.gmwp.aem.model.LinkStyle;
import com.aditya.gmwp.aem.model.ShoppingLinkModel;
import com.aditya.gmwp.aem.properties.BaseballcardBodystyleProperties;
import com.aditya.gmwp.aem.properties.BaseballcardCarlineProperties;
import com.aditya.gmwp.aem.properties.PriceConfigProperties;
import com.aditya.gmwp.aem.properties.Properties;
import com.aditya.gmwp.aem.services.config.CompanyService;
import com.aditya.gmwp.aem.services.config.LanguageSLRService;
import com.aditya.gmwp.aem.services.vehicledata.data.Bodystyle;
import com.aditya.gmwp.aem.services.vehicledata.data.BodystyleBaseballcardData;
import com.aditya.gmwp.aem.services.vehicledata.data.Carline;
import com.aditya.gmwp.aem.services.vehicledata.data.CarlineBaseballcardData;
import com.aditya.gmwp.aem.services.vehicledata.data.Series;
import com.aditya.gmwp.aem.services.vehicledata.data.ShoppingLink;
import com.aditya.gmwp.aem.services.vehicledata.data.ShoppingLinkType;
import com.aditya.gmwp.aem.services.vehicledata.data.Trim;
import com.aditya.gmwp.aem.utils.EscapeUtils;
import com.aditya.gmwp.aem.utils.ShoppingLinkUtil;
import com.aditya.gmwp.aem.utils.html.HtmlReplacementRules;
import com.aditya.gmwp.aem.utils.html.HtmlUtil;
import com.aditya.gmwp.aem.utils.uri.UriBuilder;
import com.aditya.gmwp.aem.wrapper.GMResource;
import com.day.cq.wcm.api.Page;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class BodystyleBaseballcardDataImpl implements BodystyleBaseballcardData {

	/** The Constant ALTERNATIVETEXT_PREFIX. */
	private static final String ALTERNATIVETEXT_PREFIX = "ALTERNATIVETEXT";

	private static final Logger LOG = LoggerFactory.getLogger(BodystyleBaseballcardDataImpl.class);

	private static final String LINKSTYLE_PREFIX = "LINKSTYLE";

	private final CompanyService companyService;

	private final LanguageSLRService languageSLRService;

	private final Page bodystylePage;

	private final Page carlinePage;

	private final ValueMap bodystyleProperties;

	private final ValueMap carlineProperties;

	private ValueMap shoppingLinkConfigProperties;

	private final CarlineBaseballcardData carlineData;

	private Bodystyle relatedBodystyle;

	private List<Series> series;

	private List<Trim> trims;

	@Override
	public String toString() {
		final int maxLen = 2;
		return "BodystyleBaseballcardDataImpl ["
		        + (this.companyService != null ? "companyService=" + this.companyService + ", " : "")
		        + (this.languageSLRService != null ? "languageSLRService=" + this.languageSLRService + ", " : "")
		        + (this.bodystylePage != null ? "page=" + this.bodystylePage + ", " : "")
		        + (this.carlinePage != null ? "parentPage=" + this.carlinePage + ", " : "")
		        + (this.bodystyleProperties != null ? "propertiesPage=" + toString(this.bodystyleProperties.entrySet(), maxLen) + ", " : "")
		        + (this.carlineProperties != null ? "propertiesParentPage=" + toString(this.carlineProperties.entrySet(), maxLen) + ", " : "")
		        + (this.relatedBodystyle != null ? "relatedBodystyle=" + this.relatedBodystyle + ", " : "")
		        + (this.shoppingLinkConfigProperties != null ? "shoppingLinkConfigProperties=" + toString(this.shoppingLinkConfigProperties.entrySet(), maxLen)
		                : "") + "]";
	}

	/**
	 * The method returns the string representation of the objects.
	 * 
	 * @param collection
	 *            the collection
	 * @param maxLen
	 *            the maximal length
	 * @return the string representation of the objects
	 */
	private String toString(final Collection<?> collection,
	                        final int maxLen) {
		final StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (final Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
			if (i > 0) {
				builder.append(", ");
			}
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Instantiates a new bodystyle baseballcard data impl.
	 * 
	 * @param bodystylePage
	 *            the baseballcard bodystyle page.
	 * @param languageSLRService
	 *            the language slr service
	 * @param companyService
	 *            the company service
	 */
	public BodystyleBaseballcardDataImpl(final Page bodystylePage, final LanguageSLRService languageSLRService, final CompanyService companyService) {
		this.companyService = companyService;
		this.languageSLRService = languageSLRService;
		this.bodystylePage = bodystylePage;
		this.carlinePage = this.bodystylePage.getParent();

		this.bodystyleProperties = this.bodystylePage.getProperties("./baseballcard_bodystyle");
		if (null == this.bodystyleProperties) {
			LOG.warn("No data has been maintained on baseballcard-bodystyle '" + this.bodystylePage.getPath() + "'.");
		}
		this.carlineProperties = this.carlinePage.getProperties("./baseballcard_carline");
		if (null == this.carlineProperties) {
			LOG.warn("No data has been maintained on baseballcard-carline '" + this.carlinePage.getPath() + "'.");
		}

		this.carlineData = new CarlineBaseballcardDataImpl(this.carlinePage);
		this.shoppingLinkConfigProperties = this.bodystylePage.getProperties("./shopping_links_selection");
	}

	/**
	 * Instantiates a new bodystyle baseballcard data impl. This contructor is needed for bbc
	 * configuration pages. The shopping links are stored at configuration page.
	 * 
	 * @param bodystylePage
	 *            the baseballcard bodystyle page.
	 * @param languageSLRService
	 *            the language slr service
	 * @param companyService
	 *            the company service
	 * @param configurationPage
	 *            a baseballcard configuration page
	 */
	public BodystyleBaseballcardDataImpl(final Page bodystylePage, final LanguageSLRService languageSLRService, final CompanyService companyService,
	        final Page configurationPage) {
		this(bodystylePage, languageSLRService, companyService);
		this.shoppingLinkConfigProperties = configurationPage.getProperties("./shopping_links_selection");

	}

	@Override
	public String getShoppingLinkParameter(final ShoppingLink shoppingLink) {
		if (null == shoppingLink || null == this.relatedBodystyle) {
			return StringUtils.EMPTY;
		}
		if (ShoppingLinkType.LINK_WITH_CGI_PARAMS.equals(shoppingLink.getType())) {
			final String appParams = this.relatedBodystyle.getAppParams(shoppingLink.getDdpKey());
			if (null == appParams) {
				return StringUtils.EMPTY;
			}
			// Some app parameters have spaces and spaces that looks like spaces that aren't.
			// Escaping to clean that up.
			try {
				return EscapeUtils.urlencode(appParams);
			} catch (UnsupportedEncodingException e) {
				LOG.error("The app parameters for the vehicle on" + this.carlinePage.getPath()
				        + " contain characters that can't be encoded, returning them unencoded.");
			}
			return appParams;
		}
		return StringUtils.EMPTY;
	}

	@Override
	public final String getBaseballcardProperty(final Properties baseballCardProperty) {
		String property = null;
		if (baseballCardProperty instanceof BaseballcardBodystyleProperties) {
			property = this.bodystyleProperties.get(baseballCardProperty.getPropertyName(), String.class);
		} else {
			property = this.carlineData.getBaseballcardProperty(baseballCardProperty);
		}

		return property;
	}

	@Override
	public final String getBaseballCardTitle() {
		// keep default live system behaviour
		return getTitle(true);
	}

	@Override
	public final String getBaseballCardTitle(final boolean showCarlineText) {
		// keep default live system behaviour
		return getTitle(showCarlineText);
	}

	/**
	 * Returns the carline text.
	 * 
	 * @param show
	 *            show carline text.
	 * @return the carline text or an empty string if carline text property is <code>null</code> or
	 *         parameter show is <code>false</code>
	 */
	private String getCarlineText(final boolean show) {
		String carlineTitle = "";
		if (show && null != this.carlineProperties) {
			carlineTitle = this.carlineProperties.get(BaseballcardCarlineProperties.CARLINE_TEXT.getPropertyName(), StringUtils.EMPTY);

			carlineTitle = HtmlUtil.applyReplacementRules(carlineTitle, HtmlReplacementRules.ALL_LEADING_BR, HtmlReplacementRules.ALL_TRAILING_BR,
			        HtmlReplacementRules.BR_AFTER_OPENING_P, HtmlReplacementRules.BR_BEFORE_CLOSING_P, HtmlReplacementRules.CLOSING_P_WITH_EMPTY,
			        HtmlReplacementRules.OPENING_P_WITH_EMPTY, HtmlReplacementRules.EMPTY_P);
		}
		return carlineTitle;
	}

	@Override
	public final String getCarlineTitle() {
		return getCarlineText(true);
	}

	@Override
	public final String getCarlineTitle(final boolean showCarlineText) {
		return getCarlineText(showCarlineText);
	}

	@Override
	public final boolean useGrossAndNetPrice() {
		return "gross_and_net_price".equals(getBaseballcardProperty(BaseballcardBodystyleProperties.TYPE_OF_PRICE));
	}

	/**
	 * get the legal price suffix for for the baseball cards.
	 * 
	 * @param legalPriceContext
	 *            the legalPriceContext
	 * @return the suffix
	 */
	@Override
	public final String getLegalPriceSuffix(final LegalPriceContext legalPriceContext) {
		/** The legal price suffix. */
		String legalPriceSuffix = null;
		switch (legalPriceContext) {
			case TEASERAREA_ON_T03:
				legalPriceSuffix = getBaseballcardProperty(BaseballcardBodystyleProperties.LEGAL_PRICE_SUFFIX_TEASERAREA_T03);
				break;
			case TEASERAREA_ON_T04:
				legalPriceSuffix = getBaseballcardProperty(BaseballcardBodystyleProperties.LEGAL_PRICE_SUFFIX_TEASERAREA_T04);
				break;
			case TEASERAREA_ON_N01:
				legalPriceSuffix = getBaseballcardProperty(BaseballcardBodystyleProperties.LEGAL_PRICE_SUFFIX_TEASERAREA_N01);
				break;
			case TEASERAREA_ON_N02:
				legalPriceSuffix = getBaseballcardProperty( BaseballcardBodystyleProperties.LEGAL_PRICE_SUFFIX_TEASERAREA_N02);
				break;
			case VI_2:
				legalPriceSuffix = getBaseballcardProperty(BaseballcardBodystyleProperties.LEGAL_PRICE_SUFFIX_VI2);
				break;
			case VI_3:
				legalPriceSuffix = getBaseballcardProperty(BaseballcardBodystyleProperties.LEGAL_PRICE_SUFFIX_VI3);
				break;
			case VI_5:
				legalPriceSuffix = getBaseballcardProperty(BaseballcardBodystyleProperties.LEGAL_PRICE_SUFFIX_VI5);
				break;
			case TBL_FS:
				legalPriceSuffix = getBaseballcardProperty(BaseballcardBodystyleProperties.LEGAL_PRICE_SUFFIX_TBL_FS);
				break;
			case T02_VEHICLE_SELECTION:
				legalPriceSuffix = getBaseballcardProperty(BaseballcardBodystyleProperties.LEGAL_PRICE_SUFFIX_T02_VEHICLE_SELECTION);
				break;
			case MODEL_PRICES:
				legalPriceSuffix = getBaseballcardProperty(BaseballcardBodystyleProperties.LEGAL_PRICE_SUFFIX_MODEL_PRICES);
				break;
			case NO_PRICE_CONTEXT:
				legalPriceSuffix = StringUtils.EMPTY;
				break;
			default: // also valid for baseball cards
				legalPriceSuffix = getBaseballcardProperty(BaseballcardBodystyleProperties.LEGAL_PRICE_SUFFIX_BBC);
				break;
		}
		if (legalPriceSuffix == null) {
			legalPriceSuffix = this.languageSLRService.getConfigValue(this.bodystylePage, PriceConfigProperties.LEGAL_PRICE_SUFFIX);
		}
		return legalPriceSuffix;
	}

	@Override
	public final int getModelYear() {
		final String my = getBaseballcardProperty(BaseballcardCarlineProperties.MODEL_YEAR);
		if (StringUtils.isNotEmpty(my)) {
			try {
				return Integer.parseInt(my);
			} catch (NumberFormatException e) {
				LOG.error("The carline which is configured on page " + this.carlinePage.getPath() + " contains an invalid model year property.");
				return Carline.INVALID_MODEL_YEAR;
			}
		}
		return Carline.INVALID_MODEL_YEAR;
	}

	@Override
	public final List<ShoppingLinkModel> getTemplateShoppingLinks(final ShoppingLinkContext shoppingLinkContext,
	                                                              final ResourceResolver resourceResolver) throws RepositoryException {
		if (this.shoppingLinkConfigProperties != null && this.shoppingLinkConfigProperties.containsKey(shoppingLinkContext.toString())) {
			final String[] contextShoppingLinksArray = this.shoppingLinkConfigProperties.get(shoppingLinkContext.toString(), String[].class);
			final List<String> contextShoppingLinks = Arrays.asList(contextShoppingLinksArray);
			final List<ShoppingLink> orderedShoppingLinks = this.companyService.getOrderedShoppingLinkList(this.bodystylePage);

			final List<ShoppingLinkModel> shoppingLinkModelList = new ArrayList<ShoppingLinkModel>();

			ShoppingLinkModel shoppingLinkModel;

			for (ShoppingLink orderedShoppingLink : orderedShoppingLinks) {
				if (contextShoppingLinks.contains(orderedShoppingLink.toString())) {
					shoppingLinkModel = new ShoppingLinkModel();
					// set the shopping link
					shoppingLinkModel.setInternalShoppingLink(orderedShoppingLink);

					// Check if there is an alternative link text on the bodystyle
					final String alternativeLinkTitleProp = String.format("%1$s.%2$s.%3$s", ALTERNATIVETEXT_PREFIX, shoppingLinkContext, orderedShoppingLink);

					if (this.shoppingLinkConfigProperties.get(alternativeLinkTitleProp, String.class) != null) {
						shoppingLinkModel.setShoppingLinkText(this.shoppingLinkConfigProperties.get(alternativeLinkTitleProp, String.class));
					} else {
						// Get the link text from the repository
						shoppingLinkModel
						        .setShoppingLinkText(this.languageSLRService.getConfigValue(this.bodystylePage, orderedShoppingLink.getLslrLinktext()));
					}

					shoppingLinkModel.setLinkStyle(LinkStyle.SYSTEM_DEFAULT_STYLE);
					// Check if there is a link style
					final String linkStyleProp = String.format("%1$s.%2$s.%3$s", LINKSTYLE_PREFIX, shoppingLinkContext, orderedShoppingLink); // "LINKSTYLE.T04A.BUILD_YOUR_OWN"
					final String linkStyleName = this.shoppingLinkConfigProperties.get(linkStyleProp, String.class);
					if (linkStyleName != null) {
						for (final LinkStyle currentStyle : LinkStyle.values()) {
							if (StringUtils.equals(currentStyle.getName(), linkStyleName)) {
								shoppingLinkModel.setLinkStyle(currentStyle);
							}
						}
					}

					// Validation of the Link Text
					if (StringUtils.isEmpty(shoppingLinkModel.getShoppingLinkText())) {
						shoppingLinkModel.setShoppingLinkText("No LinkText maintained");
					}

					// get the page property of the link
					final String ddpKey = orderedShoppingLink.getDdpKey();
					final String lslrInternal = ShoppingLinkUtil.getInternalLinkFromLSLR(this.languageSLRService, this.bodystylePage, orderedShoppingLink);
					final String lslrExternal = ShoppingLinkUtil.getExternalLinkFromLSLR(this.languageSLRService, this.bodystylePage, orderedShoppingLink);
					final String bbcInternal = ShoppingLinkUtil.getInternalLinkFromBBC(this, orderedShoppingLink);
					final String bbcExternal = ShoppingLinkUtil.getExternalLinkFromBBC(this, orderedShoppingLink);
					if (ShoppingLinkUtil.hasLinks(bbcInternal, bbcExternal)) { // first look on bbc for maintained internal or external
						ShoppingLinkUtil.setLinks(shoppingLinkModel, bbcInternal, bbcExternal, orderedShoppingLink, getShoppingLinkParameter(orderedShoppingLink), resourceResolver);
					} else { // look on lslr if no internal or external is maintained on bbc
						ShoppingLinkUtil.setLinks(shoppingLinkModel, lslrInternal, lslrExternal, orderedShoppingLink, getShoppingLinkParameter(orderedShoppingLink), resourceResolver);
					}
					shoppingLinkModelList.add(shoppingLinkModel);
				}
			}
			return shoppingLinkModelList;
		} else {
			LOG.error("No shopping-link selection stored for " + shoppingLinkContext + " on page " + this.bodystylePage.getPath());
			// GMDSST-54157 prevent returning null and resulting in a nullpointer exception
			return Collections.emptyList();
		}
	}

	/**
	 * This method returns the baseball card title.
	 * 
	 * @param showCarlineText
	 *            include carline text in title or not
	 * @return the bbc title with model year or not
	 */
	private String getTitle(final boolean showCarlineText) {
		final String carlineTitle = getCarlineTitle(showCarlineText);
		final String navigationTitle = this.bodystylePage.getNavigationTitle();
		String bodystyleTitle = navigationTitle == null ? this.bodystylePage.getTitle() : navigationTitle;
		if (null == bodystyleTitle) {
			bodystyleTitle = "";
		}

		if (carlineTitle.equalsIgnoreCase(bodystyleTitle)) {
			return carlineTitle;
		} else if (StringUtils.isEmpty(carlineTitle)) {
			return bodystyleTitle;
		}
		return carlineTitle + " " + bodystyleTitle;
	}

	@Override
	public final boolean isDuplicateBaseballCard() {
		final String crxValue = getBaseballcardProperty(BaseballcardBodystyleProperties.DUPLICATE);
		return Boolean.parseBoolean(crxValue);
	}

	@Override
	public final boolean isFallbackBaseballCard() {
		return AEMTemplateInfo.TEMPLATE_BASEBALLCARD_BODYSTYLE_MANUAL.matchesTemplate(this.bodystylePage);
	}

	/**
	 * Sets the bodystyle which this baseballcard relates to.
	 * 
	 * @param relatedBodystyle
	 *            the bodystyle
	 */
	public final void setRelatedBodystyle(final Bodystyle relatedBodystyle) {
		this.relatedBodystyle = relatedBodystyle;
	}

	@Override
	public final Page getPage() {
		return this.bodystylePage;
	}

	@Override
	public final CarlineBaseballcardData getCarlineBaseballcardData() {
		return this.carlineData;
	}

	@Override
	public Series getSeries(final String seriesCode) {

		Series oneSeries = null;
		final List<Series> serieslist = getSeries();
		final Iterator<Series> iterator = serieslist.iterator();
		while (iterator.hasNext()) {
			final Series currentSeries = iterator.next();
			if (currentSeries.getCode().equalsIgnoreCase(seriesCode)) {
				oneSeries = currentSeries;
				break;
			}
		}
		return oneSeries;
	}

	@Override
	public Series getSeriesByAlternateId(final String alternateId) {

		Series oneSeries = null;
		final List<Series> serieslist = getSeries();
		final Iterator<Series> iterator = serieslist.iterator();
		while (iterator.hasNext()) {
			final Series currentSeries = iterator.next();
			if (currentSeries.getAlternateId().equalsIgnoreCase(alternateId)) {
				oneSeries = currentSeries;
				break;
			}
		}
		return oneSeries;
	}

	@Override
	public List<Series> getSeries() {

		if (null == this.series) {
			this.series = new ArrayList<Series>();
			if (null != this.relatedBodystyle) {
				this.series.addAll(this.relatedBodystyle.getSeries());
				Collections.sort(this.series, new Series.SeriesComparator());
			}
		}
		return this.series;
	}

	@Override
	public Trim getTrim(final String seriesCode) {

		Trim trim = null;
		final List<Trim> currentList = getTrims();
		for (final Iterator<Trim> iterator = currentList.iterator(); iterator.hasNext();) {
			final Trim currentTrim = iterator.next();
			if (seriesCode != null && seriesCode.equalsIgnoreCase(currentTrim.getSeriesCode())) {
				trim = currentTrim;
				break;
			}
		}
		return trim;
	}

	@Override
	public List<Trim> getTrims() {

		if (null == this.trims) {
			this.trims = new ArrayList<Trim>();
			final Resource bbcPageRes = this.bodystylePage.adaptTo(Resource.class);
			final Resource trimsContainerRes = bbcPageRes.getChild("jcr:content/trims/trims");

			if (trimsContainerRes != null) {

				final GMResource trimsContainerGMRes = new GMResource(trimsContainerRes);
				final List<GMResource> allTrimsGMRes = trimsContainerGMRes.getChildren();

				for (GMResource trimGMRRes : allTrimsGMRes) {
					Trim trim = getTrim(trimGMRRes);
					trim = mergeTrimAndSeries(trim);
					this.trims.add(trim);
				}
			}
		}
		return this.trims;
	}

	/**
	 * The method gets the data from a cnt_trim_c1 and sets it to a Trim.
	 * 
	 * @param trimRes
	 *            the cnt_trim_c1 resource
	 * @return the trim
	 */
	private Trim getTrim(final GMResource trimRes) {

		final Trim trim = new TrimImpl();
		trim.setSeriesCode(trimRes.getPropertyAsString("series"));
		trim.setTitle(trimRes.getPropertyAsString("title"));
		trim.setPrice(trimRes.getPropertyAsString("price"));
		trim.setNetPrice(trimRes.getPropertyAsString("net_price"));
		trim.setDescription(trimRes.getPropertyAsString("bodylongdesc"));
		trim.setFeatureList(trimRes.getPropertyAsString("bul"));
		trim.setOverrideLinkTarget(trimRes.getPropertyAsBoolean("overrideLinkTarget"));

		String targetOfMoreDetailsLink = trimRes.getPropertyAsString("overrideTargetOfMoreDetailsLink");
		if (StringUtils.isNotEmpty(targetOfMoreDetailsLink)) {
			trim.setTargetOfMoreDetailsLink(targetOfMoreDetailsLink);
			trim.setDeeplinkingTarget(trimRes.getPropertyAsString("moreDetailsDeeplinkTarget"));

		} else {
			targetOfMoreDetailsLink = getCompareTrimsInternalLink(); // should be a link to a T10
			if (StringUtils.isNotBlank(targetOfMoreDetailsLink)) {
				trim.setTargetOfMoreDetailsLink(targetOfMoreDetailsLink);
				trim.setDeeplinkingTarget(getDeeplinkTargetFromT10(targetOfMoreDetailsLink, trim.getSeriesCode()));
			}
		}

		return trim;
	}

	/**
	 * The method merges the data from the trim and the data form the series.
	 * 
	 * @param trim
	 *            the trim
	 * @return the trim
	 */
	private Trim mergeTrimAndSeries(final Trim trim) {

		final String seriesCode = trim.getSeriesCode();
		if (null != seriesCode && null != this.relatedBodystyle) {

			final Collection<Series> allSeries = this.relatedBodystyle.getSeries();
			for (final Iterator<Series> iterator = allSeries.iterator(); iterator.hasNext();) {
				final Series s = iterator.next();

				if (s.getCode().equals(seriesCode)) {
					mergeTrimAndSeries(trim, s);
					break;
				}
			}
		}
		return trim;
	}

	/**
	 * The method merges the data from the trim and the data form the series.
	 * 
	 * @param trim
	 *            the trim
	 * @param series
	 *            the series
	 * @return the trim
	 */
	private Trim mergeTrimAndSeries(final Trim trim,
	                                final Series series) {

		if (StringUtils.isEmpty(trim.getTitle())) {
			trim.setTitle(series.getTitle());
		}
		if (StringUtils.isEmpty(trim.getPrice())) {
			trim.setPrice(series.getFormattedPrice());
		}
		if (StringUtils.isEmpty(trim.getNetPrice())) {
			trim.setNetPrice(series.getFormattedNetPrice());
		}
		if (StringUtils.isEmpty(trim.getDescription())) {
			trim.setDescription(series.getDescription());
		}
		return trim;
	}

	/**
	 * The method returns the compare trims internal link path.
	 * 
	 * @return the compare trims internal link path
	 */
	private String getCompareTrimsInternalLink() {
		return (String) this.bodystyleProperties.get("comparetrims_internal_link");
	}

	/**
	 * The method returns the deep link target from the first nav_tablay_filter_1 on a T10.
	 * 
	 * @param resourcePath
	 *            the resource path
	 * @param series
	 *            the series
	 * @return the deep link target from the first nav_tablay_filter_1 on a T10 or null
	 */
	private String getDeeplinkTargetFromT10(final String resourcePath,
	                                        final String series) {

		String deeplinkTarget = null;
		if (areParametersOk(resourcePath, series)) {
			final Page t10Page = getT10Page(resourcePath);
			if (null != t10Page) {
				final Resource vi3Parsys = getVi3Parsys(t10Page);
				if (null != vi3Parsys) {
					final Iterator<Resource> vi3Resources = getVi3Resources(vi3Parsys);
					deeplinkTarget = getDeeplinkTarget(vi3Resources, series);
				}
			}
		}
		return deeplinkTarget;
	}

	/**
	 * The method checks whether the parameters of {@link #getDeeplinkTargetFromT10(String, String)}
	 * are ok.
	 * 
	 * @param resourcePath
	 *            the resource path
	 * @param series
	 *            the series
	 * @return return true if the parameters are ok, false otherwise
	 */
	private boolean areParametersOk(final String resourcePath,
	                                final String series) {

		boolean ok = false;
		if (StringUtils.isNotBlank(resourcePath) || StringUtils.isNotBlank(series)) {
			ok = true;
		}
		return ok;
	}

	/**
	 * The method returns the T10 page.
	 * 
	 * @param resourcePath
	 *            the resource path of the T10
	 * @return the T10 page or null if the path doesn't belong to a T10 page
	 */
	private Page getT10Page(final String resourcePath) {

		Page page = this.bodystylePage.getPageManager().getPage(resourcePath);
		if (null == page || !AEMTemplateInfo.TEMPLATE_T10.matchesTemplate(page)) {
			page = null;
		}
		return page;
	}

	/**
	 * The method returns the parsys which contains the vi_3 components.
	 * 
	 * @param t10Page
	 *            the T10 page
	 * @return the parsys which contains the vi_3 components or null
	 */
	private Resource getVi3Parsys(final Page t10Page) {
		Resource vi3Parsys = t10Page.getContentResource("parsys_c1/nav_tablay_filter_c1/partab");
		if (vi3Parsys instanceof NonExistingResource) {
			vi3Parsys = null;
		}
		return vi3Parsys;
	}

	/**
	 * The method returns an iterator over the vi_3 components.
	 * 
	 * @param vi3Parsys
	 *            the parsys which contains the vi_3 components
	 * @return an iterator over the vi_3 components
	 */
	private Iterator<Resource> getVi3Resources(final Resource vi3Parsys) {
		return vi3Parsys.listChildren();
	}

	/**
	 * The method returns the deep link target from the first nav_tablay_filter_1 on a T10.
	 * 
	 * @param vi3Resources
	 *            the iterator over the vi_3 components
	 * @param series
	 *            the series
	 * @return the deep link target from the first nav_tablay_filter_1 on a T10 or null
	 */
	private String getDeeplinkTarget(final Iterator<Resource> vi3Resources,
	                                 final String series) {

		String deeplinkTarget = null;
		for (int no = 1; vi3Resources.hasNext(); no++) {
			final Resource vi3Resource = vi3Resources.next();
			final Node vi3Node = vi3Resource.adaptTo(Node.class);

			try {
				final String seriesFromVi3 = vi3Node.getProperty("series").getString();
				if (series.equalsIgnoreCase(seriesFromVi3)) {
					deeplinkTarget = "nav_tablay_layer_" + no;
				}

			} catch (RepositoryException e) {
				// vi3Node.toString() returns the node path
				LOG.error("Unable to read string property \"series\" from node " + vi3Node.toString());
			}
		}
		return deeplinkTarget;
	}
}
