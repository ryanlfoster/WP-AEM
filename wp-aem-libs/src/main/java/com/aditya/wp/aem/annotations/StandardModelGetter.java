/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StandardModelGetter {

    /**
     * Enumeration of model types.
     */
    public enum ModelType {
        FLASH, IMAGE, LINK, VIDEO
    };

    /**
     * Returns the model type.
     * 
     * @return the model type
     */
    ModelType type();
}
