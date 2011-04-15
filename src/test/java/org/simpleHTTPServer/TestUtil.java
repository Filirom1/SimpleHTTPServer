/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simpleHTTPServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author romain
 */
public class TestUtil {

    /**
     * Send "GET / HTTP/1.0" request.
     * @param socket
     * @throws IOException
     */
    public static void sendRequest(Socket socket) throws IOException {
        PrintWriter pw = new PrintWriter(socket.getOutputStream());
        pw.println("GET / HTTP/1.0");
        pw.println();
        pw.flush(); // IMPORTANT for TestCase environment...
    }

    /**
     * Check if "HTTP/1.0 200 OK" is received.
     * @param socket
     * @throws IOException
     */
    public static void checkResponse(Socket socket) throws IOException {
        InputStream is = socket.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        assert ("HTTP/1.0 200 OK".equals(br.readLine()));
    }
}
