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
public abstract class Link {

	public abstract InputStream getInputStream() throws IOException;

	public abstract String getContentType() throws IOException;

	public abstract boolean isSameAuthority(String authority);
}
