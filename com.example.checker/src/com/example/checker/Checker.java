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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

	/**
	 * In HTML this commonly identifies a Link
	 */
	private static final String PATTERN = "<a href=\"";
	/**
	 * In HTML this identifies a link iff it is preceeded by {@link Pattern}
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
		final List<String> links = new ArrayList<String>();

		// Open a connection to the (remote) web server and "read" the page line
		// by line. In case the web server does not respond (unavailable) or the
		// URL is only syntactically correct but invalid, the program (again)
		// just terminates.
		try {
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
							+ PATTERN.length());
					// Try finding the end of the URL in the substring
					final int endIdx = substring.indexOf(PATTERN_END);
					if (endIdx > -1) {
						// cut out the substring that _is_ the url
						final String link = substring.substring(0, endIdx)
								.trim();
						links.add(link);
					}
				}
			}

			// links has been instantiated with length 100, but it might
			// contains less elements. Arrays.sort does not tolerate null
			// elements, thus we copy the subset from links into copyof to
			// remove null elements.
			Collections.sort(links);
			// Loop over the (now sorted) list of urls and print them to stdout
			for (String string2 : links) {
				System.out.println(string2);
			}

		} catch (UnknownHostException e) {
			System.err.println(String.format(
					"The given url %s points to an unknown host", url));
			System.exit(1);
		} catch (IOException e) {
			System.err.println(String.format("Failed to connect to %s", url));
			System.exit(1);
		}
	}
}
