/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.tracking.initializer;

import java.util.Locale;

import com.aditya.wp.aem.services.tracking.data.OmnitureVariables;
import com.aditya.wp.aem.services.tracking.util.PagePathAssembler;
import com.day.cq.wcm.api.Page;



/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class PageNameInitializer extends TrackingVarInitializer {
	private final Locale lslrLocale;

    public PageNameInitializer(final Locale lslrLocale) {
        this.lslrLocale = lslrLocale;
    }

    @Override
    protected void initialize() {
        setVariables(createPageName(getCurrentPage()), OmnitureVariables.PAGENAME);
    }

    private String createPageName(final Page currentPage) {
        return new PagePathAssembler(currentPage.getPath()).createOmniturePageName(this.lslrLocale);
    }
}
