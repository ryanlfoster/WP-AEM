/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.utils.html;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.json.JSONObject;

import com.aditya.gmwp.aem.model.LinkModel;
import com.aditya.gmwp.aem.services.core.link.HTMLLink;
import com.aditya.gmwp.aem.services.core.link.HrefAssembler;
import com.aditya.gmwp.aem.services.core.link.aspects.DefaultHrefAssembler;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class ModalLinkBehavior extends LinkBehavior {

	public static final String DATA_ATTRIBUTE_LAYER_SRC = "layer-src";
	public static final String DATA_ATTRIBUTE_LAYER_CFG = "layer-cfg";
	public static final String CLASS_LAYER = "layer";

	private final int height;
	private final int width;
	private final boolean scrollbars;
	private final boolean resizable;
	private final String position;
	private final boolean isContentOnly;
	private final boolean isModal;
	private final HrefAssembler assembler;

	/**
	 * Creates a modal
	 *
	 * @param config
	 *            The LinkBehaviors Configuration from the CompanyTemplate
	 * @param assembler
	 *            The HrefAssembler which should be used to create URIs
	 */
	public ModalLinkBehavior(final ValueMap config, final HrefAssembler assembler) {
		super(config);
		this.height = config.get(CONFIG.HEIGHT, -1);
		this.width = config.get(CONFIG.WIDTH, -1);
		this.scrollbars = !config.get(CONFIG.SCROLLBARS_DISABLED, false);
		this.resizable = !config.get(CONFIG.RESIZEABLE_DISABLED, false);
		this.position = config.get(CONFIG.PAGE_POSITION, CONFIG.PAGE_POSITION_CENTER);
		this.isContentOnly = config.get(CONFIG.CONTENT_INCLUSION, CONFIG.CONTENT_INCLUSION_CONTENT_ONLY).equals(CONFIG.CONTENT_INCLUSION_CONTENT_ONLY);
		this.isModal = config.get(CONFIG.PAGE_BACKGROUND, CONFIG.PAGE_BACKGROUND_INACTIVE).equals(CONFIG.PAGE_BACKGROUND_INACTIVE);
		this.assembler = assembler;
	}

	/**
	 * Like {@link #ModalLinkBehavior(ValueMap, HrefAssembler)}.
	 * <p>
	 * Uses the {@link DefaultHrefAssembler} to create the modal's data-src-attribute
	 *
	 * @param config
	 *            The LinkBehaviors configuration from the CompanyTemplate
	 */
	public ModalLinkBehavior(final ValueMap config) {
		this(config, new DefaultHrefAssembler());
	}

	@Override
	public void applyTo(final HTMLLink htmlLink,
	                    final LinkModel model,
	                    final SlingHttpServletRequest request) {
		htmlLink.setClazz(CLASS_LAYER);
		htmlLink.addDataAttribute(DATA_ATTRIBUTE_LAYER_CFG, getLayerCfg());
		htmlLink.addDataAttribute(DATA_ATTRIBUTE_LAYER_SRC, getLayerSrc(model, request));
	}

	private String getLayerCfg() {
		Map<String, Object> config = createConfigMap();
		JSONObject configJson = new JSONObject(config);
		String json = configJson.toString();
		return json;
	}

	private Map<String, Object> createConfigMap() {
		Map<String, Object> config = new HashMap<String, Object>();
		if (this.width > 0) {
			config.put("width", this.width);
		}
		if (this.height > 0) {
			config.put("height", this.height);
		}
		config.put("modal", this.isModal);
		config.put("resizable", this.resizable);
		config.put("scrollable", this.scrollbars);
		config.put("position", this.position);
		return config;
	}

	/**
	 * Creates the content of the "data-layer-src"-attribute adds "contentOnly"-selector if needed
	 * and always adds "embedded"-selector. The layer-src is used by frontend to create an iframe.
	 * Delegates building of href to the {@link HrefAssembler}
	 *
	 * @param model
	 *            The LinkModel, assiociated with the behavior
	 * @param request
	 *            The current HttpRequest
	 * @return String containing a URL
	 */
	private String getLayerSrc(LinkModel model,
	                           final SlingHttpServletRequest request) {
		if (this.isContentOnly) {
			model = LinkModel.newInstance(model);
			model.addSelector("contentOnly");
		}
		model.addSelector("embedded");
		return this.assembler.buildHref(request, model);

	}
}