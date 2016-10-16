package com.krld.foxypoxy.network;

public class TlError { //TODO rename

    public Boolean ok;
    public String description;
    public Integer errorCode;

    public TlError(Throwable error) {
        description = error.getMessage();
    }

    public TlError() {
        //TODO
    }

    @Override
    public String toString() {
        return description;
    }
}
