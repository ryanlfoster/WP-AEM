/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.utils.html;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

import com.aditya.gmwp.aem.model.LinkModel;
import com.aditya.gmwp.aem.services.core.link.HTMLLink;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public abstract class LinkBehavior {

	public static final class CONFIG {

		public static final String WINDOW_TYPE = "window_type";
		public static final String CONTENT_INCLUSION = "content_inclusion";
		public static final String CONTENT_INCLUSION_CONTENT_ONLY = "content_only";
		public static final String HEIGHT = "height";
		public static final String NAME = "configuration_name";
		public static final String PAGE_POSITION = "page_position";
		public static final String PAGE_POSITION_CENTER = "center";
		public static final String PAGE_POSITION_LINK = "link";
		public static final String PAGE_BACKGROUND = "page_background";
		public static final String PAGE_BACKGROUND_INACTIVE = "inactive";
		public static final String RESIZEABLE_DISABLED = "resizeable";
		public static final String SCROLLBARS_DISABLED = "scrollbars";
		public static final String WIDTH = "width";
	}

	public static final class WINDOW_TYPES {

		public static final String DEFAULT = "default";
		public static final String MODAL_LAYER = "modal_layer";
		public static final String NEW_BROWSER_WINDOW = "new_browser_window";

	}

	public static LinkBehavior createFromResource(final String windowType,
	                                              final ValueMap valueMap) {
		LinkBehavior behavior;
		if (windowType.equals(LinkBehavior.WINDOW_TYPES.MODAL_LAYER)) {
			behavior = new ModalLinkBehavior(valueMap);
		} else if (windowType.equals(LinkBehavior.WINDOW_TYPES.NEW_BROWSER_WINDOW)) {
			behavior = new NewWindowLinkBehavior(valueMap);
		} else {
			behavior = new DefaultLinkBehavior(valueMap);
		}
		return behavior;
	}

	private String name;

	public LinkBehavior(final ValueMap config) {
		setName(config.get(CONFIG.NAME, "--- untitled ---"));
	}

	public abstract void applyTo(final HTMLLink htmlLink,
	                             final LinkModel model,
	                             final SlingHttpServletRequest request);

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public JSONObject toJson() {
		JSONObject jsonObject = new JSONObject();

		try {
			jsonObject.put("text", this.name);
			jsonObject.put("value", this.name);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}
}
