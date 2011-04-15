/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simpleHTTPClient;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author romain
 */
public class SimpleHTTPClientTest {

    @Test
    public void testGet() {
        //execute
        HTTPResponse get = SimpleHTTPClient.get("http://google.fr");

        //check
        Assert.assertTrue(get.getContent().contains("google"));
        Assert.assertEquals(get.getStatus(), 200);
        Assert.assertTrue(get.getContentType().contains("text/html"));
    }

    @Test
    public void testGet404() {
        //execute
        HTTPResponse get = SimpleHTTPClient.get("http://googlgfdsgdfgsgfe.fr");

        //check
        Assert.assertEquals(get.getStatus(), 404);
    }
}
