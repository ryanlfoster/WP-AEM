/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.utils.ddp;

import java.util.List;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface DdpDisclaimerRequestData {

    String AUTOMATIC_NUMBERING_DEFAULT_INITIAL_VALUE = "1";

    /**
     * Adds a disclaimer.
     * 
     * @param disclaimer
     *            the disclaimer
     */
    void addDisclaimer(Disclaimer disclaimer);

    /**
     * All the disclaimers that have been added so far.
     * 
     * @return all the disclaimers that have been added so far.
     */
    List<Disclaimer> getAllDisclaimers();

    /**
     * @return whether DDP disclaimers are enabled for the current page.
     */
    boolean getDoIncludeDdpDisclaimers();

    /**
     * Returns the next automatic numbering symbol, incremented to the one that was delivered in the previous
     * invocation of the method. The value will automatically be put into parentheses if this has been enabled
     * in the pricelegallinks component.
     * 
     * @return see above.
     */
    String getNextAutomaticNumberingSymbol();

    /**
     * Whether the numbering / reference symbols for the disclaimers (and the prices that refer to the
     * disclaimers) are generated automatically or fetched from the baseball-cards.
     * 
     * @return see above
     */
    boolean getUseAutomaticNumbering();
}
