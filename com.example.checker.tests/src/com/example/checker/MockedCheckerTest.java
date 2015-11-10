/**
 * Copyright 2013 me, under Beerware license.
 */
package com.example.checker;

import static org.mockito.Mockito.doAnswer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import com.example.checker.extract.HTMLExtractor;
import com.example.checker.extract.IExtractor;

/**
 * @author markus
 * 
 */
@SuppressWarnings("unchecked")
@RunWith(value = Parameterized.class)
public class MockedCheckerTest {
	/**
	 * Nodes per level (branching factor)
	 */
	private final int n;

	/**
	 * Height of resulting tree -1
	 */
	private final int depth;

	/**
	 * Level of concurrency in tests
	 */
	private final int numThreads;

	private final int totalNodes;

	@Parameters
	public static Collection<Object[]> data() {
		Collection<Object[]> col = new ArrayList<Object[]>();
		col.add(new Object[] { 2, 1, 1 });
		col.add(new Object[] { 2, 2, 1 });
		col.add(new Object[] { 2, 4, 1 });
		col.add(new Object[] { 2, 8, 1 });
		col.add(new Object[] { 2, 16, 1 });

		col.add(new Object[] { 2, 1, 1 });
		col.add(new Object[] { 4, 2, 1 });
		col.add(new Object[] { 8, 4, 1 });
		col.add(new Object[] { 16, 8, 1 });

		col.add(new Object[] { 2, 1, 8 });
		col.add(new Object[] { 2, 2, 8 });
		col.add(new Object[] { 2, 4, 8 });
		col.add(new Object[] { 2, 8, 8 });
		col.add(new Object[] { 2, 16, 8 });

		col.add(new Object[] { 2, 1, 8 });
		col.add(new Object[] { 4, 2, 8 });
		col.add(new Object[] { 8, 4, 8 });
		col.add(new Object[] { 16, 8, 8 });

		return col;
	}

	public MockedCheckerTest(int n, int depth, int numThreads) {
		this.n = n;
		this.depth = depth;
		this.numThreads = numThreads;

		// nodes in tree: (n^(depth + 1 + 1) - 1)/(n-1)
		double pow = Math.pow(n * 1.0d, (depth + 2) * 1.0d);
		this.totalNodes = (int) ((pow - 1) / (n - 1));

		System.out
				.println(String
						.format("Running test with branching %d, depth %d (total nodes: %,d) and num thread %d",
								n, depth, totalNodes, numThreads));
	}

	@Test
	// (timeout = 1000)
	public void test() throws IOException, InterruptedException,
			ExecutionException {
		final MockitoLink root = new MockitoLink(0, "ROOT");

		final IExtractor extractor = Mockito.mock(HTMLExtractor.class);

		Mockito.when(extractor.getContentType()).thenReturn(
				MockitoLink.CONTENT_TYPE);

		final Answer<Void> answer = invocation -> {
			final MockitoLink parentLink = (MockitoLink) invocation
					.getArguments()[0];
			final Set<Link> links = (Set<Link>) invocation.getArguments()[1];
			int height = parentLink.getHeight();
			if (height > depth) {
				// Create loop to first link
				links.add(root);
			} else {
				for (int i = 0; i < n; i++) {
					links.add(new MockitoLink(parentLink.getHeight() + 1,
							parentLink.getId() + ":H: " + (height + 1)
									+ " id: " + i));
				}
			}
			return null;
		};

		doAnswer(answer).when(extractor).extractLinks(Matchers.any(Link.class),
				Matchers.any(Set.class));

		final Map<String, IExtractor> extractors = new HashMap<String, IExtractor>();
		extractors.put(extractor.getContentType(), extractor);

		final Set<Link> links = new HashSet<Link>();
		links.add(root);

		final Checker checker = new Checker(extractors, depth + 2, numThreads);
		final Set<Link> check = checker.check(links);

		Assert.assertEquals(totalNodes, checker.getSeen().size());
		Assert.assertTrue(check.size() == 0);
	}
}
