/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.vehicledata.data;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public enum ShoppingLinkType {
    LINK_WITH_CGI_PARAMS, // A link that has CGI parameters, like index.html?bla=blubb
    LINK_WITH_HASH_PARAMS, // A link hat has parameters after the hash-character, link index.html#bla
    LINK_WITHOUT_PARAMS; // A link that does not have any parameters
}
