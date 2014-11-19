/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.wp.aem.services.webwrapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public interface WebwrappedApp {
    /**
     * The Class EntryPoint represent the entryPoints of the WebWrappedApp.
     */
    class EntryPoint {
        /**
         * The Class EntryPointParamItem represent the Parameters of the Entry Point.
         */
        public static class EntryPointParamItem {
            private boolean entryPointParamMandatory;
            private String entryPointParamName;
            private String entryPointParamValue;

            /**
             * Gets the entry point param mandatory.
             * 
             * @return the entry point param mandatory
             */
            public final Boolean getEntryPointParamMandatory() {
                return this.entryPointParamMandatory;
            }

            /**
             * Gets the entry point param name.
             * 
             * @return the entry point param name
             */
            public final String getEntryPointParamName() {
                return this.entryPointParamName;
            }

            /**
             * Gets the entry point param value.
             * 
             * @return the entry point param value
             */
            public final String getEntryPointParamValue() {
                return this.entryPointParamValue;
            }

            /**
             * Sets the entry point param mandatory.
             * 
             * @param entryPointParamMandatory
             *            the new entry point param mandatory
             */
            public final void setEntryPointParamMandatory(final boolean entryPointParamMandatory) {
                this.entryPointParamMandatory = entryPointParamMandatory;
            }

            /**
             * Sets the entry point param name.
             * 
             * @param entryPointParamName
             *            the new entry point param name
             */
            public final void setEntryPointParamName(final String entryPointParamName) {
                this.entryPointParamName = entryPointParamName;
            }

            /**
             * Sets the entry point param value.
             * 
             * @param entryPointParamValue
             *            the new entry point param value
             */
            public final void setEntryPointParamValue(final String entryPointParamValue) {
                this.entryPointParamValue = entryPointParamValue;
            }
        }

        /**
         * The Class SubUrl represent the The Sub uirs of the Entry Point.
         */
        public static class SubUrl {
            private final Map<String, String> params;
            private final String separator;
            private final String separatorParamName;
            private String url;

            /**
             * Instantiates a new sub url.
             * 
             * @param url
             *            the url
             * @param separator
             *            the separator
             * @param separatorParamName
             *            the separatorParamName
             * @param params
             *            the params
             */
            public SubUrl(final String url, final String separator, final String separatorParamName, final Map<String, String> params) {
                setUrl(url);
                this.separator = separator;
                this.separatorParamName = separatorParamName;
                if (params == null) {
                    this.params = new HashMap<String, String>();
                } else {
                    this.params = params;
                }
            }

            /**
             * Adds the param.
             * 
             * @param key
             *            the key
             * @param value
             *            the value
             */
            public final void addParam(final String key, final String value) {
                this.params.put(key, value);
            }

            /**
             * Gets the url params.
             * 
             * @return the params
             */
            public final Map<String, String> getParams() {
                return this.params;
            }

            /**
             * Gets the separator.
             * 
             * @return the separator
             */
            public final String getSeparator() {
                return separator;
            }

            /**
             * Gets the separator param name.
             * 
             * @return the separatorParamName
             */
            public final String getSeparatorParamName() {
                return separatorParamName;
            }

            /**
             * Gets the url.
             * 
             * @return the url
             */
            public final String getUrl() {
                return this.url;
            }

            /**
             * Sets the url.
             * 
             * @param url
             *            the new url
             */
            public final void setUrl(final String url) {
                if (url != null) {
                    this.url = StringUtils.trimToEmpty(url);
                }
            }
        }

        private String entryPointId;
        private String entryPointName;
        private String entryPointUrlPath;
        private List<EntryPoint.EntryPointParamItem> parameters;
        private List<EntryPoint.SubUrl> subUrls;

        /**
         * Adds the sub url.
         * 
         * @param url
         *            the url
         * @param separator
         *            the separator
         * @param separatorParamName
         *            the separatorParamName
         * @param params
         *            the params
         */
        public final void addSubUrl(final String url,
                                    final String separator,
                                    final String separatorParamName,
                                    final Map<String, String> params) {
            this.subUrls.add(new SubUrl(url, separator, separatorParamName, params));
        }

        /**
         * Gets the entry point id.
         * 
         * @return the entry point id
         */
        public final String getEntryPointId() {
            return entryPointId;
        }

        /**
         * Gets the entry point name.
         * 
         * @return the entry point name
         */
        public final String getEntryPointName() {
            return entryPointName;
        }

        /**
         * Gets the entry point url path.
         * 
         * @return the entry point url path
         */
        public final String getEntryPointUrlPath() {
            return entryPointUrlPath;
        }

        /**
         * Gets the parameter list.
         * 
         * @return a list of parameters
         */
        public final List<EntryPointParamItem> getParameterList() {
            return parameters;
        }

        /**
         * Gets the parameter map.
         * 
         * @return this entry points parameters in a map.
         */
        public final Map<String, String[]> getParameterMap() {
            final Map<String, String[]> map = new HashMap<String, String[]>();
            for (EntryPointParamItem paramItem : getParameterList()) {
                if (map.containsKey(paramItem.getEntryPointParamName())) {
                    final String[] oldValues = map.get(paramItem.getEntryPointParamName());
                    final String[] allValues = new String[oldValues.length + 1];
                    System.arraycopy(oldValues, 0, allValues, 0, oldValues.length);
                    allValues[allValues.length - 1] = paramItem.getEntryPointParamValue();
                    map.put(paramItem.getEntryPointParamName(), allValues);
                } else {
                    map.put(paramItem.getEntryPointParamName(), new String[] { paramItem.getEntryPointParamValue() });
                }
            }
            return map;
        }

        /**
         * Gets the parameter value.
         * 
         * @param parameterName
         *            the parameter name
         * @return the parameter value
         */
        public final String getParameterValue(final String parameterName) {
            for (EntryPoint.EntryPointParamItem entryPointParam : this.parameters) {
                if (entryPointParam.getEntryPointParamName().equals(parameterName)) {
                    return entryPointParam.getEntryPointParamValue();
                }
            }
            return null;
        }

        /**
         * Gets the sub urls.
         * 
         * @return the sub urls
         */
        public final List<EntryPoint.SubUrl> getSubUrls() {
            return subUrls;
        }

        /**
         * Checks if is parameter mandatory.
         * 
         * @param parameterName
         *            the parameter name
         * @return true, if is parameter mandatory
         */
        public final boolean isParameterMandatory(final String parameterName) {
            for (EntryPoint.EntryPointParamItem entryPointParam : this.parameters) {
                if (entryPointParam.getEntryPointParamMandatory()) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Sets the entry point id.
         * 
         * @param entryPointId
         *            the new entry point id
         */
        public final void setEntryPointId(final String entryPointId) {
            if (entryPointId != null) {
                this.entryPointId = StringUtils.trimToEmpty(entryPointId);
            }
        }

        /**
         * Sets the entry point name.
         * 
         * @param entryPointName
         *            the new entry point name
         */
        public final void setEntryPointName(final String entryPointName) {
            this.entryPointName = entryPointName;
        }

        /**
         * Sets the entry point url path.
         * 
         * @param entryPointUrlPath
         *            the new entry point url path
         */
        public final void setEntryPointUrlPath(final String entryPointUrlPath) {
            if (entryPointUrlPath != null) {
                this.entryPointUrlPath = StringUtils.trimToEmpty(entryPointUrlPath);
            }
        }

        /**
         * Sets the parameter names.
         * 
         * @param parameterNames
         *            the new parameter names
         */
        public final void setParameterNames(final List<EntryPoint.EntryPointParamItem> parameterNames) {
            this.parameters = parameterNames;
        }

        /**
         * Sets the sub urls.
         * 
         * @param subUrls
         *            the new sub urls
         */
        public final void setSubUrls(final List<EntryPoint.SubUrl> subUrls) {
            this.subUrls = subUrls;
        }
    }

    /**
     * Gets the app id.
     * 
     * @return the application ID.
     */
    String getAppId();

    /**
     * Gets the app name.
     * 
     * @return the application name.
     */
    String getAppName();

    /**
     * Gets the entry point.
     * 
     * @param entryPointId
     *            the id of the application entry point
     * @return the application entry point.
     */
    EntryPoint getEntryPoint(String entryPointId);

    /**
     * Gets the entry points.
     * 
     * @return a list of the application entry points.
     */
    List<EntryPoint> getEntryPoints();
}
