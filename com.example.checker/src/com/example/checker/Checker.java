/**
 * Copyright 2013 me, under Beerware license.
 */
package com.example.checker;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.example.checker.extract.HTMLExtractor;
import com.example.checker.extract.IExtractor;
import com.example.checker.extract.XMLExtractor;

/**
 * @author markus
 * 
 *         A Checker's purpose is to take a base/root link (URL) and from there
 *         find all outgoing links on the web page. Each outgoing link will be
 *         checked and if the check results in an http 404 marked as broken.
 * 
 */
public class Checker {

	/**
	 * @param args
	 *            The first element of the string parameters is passed on and
	 *            subsequently treated as a url.
	 */
	public static void main(String[] args) {
		new Checker().check(args[0]);
	}

	private final Map<String, IExtractor> extractors;

	public Checker() {
		extractors = new HashMap<String, IExtractor>();

		// Add built-in extractors
		IExtractor extractor = new HTMLExtractor();
		extractors.put(extractor.getContentType(), extractor);
		extractor = new XMLExtractor();
		extractors.put(extractor.getContentType(), extractor);
	}

	/**
	 * @param string
	 *            The base url where to start from
	 */
	private void check(final String string) {
		// Try to convert the String parameter into a URL. This might fail and
		// thus we have to handle the MUE. In case of said exception we notify
		// the user and exit our little program right away.
		URL url = null;
		try {
			url = new URL(string);
		} catch (MalformedURLException e) {
			System.err.println("Invalid url given");
			System.exit(1);
		}

		System.out.println(String.format("Going to start checker on url: %s",
				url.toString()));

		// links is used to store all extracted urls from the web page
		// A TreeSet implements SortedSet and thus it automagically sorts the
		// elements inserted.
		final Set<String> links = new TreeSet<String>();

		try {
			// Each content type needs a specific Extractor. E.g. html uses a
			// line based pattern matching looking for hrefs, whereas an xml
			// file could navigate the xml structure to extract links
			final String contentType = url.openConnection().getContentType();
			if (extractors.containsKey(contentType)) {
				IExtractor iExtractor = extractors.get(contentType);
				iExtractor.extractLinks(url, links);
			} else {
				System.out.println(String.format(
						"No handle for %s content type", contentType));
			}
		} catch (UnknownHostException e) {
			System.err.println(String.format(
					"The given url %s points to an unknown host", url));
			System.exit(1);
		} catch (IOException e) {
			System.err.println(String.format("Failed to connect to %s", url));
			System.exit(1);
		}

		// Loop over the (now sorted) list of urls and print them to stdout
		for (String string2 : links) {
			System.out.println(string2);
		}
	}
}
