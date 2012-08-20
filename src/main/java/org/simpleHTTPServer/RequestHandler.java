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
import java.net.Socket;

/**
 * Handling Network Socket Requests.
 * This is "general-purpose" so far and could handle different protcols (i.e. it is NOT tied to HTTP).
 *   
 * @author vorburger
 */
abstract class RequestHandler implements Runnable {
	private final Socket socket;
	
	public RequestHandler(Socket socket) {
		this.socket = socket;
	}

	/**
	 * Handle a request given a Socket - read stuff from it - answer stuff back, etc!
	 * 
	 * @param socket
	 * @throws IOException
	 * @throws SimpleWebServerException
	 */
	protected abstract void handle(Socket socket) throws IOException, SimpleWebServerException;
		
	public void run() {
		try {
			handle(socket);
		}
		catch (Exception ex) {
			// TODO Real logging... (WARN here)
			ex.printStackTrace();
			// Note: No re-throwing here! "The show must go on..." - servers doesn't die just because we had a problem with one request.
		}
		finally {
			try {
				// Should never be null, but let's be on the safe side anyway
				if ( socket != null ) {
					// Some options, faster & safer?
					socket.setSoLinger(false, 0);
					socket.shutdownInput();
					socket.shutdownOutput();
					socket.close();
				}
			} catch (IOException e) {
				// Ignore... OK.
			}
		}			
	}
}
