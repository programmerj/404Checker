/**
 * Copyright 2013 me, under Beerware license.
 */
package com.example.checker.extract;


/**
 * @author markus
 * 
 */
public class PlaintextRegexExtractor extends HTMLRegexExtractor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.checker.extract.IExtractor#getContentType()
	 */
	@Override
	public String getContentType() {
		return "text/plain";
	}
}
