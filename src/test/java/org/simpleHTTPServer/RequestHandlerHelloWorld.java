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
