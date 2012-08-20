/**
 *  Copyright 2006-2012 Michael Vorburger (http://www.vorburger.ch)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/*******************************************************************************
 * Copyright (c) 2006-2012 Michael Vorburger (http://www.vorburger.ch).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.simpleHTTPServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Return "Hello World" to all HTTP requests... used for illustration and by basic Test Cases, only.  
 * @author vorburger
 */
public class RequestHandlerHelloWorld extends RequestHandlerHTTP10 {

	public RequestHandlerHelloWorld(Socket socket) {
		super(socket);
	}

	protected void handle(HTTPRequest request, HTTPResponse response) throws IOException {
		PrintWriter pw = response.getPrintWriter();
		pw.println("Hello World!");
	}
}
