package org.simpleHTTPServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A Server that allows multiple concurrent Worker threads.
 * This implementation is based on the JDK 5.0 java.util.concurrent.* package.
 * @author vorburger
 */
class ServerMultiThreadedWorkers extends ServerSingleThreadedWorker {

    private final ExecutorService pool;

    /**
     * Constructor
     *
     * @param port TCP/IP port number that the server will listen on
     * @param timeout Timeout in miliseconds that the server will wait for a client to send the full request
     * @param threads Maximum number of threads
     */
    public ServerMultiThreadedWorkers(int port, int timeout, int threads, RequestHandlerFactory requestHandlerFactory) {
        super(port, timeout, requestHandlerFactory);
        pool = Executors.newFixedThreadPool(threads);
    }

    @Override
    protected void handle(RequestHandler handler) {
        assert handler != null;
        pool.execute(handler);
    }

    @Override
    public void terminate() {
        System.out.println("Is stopping HTTP Server");
        super.terminate();
        pool.shutdownNow();
        try {
            pool.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // Fine, whatever; main thing is that we stopped.
        }
        assert pool.isTerminated();
        assert pool.isShutdown();
    }
}
