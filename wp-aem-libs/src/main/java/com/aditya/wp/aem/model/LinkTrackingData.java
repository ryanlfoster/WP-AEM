/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.model;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface LinkTrackingData {
    /**
     * Delivers the JavaScript code that executes tracking for the link. No line breaks should occur in the code.
     * 
     * @return JavaScript code.
     */
    String toJsCode();
}
