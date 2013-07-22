/**
 * Copyright 2013 me, under Beerware license.
 */
package com.example.checker;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

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

	private static final int NUM_THREADS = 4;

	private int threshold = Integer.getInteger(getClass().getName()
			+ ".threshold", 2);

	/**
	 * The depth the checker has reached up until now during dead link
	 * detection. Inconsistent reads are acceptable here as it is just used for
	 * reporting (we can tolerate to report a slightly incorrect level).
	 */
	// XXX What about volatile here?
	private int maxDepth = 0;

	/**
	 * @param args
	 *            The first element of the string parameters is passed on and
	 *            subsequently treated as a url.
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		new Checker().check(args[0]);
	}

	private final Map<String, IExtractor> extractors;
	/**
	 * A set of vertices/nodes in the cyclic graph (what the world wide web is)
	 * to essentially remove cycles and create a directed Acyclic graph (DAG). A
	 * DAG is much easier to traverse and thus handle.
	 */
	private final Set<Link> seen = new HashSet<Link>();
	/**
	 * Set of dead links found during link checking
	 */
	private final Set<Link> dead = new TreeSet<Link>(new Comparator<Link>() {

		@Override
		public int compare(Link o1, Link o2) {
			return o2.toString().compareTo(o1.toString());
		}
	});
	/**
	 * Content types discovered by the dead link detection for which there is no
	 * IExtractor registered.
	 */
	private final Set<String> unknownContentType = new TreeSet<String>();
	/**
	 * The domain for which this dead link checker is responsible for. It causes
	 * us to not follow outgoing links. E.g. no point in moving to a search
	 * result page.
	 */
	private String authority;

	private final ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors
			.newFixedThreadPool(NUM_THREADS);

	/**
	 * Count work (links) not yet explored. Initialized with 1 to prevent main
	 * thread from exiting immediately after spawning the first worker thread
	 * (before it even had a chance to extract the first set of links) and the
	 * final decrement to 0 in {@link Checker#check(Set)}
	 */
	private AtomicInteger undone = new AtomicInteger(1);

	public Checker() {
		extractors = new HashMap<String, IExtractor>();

		// Add built-in extractors
		IExtractor extractor = new HTMLRegexExtractor();
		extractors.put(extractor.getContentType(), extractor);
		extractor = new XMLExtractor();
		extractors.put(extractor.getContentType(), extractor);
		extractor = new PlaintextRegexExtractor();
		extractors.put(extractor.getContentType(), extractor);
	}

	/**
	 * @param extractors2
	 */
	public Checker(Map<String, IExtractor> extractors2, int threshold) {
		this.extractors = extractors2;
		this.threshold = threshold;
	}

	/**
	 * @param string
	 *            The base url where to start from
	 * @throws InterruptedException
	 */
	private void check(final String string) throws InterruptedException {
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

		this.authority = url.getAuthority();

		System.out.println(String.format("Going to start checker on url: %s",
				url.toString()));

		final Set<Link> urls = new HashSet<Link>();
		urls.add(new URLLink(url));

		/* initial configuration done */

		// Create a worker thread that forks of from the main thread to do link
		// detection. The main thread will move on to the next loop and report
		// progress every five seconds.
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				check(urls);
			}
		});

		// Check if any work (links) are left undone.
		while (undone.get() > 0) {
			System.out
					.println(String
							.format("Processed %d pages at depth %d, found %d dead links so far",
									seen.size(), maxDepth, dead.size()));
			Thread.sleep(5000);
		}

		// Need to shut down the thread pool explicitly. Otherwise the VM never
		// terminates and leaves the threads open.
		threadPool.shutdown();

		/* final reportig below here */

		// Loop over the (now sorted) list of urls and print them to stdout
		System.out.println(String.format("==== Scanned %d pages ====",
				seen.size()));
		for (String contentType : unknownContentType) {
			System.out.println(String
					.format("No IExtractor registered for %s content type",
							contentType));
		}
		System.out.println("==== Dead links ====");
		for (Link string2 : dead) {
			System.out.println(string2);
		}
		System.out.println("==== Done ====");
	}

	Set<Link> check(final Set<Link> urls) {
		check(urls, dead, 0);
		undone.decrementAndGet();
		return dead;
	}

	private void check(final Set<Link> urls, final Set<Link> dead, int level) {
		// Example of ternary operator (syntactical sugar for if/else)
		// Logically it increases maxDepth if the current level is deeper
		maxDepth = (maxDepth < level) ? level : maxDepth;

		if (level++ > threshold) {
			undone.addAndGet(urls.size() * -1);
			return;
		}
		for (Link url : urls) {

			// links is used to store all extracted urls from the web page
			// A TreeSet implements SortedSet and thus it automagically sorts
			// the
			// elements inserted.
			final Set<Link> links = new HashSet<Link>();

			try {
				// Websites logically are equivalent to a _cyclic_ graph, thus a
				// list of seen vertices is used to turn it into a DAG.
				// Using the add op has the nice advantage that the set operation
				// becomes atomic (see putIfAbsent).
				if (!this.seen.add(url)) {
					continue;
				}
				try {
					// Each content type needs a specific Extractor. E.g. html uses
					// a
					// line based pattern matching looking for hrefs, whereas an xml
					// file could navigate the xml structure to extract links
					final String contentType = url.getContentType();
					if (contentType == null) {
						// 404
						dead.add(url);
					} else if (!url.isSameAuthority(authority)) {
						// (Partially redundant)
						// if we get here we know _without_ parsing the web page
						// that it is alive (because we managed to determine the
						// content type) and thus can skip it (no need to extract
						// its page links)
						continue;
					} else if (extractors.containsKey(contentType)) {
						IExtractor iExtractor = extractors.get(contentType);
						iExtractor.extractLinks(url, links);
						// Here is a slight chance of early termination
						// when
						// the main thread checks undone immediately
						// extractLinks(..) returns but before undone is
						// incremented. This is accounted for by 
						// decrementing undone in try catch
						undone.addAndGet(links.size());
					} else {
						unknownContentType.add(contentType);
						continue;
					}
				} catch (IOException | IllegalArgumentException e) {
					// 404 (if https connection fails when not connected to
					// internet)
					dead.add(url);
				}
				// Do not follow out-bound links from the page where we started on.
				// E.g. don't check google for 404s. Still check if the link itself
				// is correct
				if (!url.isSameAuthority(authority)) {
					continue;
				}
			} finally {
				// Decrement has to happen after new work have been added to the
				// work list. Otherwise we terminate too early.
				undone.decrementAndGet();
			}
			// recurse into

			// If threads are available in the pool, use one to recurse into the
			// list. If no thread is available, use the current one.
			// This reassembles depth-first search as we don't half the list
			// first and spawn two new Runnables on the same level.
			if (threadPool.getActiveCount() < NUM_THREADS) {
				final int foo = level;
				threadPool.execute(new Runnable() {
					@Override
					public void run() {
						check(links, dead, foo);
					}
				});
			} else {
				check(links, dead, level);
			}
		}
	}

	/**
	 * @return
	 */
	public Set<Link> getSeen() {
		return this.seen;
	}
}
