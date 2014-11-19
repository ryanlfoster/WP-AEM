/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.contentmigration.utils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.sling.commons.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.gmwp.aem.services.contentmigration.Job;
import com.aditya.gmwp.aem.services.contentmigration.JobMetadata;
import com.aditya.gmwp.aem.services.contentmigration.SystemInfo;
import com.aditya.gmwp.aem.services.contentmigration.impl.RemoteSystemInfoImpl;
import com.aditya.gmwp.aem.utils.EncodeDecodeUtil;
import com.day.cq.analytics.sitecatalyst.util.HttpClientUtils;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class ContentMigrationRemoteConnectionUtil {

	private static final String UNABLE_TO_LOAD_META_DATA = "Unable to load job-meta-data from system '";

	private static final Logger LOG = LoggerFactory.getLogger(ContentMigrationRemoteConnectionUtil.class);

	private static final String PROTOCOL_HTTP = "http://";

	private static final String QUERY_FOR_PUBLISHER_URLS = "/jcr:root/etc/replication/agents.author//*[ jcr:content/@sling:resourceType = 'cq/replication/components/agent' and jcr:like(jcr:content/@transportUri, '${PUBLISH_URL_PATTERN}')]";

	private static ContentMigrationRemoteConnectionUtil mySelf;

	private final Session session;

	private final String publisherUrlPattern;

	private final HttpClient httpClient = new HttpClient();

	private final String contentMigrationComponentPath;

	private final List<Job> allKnownJobs;

	/**
	 * Tries to determine all publisher servers that are known to this author server (if it is an
	 * author) by querying the replication configuration.
	 * 
	 * @return list of connected publishers
	 * @throws RepositoryException
	 *             when reading from the repository fails.
	 */
	public List<SystemInfo> getPublishServers() throws RepositoryException {
		final List<SystemInfo> systemInfos = new ArrayList<SystemInfo>();
		final String queryString = QUERY_FOR_PUBLISHER_URLS.replace("${PUBLISH_URL_PATTERN}", this.publisherUrlPattern);
		final Query query = this.session.getWorkspace().getQueryManager().createQuery(queryString, Query.XPATH);
		query.setLimit(Long.MAX_VALUE);
		final QueryResult result = query.execute();
		final NodeIterator nodeIter = result.getNodes();
		while (nodeIter.hasNext()) {
			final Node node = nodeIter.nextNode();
			final String transportUri = node.getNode("jcr:content").getProperty("transportUri").getString();
			int beginIndex = 0;
			if (transportUri.startsWith(PROTOCOL_HTTP)) {
				beginIndex = PROTOCOL_HTTP.length();
			}
			final int endIndex = transportUri.indexOf('/', beginIndex);
			final String systemName = transportUri.substring(beginIndex, endIndex);
			final String baseUrl = transportUri.substring(0, endIndex);
			systemInfos.add(new RemoteSystemInfoImpl(systemName, baseUrl));
		}
		return systemInfos;
	}

	/**
	 * Determines the status of the given system. The "status" property of the provided systemInfo
	 * will be updated.
	 * 
	 * @param systemInfo
	 *            the system to be interrogated
	 */
	public void determineSystemStatus(final RemoteSystemInfoImpl systemInfo) {
		final String urlString = systemInfo.getSystemBaseUrl() + this.contentMigrationComponentPath + ".ping.json";
		try {
			final URL url = new URL(urlString);
			final GetMethod getMethod = buildGetMethod(url);
			final int responseCode = this.httpClient.executeMethod(buildHostConfiguration(url), getMethod);
			if (responseCode == HttpStatus.SC_OK) {
				final String response = getMethod.getResponseBodyAsString(Integer.MAX_VALUE);
				try {
					final String status = ContentMigrationJsonUtil.getStringFromJson(response, "status");
					final String systemId = ContentMigrationJsonUtil.getStringFromJson(response, "systemId");
					if ("OK".equals(status) && null != systemId) {
						systemInfo.setSystemStatus(SystemInfo.SystemStatus.OK);
						systemInfo.setSystemId(systemId);
					} else {
						systemInfo.setSystemStatus(SystemInfo.SystemStatus.ERROR);
						LOG.warn("Response to 'ping' request to '" + urlString + "' was answered with the following values: status=" + status + ", systemId="
						        + systemId);
					}
				} catch (JSONException e) {
					systemInfo.setSystemStatus(SystemInfo.SystemStatus.ERROR);
					LOG.warn("Response to 'ping' request to '" + urlString + "' was answered with message '" + response + "'.");
				}
			} else {
				systemInfo.setSystemStatus(SystemInfo.SystemStatus.ERROR);
				LOG.warn("Response to 'ping' request to '" + urlString + "' was answered with status-code " + responseCode);
			}
		} catch (ConnectException e) {
			systemInfo.setSystemStatus(SystemInfo.SystemStatus.ERROR);
			LOG.info("Unable to connect to '" + urlString + "', system-status will be set to 'error'.");
		} catch (IOException e) {
			systemInfo.setSystemStatus(SystemInfo.SystemStatus.ERROR);
			final String message = "Unable to 'ping' publisher at " + systemInfo.getSystemBaseUrl();
			LOG.error(message);
			throw new IllegalStateException(message, e);
		}
	}

	/**
	 * Fetches the meta-data of all jobs that are known on the remote system.
	 * 
	 * @param systemInfo
	 *            the system to connect to.
	 * @return see above.
	 */
	public List<JobMetadata> getAllJobMetadataForSystem(final RemoteSystemInfoImpl systemInfo) {
		List<JobMetadata> metaDatas;
		final String urlString = systemInfo.getSystemBaseUrl() + this.contentMigrationComponentPath + ".jobDataForSystem.json";
		try {
			final URL url = new URL(urlString);
			final HostConfiguration hostConfiguration = buildHostConfiguration(url);
			final GetMethod getMethod = buildGetMethod(url);
			final int status = this.httpClient.executeMethod(hostConfiguration, getMethod);
			if (status == HttpStatus.SC_OK) {
				final String response = getMethod.getResponseBodyAsString(Integer.MAX_VALUE);

				metaDatas = ContentMigrationJsonUtil.jsonStringToJobMetadataList(response, systemInfo, this.allKnownJobs);

			} else {
				final String message = UNABLE_TO_LOAD_META_DATA + systemInfo.getSystemBaseUrl() + "', response code was " + status + " for URL '" + urlString
				        + "'.";
				LOG.error(message);
				throw new IllegalStateException(message);
			}
		} catch (IOException e) {
			final String message = UNABLE_TO_LOAD_META_DATA + systemInfo.getSystemBaseUrl() + "', execption was:" + e.toString();
			LOG.error(message);
			throw new IllegalStateException(message, e);
		}
		return metaDatas;
	}

	/**
	 * Executes the job on the remote system.
	 * 
	 * @param job
	 *            the job
	 * @param system
	 *            the system
	 * @param simulate
	 *            whether to simulate
	 * @return the token
	 */
	public String executeJob(final Job job,
	                  final SystemInfo system,
	                  final boolean simulate) {
		String token = null;
		String selector;
		if (simulate) {
			selector = "updatePendingOccurrences";
		} else {
			selector = "execute";
		}
		final String urlString = system.getSystemBaseUrl() + this.contentMigrationComponentPath + "." + selector + ".json?job=" + job.getClass().getName()
		        + "&system=" + EncodeDecodeUtil.urlEncode(system.getSystemId());
		try {
			final URL url = new URL(urlString);
			final HostConfiguration hostConfiguration = buildHostConfiguration(url);
			final GetMethod getMethod = buildGetMethod(url);
			final int status = this.httpClient.executeMethod(hostConfiguration, getMethod);
			if (status == HttpStatus.SC_OK) {
				final String response = getMethod.getResponseBodyAsString(Integer.MAX_VALUE);
				try {
					token = ContentMigrationJsonUtil.getStringFromJson(response, "token");
				} catch (JSONException e) {
					final String message = "Could not convert response '" + response + "' to a JSON object.";
					LOG.error(message);
					throw new IllegalStateException(message);
				}
				if (null == token) {
					final String message = "Received no token in request to system '" + system.getSystemId() + "'. Response was: " + response;
					LOG.error(message);
					throw new IllegalStateException(message);
				}
			} else {
				final String message = UNABLE_TO_LOAD_META_DATA + system.getSystemBaseUrl() + "', response code was " + status + " for URL '" + urlString
				        + "'.";
				LOG.error(message);
				throw new IllegalStateException(message);
			}
		} catch (IOException e) {
			final String message = UNABLE_TO_LOAD_META_DATA + system.getSystemBaseUrl() + "', execption was:" + e.toString();
			LOG.error(message);
			throw new IllegalStateException(message, e);
		}
		return token;
	}

	/**
	 * Fetches execution results.
	 * 
	 * @param token
	 *            the token
	 * @param system
	 *            the system to ask for results.
	 * @return the results as JobMetadata object.
	 */
	public JobMetadata getResults(final String token,
	                              final SystemInfo system) {
		JobMetadata metaData = null;
		final String urlString = system.getSystemBaseUrl() + this.contentMigrationComponentPath + ".refreshJobs.json?token="
		        + EncodeDecodeUtil.urlEncode(token);
		try {
			final URL url = new URL(urlString);
			final HostConfiguration hostConfiguration = buildHostConfiguration(url);
			final GetMethod getMethod = buildGetMethod(url);
			final int status = this.httpClient.executeMethod(hostConfiguration, getMethod);
			if (status == HttpStatus.SC_OK) {
				final String response = getMethod.getResponseBodyAsString(Integer.MAX_VALUE);
				final List<JobMetadata> metaDatas = ContentMigrationJsonUtil.jsonStringToJobMetadataList(response, system, this.allKnownJobs);
				if (null != metaDatas && metaDatas.size() > 0) {
					metaData = metaDatas.get(0);
				}
			} else {
				final String message = UNABLE_TO_LOAD_META_DATA + system.getSystemBaseUrl() + "', response code was " + status + " for URL '" + urlString
				        + "'.";
				LOG.error(message);
				throw new IllegalStateException(message);
			}
		} catch (IOException e) {
			final String message = UNABLE_TO_LOAD_META_DATA + system.getSystemBaseUrl() + "', execption was:" + e.toString();
			LOG.error(message);
			throw new IllegalStateException(message, e);
		}
		return metaData;
	}

	/**
	 * Creates the singleton instance.
	 * 
	 * @param session
	 *            the CRX session
	 * @param publisherUrlPattern
	 *            a URL pattern to be used in an XPATH query that searches for publisher URLs below
	 *            /etc/replication
	 * @param contentMigrationComponentPath
	 *            the path of the content migration dashboard on the content migration page
	 * @param allKnownJobs
	 *            a list of all known jobs
	 * @return the singleton instance
	 */
	public static ContentMigrationRemoteConnectionUtil createInstance(final Session session,
	                                                           final String publisherUrlPattern,
	                                                           final String contentMigrationComponentPath,
	                                                           final List<Job> allKnownJobs) {
		mySelf = new ContentMigrationRemoteConnectionUtil(session, publisherUrlPattern, contentMigrationComponentPath, allKnownJobs);
		return mySelf;
	}

	/**
	 * Creates the get method object for the http client.
	 * 
	 * @param url
	 *            the url to be fetched in the request
	 * @return the get method
	 */
	private GetMethod buildGetMethod(final URL url) {
		final GetMethod getMethod = new GetMethod(url.getPath());
		getMethod.setQueryString(url.getQuery());
		getMethod.addRequestHeader("accept-language", "en");
		return getMethod;
	}

	/**
	 * Builds the host configuration object for the http client.
	 * 
	 * @param url
	 *            the url
	 * @return the host configuration object.
	 */
	private HostConfiguration buildHostConfiguration(final URL url) {
		final HostConfiguration hostConfiguration = new HostConfiguration();
		hostConfiguration.setHost(url.getHost(), url.getPort(), url.getProtocol());
		return hostConfiguration;
	}

	/**
	 * Private constructor, this is a singleton.
	 * 
	 * @param session
	 *            CRX session
	 * @param publisherUrlPattern
	 *            a URL pattern to be used in an XPATH query that searches for publisher URLs below
	 *            /etc/replication
	 * @param contentMigrationComponentPath
	 *            the path of the content migration dashboard on the content migration page.
	 * @param allKnownJobs
	 *            all known jobs
	 */
	private ContentMigrationRemoteConnectionUtil(final Session session, final String publisherUrlPattern, final String contentMigrationComponentPath,
	        final List<Job> allKnownJobs) {
		this.session = session;
		this.publisherUrlPattern = publisherUrlPattern;
		this.contentMigrationComponentPath = contentMigrationComponentPath;
		this.allKnownJobs = allKnownJobs;
	}
}
