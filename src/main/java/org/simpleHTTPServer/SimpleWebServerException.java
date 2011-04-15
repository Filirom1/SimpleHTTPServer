package org.simpleHTTPServer;

class SimpleWebServerException extends Exception {

    public SimpleWebServerException() {
        super();
    }

    public SimpleWebServerException(String reason) {
        super(reason);
    }

    public SimpleWebServerException(String reason, Throwable nestedReason) {
        super(reason, nestedReason);
    }

    public SimpleWebServerException(Throwable nestedReason) {
        super(nestedReason);
    }
}
