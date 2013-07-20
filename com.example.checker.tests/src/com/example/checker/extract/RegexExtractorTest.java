/**
 * Copyright 2013 me, under Beerware license.
 */
package com.example.checker.extract;

import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

/**
 * @author markus
 * 
 */
public class RegexExtractorTest {

	@Test
	public void testValid() throws MalformedURLException {
		final List<String> valid = new ArrayList<String>();
		valid.add("http://www.vogella.com");
		valid.add("https://github.com/sbrosinski/RegexTester");
		valid.add("http://www.bastian-bergerhoff.com/eclipse/features/web/QuickREx/toc.html");
		valid.add("http://docs.oracle.com/javase/tutorial/essential/regex/");
		valid.add("http://www.vogella.com/articles/JavaRegularExpressions/article.html");
		valid.add("http://www.vogella.com/articles/JavaRegularExpressions/article.html#top");

		valid.add("http://username:pass@www.vogella.com/articles/JavaRegularExpressions/article.html");

		valid.add("ftp://ftp.osuosl.org/pub/eclipse/eclipse/downloads/drops4/");
		valid.add("ftp://ftp.osuosl.org/pub/eclipse/eclipse/downloads/drops4/../");

		// verify that we indeed test valid urls
		for (String input : valid) {
			new URL(input);
		}

		// really test the regex
		for (String input : valid) {
			assertTrue(String.format("Failed to match %s", input),
					Pattern.matches(RegexExtractor.regex, input));
		}
	}

	/**
	 * This tests our regex to match also substrings compared to
	 * {@link RegexExtractorTest#testValid()}
	 */
	@Test
	public void testValidSubstring() throws MalformedURLException {
		final List<String> valid = new ArrayList<String>();
		valid.add("<link rel=pingback\" href=\"http://www.lemmster.de/blog/xmlrpc.php\" />");

		// really test the regex
		for (String input : valid) {
			assertTrue(String.format("Failed to match %s", input),
					Pattern.matches(RegexExtractor.regex, input));
		}
	}

	@Test
	public void testInvalid() {
		final List<String> invalid = new ArrayList<String>();
		invalid.add("httb://github.com/sbrosinski/RegexTester");
		// TODO Add more invalid examples

		// verify that we indeed test invalid urls
		for (String input : invalid) {
			try {
				new URL(input);
			} catch (MalformedURLException e) {
				// expected
				continue;
			}
			throw new RuntimeException(String.format(
					"input %s has to be invalid!", input));
		}

		// really test the regex
		for (String input : invalid) {
			assertTrue(String.format("Incorrectly matched %s", input),
					!Pattern.matches(RegexExtractor.regex, input));
		}
	}
}
