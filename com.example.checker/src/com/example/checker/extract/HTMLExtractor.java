/**
 * Copyright 2013 me, under Beerware license.
 */
package com.example.checker.extract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author markus
 * 
 */
public class HTMLExtractor implements IExtractor {

	private static final Logger LOGGER = Logger.getLogger(HTMLExtractor.class
			.getName());

	/**
	 * In HTML this commonly identifies a Link
	 */
	private static final String PATTERN = "<a href=\"http://";
	/**
	 * In HTML this identifies a link iff it is preceded by
	 * {@link HTMLExtractor#Pattern}
	 */
	private static final String PATTERN_END = "\"";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.checker.extract.IExtractor#extractLinks(java.net.URL,
	 * java.util.Set)
	 */
	public void extractLinks(final URL url, final Set<URL> links)
			throws IOException {
		int i = 0;
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				url.openStream()));
		String line = "";
		while ((line = reader.readLine()) != null) {
			// Process each line to extract a URL out if:
			// a) "<a href="http://...>" identifies a URL
			// TODO What about line breaks?
			final String lowerCase = line.toLowerCase();
			final int idx = lowerCase.indexOf(PATTERN);
			if (idx > -1) {
				// Found a line that contains a string containing PATTERN

				// Chop off the first part of the the string up to and
				// including PATTERN
				final String substring = lowerCase.substring(idx
						+ PATTERN.length() - 7); // FIXME hacked length to
													// account for http:// in
													// pattern
				// Try finding the end of the URL in the substring
				final int endIdx = substring.indexOf(PATTERN_END);
				if (endIdx > -1) {
					// cut out the substring that _is_ the url
					final String link = substring.substring(0, endIdx).trim();
					links.add(new URL(link));
					LOGGER.fine(String.format(
							"Processed %d th element %s and added to set", i++,
							link));
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.checker.extract.IExtractor#getContentType()
	 */
	@Override
	public String getContentType() {
		return "text/html; charset=UTF-8";
	}
}
