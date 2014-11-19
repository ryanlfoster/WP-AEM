/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.ValueMap;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public enum FlashParameter {
    ALIGN("align", "flashParamAlign"), //
    ALLOWFULLSCREEN("allowfullscreen", "flashParamAllowfullscreen"), //
    ALLOWNETWORKING("allownetworking", "flashParamAllownetworking"), //
    ALLOWSCRIPTACCESS("allowscriptaccess", "flashParamAllowScriptAccess"), //
    BASE("base", "flashParamBase"), //
    BGCOLOR("bgcolor", "flashParamBgcolor"), //
    DEVICEFONT("devicefont", "flashParamDivicefont"), //
    FLASHVARS("FlashVars", "flashMovieParams"), //
    LOOP("loop", "flashParamLoop"), //
    MENU("menu", "flashParamMenu"), //
    PARAM1("flashParamName1", "flashParamValue1", true), //
    PARAM2("flashParamName2", "flashParamValue2", true), //
    PARAM3("flashParamName3", "flashParamValue3", true), //
    PARAM4("flashParamName4", "flashParamValue4", true), //
    PARAM5("flashParamName5", "flashParamValue5", true), //
    PARAM6("flashParamName6", "flashParamValue6", true), //
    PARAM7("flashParamName7", "flashParamValue7", true), //
    PARAM8("flashParamName8", "flashParamValue8", true), //
    PLAY("play", "flashParamPlay"), //
    QUALITY("quality", "flashParamQuality"), //
    SALIGN("salign", "flashParamSalign"), //
    SCALE("scale", "flashParamScale"), //
    SEAMLESSTABBING("seamlesstabbing", "flashParamSeamlesstabbing"), //
    SWLIVECONNECT("swliveconnect", "flashParamSwliveconnect"), //
    WMODE("wmode", "flashParamWmode");

    /**
     * @param valueMap
     *            of the current valueMap
     * @return a map with all flash parameter which are in the valueMap
     */
    public static Map<String, String> getFlashParameter(final ValueMap valueMap) {
        final Map<String, String> flashParams = new HashMap<String, String>();
        for (FlashParameter pam : FlashParameter.values()) {
            String key = pam.flashParamKey;
            if (key == null) {
                key = valueMap.get(pam.dialogKey, String.class);
            }
            if (key != null) {
                String value = valueMap.get(pam.dialogKeyValue, String.class);
                if (value != null) {
                    // workaround of true/false unhandling of cq
                    if (value.equals("'true'")) {
                        value = "true";
                    }
                    if (value.equals("'false'")) {
                        value = "false";
                    }
                    flashParams.put(key, value);
                }
            }
        }
        return flashParams;
    }

    private String dialogKey;

    private String dialogKeyValue;

    private String flashParamKey;

    /**
     * constructor.
     * 
     * @param flashParamKey
     *            the name of the flash parameter
     * @param dialogKeyValue
     *            the crx storage key of the flash parameter
     */
    private FlashParameter(final String flashParamKey, final String dialogKeyValue) {
        this.flashParamKey = flashParamKey;
        this.dialogKeyValue = dialogKeyValue;
    }

    /**
     * constructor.
     * 
     * @param dialogKey
     *            the crx storage key of the name of the flash parameter
     * @param dialogKeyValue
     *            the crx storage key of the flash parameter
     * @param b
     *            unused parameter, only for construtor overloading
     */
    private FlashParameter(final String dialogKey, final String dialogKeyValue, final boolean b) {
        this.dialogKey = dialogKey;
        this.dialogKeyValue = dialogKeyValue;
    }

    /**
     * @return name of the flash parameter
     */
    public String key() {
        return this.flashParamKey;
    }
}
