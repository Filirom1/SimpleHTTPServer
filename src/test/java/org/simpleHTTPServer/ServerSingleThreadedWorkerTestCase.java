package org.simpleHTTPServer;

import org.junit.Test;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.*;

public class ServerSingleThreadedWorkerTestCase {

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

    // -----------------------------------------------------------------------------------------------
    /**
     * Test that a very simple basic HTTP request is answered correctly.
     * @throws Exception If test has any errors
     */
    @Test
    public void testOneRequest() throws IOException {
        Socket socket = new Socket(InetAddress.getLocalHost(), port);
        TestUtil.sendRequest(socket);
        TestUtil.checkResponse(socket);
        socket.close();
    }

    /**
     * Test that starting an already running server is prevented.
     * @throws Exception If test has any errors
     */
    @Test
    public void testServerStart() throws Exception {
        try {
            server.start();
            fail("Uoups - this shouldn't have worked!");
        } catch (RuntimeException ex) {
            // Cool - as expected.
        }
    }

    /**
     * Test that an IOException during startup (such as a port-already-in-use) prevents startup and stops server
     * @throws Exception If test has any errors
     */
    @Test
    public void testStartupIOExceptionHandling() {
        SimpleHTTPServer anotherServerService = new SimpleHTTPServer();
        try {
            anotherServerService.start();
            fail("Uoups - this shouldn't have worked!");
        } catch (RuntimeException ex) {
            // Cool, as expected.
            System.out.println("Exception catched.");
        }
        anotherServerService.stop();
    }

    /**
     * Test that with one open connection the server can shutdown anyway (a "blocked" testcase indicates failure)
     * @throws Exception If test has any errors
     */
    @Test
    public void testConnectionLeftOpen() throws Exception {
        new Socket(InetAddress.getLocalHost(), port);
        // Do NOT close socket... tearDown will stop server with socket timeout
    }

    /**
     * Test that with one open connection sending partial data only the server can shutdown anyway (a "blocked" testcase indicates failure)
     * @throws Exception If test has any errors
     */
    @Test
    public void testConnectionPartialData() throws Exception {
        Socket socket = new Socket(InetAddress.getLocalHost(), port);
        PrintWriter pw = new PrintWriter(socket.getOutputStream());
        pw.print("GET"); // NOT println!
        // Do NOT close socket... tearDown will stop server, should work
    }

    /**
     * Test that even if the "request line" (first line) has an invalid format, the server keeps running and can serve another request
     * @throws Exception If test has any errors
     */
    @Test
    public void testInvalidHTTPRequestLine() throws Exception {
        Socket socket1 = new Socket(InetAddress.getLocalHost(), port);
        PrintWriter pw = new PrintWriter(socket1.getOutputStream());
        pw.println("Foobar!  Bah, I am not an HTTP request...");
        pw.println();
        pw.flush();

        this.testOneRequest();

        // Close Socket1 only now!
        socket1.close();

    }
}
