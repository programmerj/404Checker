/**
 * Copyright 2013 me, under Beerware license.
 */
package com.example.checker;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author markus
 * 
 */
public class MockitoLink extends Link {

	public static final String CONTENT_TYPE = "mockito";

	private final String id;

	private final int height;

	public MockitoLink(int height, String id) {
		this.id = id;
		this.height = height;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.checker.Link#getInputStream()
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		throw new UnsupportedOperationException("not implemented on purpose");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.checker.Link#getContentType()
	 */
	@Override
	public String getContentType() throws IOException {
		return CONTENT_TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.checker.Link#isSameAuthority(java.lang.String)
	 */
	@Override
	public boolean isSameAuthority(String authority) {
		return true;
	}

	/**
	 * @return the Id
	 */
	public String getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MockitoLink [id=" + id + ", height=" + height + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MockitoLink other = (MockitoLink) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public int getHeight() {
		return height;
	}
}
