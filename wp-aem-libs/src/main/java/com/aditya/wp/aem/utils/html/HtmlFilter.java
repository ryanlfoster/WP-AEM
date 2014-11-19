/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.utils.html;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public final class HtmlFilter {

    /**
     * The Class Builder.
     */
    public static class Builder {

        /** The optional attribute whitelist. */
        private Set<HtmlTagAttribute> attributeWhitelist;

        /** The mandatory input. */
        private final String input;

        /** The optional tag replacement table. */
        private Map<HtmlTag, HtmlTag> tagReplacementTable;

        /** The optional tag whitelist. */
        private Set<HtmlTag> tagWhitelist;

        /** The optional html replacement rules. */
        private List<HtmlReplacementRules> htmlReplacementRules;

        /**
         * Instantiates a new builder.
         * 
         * @param input
         *            the (html) input string
         */
        public Builder(final String input) {
            this.input = input;
        }

        /**
         * Adds the attribute whitelist.
         * 
         * @param attributeWhitelist
         *            the attribute whitelist
         * @return the builder
         */
        public final Builder attributeWhitelist(final Set<HtmlTagAttribute> attributeWhitelist) {
            this.attributeWhitelist = attributeWhitelist;
            return this;
        }

        /**
         * Builds the HtmlFilter.
         * 
         * @return the HtmlFilter
         */
        public final HtmlFilter build() {
            return new HtmlFilter(this);
        }

        /**
         * Adds the tag replacement table.
         * 
         * @param tagReplacementTable
         *            the tag replacement table
         * @return the builder
         */
        public final Builder tagReplacementTable(final Map<HtmlTag, HtmlTag> tagReplacementTable) {
            this.tagReplacementTable = tagReplacementTable;
            return this;
        }

        /**
         * Adds the tag whitelist.
         * 
         * @param tagWhitelist
         *            the tag whitelist
         * @return the builder
         */
        public final Builder tagWhitelist(final Set<HtmlTag> tagWhitelist) {
            this.tagWhitelist = tagWhitelist;
            return this;
        }

        /**
         * Adds the html replacement rules.
         * 
         * @param htmlReplacementRules
         *            the html replacement rules
         * @return the builder
         */
        public final Builder htmlReplacementRules(final List<HtmlReplacementRules> htmlReplacementRules) {
            this.htmlReplacementRules = htmlReplacementRules;
            return this;
        }

    }

    /**
     * This regular expression matches an html tag. The first group
     * 
     * <pre>
     * (\w+)
     * </pre>
     * 
     * matches the tag name.
     * 
     * <pre>
     * (\w|\w[\w-]*\w)(\s*=\s*(?:\&quot;.*?\&quot;|'.*?'|[&circ;'\&quot;&gt;\s]+))
     * </pre>
     * 
     * matches the attributes.
     */
    private static final String TAG_REGEX = "</?(\\w+)((\\s+(\\w|\\w[\\w-]*\\w)(\\s*=\\s*"
            + "(?:\".*?\"|'.*?'|[^'\">\\s]+))?)+\\s*|\\s*)/?>";

    private static final Pattern TAGS_PATTERN = Pattern.compile(TAG_REGEX);

    /** The optional parameter attributeWhitelist which defines the allowed (html tag) attributes in a text. */
    private final Set<HtmlTagAttribute> attributeWhitelist;

    /** The mandatory parameter input. */
    private final String input;

    /** The optional parameter tagReplacementTable which maps html tags to other tags. */
    private final Map<HtmlTag, HtmlTag> tagReplacementTable;

    /** The optional parameter tagWhitelist which defines the allowed (html) tags in a text. */
    private final Set<HtmlTag> tagWhitelist;

    /** The optional parameter htmlReplacementRules which define (html) replacements in a text. */
    private final List<HtmlReplacementRules> htmlReplacementRules;

    /**
     * Instantiates a new html filter.
     * 
     * @param builder
     *            the builder
     */
    private HtmlFilter(final Builder builder) {
        // private Constructor can only be called from Builder
        this.input = builder.input;
        this.tagWhitelist = builder.tagWhitelist;
        this.attributeWhitelist = builder.attributeWhitelist;
        this.tagReplacementTable = builder.tagReplacementTable;
        this.htmlReplacementRules = builder.htmlReplacementRules;
    }

    /**
     * Deletes all attributes which are not defined in the attrinute whitelist.
     * 
     * @param input
     *            a text (with html tags)
     * @return the filtered string
     */
    private String filterAttributes(final String input) {
        final StringBuilder out = new StringBuilder(input);
        final Matcher matcher = TAGS_PATTERN.matcher(input);
        int charsDeleted = 0;
        while (matcher.find()) {
            final String tag = matcher.group();
            final int tagStart = matcher.start();
            final String attributeRegex = "(\\w|\\w[\\w-]*\\w)(\\s*=\\s*(?:\".*?\"|'.*?'|[^'\">\\s]+))";
            final Pattern attributePattern = Pattern.compile(attributeRegex);
            final Matcher attributeMatcher = attributePattern.matcher(tag);
            while (attributeMatcher.find()) {
                final String attributeName = attributeMatcher.group(1);
                final String attribute = attributeMatcher.group();
                final int attributeStart = attributeMatcher.start();
                final int attributeEnd = attributeMatcher.end();
                if (!this.attributeWhitelist.contains(HtmlTagAttribute.fromString(attributeName))) {
                    out.delete(tagStart + attributeStart - charsDeleted, tagStart + attributeEnd - charsDeleted);
                    charsDeleted += attribute.length();
                }
            }
        }
        return out.toString();
    }

    /**
     * Deletes all tags which are not defined in the tag whitelist.
     * 
     * @param input
     *            a text (with html tags)
     * @return the filtered string
     */
    private String filterTags(final String input) {
        final StringBuilder out = new StringBuilder(input);
        final Matcher matcher = TAGS_PATTERN.matcher(input);

        int charsDeleted = 0;
        while (matcher.find()) {
            final String tagName = matcher.group(1);
            final String tag = matcher.group();
            final int tagStart = matcher.start();
            final int tagEnd = matcher.end();
            if (!this.tagWhitelist.contains(HtmlTag.fromString(tagName))) {
                out.delete(tagStart - charsDeleted, tagEnd - charsDeleted);
                charsDeleted += tag.length();
            }
        }
        return out.toString();
    }

    /**
     * Gets the attribute whitelist.
     * 
     * @return the attribute whitelist
     */
    public Set<HtmlTagAttribute> getAttributeWhitelist() {
        return this.attributeWhitelist;
    }

    /**
     * Gets the filtered output.
     * 
     * @return the filtered output
     */
    public String getFilteredOutput() {
        String output = this.input;
        if (this.htmlReplacementRules != null) {
            output = HtmlUtil.applyReplacementRules(output, this.htmlReplacementRules);
        }
        if (this.tagReplacementTable != null) {
            output = replaceTags(output);
        }
        if (this.tagWhitelist != null) {
            output = filterTags(output);
        }
        if (this.attributeWhitelist != null) {
            output = filterAttributes(output);
        }
        return output;
    }

    /**
     * Gets the input.
     * 
     * @return the input
     */
    public String getInput() {
        return this.input;
    }

    /**
     * Gets the tag replacement.
     * 
     * @return the tag replacement
     */
    public Map<HtmlTag, HtmlTag> gettagReplacement() {
        return this.tagReplacementTable;
    }

    /**
     * Gets the tag whitelist.
     * 
     * @return the tag whitelist
     */
    public Set<HtmlTag> getTagWhitelist() {
        return this.tagWhitelist;
    }

    /**
     * Replaces tags in a text (e.g. <code>b</code> by <code>strong</code>).
     * 
     * @param input
     *            a text (with html tags)
     * @return the filtered string
     */
    private String replaceTags(final String input) {
        final StringBuilder out = new StringBuilder(input);
        final Matcher matcher = TAGS_PATTERN.matcher(input);
        int charsDeleted = 0;
        while (matcher.find()) {
            final String tagName = matcher.group(1);
            final int tagNameStart = matcher.start(1);
            final int tagNameEnd = matcher.end(1);
            final HtmlTag tag = HtmlTag.fromString(tagName);
            if (this.tagReplacementTable.keySet().contains(tag)) {
                final String newTagName = this.tagReplacementTable.get(tag).toString();
                out.replace(tagNameStart - charsDeleted, tagNameEnd - charsDeleted, newTagName);
                charsDeleted = charsDeleted + tagName.length() - newTagName.length();
            }
        }
        return out.toString();
    }
}
