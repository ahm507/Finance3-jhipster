package org.pf.service;

import java.io.IOException;

public class RestoreException extends Exception {

    public RestoreException(String s) {
        super(s);
    }

    public RestoreException(String message, IOException ioe) {
        super(message, ioe);
    }
}
