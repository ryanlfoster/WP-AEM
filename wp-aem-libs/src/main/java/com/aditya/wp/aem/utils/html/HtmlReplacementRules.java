/*
 * (c) 2014 Aditya Vennelakanti. All rights reserved. This material is solely and exclusively owned
 * by Aditya Vennelakanti and may not be reproduced elsewhere without prior written approval.
 */

package com.aditya.gmwp.aem.utils.html;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * @author aditya.vennelakanti
 * @since Release 1.0
 */

public enum HtmlReplacementRules {

	BR_BEFORE_CLOSING_P("<[bB][rR][^<]*>[\\r\\n\\s]*</[pP]\\s*?>", "</p>"),

	BR_AFTER_OPENING_P("<[pP][^<]*>[\\r\\n\\s]*<[bB][rR].*?>", "<p>"),

	EMPTY_P("<[pP][^<]*>[&nbsp;\\s\\n\\r]*</[pP]\\s*?>", ""),

	EMPTY_P_WITH_BR("<[pP][^<]*>[&nbsp;\\s\\r\\n]*</[pP]\\s*?>", "<br/>"),

	OPENING_P_WITH_EMPTY("<[pP].*?>", ""),

	CLOSING_P_WITH_EMPTY("</[pP]\\s*?>", ""),

	CLOSING_P_WITH_BR("(<[bB][rR].*?>)|(</[pP]\\s*?>)", "<br/>"),

	LEADING_BR("^<[bB][rR].*?>[\\r\\n\\s]*", ""),

	ALL_LEADING_BR("^(<[bB][rR].*?>[\\r\\n\\s]*)+", ""),

	TRAILING_BR("<[bB][rR][^<]*>[\\r\\n\\s]*$", ""),

	ALL_TRAILING_BR("(<[bB][rR][^<]*>[\\r\\n\\s]*)+$", ""),

	EMPTY_A("<[aA]\\s*>[&nbsp;\\s\\n\\r]*</[aA]\\s*?>", ""),

	DOUBLE_QUOTE_WITH_ENTITY("[\"]", StringEscapeUtils.escapeHtml("\"")),

	AMPERSAND_WITH_ENTITY("[&]", "&amp;"),

	LESS_THAN_WITH_ENTITY("[<]", "&lt;"),

	GREATER_THAN_WITH_ENTITY("[>]", "&gt;"),

	ALL_OPENING_SPAN("<[sS][pP][aA][nN].*?>", ""),

	ALL_CLOSING_SPAN("</[sS][pP][aA][nN]\\s*?>", "");

	/** the regex. */
	private String regex;

	/** the replacement. */
	private String replacement;

	/**
	 * Private constructor.
	 * 
	 * @param regex
	 *            the regex <code>String</code>
	 * @param replacement
	 *            the replacement <code>String</code>
	 */
	private HtmlReplacementRules(final String regex, final String replacement) {
		this.regex = regex;
		this.replacement = replacement;
	}

	/**
	 * Returns the regex.
	 * 
	 * @return the regex
	 */
	public final String getRegex() {
		return this.regex;
	}

	/**
	 * Returns the replacement.
	 * 
	 * @return the replacement
	 */
	public final String getReplacement() {
		return this.replacement;
	}
}
