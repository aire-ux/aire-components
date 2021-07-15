/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aire.ux.test.web;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;


import org.springframework.util.Assert;

/**
 * Mock implementation of the {@link javax.servlet.RequestDispatcher} interface.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 1.0.2
 * @see MockHttpServletRequest#getRequestDispatcher(String)
 */
public class MockRequestDispatcher implements RequestDispatcher {


	static final Logger logger = Logger.getLogger(MockRequestDispatcher.class.getName());
//	private final Log logger = LogFactory.getLog(getClass());

	private final String resource;


	/**
	 * Create a new MockRequestDispatcher for the given resource.
	 * @param resource the server resource to dispatch to, located at a
	 * particular path or given by a particular name
	 */
	public MockRequestDispatcher(String resource) {
		Assert.notNull(resource, "Resource must not be null");
		this.resource = resource;
	}


	@Override
	public void forward(ServletRequest request, ServletResponse response) {
		Assert.notNull(request, "Request must not be null");
		Assert.notNull(response, "Response must not be null");
		Assert.state(!response.isCommitted(), "Cannot perform forward - response is already committed");
		getMockHttpServletResponse(response).setForwardedUrl(this.resource);
		if (logger.isLoggable(Level.INFO)) {
			logger.info("MockRequestDispatcher: forwarding to [" + this.resource + "]");
		}
	}

	@Override
	public void include(ServletRequest request, ServletResponse response) {
		Assert.notNull(request, "Request must not be null");
		Assert.notNull(response, "Response must not be null");
		getMockHttpServletResponse(response).addIncludedUrl(this.resource);
		if (logger.isLoggable(Level.INFO)) {
			logger.info("MockRequestDispatcher: including [" + this.resource + "]");
		}
	}

	/**
	 * Obtain the underlying {@link MockHttpServletResponse}, unwrapping
	 * {@link HttpServletResponseWrapper} decorators if necessary.
	 */
	protected MockHttpServletResponse getMockHttpServletResponse(ServletResponse response) {
		if (response instanceof MockHttpServletResponse) {
			return (MockHttpServletResponse) response;
		}
		if (response instanceof HttpServletResponseWrapper) {
			return getMockHttpServletResponse(((HttpServletResponseWrapper) response).getResponse());
		}
		throw new IllegalArgumentException("MockRequestDispatcher requires MockHttpServletResponse");
	}

}
