/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.model;

import java.util.Map;

import com.aditya.wp.aem.services.tracking.data.OmnitureVariables;
import com.aditya.wp.aem.utils.tracking.LinkType;
import com.aditya.wp.aem.utils.tracking.OmnitureJSBuilder;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class OmnitureLinkTrackingData implements LinkTrackingData {

	/**
	 * This enum contains all events that may be used for link-tagging in GMDS.
	 */
	public enum OmnitureLinkTrackingEvents {
		EVENT29("event29"), //
		EVENT30("event30"), // 
		EVENT35("event35"), //
		EVENT36("event36"), //
		EVENT18("event18");

		private final String identifier;

		/**
		 * Constructor.
		 * 
		 * @param identifier
		 *            the identifier
		 */
		private OmnitureLinkTrackingEvents(final String identifier) {
			this.identifier = identifier;
		}

		/**
		 * Returns the identifier.
		 * 
		 * @return the identifier.
		 */
		@Override
		public String toString() {
			return this.identifier;
		}
	}

	private final OmnitureJSBuilder builder;

	/**
	 * Creates a new instance.
	 * 
	 * @param linkName
	 *            the linkName. If null, the links href will be used.
	 * @param linkType
	 *            the linkType
	 * @param omnitureVars
	 *            omniture vars to be set when the link is tracked, may be null
	 *            or empty.
	 * @param event
	 *            the event to be set for link tracking, may be null.
	 */
	public OmnitureLinkTrackingData(final String linkName,
			final LinkType linkType,
			final Map<OmnitureVariables, String> omnitureVars,
			final OmnitureLinkTrackingEvents event) {
		this.builder = new OmnitureJSBuilder().setLinkName(linkName)
				.setLinkType(linkType).setEvent(event)
				.setVariables(omnitureVars);
	}

	/**
	 * Creates the JavaScript code that does the actual link tracking.
	 * 
	 * @return JS-code.
	 */
	@Override
	public final String toJsCode() {
		return this.builder.createJS();
	}
}
