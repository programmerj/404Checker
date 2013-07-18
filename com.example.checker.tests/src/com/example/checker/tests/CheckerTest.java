/**
 * Copyright 2013 me, under Beerware license.
 */
package com.example.checker.tests;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import com.example.checker.Checker;

/**
 * @author markus
 * 
 */
public class CheckerTest {

	@Test
	public void testExtractLinksFromKnownURL() throws SecurityException,
			NoSuchMethodException, MalformedURLException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {

		// object under test
		Checker checker = new Checker();

		int expectedAmountOfLinks = 5;
		URL url = new URL("http://training.vogella.com/eclipse/eclipseide.html");
		Set<String> links = new TreeSet<String>();

		// Use reflection to make private method visible to tests
		// (Usually not a very good idea because it leads to brittle tests)
		Method method = Checker.class.getDeclaredMethod("extractLinks",
				URL.class, Set.class);
		method.setAccessible(true);
		method.invoke(checker, url, links);

		Assert.assertTrue("Missed to see some links",
				links.size() == expectedAmountOfLinks);
	}
}
