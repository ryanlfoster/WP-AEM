/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.contentmigration.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.RepositoryException;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.runmode.RunMode;
import org.apache.sling.scripting.jsp.taglib.GetPropertyTag;
import org.apache.sling.settings.SlingSettingsService;

import com.aditya.wp.aem.services.contentmigration.ContentMigrationService;
import com.aditya.wp.aem.services.contentmigration.Job;
import com.aditya.wp.aem.services.contentmigration.JobMetadata;
import com.aditya.wp.aem.services.contentmigration.SystemInfo;
import com.aditya.wp.aem.services.contentmigration.utils.ContentMigrationCrxUtil;
import com.aditya.wp.aem.services.contentmigration.utils.ContentMigrationRemoteConnectionUtil;
import com.aditya.wp.aem.services.contentmigration.utils.JobTokenUtil;
import com.aditya.wp.aem.services.core.AbstractService;
import com.aditya.wp.aem.services.core.JcrService;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
@Service(ContentMigrationService.class)
@Component(name = "com.aditya.wp.aem.services.contentmigration.ContentMigrationService", label="WP Content Migration", immediate = true, metatype = true)
public class ContentMigrationServiceImpl extends AbstractService<ContentMigrationServiceImpl> implements ContentMigrationService {

    @Property(value = "http://%:4503/bin/receive%")
    private static final String PUBLISHER_URL_PATTERN = "contentMigrationService.publisherUrlPattern";

    @Property(value = "/etc/admin/content_migration")
    private static final String CONTENT_MIGRATION_PAGE_PATH = "contentMigrationService.contentMigrationPagePath";

    @Property(value = { "FelixStartLevel", "" })
    private static final String ACTIVATION_THREAD_WHITELIST = "contentMigrationService.activationThreadWhilelist";

    private List<Job> jobs;

    private List<SystemInfo> systems;

    private LocalJobQueue localJobQueue;

    @Reference
    private Scheduler scheduler;

    @Reference
    private SlingSettingsService slingSettingsService;

    @Reference
    private JcrService jcrService;

    private ContentMigrationRemoteConnectionUtil remoteUtil;

    @Activate
    protected final void activate(final Map<String, Object> config) {

        getLog(this).info("Content-migration-service is being activated...");

        final String publisherUrlPattern = PropertiesUtil.toString(config.get(PUBLISHER_URL_PATTERN), null);
        final String contentMigrationPagePath = PropertiesUtil.toString(config.get(CONTENT_MIGRATION_PAGE_PATH), null);
        final String[] activationThreadWhitelist = PropertiesUtil.toStringArray(config.get(ACTIVATION_THREAD_WHITELIST), null);

        // Ensure that the base path exists:
        try {
            ContentMigrationCrxUtil.ensureCrxBasePathExists(this.jcrService.getAdminSession());
        } catch (RepositoryException e) {
            final String message = "Unable to read from or write to CRX when checking for CRX base path.";
            getLog(this).error(message);
            throw new IllegalStateException(message, e);
        }

        // Instantiate all jobs.
        this.jobs = new ArrayList<Job>();
        for (String jobClassName : ContentMigrationCrxUtil.getContentMigrationJobs(this.jcrService.getAdminSession())) {
            try {
                final Job job = (Job) Class.forName(jobClassName).newInstance();
                this.jobs.add(job);
            } catch (InstantiationException e) {
                final String message = "Unable to create instance of Job-class '" + jobClassName
                        + "'. ContentMigrationService cannot be started.";
                getLog(this).error(message);
                throw new IllegalStateException(message, e);
            } catch (IllegalAccessException e) {
                final String message = "Unable to create instance of Job-class '" + jobClassName
                        + "'. ContentMigrationService cannot be started.";
                getLog(this).error(message);
                throw new IllegalStateException(message, e);
            } catch (ClassNotFoundException e) {
                final String message = "Unable to load Job-class '" + jobClassName
                        + "', class not found. ContentMigrationService cannot be started.";
                getLog(this).error(message);
                throw new IllegalStateException(message, e);
            }
        }

        // Create a list of all relevant systems:
        this.systems = buildSystemList(publisherUrlPattern, contentMigrationPagePath);

        // Create the token util:
        JobTokenUtil.createInstance(this.jobs.toArray(new Job[this.jobs.size()]), this.systems.toArray(new SystemInfo[this.systems.size()]));

        // Create the job queue for jobs that run locally:
        SystemInfo localSystem = null;
        for (SystemInfo system : this.systems) {
            if (system.getSystemType().equals(SystemInfo.SystemType.LOCAL)) {
                localSystem = system;
                break;
            }
        }
        this.localJobQueue = new LocalJobQueue(this.jcrService.getAdminSession(), this.scheduler, localSystem);

        // Place all "auto execute" jobs into the queue for execution:
        if (isCurrentThreadNameInWhitelist(activationThreadWhitelist)) {
            for (Job job : this.jobs) {
                try {
                    if (job.getExecutionType().equals(Job.ExecutionType.AUTO_EVERY_TIME)
                            || (job.getExecutionType().equals(Job.ExecutionType.AUTO_ONCE) && ContentMigrationCrxUtil.getMetaDataForJob(this.jcrService.getAdminSession(), job).getLastExecutionTime() == null)) {
                        this.localJobQueue.addJob(job, false);
                        getLog(this).info("Job '" + job.getClass().getName() + "' added to queue for auto-execution.");
                    }
                } catch (RepositoryException e) {
                    final String message = "Unable to get meta-data for job '" + job.getClass().getName()
                            + "'. Cannot start auto-execute job.";
                    getLog(this).error(message);
                }
            }
        } else {
            getLog(this).warn("Skipping execution of auto-execute threads because current thread not in white-list to start jobs");
        }

        getLog(this).info("Activation of Content-migration-service done.");
    }

	/* (non-Javadoc)
	 * @see com.aditya.wp.aem.services.contentmigration.ContentMigrationService#getAllJobs()
	 */
	@Override
	public List<Job> getAllJobs() {
		return Collections.unmodifiableList(this.jobs);
	}

	/* (non-Javadoc)
	 * @see com.aditya.wp.aem.services.contentmigration.ContentMigrationService#executeJob(com.aditya.wp.aem.services.contentmigration.Job, com.aditya.wp.aem.services.contentmigration.SystemInfo, boolean)
	 */
	@Override
	public String executeJob(final Job job,
	                         final SystemInfo systemInfo,
	                         final boolean simulate) {
		if (systemInfo.getSystemType().equals(SystemInfo.SystemType.LOCAL)) {
            return this.localJobQueue.addJob(job, simulate);
        } else {
            return this.remoteUtil.executeJob(job, systemInfo, simulate);
        }
	}

	/* (non-Javadoc)
	 * @see com.aditya.wp.aem.services.contentmigration.ContentMigrationService#getMetaDataForJob(com.aditya.wp.aem.services.contentmigration.Job, com.aditya.wp.aem.services.contentmigration.SystemInfo)
	 */
	@Override
	public JobMetadata getMetaDataForJob(final Job job,
	                                     final SystemInfo system) throws RepositoryException {
		JobMetadataImpl metaData = null;
        if (system.getSystemType().equals(SystemInfo.SystemType.LOCAL)) {
            metaData = ContentMigrationCrxUtil.getMetaDataForJob(this.jcrService.getAdminSession(), job);
            metaData.setSystemInfo(system);
            final JobMetadata statusMetadata = this.localJobQueue.getJobStatus(job);
            metaData.setCurrentStatus(statusMetadata.getCurrentStatus());
            metaData.setToken(statusMetadata.getToken());
        } else {
            final List<JobMetadata> jobMetadataList = this.remoteUtil.getAllJobMetadataForSystem((RemoteSystemInfoImpl) system);
            for (JobMetadata jmd : jobMetadataList) {
                if (jmd.getJob().equals(job)) {
                    metaData = (JobMetadataImpl) jmd;
                    break;
                }
            }
        }
        return metaData;
	}

	/* (non-Javadoc)
	 * @see com.aditya.wp.aem.services.contentmigration.ContentMigrationService#getAllMetaData()
	 */
	@Override
	public Map<Job, List<JobMetadata>> getAllMetaData() throws RepositoryException {
		final Map<Job, List<JobMetadata>> metaDatasByJob = new HashMap<Job, List<JobMetadata>>();
        for (Job job : this.jobs) {
            metaDatasByJob.put(job, new ArrayList<JobMetadata>());
        }

        for (SystemInfo system : this.systems) {
            if (system.getSystemType().equals(SystemInfo.SystemType.LOCAL)) {
                for (Job job : this.jobs) {
                    final JobMetadata metaData = getMetaDataForJob(job, system);
                    metaDatasByJob.get(job).add(metaData);
                }
            } else if (system.getSystemStatus().equals(SystemInfo.SystemStatus.OK)) {
                final List<JobMetadata> systemMetaDatas = this.remoteUtil
                        .getAllJobMetadataForSystem((RemoteSystemInfoImpl) system);
                for (JobMetadata jmd : systemMetaDatas) {
                    metaDatasByJob.get(jmd.getJob()).add(jmd);
                }
            }

        }
        return metaDatasByJob;
	}

	/* (non-Javadoc)
	 * @see com.aditya.wp.aem.services.contentmigration.ContentMigrationService#getResult(java.lang.String)
	 */
	@Override
	public JobMetadata getResult(String token) {
		final SystemInfo system = JobTokenUtil.getInstance().getSystemFromToken(token);
        final JobMetadata results;
        if (system.getSystemType().equals(SystemInfo.SystemType.LOCAL)) {
            results = this.localJobQueue.getExecutionResults(token);
            if (null != results) {
                final JobMetadata statusMetadata = this.localJobQueue.getJobStatus(results.getJob());
                ((JobMetadataImpl) results).setCurrentStatus(statusMetadata.getCurrentStatus());
            }
        } else {
            results = this.remoteUtil.getResults(token, system);
        }

        return results;
	}

	/* (non-Javadoc)
	 * @see com.aditya.wp.aem.services.contentmigration.ContentMigrationService#getAllSystems()
	 */
	@Override
	public List<SystemInfo> getAllSystems() {
		for (SystemInfo system : this.systems) {
            if (system.getSystemType().equals(SystemInfo.SystemType.REMOTE)) {
                synchronized (system) {
                    this.remoteUtil.determineSystemStatus((RemoteSystemInfoImpl) system);
                }
            }
        }
        return Collections.unmodifiableList(this.systems);
	}

    /**
     * Checks, whether the current thread is in the white-list of threads that are allowed to execute jobs during
     * service activation.
     * 
     * @param activationThreadWhitelist
     *            the white-list of thread names (or patterns) which are allowed to execute jobs.
     * @return see above.
     */
    private boolean isCurrentThreadNameInWhitelist(final String[] activationThreadWhitelist) {
        final String currentThreadName = Thread.currentThread().getName();
        for (String white : activationThreadWhitelist) {
            if (currentThreadName.matches(white)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Builds a list of systems that this service can deal with. The list will always contain the local system. If the
     * local system is an authoring system and if publishers are found that are connected via /etc/replication, those
     * will also be added to the list.
     * 
     * @param publisherUrlPattern
     *            the pattern that is used to search for publishers that might be configured in /etc/replication
     * @param contentMigrationPagePath
     *            the path of the content migration page.
     * @return list of systems.
     */
    private List<SystemInfo> buildSystemList(final String publisherUrlPattern,
                                             final String contentMigrationPagePath) {
        final List<SystemInfo> systemList = new ArrayList<SystemInfo>();
        SystemInfo localSystem = null;
        final String[] modes = this.slingSettingsService.getRunModes().toArray(new String[] { });
        if (null != modes && modes.length > 0 && Arrays.asList(modes).contains("author")) {
            try {
                this.remoteUtil = ContentMigrationRemoteConnectionUtil.createInstance(this.jcrService.getAdminSession(),
                        publisherUrlPattern, contentMigrationPagePath + "/jcr:content/content_migration_dashboard",
                        this.jobs);
                systemList.addAll(this.remoteUtil.getPublishServers());
            } catch (RepositoryException e) {
                throw new IllegalStateException("Error while activating content-migration-service: ", e);
            }
            for (SystemInfo system : systemList) {
                if (system.getSystemType().equals(SystemInfo.SystemType.REMOTE)) {
                    this.remoteUtil.determineSystemStatus((RemoteSystemInfoImpl) system);
                }
            }
            localSystem = new LocalSystemInfoImpl(LocalSystemInfoImpl.getLocalHostname() + "[author]");
        } else if (null != modes && modes.length > 0 && Arrays.asList(modes).contains("publish")) {
            localSystem = new LocalSystemInfoImpl(LocalSystemInfoImpl.getLocalHostname() + "[publish]");
        } else {
            String type = "unknown";
            if (null != modes && modes.length > 0) {
                type = modes[0];
            }
            localSystem = new LocalSystemInfoImpl(LocalSystemInfoImpl.getLocalHostname() + "[" + type + "]");
        }
        systemList.add(localSystem);
        return systemList;
    }
}
