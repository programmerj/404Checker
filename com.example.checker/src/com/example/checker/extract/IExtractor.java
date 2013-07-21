/**
 * Copyright 2013 me, under Beerware license.
 */
package com.example.checker.extract;

import java.io.IOException;
import java.util.Set;

import com.example.checker.Link;

/**
 * @author markus
 * 
 */
public interface IExtractor {
	/**
	 * @param link
	 *            The address of the (remote|local) web page to connect to
	 * @param links
	 *            All links embedded in the output of the content
	 * @throws IOException
	 *             In case the web server does not respond (unavailable) or the
	 *             URL is only syntactically correct but invalid, the program
	 *             (again) just terminates.
	 */
	void extractLinks(final Link link, final Set<Link> links)
			throws IOException;

	/**
	 * @return The content type supported by this {@link IExtractor}
	 */
	String getContentType();
}
