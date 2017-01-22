package org.sudoforlunch.mountmanager;

public class ProcMountsReadException extends Exception {
    public ProcMountsReadException() {
    }

    public ProcMountsReadException(String err) {
        super(err);
    }
}
