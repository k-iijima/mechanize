package com.gistlabs.mechanize.exceptions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ClientProtocolException;

/**
 * Factory for creating mechanize exception warping existing exceptions and also creating new exceptions.
 * @author Martin Kersten <Martin.Kersten.mk@gmail.com>
 */
public class MechanizeExceptionFactory {
	
	public static MechanizeException newException(Exception cause) {
		if(cause instanceof ClientProtocolException) 
			return new MechanizeClientProtocolException(cause);
		else if(cause instanceof IOException)
			return new MechanizeIOException(cause);
		else if(cause instanceof UnsupportedEncodingException)
			return new MechanizeUnsupportedEncodingException(cause);
		else if(cause instanceof URISyntaxException)
			return new MechanizeURISyntaxException(cause);
		else 
			return new MechanizeException(cause);
	}
	
	public static MechanizeInitializationException newInitializationException(Exception cause) {
		return new MechanizeInitializationException(cause);
	}

	public static MechanizeException newMechanizeException(String message) {
		return new MechanizeException(message);
	}
}
