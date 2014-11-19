/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.utils.diff;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceWrapper;

import com.day.cq.commons.DiffInfo;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class DiffableResource extends ResourceWrapper {

    /** the diff info. */
    private final DiffInfo diffInfo;

    /**
     * Constructor.
     * 
     * @param resource
     *            the current resource
     * @param diffInfo
     *            the diff info
     */
    public DiffableResource(final Resource resource, final DiffInfo diffInfo) {
        super(resource);
        this.diffInfo = diffInfo;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public final <AdapterType> AdapterType adaptTo(final Class<AdapterType> type) {
        if (type == DiffInfo.class) {
            return (AdapterType) this.diffInfo;
        }
        return super.adaptTo(type);
    }
}
