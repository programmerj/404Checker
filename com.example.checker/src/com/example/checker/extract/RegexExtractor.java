/**
 * Copyright 2013 me, under Beerware license.
 */
package com.example.checker.extract;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

/**
 * @author markus
 * 
 */
public class RegexExtractor implements IExtractor {

	// package private to allow access to unit tests
	static final String regex = ".*((https?|ftp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]).*";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.checker.extract.IExtractor#extractLinks(java.net.URL,
	 * java.util.Set)
	 */
	@Override
	public void extractLinks(URL url, Set<URL> links) throws IOException {
		// TODO implement
		throw new UnsupportedOperationException("not yet implemented");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.checker.extract.IExtractor#getContentType()
	 */
	@Override
	public String getContentType() {
		// TODO implement
		return null;
	}
}
