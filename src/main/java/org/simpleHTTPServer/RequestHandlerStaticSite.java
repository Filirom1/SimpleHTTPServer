package org.simpleHTTPServer;

import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.Date;

/**
 * Handle HTTP requests by serving fiels from the local filesystem.
 * Given a root directory, files corresponding to the URI are sent to the client.
 *   
 * @author vorburger
 * @author romain
 */
// TODO TestCase for RequestHandlerStaticSite
class RequestHandlerStaticSite extends RequestHandlerHTTP10 {

    File siteRoot;

    public RequestHandlerStaticSite(Socket socket, File htDocsRootPath) {
        super(socket);
        siteRoot = htDocsRootPath;
    }

    protected void handleGet(HTTPRequest request, HTTPResponse response) throws IOException {
        // Note: The JDK URI class can do RFC 2396 encoding and decoding for us here...
        URI uri;
        try {
            uri = new URI(request.getURI());
        } catch (URISyntaxException e) {
            response.setStatusCode(400); // 400 is Bad Request, seems a suitable answer for this case
            handleException(request, response, "URISyntaxException", e);
            return;
        }
        // This wouldn't handle %20-like encoding/decoding:  String uri = request.getURI();
        File file = new File(siteRoot, uri.getPath());

        if (!file.exists()) {
            response.setStatusCode(404); // 404 is 'Not Found', the correct answer for this case
            handleError(request, response, "File Not Found for requested URI '" + uri + "' ");
            return;
        }
        if (!file.canRead()) {
            response.setStatusCode(403); // 403 is 'Forbidden', this seems appropriate here
            handleError(request, response, "Local file matched by requested URI is not readable");
            // SECURITY Note: It's better not to show the full local path to the client, let's just log it on the server to help debugging
            return;
        }

        // TODO Security: Check that no request can read "outside" (above) the siteRoot... using getCanonicalPath() ?
        // (E.g. of the form http://localhost/../java/ch/vorburger/simplewebserver/RequestHandlerStaticSite.java if siteroot is src/htdocs-test)

        // TODO Implement modified-since stuff handling... something like: always send Last-Modified in response, and if request has a If-Modified-Since then check file with file.lastModified() and answer with code 304 if match (and Expires? Also not sure how exactly to handle If-Unmodified-Since request header)

        if (file.isFile()) {
            handleFile(file, response);
        } else if (file.isDirectory()) {
            handleDir(file, response);
        } else {
            handleError(request, response, "Content not file, not directory. We don't know how to handle it.");
        }
    }

    private static void handleFile(File file, HTTPResponse response) throws IOException {
        String filename = file.getName().toLowerCase();
        String contentType = URLConnection.getFileNameMap().getContentTypeFor(filename);
        response.setContentType(contentType);

        long length = file.length();
        response.setHeader(HTTPResponse.Header.ContentLength, Long.toString(length));

        FileInputStream in;
        try {
            in = new FileInputStream(file);

            // TOD Charset conversion for text/* potentially needed?  Do I need to use InputStreamReader(in, Charset/CharsetDecoder/String charsetName) here in some cases?
            OutputStream os = response.getOutputStream();

            int c;
            while ((c = in.read()) != -1) {
                os.write(c);
            }

            in.close();
            os.close();
        } catch (FileNotFoundException ex) {
            throw new IOException("File " + file + " not found.", ex);
        }
    }

    private void handleDir(File dir, HTTPResponse response) throws IOException {
        File indexFile = new File(dir.getAbsolutePath() + File.separator + "index.html");
        if (indexFile.exists()) {
            redirect(indexFile, response);
        } else {
            StringBuilder builder = new StringBuilder("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\"><html> \n"
                    + "<title>Directory listing for /</title>\n"
                    + "<body>\n"
                    + "<h2>Directory listing</h2>\n"
                    + "<hr>\n"
                    + "<ul>");

            File[] files = dir.listFiles();
            for (File file : files) {
                String link = "<li><a href=\"" + getWebPath(file) + "\">" + file.getName() + "<a/></li>\n";
                builder.append(link);
            }
            builder.append("</ul>\n"
                    + "<hr>\n"
                    + "</body>\n"
                    + "</html>");
            String content = builder.toString();
            response.setHeader(HTTPResponse.Header.ContentLength, Long.toString(content.length()));
            response.setContentType("text/html");
            OutputStream os = response.getOutputStream();
            os.write(content.getBytes("utf-8"));
            os.close();
        }
    }

    private String getWebPath(File file) throws IOException {
        return file.getCanonicalPath().replace(siteRoot.getCanonicalPath(), "");
    }

    private void redirect(File file, HTTPResponse response) throws IOException {
        response.setStatusCode(302);
        response.setHeader("Location", getWebPath(file));
    }

    @Override
    protected void handle(HTTPRequest request, HTTPResponse response) throws IOException {
        try {
            if (!HTTPRequest.Method.GET.toString().equals(request.getMethod())) {
                response.setStatusCode(501); // 501 is "Not Implemented"
                return;
            } else {
                handleGet(request, response);
            }

        } catch (Exception ex) {
            handleException(request, response, "Server Error (Unexpected '" + ex.getMessage() + "' while handling request)", ex);
        }
    }

    private void handleError(HTTPRequest request, HTTPResponse response, String message) throws IOException {
        this.handleException(request, response, message, null);
    }

    private void handleException(HTTPRequest request, HTTPResponse response, String message, Exception ex) throws IOException {
        try {
            // If earlier code has already set a more precise HTTP error then
            // leave that, make it a generic 500 only if its still the default 200
            if (response.getStatusCode() == 200) {
                response.setStatusCode(500);
            }
            PrintWriter pw;
            response.setContentType("text/html");
            pw = response.getPrintWriter();

            pw.println("<html><head><title>Server Error</title></head><body><h1>Server Error</h1><p>");
            pw.println(message);
            pw.println("</p><pre>");
            if (ex != null) {
                ex.printStackTrace(pw);
            }
            pw.println("</pre></body></html>");
        } catch (IllegalStateException e) {
            // Oh, too late to getPrintWriter()? Well... log it but otherwise
            // ignore it; at least the setStatusCode() worked if we're here.
            System.out.println("Can't send stack trace to client because OutputStream was already open for something else: " + e.toString()); // TODO Real logging...
            System.out.println("Stack trace of where the IllegalStateException occured:");
            e.printStackTrace();
            return;
        }
    }
}
