/**
 * Copyright 2013 me, under Beerware license.
 */
package com.example.checker.extract;

import java.util.regex.Pattern;

/**
 * @author markus
 * 
 */
public abstract class RegexExtractor implements IExtractor {

	// package private to allow access to unit tests
	static final String regex = ".*((https?|ftp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]).*";

	protected Pattern matcher;

	public RegexExtractor() {
		this.matcher = Pattern.compile(regex);
	}
}
