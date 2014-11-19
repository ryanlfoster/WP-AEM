/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.vehicledata.impl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aditya.wp.aem.services.vehicledata.data.impl.BodystyleImpl;
import com.aditya.wp.aem.services.vehicledata.data.impl.CarlineImpl;
import com.aditya.wp.aem.utils.StringUtil;

/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
final class XmlHttpLoader {
    private static final Logger LOG = LoggerFactory.getLogger(XmlHttpLoader.class);

    private final String baseUrl;

    private final String virtualHost;

    private static final String ATTRIBUTE_XML = "vehicle_attributes.xml";

    /**
     * The Class Constructor.
     * 
     * @param baseUrl
     *            url of DDp service
     * @param virtualHost
     *            virtual host to be used
     */
    XmlHttpLoader(final String baseUrl, final String virtualHost) {
        this.baseUrl = baseUrl;
        this.virtualHost = virtualHost;
    }

    public static String buildDDPUrl(final String baseUrl,
                                     final CarlineImpl carline,
                                     final BodystyleImpl bodystyle,
                                     final String filename) {

        final StringBuilder sb = new StringBuilder();
        if (!StringUtil.endsWith(baseUrl, '/')) {
            sb.append("/");
        }
        sb.append(carline.getModelYear());
        if (StringUtils.isNotBlank(carline.getModelYearSuffix())) {
            sb.append(carline.getModelYearSuffix());
        }
        sb.append("/");
        sb.append(carline.getCode());
        sb.append("/");
        sb.append(bodystyle.getCode());
        sb.append("/");
        sb.append(filename);
        String url = baseUrl + sb.toString();
        try {
            url = baseUrl + URIUtil.encodePath(sb.toString(), "UTF-8");
        } catch (URIException e) {
            LOG.error("Encoding of URL " + url + " failed.", e);
        }

        return url;
    }

    /**
     * Determines the url to use and then tries to load an associated attributes xml file for the
     * specified bodystyle.
     * 
     * @param carline
     *            CarlineImpl
     * @param bodystyle
     *            BodystyleImpl
     * @return the input stream
     */
    public InputStream loadAttributes(final CarlineImpl carline,
                                      final BodystyleImpl bodystyle) {

        String url = buildDDPUrl(this.baseUrl, carline, bodystyle, ATTRIBUTE_XML);
        LOG.info("Loading " + ATTRIBUTE_XML + " for " + carline.getCode() + "/" + bodystyle.getCode() + " from URL " + url);

        return get(url);
    }

    /**
     * Makes the request.
     * 
     * @param method
     *            HttpMethod
     * @return the int
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static int makeRequest(final HttpMethod method) throws IOException {
        final HttpClient c = new HttpClient();

        return c.executeMethod(method);
    }

    /**
     * Gets the input stream associated with the url.
     * 
     * @param url
     *            the url to the file
     * @return the input stream
     */
    private InputStream get(final String url) {
        final GetMethod get = new GetMethod(url);
        get.getParams().setVirtualHost(this.virtualHost);
        try {
            if (HttpStatus.SC_OK != makeRequest(get)) {
                return null;
            }
            return get.getResponseBodyAsStream();

        } catch (IOException e) {
            LOG.warn("IOException while accessing url " + url, e);
        }
        return null;
    }
}
