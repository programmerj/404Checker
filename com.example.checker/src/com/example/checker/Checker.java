/**
 * Copyright 2013 me, under Beerware license.
 */
package com.example.checker;

/**
 * @author markus
 * 
 *         A Checker's purpose is to take a base/root link (URL) and from there
 *         find all outgoing links on the web page. Each outgoing link will be
 *         checked and if the check results in an http 404 marked as broken.
 * 
 */
public class Checker {

	/**
	 * @param args
	 *            The first element of the string parameters is passed on and
	 *            subsequently treated as a url.
	 */
	public static void main(String[] args) {
		new Checker().check(args[0]);
	}

	/**
	 * @param string
	 *            The base url where to start from
	 */
	private void check(final String string) {
		// TODO Implement
	}
}
