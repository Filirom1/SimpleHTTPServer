package org.simpleHTTPServer;

import java.net.Socket;

/**
 * Factory to to return appropriate RequestHandler for dealing with an incoming Socket connection.
 * Classes implementing may require some configuration in their constructor.
 * 
 * @author vorburger
 */
interface RequestHandlerFactory {

    public RequestHandler newRequestHandler(Socket socket);
}
