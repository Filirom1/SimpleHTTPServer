package org.simpleHTTPServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A Server with a single "Worker thread" - serves one client after the next.  
 * @author vorburger
 */
class ServerSingleThreadedWorker extends Thread implements Runnable {

    private final int port;
    private final int timeout;
    ServerSocket socketServer;
    RequestHandlerFactory requestHandlerFactory;

    /**
     * Constructor
     * @param port TCP/IP port number that the server will listen on
     * @param timeout Timeout in miliseconds that the server will wait for a client to send the full request
     * @param requestHandlerClass the RequestHandlerFactory that can provide a RequestHandler who will actually deal with network request
     */
    // TODO Alternative constructors, particularly with InetAddress bindingAddress (maybe backlog?)
    public ServerSingleThreadedWorker(int port, int timeout, RequestHandlerFactory requestHandlerFactory) {
        this.port = port;
        this.timeout = timeout;
        this.requestHandlerFactory = requestHandlerFactory;
    }

    /**
     * Run the server.
     * This is a blocking call - run() will not return, unless another thread sets runServer = false.
     * @see ServerService
     */
    // TODO Look into NIO - provide an alternative implementation?
    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                Socket socket = socketServer.accept();

                // Set timeout so that clients that are much too slow and would block the server for other clients can get their butt kicked
                // NOTE: This is a setSoTimeout() on the SOCKET, not on the SocketServer (@see unblockWaitingSocket)
                socket.setSoTimeout(timeout);

                RequestHandler handler = requestHandlerFactory.newRequestHandler(socket);
                this.handle(handler);
            }
        } // Note: An IOException from ServerSocket.accept() is NOT a recoverable condition (i.e. just log it and keep waiting for the next won't help)
        // Its occurence indicates e.g. a configuration problem that would justify aborting completly, i.e. stopping the server.
        // This is different from an Exception (IOException or other) occuring during the handling of one specific request above.
        catch (IOException ex) {

            // This throw is probably not going anywhere anyway - there can be no catch for this...
            throw new RuntimeException("Unexpected problem during Socket listening", ex);

        }
    }

    @Override
    public void start() {
        try {
            // DO NOT set runningServer=true here - that's the responsability of start()
            socketServer = new ServerSocket(port);
            super.start();

        } // Note: An IOException from ServerSocket.accept() is NOT a recoverable condition (i.e. just log it and keep waiting for the next won't help)
        // Its occurence indicates e.g. a configuration problem that would justify aborting completly, i.e. stopping the server.
        // This is different from an Exception (IOException or other) occuring during the handling of one specific request above.
        catch (Exception ex) {

            // This throw is probably not going anywhere anyway - there can be no catch for this...
            throw new RuntimeException("Unexpected problem during Socket binding", ex);

        }
    }

    public void terminate() {
        try {
            if (socketServer != null) {
                socketServer.close();
            }
            this.interrupt();
        } catch (IOException ex) {
            // close() failed?  Boh, who cares, probably safe to ignore - caller could take any sensible action; we simply want it down anyway.
        }
    }

    /**
     * Serve the request from socket.
     * Subclasses can override this method.
     *
     * @param socket
     * @throws IOException
     * @throws SimpleWebServerException
     */
    protected void handle(RequestHandler handler) {
        handler.run();
    }
}
