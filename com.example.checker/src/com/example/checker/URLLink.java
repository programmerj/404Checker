/**
 * Copyright 2013 me, under Beerware license.
 */
package com.example.checker;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author markus
 * 
 */
public class URLLink extends Link implements Comparable<URLLink> {

	private URL url;

	public URLLink(URL url) {
		this.url = url;
	}

	public InputStream getInputStream() throws IOException {
		return this.url.openStream();
	}

	public String getContentType() throws IOException {
		return this.url.openConnection().getContentType();
	}

	public boolean isSameAuthority(String authority) {
		return this.url.getAuthority().equals(authority);
	}

	/**
	 * @param obj
	 * @return
	 * @see java.net.URL#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof URLLink) {
			URLLink l = (URLLink) obj;
			return url.toString().equals(l.url.toString());
		}
		return false;
	}

	/**
	 * @return
	 * @see java.net.URL#hashCode()
	 */
	public int hashCode() {
		return url.toString().hashCode();
	}

	/**
	 * @return
	 * @see java.net.URL#toString()
	 */
	public String toString() {
		return url.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(URLLink o) {
		return this.url.toString().compareTo(o.url.toString());
	}
}
