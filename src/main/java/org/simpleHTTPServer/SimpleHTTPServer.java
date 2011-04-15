/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simpleHTTPServer;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author romain
 */
public class SimpleHTTPServer {

    public static final int DEFAULT_PORT = 8000;
    public static final File DEFAULT_FILE = new File(".");
    private final int port;
    private final File rootDir;
    private int maxThreads = 10;
    private int clientTimeoutInMillis = 1000;
    private ServerMultiThreadedWorkers server;
    private boolean started = false;

    public SimpleHTTPServer() {
        this(DEFAULT_PORT, DEFAULT_FILE);
    }

    public SimpleHTTPServer(int port, File rootDir) {
        this.port = port;
        this.rootDir = rootDir;
    }

    public void start() {
        if (!started) {
            System.out.println("Serving HTTP on 0.0.0.0 port 8000 ...");
            RequestHandlerFactory requestHandlerFactory = new RequestHandlerStaticSiteFactory(rootDir);
            server = new ServerMultiThreadedWorkers(port, clientTimeoutInMillis, maxThreads, requestHandlerFactory);
            server.start();
            started = true;
        } else {
            throw new RuntimeException("Server already started (HTTP port=" + port + ", rootDir=" + rootDir.getAbsolutePath() + ")");

        }
    }

    public void stop() {
        if (started) {
            server.terminate();
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(SimpleHTTPServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("Server not started (HTTP port=" + port + ", rootDir=" + rootDir.getAbsolutePath() + ")");
        }
    }

    /**
     * @param maxThreads the maxThreads to set
     */
    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    /**
     * @param clientTimeoutInMillis the clientTimeoutInMillis to set
     */
    public void setClientTimeoutInMillis(int clientTimeoutInMillis) {
        this.clientTimeoutInMillis = clientTimeoutInMillis;
    }

    /**
     *
     * @param args. First arg is the port number.
     */
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        SimpleHTTPServer server = new SimpleHTTPServer(port, DEFAULT_FILE);
        server.start();

        //Use Ctrl + C to stop.
    }
}
