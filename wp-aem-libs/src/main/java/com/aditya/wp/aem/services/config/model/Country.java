/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.config.model;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class Country {

    /** The content path. */
    private final String contentPath;

    /** The domain. */
    private final String domain;

    /**
     * Instantiates a new country.
     * 
     * @param contentPath
     *            the content path
     * @param domain
     *            the domain
     */
    public Country(final String domain, final String contentPath) {
        this.domain = domain;
        this.contentPath = contentPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Country other = (Country) obj;
        if (this.contentPath == null) {
            if (other.contentPath != null) {
                return false;
            }
        } else if (!this.contentPath.equals(other.contentPath)) {
            return false;
        }
        if (this.domain == null) {
            if (other.domain != null) {
                return false;
            }
        } else if (!this.domain.equals(other.domain)) {
            return false;
        }
        return true;
    }

    /**
     * Gets the content path.
     * 
     * @return the content path
     */
    public final String getContentPath() {
        return this.contentPath;
    }

    /**
     * Gets the domain.
     * 
     * @return the domain
     */
    public final String getDomain() {
        return this.domain;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.contentPath == null) ? 0 : this.contentPath.hashCode());
        result = prime * result + ((this.domain == null) ? 0 : this.domain.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Country [contentPath=");
        builder.append(this.contentPath);
        builder.append(", domain=");
        builder.append(this.domain);
        builder.append("]");
        return builder.toString();
    }
}
