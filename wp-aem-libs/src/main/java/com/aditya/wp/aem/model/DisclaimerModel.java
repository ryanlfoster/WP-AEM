/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.model;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletRequest;

import org.apache.commons.lang.StringUtils;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public class DisclaimerModel {

    /** The Constant GENERAL_DISCLAIMER. */
    public static final String GENERAL_DISCLAIMER = "General Disclaimer";

    /** The Constant BODYSTYLE_DISCLAIMER. */
    public static final String BODYSTYLE_DISCLAIMER = "Bodystyle Disclaimer";

    /** The Constant PAGE_DISCLAIMER. */
    public static final String PAGE_DISCLAIMER = "Page Disclaimer";

    public static final String ABSTRACT_DISCLAIMER = "Disclaimer";

    public static final String PARENTHESES_RENDERING = "parentheses";

    public static final String SUPERSCRIPT_RENDERING = "superscript";

    public static final String SUPERSCRIPT_AND_PARANTHESES_RENDERING = "superscript_and_parentheses";

    public static final String PLAIN_RENDERING = "plain";

    public static final String REFERENCED_DISCLAIMERS_ID = "referencedDisclaimers";

    /** The label. */
    private final String label;

    /** The text. */
    private final String text;

    /** The type. */
    private final String type;

    /** The id. */
    private final String id;

    /** The id. */
    private final String renderingType;

    private final String pageReference;

    /**
     * Instantiates a new disclaimer model. `
     * 
     * @param label
     *            the label
     * @param text
     *            the text
     * @param type
     *            the type
     * @param id
     *            the id
     * @param pageReference
     *            the page reference
     * @param renderingType
     *            the rendering type
     */
    public DisclaimerModel(final String label, final String text, final String type, final String id,
            final String pageReference, final String renderingType) {
        super();
        this.label = label;
        this.text = text;
        this.type = type;
        this.id = id;
        this.pageReference = pageReference;
        this.renderingType = renderingType;
    }

    /**
     * Gets the label.
     * 
     * @return the label
     */
    public final String getLabel() {
        return this.label;
    }

    /**
     * Gets the text.
     * 
     * @return the text
     */
    public final String getText() {
        return this.text;
    }

    /**
     * Gets the type.
     * 
     * @return the type
     */
    public final String getType() {
        return this.type;
    }

    /**
     * Gets the id.
     * 
     * @return the id
     */
    public final String getId() {
        return this.id;
    }

    /**
     * Gets the author info to display where to edit the given disclaimer.
     * 
     * @return the author info
     */
    public final String getAuthorInfo() {
        String result = "";
        if (this.type.equals(GENERAL_DISCLAIMER)) {
            result = "(Edit on LSLR)";
        } else if (this.type.equals(BODYSTYLE_DISCLAIMER)) {
            result = "(Edit on BBC Bodystyle)";
        }
        return result;
    }

    public final String getFormattedPageReference() {
        String result = this.pageReference;
        if (this.renderingType.equals(SUPERSCRIPT_AND_PARANTHESES_RENDERING)) {
            result = String.format("<sup>(%s)</sup>", this.pageReference);
        } else if (this.renderingType.equals(PARENTHESES_RENDERING)) {
            result = String.format("(%s)", this.pageReference);
        } else if (this.renderingType.equals(SUPERSCRIPT_RENDERING)) {
            result = String.format("<sup>%s</sup>", this.pageReference);
        }
        return result;
    }

    /**
     * Returns the currently used disclaimers.
     * 
     * @param request
     *            current request
     * @return a (possible empty) Set of disclaimer IDs
     */
    @SuppressWarnings("unchecked")
    public static Set<String> getReferencesDisclaimerIDs(final ServletRequest request) {
        final Object referencedDisclaimerIDs = request.getAttribute(DisclaimerModel.REFERENCED_DISCLAIMERS_ID);
        if (referencedDisclaimerIDs instanceof Set) {
            return (Set<String>) referencedDisclaimerIDs;
        } else {
            return new HashSet<String>();
        }
    }

    /**
     * Adds a new disclaimer ID to the list of references disclaimers. If the ID already exists, it will not be added
     * twice.
     * 
     * @param request
     *            current request
     * @param id
     *            disclaimer ID to add
     */
    public static void addReferencedDisclaimerId(final ServletRequest request,
                                                 final String id) {
        final Set<String> referencedDisclaimerIDs = getReferencesDisclaimerIDs(request);
        referencedDisclaimerIDs.add(id);
        request.setAttribute(DisclaimerModel.REFERENCED_DISCLAIMERS_ID, referencedDisclaimerIDs);
    }

    /**
     * Returns the escaped id.
     * 
     * @return the escaped id
     */
    public String getEscapedId() {
        return escapeDisclaimerID(this.id);
    }

    /**
     * Escapes the given ID. First / gets removed and all remaining slashes get replaced by "_".
     * 
     * @param id
     * @return
     */
    public static String escapeDisclaimerID(final String id) {
        if (StringUtils.isNotBlank(id)) {
            return StringUtils.replaceChars(StringUtils.replaceOnce(id, "/", StringUtils.EMPTY), '/', '_').replace(":",
                    "-");
        } else {
            return StringUtils.EMPTY;
        }
    }
}