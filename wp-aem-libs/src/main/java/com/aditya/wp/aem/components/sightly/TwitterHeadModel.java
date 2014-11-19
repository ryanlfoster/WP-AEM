/*
 * (c) 2014 Aditya Vennelakanti. All rights reserved. This material is solely and exclusively owned
 * by Aditya Vennelakanti and may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.components.sightly;

import com.adobe.cq.sightly.WCMUse;

/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 */
public class TwitterHeadModel extends WCMUse {

	private String cardType;
	private String siteName;
	private String siteId;
	private String creator;
	private String creatorId;
	private String description;
	private String title;
	private String imageSource;
	private String imageWidth;
	private String imageHeight;
	private String image1;
	private String image2;
	private String image3;
	private String image4;
	private String player;
	private String playerWidth;
	private String playerHeight;
	private String playerStream;
	private String data1;
	private String data2;
	private String label1;
	private String label2;
	private String appNameIphone;
	private String appNameIpad;
	private String appNameGooglePlay;
	private String appIdIphone;
	private String appIdIpad;
	private String appIdGooglePlay;
	private String appUrlIphone;
	private String appUrlIpad;
	private String appUrlGooglePlay;

	@Override
	public void activate() throws Exception {
		this.cardType = get("twitter_card_type", String.class);
		this.siteName = get("twitter_card_site", String.class);
		this.siteId = get("twitter_card_site_id", String.class);
		this.creator = get("twitter_card_creator", String.class);
		this.creatorId = get("twitter_card_creator_id", String.class);
		this.description = get("twitter_card_description", String.class);
		this.title = get("twitter_card_title", String.class);
		this.imageSource = get("twitter_card_image_src", String.class);
		this.imageHeight = get("twitter_card_image_height", String.class);
		this.imageWidth = get("twitter_card_image_width", String.class);
		this.image1 = get("twitter_card_image_1", String.class);
		this.image2 = get("twitter_card_image_2", String.class);
		this.image3 = get("twitter_card_image_3", String.class);
		this.image4 = get("twitter_card_image_4", String.class);
		this.player = get("twitter_card_player", String.class);
		this.playerWidth = get("twitter_card_player_width", String.class);
		this.playerHeight = get("twitter_card_player_height", String.class);
		this.playerStream = get("twitter_card_player_stream", String.class);
		this.data1 = get("twitter_card_data_1", String.class);
		this.data2 = get("twitter_card_data_2", String.class);
		this.label1 = get("twitter_card_label_1", String.class);
		this.label2 = get("twitter_card_label_2", String.class);
		this.appNameIphone = get("twitter_card_app_name_iphone", String.class);
		this.appNameIpad = get("twitter_card_app_name_ipad", String.class);
		this.appNameGooglePlay = get("twitter_card_app_name_googleplay", String.class);
		this.appIdIphone = get("twitter_card_app_id_iphone", String.class);
		this.appIdIpad = get("twitter_card_app_id_ipad", String.class);
		this.appIdGooglePlay = get("twitter_card_app_id_googleplay", String.class);
		this.appUrlIphone = get("twitter_card_app_url_iphone", String.class);
		this.appUrlIpad = get("twitter_card_app_url_ipad", String.class);
		this.appUrlGooglePlay = get("twitter_card_app_url_googleplay", String.class);
	}

	/**
	 * @return the cardType
	 */
	public final String getCardType() {
		return cardType;
	}

	/**
	 * @return the siteName
	 */
	public final String getSiteName() {
		return siteName;
	}

	/**
	 * @return the siteId
	 */
	public final String getSiteId() {
		return siteId;
	}

	/**
	 * @return the creator
	 */
	public final String getCreator() {
		return creator;
	}

	/**
	 * @return the creatorId
	 */
	public final String getCreatorId() {
		return creatorId;
	}

	/**
	 * @return the description
	 */
	public final String getDescription() {
		return description;
	}

	/**
	 * @return the title
	 */
	public final String getTitle() {
		return title;
	}

	/**
	 * @return the imageSource
	 */
	public final String getImageSource() {
		return imageSource;
	}

	/**
	 * @return the imageWidth
	 */
	public final String getImageWidth() {
		return imageWidth;
	}

	/**
	 * @return the imageHeight
	 */
	public final String getImageHeight() {
		return imageHeight;
	}

	/**
	 * @return the image1
	 */
	public final String getImage1() {
		return image1;
	}

	/**
	 * @return the image2
	 */
	public final String getImage2() {
		return image2;
	}

	/**
	 * @return the image3
	 */
	public final String getImage3() {
		return image3;
	}

	/**
	 * @return the image4
	 */
	public final String getImage4() {
		return image4;
	}

	/**
	 * @return the player
	 */
	public final String getPlayer() {
		return player;
	}

	/**
	 * @return the playerWidth
	 */
	public final String getPlayerWidth() {
		return playerWidth;
	}

	/**
	 * @return the playerHeight
	 */
	public final String getPlayerHeight() {
		return playerHeight;
	}

	/**
	 * @return the playerStream
	 */
	public final String getPlayerStream() {
		return playerStream;
	}

	/**
	 * @return the data1
	 */
	public final String getData1() {
		return data1;
	}

	/**
	 * @return the data2
	 */
	public final String getData2() {
		return data2;
	}

	/**
	 * @return the label1
	 */
	public final String getLabel1() {
		return label1;
	}

	/**
	 * @return the label2
	 */
	public final String getLabel2() {
		return label2;
	}

	/**
	 * @return the appNameIphone
	 */
	public final String getAppNameIphone() {
		return appNameIphone;
	}

	/**
	 * @return the appNameIpad
	 */
	public final String getAppNameIpad() {
		return appNameIpad;
	}

	/**
	 * @return the appNameGooglePlay
	 */
	public final String getAppNameGooglePlay() {
		return appNameGooglePlay;
	}

	/**
	 * @return the appIdIphone
	 */
	public final String getAppIdIphone() {
		return appIdIphone;
	}

	/**
	 * @return the appIdIpad
	 */
	public final String getAppIdIpad() {
		return appIdIpad;
	}

	/**
	 * @return the appIdGooglePlay
	 */
	public final String getAppIdGooglePlay() {
		return appIdGooglePlay;
	}

	/**
	 * @return the appUrlIphone
	 */
	public final String getAppUrlIphone() {
		return appUrlIphone;
	}

	/**
	 * @return the appUrlIpad
	 */
	public final String getAppUrlIpad() {
		return appUrlIpad;
	}

	/**
	 * @return the appUrlGooglePlay
	 */
	public final String getAppUrlGooglePlay() {
		return appUrlGooglePlay;
	}
}
