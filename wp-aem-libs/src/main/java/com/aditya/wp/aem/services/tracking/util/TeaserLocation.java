/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.tracking.util;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public enum TeaserLocation {
	UNKNOWN("", ""),
	IN_TEASER_LINKLIST("teaser-linklist", "teaser-linklist"),
	IN_CONTENT_AREA("content-area", "content-area"),
	SCROLLER_ON_CATWALK("catwalk", "catwalk"),
	BELOW_SERVICE_ICONS("service-icons", "special"),
	NAV_FOOTER("footer-navigation", "footer-navigation");

	private final String pageArea;
	private final String teaserType;

	private TeaserLocation(final String pageArea, final String teaserType) {
		this.pageArea = pageArea;
		this.teaserType = teaserType;
	}

	public String getPageArea() {
		return this.pageArea;
	}

	public String getTeaserType() {
		return this.teaserType;
	}
}
