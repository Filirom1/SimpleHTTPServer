package org.simpleHTTPServer;

import java.io.File;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.*;

public class ServerMultiThreadedWorkersTestCase {

    SimpleHTTPServer server;
    int port = 8000;
    static private final File rootDir = new File(SimpleHTTPServerTest.class.getClassLoader().getResource(".").getFile());

    @Before
    public void init() {
        server = new SimpleHTTPServer(8000, rootDir);
        server.start();
    }

    @After
    public void setDown() {
        server.stop();
    }
    static final int NUM_THREADS = 27;

    protected ServerSingleThreadedWorker newServer() {
        // This timeout HAS TO BE higher than the client timeout in testTwoRequests
        return new ServerMultiThreadedWorkers(port, 100, NUM_THREADS, new RequestHandlerHelloWorldFactory());
    }

    /**
     * Test that a 2nd connection can be made while a first one is still open - very basic multithreading test
     * @throws Exception If test has any errors
     */
    public void testTwoParallelRequests() throws Exception {
        Socket socket1 = new Socket(InetAddress.getLocalHost(), port);
        PrintWriter pw = new PrintWriter(socket1.getOutputStream());
        pw.print("GET");
        // Keep this first guy waiting... (NOT printLN!)

        Socket socket2 = new Socket(InetAddress.getLocalHost(), port);
        // This timeout HAS TO BE much lower than the server timeout in the newServer method above
        socket2.setSoTimeout(20);
        TestUtil.sendRequest(socket2);
        try {
            TestUtil.checkResponse(socket2);
        } catch (SocketTimeoutException ex) {
            fail("Test failed, second socket did not respond while first one still busy: " + ex.toString());
        }

        socket1.close();
        socket2.close();
    }

    /**
     * Test that with as many open connections as there are threads, the server can shutdown anyway (a "blocked" testcase indicates failure)
     * @throws Exception If test has any errors
     */
    /*
    public void testManyConnectionsLeftOpen() throws Exception {
        for (int i = 0; i < NUM_THREADS; i++) {
            this.testConnectionLeftOpen();
        }
    }
*/
    /**
     * Test that with as many open connection as there are threads, sending partial data only the server can shutdown anyway (a "blocked" testcase indicates failure)
     * @throws Exception If test has any errors
     */
    /*
    public void testManyConnectionWithPartialData() throws Exception {
        for (int i = 0; i < NUM_THREADS; i++) {
            this.testConnectionPartialData();
        }
    }
     *
     */
}
