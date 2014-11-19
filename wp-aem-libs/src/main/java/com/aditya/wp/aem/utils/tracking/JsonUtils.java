/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.utils.tracking;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.oak.commons.json.JsonObject;

import com.aditya.gmwp.aem.services.tracking.data.OmnitureVariables;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class JsonUtils {

	/**
	 * Private constructor to prevent instantiation of this class.
	 */
	private JsonUtils() {
		throw new AssertionError("This class is not ment to be instantiated.");
	}

	public static String getAsJsString(final Map<OmnitureVariables, String> omnitureVars) {
		return createJsonFromOmnitureVars(omnitureVars).toString().replaceAll("\"", "'");
	}

	private static JsonObject createJsonFromOmnitureVars(final Map<OmnitureVariables, String> omnitureVars) {
		final JsonObject JsonObject = new JsonObject();

		if (null != omnitureVars) {
			for (Map.Entry<OmnitureVariables, String> entry : omnitureVars.entrySet()) {
				if (entry.getValue() != null) {
					JsonObject.getProperties().put(entry.getKey().getJavaScriptVariableName(), StringUtils.replace(entry.getValue(), "'", ""));
				}
			}
		}

		return JsonObject;
	}
}