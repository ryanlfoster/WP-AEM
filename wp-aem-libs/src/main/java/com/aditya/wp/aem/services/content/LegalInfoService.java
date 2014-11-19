/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.services.content;

import java.util.List;
import java.util.Set;

import com.aditya.gmwp.aem.model.DisclaimerModel;
import com.aditya.gmwp.aem.wrapper.GMResource;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface LegalInfoService {
    /**
     * The Class ReferenceCounter.
     */
    static class Counter {
        /** The numeric ref. */
        private int numericRef;

        /** The alphabetic ref. */
        private char alphabeticRef;

        /** Is numeric or alphabetic ?. */
        private boolean numeric;

        /**
         * Instantiates a new ref counter.
         * 
         * @param startValue
         *            the start value
         */
        public Counter(final String startValue) {
            super();

            try {
                this.numericRef = Integer.valueOf(startValue).intValue();
                this.numeric = true;
            } catch (final Exception e) {
                this.numeric = false;
            }

            if (!this.numeric) {
                this.alphabeticRef = startValue.charAt(0);
            }
        }

        /**
         * Gets the value.
         * 
         * @return the value
         */
        public String getValue() {
        	return (this.numeric ? String.valueOf(this.numericRef) : String.valueOf(this.alphabeticRef));
        }

        /**
         * Increase.
         */
        public void increase() {
            if (this.numeric) {
                this.numericRef++;
            } else {
                this.alphabeticRef++;
            }
        }

        /**
         * Next. Will give the current value back and increase the count.
         * 
         * @return the string
         */
        public String next() {
            final String current = getValue();
            increase();
            return current;
        }
    }

	/**
     * Find all disclaimers for resource.
     * 
     * @param currentPath
     *            the current path
     * @return the list
     */
	List<DisclaimerModel> findAllPotentialDisclaimersForRessource(final String currentPath);

	/**
     * Checks if is enable layers.
     * 
     * @param path
     *            the path
     * @return true, if is enable layers
     */
	boolean isEnableLayers(final String path);

	/**
     * Find all referenced disclaimers for resource.
     * 
     * @param disclaimerIds
     *            the disclaimer ids
     * @param currentRes
     *            the current res
     * @return the list
     */
	List<DisclaimerModel> createDisclaimersByIdsForRessource(final Set<String> disclaimerIds, final GMResource currentRes);
}
