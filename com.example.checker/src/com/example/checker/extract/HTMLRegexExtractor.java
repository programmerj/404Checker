/**
 * Copyright 2013 me, under Beerware license.
 */
package com.example.checker.extract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * @author markus
 * 
 */
public class HTMLRegexExtractor extends RegexExtractor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.checker.extract.IExtractor#extractLinks(java.net.URL,
	 * java.util.Set)
	 */
	@Override
	public void extractLinks(URL url, Set<URL> links) throws IOException {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				url.openStream()));
		String line = "";
		while ((line = reader.readLine()) != null) {
			final Matcher match = this.matcher.matcher(line);
			if (match.matches()) {
				// second group is the match, first is the full line
				links.add(new URL(match.group(1)));
			}
		}
		reader.close();
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
