/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.utils.uri;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.aditya.gmwp.aem.utils.EncodeDecodeUtil;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class UriBuilder {

	private final String baseUri;

	private final GMUri gmuri;

	/**
	 * Instantiates a new uri builder. All whitespaces in the given baseUri will be removed
	 *
	 * @param baseUri
	 *            the base uri
	 */
	public UriBuilder(final String baseUri) {
		this.baseUri = StringUtils.stripToEmpty(baseUri);
		this.gmuri = new UriExtractor(baseUri).getGMUri();
	}

	/**
	 * Adds all parameters (a parameter can have multiple values).
	 *
	 * @param parameters
	 *            the parameters
	 * @return
	 */
	public final UriBuilder addAllMultiParameters(final Map<String, Set<String>> parameters) {
		if (parameters != null) {
			for (Map.Entry<String, Set<String>> parameterEntry : parameters.entrySet()) {
				for (String value : parameterEntry.getValue()) {
					addParameter(parameterEntry.getKey(), value);
				}
			}
		}
		return this;
	}

	/**
	 * Adds all parameters.
	 *
	 * @param parameters
	 *            the parameters
	 * @return
	 */
	public final UriBuilder addAllParameters(final Map<String, String> parameters) {
		if (parameters != null) {
			for (Map.Entry<String, String> parameterEntry : parameters.entrySet()) {
				addParameter(parameterEntry.getKey(), parameterEntry.getValue());
			}
		}
		return this;
	}

	/**
	 * Adds selectors.
	 *
	 * @param selectors
	 *            the selectors
	 * @return
	 */
	public final UriBuilder addAllSelectors(final List<String> selectors) {
		if (selectors != null) {
			for (String selector : selectors) {
				addSelector(selector);
			}
		}
		return this;
	}

	/**
	 * Adds the parameter. A key can have multible values.
	 *
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @return
	 */
	public final UriBuilder addParameter(final String key,
	                                     final String value) {
		if (!StringUtils.isEmpty(key)) {

			Set<String> values = this.gmuri.getParameters().get(key);

			if (values == null) {
				values = new HashSet<String>();
			}

			values.add(value);
			this.gmuri.getParameters().put(key, values);
		}
		return this;
	}

	/**
	 * Adds the selector. If selector string contains dots, it will be split at these and several
	 * selectors will be stored.
	 *
	 * @param selector
	 *            the selector string
	 * @return
	 */
	public final UriBuilder addSelector(final String selector) {
		if (!StringUtils.isEmpty(selector)) {

			final String[] selectorArray = selector.split("\\.");
			for (String s : selectorArray) {
				if (StringUtils.isNotBlank(s)) {
					this.gmuri.getSelectors().add(s);
				}
			}
		}
		return this;
	}

	/**
	 * Builds the Uri. All blanks will be removed.
	 *
	 * @return the string
	 */
	public final String build() {
		// [scheme:][//host][:port][contentPath][selectors][.extension][suffix][?query][#anchor]
		final StringBuilder workingURI = new StringBuilder(128);
		if (StringUtils.isNotBlank(this.gmuri.getScheme())) {
			workingURI.append(this.gmuri.getScheme());
			workingURI.append(':');
		}
		if (StringUtils.isNotBlank(this.gmuri.getHost())) {
			appendHost(workingURI);
		}
		if (StringUtils.isNotBlank(this.gmuri.getContentPath())) {
			appendContentPath(workingURI);
		}
		for (String selector : this.gmuri.getSelectors()) {
			workingURI.append('.');
			workingURI.append(selector);
		}
		if (StringUtils.isNotBlank(this.gmuri.getExtension()) && StringUtils.isNotEmpty(this.gmuri.getContentPath())
				&& !"/".equals(this.gmuri.getContentPath())) {
			workingURI.append('.');
			workingURI.append(this.gmuri.getExtension());
		}
		if (StringUtils.isNotBlank(this.gmuri.getSuffix())) {
			workingURI.append(this.gmuri.getSuffix());
		}
		if (this.gmuri.getParameters().size() > 0) {
			appendParameters(workingURI);
		}
		if (StringUtils.isNotBlank(this.gmuri.getAnchor())) {
			workingURI.append('#');
			workingURI.append(this.gmuri.getAnchor());
		}

		return workingURI.toString().replaceAll("\\s", StringUtils.EMPTY);
	}

	/**
	 * Gets the anchor.
	 *
	 * @return the anchor
	 */
	public final String getAnchor() {
		return this.gmuri.getAnchor();
	}

	/**
	 * Gets the base uri.
	 *
	 * @return the baseUri
	 */
	public final String getBaseUri() {
		return this.baseUri;
	}

	/**
	 * Gets the content path.
	 *
	 * @return the contentPath
	 */
	public final String getContentPath() {
		return this.gmuri.getContentPath();
	}

	/**
	 * Gets the extension.
	 *
	 * @return the extension
	 */
	public final String getExtension() {
		return this.gmuri.getExtension();
	}

	/**
	 * Gets the host.
	 *
	 * @return the host
	 */
	public final String getHost() {
		return this.gmuri.getHost();
	}

	/**
	 * Gets the parameters.
	 *
	 * @return the parameters
	 */
	public final Map<String, Set<String>> getParameters() {
		return this.gmuri.getParameters();
	}

	/**
	 * Gets the scheme.
	 *
	 * @return the scheme
	 */
	public final String getScheme() {
		return this.gmuri.getScheme();
	}

	/**
	 * Gets the selectors.
	 *
	 * @return the selectors
	 */
	public final List<String> getSelectors() {
		return this.gmuri.getSelectors();
	}

	/**
	 * Gets the suffix.
	 *
	 * @return the suffix
	 */
	public final String getSuffix() {
		return this.gmuri.getSuffix();
	}

	/**
	 * Removes the anchor.
	 *
	 * @return true, if successful
	 */
	public final UriBuilder removeAnchor() {
		if (this.gmuri.getAnchor() != null) {
			this.gmuri.setAnchor(null);
		}
		return this;
	}

	/**
	 * Removes the extension.
	 *
	 * @return true, if successful
	 */
	public final UriBuilder removeExtension() {
		if (this.gmuri.getExtension() != null) {
			this.gmuri.setExtension(null);
		}
		return this;
	}

	/**
	 * Removes the host.
	 *
	 * @return true, if successful
	 */
	public final UriBuilder removeHost() {
		if (this.gmuri.getHost() != null) {
			this.gmuri.setHost(null);
		}
		return this;
	}

	/**
	 * Removes the parameter.
	 *
	 * @param key
	 *            the key
	 * @return true, if successful
	 */
	public final UriBuilder removeParameter(final String key) {
		if (this.gmuri.getParameters() == null || !this.gmuri.getParameters().containsKey(key)) {
			return this;
		}
		this.gmuri.getParameters().remove(key);
		return this;
	}

	/**
	 * Removes the scheme.
	 *
	 * @return true, if successful
	 */
	public final UriBuilder removeScheme() {
		if (this.gmuri.getScheme() != null) {
			this.gmuri.setScheme(null);
		}
		return this;
	}

	/**
	 * Removes the selector.
	 *
	 * @param selector
	 *            the selector
	 * @return true, if successful
	 */
	public final UriBuilder removeSelector(final String selector) {
		if (this.gmuri.getSelectors() != null) {
			this.gmuri.getSelectors().remove(selector);
		}
		return this;
	}

	/**
	 * Sets the anchor and removes all '#' out of the given value.
	 *
	 * @param anchor
	 *            the new anchor
	 * @return true, if successful
	 */
	public final UriBuilder setAnchor(final String anchor) {
		if (!StringUtils.isEmpty(anchor)) {
			this.gmuri.setAnchor(anchor.replaceAll("#", StringUtils.EMPTY));
		}
		return this;
	}

	/**
	 * Sets the extension. and removes all '.' out of the given value the extension to set.
	 *
	 * @param extension
	 *            the new extension
	 * @return true, if successful
	 */
	public final UriBuilder setExtension(final String extension) {
		if (!StringUtils.isEmpty(extension)) {
			this.gmuri.setExtension(extension.replaceAll("\\.", StringUtils.EMPTY));
		}
		return this;
	}

	/**
	 * Sets the host.
	 *
	 * @param host
	 *            the new host
	 * @return UriBuilder
	 */
	public final UriBuilder setHost(final String host) {
		if (!StringUtils.isEmpty(host)) {
			this.gmuri.setHost(host);
		}
		return this;
	}

	/**
	 * Sets the scheme.
	 *
	 * @param scheme
	 *            the new scheme
	 * @return UriBuilder
	 */
	public final UriBuilder setScheme(final String scheme) {
		if (!StringUtils.isEmpty(scheme)) {
			this.gmuri.setScheme(scheme);
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("UriBuilder [anchor=");
		builder.append(this.gmuri.getAnchor());
		builder.append(", baseUri=");
		builder.append(this.baseUri);
		builder.append(", contentPath=");
		builder.append(this.gmuri.getContentPath());
		builder.append(", extension=");
		builder.append(this.gmuri.getExtension());
		builder.append(", host=");
		builder.append(this.gmuri.getHost());
		builder.append(", parameters=");
		builder.append(this.gmuri.getParameters());
		builder.append(", port=");
		builder.append(this.gmuri.getPort());
		builder.append(", scheme=");
		builder.append(this.gmuri.getScheme());
		builder.append(", selectors=");
		builder.append(this.gmuri.getSelectors());
		builder.append(", suffix=");
		builder.append(this.gmuri.getSuffix());
		builder.append(']');
		return builder.toString();
	}

	/**
	 * Append content path.
	 *
	 * @param workingURI
	 *            the working uri
	 */
	private void appendContentPath(final StringBuilder workingURI) {
		String path = this.gmuri.getContentPath();
		if (path != null && !"/".equals(path)) {
			// if current string ends with ':' or '/', delete a leading '/' of the contentPath
			if (workingURI.length() > 0) {
				final char lastChar = workingURI.charAt(workingURI.length() - 1);
				if ((lastChar == ':' || lastChar == '/') && (path.indexOf('/') == 0)) {
					path = path.substring(1);
				}
			}
			// if path ends with '/' and there are selectors, an extension or suffix delete it
			final int len = path.length();
			final char lastContentPathChar = path.charAt(len - 1);
			if (lastContentPathChar == '/' && (this.gmuri.getSelectors().size() > 0 //
					|| StringUtils.isNotBlank(this.gmuri.getExtension()) || StringUtils.isNotEmpty(this.gmuri.getSuffix()))) {
				path = path.substring(0, len - 1);
			}
		}
		workingURI.append(path);
	}

	/**
	 * Append host.
	 *
	 * @param workingURI
	 *            the working uri
	 */
	private void appendHost(final StringBuilder workingURI) {
		workingURI.append("//");
		workingURI.append(this.gmuri.getHost());
		if (this.gmuri.getPort() > 0) {
			workingURI.append(':');
			workingURI.append(this.gmuri.getPort());
		}
	}

	/**
	 * Append parameters.
	 *
	 * @param workingURI
	 *            the working uri
	 */
	private void appendParameters(final StringBuilder workingURI) {
		final List<String> keys = new ArrayList<String>(this.gmuri.getParameters().keySet());
		boolean isFirstParam = true;
		for (String paramKey : keys) {
			final Set<String> paramValues = this.gmuri.getParameters().get(paramKey);
			if (paramValues != null) {
				for (String paramValue : paramValues) {
					if (isFirstParam) {
						workingURI.append('?');
						isFirstParam = false;
					} else {
						workingURI.append('&');
					}
					workingURI.append(EncodeDecodeUtil.urlEncode(paramKey));
					workingURI.append('=');
					if (StringUtils.isNotBlank(paramValue)) {
						workingURI.append(EncodeDecodeUtil.urlEncode(paramValue));
					}
				}
			} else {
				if (isFirstParam) {
					workingURI.append('?');
					isFirstParam = false;
				} else {
					workingURI.append('&');
				}
				workingURI.append(paramKey);
				workingURI.append('=');
			}
		}
	}
}
