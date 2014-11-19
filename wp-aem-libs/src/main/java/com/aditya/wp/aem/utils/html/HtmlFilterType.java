/* (c) 2014 Aditya Vennelakanti. All rights reserved.
 * This material is solely and exclusively owned by Aditya Vennelakanti and 
 * may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.utils.html;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 *
 */
public enum HtmlFilterType {

    /**
     * Adam richtxt editor filter allowing linebreaks, bold, italic, sub- and superscript.
     */
    ADAM("adam") {

        private final Set<HtmlTag> adamRetainWhitelist = new HashSet<HtmlTag>(Arrays.asList(new HtmlTag[] { HtmlTag.P, HtmlTag.BR, HtmlTag.B, HtmlTag.STRONG,
                HtmlTag.I, HtmlTag.EM, HtmlTag.SUP, HtmlTag.SUB, HtmlTag.SPAN }));
        private final Set<HtmlTagAttribute> adamRetainAttributeWhitelist = new HashSet<HtmlTagAttribute>(
                Arrays.asList(new HtmlTagAttribute[] { HtmlTagAttribute.STYLE }));

        @Override
        public Set<HtmlTagAttribute> getAttributeWhitelist() {
            return this.adamRetainAttributeWhitelist;
        }

        @Override
        public boolean getEscape() {
            return false;
        }

        @Override
        public List<HtmlReplacementRules> getReplacementRules() {
            return ADAM_REPLACEMENT_RULES;
        }

        @Override
        public Map<HtmlTag, HtmlTag> getReplacementTable() {
            return REPLACEMENT_TABLE;
        }

        @Override
        public Set<HtmlTag> getTagWhitelist() {
            return super.getTagWhitelist(this.adamRetainWhitelist);
        }
    },

    /**
     * Adam plus richtxt editor filter allowing linebreaks, bold, italic, sub- and superscript and
     * links.
     */
    ADAM_PLUS("adamplus") {

        private final Set<HtmlTag> adamPlusRetainWhitelist = new HashSet<HtmlTag>(Arrays.asList(new HtmlTag[] { HtmlTag.P, HtmlTag.BR, HtmlTag.B,
                HtmlTag.STRONG, HtmlTag.I, HtmlTag.EM, HtmlTag.SUP, HtmlTag.SUB, HtmlTag.A, HtmlTag.SPAN }));

        /**
         * {@inheritDoc} Returning null means allow all attributes on a tag.
         */
        @Override
        public Set<HtmlTagAttribute> getAttributeWhitelist() {
            return null;
        }

        @Override
        public boolean getEscape() {
            return false;
        }

        @Override
        public List<HtmlReplacementRules> getReplacementRules() {
            return ADAM_REPLACEMENT_RULES;
        }

        @Override
        public Map<HtmlTag, HtmlTag> getReplacementTable() {
            return REPLACEMENT_TABLE;
        }

        @Override
        public Set<HtmlTag> getTagWhitelist() {
            return super.getTagWhitelist(this.adamPlusRetainWhitelist);
        }
    },

    /**
     * Eve/Eve+ richtxt editor filter allowing linebreaks, bold, italic, sub- and superscript,
     * justify, links, lists and indent. Additionally Eve+ allows options links. So Eve+ is
     * basically the same as Eve it just adds an additional attribute to the link if options link
     * plugin is used.
     */
    EVE("eve") {

        /**
         * {@inheritDoc} Returning null means allow all attributes on a tag.
         */
        @Override
        public Set<HtmlTagAttribute> getAttributeWhitelist() {
            return null;
        }

        @Override
        public boolean getEscape() {
            return false;
        }

        /**
         * {@inheritDoc} Since Eve/Eve+ are 'real' richtxt editors with justify, indent etc. we
         * don't replace anything.
         */
        @Override
        public List<HtmlReplacementRules> getReplacementRules() {
            return null;
        }

        @Override
        public Map<HtmlTag, HtmlTag> getReplacementTable() {
            return REPLACEMENT_TABLE;
        }

        @Override
        public Set<HtmlTag> getTagWhitelist() {
            return TAG_WHITELIST;
        }
    },

    STRIP_ALL("strip") {

        private final List<HtmlReplacementRules> stripAllRetainRules = Arrays.asList(new HtmlReplacementRules[] { HtmlReplacementRules.BR_BEFORE_CLOSING_P,
                HtmlReplacementRules.BR_AFTER_OPENING_P, HtmlReplacementRules.EMPTY_P, HtmlReplacementRules.ALL_LEADING_BR,
                HtmlReplacementRules.ALL_TRAILING_BR, HtmlReplacementRules.ALL_OPENING_SPAN, HtmlReplacementRules.ALL_CLOSING_SPAN });

        @Override
        public final Set<HtmlTagAttribute> getAttributeWhitelist() {
            return null;
        }

        @Override
        public final boolean getEscape() {
            return false;
        }

        @Override
        public final List<HtmlReplacementRules> getReplacementRules() {
            return super.getReplacementRules(this.stripAllRetainRules);
        }

        @Override
        public final Map<HtmlTag, HtmlTag> getReplacementTable() {
            return null;
        }

        @Override
        public final Set<HtmlTag> getTagWhitelist() {
            return Collections.emptySet();
        }
    },

    DISCLAIMER("disclaimer") {

        private final List<HtmlReplacementRules> disclaimerRetainRules = Arrays.asList(new HtmlReplacementRules[] { HtmlReplacementRules.EMPTY_P,
                HtmlReplacementRules.CLOSING_P_WITH_BR, HtmlReplacementRules.OPENING_P_WITH_EMPTY, HtmlReplacementRules.ALL_LEADING_BR,
                HtmlReplacementRules.ALL_TRAILING_BR });
        private final Set<HtmlTag> disclaimerRetainWhitelist = new HashSet<HtmlTag>(Arrays.asList(new HtmlTag[] { HtmlTag.SUB, HtmlTag.EM, HtmlTag.I,
                HtmlTag.B, HtmlTag.A, HtmlTag.SUP, HtmlTag.STRONG, HtmlTag.SPAN }));

        @Override
        public final Set<HtmlTagAttribute> getAttributeWhitelist() {
            return null;
        }

        @Override
        public final boolean getEscape() {
            return false;
        }

        @Override
        public final List<HtmlReplacementRules> getReplacementRules() {
            return super.getReplacementRules(this.disclaimerRetainRules);
        }

        @Override
        public final Map<HtmlTag, HtmlTag> getReplacementTable() {
            return REPLACEMENT_TABLE;
        }

        @Override
        public final Set<HtmlTag> getTagWhitelist() {
            return super.getTagWhitelist(this.disclaimerRetainWhitelist);
        }
    },

    /**
     * Price richtxt editor filter is actually a 'adamplus' allowing linebreaks, bold, italic, sub-
     * and superscript, links and additionally the script tag due to prices can be written out via
     * javascript (see PriceTag/PriceFragment class).
     */
    PRICE("price") {

        private final Set<HtmlTag> priceRetainWhitelist = new HashSet<HtmlTag>(Arrays.asList(new HtmlTag[] { HtmlTag.A, HtmlTag.SUP, HtmlTag.SUB,
                HtmlTag.STRONG, HtmlTag.EM, HtmlTag.I, HtmlTag.BR, HtmlTag.P, HtmlTag.B, HtmlTag.SCRIPT, HtmlTag.SPAN }));

        @Override
        public final Set<HtmlTagAttribute> getAttributeWhitelist() {
            return null;
        }

        @Override
        public final boolean getEscape() {
            return false;
        }

        @Override
        public final List<HtmlReplacementRules> getReplacementRules() {
            return ADAM_REPLACEMENT_RULES;
        }

        @Override
        public final Map<HtmlTag, HtmlTag> getReplacementTable() {
            return REPLACEMENT_TABLE;
        }

        @Override
        public final Set<HtmlTag> getTagWhitelist() {
            return super.getTagWhitelist(this.priceRetainWhitelist);
        }
    };

    private static final List<HtmlReplacementRules> REPLACEMENT_RULES = new ArrayList<HtmlReplacementRules>();
    private static final Map<HtmlTag, HtmlTag> REPLACEMENT_TABLE = new HashMap<HtmlTag, HtmlTag>();
    private static final Set<HtmlTag> TAG_WHITELIST = new HashSet<HtmlTag>();
    private static final List<HtmlReplacementRules> ADAM_REPLACEMENT_RULES = new ArrayList<HtmlReplacementRules>();

    static {
        REPLACEMENT_RULES.addAll(EnumSet.allOf(HtmlReplacementRules.class));
        ADAM_REPLACEMENT_RULES.addAll(Arrays.asList(new HtmlReplacementRules[] { HtmlReplacementRules.BR_BEFORE_CLOSING_P,
                HtmlReplacementRules.EMPTY_P_WITH_BR, HtmlReplacementRules.CLOSING_P_WITH_BR, HtmlReplacementRules.OPENING_P_WITH_EMPTY,
                HtmlReplacementRules.TRAILING_BR, HtmlReplacementRules.EMPTY_A }));
        REPLACEMENT_TABLE.put(HtmlTag.I, HtmlTag.EM);
        REPLACEMENT_TABLE.put(HtmlTag.B, HtmlTag.STRONG);
        TAG_WHITELIST.addAll(EnumSet.allOf(HtmlTag.class));
    };

    /**
     * Lazy init.
     */
    private static final class Holder {

        static final Map<String, HtmlFilterType> TYPES = new HashMap<String, HtmlFilterType>();
        static {
            for (HtmlFilterType hft : values()) {
                TYPES.put(hft.getType(), hft);
            }
        }

        /**
         * Constructor.
         */
        private Holder() {

        }
    }

    /**
     * This method gets the <code>HtmlFilterType</code> by type.
     * 
     * @param type
     *            the type
     * @return the <code>HtmlFilterType</code> or <code>null</code>, if type not found or is null
     */
    public static final HtmlFilterType fromString(final String type) {
        return Holder.TYPES.get(type);
    }

    private String type;

    /**
     * Private constructor.
     * 
     * @param type
     *            the html filtr type
     */
    private HtmlFilterType(final String type) {
        this.type = type;
    }

    /**
     * Returns the tag whitelist by first adding all defined tags in this class and than only retain
     * all that have been passed.
     * 
     * @param retainWhitelist
     *            the set of tags to retain
     * @return tag whitelist
     */
    private Set<HtmlTag> getTagWhitelist(final Set<HtmlTag> retainWhitelist) {
        final Set<HtmlTag> wl = new HashSet<HtmlTag>(TAG_WHITELIST);
        wl.retainAll(retainWhitelist);

        return wl;
    }

    /**
     * Returns the html replacement rules by first adding all defined replacement rules in this
     * class and than only retain all that have been passed.
     * 
     * @param retainRules
     *            the list of html replacement rules to retain
     * @return html replacement rules
     */
    private List<HtmlReplacementRules> getReplacementRules(final List<HtmlReplacementRules> retainRules) {
        final List<HtmlReplacementRules> rr = new ArrayList<HtmlReplacementRules>(REPLACEMENT_RULES);
        rr.retainAll(retainRules);

        return rr;
    }

    /**
     * Returns a set of whitelist html tag attributes.
     * 
     * @return the <code>Set</code> of whitelist html tag attributes
     */
    public abstract Set<HtmlTagAttribute> getAttributeWhitelist();

    /**
     * Returns whether escape content.
     * 
     * @return the escape status
     */
    public abstract boolean getEscape();

    /**
     * Returns a list of html replacement rules.
     * 
     * @return the <code>List</code> of <code>HtmlReplacementRules</code>
     */
    public abstract List<HtmlReplacementRules> getReplacementRules();

    /**
     * Returns a map of replacement html tags.
     * 
     * @return the <code>Map</code> of replacement html tags
     */
    public abstract Map<HtmlTag, HtmlTag> getReplacementTable();

    /**
     * Returns a set of whitelist html tags.
     * 
     * @return the <code>Set</code> of whitelist html tags
     */
    public abstract Set<HtmlTag> getTagWhitelist();

    /**
     * Returns the filter type.
     * 
     * @return the html filter type
     */
    public final String getType() {
        return this.type;
    }
}
