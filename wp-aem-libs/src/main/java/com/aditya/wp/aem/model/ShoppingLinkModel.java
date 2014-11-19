/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.model;


import com.aditya.gmwp.aem.services.vehicledata.data.ShoppingLink;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class ShoppingLinkModel extends LinkModel {

	private ShoppingLink externalShoppingLink;

	private ShoppingLink internalShoppingLink;

	private LinkStyle linkStyle;

	private String shoppingLinkText;

	/**
	 * Instantiates a new shopping link model.
	 */
	public ShoppingLinkModel() {
	}

	/**
	 * Copy constructor.
	 * 
	 * @param shoppingLinkModel
	 *            the {@link ShoppingLinkModel} to copy
	 */
	private ShoppingLinkModel(final ShoppingLinkModel shoppingLinkModel) {
		// link model fields
		this.anchor = shoppingLinkModel.anchor;
		this.directExternalLink = shoppingLinkModel.directExternalLink;
		this.disclaimerLink = shoppingLinkModel.disclaimerLink;
		this.externalLink = shoppingLinkModel.externalLink;
		this.externalLinkModel = shoppingLinkModel.externalLinkModel;
		this.followRedirect = shoppingLinkModel.followRedirect;
		this.glossaryLink = shoppingLinkModel.glossaryLink;
		this.id = shoppingLinkModel.id;
		this.inPageLink = shoppingLinkModel.inPageLink;
		this.internalLink = shoppingLinkModel.internalLink;
		this.linkTrackingData.addAll(shoppingLinkModel.linkTrackingData);
		this.parameters = shoppingLinkModel.parameters;
		this.selectors = shoppingLinkModel.selectors;
		this.text = shoppingLinkModel.text;
		this.title = shoppingLinkModel.title;
		// shopping link model field
		this.externalShoppingLink = shoppingLinkModel.externalShoppingLink;
		this.internalShoppingLink = shoppingLinkModel.internalShoppingLink;
		this.linkStyle = shoppingLinkModel.linkStyle;
		this.shoppingLinkText = shoppingLinkModel.shoppingLinkText;
	}

	/**
	 * Creates a new instance invoking copy constructor.
	 * 
	 * @param shoppingLinkModel
	 *            the {@link ShoppingLinkModel} to copy
	 * @return a new instance of the given {@code ShoppingLinkModel}
	 */
	public static ShoppingLinkModel newInstance(final ShoppingLinkModel shoppingLinkModel) {
		return new ShoppingLinkModel(shoppingLinkModel);
	}

	/**
	 * Returns the css class used as a selector by jQuery for adding parameter to url for btl
	 * clipped applications.
	 * 
	 * @return the cssClass
	 */
	public final String getCssClass() {
		String clazz = "shppngLnk";
		if (this.internalShoppingLink != null) {
			clazz = clazz + " sl-" + this.internalShoppingLink.getDdpKey();
		} else if (this.externalShoppingLink != null) {
			clazz = clazz + " sl-" + this.externalShoppingLink.getDdpKey();
		} else {
			clazz = "";
		}
		return clazz;
	}

	/**
	 * Gets the external shopping link.
	 * 
	 * @return the external shopping link
	 */
	public final ShoppingLink getExternalShoppingLink() {
		return this.externalShoppingLink;
	}

	/**
	 * Gets the shopping link.
	 * 
	 * @return the shopping link
	 */
	public final ShoppingLink getInternalShoppingLink() {
		return this.internalShoppingLink;
	}

	/**
	 * Gets the link key.
	 * 
	 * @return the link key
	 */
	public final String getLinkKey() {
		if (this.internalShoppingLink != null) {
			return this.internalShoppingLink.getDdpKey();
		} else if (this.externalShoppingLink != null) {
			return this.externalShoppingLink.getDdpKey();
		} else {
			return null;
		}
	}

	/**
	 * Gets the shopping link text.
	 * 
	 * @return the shopping link text
	 */
	public final String getShoppingLinkText() {
		return this.shoppingLinkText;
	}

	/**
	 * Sets the external shopping link.
	 * 
	 * @param externalShoppingLink
	 *            the new external shopping link
	 */
	public final void setExternalShoppingLink(final ShoppingLink externalShoppingLink) {
		this.externalShoppingLink = externalShoppingLink;
	}

	/**
	 * Sets the shopping link.
	 * 
	 * @param internalShoppingLink
	 *            the new shopping link
	 */
	public final void setInternalShoppingLink(final ShoppingLink internalShoppingLink) {
		this.internalShoppingLink = internalShoppingLink;
	}

	/**
	 * Sets the shopping link text.
	 * 
	 * @param shoppingLinkText
	 *            the new shopping link text
	 */
	public final void setShoppingLinkText(final String shoppingLinkText) {
		this.shoppingLinkText = shoppingLinkText;
	}

	/**
	 * Gets the hyperlink style.
	 * 
	 * @return the linkStyle
	 */
	public final LinkStyle getLinkStyle() {
		return this.linkStyle;
	}

	/**
	 * Sets the hyperlink style for this ShoppingLink.
	 * 
	 * @param linkStyle
	 *            the linkStyle to set
	 */
	public final void setLinkStyle(final LinkStyle linkStyle) {
		this.linkStyle = linkStyle;
	}
}