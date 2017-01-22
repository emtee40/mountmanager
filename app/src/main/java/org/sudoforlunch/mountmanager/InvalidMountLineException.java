package org.sudoforlunch.mountmanager;

public class InvalidMountLineException extends Exception {
    public InvalidMountLineException() {
    }

    public InvalidMountLineException(String err) {
        super(err);
    }
}
