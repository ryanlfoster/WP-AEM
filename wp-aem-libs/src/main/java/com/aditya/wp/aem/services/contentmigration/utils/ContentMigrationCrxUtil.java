/*
 * (c) 2014 Aditya Vennelakanti. All rights reserved. This material is solely and exclusively owned
 * by Aditya Vennelakanti and may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.contentmigration.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.apache.commons.lang.StringUtils;

import com.aditya.wp.aem.services.contentmigration.Job;
import com.aditya.wp.aem.services.contentmigration.JobMetadata;
import com.aditya.wp.aem.services.contentmigration.impl.JobMetadataImpl;
import com.day.cq.commons.jcr.JcrConstants;

/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 */
public final class ContentMigrationCrxUtil {

	private static final String CRX_BASE_PATH = "/var/wp/content_migration";
	private static final String PROP_LAST_EXEC_TIME = "last_exec_time";
	private static final String PROP_LAST_SIMULATION_TIME = "last_simulation_time";
	private static final String PROP_LAST_EXEC_FIXED_OCCURRENCES = "last_exec_fixed_occurrences";
	private static final String PROP_LAST_EXEC_FAILED_OCCURRENCES = "last_exec_failed_occurrences";
	private static final String PROP_LAST_EXEC_MESSAGES = "last_exec_messages";
	private static final String PROP_LAST_SIMULATION_MESSAGES = "last_simulation_messages";
	private static final String JOBS_PROPERTY_NAME = "jobs";

	/**
	 * Private constructor, this class contains only static methods.
	 */
	private ContentMigrationCrxUtil() {
		throw new AssertionError("This class is not ment to be instantiated.");
	}

	/**
	 * Stores the meta data for a job in the CRX.
	 * 
	 * @param session
	 *            the crx session
	 * @param metaData
	 *            the meta data to be stored
	 * @param wasSimulate
	 *            whether the data was created in a simulation run.
	 * @throws RepositoryException
	 *             when saving the data fails.
	 */
	public static void storeMetaDataForJob(final Session session,
	                                final JobMetadata metaData,
	                                final boolean wasSimulate) throws RepositoryException {
		Node node = session.getNode(ContentMigrationCrxUtil.CRX_BASE_PATH);
		if (node.hasNode(metaData.getJob().getClass().getName())) {
			node = node.getNode(metaData.getJob().getClass().getName());
		} else {
			node = node.addNode(metaData.getJob().getClass().getName(), JcrConstants.NT_UNSTRUCTURED);
		}
		if (!wasSimulate) {
			final Calendar cal = new GregorianCalendar();
			cal.setTime(metaData.getLastExecutionTime());
			node.setProperty(ContentMigrationCrxUtil.PROP_LAST_EXEC_TIME, cal);
			node.setProperty(ContentMigrationCrxUtil.PROP_LAST_EXEC_FIXED_OCCURRENCES, metaData.getFixedOccurrencesInLastRun());
			node.setProperty(ContentMigrationCrxUtil.PROP_LAST_EXEC_FAILED_OCCURRENCES, metaData.getFailedOccurrencesInLastRun());
			final Value[] messageValues = new Value[metaData.getErrorMessagesOfLastRun().size()];
			for (int i = 0; i < metaData.getErrorMessagesOfLastRun().size(); i++) {
				messageValues[i] = session.getValueFactory().createValue(metaData.getErrorMessagesOfLastRun().get(i));
			}
			node.setProperty(ContentMigrationCrxUtil.PROP_LAST_EXEC_MESSAGES, messageValues);
		} else {
			final Calendar cal = new GregorianCalendar();
			cal.setTime(metaData.getLastSimulationTime());
			node.setProperty(ContentMigrationCrxUtil.PROP_LAST_SIMULATION_TIME, cal);
			final Value[] messageValues = new Value[metaData.getErrorMessagesOfLastSimulation().size()];
			for (int i = 0; i < metaData.getErrorMessagesOfLastSimulation().size(); i++) {
				messageValues[i] = session.getValueFactory().createValue(metaData.getErrorMessagesOfLastSimulation().get(i));
			}
			node.setProperty(ContentMigrationCrxUtil.PROP_LAST_SIMULATION_MESSAGES, messageValues);
		}
		session.save();
	}

	/**
	 * Loads meta data for a certain content migration job from the CRX.
	 * 
	 * @param session
	 *            the CRX session
	 * @param job
	 *            the job to get meta data for
	 * @return the meta data
	 * @throws RepositoryException
	 *             when reading from the repository fails.
	 */
	public static JobMetadataImpl getMetaDataForJob(final Session session,
	                                         final Job job) throws RepositoryException {
		final JobMetadataImpl metaData = new JobMetadataImpl(job);
		Node node = session.getNode(ContentMigrationCrxUtil.CRX_BASE_PATH);
		if (node.hasNode(job.getClass().getName())) {
			node = node.getNode(job.getClass().getName());
			if (node.hasProperty(ContentMigrationCrxUtil.PROP_LAST_EXEC_TIME)) {
				metaData.setLastExecutionTime(node.getProperty(ContentMigrationCrxUtil.PROP_LAST_EXEC_TIME).getDate().getTime());
			}
			if (node.hasProperty(ContentMigrationCrxUtil.PROP_LAST_SIMULATION_TIME)) {
				metaData.setLastSimulationTime(node.getProperty(ContentMigrationCrxUtil.PROP_LAST_SIMULATION_TIME).getDate().getTime());
			}
			if (node.hasProperty(ContentMigrationCrxUtil.PROP_LAST_EXEC_FIXED_OCCURRENCES)) {
				metaData.setFixedOccurrencesInLastRun((int) node.getProperty(ContentMigrationCrxUtil.PROP_LAST_EXEC_FIXED_OCCURRENCES).getLong());
			}
			if (node.hasProperty(ContentMigrationCrxUtil.PROP_LAST_EXEC_FAILED_OCCURRENCES)) {
				metaData.setFailedOccurrencesInLastRun((int) node.getProperty(ContentMigrationCrxUtil.PROP_LAST_EXEC_FAILED_OCCURRENCES).getLong());
			}
			if (node.hasProperty(ContentMigrationCrxUtil.PROP_LAST_EXEC_MESSAGES)) {
				final Property prop = node.getProperty(ContentMigrationCrxUtil.PROP_LAST_EXEC_MESSAGES);
				final List<String> messages = new ArrayList<String>();
				for (Value v : prop.getValues()) {
					messages.add(v.getString());
				}
				metaData.setErrorMessages(messages);
			}
			if (node.hasProperty(ContentMigrationCrxUtil.PROP_LAST_SIMULATION_MESSAGES)) {
				final Property prop = node.getProperty(ContentMigrationCrxUtil.PROP_LAST_SIMULATION_MESSAGES);
				final List<String> messages = new ArrayList<String>();
				for (Value v : prop.getValues()) {
					messages.add(v.getString());
				}
				metaData.setErrorMessagesSimulation(messages);
			}
		}
		return metaData;
	}

	/**
	 * Ensures that the base-path in the repository under which this service stores its data is
	 * present. If it is not yet present, it will automatically be created.
	 * 
	 * @param session
	 *            the crx session
	 * @throws RepositoryException
	 *             when creating the CRX base path fails.
	 */
	public static void ensureCrxBasePathExists(final Session session) throws RepositoryException {
		Node currentNode = session.getRootNode();
		final String[] parts = ContentMigrationCrxUtil.CRX_BASE_PATH.split("/");
		boolean changed = false;
		for (String pathElem : parts) {
			if (StringUtils.isNotEmpty(pathElem)) {
				if (currentNode.hasNode(pathElem)) {
					currentNode = currentNode.getNode(pathElem);
				} else {
					currentNode = currentNode.addNode(pathElem, "sling:Folder");
					changed = true;
				}
			}
		}
		if (changed) {
			session.save();
		}
	}

	/**
	 * Reads a list of all content migration jobs from the CRX.
	 * 
	 * @param session
	 *            the crx session
	 * @return list of content migration jobs.
	 */
	public static List<String> getContentMigrationJobs(final Session session) {
		final List<String> classNames = new ArrayList<String>();
		try {
			final Node node = session.getNode(CRX_BASE_PATH);
			if (node.hasProperty(JOBS_PROPERTY_NAME)) {
				final Value[] values = node.getProperty(JOBS_PROPERTY_NAME).getValues();
				for (int i = 0; i < values.length; i++) {
					classNames.add(values[i].getString().trim());
				}
			}
		} catch (RepositoryException e) {
			throw new IllegalStateException("Could not read from CRX.", e);
		}
		return classNames;
	}
}
