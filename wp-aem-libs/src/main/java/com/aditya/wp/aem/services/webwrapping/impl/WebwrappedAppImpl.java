/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.webwrapping.impl;

import java.util.List;
import java.util.ArrayList;
import com.aditya.wp.aem.services.webwrapping.WebwrappedApp;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class WebwrappedAppImpl implements WebwrappedApp {
    private String appId;

    private String appName;

    private String entryPoint;

    private List<EntryPoint> entryPoints;

    /**
     * Instantiates a new web wrapped app impl.
     */
    public WebwrappedAppImpl() {
        this.entryPoints = new ArrayList<WebwrappedApp.EntryPoint>();
    }

    /**
     * Instantiates a new web wrapped app impl.
     * 
     * @param appId
     *            the app id
     */
    public WebwrappedAppImpl(final String appId) {
        this.entryPoints = new ArrayList<WebwrappedApp.EntryPoint>();
        this.appId = appId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getAppId() {
        return this.appId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getAppName() {
        return this.appName;
    }

    /**
     * {@inheritDoc}
     */
    public final String getEntryPoint() {
        return this.entryPoint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final EntryPoint getEntryPoint(final String entryPointId) {
        for (final WebwrappedApp.EntryPoint appEntryPoint : this.entryPoints) {
            if (appEntryPoint.getEntryPointId().equals(entryPointId)) {
                return appEntryPoint;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<WebwrappedApp.EntryPoint> getEntryPoints() {
        return this.entryPoints;
    }

    /**
     * @param appId
     *            the new app id
     */
    public final void setAppId(final String appId) {
        this.appId = appId;
    }

    /**
     * @param appName
     *            the new app name
     */
    public final void setAppName(final String appName) {
        this.appName = appName;
    }

    /**
     * @param entryPoint
     *            the new entry point
     */
    public final void setEntryPoint(final String entryPoint) {
        this.entryPoint = entryPoint;
    }

    /**
     * @param entryPoints
     *            the new entry points
     */
    public final void setEntryPoints(final List<WebwrappedApp.EntryPoint> entryPoints) {
        this.entryPoints = entryPoints;
    }
}
