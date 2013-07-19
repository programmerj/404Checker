/**
 * Copyright 2013 me, under Beerware license.
 */
package com.example.checker.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.example.checker.extract.HTMLExtractor;
import com.example.checker.extract.IExtractor;
import com.example.checker.extract.XMLExtractor;

/**
 * @author markus
 * 
 */
@RunWith(value = Parameterized.class)
public class IExtractorTest {

	@Parameters
	public static Collection<Object[]> data() {
		Collection<Object[]> col = new ArrayList<Object[]>();
		col.add(new Object[] { new HTMLExtractor(), "http://www.vogella.com", 5 });
		col.add(new Object[] { new XMLExtractor(),
				"http://blog.vogella.com/feed", 5 });
		return col;
	}

	private final IExtractor extractor;
	private final int expectedAmountOfLinks;
	private URL url;
	private Set<String> links;

	public IExtractorTest(IExtractor extractor, String url,
			Integer expectedAmountOfLinks) {
		this.extractor = extractor;
		this.expectedAmountOfLinks = expectedAmountOfLinks;

		// Expect url conversion to _always_ succeed
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Before
	public void setup() throws IOException {
		links = new HashSet<String>();
		extractor.extractLinks(url, links);
	}

	@Test
	public void testExtractLinksFromKnownURL() throws IOException {
		assertTrue("Missed to see some links",
				links.size() == expectedAmountOfLinks);
	}

	@Test
	public void testExtractLinksConvertToURL() throws IOException {
		for (String string : links) {
			try {
				assertTrue("Links are expected to be convertible to URL",
						new URL(string).toString().equals(string));
			} catch (MalformedURLException e) {
				fail(e.getMessage());
			}
		}
	}
}
