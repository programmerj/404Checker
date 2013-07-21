/**
 * Copyright 2013 me, under Beerware license.
 */
package com.example.checker.extract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

import com.example.checker.Link;

/**
 * @author markus
 * 
 */
public class XMLExtractor implements IExtractor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.checker.extract.IExtractor#extractLinks(java.net.URL,
	 * java.util.Set)
	 */
	@Override
	public void extractLinks(Link link, Set<Link> links) throws IOException {
		// Read the full xml file into memory
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				link.getInputStream()));
		extractLinks(reader, links);
	}

	public void extractLinks(BufferedReader reader, Set<Link> links)
			throws IOException {
		StringBuffer buf = new StringBuffer();
		String line = "";
		while ((line = reader.readLine()) != null) {
			buf.append(line);
		}
		reader.close();

		// TODO extract links from xml file
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.checker.extract.IExtractor#getContentType()
	 */
	@Override
	public String getContentType() {
		return "text/xml; charset=UTF-8";
	}
}
