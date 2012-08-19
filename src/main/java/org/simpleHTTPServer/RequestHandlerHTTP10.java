package org.simpleHTTPServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;

/**
 * A Handler for the HTTP protocol. 
 * 
 * @author vorburger
 */
abstract class RequestHandlerHTTP10 extends RequestHandler {

    final static String RFC1123_DATE_PATTERN = "EEE, dd MMM yyyy HH:mm:ss 'GMT'";

    public RequestHandlerHTTP10(Socket socket) {
        super(socket);
    }

    protected abstract void handle(HTTPRequest request, HTTPResponse response) throws IOException;

    @Override
    protected void handle(Socket socket) throws IOException, SimpleWebServerException {
        HTTPRequest request = this.getHTTPRequest(socket);
        HTTPResponse response = new HTTPResponse(socket);

        // "The Date general-header field represents the date and time at which the message was originated"
        // TODO Profile/research: Is DateFormat initialization an expensive operation?  Do this only once...
        DateFormat rfc1123_DateFormat = new SimpleDateFormat(RFC1123_DATE_PATTERN, Locale.US);
        rfc1123_DateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));
        String date = rfc1123_DateFormat.format(new Date());
        response.setHeader(HTTPResponse.Header.Date, date);

        response.setHeader(HTTPResponse.Header.Server, "SimpleHTTPServer/1.0");

        // This is Connection: close is probably not strictly neccessary as we are a HTTP/1.0 server so far here, but it can't work and seems to work well
        response.setHeader(HTTPResponse.Header.Connection, "close");
        if (HTTPRequest.Version.HTTP11.toString().equals(request.getHTTPVersion())) {
            // Until HTTP/1.1 is properly implemented here, simply "force" (?) response to 1.0 (http://www.ietf.org/rfc/rfc2145.txt)
            // I'm not 1000% sure if this is correct... but it seems to work well with HTTP/1.1 browsers...
            response.setHTTPVersion(HTTPRequest.Version.HTTP10);
        } else if (!HTTPRequest.Version.HTTP10.toString().equals(request.getHTTPVersion())) {
            throw new SimpleWebServerException("Don't know how to answer HTTP requests with this version header: " + request.getHTTPVersion());
        }

        this.handle(request, response);

        System.out.println(socket.getInetAddress().getHostAddress()+" [" + new Date().toString() + "] " + request.getMethod() + " " + request.getHTTPVersion() + " " + request.getURI() + " " + response.getStatusCode());

        // TODO HTTP/1.1 support, we probably don't want to close this response (ultimately, underlying socket) just yet and wait for more requests in this same Handler?
        response.close();
    }

    private HTTPRequest getHTTPRequest(Socket socket) throws IOException, SimpleWebServerException {
        HTTPRequest r = new HTTPRequest();
        InputStream is = socket.getInputStream();
        // TODO Charset of IS?  Try an URL with an Umlaut..  UTF-8?
        Reader reader = new InputStreamReader(is /* charset??? */);
        BufferedReader bufferedReader = new BufferedReader(reader/*, size???  Default is 8k - leave that for now */);
        String httpRequestLine = "";
        // TODO Security: Use e.g. a custom BufferedReader subclass that limits characters per line and total lines to avoid DOS/exhaustion attacks.. (but take big file uploads via POST into account!)
        httpRequestLine = bufferedReader.readLine();
        // This could throw a SocketTimeoutException, which will propagate to the caller, as it should.
        // If null, this also indicates a timeout occured, and we are not dealing with the request either...
        if (httpRequestLine == null) {
            throw new SimpleWebServerException("No (or not enough) data received (within timeout)");
        }

        try {
            String[] httpRequestLineSplitArray = httpRequestLine.split(" ");
            r.method = httpRequestLineSplitArray[0];
            r.URI = httpRequestLineSplitArray[1];
            r.HTTPVersion = httpRequestLineSplitArray[2];
        } catch (Exception ex) {
            throw new SimpleWebServerException("HTTP Request Line (1st line) invalid, should be 'VERB URI VERSION' and not '" + httpRequestLine + "'; see RFC 2616, Section 5", ex);
        }

        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            if (line.length() == 0) {
                break;
            }
            int httpRequestHeaderKeySeparatorPos = line.indexOf(':');
            String httpRequestHeaderKey = line.substring(0, httpRequestHeaderKeySeparatorPos);
            String httpRequestHeaderValue = line.substring(httpRequestHeaderKeySeparatorPos + 1, line.length());
            httpRequestHeaderValue = httpRequestHeaderValue.trim(); // RFC 2616 Section 4.2

            r.headers.put(httpRequestHeaderKey, httpRequestHeaderValue);
        }

        // TODO Test if Header/Body delimiter code here works
        StringBuffer bodySB = new StringBuffer(1024);
        while (bufferedReader.ready()) {
            String line = "";
            do {
                line = bufferedReader.readLine();
            } while (line.length() == 0);
            bodySB.append(line);
            bodySB.append('\n');
        }
        r.body = bodySB.toString();

        return r;
    }
}
