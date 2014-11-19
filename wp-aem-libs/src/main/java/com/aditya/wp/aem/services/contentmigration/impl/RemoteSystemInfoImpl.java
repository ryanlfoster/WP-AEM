/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.contentmigration.impl;

import com.aditya.gmwp.aem.services.contentmigration.SystemInfo;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class RemoteSystemInfoImpl implements SystemInfo {

	private String systemId;

	private final String baseUrl;

	private SystemStatus systemStatus;

	/**
	 * Creates a new instance.
	 * 
	 * @param systemId
	 *            the system ID.
	 * @param baseUrl
	 *            the base url.
	 */
	public RemoteSystemInfoImpl(final String systemId, final String baseUrl) {
		this.systemId = systemId;
		this.baseUrl = baseUrl;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final SystemType getSystemType() {
		return SystemType.REMOTE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getSystemId() {
		return this.systemId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getSystemBaseUrl() {
		return this.baseUrl;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final SystemStatus getSystemStatus() {
		return this.systemStatus;
	}

	/**
	 * Sets the system status.
	 * 
	 * @param systemStatus
	 *            the system status.
	 */
	public final void setSystemStatus(final SystemStatus systemStatus) {
		this.systemStatus = systemStatus;
	}

	/**
	 * Sets the system ID.
	 * 
	 * @param systemId
	 *            the system ID.
	 */
	public final void setSystemId(final String systemId) {
		this.systemId = systemId;
	}
}
