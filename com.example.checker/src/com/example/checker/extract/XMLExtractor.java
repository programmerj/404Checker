/**
 * Copyright 2013 me, under Beerware license.
 */
package com.example.checker.extract;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

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
	public void extractLinks(URL url, Set<URL> links) throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("not yet implemented");
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
