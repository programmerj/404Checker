/**
 * Copyright 2013 me, under Beerware license.
 */
package com.example.checker.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.TreeSet;

import org.junit.BeforeClass;
import org.junit.Test;

import com.example.checker.Checker;

/**
 * @author markus
 * 
 */
public class CheckerTest {

	private static Set<String> links;

	@BeforeClass
	public static void setUp() throws MalformedURLException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, SecurityException, NoSuchMethodException {

		// object under test
		Checker checker = new Checker();

		URL url = new URL("http://www.vogella.com/");
		links = new TreeSet<String>();

		// Use reflection to make private method visible to tests
		// (Usually not a very good idea because it leads to brittle tests)
		Method method = Checker.class.getDeclaredMethod("extractLinks",
				URL.class, Set.class);
		method.setAccessible(true);
		method.invoke(checker, url, links);
	}

	@Test
	public void testExtractLinksFromKnownURL() throws SecurityException,
			NoSuchMethodException, MalformedURLException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {

		int expectedAmountOfLinks = 5;

		assertTrue("Missed to see some links",
				links.size() == expectedAmountOfLinks);
	}

	@Test
	public void testExtractLinksConvertToURL() {
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
