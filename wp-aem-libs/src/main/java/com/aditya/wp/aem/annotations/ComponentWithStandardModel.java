/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentWithStandardModel {

    /**
     * Returns whether the annotated component class has a standard flashModel field.
     * 
     * @return true, if the component has a standard flashModel field
     */
    boolean hasFlash();

    /**
     * Returns whether the annotated component class has a standard imageModel field.
     * 
     * @return true, if the component has a standard imageModel field.
     */
    boolean hasImage();

    /**
     * Returns whether the annotated component class has a standard linkModel field.
     * 
     * @return true, if the component has a standard linkModel field.
     */
    boolean hasLink();
}
