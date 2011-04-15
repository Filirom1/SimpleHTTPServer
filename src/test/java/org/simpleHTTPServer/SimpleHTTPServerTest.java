/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simpleHTTPServer;

import java.io.File;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.simpleHTTPClient.HTTPResponse;
import org.simpleHTTPClient.SimpleHTTPClient;

/**
 *
 * @author romain
 */
public class SimpleHTTPServerTest {

    static private final File rootDir = new File(SimpleHTTPServerTest.class.getClassLoader().getResource(".").getFile());
    SimpleHTTPServer server;

    @Before
    public void init() {
        server = new SimpleHTTPServer(8000, rootDir);
        server.start();
    }

    @Test
    public void testListingDir() {
        //execute
        HTTPResponse get = SimpleHTTPClient.get("http://localhost:8000");

        //check
        Assert.assertEquals(get.getStatus(), 200);
        System.out.println(get.getContent());
        Assert.assertTrue(get.getContentType().startsWith("text/html"));
        Assert.assertTrue(get.getContent().contains("Directory listing"));
        Assert.assertTrue(get.getContent().contains("href=\"/myPage.html\""));
        Assert.assertTrue(get.getContent().contains("href=\"/image.png\""));
        Assert.assertTrue(get.getContent().contains("href=\"/aFolder\""));
    }

    @Test
    public void testListingSubDir() {
        //execute
        HTTPResponse get = SimpleHTTPClient.get("http://localhost:8000/aFolder");

        //check
        Assert.assertEquals(get.getStatus(), 200);
        System.out.println(get.getContent());
        Assert.assertTrue(get.getContentType().startsWith("text/html"));
        Assert.assertTrue(get.getContent().contains("Directory listing"));
        Assert.assertTrue(get.getContent().contains("href=\"/aFolder/page.html\""));
        Assert.assertTrue(get.getContent().contains("href=\"/aFolder/textFile.txt\""));
    }

    @Test
    public void testReadingHTML() {
        //execute
        HTTPResponse get = SimpleHTTPClient.get("http://localhost:8000/myPage.html");

        //check
        Assert.assertEquals(get.getStatus(), 200);
        System.out.println(get.getContent());
        Assert.assertTrue(get.getContentType().startsWith("text/html"));
        Assert.assertTrue(get.getContent().contains("<html>"));
    }

    @Test
    public void testReadingText() {
        //execute
        HTTPResponse get = SimpleHTTPClient.get("http://localhost:8000/aFolder/textFile.txt");

        //check
        Assert.assertEquals(get.getStatus(), 200);
        System.out.println(get.getContent());
        Assert.assertTrue(get.getContentType().startsWith("text/plain"));
        Assert.assertTrue(get.getContent().contains("a text file"));
    }

    @Test
    public void testIndexHtml() throws InterruptedException {
        //execute
        HTTPResponse get = SimpleHTTPClient.get("http://localhost:8000/aFolder/subFolder");

        //check
        Assert.assertEquals(200, get.getStatus());
        Assert.assertTrue(get.getContentType().startsWith("text/html"));
        Assert.assertTrue(get.getContent().contains("<h1>Hello World</h1>"));
    }

    @After
    public void setDown() {
        server.stop();
    }
}
