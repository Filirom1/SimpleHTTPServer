package org.simpleHTTPServer;

import java.io.File;
import java.net.Socket;

class RequestHandlerStaticSiteFactory implements RequestHandlerFactory {

	File rootDirectory;
	
	public RequestHandlerStaticSiteFactory(File rootDirectory) {
		this.rootDirectory = rootDirectory;
	}
	
	public RequestHandler newRequestHandler(Socket socket) {
		return new RequestHandlerStaticSite(socket, rootDirectory);
	}
}
