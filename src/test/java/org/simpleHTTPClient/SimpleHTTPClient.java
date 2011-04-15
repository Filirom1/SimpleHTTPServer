/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simpleHTTPClient;

import java.net.*;
import java.io.*;

/**
 *
 * @author romain
 */
public class SimpleHTTPClient {

    public static HTTPResponse get(String url) throws RuntimeException {
        HTTPResponse httpResponse = new HTTPResponse();
        try {
            URL yahoo = new URL(url);
            URLConnection yc = yahoo.openConnection();
            if (yc instanceof HttpURLConnection) {
                HttpURLConnection httpConnection = (HttpURLConnection) yc;
                StringBuilder builder = new StringBuilder();
                try {
                    InputStream inputStream = httpConnection.getInputStream();
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(inputStream));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        builder.append(inputLine);
                    }
                    in.close();
                    httpResponse.setContent(builder.toString());
                    httpResponse.setContentType(httpConnection.getContentType());
                    httpResponse.setStatus(httpConnection.getResponseCode());

                } catch (Exception e) {
                    httpResponse.setStatus(404);
                }
                return httpResponse;
                // do something with code .....
            } else {
                throw new RuntimeException("error - not a http request!");
            }

        } catch (Exception ex) {
            throw new RuntimeException("Unable to GET " + url, ex);
        }

    }
}
