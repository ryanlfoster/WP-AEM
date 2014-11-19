/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.core.impl;

import java.io.File;
import java.io.InputStream;
import java.util.Dictionary;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.gmwp.aem.services.core.AbstractService;
import com.aditya.gmwp.aem.services.core.ExportService;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
@Component(name = "com.aditya.gmwp.aem.services.core.ExportService", label = "GMWP SFTP Export Service", description = "Allows for SFTP uploads.", enabled = true, metatype = true)
@Service(value = ExportService.class)
public class SFTPExportServiceImpl extends AbstractService<SFTPExportServiceImpl> implements ExportService {
	
    private static final int DEFAULT_SERVER_SFTP_PORT = 22;
    private static final String NAMESPACE = "sftpexportservice.";

    /** The Constant SERVER_URL_PROPERTY. */
    @Property(label = "Server URL", description = "The URL of the SFTP server, without schema (e.G. sftp://)", value = "getLog(this).gm.plusline.net")
    protected static final String SERVER_URL_PROPERTY = NAMESPACE + "serverurl";
    private String serverUrl;

    /** The Constant SERVER_PORT_PROPERTY. */
    @Property(label = "Server Port", description = "The Port of the SFTP server (e.G. 22), defaults to 22.", intValue = DEFAULT_SERVER_SFTP_PORT)
    protected static final String SERVER_PORT_PROPERTY = NAMESPACE + "serverport";
    private int serverPort = DEFAULT_SERVER_SFTP_PORT;

    /** The Constant USER_NAME_PROPERTY. */
    @Property(label = "Username", description = "The username for the SFTP connection")
    protected static final String USER_NAME_PROPERTY = NAMESPACE + "username";
    private String userName;

    /** The Constant PRIVATE_KEY_LOCATION_PROPERTY. */
    @Property(label = "Private key location", description = "The full file system path to the "
            + "private key used for SSH authentication (e.G. /home/namics/.ssh/id_rsa)")
    protected static final String PRIVATE_KEY_LOCATION_PROPERTY = NAMESPACE + "privatekeylocation";
    private String privateKeyLocation;

    /** The Constant KNOWN_HOSTS_FILE_LOCATION_PROPERTY. */
    @Property(label = "Known hosts file location", description = "Optional. Full file system path to the OpenSSH "
            + "known hosts file. Used to verify the identiy of the SFTP server. "
            + "(e.G. /home/namics/.ssh/known_hosts)")
    protected static final String KNOWN_HOSTS_FILE_LOCATION_PROPERTY = NAMESPACE + "knownhostsfilelocation";
    private String knownHostsFileLocation;

    /** The configured. */
    private boolean configured = true;

    /*
     * (non-Javadoc)
     * @see com.gm.gssm.gmds.cq.services.export.ExportService#fileExists(java.lang.String)
     */
    @Override
    public final boolean fileExists(final String path) {
        return new SftpConnectionTemplate<Boolean>() {

            @Override
            Boolean execute(final ChannelSftp sftpChannel) {
                try {
                    sftpChannel.lstat(path);
                    return true;
                } catch (SftpException e) {
                    return false;
                }
            };
        }.connect();

    }

    /*
     * (non-Javadoc)
     * @see com.gm.gssm.gmds.cq.services.export.ExportService#makeDirectory(java.lang.String)
     */
    @Override
    public final void makeDirectory(final String path) {
        new SftpConnectionTemplate<Void>() {

            @Override
            Void execute(final ChannelSftp sftpChannel) throws SftpException {
                sftpChannel.mkdir(path);
                return null;
            }
        }.connect();
    }

    /*
     * (non-Javadoc)
     * @see com.gm.gssm.gmds.cq.services.export.ExportService#putFile(java.io.InputStream, java.lang.String)
     */
    @Override
    public final void putFile(final InputStream uploadStream,
                              final String destination) {
        new SftpConnectionTemplate<Void>() {

            @Override
            Void execute(final ChannelSftp sftpChannel) throws SftpException {
                sftpChannel.put(uploadStream, destination);
                return null;
            }
        }.connect();
    }

    /*
     * (non-Javadoc)
     * @see com.gm.gssm.gmds.cq.services.export.ExportService#deleteFile(java.lang.String)
     */
    @Override
    public final void deleteFile(final String filePath) {
        new SftpConnectionTemplate<Void>() {

            @Override
            Void execute(final ChannelSftp sftpChannel) throws SftpException {
                sftpChannel.rm(filePath);
                return null;
            }
        }.connect();
    }

    /**
     * The Class SftpConnectionTemplate.
     * 
     * @param <T>
     *            the generic type
     */
    private class SftpConnectionTemplate<T> {

        /**
         * Connect.
         * 
         * @return the t
         */
        T connect() {
            JSch jsch = null;
            Session session = null;
            T returnValue = null;
            ChannelSftp sftpChannel = null;
            try {
                jsch = configureJSch();
                session = establishSession(jsch);
                sftpChannel = openSftpChannel(session);
                returnValue = execute(sftpChannel);
            } catch (JSchException e) {
                getLog(SFTPExportServiceImpl.this).error("Error during SFTP connection", e);
            } catch (SftpException e) {
                getLog(SFTPExportServiceImpl.this).error("Error during SFTP operation", e);
            } finally {
                try {
                    if (sftpChannel != null) {
                        sftpChannel.disconnect();
                    }
                } finally {
                    if (session != null) {
                        session.disconnect();
                    }
                }
            }
            return returnValue;
        }

        /**
         * Establish session.
         * 
         * @param jsch
         *            the jsch
         * @return the session
         * @throws JSchException
         *             the j sch exception
         */
        private Session establishSession(final JSch jsch) throws JSchException {
            final Session session = jsch.getSession(SFTPExportServiceImpl.this.userName,
                    SFTPExportServiceImpl.this.serverUrl, SFTPExportServiceImpl.this.serverPort);
            session.connect();
            return session;
        }

        /**
         * Configure j sch.
         * 
         * @return the j sch
         * @throws JSchException
         *             the j sch exception
         */
        private JSch configureJSch() throws JSchException {
            JSch jsch;
            jsch = new JSch();
            setHostsFileIfGivenElseDisableChecking(jsch);
            jsch.addIdentity(SFTPExportServiceImpl.this.privateKeyLocation);
            return jsch;
        }

        /**
         * Open sftp channel.
         * 
         * @param session
         *            the session
         * @return the channel sftp
         * @throws JSchException
         *             the j sch exception
         */
        private ChannelSftp openSftpChannel(final Session session) throws JSchException {
            ChannelSftp sftpChannel;
            final Channel channel = session.openChannel("sftp");
            channel.connect();
            sftpChannel = (ChannelSftp) channel;
            return sftpChannel;
        }

        /**
         * Sets the hosts file if given else disable checking.
         * 
         * @param jsch
         *            the new hosts file if given else disable checking
         * @throws JSchException
         *             the j sch exception
         */
        private void setHostsFileIfGivenElseDisableChecking(final JSch jsch) throws JSchException {
            if (StringUtils.isNotEmpty(SFTPExportServiceImpl.this.knownHostsFileLocation)) {
                jsch.setKnownHosts(SFTPExportServiceImpl.this.knownHostsFileLocation);
            } else {
                JSch.setConfig("StrictHostKeyChecking", "no");
            }
        }

        /**
         * Execute.
         * 
         * @param sftpChannel
         *            the sftp channel
         * @return the t
         * @throws SftpException
         *             the sftp exception
         */
        T execute(final ChannelSftp sftpChannel) throws SftpException {
            return null;
        }
    }

    /**
     * Gets the server url.
     * 
     * @return the serverUrl
     */
    protected final String getServerUrl() {
        return this.serverUrl;
    }

    /**
     * Sets the server url.
     * 
     * @param serverUrl
     *            the serverUrl to set
     */
    protected final void setServerUrl(final String serverUrl) {
        this.serverUrl = serverUrl;
    }

    /**
     * Gets the server port.
     * 
     * @return the serverPort
     */
    protected final int getServerPort() {
        return this.serverPort;
    }

    /**
     * Sets the server port.
     * 
     * @param serverPort
     *            the serverPort to set
     */
    protected final void setServerPort(final int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * Gets the user name.
     * 
     * @return the userName
     */
    protected final String getUserName() {
        return this.userName;
    }

    /**
     * Sets the user name.
     * 
     * @param userName
     *            the userName to set
     */
    protected final void setUserName(final String userName) {
        this.userName = userName;
    }

    /**
     * Gets the private key location.
     * 
     * @return the privateKeyLocation
     */
    protected final String getPrivateKeyLocation() {
        return this.privateKeyLocation;
    }

    /**
     * Sets the private key location.
     * 
     * @param privateKeyLocation
     *            the privateKeyLocation to set
     */
    protected final void setPrivateKeyLocation(final String privateKeyLocation) {
        this.privateKeyLocation = privateKeyLocation;
    }

    /**
     * Gets the known hosts file location.
     * 
     * @return the knownHostsFileLocation
     */
    protected final String getKnownHostsFileLocation() {
        return this.knownHostsFileLocation;
    }

    /**
     * Sets the known hosts file location.
     * 
     * @param knownHostsFileLocation
     *            the knownHostsFileLocation to set
     */
    protected final void setKnownHostsFileLocation(final String knownHostsFileLocation) {
        this.knownHostsFileLocation = knownHostsFileLocation;
    }

    /**
     * Activate.
     * 
     * @param context
     *            the context
     */
    public final void activate(final ComponentContext context) {
        @SuppressWarnings("rawtypes")
        final Dictionary properties = context.getProperties();
        if (containsNoValidValueFor(properties, PRIVATE_KEY_LOCATION_PROPERTY)
                || containsNoValidValueFor(properties, SERVER_URL_PROPERTY)
                || containsNoValidValueFor(properties, USER_NAME_PROPERTY)) {
            getLog(this).error("SFTP Export service is misconfigured!");
            getLog(this).error(String.format(
                    "PRIVATE_KEY_LOCATION_PROPERTY = %s, SERVER_URL_PROPERTY = %s, USER_NAME_PROPERTY = %s",
                    (properties.get(PRIVATE_KEY_LOCATION_PROPERTY)), (properties.get(SERVER_URL_PROPERTY)),
                    (properties.get(USER_NAME_PROPERTY))));
            this.configured = false;
            return;
        }

        this.serverUrl = (String) properties.get(SERVER_URL_PROPERTY);
        this.userName = (String) properties.get(USER_NAME_PROPERTY);
        this.privateKeyLocation = (String) properties.get(PRIVATE_KEY_LOCATION_PROPERTY);
        if (!new File(this.privateKeyLocation).exists()) {
            this.configured = false;
            getLog(this).error("The private key file needed to access the SFTP server is not accessible or does not exist.");
            return;
        }

        final String serverPortProperty = properties.get(SERVER_PORT_PROPERTY) + "";
        if (StringUtils.isNotEmpty(serverPortProperty)) {
            try {
                this.serverPort = Integer.parseInt(serverPortProperty);
            } catch (NumberFormatException e) {
                logErrorAndFallBackToDefaultPort(serverPortProperty);
            } catch (ClassCastException e) {
                logErrorAndFallBackToDefaultPort(serverPortProperty);
            }
        } else {
            logErrorAndFallBackToDefaultPort(serverPortProperty);
        }

        this.knownHostsFileLocation = (String) properties.get(KNOWN_HOSTS_FILE_LOCATION_PROPERTY);
    }

    /**
     * getLog(this) error and fall back to default port.
     * 
     * @param serverPortProperty
     *            the server port property
     */
    private void logErrorAndFallBackToDefaultPort(final String serverPortProperty) {
        getLog(this).error("Illegal value for server port - should be non-negative integer, was " + serverPortProperty);
        getLog(this).error("Falling back to default port " + DEFAULT_SERVER_SFTP_PORT);
        this.serverPort = DEFAULT_SERVER_SFTP_PORT;
    }

    /**
     * Contains no valid value for.
     * 
     * @param properties
     *            the properties
     * @param propertyKey
     *            the property key
     * @return true, if successful
     */
    @SuppressWarnings("rawtypes")
    private boolean containsNoValidValueFor(final Dictionary properties,
                                            final String propertyKey) {
        final Object propertyValue = properties.get(propertyKey);
        return !(propertyValue instanceof String) || StringUtils.isEmpty((String) propertyValue);
    }

    /*
     * (non-Javadoc)
     * @see com.gm.gssm.gmds.cq.services.export.ExportService#isConfigured()
     */
    @Override
    public final boolean isConfigured() {
        return this.configured;
    }
}
