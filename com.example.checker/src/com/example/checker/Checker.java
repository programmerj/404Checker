/**
 * Copyright 2013 me, under Beerware license.
 */
package com.example.checker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author markus
 * 
 *         A Checker's purpose is to take a base/root link (URL) and from there
 *         find all outgoing links on the web page. Each outgoing link will be
 *         checked and if the check results in an http 404 marked as broken.
 * 
 */
public class Checker {

	private static final Logger LOGGER = Logger.getLogger(Checker.class
			.getName());

	/**
	 * In HTML this commonly identifies a Link
	 */
	private static final String PATTERN = "<a href=\"http://";
	/**
	 * In HTML this identifies a link iff it is preceded by {@link Pattern}
	 */
	private static final String PATTERN_END = "\"";

	/**
	 * @param args
	 *            The first element of the string parameters is passed on and
	 *            subsequently treated as a url.
	 */
	public static void main(String[] args) {
		new Checker().check(args[0]);
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
			extractLinks(url, links);
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

	/**
	 * @param url
	 *            The address of the (remote|local) web page to connect to
	 * @param links
	 *            All links embedded in the output of the content
	 * @throws IOException
	 *             In case the web server does not respond (unavailable) or the
	 *             URL is only syntactically correct but invalid, the program
	 *             (again) just terminates.
	 */
	private void extractLinks(final URL url, final Set<String> links)
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
					links.add(link);
					LOGGER.fine(String.format(
							"Processed %d th element %s and added to set", i++,
							link));
				}
			}
		}
	}
}
