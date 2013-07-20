/**
 * Copyright 2013 me, under Beerware license.
 */
package com.example.checker;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.example.checker.extract.HTMLRegexExtractor;
import com.example.checker.extract.IExtractor;
import com.example.checker.extract.PlaintextRegexExtractor;
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

	private static final int THRESHOLD = 2;

	/**
	 * @param args
	 *            The first element of the string parameters is passed on and
	 *            subsequently treated as a url.
	 */
	public static void main(String[] args) {
		new Checker().check(args[0]);
	}

	private final Map<String, IExtractor> extractors;
	private final Comparator<? super URL> urlComparator;
	/**
	 * A set of vertices/nodes in the cyclic graph (what the world wide web is)
	 * to essentially remove cycles and create a directed Acyclic graph (DAG). A
	 * DAG is much easier to traverse and thus handle.
	 */
	private final Set<URL> seen = new HashSet<URL>();

	public Checker() {
		extractors = new HashMap<String, IExtractor>();

		// Add built-in extractors
		IExtractor extractor = new HTMLRegexExtractor();
		extractors.put(extractor.getContentType(), extractor);
		extractor = new XMLExtractor();
		extractors.put(extractor.getContentType(), extractor);
		extractor = new PlaintextRegexExtractor();
		extractors.put(extractor.getContentType(), extractor);

		this.urlComparator = new Comparator<URL>() {

			@Override
			public int compare(URL o1, URL o2) {
				return o1.toString().compareTo(o2.toString());
			}
		};
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

		final Set<URL> urls = new HashSet<URL>();
		urls.add(url);
		Set<URL> dead = check(urls);

		// Loop over the (now sorted) list of urls and print them to stdout
		System.out.println("==== Dead links ====");
		for (URL string2 : dead) {
			System.out.println(string2);
		}
		System.out.println("==== Done ====");
	}

	private Set<URL> check(final Set<URL> urls) {
		final Set<URL> dead = new HashSet<URL>();
		check(urls, dead, 0);
		return dead;
	}

	private void check(final Set<URL> urls, Set<URL> dead, int level) {
		if (level++ > THRESHOLD) {
			return;
		}
		for (URL url : urls) {
			// Websites logically are equivalent to a _cyclic_ graph, thus a
			// list of seen vertices is used to turn it into a DAG.
			// Using the add op has the nice advantage that the set operation
			// becomes atomic (see putIfAbsent).
			if (!this.seen.add(url)) {
				continue;
			}

			// links is used to store all extracted urls from the web page
			// A TreeSet implements SortedSet and thus it automagically sorts
			// the
			// elements inserted.
			final Set<URL> links = new TreeSet<URL>(urlComparator);

			try {
				// Each content type needs a specific Extractor. E.g. html uses
				// a
				// line based pattern matching looking for hrefs, whereas an xml
				// file could navigate the xml structure to extract links
				final String contentType = url.openConnection()
						.getContentType();
				if (contentType == null) {
					// 404
					dead.add(url);
				} else if (extractors.containsKey(contentType)) {
					IExtractor iExtractor = extractors.get(contentType);
					iExtractor.extractLinks(url, links);
				} else {
					System.out.println(String.format(
							"No handle for %s content type", contentType));
				}
			} catch (UnknownHostException e) {
				// 404
				dead.add(url);
			} catch (IOException e) {
				System.err.println(String
						.format("Failed to connect to %s", url));
				// FIXME exiting the the VM in case of an error does not make
				// much sense with recursion anymore!
				System.exit(1);
			}

			// recurse into
			check(links, dead, level);
		}
	}
}
