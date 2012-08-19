package org.simpleHTTPServer;

import java.net.Socket;

public class RequestHandlerHelloWorldFactory implements RequestHandlerFactory {

	public RequestHandler newRequestHandler(Socket socket) {
		return new RequestHandlerHelloWorld(socket);
	}
}
